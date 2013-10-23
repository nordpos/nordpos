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

package com.openbravo.pos.printer.javapos;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JComponent;
import jpos.CashDrawer;
import jpos.JposException;
import jpos.POSPrinter;
import jpos.POSPrinterConst;
import com.openbravo.data.loader.ImageUtils;
import com.openbravo.pos.printer.DevicePrinter;
import com.openbravo.pos.printer.TicketPrinterException;

public class DevicePrinterJavaPOS  implements DevicePrinter {
    
    private static final String JPOS_SIZE0 = "\u001b|1C";
    private static final String JPOS_SIZE1 = "\u001b|2C";
    private static final String JPOS_SIZE2 = "\u001b|3C";
    private static final String JPOS_SIZE3 = "\u001b|4C";
    private static final String JPOS_LF = "\n";
    private static final String JPOS_BOLD = "\u001b|bC";
    private static final String JPOS_UNDERLINE = "\u001b|uC";
    private static final String JPOS_CUT = "\u001b|100fP";
    
    private String m_sName;
    
    private POSPrinter m_printer = null;
    private CashDrawer m_drawer = null;
    
    private StringBuffer m_sline;

    /** Creates a new instance of DevicePrinterJavaPOS */
    public DevicePrinterJavaPOS(String sDevicePrinterName, String sDeviceDrawerName) throws TicketPrinterException {
        
        m_sName = sDevicePrinterName;
        if (sDeviceDrawerName != null && !sDeviceDrawerName.equals("")) {
            m_sName += " - " + sDeviceDrawerName;
        }
               
        try {       
            m_printer = new POSPrinter();
            m_printer.open(sDevicePrinterName);
            m_printer.claim(10000);
            m_printer.setDeviceEnabled(true);
            m_printer.setMapMode(POSPrinterConst.PTR_MM_METRIC);  // unit = 1/100 mm - i.e. 1 cm = 10 mm = 10 * 100 units
        } catch (JposException e) {
            // cannot live without the printer.
            throw new TicketPrinterException(e.getMessage(), e);
        } 
        
        try {
            m_drawer = new CashDrawer();
            m_drawer.open(sDeviceDrawerName);
            m_drawer.claim(10000);
            m_drawer.setDeviceEnabled(true);
        } catch (JposException e) {
            // can live without the drawer;
            m_drawer = null;
        }
    }
   
    public String getPrinterName() {
        return m_sName;
    }
    public String getPrinterDescription() {
        return null;
    }   
    public JComponent getPrinterComponent() {
        return null;
    }
    public void reset() {
    }
    
    public void beginReceipt() {
        try {
            m_printer.transactionPrint(POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_TP_TRANSACTION);
        } catch (JposException e) {
        }
    }
    
    public void printImage(BufferedImage image) {
        try {
            if (m_printer.getCapRecBitmap()) { // si podemos imprimir bitmaps.
                
                File f = File.createTempFile("jposimg", ".png");
                OutputStream out = new FileOutputStream(f);
                out.write(ImageUtils.writeImage(image));
                out.close();
                
                m_printer.printBitmap(POSPrinterConst.PTR_S_RECEIPT, f.getAbsolutePath(), POSPrinterConst.PTR_BM_ASIS, POSPrinterConst.PTR_BM_CENTER);
            }
        } catch (IOException eIO) {
        } catch (JposException e) {
        }
    }
    
    public void printBarCode(String type, String position, String code) {
        try {
            if (m_printer.getCapRecBarCode()) { // si podemos imprimir codigos de barras
                if (DevicePrinter.POSITION_NONE.equals(position)) {                
                    m_printer.printBarCode(POSPrinterConst.PTR_S_RECEIPT, code, POSPrinterConst.PTR_BCS_EAN13, 10 * 100, 60 * 100, POSPrinterConst.PTR_BC_CENTER, POSPrinterConst.PTR_BC_TEXT_NONE);
                } else {
                    m_printer.printBarCode(POSPrinterConst.PTR_S_RECEIPT, code, POSPrinterConst.PTR_BCS_EAN13, 10 * 100, 60 * 100, POSPrinterConst.PTR_BC_CENTER, POSPrinterConst.PTR_BC_TEXT_BELOW);
                }
            }
        } catch (JposException e) {
        }
    }
    
    public void beginLine(int iTextSize) {
        m_sline = new StringBuffer();
        if (iTextSize == DevicePrinter.SIZE_0) {
            m_sline.append(JPOS_SIZE0);
        } else if (iTextSize == DevicePrinter.SIZE_1) {
            m_sline.append(JPOS_SIZE1);
        } else if (iTextSize == DevicePrinter.SIZE_2) {
            m_sline.append(JPOS_SIZE2);
        } else if (iTextSize == DevicePrinter.SIZE_3) {
            m_sline.append(JPOS_SIZE3);
        } else {
            m_sline.append(JPOS_SIZE0);
        }
    }
    
    public void printText(int iStyle, String sText) {
        
        if ((iStyle & DevicePrinter.STYLE_BOLD) != 0) {
            m_sline.append(JPOS_BOLD);
        }
        if ((iStyle & DevicePrinter.STYLE_UNDERLINE) != 0) {
            m_sline.append(JPOS_UNDERLINE);
        }
        m_sline.append(sText);
    }
    
    public void endLine() {
        
        m_sline.append(JPOS_LF);
        try {
            m_printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, m_sline.toString());
        } catch (JposException e) {
        }
        m_sline = null;
    }
    
    public void endReceipt() {
        try {
            // cut the receipt
            m_printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, JPOS_CUT);
            
            // end of the transaction
            m_printer.transactionPrint(POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_TP_NORMAL);
        } catch (JposException e) {
        }
    }     
    
    public void openDrawer() {
        
        if (m_drawer != null) {
            try {
                m_drawer.openDrawer();
            } catch (JposException e) {
            }
        }
    }
    
    @Override
    public void finalize() throws Throwable {
       
        m_printer.setDeviceEnabled(false);
        m_printer.release();
        m_printer.close();
        
        if (m_drawer != null) {
            m_drawer.setDeviceEnabled(false);
            m_drawer.release();
            m_drawer.close();
        }
        
        super.finalize();
    }    
}
