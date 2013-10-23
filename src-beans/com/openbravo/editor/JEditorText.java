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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import com.openbravo.basic.BasicException;

public abstract class JEditorText extends JEditorAbstract {
    
    protected String m_svalue;
    
    public static final int MODE_Abc1 = 0;
    public static final int MODE_abc1 = 1;
    public static final int MODE_ABC1 = 2;
    public static final int MODE_123 = 3;    
    public int m_iMode;
    
    protected int m_iTicks;
    protected char m_cLastChar;
    protected long m_lcount;
    
    private Timer m_jtimer;
    
    private static final char[] CHAR_abc1_1 = {'.', '?', '!', ',', '1', ';', ':'};
    private static final char[] CHAR_abc1_2 = {'a', 'b', 'c', '2', '\u00a0'};
    private static final char[] CHAR_abc1_3 = {'d', 'e', 'f', '3', '\u201a'};
    private static final char[] CHAR_abc1_4 = {'g', 'h', 'i', '4', '\u00a1'};
    private static final char[] CHAR_abc1_5 = {'j', 'k', 'l', '5'};
    private static final char[] CHAR_abc1_6 = {'m', 'n', 'o', '6', '\u00a4', '\u00a2'};
    private static final char[] CHAR_abc1_7 = {'p', 'q', 'r', 's', '7'};
    private static final char[] CHAR_abc1_8 = {'t', 'u', 'v', '8', '\u00a3', '\ufffd'};
    private static final char[] CHAR_abc1_9 = {'w', 'x', 'y', 'z', '9'};
    private static final char[] CHAR_abc1_0 = {' ', '0'};
    
    private static final char[] CHAR_ABC1_1 = {'.', '?', '!', ',', '1', ';', ':'};
    private static final char[] CHAR_ABC1_2 = {'A', 'B', 'C', '2', '\u00b5'};
    private static final char[] CHAR_ABC1_3 = {'D', 'E', 'F', '3', '\u00c9'};
    private static final char[] CHAR_ABC1_4 = {'G', 'H', 'I', '4', '\u00cd'};
    private static final char[] CHAR_ABC1_5 = {'J', 'K', 'L', '5'};
    private static final char[] CHAR_ABC1_6 = {'M', 'N', 'O', '6', '\u00d1', '\u00d3'};
    private static final char[] CHAR_ABC1_7 = {'P', 'Q', 'R', 'S', '7'};
    private static final char[] CHAR_ABC1_8 = {'T', 'U', 'V', '8', '\u00da', '\u00dc'};
    private static final char[] CHAR_ABC1_9 = {'W', 'X', 'Y', 'Z', '9'};
    private static final char[] CHAR_ABC1_0 = {' ', '0'};   
    
    /** Creates a new instance of JEditorString */
    public JEditorText() {
        m_svalue = null;
        
        m_iTicks = 0;
        m_cLastChar = '\u0000';
        m_jtimer = new javax.swing.Timer(1000, new TimerAction());
        m_lcount = 0L;
        m_iMode = getStartMode(); //MODE_Abc1;
        m_jtimer.start();
    }
    
    protected abstract int getStartMode();

    
    public final void reset() {
        
        String sOldText = getText();
        
        // Los hemos borrado todos.
        m_iMode = getStartMode(); //MODE_Abc1;
        m_svalue = null;
        m_iTicks = 0;
        m_cLastChar = '\u0000';  
        
        reprintText();
        
        firePropertyChange("Text", sOldText, getText());
    } 
    
    public final void setText(String sText) {
        
        String sOldText = getText();

        m_svalue = sText;
        m_iTicks = 0;
        m_cLastChar = '\u0000';  
        
        reprintText();
        
        firePropertyChange("Text", sOldText, getText());
    }
    
    public final void setEditModeEnum(int iMode) {
        
        m_iMode = iMode;
        m_iTicks = 0;
        m_cLastChar = '\u0000';  
        
        reprintText();
    }    
    
    public final String getText() {
        if (m_cLastChar == '\u0000') {
            return m_svalue;
        } else {
            return appendChar2Value(getKeyChar());
        }        
    }
      
    protected final int getAlignment() {
        return javax.swing.SwingConstants.LEFT;
    }
       
    protected final String getEditMode() {
        switch (m_iMode) {
        case MODE_Abc1: return "Abc1";
        case MODE_abc1: return "abc1";
        case MODE_ABC1: return "ABC1";
        case MODE_123:  return "123";
        default: return null;
        }
    }
    
    protected String getTextEdit() {
        
        StringBuffer s = new StringBuffer();
        s.append("<html>");
        if (m_svalue != null) {
            s.append(m_svalue);
        }
        if (m_cLastChar != '\u0000') {
                s.append("<font color=\"#a0a0a0\">");
                s.append(getKeyChar());
                s.append("</font>");
        }
        s.append("<font color=\"#a0a0a0\">_</font>");

        return s.toString(); 
    }
    
    protected String getTextFormat() throws BasicException {
        return (m_svalue == null)
                ? "<html>"
                : "<html>" + m_svalue;
    }
    
