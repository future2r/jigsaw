module name.ulbricht.jigsaw.application {

	// JDK modules
	requires java.desktop;
	requires java.logging;

	// JAXB API (deprecated module, must be replaced with Java 11)
	requires java.xml.bind;

	// export the root package only
	exports name.ulbricht.jigsaw.application;

	// open the JAXB annotated classes to JAXB
	opens name.ulbricht.jigsaw.application.xml to java.xml.bind;
}