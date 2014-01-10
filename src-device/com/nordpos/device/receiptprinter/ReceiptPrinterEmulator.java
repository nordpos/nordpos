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

import com.nordpos.device.ReceiptPrinterInterface;
import com.openbravo.pos.util.StringParser;
import java.awt.Component;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3.0
 */
public class ReceiptPrinterEmulator implements ReceiptPrinterInterface {

    @Override
    public DevicePrinter getReceiptPrinter(String sProperty) throws Exception {
        StringParser sp = new StringParser(sProperty);
        String sPrinterType = sp.nextToken(':');
        switch (sPrinterType) {
            case "screen":
                return new DevicePrinterPanel();
            default:
                return new DevicePrinterNull();
        }
    }

    @Override
    public DevicePrinter getReceiptPrinter(Component awtComponent, String sProperty, PaperFormat paperFormat) throws Exception {
        StringParser sp = new StringParser(sProperty);
        String sPrinterType = sp.nextToken(':');
        String sPrinterParam1 = sp.nextToken(',');
        switch (sPrinterType) {
            case "printer":
                return new DevicePrinterPrinter(awtComponent, sPrinterParam1, paperFormat.getMarginLeft(), paperFormat.getMarginTop(), paperFormat.getWidth(), paperFormat.getHeight(), paperFormat.getType());
            default:
                return new DevicePrinterNull();
        }
    }
}
