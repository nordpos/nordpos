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

/**
 *
 * @author adrianromero
 */
public class ScrollAnimator extends BaseAnimator {

    private int msglength;

    public ScrollAnimator(String line1, String line2) {
        msglength = Math.max(line1.length(), line2.length());
        baseLine1 = DeviceTicket.alignLeft(line1, msglength);
        baseLine2 = DeviceTicket.alignLeft(line2, msglength);
    }

    public void setTiming(int i) {
        int j = (i / 2) % (msglength + 20);
        if (j < 20) {
            currentLine1 = DeviceTicket.alignLeft(DeviceTicket.getWhiteString(20 - j) + baseLine1, 20);
            currentLine2 = DeviceTicket.alignLeft(DeviceTicket.getWhiteString(20 - j) + baseLine2, 20);
        } else {
            currentLine1 = DeviceTicket.alignLeft(baseLine1.substring(j - 20), 20);
            currentLine2 = DeviceTicket.alignLeft(baseLine2.substring(j - 20), 20);
        }
    }
}
