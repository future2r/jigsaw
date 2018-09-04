module name.ulbricht.jigsaw.msgbox {

	// JDK modules
	requires java.desktop;

	// service API
	requires name.ulbricht.jigsaw.greetings;
	
	// provide service implemenations
	provides name.ulbricht.jigsaw.greetings.GreetingsHandler with
		name.ulbricht.jigsaw.msgbox.AWTGreetingsHandler,
		name.ulbricht.jigsaw.msgbox.SwingGreetingsHandler;
}