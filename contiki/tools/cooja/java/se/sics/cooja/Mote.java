/*
 * Copyright (c) 2006, Swedish Institute of Computer Science. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * Institute nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * $Id: Mote.java,v 1.7 2009/09/17 11:05:09 fros4943 Exp $
 */

package se.sics.cooja;

import java.util.Collection;

import org.jdom.Element;

/**
 * A simulated mote.
 *
 * All motes have an interface handler, a mote type and a mote memory.
 *
 * @see se.sics.cooja.MoteInterfaceHandler
 * @see se.sics.cooja.MoteMemory
 * @see se.sics.cooja.MoteType
 *
 * @author Fredrik Osterlind
 */
public interface Mote {

  /**
   * @return Unique mote ID
   */
  public int getID();
  
  /**
   * Returns the interface handler of this mote.
   *
   * @see #setInterfaces(MoteInterfaceHandler)
   * @return Mote interface handler
   */
  public MoteInterfaceHandler getInterfaces();

  /**
   * Sets the interface handler of this mote.
   *
   * @param moteInterfaceHandler
   *          New interface handler
   * @see #getInterfaces()
   */
  public void setInterfaces(MoteInterfaceHandler moteInterfaceHandler);

  /**
   * Returns the memory of this mote.
   *
   * @see #setMemory(MoteMemory)
   * @return Mote memory
   */
  public MoteMemory getMemory();

  /**
   * Sets the memory of this mote.
   *
   * @see #getMemory()
   * @param memory
   *          Mote memory
   */
  public void setMemory(MoteMemory memory);

  /**
   * Returns mote type.
   *
   * @see #setType(MoteType)
   * @return Mote type
   */
  public MoteType getType();

  /**
   * Sets mote type to given argument.
   *
   * @see #getType()
   * @param type
   *          New type
   */
  public void setType(MoteType type);

  /**
   * Returns simulation which holds this mote.
   *
   * @see #setSimulation(Simulation)
   * @return Simulation
   */
  public Simulation getSimulation();

  /**
   * Sets the simulation which holds this mote.
   *
   * @see #getSimulation()
   * @param simulation
   *          Simulation
   */
  public void setSimulation(Simulation simulation);

  /**
   * Ticks this mote and increases any internal time to given argument.
   *
   * Each mote implementation may handle calls to this method differently, but
   * typically the simulated mote should at least handle one event.
   *
   * This method is responsible for updating the mote interfaces.
   *
   * A call to this method typically polls all interfaces, activates the memory,
   * lets the underlying mote software handle one event, fetches the updated
   * memory and finally polls all interfaces again.
   *
   * @param simTime
   *          New simulation time
   * @return True is mote accepts another immediate tick
   */
  public boolean tick(long simTime);

  /**
   * Returns XML elements representing the current config of this mote. This is
   * fetched by the simulator for example when saving a simulation configuration
   * file. For example a mote may return the configs of all its interfaces. This
   * method should however not return state specific information.
   * (All nodes are restarted when loading a simulation.)
   *
   * @see #setConfigXML(Simulation, Collection, boolean)
   * @return XML elements representing the current mote config
   */
  public abstract Collection<Element> getConfigXML();

  /**
   * Sets the current mote config depending on the given XML elements.
   *
   * @param simulation
   *          Simulation holding this mote
   * @param configXML
   *          Config XML elements
   *
   * @see #getConfigXML()
   */
  public abstract boolean setConfigXML(Simulation simulation,
      Collection<Element> configXML, boolean visAvailable);

}
