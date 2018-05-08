package com.dival.GUI;

import javax.swing.*;
import java.awt.*;

public class dataViewGUI extends JFrame {
    private Font font20Pt = new Font("Helvetica" , Font.PLAIN, 20);
    Container container;
    Color textColor = new Color(0, 0, 0);
    Color backColor1 = new Color(236,239,241);
    Color backColor = new Color(38, 50, 56);

    public dataViewGUI(){
        JPanel layoutManager = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        setSize(1900,1200);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setVisible(true);
        setBackground(backColor);
        container = getContentPane();
        container.setBackground(backColor);
        layoutManager.setBackground(backColor);

        c.gridx = 1;
        c.gridy = 0;
        c.weightx  = 1;




    }
}
