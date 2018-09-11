package name.ulbricht.jigsaw.application.gui;

import java.awt.Image;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Provides access to the resources like images and localized strings.
 * 
 * @author Frank.Ulbricht
 */
final class Resources {

	private static final Logger log = Logger.getLogger(Resources.class.getPackageName());
	private static final String RESOURCE_BUNDLE_BASE_NAME = "name.ulbricht.jigsaw.application.gui.resources";
	private static final Map<String, ImageIcon> icons = new HashMap<>();

	/**
	 * Creates a new resources accessor with the specified prefix. The prefix is used with each key when accessing resources.
	 * 
	 * @param prefix a non-null prefix without the separating dot
	 * @return a new resources accessor
	 */
	static Resources of(final String prefix) {
		return new Resources(prefix);
	}

	private final String prefix;

	private Resources(final String prefix) {
		this.prefix = Objects.requireNonNull(prefix);
	}

	/**
	 * Adds the specified prefix with a separating dot to the current prefix and returns a new resources accessor.
	 * 
	 * @param prefix a non-null prefix without the separating dot
	 * @return a new resources accessor with the combined prefix
	 */
	Resources with(final String prefix) {
		return new Resources(prefix(Objects.requireNonNull(prefix)));
	}

	/**
	 * Returns the localized string for the given key. If a prefix is set, it will be used with the key.
	 * 
	 * @param key a non-null key
	 * @return the localized string for the key or the key itself
	 */
	String getString(final String key) {
		return getStringValue(key).orElse("!" + prefix(key) + "!");
	}

	/**
	 * Returns the localized char for the given key. If the string has more then one character the first character will be returned.
	 * If a prefix is set, it will be used with the key.
	 * 
	 * @param key a non-null key
	 * @return the localized string for the key or the key itself
	 */
	char getChar(final String key) {
		final var value = getStringValue(key);
		if (value.isPresent()){
			final var s = value.get();
			if (!s.isEmpty()) return s.charAt(0);
		}
		return 0;
	}

	/**
	 * Returns the image for the given key. If a prefix is set, it will be used with the key. Images will be cached internally.
	 * 
	 * @param key a non-null key
	 * @return the image or {@code null}
	 */
	Image getImage(final String key) {
		return Optional.ofNullable(loadImageIcon(getString(key))).map(ImageIcon::getImage).orElse(null);
	}

	/**
	 * Returns a list of icons for the given key. The icon names must be comma-separated. If a prefix is set, it will be used with the key.
	 * Icons will be cached internally.
	 * 
	 * @param key a non-null key
	 * @return the icon or {@code null}
	 */
	List<Image> getImages(final String key) {
		return Stream.of(getString(key).split(",")).map(Resources::loadImageIcon).filter(Objects::nonNull).map(ImageIcon::getImage).collect(Collectors.toList());
	}

	/**
	 * Returns the icon for the given key. If a prefix is set, it will be used with the key. Icons will be cached internally.
	 * 
	 * @param key a non-null key
	 * @return the icon or {@code null}
	 */
	Icon getIcon(final String key) {
		return loadImageIcon(getString(key));
	}
	
	/**
	 * Returns a list of icons for the given key. The icon names must be comma-separated. If a prefix is set, it will be used with the key.
	 * Icons will be cached internally.
	 * 
	 * @param key a non-null key
	 * @return the icon or {@code null}
	 */
	List<Icon> getIcons(final String key) {
		return Stream.of(getString(key).split(",")).map(Resources::loadImageIcon).filter(Objects::nonNull).collect(Collectors.toList());
	}

	private static ImageIcon loadImageIcon(final String name) {
		final var image = icons.computeIfAbsent(name, k -> Optional.ofNullable(Resources.class.getResource(k)).map(ImageIcon::new).orElse(null));
		if (image == null) log.severe(() -> "Image not found: " + name);
		return image;
	}

	private String prefix(final String s) {
		return this.prefix.isEmpty() ? s : this.prefix + "." + s;
	}

	private Optional<String> getStringValue(final String key) {
		final var prefixedKey = prefix(Objects.requireNonNull(key));
		try {
			return Optional.of(ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME).getString(prefixedKey));
		} catch (MissingResourceException ex) {
			return Optional.empty();
		}
	}
}