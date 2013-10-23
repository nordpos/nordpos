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

import java.awt.*;
import javax.swing.*;
import com.openbravo.data.loader.LocalRes;

public class MessageInf {
        
    // SIGNAL_WORD'S
    public final static int SGN_DANGER = 0xFF000000; // Death or serious injury will occur
    public final static int SGN_WARNING = 0xFE000000; // Death or serious injury may occur
    public final static int SGN_CAUTION = 0xFD000000; // Minor or moderate injury may occur
    public final static int SGN_NOTICE = 0xFC000000; // Damage to property may occur
    public final static int SGN_IMPORTANT = 0xFA000000; // Operating or maintenance instructions or additional information
    public final static int SGN_SUCCESS = 0xFB000000;
    
    // ERROR_CLASS'ES
    public final static int CLS_GENERIC = 0x00000000;
    
    // ERROR_CODE'S
    
    // VARIABLES
    private int m_iMsgNumber; // = SIGNAL_WORD (0xFF000000) | ERROR_CLASS (0x00FF0000) | ERROR_CODE (0x0000FFFF)
    private String m_sHazard;
    private String m_sConsequences;
    private String m_sAvoiding;
    
    // CAUSE
    private Object m_eCause;
    
    /** Creates a new instance of MessageInf */
    public MessageInf(int iSignalWord, String sHazard, Object e) {
        m_iMsgNumber = iSignalWord | CLS_GENERIC;
        m_sHazard = sHazard;
        m_sConsequences = "";
        m_sAvoiding = "";
        m_eCause = e;
    }
    /** Creates a new instance of MessageInf */
    public MessageInf(int iSignalWord, String sHazard) {
        this (iSignalWord, sHazard, null);
    }
    
    /** Creates a new instance of MessageInf */
    public MessageInf(Throwable e) {
        this(SGN_WARNING, e.getLocalizedMessage(), e);
    }
    
    public void show(Component parent) {
        JMessageDialog.showMessage(parent, this);
    }
    
    public Object getCause() {
        return m_eCause;
    }
    
    public int getSignalWord() {
        return m_iMsgNumber & 0xFF000000;
    }
    
    public Icon getSignalWordIcon() {
        int iSignalWord = getSignalWord();
        if (iSignalWord == SGN_DANGER) {
            return UIManager.getIcon("OptionPane.errorIcon");
        } else if (iSignalWord == SGN_WARNING) {
            return UIManager.getIcon("OptionPane.warningIcon");
       } else if (iSignalWord == SGN_CAUTION) {
            return UIManager.getIcon("OptionPane.warningIcon");
        } else if (iSignalWord == SGN_NOTICE) {
            return UIManager.getIcon("OptionPane.informationIcon");
        } else if (iSignalWord == SGN_IMPORTANT) {
            return UIManager.getIcon("OptionPane.informationIcon");
        } else if (iSignalWord == SGN_SUCCESS) {
            return UIManager.getIcon("OptionPane.informationIcon");
        } else {
            return UIManager.getIcon("OptionPane.questionIcon");
        }
    }
    
    public String getErrorCodeMsg() {
        
        StringBuffer sb = new StringBuffer();       
        int iSignalWord = getSignalWord();
        if (iSignalWord == SGN_DANGER) {
            sb.append("DNG_");
        } else if (iSignalWord == SGN_WARNING) {
            sb.append("WRN_");
        } else if (iSignalWord == SGN_CAUTION) {
            sb.append("CAU_");
        } else if (iSignalWord == SGN_NOTICE) {
            sb.append("NOT_");
        } else if (iSignalWord == SGN_IMPORTANT) {
            sb.append("IMP_");
        } else if (iSignalWord == SGN_SUCCESS) {
            sb.append("INF_");
        } else {
            sb.append("UNK_");
        }
        sb.append(toHex((m_iMsgNumber & 0x00FF0000) >> 16, 2));
        sb.append('_');
        sb.append(toHex(m_iMsgNumber & 0x0000FFFF, 4));
        return sb.toString();
    }
    
    private String toHex(int i, int iChars) {
        String s = Integer.toHexString(i);
        return s.length() >= iChars ? s : fillString(iChars - s.length()) + s;
    }
    
    private String fillString(int iChars) {
        char[] aStr = new char[iChars];
        for (int i = 0; i < aStr.length; i++) {
            aStr[i] = '0';
        }
        return new String(aStr);
    }

    
    public String getMessageMsg() {
        
        StringBuffer sb = new StringBuffer();     
        int iSignalWord = getSignalWord();
        if (iSignalWord == SGN_DANGER) {
            sb.append(LocalRes.getIntString("sgn.danger"));
        } else if (iSignalWord == SGN_WARNING) {
            sb.append(LocalRes.getIntString("sgn.warning"));
        } else if (iSignalWord == SGN_CAUTION) {
            sb.append(LocalRes.getIntString("sgn.caution"));
        } else if (iSignalWord == SGN_NOTICE) {
            sb.append(LocalRes.getIntString("sgn.notice"));
        } else if (iSignalWord == SGN_IMPORTANT) {
            sb.append(LocalRes.getIntString("sgn.important"));
        } else if (iSignalWord == SGN_SUCCESS) {
            sb.append(LocalRes.getIntString("sgn.success"));
        } else {
            sb.append(LocalRes.getIntString("sgn.unknown"));
        }
        sb.append(m_sHazard);
        sb.append(m_sConsequences);
        sb.append(m_sAvoiding);
        return sb.toString();
    }    
}
