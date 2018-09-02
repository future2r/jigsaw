module name.ulbricht.jigsaw.console {

	// service API
	requires name.ulbricht.jigsaw.greetings;

	// provide a service implemenation
	provides name.ulbricht.jigsaw.greetings.GreetingsHandler with name.ulbricht.jigsaw.console.ConsoleGreetingsHandler;
}