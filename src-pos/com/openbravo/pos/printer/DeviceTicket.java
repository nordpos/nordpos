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

import com.openbravo.pos.forms.AppProperties;
import com.openbravo.pos.printer.escpos.*;
import com.openbravo.pos.printer.javapos.DeviceDisplayJavaPOS;
import com.openbravo.pos.printer.javapos.DeviceFiscalPrinterJavaPOS;
import com.openbravo.pos.printer.javapos.DevicePrinterJavaPOS;
import com.openbravo.pos.printer.printer.DevicePrinterPrinter;
import com.openbravo.pos.printer.screen.DeviceDisplayPanel;
import com.openbravo.pos.printer.screen.DeviceDisplayWindow;
import com.openbravo.pos.printer.screen.DevicePrinterPanel;
import com.openbravo.pos.util.SerialPortParameters;
import com.openbravo.pos.util.StringParser;
import gnu.io.SerialPort;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeviceTicket {

    private static final Logger logger = Logger.getLogger(DeviceTicket.class.getName());

    private DeviceFiscalPrinter m_deviceFiscal;
    private DeviceDisplay m_deviceDisplay;
    private DevicePrinter m_nullprinter;
    private DeviceLabelPrinter m_deviceLabel;
    private Map<String, DevicePrinter> m_deviceprinters;
    private List<DevicePrinter> m_deviceprinterslist;

    public DeviceTicket() {
        // Una impresora solo de pantalla.

        m_deviceFiscal = new DeviceFiscalPrinterNull();
        m_deviceDisplay = new DeviceDisplayNull();
        m_deviceLabel = new DeviceLabelPrinterNull();
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
        String sFiscalParam2 = sf.nextToken(',');

        Integer iFiscalPrinterSerialPortSpeed = 115200;
        Integer iFiscalPrinterSerialPortDataBits = SerialPort.DATABITS_8;
        Integer iFiscalPrinterSerialPortStopBits = SerialPort.STOPBITS_1;
        Integer iFiscalPrinterSerialPortParity = SerialPort.PARITY_NONE;

        if ("serial".equals(sFiscalParam1)) {
            iFiscalPrinterSerialPortSpeed = SerialPortParameters.getSpeed(sf.nextToken(','));
            iFiscalPrinterSerialPortDataBits =  SerialPortParameters.getDataBits(sf.nextToken(','));
            iFiscalPrinterSerialPortStopBits = SerialPortParameters.getStopBits(sf.nextToken(','));
            iFiscalPrinterSerialPortParity = SerialPortParameters.getParity(sf.nextToken(','));
        }

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

        Integer iDisplaySerialPortSpeed = 9600;
        Integer iDisplaySerialPortDataBits = SerialPort.DATABITS_8;
        Integer iDisplaySerialPortStopBits = SerialPort.STOPBITS_1;
        Integer iDisplaySerialPortParity = SerialPort.PARITY_NONE;

        // compatibilidad hacia atras.
        if ("serial".equals(sDisplayType) || "rxtx".equals(sDisplayType) || "file".equals(sDisplayType)) {
            sDisplayParam2 = sDisplayParam1;
            sDisplayParam1 = sDisplayType;
            sDisplayType = "epson";
        }

        if ("serial".equals(sDisplayParam1)) {
                iDisplaySerialPortSpeed = SerialPortParameters.getSpeed(sd.nextToken(','));
                iDisplaySerialPortDataBits =  SerialPortParameters.getDataBits(sd.nextToken(','));
                iDisplaySerialPortStopBits = SerialPortParameters.getStopBits(sd.nextToken(','));
                iDisplaySerialPortParity = SerialPortParameters.getParity(sd.nextToken(','));
            }

        try {
            if ("screen".equals(sDisplayType)) {
                m_deviceDisplay = new DeviceDisplayPanel();
            } else if ("window".equals(sDisplayType)) {
                m_deviceDisplay = new DeviceDisplayWindow();
            } else if ("epson".equals(sDisplayType)) {
                m_deviceDisplay = new DeviceDisplayESCPOS(pws.getPrinterWritter(sDisplayParam1, sDisplayParam2, iDisplaySerialPortSpeed, iDisplaySerialPortDataBits, iDisplaySerialPortStopBits, iDisplaySerialPortParity), new UnicodeTranslatorInt());
            } else if ("surepos".equals(sDisplayType)) {
                m_deviceDisplay = new DeviceDisplaySurePOS(pws.getPrinterWritter(sDisplayParam1, sDisplayParam2, iDisplaySerialPortSpeed, iDisplaySerialPortDataBits, iDisplaySerialPortStopBits, iDisplaySerialPortParity));
            } else if ("ld200".equals(sDisplayType)) {
                m_deviceDisplay = new DeviceDisplayESCPOS(pws.getPrinterWritter(sDisplayParam1, sDisplayParam2, iDisplaySerialPortSpeed, iDisplaySerialPortDataBits, iDisplaySerialPortStopBits, iDisplaySerialPortParity), new UnicodeTranslatorEur());
            } else if ("javapos".equals(sDisplayType)) {
                m_deviceDisplay = new DeviceDisplayJavaPOS(sDisplayParam1);
            } else {
                m_deviceDisplay = new DeviceDisplayNull();
            }
        } catch (TicketPrinterException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            m_deviceDisplay = new DeviceDisplayNull(e.getMessage());
        }

        StringParser slp = new StringParser(props.getProperty("machine.labelprinter"));
        String sLabelPrinterType = slp.nextToken(':');
        String sLabelPrinterParam1 = slp.nextToken(',');
        String sLabelPrinterParam2 = slp.nextToken(',');

        Integer iLabelPrinterSerialPortSpeed = 9600;
        Integer iLabelPrinterSerialPortDataBits = SerialPort.DATABITS_8;
        Integer iLabelPrinterSerialPortStopBits = SerialPort.STOPBITS_1;
        Integer iLabelPrinterSerialPortParity = SerialPort.PARITY_NONE;

        if ("serial".equals(sLabelPrinterParam1)) {
            iLabelPrinterSerialPortSpeed = SerialPortParameters.getSpeed(sd.nextToken(','));
            iLabelPrinterSerialPortDataBits = SerialPortParameters.getDataBits(sd.nextToken(','));
            iLabelPrinterSerialPortStopBits = SerialPortParameters.getStopBits(sd.nextToken(','));
            iLabelPrinterSerialPortParity = SerialPortParameters.getParity(sd.nextToken(','));
        }

//        try {
                m_deviceLabel = new DeviceLabelPrinterNull();
//        } catch (TicketPrinterException e) {
//            logger.log(Level.WARNING, e.getMessage(), e);
//            m_deviceLabel = new DeviceLabelPrinterNull(e.getMessage());
//        }

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
            Integer iPrinterSerialPortSpeed = 9600;
            Integer iPrinterSerialPortDataBits = SerialPort.DATABITS_8;
            Integer iPrinterSerialPortStopBits = SerialPort.STOPBITS_1;
            Integer iPrinterSerialPortParity = SerialPort.PARITY_NONE;

            // compatibilidad hacia atras.
            if ("serial".equals(sPrinterType) || "rxtx".equals(sPrinterType) || "file".equals(sPrinterType)) {
                sPrinterParam2 = sPrinterParam1;
                sPrinterParam1 = sPrinterType;
                sPrinterType = "epson";
            }

            if ("serial".equals(sPrinterParam1)) {
                iPrinterSerialPortSpeed = SerialPortParameters.getSpeed(sp.nextToken(','));
                iPrinterSerialPortDataBits =  SerialPortParameters.getDataBits(sp.nextToken(','));
                iPrinterSerialPortStopBits = SerialPortParameters.getStopBits(sp.nextToken(','));
                iPrinterSerialPortParity = SerialPortParameters.getParity(sp.nextToken(','));
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
                    addPrinter(sPrinterIndex, new DevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2, iPrinterSerialPortSpeed, iPrinterSerialPortDataBits, iPrinterSerialPortStopBits, iPrinterSerialPortParity), new CodesEpson(), new UnicodeTranslatorInt()));
                } else if ("tmu220".equals(sPrinterType)) {
                    addPrinter(sPrinterIndex, new DevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2, iPrinterSerialPortSpeed, iPrinterSerialPortDataBits, iPrinterSerialPortStopBits, iPrinterSerialPortParity), new CodesTMU220(), new UnicodeTranslatorInt()));
                } else if ("star".equals(sPrinterType)) {
                    addPrinter(sPrinterIndex, new DevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2, iPrinterSerialPortSpeed, iPrinterSerialPortDataBits, iPrinterSerialPortStopBits, iPrinterSerialPortParity), new CodesStar(), new UnicodeTranslatorStar()));
                } else if ("ithaca".equals(sPrinterType)) {
                    addPrinter(sPrinterIndex, new DevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2, iPrinterSerialPortSpeed, iPrinterSerialPortDataBits, iPrinterSerialPortStopBits, iPrinterSerialPortParity), new CodesIthaca(), new UnicodeTranslatorInt()));
                } else if ("surepos".equals(sPrinterType)) {
                    addPrinter(sPrinterIndex, new DevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2, iPrinterSerialPortSpeed, iPrinterSerialPortDataBits, iPrinterSerialPortStopBits, iPrinterSerialPortParity), new CodesSurePOS(), new UnicodeTranslatorSurePOS()));
                } else if ("plain".equals(sPrinterType)) {
                    addPrinter(sPrinterIndex, new DevicePrinterPlain(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2, iPrinterSerialPortSpeed, iPrinterSerialPortDataBits, iPrinterSerialPortStopBits, iPrinterSerialPortParity)));
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

        public PrinterWritter getPrinterWritter(String con, String port, Integer speed,  Integer bits, Integer stopbits, Integer parity) throws TicketPrinterException {

            String skey = con + "-->" + port;
            PrinterWritter pw = (PrinterWritter) m_apool.get(skey);
            if (pw == null) {
                if ("serial".equals(con) || "rxtx".equals(con)) {
                    pw = new PrinterWritterRXTX(port, speed, bits, stopbits, parity);
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
        return m_deviceDisplay;
    }

    public DeviceLabelPrinter getLabelPrinter() {
        return m_deviceLabel;
    }

    // Receipt printers
    public DevicePrinter getDevicePrinter(String key) {
        DevicePrinter printer = m_deviceprinters.get(key);
        return printer == null ? m_nullprinter : printer;
    }

    public List<DevicePrinter> getDevicePrinterAll() {
        return m_deviceprinterslist;
    }
}
