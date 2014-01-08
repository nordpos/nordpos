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
package com.nordpos.device.labelprinter;

import com.openbravo.pos.forms.AppLocal;

public class DeviceLabelPrinterNull implements DeviceLabelPrinter {

    private String m_sName;
    private String m_sDescription;

    public DeviceLabelPrinterNull() {
        this(null);
    }

    public DeviceLabelPrinterNull(String desc) {
        m_sName = AppLocal.getIntString("Printer.Null");
        m_sDescription = desc;
    }

    @Override
    public String getPrinterName() {
        return m_sName;
    }

    @Override
    public String getPrinterDescription() {
        return m_sDescription;
    }

    @Override
    public void reset() {
    }

    @Override
    public void beginLabel(String sCharset, String sLength, String sWidth, String sGap, String sX, String sY, String sRotation, String sResolution) {
    }

    @Override
    public void drawText(String sFontType, String sOrientation, String sX, String sY, String sFontWidth, String sFontHeight, String sFontWeight, String sText) {
    }

    @Override
    public void drawBarCode(String sBarcodeType, String sTextPosition, String sOrientation, String sX, String sY, String sHeight, String sText) {
    }

    @Override
    public void drawRectangle(String sFill, String sXBegin, String sYBegin, String sXEnd, String sYEnd) {
    }

    @Override
    public void drawLine(String sThickness, String sXBegin, String sYBegin, String sXEnd, String sYEnd){
    }

    @Override
    public void drawFrame(String sThickness, String sXBegin, String sYBegin, String sXEnd, String sYEnd){

    }

    @Override
    public void endLabel() {
    }
}
