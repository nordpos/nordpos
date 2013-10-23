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

package com.openbravo.editor;

import java.awt.Toolkit;
import com.openbravo.basic.BasicException;
import com.openbravo.format.DoubleUtils;
import com.openbravo.format.Formats;

public abstract class JEditorNumber extends JEditorAbstract {
    
    // Variable numerica
    private final static int NUMBER_ZERONULL = 0;
    private final static int NUMBER_INT = 1;
    private final static int NUMBER_DEC = 2;
    
    private int m_iNumberStatus;
    private String m_sNumber;
    private boolean m_bNegative;
    
    private Formats m_fmt;
    
    /** Creates a new instance of JEditorNumber */
    public JEditorNumber() {
        m_fmt = getFormat();
        reset();
    }
    
    protected abstract Formats getFormat();
    
    public void reset() {
        
        String sOldText = getText();
        
        m_sNumber = "";
        m_bNegative = false;
        m_iNumberStatus = NUMBER_ZERONULL;
        
        reprintText();
        
        firePropertyChange("Text", sOldText, getText());
    }   
    
    public void setDoubleValue(Double dvalue) {
        
        String sOldText = getText();
        
        if (dvalue == null) {
            m_sNumber = "";
            m_bNegative = false;
            m_iNumberStatus = NUMBER_ZERONULL;                 
        } else if (dvalue >= 0.0) {
            m_sNumber = formatDouble(dvalue);
            m_bNegative = false;
            m_iNumberStatus = NUMBER_ZERONULL;            
        } else {
            m_sNumber = formatDouble(-dvalue);
            m_bNegative = true;
            m_iNumberStatus = NUMBER_ZERONULL;            
        }
        reprintText();
        
        firePropertyChange("Text", sOldText, getText());
    } 
    
    public Double getDoubleValue() {  
        
        String text = getText();
        if (text == null || text.equals("")) {
            return null; 
        } else {
            try {
                return Double.parseDouble(text);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }
    
    public void setValueInteger(int ivalue) {
        
        String sOldText = getText();
        
        if (ivalue >= 0) {
            m_sNumber = Integer.toString(ivalue);
            m_bNegative = false;
            m_iNumberStatus = NUMBER_ZERONULL;            
        } else {
            m_sNumber = Integer.toString(-ivalue);
            m_bNegative = true;
            m_iNumberStatus = NUMBER_ZERONULL;            
        }
        reprintText();
        
        firePropertyChange("Text", sOldText, getText());
    } 
    
    public int getValueInteger() throws BasicException {  
        try {
            return Integer.parseInt(getText());
        } catch (NumberFormatException e) {
            throw new BasicException(e);
        }
    }    
    
    private String formatDouble(Double value) {
        String sNumber = Double.toString(DoubleUtils.fixDecimals(value));
        if (sNumber.endsWith(".0")) {
            sNumber = sNumber.substring(0,  sNumber.length() - 2);
        }
        return sNumber;
    }
    
    protected String getEditMode() {
        return "-1.23";
    }  
    
    public String getText() {
        return (m_bNegative ? "-" : "") + m_sNumber;
    }   
    
    protected int getAlignment() {
        return javax.swing.SwingConstants.RIGHT;
    }
    
    protected String getTextEdit() {
        return getText();
    }
    
    protected String getTextFormat() throws BasicException {
        return m_fmt.formatValue(getDoubleValue());
    }
    
    protected void typeCharInternal(char cTrans) {
        transChar(cTrans);
    }
    
    protected void transCharInternal(char cTrans) {
        
        String sOldText = getText();

        if (cTrans == '\u007f') {
            reset();
        } else if (cTrans == '-') {
            m_bNegative = !m_bNegative;
        } else if ((cTrans == '0')
        && (m_iNumberStatus == NUMBER_ZERONULL)) {
            // m_iNumberStatus = NUMBER_ZERO;
            m_sNumber = "0";
        } else if ((cTrans == '1' || cTrans == '2' || cTrans == '3' || cTrans == '4' || cTrans == '5' || cTrans == '6' || cTrans == '7' || cTrans == '8' || cTrans == '9')
        && (m_iNumberStatus == NUMBER_ZERONULL)) {
            m_iNumberStatus = NUMBER_INT;
            m_sNumber = Character.toString(cTrans);
        } else if (cTrans == '.' &&  m_iNumberStatus == NUMBER_ZERONULL) {
            m_iNumberStatus = NUMBER_DEC;
            m_sNumber = "0.";
            
        } else if ((cTrans == '0' || cTrans == '1' || cTrans == '2' || cTrans == '3' || cTrans == '4' || cTrans == '5' || cTrans == '6' || cTrans == '7' || cTrans == '8' || cTrans == '9')
        && (m_iNumberStatus == NUMBER_INT)) {
            //m_iNumberStatus = NUMBER_INT;
            m_sNumber += cTrans;
        } else if (cTrans == '.' &&  m_iNumberStatus == NUMBER_INT) {
            m_iNumberStatus = NUMBER_DEC;
            m_sNumber += '.';
            
        } else if ((cTrans == '0' || cTrans == '1' || cTrans == '2' || cTrans == '3' || cTrans == '4' || cTrans == '5' || cTrans == '6' || cTrans == '7' || cTrans == '8' || cTrans == '9')
        && (m_iNumberStatus == NUMBER_DEC)) {
            m_sNumber += cTrans;
            
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
        
        firePropertyChange("Text", sOldText, getText());
    } 
   
}
