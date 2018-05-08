package com.dival.GUI;

import com.dival.Graphics.Components.ImageBox;
import com.sun.javaws.ui.SplashScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static com.sun.glass.ui.Cursor.setVisible;

public class SplashScreenGUI extends JFrame {
    private Font font20Pt = new Font("Helvetica" , Font.PLAIN, 20);
    Container container;
    Color textColor = new Color(0, 0, 0);
    Color backColor1 = new Color(236,239,241);
    Color backColor = new Color(38, 50, 56);
    ImageBox SplashScreenLogo = new ImageBox("logo.png");



    public SplashScreenGUI(){
            JPanel layoutManager = new JPanel(new GridLayout(1,1));
            GridBagConstraints c = new GridBagConstraints();

            setSize(1900,1200);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setUndecorated(true);
            setVisible(true);
            setBackground(backColor);
            container = getContentPane();
            container.setBackground(backColor);
            layoutManager.setBackground(backColor);
            layoutManager.add(SplashScreenLogo);
            container.add(layoutManager);

            this.addWindowFocusListener(new WindowFocusListener() {
                @Override
                public void windowGainedFocus(WindowEvent e) {
                    SplashScreenLogo.generateOverlayAlpha();
                }

                @Override
                public void windowLostFocus(WindowEvent e) {

                }
            });
        }


    }

