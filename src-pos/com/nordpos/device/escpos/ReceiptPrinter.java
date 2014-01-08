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
package com.nordpos.device.escpos;

import com.nordpos.device.receiptprinter.DevicePrinter;
import com.nordpos.device.receiptprinter.DevicePrinterNull;
import com.nordpos.device.ReceiptPrinterInterface;
import com.nordpos.device.receiptprinter.PaperFormat;
import com.nordpos.device.writter.Writter;
import com.nordpos.device.writter.WritterFile;
import com.nordpos.device.writter.WritterRXTX;
import com.nordpos.device.ticket.TicketPrinterException;
import com.nordpos.device.receiptprinter.DevicePrinterPrinter;
import com.nordpos.device.receiptprinter.DevicePrinterPanel;
import com.nordpos.device.util.SerialPortParameters;
import com.openbravo.pos.util.StringParser;
import gnu.io.SerialPort;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3.0
 */
public class ReceiptPrinter implements ReceiptPrinterInterface {

    @Override
    public DevicePrinter getTicketPrinter(String sProperty) throws Exception {
        PrinterWritterPool pws = new PrinterWritterPool();
        StringParser sp = new StringParser(sProperty);
        String sPrinterType = sp.nextToken(':');
        String sPrinterParam1 = sp.nextToken(',');
        String sPrinterParam2 = sp.nextToken(',');
        Integer iPrinterSerialPortSpeed = 9600;
        Integer iPrinterSerialPortDataBits = SerialPort.DATABITS_8;
        Integer iPrinterSerialPortStopBits = SerialPort.STOPBITS_1;
        Integer iPrinterSerialPortParity = SerialPort.PARITY_NONE;
        if ("serial".equals(sPrinterParam1)) {
            iPrinterSerialPortSpeed = SerialPortParameters.getSpeed(sp.nextToken(','));
            iPrinterSerialPortDataBits = SerialPortParameters.getDataBits(sp.nextToken(','));
            iPrinterSerialPortStopBits = SerialPortParameters.getStopBits(sp.nextToken(','));
            iPrinterSerialPortParity = SerialPortParameters.getParity(sp.nextToken(','));
        }

        switch (sPrinterType) {
            case "epson":
                return new DevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2, iPrinterSerialPortSpeed, iPrinterSerialPortDataBits, iPrinterSerialPortStopBits, iPrinterSerialPortParity), new CodesEpson(), new UnicodeTranslatorInt());
            case "screen":
                return new DevicePrinterPanel();
            default:
                return new DevicePrinterNull();
        }

    }

    @Override
    public DevicePrinter getTicketPrinter(Component awtComponent, String sProperty, PaperFormat paperFormat) throws Exception {
        StringParser sp = new StringParser(sProperty);
        String sPrinterType = sp.nextToken(':');
        String sPrinterParam1 = sp.nextToken(',');
        switch (sPrinterType) {
            case "printer":
                return new DevicePrinterPrinter(awtComponent, sPrinterParam1, paperFormat.getMarginLeft(), paperFormat.getMarginTop(), paperFormat.getWidth(), paperFormat.getHeight(), paperFormat.getType());
            default:
                return new DevicePrinterNull();
        }

//                } else if ("tmu220".equals(sPrinterType)) {
//                    addPrinter(sPrinterIndex, new DevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2, iPrinterSerialPortSpeed, iPrinterSerialPortDataBits, iPrinterSerialPortStopBits, iPrinterSerialPortParity), new CodesTMU220(), new UnicodeTranslatorInt()));
//                } else if ("star".equals(sPrinterType)) {
//                    addPrinter(sPrinterIndex, new DevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2, iPrinterSerialPortSpeed, iPrinterSerialPortDataBits, iPrinterSerialPortStopBits, iPrinterSerialPortParity), new CodesStar(), new UnicodeTranslatorStar()));
//                } else if ("ithaca".equals(sPrinterType)) {
//                    addPrinter(sPrinterIndex, new DevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2, iPrinterSerialPortSpeed, iPrinterSerialPortDataBits, iPrinterSerialPortStopBits, iPrinterSerialPortParity), new CodesIthaca(), new UnicodeTranslatorInt()));
//                } else if ("surepos".equals(sPrinterType)) {
//                    addPrinter(sPrinterIndex, new DevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2, iPrinterSerialPortSpeed, iPrinterSerialPortDataBits, iPrinterSerialPortStopBits, iPrinterSerialPortParity), new CodesSurePOS(), new UnicodeTranslatorSurePOS()));
//
    }

    private static class PrinterWritterPool {

        private final Map<String, Writter> m_apool = new HashMap<>();

        public Writter getPrinterWritter(String con, String port, Integer speed, Integer bits, Integer stopbits, Integer parity) throws TicketPrinterException {

            String skey = con + "-->" + port;
            Writter pw = (Writter) m_apool.get(skey);
            if (pw == null) {
                switch (con) {
                    case "serial":
                    case "rxtx":
                        pw = new WritterRXTX(port, speed, bits, stopbits, parity);
                        m_apool.put(skey, pw);
                        break;
                    case "file":
                        pw = new WritterFile(port);
                        m_apool.put(skey, pw);
                        break;
                    default:
                        throw new TicketPrinterException();
                }
            }
            return pw;
        }
    }
}
