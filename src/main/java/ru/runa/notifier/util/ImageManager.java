
package ru.runa.notifier.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.graphics.Image;

import ru.runa.notifier.GUI;

public class ImageManager {
    private static final Log log = LogFactory.getLog(ImageManager.class);

    public static Image iconTrayNoTasks;

    public static Image iconTrayTasks;

    public static Image iconTrayNotLogged;

    public static Image iconTrayError;

    public static Image iconApplication;

    public static Image iconTrayClose;

    public static Image iconTrayCloseOver;

    public static Image iconTrayTease;

    public static Image imageSplash;

    public static void disposeIcons() {
        iconTrayTasks.dispose();
        iconApplication.dispose();
        iconTrayClose.dispose();
        iconTrayCloseOver.dispose();
        iconTrayTease.dispose();
        iconTrayNoTasks.dispose();
        iconTrayNotLogged.dispose();
        imageSplash.dispose();
    }

    public static void initIcons() {
        iconTrayTasks = loadImage("/img/tasks.gif");
        iconApplication = loadImage("/img/application.gif");
        iconTrayClose = loadImage("/img/tray_close.gif");
        iconTrayCloseOver = loadImage("/img/tray_close_over.gif");
        iconTrayTease = loadImage("/img/tray_tease.gif");
        iconTrayNoTasks = loadImage("/img/tray.gif");
        iconTrayNotLogged = loadImage("/img/tray_not_logged.gif");
        iconTrayError = loadImage("/img/tray_error.gif");
        imageSplash = loadImage("/img/splash.gif");
    }

    private static Image loadImage(String path) {
        Image image;
        InputStream is = null;
        try {
            is = ImageManager.class.getResourceAsStream(path);
            if (is != null) {
                image = new Image(GUI.display, is);
            } else {
                image = new Image(GUI.display, 16, 16);
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    if (log.isDebugEnabled()) {
                        log.debug("Error closing image " + path, e);
                    }
                }
            }
        }

        return image;
    }
}
