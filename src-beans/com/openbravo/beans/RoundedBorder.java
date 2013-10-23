//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2007-2009 Openbravo, S.L.
//    http://www.openbravo.com/product/pos
//
//    This file is part of Openbravo POS.
//
//    Openbravo POS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Openbravo POS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Openbravo POS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.beans;

import java.awt.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

public class RoundedBorder extends AbstractBorder {
    
    private static Border blackLine;
    private static Border grayLine;
    private static Border gradientBorder;
    
    protected Color colorBorder;
    protected Color colorgradient;
    protected int roundedRadius;
    protected float thickness;
    protected boolean filled;
    
    private float ftop;
    private float fbottom;
    private float ftopinset;
    private float fbottominset;

    public static Border createBlackLineBorder() {
        if (blackLine == null) {
            blackLine = new RoundedBorder(Color.BLACK);
        }
        return blackLine;
    }

    public static Border createGrayLineBorder() {
        if (grayLine == null) {
            grayLine = new RoundedBorder(Color.GRAY);
        }
        return grayLine;
    }
    
    public static Border createGradientBorder() {
        if (gradientBorder == null) {
            gradientBorder = new RoundedBorder(Color.GRAY, 0f, 8, false, false);
        }
        return gradientBorder;
    }
    
    public RoundedBorder(Color colorBorder) {
        this(colorBorder, Color.WHITE, 1f, 0, true, true);
    }

    public RoundedBorder(Color colorBorder, float thickness) {
        this(colorBorder, Color.WHITE, thickness, 0, true, true);
    }

    public RoundedBorder(Color colorBorder, float thickness, int roundedRadius) {
        this(colorBorder, Color.WHITE, thickness, roundedRadius, true, true);
    }

    public RoundedBorder(Color colorBorder, float thickness, int roundedRadius, boolean btopborder, boolean bbottomborder) {
        this(colorBorder, Color.WHITE, thickness, roundedRadius, btopborder, bbottomborder);
    }
    public RoundedBorder(Color colorBorder, Color colorgradient, float thickness, int roundedRadius, boolean btopborder, boolean bbottomborder) {
        
        this.colorBorder = colorBorder;
        this.colorgradient = colorgradient;
        this.thickness = thickness;
        this.roundedRadius = roundedRadius;
        this.filled = true;
        
        ftop = btopborder ? 0f : thickness + roundedRadius;
        fbottom = bbottomborder ? 0f : thickness + roundedRadius;
        ftopinset = btopborder ? 0f : thickness; // para los bordes a derecha e izquierda
        fbottominset = bbottomborder ? 0f : thickness; // para los bordes a derecha e izquierda
    }
    
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        
        Graphics2D g2d = (Graphics2D) g;
        
        Object oldAntialias = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        Stroke oldStroke = g2d.getStroke();       
        Paint oldColor = g2d.getPaint();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);    
             
        float imedium = thickness; 
        
        if (filled) {
            if (c.getComponentOrientation() == ComponentOrientation.RIGHT_TO_LEFT) {
                g2d.setPaint(new GradientPaint(0, 0, c.getBackground(), width, 0, colorgradient));        
            } else {
                g2d.setPaint(new GradientPaint(0, 0, colorgradient, width, 0, c.getBackground()));        
            }
            g2d.fillRoundRect(                
                    (int) (x + thickness), 
                    (int) (y + thickness - ftop), 
                    (int) (width - thickness - thickness), 
                    (int) (height - thickness - thickness + ftop + fbottom), 
                    (int) (roundedRadius * 2 - imedium), 
                    (int) (roundedRadius * 2 - imedium)); 
        }
        
        if (thickness > 0f) {
            g2d.setStroke(new BasicStroke(thickness));
            g2d.setPaint(colorBorder);
            g2d.drawRoundRect(
                    (int) (x), 
                    (int) (y - ftop), 
                    (int) (width- thickness), 
                    (int) (height - thickness + ftop + fbottom), 
                    roundedRadius * 2, 
                    roundedRadius * 2);  
        }
        
        g2d.setPaint(oldColor);
        g2d.setStroke(oldStroke);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntialias);    
    }

    @Override
    public Insets getBorderInsets(Component c) {
 
//        // Los bordes estan arriba y abajo       
//        return new Insets(
//                (int)(0.5 + thickness + roundedRadius - ftop), 
//                (int)(0.5 + thickness),
//                (int)(0.5 + thickness + roundedRadius - fbottom), 
//                (int)(0.5 + thickness));  
        
        // Los bordes estan a derecha y a izquierda
        return new Insets(
                (int)(0.5 + thickness - ftopinset), 
                (int)(0.5 + thickness + roundedRadius),
                (int)(0.5 + thickness - fbottominset), 
                (int)(0.5 + thickness + roundedRadius));        
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {

//        insets.top = (int)(0.5 + thickness + roundedRadius - ftop);
//        insets.left =(int)(0.5 + thickness);
//        insets.bottom = (int)(0.5 + thickness + roundedRadius - fbottom);
//        insets.right = (int)(0.5 + thickness);
        
        insets.top = (int)(0.5 + thickness - ftopinset);
        insets.left =(int)(0.5 + thickness + roundedRadius);
        insets.bottom = (int)(0.5 + thickness - fbottominset);
        insets.right = (int)(0.5 + thickness + roundedRadius);
        
        return insets;
    }

    public Color getLineColor() {
        return colorBorder;
    }

    public float getThickness() {
        return thickness;
    }
    
    public boolean isFilled() {
        return filled;
    }

    @Override
    public boolean isBorderOpaque() { 
        return true; 
    }
}