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

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

public class JFlowPanel extends JPanel implements Scrollable {
    
    private int hgap = 5;
    private int vgap  = 5;
    
    public JFlowPanel() {
        this(5, 5);
    }
    public JFlowPanel(int hgap, int vgap) {
        this.hgap = hgap;
        this.vgap = vgap;
    }
    
    public void setHorizontalGap(int iValue) {
        hgap = iValue;
    }
    public int getHorizontalGap() {
        return hgap;
    }
    public void setVerticalGap(int iValue) {
        vgap = iValue;
    }
    public int getVerticalGap(int iValue) {
        return vgap;
    }  
    
    private Dimension calculateFlowLayout(boolean bDoChilds) {
        Dimension dim = new Dimension(0, hgap);
        
        int maxWidth;
        if (getParent() != null && getParent() instanceof JViewport) {
            JViewport viewport = (JViewport) getParent();
            maxWidth = viewport.getExtentSize().width;
        } else if (getParent() != null){
            maxWidth = getParent().getWidth();
        } else {
            maxWidth = getWidth();
        }
        
        synchronized (getTreeLock()) {
            int compCount = getComponentCount();
            int maxRowWidth = 0;
            int maxRowHeight = 0;
            int x = 0;

            for (int i = 0 ; i < compCount ; i++) {
                Component m = getComponent(i);
                if (m.isVisible()) {
                    Dimension d = m.getPreferredSize();
                    if (x == 0 || (x + hgap + d.width + hgap) <= maxWidth) {
                        // continuamos con esta linea
                        x += hgap;
                        if (bDoChilds) m.setBounds(getPosition(x, maxWidth - d.width), dim.height, d.width, d.height);
                        x += d.width;
                        if (d.height > maxRowHeight) {
                            maxRowHeight = d.height;
                        }
                    } else {
                        // nueva linea
                        dim.height += maxRowHeight + vgap;
                        if (bDoChilds) m.setBounds(getPosition(hgap, maxWidth - d.width), dim.height, d.width, d.height);
                        if (x > maxRowWidth) {
                            maxRowWidth = x;
                        }
                        x = hgap + d.width;
                        maxRowHeight = d.height;
                    }
                }
            }

            // calculamos la ultima linea.
            dim.height += maxRowHeight + vgap;
            if (x > maxRowWidth) {
                maxRowWidth = x;
            }
            dim.width = maxRowWidth;
        }
        return dim;
    }
    
    private int getPosition(int x, int width) {
        if (getComponentOrientation() == ComponentOrientation.RIGHT_TO_LEFT) {
            return width - x ;
        } else {
            return x;
        }
    }
       
    public Dimension getPreferredSize() {
        return calculateFlowLayout(false);
    }
    public Dimension getMinimumSize() { 
        return calculateFlowLayout(false);
    }
    public Dimension getMaximumSize() {
        return calculateFlowLayout(false);
    } 
    public Dimension getPreferredScrollableViewportSize() {
        return calculateFlowLayout(false);
    }
    
    public void doLayout() {
       calculateFlowLayout(true);
    }    
    
    public boolean getScrollableTracksViewportHeight() {
        return (getParent().getHeight() > getPreferredSize().height);
    }

    public boolean getScrollableTracksViewportWidth() {
        return (getParent().getWidth() > getPreferredSize().width);
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        if (getComponentCount() == 0) {
            return orientation == SwingConstants.HORIZONTAL ? hgap : vgap;
        } else {
            return orientation == SwingConstants.HORIZONTAL 
                    ? getComponent(0).getWidth() + hgap 
                    : getComponent(0).getHeight() + vgap;
	}
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        if (getComponentCount() == 0) {
            return orientation == SwingConstants.HORIZONTAL ? hgap : vgap;
        } else {
            if (orientation == SwingConstants.HORIZONTAL) {
                int hunit = getComponent(0).getWidth() + hgap;
                return (visibleRect.width / hunit) * hunit;
            } else {
                int vunit = getComponent(0).getHeight() + vgap;
                return (visibleRect.width / vunit) * vunit;
            }
        }
    }   
    
    public void setEnabled(boolean value) {
        synchronized (getTreeLock()) {
            int compCount = getComponentCount();
            for (int i = 0 ; i < compCount ; i++) {
                getComponent(i).setEnabled(value);
            }
        }
        super.setEnabled(value);
    }
}
