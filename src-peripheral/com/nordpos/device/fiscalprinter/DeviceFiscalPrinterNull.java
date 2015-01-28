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
package com.nordpos.device.fiscalprinter;

import javax.swing.JComponent;

/**
 *
 * @author adrianromero
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3
 */
public class DeviceFiscalPrinterNull implements DeviceFiscalPrinter {

    public DeviceFiscalPrinterNull() {
    }

    public DeviceFiscalPrinterNull(String desc) {
    }

    @Override
    public String getFiscalName() {
        return null;
    }

    @Override
    public JComponent getFiscalComponent() {
        return null;
    }

    @Override
    public void beginReceipt(String sType, int iNumber, String sDate, String sTime, String sCashier) {
    }

    @Override
    public void endReceipt() {
    }

    @Override
    public void printLine(String sproduct, double dprice, double dunits, int taxinfo) {
    }

    @Override
    public void printMessage(String smessage) {
    }

    @Override
    public void printTotal(String sPayment, double dpaid, String sPaymentType) {
    }

    @Override
    public void cutPaper(boolean complete) {
    }

    @Override
    public void printZReport() {
    }

    @Override
    public void printXReport() {
    }

    @Override
    public void printCashIn(double dsumm) {
    }

    @Override
    public void printCashOut(double dsumm) {
    }
}
