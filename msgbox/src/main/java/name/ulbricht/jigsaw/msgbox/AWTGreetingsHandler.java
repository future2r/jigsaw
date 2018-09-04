package name.ulbricht.jigsaw.msgbox;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import name.ulbricht.jigsaw.greetings.Greetings;
import name.ulbricht.jigsaw.greetings.GreetingsHandler;

public final class AWTGreetingsHandler implements GreetingsHandler {

    public void sendGreetings(final Greetings greetings) {
        
        final var dlg = new Dialog((Dialog) null, greetings.getSource(), true);
        dlg.addWindowListener(new WindowAdapter() {
            @Override
             public void windowClosing(final WindowEvent e) {
                dlg.dispose();
             }
          });
     
        dlg.setLayout(new BorderLayout());
        dlg.add(new Label(greetings.getMessage()), BorderLayout.CENTER);

        final var panel = new Panel();
        panel.setLayout(new FlowLayout());
        
        final var okButton = new Button("OK");
        okButton.addActionListener(e -> dlg.dispose());
        panel.add(okButton);
        
        dlg.add(panel, BorderLayout.SOUTH);
        
        dlg.pack();
        dlg.setLocationByPlatform(true);
        dlg.setVisible(true);
    }
}