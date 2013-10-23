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

package com.openbravo.pos.printer.ticket;

import java.awt.*;
import java.awt.geom.*;

import com.openbravo.pos.printer.DevicePrinter;

public class MyPrinterState {

    private int m_iSize;

    /** Creates a new instance of PrinterState */
    public MyPrinterState(int iSize) {
        m_iSize = iSize;
    }

    public int getLineMult() {
        return getLineMult(m_iSize);
    }

    public static int getLineMult(int iSize) {
        switch (iSize) {
            case 0:
            case 2:
                return 1;
            case 1:
            case 3:
                return 2;
            default:
                return 1;
        }
    }

    public Font getFont(Font baseFont, int iStyle) {

        Font f;
        AffineTransform a;
        switch (m_iSize) {
            case 0:
                f = baseFont;
                break;
            case 2:
                a = AffineTransform.getScaleInstance(2.0, 1.0);
                a.preConcatenate(baseFont.getTransform());
                f = baseFont.deriveFont(a);
                break;
            case 1:
                a = AffineTransform.getScaleInstance(1.0, 2.0);
                a.preConcatenate(baseFont.getTransform());
                f = baseFont.deriveFont(a);
                break;
            case 3:
                a = AffineTransform.getScaleInstance(2.0, 2.0);
                a.preConcatenate(baseFont.getTransform());
                f = baseFont.deriveFont(a);
                break;
            default:
                f = baseFont;
                break;
        }
        f = f.deriveFont((iStyle & DevicePrinter.STYLE_BOLD) != 0 ? Font.BOLD : baseFont.getStyle());
        // Falta aplicar el subrayado
        return f;
    }
}
