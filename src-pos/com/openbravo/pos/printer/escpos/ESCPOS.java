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
        
    public static final byte[] BAR_HEIGHT = {0x1D, 0x68, 0x40};
    public static final byte[] BAR_POSITIONDOWN = {0x1D, 0x48, 0x02};
    public static final byte[] BAR_POSITIONNONE = {0x1D, 0x48, 0x00};
    public static final byte[] BAR_HRIFONT1 = {0x1D, 0x66, 0x01}; 
    
    public static final byte[] BAR_CODE02 = {0x1D, 0x6B, 0x02}; // 12 numeros fijos
    
    public static final byte[] VISOR_HIDE_CURSOR = {0x1F, 0x43, 0x00};
    public static final byte[] VISOR_SHOW_CURSOR = {0x1F, 0x43, 0x01};
    public static final byte[] VISOR_HOME = {0x0B};
    public static final byte[] VISOR_CLEAR = {0x0C};
        
    public static final byte[] CODE_TABLE_00 = {0x1B, 0x74, 0x00};
    public static final byte[] CODE_TABLE_13 = {0x1B, 0x74, 0x13}; 
    
    private ESCPOS() {       
    }
}
