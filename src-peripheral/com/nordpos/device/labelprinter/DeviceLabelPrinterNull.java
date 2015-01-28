/**
 *
 * NORD POS is a fork of Openbravo POS.
 *
 * Copyright (C) 2009-2015 Nord Trading Ltd. <http://www.nordpos.com>
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
package com.nordpos.device.labelprinter;

import com.openbravo.pos.forms.AppLocal;

public class DeviceLabelPrinterNull implements DeviceLabelPrinter {

    private String m_sName;
    private String m_sDescription;

    public DeviceLabelPrinterNull() {
        this(null);
    }

    public DeviceLabelPrinterNull(String desc) {
        m_sName = AppLocal.getIntString("LabelPrinter.Null");
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
    public void drawBarCode(String sBarcodeType, String sTextPosition, String sOrientation, String sX, String sY, String sHeight, String sDimension, String sText) {
    }

    @Override
    public void drawRectangle(String sFill, String sXBegin, String sYBegin, String sXEnd, String sYEnd) {
    }

    @Override
    public void drawLine(String sThickness, String sXBegin, String sYBegin, String sXEnd, String sYEnd) {
    }

    @Override
    public void drawFrame(String sThickness, String sXBegin, String sYBegin, String sXEnd, String sYEnd) {
    }

    @Override
    public void endLabel() {
    }

}
