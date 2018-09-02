package name.ulbricht.jigsaw.console;

import name.ulbricht.jigsaw.greetings.Greetings;
import name.ulbricht.jigsaw.greetings.GreetingsHandler;

public final class ConsoleGreetingsHandler implements GreetingsHandler {

    public void sendGreetings(final Greetings greetings) {
        System.out.print(String.format("At %s %s said: %s%n", greetings.getTime(), greetings.getSource(), greetings.getMessage()));
    }

}