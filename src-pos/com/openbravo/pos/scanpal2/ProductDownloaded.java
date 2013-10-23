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

package com.openbravo.pos.scanpal2;

public class ProductDownloaded {
    
    private String m_sCode;
    private double m_dQuantity;
    
    public ProductDownloaded() {
    }
    
    public void setCode(String value) {
        m_sCode = value;
    }
    public String getCode() {
        return m_sCode;
    }
    public void setQuantity(double value) {
        m_dQuantity = value;
    }
    public double getQuantity() {
        return m_dQuantity;
    }
}