    protected void typeCharInternal(char c) {
        
        String sOldText = getText();
        
        if (c == '\u0008') {
            if (m_cLastChar == '\u0000') {
                // borramos el \u00c3\u00baltimo caracter el si existe
                if (m_svalue != null && m_svalue.length() > 0) {
                    m_svalue = m_svalue.substring(0, m_svalue.length() - 1);
                }
            } else {
                // borramos el caracter pendiente
                m_iTicks = 0;
                m_cLastChar = '\u0000';
            }
        } else if (c == '\u007f') {
            // Los hemos borrado todos.
            m_iMode = getStartMode(); //MODE_Abc1;
            m_svalue = null;
            m_iTicks = 0;
            m_cLastChar = '\u0000';    
        } else if (c >= ' ') { // es un caracter en condiciones
            if (m_cLastChar != '\u0000') {
                char ckey = getKeyChar();
                m_svalue = appendChar2Value(ckey);
                acceptKeyChar(ckey);
            }
            m_iTicks = 0;
            m_cLastChar = '\u0000';
            m_svalue = appendChar2Value(c);
        }
        
        m_jtimer.restart();      
        
        firePropertyChange("Text", sOldText, getText());
    }
    
    protected void transCharInternal(char c) {
        
        String sOldText = getText();
        
        if (c == '-') {
            if (m_cLastChar == '\u0000') {
                // borramos el \u00c3\u00baltimo caracter el si existe
                if (m_svalue != null && m_svalue.length() > 0) {
                    m_svalue = m_svalue.substring(0, m_svalue.length() - 1);
                }
            } else {
                // borramos el caracter pendiente
                m_iTicks = 0;
                m_cLastChar = '\u0000';
            }
        } else if (c == '\u007f') {
            // Los hemos borrado todos.
            m_iMode = getStartMode(); //MODE_Abc1;
            m_svalue = null;
            m_iTicks = 0;
            m_cLastChar = '\u0000';           
        } else if (c == '.') {
            if (m_cLastChar != '\u0000') {
                m_svalue = appendChar2Value(getKeyChar());
            }
            m_iTicks = 0;
            m_cLastChar = '\u0000';           
            m_iMode = (m_iMode + 1) % 4;
        } else if (c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == '0') {
            if (m_iMode == MODE_123) {
                m_svalue = appendChar2Value(c);
            } else if (c == m_cLastChar) {
                m_iTicks ++;
            } else {
                if (m_cLastChar != '\u0000') {
                    char ckey = getKeyChar();
                    m_svalue = appendChar2Value(ckey);
                    acceptKeyChar(ckey);
                }
                m_iTicks = 0;
                m_cLastChar = c;
            }
        }
        
        m_jtimer.restart();  
        
        firePropertyChange("Text", sOldText, getText());
    }

    private void acceptKeyChar(char c) {
        if (m_iMode == MODE_Abc1 && c != ' ') {
            m_iMode = MODE_abc1;
        } else if (m_iMode == MODE_abc1 && c == '.') {
            m_iMode = MODE_Abc1;
        }
    }
    
    protected char getKeyChar() {
        
        char[] clist = null;
        switch (m_iMode) {
        case MODE_abc1:
             switch (m_cLastChar) {
                case '1': clist = CHAR_abc1_1; break; 
                case '2': clist = CHAR_abc1_2; break; 
                case '3': clist = CHAR_abc1_3; break; 
                case '4': clist = CHAR_abc1_4; break; 
                case '5': clist = CHAR_abc1_5; break; 
                case '6': clist = CHAR_abc1_6; break; 
                case '7': clist = CHAR_abc1_7; break; 
                case '8': clist = CHAR_abc1_8; break; 
                case '9': clist = CHAR_abc1_9; break; 
                case '0': clist = CHAR_abc1_0; break; 
            }
            break;
        case MODE_Abc1:
        case MODE_ABC1:                
            switch (m_cLastChar) {
                case '1': clist = CHAR_ABC1_1; break; 
                case '2': clist = CHAR_ABC1_2; break; 
                case '3': clist = CHAR_ABC1_3; break; 
                case '4': clist = CHAR_ABC1_4; break; 
                case '5': clist = CHAR_ABC1_5; break; 
                case '6': clist = CHAR_ABC1_6; break; 
                case '7': clist = CHAR_ABC1_7; break; 
                case '8': clist = CHAR_ABC1_8; break; 
                case '9': clist = CHAR_ABC1_9; break; 
                case '0': clist = CHAR_ABC1_0; break; 
            }
            break;
        }
        
        if (clist == null) {
            return m_cLastChar;
        } else {
            return clist[m_iTicks % clist.length];
        }        
    }
     
    private class TimerAction implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            if (m_cLastChar != '\u0000') {
                // This method does not modify the "Text" property.
                char ckey = getKeyChar();
                m_svalue = appendChar2Value(ckey);
                acceptKeyChar(ckey);
                m_iTicks = 0;
                m_cLastChar = '\u0000';
                m_jtimer.restart();
                reprintText();
            }
        }
    }    
    
    private String appendChar2Value(char c) {
        StringBuffer s = new StringBuffer();
        if (m_svalue != null) {
            s.append(m_svalue);
        }
        s.append(c);
        return s.toString();
    }
}
