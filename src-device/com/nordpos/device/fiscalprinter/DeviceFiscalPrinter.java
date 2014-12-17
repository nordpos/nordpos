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
package com.nordpos.device.fiscalprinter;

import com.nordpos.device.ticket.TicketPrinterException;
import javax.swing.JComponent;

/**
 * 
 * @author adrianromero
 * @author Gennady Kovalev <gik@bigur.ru>
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @author Artur Akchurin <akartkam@gmail.com>
 * @version NORD POS 3
 */
public interface DeviceFiscalPrinter {

    public String getFiscalName();
    public JComponent getFiscalComponent();

    public void beginReceipt(String sType, int iNumber, String sDate, String sTime, String sCashier) throws TicketPrinterException;
    public void endReceipt() throws TicketPrinterException;
    public void printLine(String sproduct, double dprice, double dunits, int taxinfo) throws TicketPrinterException;
    public void printMessage(String smessage) throws TicketPrinterException;
    public void printTotal(String sPayment, double dpaid, String sPaymentType) throws TicketPrinterException;

    public void cutPaper(boolean complete) throws TicketPrinterException;

    public void printZReport() throws TicketPrinterException;
    public void printXReport() throws TicketPrinterException;

    public void printCashIn(double dsumm) throws TicketPrinterException;
    public void printCashOut(double dsumm) throws TicketPrinterException;
}
