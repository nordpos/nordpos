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

import java.util.*;
import com.openbravo.pos.forms.AppProperties;
import com.openbravo.pos.printer.escpos.*;
import com.openbravo.pos.printer.javapos.DeviceDisplayJavaPOS;
import com.openbravo.pos.printer.javapos.DeviceFiscalPrinterJavaPOS;
import com.openbravo.pos.printer.javapos.DevicePrinterJavaPOS;
import com.openbravo.pos.printer.printer.DevicePrinterPrinter;
import com.openbravo.pos.printer.screen.*;

import com.openbravo.pos.util.StringParser;
import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeviceTicket {

    private static Logger logger = Logger.getLogger("com.openbravo.pos.printer.DeviceTicket");

    private DeviceFiscalPrinter m_deviceFiscal;
    private DeviceDisplay m_devicedisplay;
    private DevicePrinter m_nullprinter;
    private Map<String, DevicePrinter> m_deviceprinters;
    private List<DevicePrinter> m_deviceprinterslist;

    /** Creates a new instance of DeviceTicket */
    public DeviceTicket() {
        // Una impresora solo de pantalla.

        m_deviceFiscal = new DeviceFiscalPrinterNull();

        m_devicedisplay = new DeviceDisplayNull();

        m_nullprinter = new DevicePrinterNull();
        m_deviceprinters = new HashMap<String, DevicePrinter>();
        m_deviceprinterslist = new ArrayList<DevicePrinter>();

        DevicePrinter p = new DevicePrinterPanel();
        m_deviceprinters.put("1", p);
        m_deviceprinterslist.add(p);
    }

    public DeviceTicket(Component parent, AppProperties props) {

        PrinterWritterPool pws = new PrinterWritterPool();

        // La impresora fiscal
        StringParser sf = new StringParser(props.getProperty("machine.fiscalprinter"));
        String sFiscalType = sf.nextToken(':');
        String sFiscalParam1 = sf.nextToken(',');
        try {
            if ("javapos".equals(sFiscalType)) {
                m_deviceFiscal = new DeviceFiscalPrinterJavaPOS(sFiscalParam1);
            } else {
                m_deviceFiscal = new DeviceFiscalPrinterNull();
            }
        } catch (TicketPrinterException e) {
            m_deviceFiscal = new DeviceFiscalPrinterNull(e.getMessage());
        }

        // El visor
        StringParser sd = new StringParser(props.getProperty("machine.display"));
        String sDisplayType = sd.nextToken(':');
        String sDisplayParam1 = sd.nextToken(',');
        String sDisplayParam2 = sd.nextToken(',');

        // compatibilidad hacia atras.
        if ("serial".equals(sDisplayType) || "rxtx".equals(sDisplayType) || "file".equals(sDisplayType)) {
            sDisplayParam2 = sDisplayParam1;
            sDisplayParam1 = sDisplayType;
            sDisplayType = "epson";
        }

        try {
            if ("screen".equals(sDisplayType)) {
                m_devicedisplay = new DeviceDisplayPanel();
            } else if ("window".equals(sDisplayType)) {
                m_devicedisplay = new DeviceDisplayWindow();
            } else if ("epson".equals(sDisplayType)) {
                m_devicedisplay = new DeviceDisplayESCPOS(pws.getPrinterWritter(sDisplayParam1, sDisplayParam2), new UnicodeTranslatorInt());
            } else if ("surepos".equals(sDisplayType)) {
                m_devicedisplay = new DeviceDisplaySurePOS(pws.getPrinterWritter(sDisplayParam1, sDisplayParam2));
            } else if ("ld200".equals(sDisplayType)) {
                m_devicedisplay = new DeviceDisplayESCPOS(pws.getPrinterWritter(sDisplayParam1, sDisplayParam2), new UnicodeTranslatorEur());
            } else if ("javapos".equals(sDisplayType)) {
                m_devicedisplay = new DeviceDisplayJavaPOS(sDisplayParam1);
            } else {
                m_devicedisplay = new DeviceDisplayNull();
            }
        } catch (TicketPrinterException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            m_devicedisplay = new DeviceDisplayNull(e.getMessage());
        }

        m_nullprinter = new DevicePrinterNull();
        m_deviceprinters = new HashMap<String, DevicePrinter>();
        m_deviceprinterslist = new ArrayList<DevicePrinter>();

        // Empezamos a iterar por las impresoras...
        int iPrinterIndex = 1;
        String sPrinterIndex = Integer.toString(iPrinterIndex);
        String sprinter = props.getProperty("machine.printer");

        while (sprinter != null && !"".equals(sprinter)) {

            StringParser sp = new StringParser(sprinter);
            String sPrinterType = sp.nextToken(':');
            String sPrinterParam1 = sp.nextToken(',');
            String sPrinterParam2 = sp.nextToken(',');

            // compatibilidad hacia atras.
            if ("serial".equals(sPrinterType) || "rxtx".equals(sPrinterType) || "file".equals(sPrinterType)) {
                sPrinterParam2 = sPrinterParam1;
                sPrinterParam1 = sPrinterType;
                sPrinterType = "epson";
            }

            try {
                if ("screen".equals(sPrinterType)) {
                    addPrinter(sPrinterIndex, new DevicePrinterPanel());
                } else if ("printer".equals(sPrinterType)) {

                    // backward compatibility
                    if (sPrinterParam2 == null || sPrinterParam2.equals("") || sPrinterParam2.equals("true")) {
                        sPrinterParam2 = "receipt";
                    } else if (sPrinterParam2.equals("false")) {
                        sPrinterParam2 = "standard";
                    }

                    addPrinter(sPrinterIndex, new DevicePrinterPrinter(parent, sPrinterParam1,
                            Integer.parseInt(props.getProperty("paper." + sPrinterParam2 + ".x")),
                            Integer.parseInt(props.getProperty("paper." + sPrinterParam2 + ".y")),
                            Integer.parseInt(props.getProperty("paper." + sPrinterParam2 + ".width")),
                            Integer.parseInt(props.getProperty("paper." + sPrinterParam2 + ".height")),
                            props.getProperty("paper." + sPrinterParam2 + ".mediasizename")
                            ));
                } else if ("epson".equals(sPrinterType)) {
                    addPrinter(sPrinterIndex, new DevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2), new CodesEpson(), new UnicodeTranslatorInt()));
                } else if ("tmu220".equals(sPrinterType)) {
                    addPrinter(sPrinterIndex, new DevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2), new CodesTMU220(), new UnicodeTranslatorInt()));
                } else if ("star".equals(sPrinterType)) {
                    addPrinter(sPrinterIndex, new DevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2), new CodesStar(), new UnicodeTranslatorStar()));
                } else if ("ithaca".equals(sPrinterType)) {
                    addPrinter(sPrinterIndex, new DevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2), new CodesIthaca(), new UnicodeTranslatorInt()));
                } else if ("surepos".equals(sPrinterType)) {
                    addPrinter(sPrinterIndex, new DevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2), new CodesSurePOS(), new UnicodeTranslatorSurePOS()));
                } else if ("plain".equals(sPrinterType)) {
                    addPrinter(sPrinterIndex, new DevicePrinterPlain(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2)));
                } else if ("javapos".equals(sPrinterType)) {
                    addPrinter(sPrinterIndex, new DevicePrinterJavaPOS(sPrinterParam1, sPrinterParam2));
                }
            } catch (TicketPrinterException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }

            // siguiente impresora...
            iPrinterIndex++;
            sPrinterIndex = Integer.toString(iPrinterIndex);
            sprinter = props.getProperty("machine.printer." + sPrinterIndex);
        }
    }

    private void addPrinter(String sPrinterIndex, DevicePrinter p) {
        m_deviceprinters.put(sPrinterIndex, p);
        m_deviceprinterslist.add(p);
    }

    private static class PrinterWritterPool {

        private Map<String, PrinterWritter> m_apool = new HashMap<String, PrinterWritter>();

        public PrinterWritter getPrinterWritter(String con, String port) throws TicketPrinterException {

            String skey = con + "-->" + port;
            PrinterWritter pw = (PrinterWritter) m_apool.get(skey);
            if (pw == null) {
                if ("serial".equals(con) || "rxtx".equals(con)) {
                    pw = new PrinterWritterRXTX(port);
                    m_apool.put(skey, pw);
                } else if ("file".equals(con)) {
                    pw = new PrinterWritterFile(port);
                    m_apool.put(skey, pw);
                } else {
                    throw new TicketPrinterException();
                }
            }
            return pw;
        }
    }
    // Impresora fiscal
    public DeviceFiscalPrinter getFiscalPrinter() {
        return m_deviceFiscal;
    }
    // Display
    public DeviceDisplay getDeviceDisplay() {
        return m_devicedisplay;
    }
    // Receipt printers
    public DevicePrinter getDevicePrinter(String key) {
        DevicePrinter printer = m_deviceprinters.get(key);
        return printer == null ? m_nullprinter : printer;
    }

    public List<DevicePrinter> getDevicePrinterAll() {
        return m_deviceprinterslist;
    }
    // Utilidades
    public static String getWhiteString(int iSize, char cWhiteChar) {

        char[] cFill = new char[iSize];
        for (int i = 0; i < iSize; i++) {
            cFill[i] = cWhiteChar;
        }
        return new String(cFill);
    }

    public static String getWhiteString(int iSize) {

        return getWhiteString(iSize, ' ');
    }

    public static String alignBarCode(String sLine, int iSize) {

        if (sLine.length() > iSize) {
            return sLine.substring(sLine.length() - iSize);
        } else {
            return getWhiteString(iSize - sLine.length(), '0') + sLine;
        }
    }

    public static String alignLeft(String sLine, int iSize) {

        if (sLine.length() > iSize) {
            return sLine.substring(0, iSize);
        } else {
            return sLine + getWhiteString(iSize - sLine.length());
        }
    }

    public static String alignRight(String sLine, int iSize) {

        if (sLine.length() > iSize) {
            return sLine.substring(sLine.length() - iSize);
        } else {
            return getWhiteString(iSize - sLine.length()) + sLine;
        }
    }

    public static String alignCenter(String sLine, int iSize) {

        if (sLine.length() > iSize) {
            return alignRight(sLine.substring(0, (sLine.length() + iSize) / 2), iSize);
        } else {
            return alignRight(sLine + getWhiteString((iSize - sLine.length()) / 2), iSize);
        }
    }

    public static String alignCenter(String sLine) {
        return alignCenter(sLine, 42);
    }

    public static final byte[] transNumber(String sCad) {

        if (sCad == null) {
            return null;
        } else {
            byte bAux[] = new byte[sCad.length()];
            for( int i = 0; i < sCad.length(); i++) {
                bAux[i] = transNumberChar(sCad.charAt(i));
            }
            return bAux;
        }
    }

    public static byte transNumberChar(char sChar) {
        switch (sChar) {
        case '0' : return 0x30;
        case '1' : return 0x31;
        case '2' : return 0x32;
        case '3' : return 0x33;
        case '4' : return 0x34;
        case '5' : return 0x35;
        case '6' : return 0x36;
        case '7' : return 0x37;
        case '8' : return 0x38;
        case '9' : return 0x39;
        default: return 0x30;
        }
    }
}
