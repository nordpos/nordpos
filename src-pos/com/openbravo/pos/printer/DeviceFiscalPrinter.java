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

package com.openbravo.pos.printer;

import javax.swing.JComponent;

/**
 * @author: Gennady Kovalev <gik@bigur.ru>
 * @author: Andrey Svininykh <svininykh@gmail.com>
 * @author: Artur Akchurin <akartkam@gmail.com>
 */

public interface DeviceFiscalPrinter {

    public String getFiscalName();
    public JComponent getFiscalComponent();

    public void beginReceipt(String sType, int iNumber, String sDate, String sTime, String sCashier) throws TicketFiscalPrinterException;
    public void endReceipt() throws TicketFiscalPrinterException;
    public void printLine(String sproduct, double dprice, double dunits, int taxinfo) throws TicketFiscalPrinterException;
    public void printMessage(String smessage) throws TicketFiscalPrinterException;
    public void printTotal(String sPayment, double dpaid, String sPaymentType) throws TicketFiscalPrinterException;

    public void cutPaper(boolean complete) throws TicketFiscalPrinterException;

    public void printZReport() throws TicketFiscalPrinterException;
    public void printXReport() throws TicketFiscalPrinterException;
    
    public void printCashIn(double dsumm) throws TicketFiscalPrinterException;
    public void printCashOut(double dsumm) throws TicketFiscalPrinterException;
}
