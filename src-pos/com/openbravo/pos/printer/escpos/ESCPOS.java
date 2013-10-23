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

public class ESCPOS {

    public static final byte[] INIT = {0x1B, 0x40};
        
    public static final byte[] SELECT_PRINTER = {0x1B, 0x3D, 0x01};
    public static final byte[] SELECT_DISPLAY = {0x1B, 0x3D, 0x02};    

    public static final byte[] HT = {0x09}; // Horizontal Tab
//    public static final byte[] LF = {0x0A}; // Print and line feed
    public static final byte[] FF = {0x0C}; // 
//    public static final byte[] CR = {0x0D}; // Print and carriage return
        
    public static final byte[] CHAR_FONT_0 = {0x1B, 0x4D, 0x00};
    public static final byte[] CHAR_FONT_1 = {0x1B, 0x4D, 0x01};
    public static final byte[] CHAR_FONT_2 = {0x1B, 0x4D, 0x30};
    public static final byte[] CHAR_FONT_3 = {0x1B, 0x4D, 0x31};
        
    public static final byte[] BAR_HEIGHT = {0x1D, 0x68, (byte) 0x60};
    public static final byte[] BAR_POSITIONDOWN = {0x1D, 0x48, 0x02};
    public static final byte[] BAR_POSITIONUP = {0x1D, 0x48, 0x01};
    public static final byte[] BAR_POSITIONNONE = {0x1D, 0x48, 0x00};
    public static final byte[] BAR_HRIFONT1 = {0x1D, 0x66, 0x01}; 

    public static final byte[] BAR_EAN13 = {0x1D, 0x6B, 0x02}; // 12 numbers
    public static final byte[] BAR_EAN8 = {0x1D, 0x6B, 0x03}; // 7 numbers
    public static final byte[] BAR_CODE39 = {0x1D, 0x6B, 0x04}; // numbers and upper latin symbols
    public static final byte[] BAR_CODE128 = {0x1D, 0x6B, 0x49}; // 128 numbers and latin symbols

    public static final byte[] BAR_CODE_CODE128_A = {0x7B, 0x41}; // SET A
    public static final byte[] BAR_CODE_CODE128_B = {0x7B, 0x42}; // SET B
    public static final byte[] BAR_CODE_CODE128_C = {0x7B, 0x43}; // SET C   

    public static final byte[] VISOR_HIDE_CURSOR = {0x1F, 0x43, 0x00};
    public static final byte[] VISOR_SHOW_CURSOR = {0x1F, 0x43, 0x01};
    public static final byte[] VISOR_HOME = {0x0B};
    public static final byte[] VISOR_CLEAR = {0x0C};

    public static final byte[] CANCEL_USER_CHAR = {0x1B, 0x25, 0x00};
    public static final byte[] SELECT_USER_CHAR = {0x1B, 0x25, 0x01}; 
        
    public static final byte[] CODE_TABLE_00 = {0x1B, 0x74, 0x00};
    public static final byte[] CODE_TABLE_13 = {0x1B, 0x74, 0x13}; 
    
    public static final byte[] CODE_TABLE_7 = {0x1B, 0x74, 0x07};
    public static final byte[] CODE_TABLE_17 = {0x1B, 0x74, 0x17};

    public static final byte[] CODE_TABLE_RUS = {0x1B, 0x63, 0x52};

    public static final byte[] CODE_TABLE_KANJI_RUS = {0x1C, 0x2E, 0x1B, 0x74, 0x11};

//    public static final byte[] CODE_TABLE_CHINA_RUS = {0x1C, 0x2E, 0x1B, 0x74, 0x05};

    private ESCPOS() {       
    }
}
