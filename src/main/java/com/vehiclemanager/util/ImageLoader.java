package com.vehiclemanager.util;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.File;
import java.net.URL;

/**
 * Resolves image file names to scaled {@link ImageIcon}s.
 * Fixes the old hard-coded absolute paths: images are looked up by file name
 * in a configurable directory (system property {@code garage.images.dir}),
 * then on the classpath, with a graceful placeholder fallback.
 */
public final class ImageLoader {

    private static final String[] BASE_DIRS = {
            System.getProperty("garage.images.dir", "src/main/resources/images"),
            "images", "src/main/resources/images"
    };
    public static final String PLACEHOLDER = "pas_image.png";

    private ImageLoader() {}

    /** Load {@code name} scaled to fit {@code w x h}, or a placeholder if missing. */
    public static ImageIcon load(String name, int w, int h) {
        Image img = rawImage(name);
        if (img == null) img = rawImage(PLACEHOLDER);
        if (img == null) return blank(w, h);
        return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    private static Image rawImage(String name) {
        if (name == null || name.isBlank()) return null;
        String file = new File(name).getName(); // keep file name only
        for (String dir : BASE_DIRS) {
            File f = new File(dir, file);
            if (f.isFile()) return new ImageIcon(f.getAbsolutePath()).getImage();
        }
        URL res = ImageLoader.class.getResource("/images/" + file);
        if (res != null) return new ImageIcon(res).getImage();
        // maybe an absolute/relative path that actually exists (legacy data)
        File direct = new File(name);
        if (direct.isFile()) return new ImageIcon(direct.getAbsolutePath()).getImage();
        return null;
    }

    private static ImageIcon blank(int w, int h) {
        java.awt.image.BufferedImage b =
                new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = b.createGraphics();
        g.setColor(new java.awt.Color(230, 230, 230));
        g.fillRect(0, 0, w, h);
        g.setColor(new java.awt.Color(150, 150, 150));
        g.drawRect(0, 0, w - 1, h - 1);
        g.drawString("Pas d'image", w / 2 - 35, h / 2);
        g.dispose();
        return new ImageIcon(b);
    }
}
