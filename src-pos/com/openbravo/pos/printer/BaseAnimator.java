//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2008-2009 Openbravo, S.L.
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
public abstract class BaseAnimator implements DisplayAnimator {
    
    protected String baseLine1;
    protected String baseLine2;
    protected String currentLine1;
    protected String currentLine2; 
    
    public BaseAnimator() {
        baseLine1 = null;
        baseLine2 = null;
    }

    public BaseAnimator(String line1, String line2) {
        baseLine1 = line1;
        baseLine2 = line2;
    }

    public String getLine1() {
        return currentLine1;
    }

    public String getLine2() {
        return currentLine2;
    }
}
