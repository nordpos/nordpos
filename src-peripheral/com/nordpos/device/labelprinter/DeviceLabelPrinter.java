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

/**
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3
 */
public interface DeviceLabelPrinter {

    public String getPrinterName();

    public String getPrinterDescription();

    public void reset();

    public void beginLabel(String sCharset, String sLength, String sWidth, String sGap, String sX, String sY, String sRotation, String sResolution);

    public void drawText(String sFontType, String sOrientation, String sX, String sY, String sFontWidth, String sFontHeight, String sFontWeight, String sText);

    public void drawBarCode(String sBarcodeType, String sTextPosition, String sOrientation, String sX, String sY, String sHeight, String sDimension, String sText);

    public void drawRectangle(String sFill, String sXBegin, String sYBegin, String sXEnd, String sYEnd);

    public void drawLine(String sThickness, String sXBegin, String sYBegin, String sXEnd, String sYEnd);

    public void drawFrame(String sThickness, String sXBegin, String sYBegin, String sXEnd, String sYEnd);

    public void endLabel();
}
