package name.ulbricht.jigsaw.application.gui;

import java.awt.Image;
import java.util.Map;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;

final class Resources {

	private static final Map<String, ImageIcon> icons = new HashMap<>();

	static Image getImage(final String name) {
		return getImageIcon(name).getImage();
	}

	static Icon getIcon(final String name) {
		return getImageIcon(name);
	}

	private static ImageIcon getImageIcon(final String name) {
		return icons.computeIfAbsent(name, k -> new ImageIcon(Resources.class.getResource(k)));
	}
}