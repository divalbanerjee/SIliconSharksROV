package com.dival.Graphics.Components;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;


//image name is in the format: myImageName.png    without the file extension this will not work
public class ImageBox extends JPanel {

    private BufferedImage imgMyImage;
    private double scaleX;
    private double scaleY;
    private double rotationInDegrees;
    private boolean fadeIsEnabled = true;
    private int opacity;
    Thread runner;

    public ImageBox(String imageName){
        this.setBackground(new Color(0,0,0,0));
        this.scaleX = .8;
        this.scaleY = .8;
        this.rotationInDegrees = 0;
        openImage(imageName);
    }

    public ImageBox(String imageName, double scaleWidth, double scaleHeight,double rotationAngle){
        this.setBackground(new Color(0,0,0,0));
        this.scaleX = scaleWidth;
        this.scaleY = scaleHeight;
        this.rotationInDegrees = rotationAngle;
        openImage(imageName);
    }
    public void setScaleX(double scaleWidth){
        this.scaleX = scaleWidth;
    }

    public void setScaleY(double scaleHeight){
        this.scaleY = scaleHeight;
    }

    public void setRotationInDegrees(double Angle_In_Degrees){
        this.rotationInDegrees = Angle_In_Degrees;
    }

    public void setFade(boolean IS_Enabled){
        this.fadeIsEnabled = IS_Enabled;
    }

    public void openImage(String imageName){
        try{
            BufferedImage before = ImageIO.read(new File("src/com/dival/Graphics/Images/" + imageName));
            int w = before.getWidth();
            int h = before.getHeight();
            BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            AffineTransform at = new AffineTransform();
            at.scale(scaleX, scaleY);  //Allows for easy way to scale the image
            AffineTransformOp scaleOp =
                    new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            this.imgMyImage = scaleOp.filter(before, after);
        }
        //In the event that the image can't  be found or some other error
        catch(IOException e){
            System.out.println("Image failed to open");
        }
    }

    public void generateOverlayAlpha(){
        //this generates the alpha value(transparency value) for the OverlayPanel that is drawn
        //on top of the image
        Thread fade = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thead started");

                for (int c = 255; c > 0 ; c--) {
                    try {
                        sleep(20);    //Change this number to speed up or slow down the fade
                        System.out.println(c);
                        opacity = c;
                        repaint();
                    } catch (InterruptedException ex) {
                        System.out.println("wait failed");
                    }
                }
            }

        });
        fade.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;


        AffineTransform tx = AffineTransform.getRotateInstance(Math.toRadians(this.rotationInDegrees),
                0, 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

        g2.drawImage(op.filter(this.imgMyImage, null),
                (int)(this.getWidth()-(imgMyImage.getWidth()*this.scaleX))/2,
                (int)(this.getHeight()-(imgMyImage.getHeight()*this.scaleY))/2,
               null);

        if (fadeIsEnabled) {
            System.out.println("true");
            Color backGroundColor = new Color(getParent().getBackground().getRGB());
            g2.setColor(backGroundColor);


            Color overlayColor = new Color(backGroundColor.getRed(), backGroundColor.getGreen(),
                    backGroundColor.getBlue(), opacity);
            g2.setColor(overlayColor);
            g2.fillRect(0, 0, getParent().getWidth(), getParent().getHeight());

        }
    }
}
