/**
 *
 * NORD POS is a fork of Openbravo POS.
 *
 * Copyright (C) 2009-2013 Nord Trading Ltd. <http://www.nordpos.com>
 *
 * This file is part of NORD POS.
 *
 * NORD POS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * NORD POS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * NORD POS. If not, see <http://www.gnu.org/licenses/>.
 */
package com.nordpos.device.traslator;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3.0
 */
public class UnicodeTranslatorInt extends UnicodeTranslator {

    @Override
    public final byte[] convertString(String sConvert) {
        byte bAux[] = new byte[sConvert.length()];
        for (int i = 0; i < sConvert.length(); i++) {
            bAux[i] = transChar(sConvert.charAt(i));
        }
        return bAux;
    }

    private byte transChar(char sChar) {
        if ((sChar >= 0x0000) && (sChar < 0x0080)) {
            return (byte) sChar;
        } else {
            switch (sChar) {
                case '\u0410': return (byte) 0x80;// A
                case '\u0430': return (byte) 0xA0;// a
                case '\u00c7': return -0x80;
                case '\u00fc': return -0x7F; // u dieresis
                case '\u00e2': return -0x7D;
                case '\u00e4': return -0x7C;
                case '\u00e0': return -0x7B;
                case '\u00E5': return -0x7A; // ao
                case '\u00e7': return -0x79;
                case '\u00ea': return -0x78;
                case '\u00eb': return -0x77;
                case '\u00e8': return -0x76;
                case '\u00ef': return -0x75;
                case '\u00ee': return -0x74;
                case '\u00ec': return -0x73;
                case '\u00c4': return -0x72;
                case '\u00C5': return -0x71; // Ao

                case '\u00c9': return -0x70; // E acute
                case '\u00E6': return -0x6F; // ae
                case '\u00C6': return -0x6E; // AE
                case '\u00f4': return -0x6D;
                case '\u00f6': return -0x6C;
                case '\u00f2': return -0x6B;
                case '\u00fb': return -0x6A;
                case '\u00f9': return -0x69;
                case '\u00ff': return -0x68;
                case '\u00d6': return -0x67;
                case '\u00dc': return -0x66; // U dieresis
                case '\u00F8': return -0x65; // o/
                case '\u00A3': return -0x64; // L- Libra esterlina
                case '\u00D8': return -0x63; // O/
                case '\u00D7': return -0x62; // X

                case '\u00e1': return -0x60; // a acute
                case '\u00e9': return -0x7E; // e acute
                case '\u00ed': return -0x5F; // i acute
                case '\u00f3': return -0x5E; // o acute
                case '\u00fa': return -0x5D; // u acute
                case '\u00f1': return -0x5C; // n tilde
                case '\u00d1': return -0x5B; // N tilde
                case '\u00aa': return -0x5A;
                case '\u00ba': return -0x59;
                case '\u00bf': return -0x58; // abrir interrogacion
                case '\u00AE': return -0x57; // (R) Registrado
                case '\u00ac': return -0x56; // Not sign
                case '\u00BD': return -0x55; // 1/2
                case '\u00BC': return -0x54; // 1/4
                case '\u00a1': return -0x53; // abrir admiracion
                case '\u00AB': return -0x52; // <<
                case '\u00BB': return -0x51; // >>

                case '\u2591': return -0x50; // Light shade
                case '\u2592': return -0x4F; // Medium shade
                case '\u2593': return -0x4E; // Dark shade
                case '\u2502': return -0x4D; // BOX DRAWINGS LIGHT VERTICAL
                case '\u2524': return -0x4C; // BOX DRAWINGS LIGHT VERTICAL AND LEFT
                case '\u00c1': return -0x4B; // A acute
                case '\u00c2': return -0x4A;
                case '\u00c0': return -0x49;
                case '\u00A9': return -0x48; // (c) Copyright
                case '\u2563': return -0x47; // BOX DRAWINGS DOUBLE VERTICAL AND LEFT
                case '\u2551': return -0x46; // BOX DRAWINGS DOUBLE VERTICAL
                case '\u2557': return -0x45; // BOX DRAWINGS DOUBLE DOWN AND LEFT
                case '\u255D': return -0x44; // BOX DRAWINGS DOUBLE UP AND LEFT
                case '\u00A2': return -0x43; // Cent Sign
                case '\u00A5': return -0x42; // Yen Sign
                case '\u2510': return -0x41; // BOX DRAWINGS LIGHT DOWN AND LEFT

                case '\u2514': return -0x40; // BOX DRAWINGS LIGHT UP ANDRIGHT

                case '\u00ca': return -0x2E; //
                case '\u00cb': return -0x2D; //
                case '\u00c8': return -0x2C; //
                case '\u20ac': return -0x2B; // Euro Sign
                case '\u00cd': return -0x2A; // I acute
                case '\u00ce': return -0x29; //
                case '\u00cf': return -0x28; //

                case '|': return -0x23; //
                case '\u00cc': return -0x22; //

                case '\u00d3': return -0x20; // O acute
                case '\u00d4': return -0x1E; //
                case '\u00d2': return -0x1D; //
                case '\u00da': return -0x17; // U acute
                case '\u00db': return -0x16; //
                case '\u00d9': return -0x15; //
                case '\u00fd': return -0x14; //
                case '\u00dd': return -0x13; //
                case '\u00b4': return -0x11; //

                case '\u00a8': return -0x07; //

                default: return 0x3F; // ? Not valid character.
            }
        }
    }
}
