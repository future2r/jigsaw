module name.ulbricht.jigsaw.msgbox {

	// JDK modules
	requires java.desktop;

	// service API
	requires name.ulbricht.jigsaw.greetings;
	
	// provide a service implemenation
	provides name.ulbricht.jigsaw.greetings.GreetingsHandler with name.ulbricht.jigsaw.msgbox.MessageBoxGreetingsHandler;
}