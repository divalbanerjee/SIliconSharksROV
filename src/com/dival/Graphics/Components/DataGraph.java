package com.dival.Graphics.Components;

import com.dival.Core.ROVInfo;
import com.dival.Core.ROVStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class DataGraph extends JPanel {
    private ROVStatus[] rovStatuses;
    private int type;
    private int numitems;
    private Font font12Pt = new Font("Helvetica" , Font.PLAIN, 12);

    public DataGraph(int type){
        rovStatuses = ROVInfo.getRovStatuses();
        numitems = rovStatuses.length;
        this.type = type;
        setBackground(getParent().getBackground());
    }

    private int getY1(ROVStatus rovStatus){
        if(rovStatus.getTimeStamp() <= -1) return 0;
        switch(type){
            case 0: return (int)((rovStatus.getVoltage()*230)/15);
            case 5: return (int)(rovStatus.getAmperage(4)*230/6);
            default: return (int) (rovStatus.getAmperage(type-1)*230/30);
        }
    }
    private int getY2(ROVStatus rovStatus){
        if(rovStatus.getTimeStamp() <= -1) return 0;
        switch(type){
            case 1: return (int)((Math.abs(rovStatus.getThruster(0))+Math.abs(rovStatus.getThruster(1))+Math.abs(rovStatus.getThruster(2)))*25*230/30);
            case 2: return (int)(Math.abs(rovStatus.getThruster(0))*25*230/30);
            case 3: return (int)(Math.abs(rovStatus.getThruster(1))*25*230/30);
            case 4: return (int)(Math.abs(rovStatus.getThruster(2))*25*230/30);
        }
        return 0;
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.cyan);
        g2.setFont(font12Pt);
        g2.drawLine(20,20,20,230);
        g2.drawLine(20,230,230,230);
        g2.setColor(Color.YELLOW);
        AffineTransform orig = g2.getTransform();
        g2.translate(125,125);
        g2.rotate(-Math.PI/2);
        switch(type){
            case 0:{
                g2.drawString("Voltage",-30,-110);
                g2.setTransform(orig);
                g2.drawString("12",0,70);
                g2.drawLine(20,66,230,66);
                break;
            }
            case 1:{
                g2.drawString("Amperage",-35,-110);
                g2.setTransform(orig);
                g2.drawString("25",0,63);
                g2.drawLine(20,38,230,38);
                break;
            }
            case 5: {
                g2.drawString("S Amperage", -35, -110);
                g2.setTransform(orig);
                g2.drawString("6", 0, 63);
                g2.drawLine(20, 38, 230, 38);
                break;
            }
            default:{
                g2.drawString("T"+ String.valueOf(type-1)+" Amperage",-42,-110);
                g2.setTransform(orig);
                g2.drawString("25",0,63);
                g2.drawLine(20,38,230,38);
                break;
            }
        }
        int first = numitems - 105;
        int prevY = getY1(rovStatuses[first]);
        int newY;
        g2.setColor(Color.RED);
        for(int i = first; i < numitems-1; i++){
            newY = getY1(rovStatuses[i+1]);
            g2.drawLine((i-first)*2+20,230-prevY,(i+1-first)*2+20,230-newY);
            prevY = newY;
        }
        prevY = getY2(rovStatuses[0]);
        g2.setColor(Color.GREEN);
        if(type >= 1){
            for(int i = first; i < numitems-1; i++){
                newY = getY2(rovStatuses[i+1]);
                g2.drawLine((i-first)*2+20,230-prevY,(i+1-first)*2+20,230-newY);
                prevY = newY;
            }
        }
    }
}

