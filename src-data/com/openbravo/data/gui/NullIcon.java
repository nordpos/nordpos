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

package com.openbravo.data.gui;

import javax.swing.Icon;

/**
 *
 * @author  adrian
 */
public class NullIcon implements Icon {
    
    private int m_iWidth;
    private int m_iHeight;
    
    /** Creates a new instance of NullIcon */
    public NullIcon(int width, int height) {
        m_iWidth = width;
        m_iHeight = height;
    }
    
    public int getIconHeight() {
        return m_iHeight;
    }
    
    public int getIconWidth() {
        return m_iWidth;
    }
    
    public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {
    }
    
}
