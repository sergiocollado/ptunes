pTunes
======

pTunes is a framework for runtime adaptation of low-power wireless MAC protocol parameters. It is designed for sensor network data collection applications with soft requirements on network lifetime, end-to-end latency, and end-to-end reliability. pTunes automatically determines optimized MAC parameters at a central controller (e.g., a laptop serving as the gateway between the sensor network and the Internet) to satisfy given application requirements in the face of transient links, topology changes, and traffic dynamics. To close the loop, pTunes uses a novel approach based on [Glossy](ftp://ftp.tik.ee.ethz.ch/pub/people/ferrarif/FZTS2011.pdf) network floods to periodically (1) collect accurate, consistent information about the current network and traffic conditions, and (2) disseminate optimized MAC parameters to all nodes in the network. In this way, pTunes greatly aids in meeting the requirements of real-world sensor network applications by eliminating the need for time-consuming, yet error-prone manual MAC configuration as the network conditions change. In particular, pTunes outperforms static MAC parameters optimized for specific traffic loads and performance trade-offs, achieving severalfold improvements in network lifetime while satisfying end-to-end latency and reliability requirements under heavy wireless interference, sudden traffic peaks, and multiple node failures.

Check out our <a href="ftp://ftp.tik.ee.ethz.ch/pub/people/marcoz/ZFMVT2012.pdf">IPSN'12 paper</a> and [tech report](ftp://ftp.tik.ee.ethz.ch/pub/publications/TIK-Report-325.pdf) to read more about the pTunes system support, our MAC protocol models, and experimental results.

System and Implementation Overview
----------------------------------

The pTunes framework currently consists of four main components:

* Sensor nodes run the default data collection protocol in [Contiki](http://www.contiki-os.org/) v2.3 on top of the X-MAC or LPP link layer. Both link layers expose an interface that allows to change selected operational parameters at runtime. pTunes periodically interrupts the normal application operation for a very short time, with microsecond accuracy simultaneously at all nodes. During these short phases, pTunes uses sequential [Glossy](ftp://ftp.tik.ee.ethz.ch/pub/people/ferrarif/FZTS2011.pdf) floods to collect information about the current routing topology, link qualities, and traffic load, and to disseminate optimized MAC parameters to all nodes in the network.

* The collection sink connects (e.g., via USB) to the base station (e.g., a laptop computer), passing collected network state information on to the base station and receiving optimized MAC parameters from the base station.

* The base station runs the pTunes controller, which is implemented in Java. The controller communicates with the collection sink, decides when to determine new MAC parameters, and starts the optimization process accordingly.

* The MAC parameter optimization problem is modeled as a constraint program and solved on the base station using branch-and-bound coupled with a complete search routine, both of which are provided by the Interval Constraint (IC) solver of the [ECLiPSe](http://eclipseclp.org/) constraint programming system.

Code Layout
-----------

`/ptunes/contiki` (full [Contiki](http://www.contiki-os.org/) v2.3 source tree from early 2011)

`/ptunes/contiki/apps/adaptive-mac` (pTunes test application running on the sensor nodes and sink)

`/ptunes/controller` (pTunes controller running on the base station, including build scripts and config files)

`/ptunes/controller/src` (Java code of the pTunes controller)

`/ptunes/controller/eclipse` ([ECLiPSe](http://eclipseclp.org/) constraint programming system])

`/ptunes/controller/lib` ([ECLiPSe](http://eclipseclp.org/) and [Apache log4j](http://logging.apache.org/log4j/1.2/) libraries)

`/ptunes/cp` ([ECLiPSe](http://eclipseclp.org/) optimization code, including the implementation of the X-MAC and LPP models)

Getting pTunes to Work
----------------------

pTunes is a research prototype. You should be able to built and run pTunes, however, using TelosB-compliant devices, such as the [Tmote Sky](http://www.snm.ethz.ch/Projects/TmoteSky), and a base station computer. Depending on the operating system used on the base station, you may need to update the [ECLiPSe](http://eclipseclp.org/) system and Java libraries accordingly.

Research
--------

pTunes was developed at the [Computer Engineering and Networks Laboratory](http://www.tec.ethz.ch/) at [ETH Zurich](http://www.ethz.ch/) and at the [Networked Embedded Systems Group](https://www.sics.se/groups/networked-embedded-systems-group-nes) at [SICS Swedish ICT](https://www.sics.se/). The pTunes team consists of [Marco Zimmerling](http://www.tik.ee.ethz.ch/~marcoz/), [Federico Ferrari](http://www.tik.ee.ethz.ch/~ferrarif/), [Luca Mottola](http://home.deib.polimi.it/mottola/), [Thiemo Voigt](https://www.sics.se/people/thiemo-voigt), and [Lothar Thiele](http://www.tik.ee.ethz.ch/~thiele/pmwiki/pmwiki.php/Site/Home). Please contact us for more information.