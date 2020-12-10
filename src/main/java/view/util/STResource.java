package view.util;

import java.net.URL;

import javax.swing.ImageIcon;

import view.MainView;

public class STResource {
	private STResource() {
	}

	public static ImageIcon getStartImage() {
		return new ImageIcon(STResource.get("start.png"));
	}

	public static ImageIcon getStopImage() {
		return new ImageIcon(STResource.get("stop.png"));
	}

	public static ImageIcon getResetImage() {
		return new ImageIcon(STResource.get("reset.png"));
	}

	public static ImageIcon getExportImage() {
		return new ImageIcon(STResource.get("export.png"));
	}

	private static URL get(final String resource) {
		final URL res = MainView.class.getResource("/img/" + resource);
		return res != null ? res : MainView.class.getResource("/img/notFound.png");
	}
}
