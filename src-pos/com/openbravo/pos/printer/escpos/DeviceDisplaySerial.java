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

package com.openbravo.pos.printer.escpos;

import com.openbravo.pos.printer.*;
import com.openbravo.pos.forms.AppLocal;
/**
 *
 * @author adrianromero
 */
public abstract class DeviceDisplaySerial implements DeviceDisplay, DeviceDisplayImpl {
    
    private String m_sName;    
    protected PrinterWritter display;  
    
    protected DeviceDisplayBase m_displaylines;
    
    public DeviceDisplaySerial() {
        m_displaylines = new DeviceDisplayBase(this);
    }
    
    protected void init(PrinterWritter display) {                
        m_sName = AppLocal.getIntString("Printer.Serial");
        this.display = display;      
        initVisor();        
    }
   
    public String getDisplayName() {
        return m_sName;
    }    
    public String getDisplayDescription() {
        return null;
    }        
    public javax.swing.JComponent getDisplayComponent() {
        return null;
    }
    
    public void writeVisor(int animation, String sLine1, String sLine2) {
        m_displaylines.writeVisor(animation, sLine1, sLine2);
    }    
    
    public void writeVisor(String sLine1, String sLine2) {        
        m_displaylines.writeVisor(sLine1, sLine2);
    }
     
    public void clearVisor() {
        m_displaylines.clearVisor();
    }
    
    public abstract void initVisor();
}
