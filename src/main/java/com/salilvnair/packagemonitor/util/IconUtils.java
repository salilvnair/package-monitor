package com.salilvnair.packagemonitor.util;

import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.net.URL;

/**
 * @author Salil V Nair
 */
public class IconUtils {
    private IconUtils() {}

    public static ImageIcon createIcon(String path) {
        URL url = IconUtils.class.getResource(path);
        if(url == null ) {
            System.err.println("icon not found");
        }
        assert url != null;
        return new ImageIcon(url);
    }

    public static ImageIcon createIconByTheme(String... paths) {
        String darkPath = paths[0];
        String lightPath = paths[1];
        if(UIUtil.isUnderDarcula()) {
            return createIcon(darkPath);
        }
        return createIcon(lightPath);
    }
}
