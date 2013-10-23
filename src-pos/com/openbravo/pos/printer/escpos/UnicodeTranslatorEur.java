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

public class UnicodeTranslatorEur extends UnicodeTranslator {
    
    /** Creates a new instance of UnicodeTranslatorEur */
    public UnicodeTranslatorEur() {
    }
    
    public byte[] getCodeTable() {
        return ESCPOS.CODE_TABLE_13;            
    }
    
    public byte transChar(char sChar) {
        if ((sChar >= 0x0000) && (sChar < 0x0080)) {
            return (byte) sChar;
        } else {
            switch (sChar) {
                case '\u00e1': return -0x60; // a acute
                case '\u00e9': return -0x7E; // e acute
                case '\u00ed': return -0x5F; // i acute
                case '\u00f3': return -0x5E; // o acute
                case '\u00fa': return -0x5D; // u acute
                case '\u00fc': return -0x7F; // u dieresis
                case '\u00f1': return -0x5C; // n tilde
                case '\u00d1': return -0x5B; // N tilde 
                
                case '\u00c1': return 0x41; // A acute
                case '\u00c9': return 0x45; // E acute
                case '\u00cd': return 0x49; // I acute
                case '\u00d3': return 0x4F; // O acute
                case '\u00da': return 0x55; // U acute
                case '\u00dc': return -0x66; // U dieresis
                case '\u00bf': return -0x58; // abrir interrogacion
                case '\u00a1': return -0x53; // abrir admiracion
                case '\u20ac': return -0x12; // Euro Sign

        
                default: return 0x3F; // ? Not valid character.
            }          
        }
    }      
}
