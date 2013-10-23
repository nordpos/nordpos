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

package com.openbravo.pos.payment;

import java.awt.event.KeyEvent;

public class MagCardReaderAlternative implements MagCardReader {

    private MagCardParser magcardparser;
    private StringBuffer asciiCode = null;

    /** Creates a new instance of GenericMagCardReader */
    public MagCardReaderAlternative() {
        magcardparser = new MagCardParserGeneric();
    }

    @Override
    public String getReaderName() {
        return "Alternative magnetic card reader";
    }
    @Override
    public void keyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ALT) {
            asciiCode = new StringBuffer();
        }
    }
    @Override
    public void keyReleased(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ALT) {
            if (asciiCode != null) {
                try {
                    magcardparser.append((char) Integer.parseInt(asciiCode.toString()));
                } catch (NumberFormatException e) {
                    // ignore the entry
                }
            }
            asciiCode = null;
        }
    }
    @Override
    public void keyTyped(java.awt.event.KeyEvent evt) {
        if (asciiCode != null && Character.isDigit(evt.getKeyChar())) {
            asciiCode.append(evt.getKeyChar());
        } else if (evt.getKeyChar() == '\n') {
            magcardparser.append(evt.getKeyChar());
        }
    }
    @Override
    public MagCardParser getMagCard() {
        return magcardparser;
    }
}