/*
 * Copyright 2013 ETH Zurich and SICS Swedish ICT 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package sics.adaptMac.triggers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import sics.adaptMac.EclipseDriver;
import sics.adaptMac.MacConfiguration;
import sics.adaptMac.NodeTopologyInfo;
import sics.adaptMac.Topology;

/**
 * A trigger that optimizes periodically,
 * 
 * @author Marco Zimmerling (zimmerling@tik.ee.eth.ch)
 */
public class TimedTrigger extends AbstractTrigger {
	
	// Controller logger
	private static Logger logger = Logger.getLogger(TimedTrigger.class.getName());

	// Statistics logger
	private static Logger statsLogger = Logger.getLogger("stats");
	
	// Maximum number of optimization retries
	private static final int MAX_RETRIES = 4;
	
	// ECLiPSe driver
	private final EclipseDriver driver;
	
	// History of topologies
	private final LinkedList<Topology> topologyHistory;
	
	// Stream to output determined MAC parameters via serialdump to the sink node
	private final BufferedWriter output;
	
	// Helper object to notify and wake up the timed trigger thread
	private final WaitNotify waitNotify;
	
	// Initial delay in minutes
	private final int initialDelay;

	// Optimization period (in number of Glossy phases)
	private final int period;
	
	// Number of Glossy phases between failed optimization and retry
	private final int retryPeriod;
	
	// End-to-end constraints
	private final double reliabilityConstraint;
	private final double latencyConstraint;
	
	/**
	 * Constructor to create an instance of this trigger.
	 * 
	 * @param driver The ECLiPSe driver.
	 * @param topologyHistory History of topologies.
	 * @param initialDelay Initial delay in minutes.
	 * @param period Optimization period in number of Glossy phases.
	 * @param retryPeriod Number of Glossy phases between failed optimization and retry.
	 * @param output Stream to output MAC parameters to serialdump.
	 * @param reliabilityConstraint End-to-end constraint on reliability.
	 * @param latencyConstraint End-to-end constraint on latency.
	 */
	public TimedTrigger(final EclipseDriver driver,
			final LinkedList<Topology> topologyHistory,
			final int initialDelay,
			final int period,
			final int retryPeriod,
			final BufferedWriter output,
			final double reliabilityConstraint,
			final double latencyConstraint) {
		this.driver = driver;
		this.topologyHistory = topologyHistory;
		this.period = period;
		this.waitNotify = new WaitNotify(period);
		this.initialDelay = initialDelay;
		this.retryPeriod = retryPeriod;
		this.output = output;
		this.reliabilityConstraint = reliabilityConstraint;
		this.latencyConstraint = latencyConstraint;
		
		// Start the timed trigger thread
		startTimedTrigger();
		logger.info("Started TimedTrigger with OptimizationTriggerInitialDelay " + this.initialDelay
				+ " minutes, OptimizationPeriod " + this.period
				+ ", OptimizationRetryPeriod = " + this.retryPeriod
				+ ", ReliabilityConstraint " + this.reliabilityConstraint
				+ ", LatencyContraint " + this.latencyConstraint + " seconds");
	}

	/**
	 * Instantiates and starts the timed trigger thread.
	 */
	private void startTimedTrigger() {
		new Thread(new Runnable() {
			public synchronized void run() {
				logger.info("Starting initial delay of " + initialDelay + " minutes");
				try {
					wait(initialDelay * 60 * 1000);
				} catch (InterruptedException e) {
					logger.error("Error during initial delay of timed trigger thread", e);
				}
				logger.info("Initial delay finished, starting to work");
				
				int retries = 0;
				boolean initialOptimizationDone = false;
				while (true) {
					if (retries > 0) {
						// This is a retry, so we wait for the retryPeriod collection phase 
						logger.info("Retry optimization after next collection phase, " + retries + " retries");
						retries--;
						waitNotify.setPeriod(retryPeriod);
						waitNotify.doWait();
					} else {
						// Wait until period number of collection (Glossy) phases elapsed
						logger.info("Waiting for next optimization");
						retries = MAX_RETRIES;
						waitNotify.setPeriod(period);
						waitNotify.doWait();
					}
					
					// We need this for experiments with only one initial optimization.
					// Otherwise, we should comment it out.					
// 					if (!initialOptimizationDone) {
						statsLogger.info("OPTIMIZE");
						MacConfiguration newConf = driver.optimize(prepareTopologies(topologyHistory, false),
								reliabilityConstraint,
								latencyConstraint);
						if (newConf != null) {
							// Schedule next optimization depending on the retrieved parameters.
							logger.debug("Optimization successful, injecting new MAC parameters " + newConf);
// 							if (  (newConf.getTl() == EclipseDriver.XMAC_REL_TL && newConf.getTs() == EclipseDriver.XMAC_REL_TS && newConf.getN() == EclipseDriver.XMAC_REL_N) 
// 							   || (newConf.getTl() == EclipseDriver.LPP_REL_TL && newConf.getTs() == EclipseDriver.LPP_REL_TS && newConf.getN() == EclipseDriver.LPP_REL_N) ) {
// 								// The end-to-end constraints were not satisfiable and the optimizer thus
// 								// returned parameters that are optimized for reliability. So we schedule
// 								// the next optimization at the next Glossy phase.
// 								logger.debug("End-to-end constraints were not satisfiable, injecting parameters optimized for reliability");
// 								retries = 1; // HACK: setting retries = 1 gives the illusion of a retry and thus will schedule the next optimization with retryPeriod
// 							} else {
								// The end-to-end constraints are satisfiable, so we schedule the next
								// optimization normally in period Glossy phases.
								waitNotify.setPeriod(period);
								retries = 0;
								initialOptimizationDone = true;
// 							}
							
							// Inject the new parameters.
							try {
								statsLogger.info("INJECT "+newConf.getTs()+" "+newConf.getTl()+" "+newConf.getN());
								newConf.inject(output);
							} catch (IOException e) {
								logger.error("Injecting new MAC parameters failed", e);
							}
						} else {
							logger.debug("Optimization failed, retrying in " + retryPeriod + " seconds");
						}
// 					}
					
				}
			}
		}, "timed trigger").start();
	}
	
	/**
	 * Callback function. Signals that Glossy has finished the
	 * collection of network state information. 
	 */
	public void collectionFinished() {
		if (topologyHistory.isEmpty()) {
			return;
		}
		
		waitNotify.doNotify();
	}
	
	/**
	 * Callback function. Signals triggers that node have been
	 * purged from the topology.
	 * 
	 * @param purgedNodes The nodes that have been purged.
	 */	
	public void nodesHaveBeenPurged(HashSet<NodeTopologyInfo> purgedNodes) {

	}
	
}
