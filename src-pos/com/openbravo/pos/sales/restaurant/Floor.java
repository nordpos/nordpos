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

package com.openbravo.pos.sales.restaurant;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.ImageUtils;
import com.openbravo.data.loader.SerializableRead;
import com.openbravo.pos.util.ThumbNailBuilder;
import java.awt.ComponentOrientation;

public class Floor implements SerializableRead {
    
    private static final long serialVersionUID = 8694154682897L;
    private String m_sID;
    private String m_sName;
    private Container m_container;
    private Icon m_icon;
    
    private static Image defimg = null;
    
    /** Creates a new instance of Floor */
    public Floor() {
        try {
            defimg = ImageIO.read(getClass().getClassLoader().getResourceAsStream("com/openbravo/images/atlantikdesigner.png"));               
        } catch (Exception fnfe) {
        }            
    }

    public void readValues(DataRead dr) throws BasicException {
        m_sID = dr.getString(1);
        m_sName = dr.getString(2);
        BufferedImage img = ImageUtils.readImage(dr.getBytes(3));
        ThumbNailBuilder tnbcat = new ThumbNailBuilder(32, 32, defimg);
        m_container = new JPanelDrawing(img);
        m_icon = new ImageIcon(tnbcat.getThumbNail(img));        
    }    
    
    public String getID() {
        return m_sID;
    }
    public String getName() {
        return m_sName;
    }
    public Icon getIcon() {
        return m_icon;
    }    
    public Container getContainer() {
        return m_container;
    }    
    
    private static class JPanelDrawing extends JPanel {
        private Image img;
        
        public JPanelDrawing(Image img) {
            this.img = img;
            setLayout(null);
        }
        
        protected void paintComponent (Graphics g) { 
            super.paintComponent(g);
            if (img != null) {
                g.drawImage(img, 0, 0, this);
            }
        }
        
        public Dimension getPreferredSize() {
            return (img == null) 
                ? new Dimension(640, 480) 
                : new Dimension(img.getWidth(this), img.getHeight(this));
        }
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }
    }    
}
