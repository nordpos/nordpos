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

import com.nordpos.device.ticket.TicketPrinterException;
import com.nordpos.device.writter.Writter;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3.0
 */
public class DevicePrinterPlainText implements DevicePrinter {

    private final byte[] bEndOfLine;

    private final Writter out;

    public DevicePrinterPlainText(Writter CommOutputPrinter, byte[] bEndOfLine) throws TicketPrinterException {
        out = CommOutputPrinter;
        this.bEndOfLine = bEndOfLine;
    }

    @Override
    public String getPrinterName() {
        return "label.ReceiptPrinterPlainText";
    }

    @Override
    public String getPrinterDescription() {
        return null;
    }

    @Override
    public void reset() {
    }

    @Override
    public void beginReceipt() {
    }

    @Override
    public void beginLine(Integer iTextSize) {
    }

    @Override
    public void printText(Integer iCharacterSize, String sUnderlineType, Boolean bBold, String sText) {
        out.write(sText);
    }

    @Override
    public void endLine() {
        out.write(bEndOfLine);
    }

    @Override
    public void endReceipt() {
        out.write(bEndOfLine);
        out.write(bEndOfLine);
        out.write(bEndOfLine);
        out.write(bEndOfLine);
        out.write(bEndOfLine);
        out.flush();
    }

    @Override
    public JComponent getPrinterComponent() {
        return null;
    }

    @Override
    public void printImage(BufferedImage image) {
    }

    @Override
    public void printBarCode(String type, String position, String code) {
    }

    @Override
    public void cutPaper(boolean complete) {
    }

    @Override
    public void openDrawer() {
    }

}
