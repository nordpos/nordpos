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

import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.nordpos.device.util.BarcodeString;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.impl.upcean.EAN8Bean;
import org.krysalis.barcode4j.output.java2d.Java2DCanvasProvider;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3.0
 */
public class PrintItemBarcode implements PrintItem {

    protected AbstractBarcodeBean m_barcode;
    protected BitMatrix m_qrMatrix;
    protected String m_sCode;
    protected int m_iWidth;
    protected int m_iHeight;
    protected double scale;

    public PrintItemBarcode(String type, String position, String code, double scale) {

        m_sCode = code;
        this.scale = scale;

        if (null != type) {
            switch (type) {
                case DevicePrinter.BARCODE_CODE128:
                    m_barcode = new Code128Bean();
                    m_sCode = BarcodeString.getBarcodeStringCode128(m_sCode);
                    break;
                case DevicePrinter.BARCODE_CODE39:
                    m_barcode = new Code39Bean();
                    m_sCode = BarcodeString.getBarcodeStringCode39(m_sCode);
                    break;
                case DevicePrinter.BARCODE_EAN8:
                    m_barcode = new EAN8Bean();
                    m_sCode = BarcodeString.getBarcodeStringEAN8(m_sCode);
                    break;
                case DevicePrinter.BARCODE_DATAMATRIX:
                    m_barcode = new DataMatrixBean();
                    m_sCode = BarcodeString.getBarcodeStringDataMatrix(m_sCode);
                    break;
                case DevicePrinter.BARCODE_QRCODE:
                    m_sCode = BarcodeString.getBarcodeStringQRCode(m_sCode);
                    m_qrMatrix = new BitMatrix(128);
                    m_iWidth = m_qrMatrix.getWidth();
                    m_iHeight = m_iWidth;
                    break;
                default:
                    m_barcode = new EAN13Bean();
                    m_sCode = BarcodeString.getBarcodeStringEAN13(m_sCode);
                    break;
            }
        }

        if (m_barcode != null) {
            if (DevicePrinter.BARCODE_DATAMATRIX.equals(type)) {
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
                if (null != position) {
                    switch (position) {
                        case DevicePrinter.POSITION_NONE:
                            m_barcode.setMsgPosition(HumanReadablePlacement.HRP_NONE);
                            break;
                        case DevicePrinter.POSITION_TOP:
                            m_barcode.setMsgPosition(HumanReadablePlacement.HRP_TOP);
                            break;
                        default:
                            m_barcode.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);
                            break;
                    }
                }
                BarcodeDimension dim = m_barcode.calcDimensions(m_sCode);
                m_iWidth = (int) dim.getWidth(0);
                m_iHeight = (int) dim.getHeight(0);
            }
        }
    }

    @Override
    public void draw(Graphics2D g, int x, int y, int width) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldt = g2d.getTransform();
        g2d.translate(x - 10 + (width - (int) (m_iWidth * scale)) / 2, y + 10);
        g2d.scale(scale, scale);

        try {
            if (m_qrMatrix != null) {
                com.google.zxing.Writer writer = new QRCodeWriter();
                m_qrMatrix = writer.encode(m_sCode, com.google.zxing.BarcodeFormat.QR_CODE, m_iWidth, m_iHeight);
                g2d.drawImage(MatrixToImageWriter.toBufferedImage(m_qrMatrix), null, 0, 0);
            } else if (m_barcode != null) {
                m_barcode.generateBarcode(new Java2DCanvasProvider(g2d, 0), m_sCode);
            }
        } catch (IllegalArgumentException | WriterException ex) {
            g2d.drawRect(0, 0, m_iWidth, m_iHeight);
            g2d.drawLine(0, 0, m_iWidth, m_iHeight);
            g2d.drawLine(m_iWidth, 0, 0, m_iHeight);
        }

        g2d.setTransform(oldt);
    }

    @Override
    public int getHeight() {
        return (int) (m_iHeight * scale) + 20;
    }
}
