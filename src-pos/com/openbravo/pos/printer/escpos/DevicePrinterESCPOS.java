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

package com.openbravo.pos.printer.escpos;

import java.awt.image.BufferedImage;
import javax.swing.JComponent;

import com.openbravo.pos.printer.*;
import com.openbravo.pos.forms.AppLocal;

public class DevicePrinterESCPOS implements DevicePrinter  {
      
    private PrinterWritter m_CommOutputPrinter;   
    private Codes m_codes;
    private UnicodeTranslator m_trans;
    
//    private boolean m_bInline;
    private String m_sName;
    
    // Creates new TicketPrinter
    public DevicePrinterESCPOS(PrinterWritter CommOutputPrinter, Codes codes, UnicodeTranslator trans) throws TicketPrinterException {
        
        m_sName = AppLocal.getIntString("Printer.Serial");
        m_CommOutputPrinter = CommOutputPrinter;
        m_codes = codes;
        m_trans = trans;

        // Inicializamos la impresora
        m_CommOutputPrinter.init(ESCPOS.INIT);

        m_CommOutputPrinter.write(ESCPOS.SELECT_PRINTER); // A la impresora
        m_CommOutputPrinter.init(m_codes.getInitSequence());
        m_CommOutputPrinter.write(m_trans.getCodeTable());

        m_CommOutputPrinter.flush();  
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
    }
    
    public void printImage(BufferedImage image) {
        
        m_CommOutputPrinter.write(ESCPOS.SELECT_PRINTER);        
        m_CommOutputPrinter.write(m_codes.transImage(image));
    }
    
    public void printBarCode(String type, String position, String code) {
        
        m_CommOutputPrinter.write(ESCPOS.SELECT_PRINTER);        
        m_codes.printBarcode(m_CommOutputPrinter, type, position, code);
    }
    
    public void beginLine(int iTextSize) {

        m_CommOutputPrinter.write(ESCPOS.SELECT_PRINTER);        
        
        if (iTextSize == DevicePrinter.SIZE_0) {
            m_CommOutputPrinter.write(m_codes.getSize0());
        } else if (iTextSize == DevicePrinter.SIZE_1) {
            m_CommOutputPrinter.write(m_codes.getSize1());
        } else if (iTextSize == DevicePrinter.SIZE_2) {
            m_CommOutputPrinter.write(m_codes.getSize2());
        } else if (iTextSize == DevicePrinter.SIZE_3) {
            m_CommOutputPrinter.write(m_codes.getSize3());
        } else {
            m_CommOutputPrinter.write(m_codes.getSize0());
        }
    }
    
    public void printText(int iStyle, String sText) {

        m_CommOutputPrinter.write(ESCPOS.SELECT_PRINTER);   

        if ((iStyle & DevicePrinter.STYLE_BOLD) != 0) {
            m_CommOutputPrinter.write(m_codes.getBoldSet());
        }
        if ((iStyle & DevicePrinter.STYLE_UNDERLINE) != 0) {
            m_CommOutputPrinter.write(m_codes.getUnderlineSet());
        }
        m_CommOutputPrinter.write(m_trans.transString(sText));
        if ((iStyle & DevicePrinter.STYLE_UNDERLINE) != 0) {
            m_CommOutputPrinter.write(m_codes.getUnderlineReset());
        }
        if ((iStyle & DevicePrinter.STYLE_BOLD) != 0) {
            m_CommOutputPrinter.write(m_codes.getBoldReset());
        }     
    }
    public void endLine() {
        m_CommOutputPrinter.write(ESCPOS.SELECT_PRINTER);   
        m_CommOutputPrinter.write(m_codes.getNewLine());
    }
    
    public void endReceipt() {
        m_CommOutputPrinter.write(ESCPOS.SELECT_PRINTER);   
        
        m_CommOutputPrinter.write(m_codes.getNewLine());
        m_CommOutputPrinter.write(m_codes.getNewLine());
        m_CommOutputPrinter.write(m_codes.getNewLine());
        m_CommOutputPrinter.write(m_codes.getNewLine());
        m_CommOutputPrinter.write(m_codes.getNewLine());

        m_CommOutputPrinter.write(m_codes.getCutReceipt());
        m_CommOutputPrinter.flush();
    }
    
    public void openDrawer() {

        m_CommOutputPrinter.write(ESCPOS.SELECT_PRINTER);   
        m_CommOutputPrinter.write(m_codes.getOpenDrawer());
        m_CommOutputPrinter.flush();
    }
}

