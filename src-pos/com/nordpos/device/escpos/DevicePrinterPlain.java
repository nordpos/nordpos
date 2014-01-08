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
package com.nordpos.device.escpos;

import com.nordpos.device.writter.Writter;
import com.nordpos.device.receiptprinter.DevicePrinter;
import com.nordpos.device.ticket.TicketPrinterException;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

public class DevicePrinterPlain implements DevicePrinter {

    private static final byte[] NEW_LINE = {0x0D, 0x0A}; // Print and carriage return

    private final Writter out;
    private final UnicodeTranslator trans;

    // Creates new TicketPrinter
    public DevicePrinterPlain(Writter CommOutputPrinter) throws TicketPrinterException {

        out = CommOutputPrinter;
        trans = new UnicodeTranslatorStar(); // The star translator stands for the 437 int char page
    }

    @Override
    public String getPrinterName() {
        return "Plain";
    }

    @Override
    public String getPrinterDescription() {
        return null;
    }

    @Override
    public JComponent getPrinterComponent() {
        return null;
    }

    @Override
    public void reset() {
    }

    @Override
    public void beginReceipt() {
    }

    @Override
    public void printImage(BufferedImage image) {
    }

    @Override
    public void printBarCode(String type, String position, String code) {
        if (!DevicePrinter.POSITION_NONE.equals(position)) {
            out.write(code);
            out.write(NEW_LINE);
        }
    }

    @Override
    public void beginLine(int iTextSize) {
    }

    @Override
    public void printText(int iStyle, String sText) {
        out.write(trans.transString(sText));
    }

    @Override
    public void endLine() {
        out.write(NEW_LINE);
    }

    @Override
    public void endReceipt() {
        out.write(NEW_LINE);
        out.write(NEW_LINE);
        out.write(NEW_LINE);
        out.write(NEW_LINE);
        out.write(NEW_LINE);
        out.flush();
    }

    @Override
    public void openDrawer() {
    }

    @Override
    public void cutPaper(boolean complete) {
    }
}
