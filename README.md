Hawkshaw
========

Tools for tracking down memory / JVM problems &amp; generating predictable-as-possible VM behaviour 

You can Use Hawkshaw to mimic application object allocation rate behaviour in order to test out GC tuning ideas!

It enables decent control of the memory profile and garbage collection dynamics of the app. At least, that's the theory!

Drivers
-------

Hawkshaw comes with several pre-built drivers, see the hawkshaw.drivers package

Building Hawkshaw
-----------------

Hawkshaw is a Maven 3 project, run the following to build Hawkshaw:

    mvn clean install

Running Hawkshaw
----------------

Hawkshaw is a CL tool, you can run it's various drivers:

    java -cp hawkshaw-<version>.jar hawksahaw.drivers.<Driver> 

e.g. 

    java -cp .:hawkshaw-0.0.1-SNAPSHOT.jar hawkshaw.drivers.GcChurner

License
-------

This project is licensed under GPL v2 - see separate LICENSE.txt file for details.
