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

public class CodesIthaca extends Codes {

    private static final byte[] INITSEQUENCE = {};

    private static final byte[] CHAR_SIZE_0 = {0x1D, 0x21, 0x00};
    private static final byte[] CHAR_SIZE_1 = {0x1D, 0x21, 0x01};
    private static final byte[] CHAR_SIZE_2 = {0x1D, 0x21, 0x30};
    private static final byte[] CHAR_SIZE_3 = {0x1D, 0x21, 0x31};

    public static final byte[] BOLD_SET = {0x1B, 0x45, 0x01};
    public static final byte[] BOLD_RESET = {0x1B, 0x45, 0x00};
    public static final byte[] UNDERLINE_SET = {0x1B, 0x2D, 0x01};
    public static final byte[] UNDERLINE_RESET = {0x1B, 0x2D, 0x00};
    
    private static final byte[] OPEN_DRAWER = {0x1B, 0x78, 0x01};    
    private static final byte[] PARTIAL_CUT = {0x1B, 0x50, 0x00};
    private static final byte[] IMAGE_HEADER = {0x1D, 0x76, 0x30, 0x03};
    private static final byte[] NEW_LINE = {0x0D, 0x0A}; // Print and carriage return
    
    /** Creates a new instance of CodesIthaca */
    public CodesIthaca() {
    }

    public byte[] getInitSequence() { return INITSEQUENCE; }
     
    public byte[] getSize0() { return CHAR_SIZE_0; }
    public byte[] getSize1() { return CHAR_SIZE_1; }
    public byte[] getSize2() { return CHAR_SIZE_2; }
    public byte[] getSize3() { return CHAR_SIZE_3; }

    public byte[] getBoldSet() { return BOLD_SET; }
    public byte[] getBoldReset() { return BOLD_RESET; }
    public byte[] getUnderlineSet() { return UNDERLINE_SET; }
    public byte[] getUnderlineReset() { return UNDERLINE_RESET; }
    
    public byte[] getOpenDrawer() { return OPEN_DRAWER; }    
    public byte[] getCutReceipt() { return PARTIAL_CUT; }       
    public byte[] getNewLine() { return NEW_LINE; } 
    public byte[] getImageHeader() { return IMAGE_HEADER; } 
    public int getImageWidth() { return 256; }
}
