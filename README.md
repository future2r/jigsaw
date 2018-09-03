# jigsaw
Demonstrates the usage of Java module system

* An application using services and the deprecated module for JAXB
* A module defining a service API
* Some services implementing the service API

The project is created with Open JDK 10 and Maven. The goal is to provide a blueprint for the configuration of a modularized application up to the generation of an executable image with a custom JRE.

After the Maven build has completed successfully you will find the executable application in **application/target/runimage**. There you can execute **bin/JigsawApplication**. Or you just use the **runme.cmd** in the root forlder.

Thanks to [qualitype](http://qualitype.de) for inspiration.

P.S.: As soon as Java 11 is final released I will try to update this project to use an alternative to the current JAXB implementation.
