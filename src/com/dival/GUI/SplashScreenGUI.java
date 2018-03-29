package com.dival.GUI;

import javax.swing.*;
import java.awt.*;

import static com.sun.glass.ui.Cursor.setVisible;

public class SplashScreenGUI extends JFrame {
    private Font font20Pt = new Font("Helvetica" , Font.PLAIN, 20);
    Container container;
    Color textColor = new Color(0, 0, 0);
    Color backColor = new Color(236,239,241);

    public SplashScreenGUI(){
        JPanel layoutManager = new JPanel(new GridLayout(1,1));
        GridBagConstraints c = new GridBagConstraints();

        setSize(1900,1200);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setVisible(true);
        container = getContentPane();
        container.setBackground(backColor);
        layoutManager.setBackground(backColor);
        container.add(layoutManager);

    }
}
