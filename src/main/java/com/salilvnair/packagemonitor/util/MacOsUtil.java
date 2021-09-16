package com.salilvnair.packagemonitor.util;


import java.awt.*;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitResponse;

/**
 * @author Salil V Nair
 */
public class MacOsUtil {
    public static void closeOnQuit() {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            desktop.setQuitHandler((evt, res) -> {
                System.exit(0);
            });
        }
    }
}
