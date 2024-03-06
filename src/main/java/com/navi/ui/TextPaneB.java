package com.navi.ui;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class TextPaneB extends JTextPane {


    @Override
    public boolean getScrollableTracksViewportWidth() {
        Component parent = getParent();
        ComponentUI ui = getUI();

        return parent != null ? (ui.getPreferredSize(this).width <= parent.getSize().width) : true;
    }
}
