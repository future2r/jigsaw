package name.ulbricht.jigsaw.msgbox;

import name.ulbricht.jigsaw.greetings.Greetings;
import name.ulbricht.jigsaw.greetings.GreetingsHandler;

import javax.swing.JOptionPane;

public final class MessageBoxGreetingsHandler implements GreetingsHandler {

    public void sendGreetings(final Greetings greetings) {
        JOptionPane.showMessageDialog(null, greetings.getMessage(), greetings.getSource(), JOptionPane.INFORMATION_MESSAGE);
    }

}