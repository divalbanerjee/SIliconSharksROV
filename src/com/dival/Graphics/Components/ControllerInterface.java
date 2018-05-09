package com.dival.Graphics.Components;

import com.SiliconSharks.Controller.ControlSystem;
import com.SiliconSharks.Controller.Gamepad;
import com.dival.Core.Gamepad;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by bandi on 6/1/2017.
 */
public class ControllerInterface extends JPanel{

    private Gamepad gamePad;
    private int myPilotNumber;
    private Font font30Pt = new Font("Helvetica" , Font.PLAIN, 20);
    private BufferedImage imgController = null;
    private Boolean drawStatus = false;
    private boolean repaintonce = false;

    public ControllerInterface(int pilotNumber){
        setBackground(new Color(44, 62, 80));
        myPilotNumber = pilotNumber;
        gamePad  = ControlSystem.getGamepad(myPilotNumber-1);
    }

    public BufferedImage getControllerImage() {
        //Open Image
        BufferedImage controllerIMG = new BufferedImage(400,250,BufferedImage.TYPE_INT_ARGB);

        try {
            BufferedImage before = ImageIO.read(new File("src/com/SiliconSharks/" +
                    "Graphics/Images/controller.png"));

            int w = before.getWidth();
            int h = before.getHeight();
            //Scale the image
            AffineTransform at = new AffineTransform();
            at.scale(.3, .3);

            BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

            AffineTransformOp scaleOp =
                    new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

            controllerIMG  = scaleOp.filter(before, after);

        } catch (IOException e) {
            System.out.println(e);
        }
        return controllerIMG;
    }

    public void paintComponent(Graphics g){
        //super.paintComponent(g);
        int w = this.getWidth(), h = this.getHeight();
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if(!drawStatus){
            imgController = this.getControllerImage();
            drawStatus = true;
        }
        g2.drawImage(imgController,10,10,null);
        g2.setFont(font30Pt);
        FontMetrics fontMetrics = getFontMetrics(font30Pt);
        g2.setColor(new Color(236, 239, 241));
        g2.drawString("Pilot " + this.myPilotNumber +"'s Controller",
                w/2-fontMetrics.stringWidth("Pilot " + this.myPilotNumber +"'s Controller")/2,h*9/10+3);
        try {

            if (gamePad.isConnected()) {
                //TODO:I'll clean this up in a bit
                if (gamePad.getButton('X')) g2.setColor(new Color(163, 50, 20));   //left
                else g2.setColor(new Color(63, 143, 40));
                g2.fillOval(193, 63, 18, 18);
                if (gamePad.getButton('B')) g2.setColor(new Color(163, 50, 20));   //right
                else g2.setColor(new Color(63, 143, 40));
                g2.fillOval(232, 63, 18, 18);
                if (gamePad.getButton('Y')) g2.setColor(new Color(163, 50, 20));   //up
                else g2.setColor(new Color(63, 143, 40));
                g2.fillOval(213, 43, 18, 18);
                if (gamePad.getButton('A')) g2.setColor(new Color(163, 50, 20));   //down
                else g2.setColor(new Color(63, 143, 40));
                g2.fillOval(213, 82, 18, 18);
                g2.setColor(new Color(150, 150, 40));
                g2.fillOval(99 + (int) (14 * gamePad.getAxis("LX")), 110 + (int) (14 * gamePad.getAxis("LY")), 10, 10);
                g2.fillOval(177 + (int) (14 * gamePad.getAxis("RX")), 110 + (int) (14 * gamePad.getAxis("RY")), 10, 10);
                g2.setFont(new Font("Helvetica",Font.PLAIN,15));
                fontMetrics= getFontMetrics(g2.getFont());
                g2.setColor(Color.WHITE);
                g2.drawString("Connected", w/2-fontMetrics.stringWidth("Connected")/2-3,h*8/10);
                g2.setColor(new Color(50, 190, 50));
                g2.fillOval(w/2+fontMetrics.stringWidth("Connected")/2, h*8/10-10, 10, 10);
            } else {
                g2.setColor(Color.WHITE);
                fontMetrics= getFontMetrics(g2.getFont());
                g2.setColor(Color.WHITE);
                g2.drawString("Disconnected", w/2-fontMetrics.stringWidth("Disconnected")/2-3,h*8/10);
                g2.setColor(new Color(230, 40, 40));
                g2.fillOval(w/2+fontMetrics.stringWidth("Disconnected")/2, h*8/10-10, 10, 10);
            }
        }catch(NullPointerException e){
            System.out.println(e);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Helvetica",Font.PLAIN,15));
            g2.drawString("Disconnected", imgController.getWidth()/4 - 100,
                    (int) (imgController.getHeight() / 2) -30);
            g2.setColor(new Color(200, 70, 50));
            g2.fillOval(imgController.getWidth()/4+30, (imgController.getHeight() / 2) + 30, 10, 10);
        }
    }

    public void smartRepaint() {
        repaint();
    }
}
