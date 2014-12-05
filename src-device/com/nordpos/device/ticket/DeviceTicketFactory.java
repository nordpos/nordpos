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
package com.nordpos.device.ticket;

import com.nordpos.device.receiptprinter.DevicePrinter;
import com.nordpos.device.receiptprinter.DevicePrinterNull;
import com.nordpos.device.receiptprinter.PaperFormat;
import com.nordpos.device.receiptprinter.DevicePrinterPanel;
import com.nordpos.device.display.DeviceDisplayNull;
import com.nordpos.device.display.DeviceDisplay;
import com.nordpos.device.DisplayInterface;
import com.nordpos.device.FiscalPrinterInterface;
import com.nordpos.device.LabelPrinterInterface;
import com.nordpos.device.ReceiptPrinterInterface;
import com.openbravo.pos.forms.AppProperties;
import com.nordpos.device.fiscalprinter.DeviceFiscalPrinter;
import com.nordpos.device.fiscalprinter.DeviceFiscalPrinterNull;
import com.nordpos.device.labelprinter.DeviceLabelPrinter;
import com.nordpos.device.labelprinter.DeviceLabelPrinterNull;
import com.nordpos.device.util.StringParser;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeviceTicketFactory {

    private static final Logger logger = Logger.getLogger(DeviceTicketFactory.class.getName());

    private DeviceFiscalPrinter m_deviceFiscal;
    private DeviceDisplay m_deviceDisplay;
    private DevicePrinter m_nullprinter;
    private DeviceLabelPrinter m_deviceLabel;
    private Map<String, DevicePrinter> m_deviceprinters;
    private List<DevicePrinter> m_deviceprinterslist;

    public DeviceTicketFactory() {
        m_deviceFiscal = new DeviceFiscalPrinterNull();
        m_nullprinter = new DevicePrinterNull();
        m_deviceprinters = new HashMap<>();
        m_deviceprinterslist = new ArrayList<>();

        DevicePrinter p = new DevicePrinterPanel();
        m_deviceprinters.put("1", p);
        m_deviceprinterslist.add(p);
    }

    public DeviceTicketFactory(Component parent, AppProperties props) {

        ServiceLoader<FiscalPrinterInterface> fiscalLoader = ServiceLoader.load(FiscalPrinterInterface.class);
        ServiceLoader<DisplayInterface> displayLoader = ServiceLoader.load(DisplayInterface.class);
        ServiceLoader<LabelPrinterInterface> labelPrinterLoader = ServiceLoader.load(LabelPrinterInterface.class);
        ServiceLoader<ReceiptPrinterInterface> ticketPrinterLoader = ServiceLoader.load(ReceiptPrinterInterface.class);

        m_deviceFiscal = new DeviceFiscalPrinterNull();
        m_deviceDisplay = new DeviceDisplayNull();
        m_deviceLabel = new DeviceLabelPrinterNull();

        for (FiscalPrinterInterface machineInterface : fiscalLoader) {
            try {
                m_deviceFiscal = machineInterface.getFiscalPrinter(props.getProperty("machine.fiscalprinter"));
            } catch (Exception e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        }

        for (DisplayInterface machineInterface : displayLoader) {
            try {
                m_deviceDisplay = machineInterface.getDisplay(props.getProperty("machine.display"));
            } catch (Exception e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        }

        for (LabelPrinterInterface machineInterface : labelPrinterLoader) {
            try {
                m_deviceLabel = machineInterface.getLabelPrinter(props.getProperty("machine.labelprinter"));
            } catch (Exception e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        }

        m_nullprinter = new DevicePrinterNull();
        m_deviceprinters = new HashMap<>();
        m_deviceprinterslist = new ArrayList<>();

        int iPrinterIndex = 1;
        String sPrinterIndex = Integer.toString(iPrinterIndex);
        String sprinter = props.getProperty("machine.printer");

        while (sprinter != null && !sprinter.isEmpty()) {

            StringParser sp = new StringParser(sprinter);
            sp.nextToken(':');
            sp.nextToken(',');
            String sPrinterParam2 = sp.nextToken(',');

            PaperFormat paperFormat = new PaperFormat();
            if (sPrinterParam2.equals("receipt") || sPrinterParam2.equals("standard")) {
                paperFormat.setType(props.getProperty("paper." + sPrinterParam2 + ".mediasizename"));
                paperFormat.setMarginLeft(Integer.parseInt(props.getProperty("paper." + sPrinterParam2 + ".x")));
                paperFormat.setMarginTop(Integer.parseInt(props.getProperty("paper." + sPrinterParam2 + ".y")));
                paperFormat.setWidth(Integer.parseInt(props.getProperty("paper." + sPrinterParam2 + ".width")));
                paperFormat.setHeight(Integer.parseInt(props.getProperty("paper." + sPrinterParam2 + ".height")));
                for (ReceiptPrinterInterface machineInterface : ticketPrinterLoader) {
                    try {
                        addPrinter(sPrinterIndex, machineInterface.getReceiptPrinter(parent, sprinter, paperFormat));
                    } catch (Exception e) {
                        logger.log(Level.WARNING, e.getMessage(), e);
                        addPrinter(sPrinterIndex, new DevicePrinterNull(e.getMessage()));
                    }
                }
            } else {
                for (ReceiptPrinterInterface machineInterface : ticketPrinterLoader) {
                    try {
                        addPrinter(sPrinterIndex, machineInterface.getReceiptPrinter(sprinter));
                    } catch (Exception e) {
                        logger.log(Level.WARNING, e.getMessage(), e);
                        addPrinter(sPrinterIndex, new DevicePrinterNull(e.getMessage()));
                    }
                }
            }
            iPrinterIndex++;
            sPrinterIndex = Integer.toString(iPrinterIndex);
            sprinter = props.getProperty("machine.printer." + sPrinterIndex);
        }
    }

    private void addPrinter(String sPrinterIndex, DevicePrinter p) {
        m_deviceprinters.put(sPrinterIndex, p);
        m_deviceprinterslist.add(p);
    }

    public DeviceFiscalPrinter getFiscalPrinter() {
        return m_deviceFiscal;
    }

    public DeviceDisplay getDeviceDisplay() {
        return m_deviceDisplay;
    }

    public DeviceLabelPrinter getLabelPrinter() {
        return m_deviceLabel;
    }

    public DevicePrinter getDevicePrinter(String key) {
        DevicePrinter printer = m_deviceprinters.get(key);
        return printer == null ? m_nullprinter : printer;
    }

    public List<DevicePrinter> getDevicePrinterAll() {
        return m_deviceprinterslist;
    }
}
