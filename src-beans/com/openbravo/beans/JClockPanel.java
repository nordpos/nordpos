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

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class JClockPanel extends javax.swing.JPanel {
    
    private static Calendar m_calendar = new GregorianCalendar(); // solo de ayuda...
    
    private Date m_date;
    private boolean m_bSeconds;
    private long m_lPeriod;
    
    /** Creates new form JClockPanel */
    public JClockPanel() {
        this(true);
    }
    
    public JClockPanel(boolean bSeconds) {
        
        initComponents();
        
        m_bSeconds = bSeconds;
        m_date = null;
        m_lPeriod = 0L;
    }
    
    public void setSecondsVisible(boolean bValue) {
        m_bSeconds = bValue;
        repaint();
    }
    public boolean isSecondsVisible() {
        return m_bSeconds;
    }
    public void setPeriod(long period) {
        if (period >= 0L) {
            m_lPeriod = period;
            repaint();
        }
    }
    public long getPeriod() {
        return m_lPeriod;
    }
    
    public void setTime(Date dDate){
        m_date = dDate;
        repaint();
    }
    
    public Date getTime() {
        return m_date;
    }
    
    public void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        
        int width = getWidth();
        int height = getHeight();
        
        double dhour = 0.0;
        double dminute = 0.0;
        double dsecond = 0.0;
            
        // Calculo los atributos de la hora que voy a pintar
        if (m_date != null) {            
            m_calendar.setTime(m_date);
            dhour = (double) m_calendar.get(Calendar.HOUR_OF_DAY);
            dminute = (double) m_calendar.get(Calendar.MINUTE);
            dsecond = (double) m_calendar.get(Calendar.SECOND);
        }
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // guardo los valores iniciales
        Paint oldPainter = g2.getPaint();
        AffineTransform oldt = g2.getTransform();

        // pinto el fondo
//        g2.setPaint(new GradientPaint(0, 0, Color.WHITE, width, 0, Color.LIGHT_GRAY));
//        g2.fill(g2.getClip());
        
        // Calculo el centro y el tamano del reloj
        int icenterx = width / 2;
        int icentery = height / 2;
        int iradius = Math.min(icenterx, icentery);        
        
        // Centro las coordenadas y ajusto la transformacion del tamano del reloj
        g2.transform(AffineTransform.getTranslateInstance(icenterx, icentery));
        g2.transform(AffineTransform.getScaleInstance(iradius / 1100.0 , iradius / 1100.0));       
        AffineTransform mytrans = g2.getTransform();
        
        // Pinto la esfera del reloj;
        g2.setPaint(this.isEnabled() 
            ? new GradientPaint(-1200, -1200, Color.BLUE, 1200, 1200, Color.CYAN)
            : new GradientPaint(-1200, -1200, Color.GRAY, 1200, 1200, Color.LIGHT_GRAY));
        g2.fillOval(-1000, -1000, 2000, 2000);
        g2.setPaint(this.isEnabled()
            ? new GradientPaint(-1200, -1200, Color.CYAN, 1200, 1200, Color.BLUE)
            : new GradientPaint(-1200, -1200, Color.LIGHT_GRAY, 1200, 1200, Color.GRAY));
        g2.fillOval(-900, -900, 1800, 1800);
        g2.setColor(Color.BLACK);
        g2.drawOval(-1000, -1000, 2000, 2000);       
        
        // Pinto las marcas pequenas, los minutos
        for (int i = 0; i < 60; i++) {
            g2.setColor(Color.WHITE);
            g2.fillRect(900, -5 , 50, 10);
            g2.transform(AffineTransform.getRotateInstance(Math.PI / 30.0));
        }
        
        // Pinto las marcas grandes, las horas.
        g2.setTransform(mytrans);
        for (int i = 0; i < 12; i++) {
            g2.setColor(Color.WHITE);
            g2.fillRect(800, -15 , 150, 30);
            // g2.setColor(Color.BLACK);
            // g2.drawRect(800, -15 , 150, 30);
            g2.transform(AffineTransform.getRotateInstance(Math.PI / 6.0));
        }
        
        if (m_date != null) {
            // Aguja de las horas
            g2.setTransform(mytrans);       
            g2.transform(AffineTransform.getRotateInstance((dhour + dminute / 60.0) * Math.PI / 6.0)); // Poner hora
            
            if (m_lPeriod > 0L) { // pintamos la marca del periodo...
                // dibujo un arco con el periodo seleccionado...
                int iArc = (int) (m_lPeriod / 120000L);
                g2.setColor(new Color(255, 255, 255, 100));
                g2.fillArc(-1000, -1000, 2000, 2000, 90 - iArc, iArc);
                g2.setColor(Color.DARK_GRAY);
                g2.drawArc(-1000, -1000, 2000, 2000, 90 - iArc, iArc);
            } else {
                // la aguja de las horas
                g2.setColor(Color.WHITE);
                g2.fillPolygon(new int[]{0, -35, 0, 35}, new int[]{100, 0, -600, 0}, 4);   
                g2.setColor(Color.DARK_GRAY);
                g2.drawPolygon(new int[]{0, -35, 0, 35}, new int[]{100, 0, -600, 0}, 4);

                // Aguja de los minutos
                g2.setTransform(mytrans);       
                g2.transform(AffineTransform.getRotateInstance((dminute) * Math.PI / 30.0)); // Poner minutos
                g2.setColor(Color.WHITE);
                g2.fillPolygon(new int[]{0, -35, 0, 35}, new int[]{100, 0, -900, 0}, 4);   
                g2.setColor(Color.DARK_GRAY);
                g2.drawPolygon(new int[]{0, -35, 0, 35}, new int[]{100, 0, -900, 0}, 4);      
        
                // Aguja de los segundos
                if (m_bSeconds) {
                    g2.setTransform(mytrans);       
                    g2.transform(AffineTransform.getRotateInstance(dsecond * Math.PI / 30.0)); // Poner segundos
                    g2.setColor(Color.YELLOW);
                    g2.fillPolygon(new int[]{-15, 0, 15}, new int[]{200, -900, 200},  3);   
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawPolygon(new int[]{-15, 0, 15}, new int[]{200, -900, 200},  3);   

                    g2.setTransform(mytrans);       
                    g2.setColor(Color.YELLOW);
                    g2.fillOval(-25, -25, 50, 50);
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawOval(-25, -25, 50, 50);
                }
            }
        }
        
        // Pinto el tornillo central
        g2.setColor(Color.WHITE);
        g2.fillOval(-10, -10, 20, 20);
        g2.setColor(Color.BLACK);
        g2.drawOval(-10, -10, 20, 20);
        
        // restauro los valores iniciales
        g2.setTransform(oldt);
        g2.setPaint(oldPainter);
    }   
    
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

    }
    // </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
