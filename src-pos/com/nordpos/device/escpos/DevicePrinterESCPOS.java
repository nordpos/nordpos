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

import com.nordpos.device.ticket.TicketPrinterException;
import com.nordpos.device.receiptprinter.DevicePrinter;
import com.nordpos.device.writter.Writter;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import javax.swing.JComponent;

import com.openbravo.pos.forms.AppLocal;

public class DevicePrinterESCPOS implements DevicePrinter {

    private final Writter m_CommOutputPrinter;
    private final Procedures m_codes;
    private final UnicodeTranslator m_trans;
    private final String m_sName;

    public DevicePrinterESCPOS(Writter CommOutputPrinter, Procedures codes, UnicodeTranslator trans) throws TicketPrinterException {

        m_sName = AppLocal.getIntString("Printer.Serial");
        m_CommOutputPrinter = CommOutputPrinter;
        m_codes = codes;
        m_trans = trans;

        m_CommOutputPrinter.init(Commands.INIT);
        m_CommOutputPrinter.write(Commands.SELECT_PRINTER);
        m_CommOutputPrinter.init(m_codes.getInitSequence());
        m_CommOutputPrinter.write(m_trans.getCodeTable());

        m_CommOutputPrinter.flush();
    }

    @Override
    public String getPrinterName() {
        return m_sName;
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

        m_CommOutputPrinter.write(Commands.SELECT_PRINTER);
        m_CommOutputPrinter.write(m_codes.transImage(image));
    }

    @Override
    public void printBarCode(String type, String position, String code) {

        m_CommOutputPrinter.write(Commands.SELECT_PRINTER);
        try {
            m_codes.printBarcode(m_CommOutputPrinter, type, position, code);
        } catch (UnsupportedEncodingException ex) {
        }
    }

    @Override
    public void beginLine(int iTextSize) {

        m_CommOutputPrinter.write(Commands.SELECT_PRINTER);

        if (iTextSize == DevicePrinter.SIZE_0) {
            m_CommOutputPrinter.write(m_codes.getSize0());
        } else if (iTextSize == DevicePrinter.SIZE_1) {
            m_CommOutputPrinter.write(m_codes.getSize1());
        } else if (iTextSize == DevicePrinter.SIZE_2) {
            m_CommOutputPrinter.write(m_codes.getSize2());
        } else if (iTextSize == DevicePrinter.SIZE_3) {
            m_CommOutputPrinter.write(m_codes.getSize3());
        } else {
            m_CommOutputPrinter.write(m_codes.getSize0());
        }
    }

    @Override
    public void printText(int iStyle, String sText) {

        m_CommOutputPrinter.write(Commands.SELECT_PRINTER);

        if ((iStyle & DevicePrinter.STYLE_BOLD) != 0) {
            m_CommOutputPrinter.write(m_codes.getBoldSet());
        }
        if ((iStyle & DevicePrinter.STYLE_UNDERLINE) != 0) {
            m_CommOutputPrinter.write(m_codes.getUnderlineSet());
        }
        m_CommOutputPrinter.write(m_trans.transString(sText));
        if ((iStyle & DevicePrinter.STYLE_UNDERLINE) != 0) {
            m_CommOutputPrinter.write(m_codes.getUnderlineReset());
        }
        if ((iStyle & DevicePrinter.STYLE_BOLD) != 0) {
            m_CommOutputPrinter.write(m_codes.getBoldReset());
        }
    }

    @Override
    public void endLine() {
        m_CommOutputPrinter.write(Commands.SELECT_PRINTER);
        m_CommOutputPrinter.write(m_codes.getNewLine());
    }

    @Override
    public void endReceipt() {
        m_CommOutputPrinter.write(Commands.SELECT_PRINTER);

        m_CommOutputPrinter.write(m_codes.getNewLine());
        m_CommOutputPrinter.write(m_codes.getNewLine());
        m_CommOutputPrinter.write(m_codes.getNewLine());
        m_CommOutputPrinter.write(m_codes.getNewLine());
        m_CommOutputPrinter.write(m_codes.getNewLine());

        m_CommOutputPrinter.write(m_codes.getCutReceipt());
        m_CommOutputPrinter.flush();
    }

    @Override
    public void openDrawer() {

        m_CommOutputPrinter.write(Commands.SELECT_PRINTER);
        m_CommOutputPrinter.write(m_codes.getOpenDrawer());
        m_CommOutputPrinter.flush();
    }

    @Override
    public void cutPaper(boolean complete) {
    }
}
