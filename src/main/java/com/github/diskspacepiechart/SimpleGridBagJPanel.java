package com.github.diskspacepiechart;

import javax.swing.*;
import java.awt.*;

public class SimpleGridBagJPanel extends JPanel {

    private final GridBagLayout layout;

    SimpleGridBagJPanel() {
        layout = new GridBagLayout();
        setLayout(layout);
    }

    public void add(Component comp, int x, int y, int w, int h, int fill) {

        GridBagConstraints cons = new GridBagConstraints();
        cons.gridx = x;
        cons.gridy = y;
        cons.gridwidth = w;
        cons.gridheight = h;
        cons.fill = fill;
        add(comp, cons);
    }
}
