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

/**
 *
 * @author adrianromero
 * @author Andrey Svininykh <svininykh@gmail.com>
 */

public abstract class JKeyboardText extends JEditorAbstract {
    
    protected String m_svalue;
    
    public static final int MODE_Abc1 = 0;
    public static final int MODE_abc1 = 1;
    public static final int MODE_ABC1 = 2;
    public static final int MODE_123 = 3;    
    public int m_iMode;
    
//    protected int m_iTicks;
    protected char m_cLastChar;
    protected long m_lcount;
    
    private Timer m_jtimer;
    
    private static final char CHAR_abc1_1 = '1';
    private static final char CHAR_abc1_2 = '2';
    private static final char CHAR_abc1_3 = '3';
    private static final char CHAR_abc1_4 = '4';
    private static final char CHAR_abc1_5 = '5';
    private static final char CHAR_abc1_6 = '6';
    private static final char CHAR_abc1_7 = '7';
    private static final char CHAR_abc1_8 = '8';
    private static final char CHAR_abc1_9 = '9';
    private static final char CHAR_abc1_0 = '0';
    private static final char CHAR_abc1_Q = 'q';
    private static final char CHAR_abc1_W = 'w';
    private static final char CHAR_abc1_E = 'e';
    private static final char CHAR_abc1_R = 'r';
    private static final char CHAR_abc1_T = 't';
    private static final char CHAR_abc1_Y = 'y';
    private static final char CHAR_abc1_U = 'u';
    private static final char CHAR_abc1_I = 'i';
    private static final char CHAR_abc1_O = 'o';
    private static final char CHAR_abc1_P = 'p';
    private static final char CHAR_abc1_A = 'a';
    private static final char CHAR_abc1_S = 's';
    private static final char CHAR_abc1_D = 'd';
    private static final char CHAR_abc1_F = 'f';
    private static final char CHAR_abc1_G = 'g';
    private static final char CHAR_abc1_H = 'h';
    private static final char CHAR_abc1_J = 'j';
    private static final char CHAR_abc1_K = 'k';
    private static final char CHAR_abc1_L = 'l';
    private static final char CHAR_abc1_Z = 'z';
    private static final char CHAR_abc1_X = 'x';
    private static final char CHAR_abc1_C = 'c';
    private static final char CHAR_abc1_V = 'v';
    private static final char CHAR_abc1_B = 'b';
    private static final char CHAR_abc1_N = 'n';
    private static final char CHAR_abc1_M = 'm';
    private static final char CHAR_abc1_Comma = ',';
    private static final char CHAR_abc1_Space = ' ';
    private static final char CHAR_abc1_Dot = '.';
    private static final char CHAR_abc1_LSquareBrack= '[';
    private static final char CHAR_abc1_RSquareBrack = ']';
    private static final char CHAR_abc1_Colon = ':';
    private static final char CHAR_abc1_Quotation = '"';
    private static final char CHAR_abc1_Slash = '/';
    private static final char CHAR_abc1_Ampersand = '&';
    private static final char CHAR_abc1_At = '@';
    
    //private static final char CHAR_abc1_Backspace = '\u0008';
    private static final char CHAR_abc1_Minus = '-';
    
    private static final char CHAR_ABC1_1 = '1';
    private static final char CHAR_ABC1_2 = '2';
    private static final char CHAR_ABC1_3 = '3';
    private static final char CHAR_ABC1_4 = '4';
    private static final char CHAR_ABC1_5 = '5';
    private static final char CHAR_ABC1_6 = '6';
    private static final char CHAR_ABC1_7 = '7';
    private static final char CHAR_ABC1_8 = '8';
    private static final char CHAR_ABC1_9 = '9';
    private static final char CHAR_ABC1_0 = '0';
    private static final char CHAR_ABC1_Q = 'Q';
    private static final char CHAR_ABC1_W = 'W';
    private static final char CHAR_ABC1_E = 'E';
    private static final char CHAR_ABC1_R = 'R';
    private static final char CHAR_ABC1_T = 'T';
    private static final char CHAR_ABC1_Y = 'Y';
    private static final char CHAR_ABC1_U = 'U';
    private static final char CHAR_ABC1_I = 'I';
    private static final char CHAR_ABC1_O = 'O';
    private static final char CHAR_ABC1_P = 'P';
    private static final char CHAR_ABC1_A = 'A';
    private static final char CHAR_ABC1_S = 'S';
    private static final char CHAR_ABC1_D = 'D';
    private static final char CHAR_ABC1_F = 'F';
    private static final char CHAR_ABC1_G = 'G';
    private static final char CHAR_ABC1_H = 'H';
    private static final char CHAR_ABC1_J = 'J';
    private static final char CHAR_ABC1_K = 'K';
    private static final char CHAR_ABC1_L = 'L';
    private static final char CHAR_ABC1_Z = 'Z';
    private static final char CHAR_ABC1_X = 'X';
    private static final char CHAR_ABC1_C = 'C';
    private static final char CHAR_ABC1_V = 'V';
    private static final char CHAR_ABC1_B = 'B';
    private static final char CHAR_ABC1_N = 'N';
    private static final char CHAR_ABC1_M = 'M';
    private static final char CHAR_ABC1_Comma = ',';
    private static final char CHAR_ABC1_Space = ' ';
    private static final char CHAR_ABC1_Dot = '.';
    private static final char CHAR_ABC1_LSquareBrack= '[';
    private static final char CHAR_ABC1_RSquareBrack = ']';
    private static final char CHAR_ABC1_Colon = ':';
    private static final char CHAR_ABC1_Quotation = '"';
    private static final char CHAR_ABC1_Slash = '/';
    private static final char CHAR_ABC1_Ampersand = '&';
    private static final char CHAR_ABC1_At = '@';
    
        
//    private static final char CHAR_ABC1_Backspace = '\u0008';
    private static final char CHAR_ABC1_Minus = '-';
    
