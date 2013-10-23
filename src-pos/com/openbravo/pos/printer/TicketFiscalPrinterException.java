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

import javax.print.PrintException;

public class TicketFiscalPrinterException extends PrintException {

    public TicketFiscalPrinterException() {
    }

    public TicketFiscalPrinterException(String msg) {
        super(msg);
    }

    public TicketFiscalPrinterException(Exception e) {
        super(e);
    }

    public TicketFiscalPrinterException(String msg, Exception e) {
        super(msg, e);
    }
}

