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
package com.nordpos.device.receiptprinter;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3.0
 */
public class DevicePrinterNull implements DevicePrinter {

    private String m_sName;
    private String m_sDescription;

    public DevicePrinterNull() {
        this(null);
    }

    public DevicePrinterNull(String desc) {
        m_sName = "label.ReceiptPrinter.Null";
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
    public javax.swing.JComponent getPrinterComponent() {
        return null;
    }

    @Override
    public void reset() {
    }

    @Override
    public void beginReceipt() {
    }

    @Override
    public void printBarCode(String type, String position, String code) {
    }

    @Override
    public void printImage(java.awt.image.BufferedImage image) {
    }

    @Override
    public void beginLine(Integer iTextSize) {
    }

    @Override
    public void printText(Integer iCharacterSize, String sUnderlineType, Boolean bBold, String sText) {
    }

    @Override
    public void endLine() {
    }

    @Override
    public void endReceipt() {
    }

    @Override
    public void openDrawer() {
    }

    @Override
    public void cutPaper(boolean complete) {
    }

}
