package com.dival.Graphics.Components;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


//image name is in the format: myImageName.png    without the file extension this will not work
public class ImageBox extends JPanel {

    private BufferedImage imgMyImage;
    private double scaleX;
    private double scaleY;
    private double rotationInDegrees;

    public ImageBox(String imageName){
        this.setBackground(new Color(0,0,0,0));
        this.scaleX = .8;
        this.scaleY = .8;
        rotationInDegrees = 0;
        openImage(imageName);
    }

    public void openImage(String imageName){
        try{
            BufferedImage before = ImageIO.read(new File("src/com.dival/Graphics/Images/" + imageName));
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

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    }

}