    /** Creates a new instance of JEditorString */
    public JKeyboardText() {
        m_svalue = null;
        
//        m_iTicks = 0;
        m_cLastChar = '\u0000';
        m_jtimer = new javax.swing.Timer(100, new TimerAction());
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
//        m_iTicks = 0;
        m_cLastChar = '\u0000';  
        
        reprintText();
        
        firePropertyChange("Text", sOldText, getText());
    } 
    
    public final void setText(String sText) {
        
        String sOldText = getText();

        m_svalue = sText;
 //       m_iTicks = 0;
        m_cLastChar = '\u0000';  
        
        reprintText();
        
        firePropertyChange("Text", sOldText, getText());
    }
    
    public final void setEditModeEnum(int iMode) {
        
        m_iMode = iMode;
//        m_iTicks = 0;
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
//                m_iTicks = 0;
                m_cLastChar = '\u0000';
            }
        } else if (c == '\u007f') {
            // Los hemos borrado todos.
            m_iMode = getStartMode(); //MODE_Abc1;
            m_svalue = null;
//            m_iTicks = 0;
            m_cLastChar = '\u0000';    
        } else if (c >= ' ') { // es un caracter en condiciones
            if (m_cLastChar != '\u0000') {
                char ckey = getKeyChar();
                m_svalue = appendChar2Value(ckey);
                acceptKeyChar(ckey);
            }
//            m_iTicks = 0;
            m_cLastChar = '\u0000';
            m_svalue = appendChar2Value(c);
        }
        
        m_jtimer.restart();      
        
        firePropertyChange("Text", sOldText, getText());
    }
    
    protected void transCharInternal(char c) {
        
        String sOldText = getText();
        
        if (c == '\u0008') {
            if (m_cLastChar == '\u0000') {
                // borramos el \u00c3\u00baltimo caracter el si existe
                if (m_svalue != null && m_svalue.length() > 0) {
                    m_svalue = m_svalue.substring(0, m_svalue.length() - 1);
                }
            } else {
                // borramos el caracter pendiente
//                m_iTicks = 0;
                m_cLastChar = '\u0000';
            }
        } else if (c == '\u007f') {
            // Los hemos borrado todos.
            m_iMode = getStartMode(); //MODE_Abc1;
            m_svalue = null;
//            m_iTicks = 0;
            m_cLastChar = '\u0000';           
        } else if (c == '\u0010') {
            if (m_cLastChar != '\u0000') {
                m_svalue = appendChar2Value(getKeyChar());
            }
//            m_iTicks = 0;
            m_cLastChar = '\u0000';           
            m_iMode = (m_iMode + 1) % 4;
        } else if (c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == '0' 
                || c == 'Q' || c == 'W' || c == 'E' || c == 'R' || c == 'T' || c == 'Y' || c == 'U' || c == 'I' || c == 'O' || c == 'P' 
                || c == 'A' || c == 'S' || c == 'D' || c == 'F' || c == 'G' || c == 'H' || c == 'J' || c == 'K' || c == 'L'
                || c == 'Z' || c == 'X' || c == 'C' || c == 'V' || c == 'B' || c == 'N' || c == 'M' 
                || c == ',' || c == ' ' || c == '.' || c == '-' || c == '@' || c == '&' || c == '[' || c == ']' || c == ':' || c == '"' || c == '/') {
            if (m_iMode == MODE_123) {
                m_svalue = appendChar2Value(c);
            } else if (c == m_cLastChar) {
//                m_iTicks ++;
            } else {
                if (m_cLastChar != '\u0000') {
                    char ckey = getKeyChar();
                    m_svalue = appendChar2Value(ckey);
                    acceptKeyChar(ckey);
                }
//                m_iTicks = 0;
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
        
        char c = 0;
        switch (m_iMode) {
        case MODE_abc1:
             switch (m_cLastChar) {
                case '1': c = CHAR_abc1_1; break; 
                case '2': c = CHAR_abc1_2; break; 
                case '3': c = CHAR_abc1_3; break; 
                case '4': c = CHAR_abc1_4; break; 
                case '5': c = CHAR_abc1_5; break; 
                case '6': c = CHAR_abc1_6; break; 
                case '7': c = CHAR_abc1_7; break; 
                case '8': c = CHAR_abc1_8; break; 
                case '9': c = CHAR_abc1_9; break; 
                case '0': c = CHAR_abc1_0; break;
                case 'Q': c = CHAR_abc1_Q; break;
                case 'W': c = CHAR_abc1_W; break;
                case 'E': c = CHAR_abc1_E; break;
                case 'R': c = CHAR_abc1_R; break;
                case 'T': c = CHAR_abc1_T; break;
                case 'Y': c = CHAR_abc1_Y; break;
                case 'U': c = CHAR_abc1_U; break;
                case 'I': c = CHAR_abc1_I; break;
                case 'O': c = CHAR_abc1_O; break;
                case 'P': c = CHAR_abc1_P; break;    
                case 'A': c = CHAR_abc1_A; break;
                case 'S': c = CHAR_abc1_S; break;
                case 'D': c = CHAR_abc1_D; break;
                case 'F': c = CHAR_abc1_F; break;
                case 'G': c = CHAR_abc1_G; break;
                case 'H': c = CHAR_abc1_H; break;
                case 'J': c = CHAR_abc1_J; break;
                case 'K': c = CHAR_abc1_K; break;
                case 'L': c = CHAR_abc1_L; break;
                case 'Z': c = CHAR_abc1_Z; break;
                case 'X': c = CHAR_abc1_X; break;
                case 'C': c = CHAR_abc1_C; break;
                case 'V': c = CHAR_abc1_V; break;
                case 'B': c = CHAR_abc1_B; break;
                case 'N': c = CHAR_abc1_N; break;
                case 'M': c = CHAR_abc1_M; break;
                case ',': c = CHAR_abc1_Comma; break;
                case ' ': c = CHAR_abc1_Space; break;
                case '.': c = CHAR_abc1_Dot; break;
                case '-': c = CHAR_abc1_Minus; break;
              }
            break;
        case MODE_Abc1:
        case MODE_ABC1:                
            switch (m_cLastChar) {
                case '1': c = CHAR_ABC1_1; break; 
                case '2': c = CHAR_ABC1_2; break; 
                case '3': c = CHAR_ABC1_3; break; 
                case '4': c = CHAR_ABC1_4; break; 
                case '5': c = CHAR_ABC1_5; break; 
                case '6': c = CHAR_ABC1_6; break; 
                case '7': c = CHAR_ABC1_7; break; 
                case '8': c = CHAR_ABC1_8; break; 
                case '9': c = CHAR_ABC1_9; break; 
                case '0': c = CHAR_ABC1_0; break;
                case 'Q': c = CHAR_ABC1_Q; break;
                case 'W': c = CHAR_ABC1_W; break;
                case 'E': c = CHAR_ABC1_E; break;
                case 'R': c = CHAR_ABC1_R; break;
                case 'T': c = CHAR_ABC1_T; break;
                case 'Y': c = CHAR_ABC1_Y; break;
                case 'U': c = CHAR_ABC1_U; break;
                case 'I': c = CHAR_ABC1_I; break;
                case 'O': c = CHAR_ABC1_O; break;
                case 'P': c = CHAR_ABC1_P; break;    
                case 'A': c = CHAR_ABC1_A; break;
                case 'S': c = CHAR_ABC1_S; break;
                case 'D': c = CHAR_ABC1_D; break;
                case 'F': c = CHAR_ABC1_F; break;
                case 'G': c = CHAR_ABC1_G; break;
                case 'H': c = CHAR_ABC1_H; break;
                case 'J': c = CHAR_ABC1_J; break;
                case 'K': c = CHAR_ABC1_K; break;
                case 'L': c = CHAR_ABC1_L; break;
                case 'Z': c = CHAR_ABC1_Z; break;
                case 'X': c = CHAR_ABC1_X; break;
                case 'C': c = CHAR_ABC1_C; break;
                case 'V': c = CHAR_ABC1_V; break;
                case 'B': c = CHAR_ABC1_B; break;
                case 'N': c = CHAR_ABC1_N; break;
                case 'M': c = CHAR_ABC1_M; break;
                case ',': c = CHAR_ABC1_Comma; break;
                case ' ': c = CHAR_ABC1_Space; break;
                case '.': c = CHAR_ABC1_Dot; break;
                case '-': c = CHAR_ABC1_Minus; break;
            }
            break;
        }
        
        if (c == 0) {
            return m_cLastChar;
        } else {
            return c;
        }        
    }
     
    private class TimerAction implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            if (m_cLastChar != '\u0000') {
                // This method does not modify the "Text" property.
                char ckey = getKeyChar();
                m_svalue = appendChar2Value(ckey);
                acceptKeyChar(ckey);
//                m_iTicks = 0;
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
