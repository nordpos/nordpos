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

package com.openbravo.pos.printer.ticket;

import com.openbravo.pos.printer.DevicePrinter;
import com.openbravo.pos.util.BarcodeString;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.impl.qr.QRCodeBean;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.impl.upcean.EAN8Bean;
import org.krysalis.barcode4j.output.java2d.Java2DCanvasProvider;

public class PrintItemBarcode implements PrintItem {

    protected AbstractBarcodeBean m_barcode;
    protected QRCodeBean m_qrcode;
    protected String m_sCode;
    protected int m_iWidth;
    protected int m_iHeight;
    protected double scale;

    /** Creates a new instance of PrinterItemBarcode */
    public PrintItemBarcode(String type, String position, String code, double scale) {

        m_sCode = code;
        this.scale = scale;
        
        if (DevicePrinter.BARCODE_CODE128.equals(type)) {
            m_barcode = new Code128Bean();
            m_sCode = BarcodeString.getBarcodeStringCode128(m_sCode);               
        } else if (DevicePrinter.BARCODE_CODE39.equals(type)) {
            m_barcode = new Code39Bean();
            m_sCode = BarcodeString.getBarcodeStringCode39(m_sCode);           
        } else if (DevicePrinter.BARCODE_EAN8.equals(type)) {
            m_barcode = new EAN8Bean();
            m_sCode = BarcodeString.getBarcodeStringEAN8(m_sCode);
        } else if (DevicePrinter.BARCODE_DATAMATRIX.equals(type)) {
            m_barcode = new DataMatrixBean();
            m_sCode = BarcodeString.getBarcodeStringDataMatrix(m_sCode);
        } else if (DevicePrinter.BARCODE_QRCODE.equals(type)) {
            m_qrcode = new QRCodeBean();
            m_sCode = BarcodeString.getBarcodeStringQRCode(m_sCode);
            m_qrcode.setEncoding("Cp1251");
            m_barcode = m_qrcode;
        } else {
            m_barcode = new EAN13Bean();
            m_sCode = BarcodeString.getBarcodeStringEAN13(m_sCode);
        }

        if (m_barcode != null) {
            if (DevicePrinter.BARCODE_DATAMATRIX.equals(type) || DevicePrinter.BARCODE_QRCODE.equals(type)) {
               m_barcode.setModuleWidth(5.0);
               BarcodeDimension dim = m_barcode.calcDimensions(m_sCode);
               m_iWidth = (int) dim.getWidth(0);
               m_iHeight = (int) dim.getHeight(0);                
            } else {
                m_barcode.setModuleWidth(1.0);
                m_barcode.setBarHeight(40.0);
                m_barcode.setFontSize(10.0);
                m_barcode.setQuietZone(10.0);
                m_barcode.doQuietZone(true);
                if (DevicePrinter.POSITION_NONE.equals(position)) {
                    m_barcode.setMsgPosition(HumanReadablePlacement.HRP_NONE);
                } else if (DevicePrinter.POSITION_TOP.equals(position)) {
                    m_barcode.setMsgPosition(HumanReadablePlacement.HRP_TOP);
                } else {
                    m_barcode.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);
                }
                BarcodeDimension dim = m_barcode.calcDimensions(m_sCode);
                m_iWidth = (int) dim.getWidth(0);
                m_iHeight = (int) dim.getHeight(0);
            }
        }
    }

    public void draw(Graphics2D g, int x, int y, int width) {

        if (m_barcode != null) {
            Graphics2D g2d = (Graphics2D) g;

            AffineTransform oldt = g2d.getTransform();
        
            g2d.translate(x - 10 + (width - (int)(m_iWidth * scale)) / 2, y + 10);
            g2d.scale(scale, scale);

            try {
                m_barcode.generateBarcode(new Java2DCanvasProvider(g2d, 0), m_sCode);
            } catch (IllegalArgumentException e) {
                g2d.drawRect(0, 0, m_iWidth, m_iHeight);
                g2d.drawLine(0, 0, m_iWidth, m_iHeight);
                g2d.drawLine(m_iWidth, 0, 0, m_iHeight);
            }

            g2d.setTransform(oldt);
        }
    }

    public int getHeight() {
        return (int) (m_iHeight * scale) + 20;
    }
    
//    public final String transSymbolCode39(String sCad) {
//
//
//    }
}