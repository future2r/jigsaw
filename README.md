# jigsaw
Demonsstrates the usage of Java module system

* An application using a service and the deprecated module for JAXB
* A module defining a service API
* A service implementing the API

The project is created with Open JDK 10 and Maven. The goal is to provide a blueprint for the configuration of a modularized application up to the generation of an executable image.

After the Maven build has completed successfully you will find the executable application in **application/target/runimage**. There you can execute **JigsawApplication**.

Thanks to [qualitype](http://qualitype.de) for inspiration.
