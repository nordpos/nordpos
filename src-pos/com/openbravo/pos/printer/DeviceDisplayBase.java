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

package com.openbravo.pos.printer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author adrianromero
 */
public class DeviceDisplayBase {
    
    public static final int ANIMATION_NULL = 0;
    public static final int ANIMATION_FLYER = 1;
    public static final int ANIMATION_SCROLL = 2;
    public static final int ANIMATION_BLINK = 3;
    public static final int ANIMATION_CURTAIN = 4;
    
    private DeviceDisplayImpl impl;    
    private DisplayAnimator anim;     
    private javax.swing.Timer m_tTimeTimer;    
    private int counter = 0;
    
    /** Creates a new instance of DeviceDisplayBase */
    public DeviceDisplayBase(DeviceDisplayImpl impl) {
        this.impl = impl; 
        anim = new NullAnimator("", "");
        m_tTimeTimer = new javax.swing.Timer(50, new PrintTimeAction());
    }
    
    public void writeVisor(int animation, String sLine1, String sLine2) {
        
        m_tTimeTimer.stop();
        switch (animation) {
            case ANIMATION_FLYER:
                anim = new FlyerAnimator(sLine1, sLine2);
                break;
            case ANIMATION_SCROLL:
                anim = new ScrollAnimator(sLine1, sLine2);
                break;
            case ANIMATION_BLINK:
                anim = new BlinkAnimator(sLine1, sLine2);
                break;
            case ANIMATION_CURTAIN:
                anim = new CurtainAnimator(sLine1, sLine2);
                break;
            default: // ANIMATION_NULL
                anim = new NullAnimator(sLine1, sLine2);
                break;
        }
        
        counter = 0;
        anim.setTiming(counter);
        impl.repaintLines();
        
        if (animation != ANIMATION_NULL) {
            counter = 0;
            m_tTimeTimer.start();
        }
    }
         
    public void writeVisor(String sLine1, String sLine2) {
        writeVisor(ANIMATION_NULL, sLine1, sLine2);
    }
    
    public void clearVisor() {
        writeVisor(ANIMATION_NULL, "", "");
    }
    
    public String getLine1() {
        return anim.getLine1();
    }
    
    public String getLine2() {
        return anim.getLine2();
    }
    
    private class PrintTimeAction implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            counter ++;
            anim.setTiming(counter);
            impl.repaintLines();
        }        
    }    
}
