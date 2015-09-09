package jsudokusolver.swing;

import java.awt.Font;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

class Utils {

	private static final String ICON_PATH = "/icons/";

	static final String ICON_EXTENSION = ".png";

	static final Font FONT_DEFAULT = new Font("Arial", Font.PLAIN, 16);

	static Image getImage(String imageName, int size) {
		try {
			String iconPath = ICON_PATH + imageName;
			URL iconUrl = Utils.class.getResource(iconPath);
			return ImageIO.read(iconUrl).getScaledInstance(size, size, Image.SCALE_SMOOTH);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


}
