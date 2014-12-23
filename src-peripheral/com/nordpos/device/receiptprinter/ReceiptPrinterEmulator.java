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

import com.nordpos.device.util.StringParser;
import com.nordpos.device.writter.WritterFile;
import java.awt.Component;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
public class ReceiptPrinterEmulator implements ReceiptPrinterInterface {

    public static final byte[] EOL_DOS = {0x0D, 0x0A}; // Print and carriage return
    public static final byte[] EOL_UNIX = {0x0A};

    @Override
    public DevicePrinter getReceiptPrinter(String sProperty) throws Exception {
        StringParser sp = new StringParser(sProperty);
        String sPrinterType = sp.nextToken(':');
        String sPrinterParam1 = sp.nextToken(',');
        String sPrinterParam2 = sp.nextToken(',');
        String sPrinterParam3 = sp.nextToken(',');

        switch (sPrinterType) {
            case "plaintext":
                if ("file".equals(sPrinterParam1)) {
                    if ("unix".equals(sPrinterParam3)) {
                        return new DevicePrinterPlainText(new WritterFile(sPrinterParam2), EOL_UNIX);
                    } else {
                        return new DevicePrinterPlainText(new WritterFile(sPrinterParam2), EOL_DOS);
                    }

                } else {
                    return new DevicePrinterNull();
                }
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
