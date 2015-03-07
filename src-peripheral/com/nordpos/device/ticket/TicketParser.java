/**
 *
 * NORD POS is a fork of Openbravo POS.
 *
 * Copyright (C) 2009-2015 Nord Trading Ltd. <http://www.nordpos.com>
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
package com.nordpos.device.ticket;

import com.nordpos.device.display.DeviceDisplayBase;
import com.nordpos.device.receiptprinter.DevicePrinter;
import com.openbravo.data.loader.LocalRes;
import com.openbravo.pos.forms.DataLogicSystem;
import com.nordpos.device.fiscalprinter.DeviceFiscalPrinter;
import com.nordpos.device.labelprinter.DeviceLabelPrinter;
import com.nordpos.device.util.StringUtils;
import com.openbravo.pos.scripting.ScriptEngine;
import com.openbravo.pos.scripting.ScriptException;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Scanner;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @author Gennady Kovalev <gik@bigur.ru>
 * @author Artur Akchurin <akartkam@gmail.com>
 * @version NORD POS 3
 */
public class TicketParser extends DefaultHandler {

    private static SAXParser m_sp = null;
    private static XMLReader m_sr = null;

    private final DeviceTicketFactory printer;
    private DataLogicSystem m_system;

    private StringBuffer text;

    private String bctype;
    private String bcposition;
    private int m_iTextAlign;
    private int m_iTextLength;
    private int m_iTextStyle;

    private StringBuffer m_sVisorLine;
    private int m_iVisorAnimation;
    private String m_sVisorLine1;
    private String m_sVisorLine2;

    private double m_dValue1;
    private double m_dValue2;
    private int attribute3;

    private String m_sPaymentType;

    private Integer integerCharacterSize;
    private String sUnderline;
    private boolean bBold;

    private int m_iOutputType;
    private static final int OUTPUT_NONE = 0;
    private static final int OUTPUT_DISPLAY = 1;
    private static final int OUTPUT_TICKET = 2;
    private static final int OUTPUT_FISCAL = 3;
    private static final int OUTPUT_FISCALREP = 4;
    private static final int OUTPUT_FISCALCASH = 5;
    private static final int OUTPUT_LABEL = 6;

    private DevicePrinter m_oOutputPrinter;

    private DeviceFiscalPrinter m_oFiscalPrinter;

    private DeviceLabelPrinter m_oLabelPrinter;
    private String sLabelTextFontType;
    private String sLabelTextOrientation;
    private String sLabelTextX;
    private String sLabelTextY;
    private String sLabelTextFontHeight;
    private String sLabelTextFontWidth;
    private String sLabelTextFontWeight;

    private String sLabelBarcodeOrientation;
    private String sLabelBarcodeX;
    private String sLabelBarcodeY;
    private String sLabelBarcodeHeight;
    private String sLabelBarcodesDimension;

    private final InputStream shemaFile;

    public TicketParser(InputStream shemaFile, DeviceTicketFactory printer) {
        this.shemaFile = shemaFile;
        this.printer = printer;
    }

    public void printTicket(InputStream scriptFile, ScriptEngine script) throws TicketPrinterException, ScriptException {
        String sXML = new Scanner(scriptFile, "UTF-8").useDelimiter("\\A").next();
        printTicket(new StringReader(script.eval(sXML).toString()));
    }

    public void printTicket(Reader in) throws TicketPrinterException {
        try {
            if (m_sp == null) {
                SAXParserFactory spf = SAXParserFactory.newInstance();
                spf.setValidating(false);
                spf.setNamespaceAware(true);
                SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
                spf.setSchema(schemaFactory.newSchema(new Source[]{new StreamSource(shemaFile)}));
                m_sp = spf.newSAXParser();
                m_sr = m_sp.getXMLReader();
            }
            m_sr.setContentHandler(this);
            m_sr.parse(new InputSource(in));
        } catch (ParserConfigurationException ePC) {
            throw new TicketPrinterException(LocalRes.getIntString("exception.parserconfig"), ePC);
        } catch (SAXException eSAX) {
            throw new TicketPrinterException(LocalRes.getIntString("exception.xmlfile"), eSAX);
        } catch (IOException eIO) {
            throw new TicketPrinterException(LocalRes.getIntString("exception.iofile"), eIO);
        }
    }

    @Override
    public void startDocument() throws SAXException {
        // inicalizo las variables pertinentes
        text = null;
        bctype = null;
        bcposition = null;
        m_sVisorLine = null;
        m_iVisorAnimation = DeviceDisplayBase.ANIMATION_NULL;
        m_sVisorLine1 = null;
        m_sVisorLine2 = null;
        m_iOutputType = OUTPUT_NONE;
        m_oOutputPrinter = null;
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        switch (m_iOutputType) {
            case OUTPUT_NONE:
                if ("opendrawer".equals(qName)) {
                    printer.getDevicePrinter(readString(attributes.getValue("printer"), "1")).openDrawer();
                } else if ("play".equals(qName)) {
                    text = new StringBuffer();
                } else if ("ticket".equals(qName)) {
                    m_iOutputType = OUTPUT_TICKET;
                    m_oOutputPrinter = printer.getDevicePrinter(readString(attributes.getValue("printer"), "1"));
                    m_oOutputPrinter.beginReceipt();
                } else if ("display".equals(qName)) {
                    m_iOutputType = OUTPUT_DISPLAY;
                    String animation = readString(attributes.getValue("animation"), "none");
                    if ("scroll".equals(animation)) {
                        m_iVisorAnimation = DeviceDisplayBase.ANIMATION_SCROLL;
                    } else if ("flyer".equals(animation)) {
                        m_iVisorAnimation = DeviceDisplayBase.ANIMATION_FLYER;
                    } else if ("blink".equals(animation)) {
                        m_iVisorAnimation = DeviceDisplayBase.ANIMATION_BLINK;
                    } else if ("curtain".equals(animation)) {
                        m_iVisorAnimation = DeviceDisplayBase.ANIMATION_CURTAIN;
                    } else { // "none"
                        m_iVisorAnimation = DeviceDisplayBase.ANIMATION_NULL;
                    }
                    m_sVisorLine1 = null;
                    m_sVisorLine2 = null;
                    m_oOutputPrinter = null;
                } else if ("fiscalreceipt".equals(qName)) {
                    try {
                        m_iOutputType = OUTPUT_FISCAL;
                        m_oFiscalPrinter = printer.getFiscalPrinter();
                        m_oFiscalPrinter.beginReceipt(
                                readString(attributes.getValue("type"), "sale"),
                                parseInt(attributes.getValue("number")),
                                readString(attributes.getValue("date"), "01.01.2012"),
                                readString(attributes.getValue("time"), "00:00"),
                                readString(attributes.getValue("cashier"), "Администратор"));
                    } catch (TicketPrinterException ex) {
                        throw new SAXException(ex);
                    }
                } else if ("fiscalreport".equals(qName)) {
                    m_iOutputType = OUTPUT_FISCALREP;
                } else if ("fiscalcash".equals(qName)) {
                    m_iOutputType = OUTPUT_FISCALCASH;
                } else if ("label".equals(qName)) {
                    m_iOutputType = OUTPUT_LABEL;
                    m_oLabelPrinter = printer.getLabelPrinter();
                    m_oLabelPrinter.beginLabel(
                            readString(attributes.getValue("charset"), ""),
                            readString(attributes.getValue("length"), "0"),
                            readString(attributes.getValue("width"), "0"),
                            readString(attributes.getValue("gap"), "0"),
                            readString(attributes.getValue("x"), "0"),
                            readString(attributes.getValue("y"), "0"),
                            readString(attributes.getValue("rotation"), "0"),
                            readString(attributes.getValue("resolution"), ""));
                }
                break;
            case OUTPUT_TICKET:
                switch (qName) {
                    case "image":
                        text = new StringBuffer();
                        break;
                    case "line":
                        m_oOutputPrinter.beginLine(parseInt(attributes.getValue("size"), 0));
                        break;
                    case "barcode":
                        text = new StringBuffer();
                        bctype = readString(attributes.getValue("type"), "EAN13");
                        bcposition = readString(attributes.getValue("position"), "bottom");
                        break;
                    case "text":
                        text = new StringBuffer();
                        integerCharacterSize = parseInteger(attributes.getValue("size"));
                        sUnderline = readString(attributes.getValue("underline"));
                        bBold = attributes.getValue("bold").equals("true");
                        String sAlign = readString(attributes.getValue("align"));
                        switch (sAlign) {
                            case "right":
                                m_iTextAlign = DevicePrinter.ALIGN_RIGHT;
                                break;
                            case "center":
                                m_iTextAlign = DevicePrinter.ALIGN_CENTER;
                                break;
                            default:
                                m_iTextAlign = DevicePrinter.ALIGN_LEFT;
                                break;
                        }
                        m_iTextLength = parseInt(attributes.getValue("length"), 0);
                    case "cutpaper":
                        m_oOutputPrinter.cutPaper(readBoolean(attributes.getValue("complete"), true));
                }
                break;
            case OUTPUT_DISPLAY:
                if ("line".equals(qName)) { // line 1 or 2 of the display
                    m_sVisorLine = new StringBuffer();
                } else if ("line1".equals(qName)) { // linea 1 del visor
                    m_sVisorLine = new StringBuffer();
                } else if ("line2".equals(qName)) { // linea 2 del visor
                    m_sVisorLine = new StringBuffer();
                } else if ("text".equals(qName)) {
                    text = new StringBuffer();
                    String sAlign = readString(attributes.getValue("align"), "center");
                    if ("right".equals(sAlign)) {
                        m_iTextAlign = DevicePrinter.ALIGN_RIGHT;
                    } else if ("center".equals(sAlign)) {
                        m_iTextAlign = DevicePrinter.ALIGN_CENTER;
                    } else {
                        m_iTextAlign = DevicePrinter.ALIGN_LEFT;
                    }
                    m_iTextLength = parseInt(attributes.getValue("length"), 0);
                }
                break;
            case OUTPUT_FISCAL:
                if ("line".equals(qName)) {
                    text = new StringBuffer();
                    m_dValue1 = parseDouble(attributes.getValue("price"), 0.0);
                    m_dValue2 = parseDouble(attributes.getValue("units"), 1.0);
                    attribute3 = parseInt(attributes.getValue("tax"), 0);
                } else if ("message".equals(qName)) {
                    text = new StringBuffer();
                } else if ("total".equals(qName)) {
                    text = new StringBuffer();
                    m_dValue1 = parseDouble(attributes.getValue("paid"), 0.0);
                    m_sPaymentType = readString(attributes.getValue("type"), "cash");
                } else if ("cutpaper".equals(qName)) {
                    try {
                        m_oFiscalPrinter.cutPaper(readBoolean(attributes.getValue("complete"), true));
                    } catch (TicketPrinterException ex) {
                        throw new SAXException(ex);
                    }
                }
                break;
            case OUTPUT_FISCALREP:
                try {
                    m_oFiscalPrinter = printer.getFiscalPrinter();
                    if ("fiscalzreport".equals(qName)) {
                        m_oFiscalPrinter.printZReport();
                    } else if ("fiscalxreport".equals(qName)) {
                        m_oFiscalPrinter.printXReport();
                    } else if ("cutpaper".equals(qName)) {
                        m_oFiscalPrinter.cutPaper(readBoolean(attributes.getValue("complete"), true));
                    }
                } catch (TicketPrinterException ex) {
                    throw new SAXException(ex);
                }
            case OUTPUT_FISCALCASH:
                try {
                    m_oFiscalPrinter = printer.getFiscalPrinter();
                    if ("fiscalcashin".equals(qName)) {
                        m_oFiscalPrinter.printCashIn(parseDouble(attributes.getValue("value"), 0.0));
                    } else if ("fiscalcashout".equals(qName)) {
                        m_oFiscalPrinter.printCashOut(parseDouble(attributes.getValue("value"), 0.0));
                    } else if ("cutpaper".equals(qName)) {
                        m_oFiscalPrinter.cutPaper(readBoolean(attributes.getValue("complete"), true));
                    }
                } catch (TicketPrinterException ex) {
                    throw new SAXException(ex);
                }
            case OUTPUT_LABEL:
                if ("text".equals(qName)) {
                    text = new StringBuffer();
                    sLabelTextFontType = readString(attributes.getValue("font"), "");
                    sLabelTextOrientation = readString(attributes.getValue("orientation"), "0");
                    sLabelTextX = readString(attributes.getValue("x"), "0");
                    sLabelTextY = readString(attributes.getValue("y"), "0");
                    sLabelTextFontHeight = readString(attributes.getValue("font-height"), "1");
                    sLabelTextFontWidth = readString(attributes.getValue("font-width"), "1");
                    sLabelTextFontWeight = readString(attributes.getValue("font-weight"), "");
                } else if ("barcode".equals(qName)) {
                    text = new StringBuffer();
                    sLabelBarcodeOrientation = readString(attributes.getValue("orientation"), "0");
                    sLabelBarcodeX = readString(attributes.getValue("x"), "0");
                    sLabelBarcodeY = readString(attributes.getValue("y"), "0");
                    sLabelBarcodeHeight = readString(attributes.getValue("height"), "1");
                    bctype = readString(attributes.getValue("type"), "EAN13");
                    bcposition = readString(attributes.getValue("position"), "bottom");
                    sLabelBarcodesDimension = readString(attributes.getValue("dimension"));
                } else if ("rectangle".equals(qName)) {
                    m_oLabelPrinter.drawRectangle(
                            readString(attributes.getValue("fill"), ""),
                            readString(attributes.getValue("x-begin"), "0"),
                            readString(attributes.getValue("y-begin"), "0"),
                            readString(attributes.getValue("x-end"), "0"),
                            readString(attributes.getValue("y-end"), "0"));
                } else if ("line".equals(qName)) {
                    m_oLabelPrinter.drawLine(
                            readString(attributes.getValue("thickness"), "1"),
                            readString(attributes.getValue("x-begin"), "0"),
                            readString(attributes.getValue("y-begin"), "0"),
                            readString(attributes.getValue("x-end"), "0"),
                            readString(attributes.getValue("y-end"), "0"));
                } else if ("frame".equals(qName)) {
                    m_oLabelPrinter.drawFrame(
                            readString(attributes.getValue("thickness"), "1"),
                            readString(attributes.getValue("x-begin"), "0"),
                            readString(attributes.getValue("y-begin"), "0"),
                            readString(attributes.getValue("x-end"), "0"),
                            readString(attributes.getValue("y-end"), "0"));
                }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        switch (m_iOutputType) {
            case OUTPUT_NONE:
                if ("play".equals(qName)) {
                    try {
                        AudioClip oAudio = Applet.newAudioClip(getClass().getClassLoader().getResource(text.toString()));
                        oAudio.play();
                    } catch (Exception fnfe) {
                        //throw new ResourceNotFoundException( fnfe.getMessage() );
                    }
                    text = null;
                }
                break;
            case OUTPUT_TICKET:
                switch (qName) {
                    case "image":
                        try {
                            // BufferedImage image = ImageIO.read(getClass().getClassLoader().getResourceAsStream(m_sText.toString()));
                            BufferedImage image = m_system.getResourceAsImage(text.toString());
                            if (image != null) {
                                m_oOutputPrinter.printImage(image);
                            }
                        } catch (Exception fnfe) {
                            //throw new ResourceNotFoundException( fnfe.getMessage() );
                        }
                        text = null;
                        break;
                    case "barcode":
                        m_oOutputPrinter.printBarCode(
                                bctype,
                                bcposition,
                                readString(text.toString(), "0"));
                        text = null;
                        break;
                    case "text":
                        if (m_iTextLength > 0) {
                            switch (m_iTextAlign) {
                                case DevicePrinter.ALIGN_RIGHT:
                                    m_oOutputPrinter.printText(integerCharacterSize, sUnderline, bBold, StringUtils.alignRight(text.toString(), m_iTextLength));
                                    break;
                                case DevicePrinter.ALIGN_CENTER:
                                    m_oOutputPrinter.printText(integerCharacterSize, sUnderline, bBold, StringUtils.alignCenter(text.toString(), m_iTextLength));
                                    break;
                                default:
                                    m_oOutputPrinter.printText(integerCharacterSize, sUnderline, bBold, StringUtils.alignLeft(text.toString(), m_iTextLength));
                                    break;
                            }
                        } else {
                            m_oOutputPrinter.printText(integerCharacterSize, sUnderline, bBold, text.toString());
                        }
                        text = null;
                        break;
                    case "line":
                        m_oOutputPrinter.endLine();
                        break;
                    case "ticket":
                        m_oOutputPrinter.endReceipt();
                        m_iOutputType = OUTPUT_NONE;
                        m_oOutputPrinter = null;
                        break;
                }
                break;
            case OUTPUT_DISPLAY:
                if ("line".equals(qName)) { // line 1 or 2 of the display
                    if (m_sVisorLine1 == null) {
                        m_sVisorLine1 = m_sVisorLine.toString();
                    } else {
                        m_sVisorLine2 = m_sVisorLine.toString();
                    }
                    m_sVisorLine = null;
                } else if ("line1".equals(qName)) { // linea 1 del visor
                    m_sVisorLine1 = m_sVisorLine.toString();
                    m_sVisorLine = null;
                } else if ("line2".equals(qName)) { // linea 2 del visor
                    m_sVisorLine2 = m_sVisorLine.toString();
                    m_sVisorLine = null;
                } else if ("text".equals(qName)) {
                    if (m_iTextLength > 0) {
                        switch (m_iTextAlign) {
                            case DevicePrinter.ALIGN_RIGHT:
                                m_sVisorLine.append(StringUtils.alignRight(text.toString(), m_iTextLength));
                                break;
                            case DevicePrinter.ALIGN_CENTER:
                                m_sVisorLine.append(StringUtils.alignCenter(text.toString(), m_iTextLength));
                                break;
                            default: // DevicePrinter.ALIGN_LEFT
                                m_sVisorLine.append(StringUtils.alignLeft(text.toString(), m_iTextLength));
                                break;
                        }
                    } else {
                        m_sVisorLine.append(text);
                    }
                    text = null;
                } else if ("display".equals(qName)) {
                    printer.getDeviceDisplay().writeVisor(m_iVisorAnimation, m_sVisorLine1, m_sVisorLine2);
                    m_iVisorAnimation = DeviceDisplayBase.ANIMATION_NULL;
                    m_sVisorLine1 = null;
                    m_sVisorLine2 = null;
                    m_iOutputType = OUTPUT_NONE;
                    m_oOutputPrinter = null;
                }
                break;
            case OUTPUT_FISCAL:
                try {
                    if ("fiscalreceipt".equals(qName)) {
                        m_oFiscalPrinter.endReceipt();
                        m_iOutputType = OUTPUT_NONE;
                    } else if ("line".equals(qName)) {
                        m_oFiscalPrinter.printLine(text.toString(), m_dValue1, m_dValue2, attribute3);
                        text = null;
                    } else if ("message".equals(qName)) {
                        m_oFiscalPrinter.printMessage(text.toString());
                        text = null;
                    } else if ("total".equals(qName)) {
                        m_oFiscalPrinter.printTotal(text.toString(), m_dValue1, m_sPaymentType);
                        text = null;
                    }
                } catch (TicketPrinterException ex) {
                    throw new SAXException(ex);
                }
                break;
            case OUTPUT_FISCALREP:
                if ("fiscalreport".equals(qName)) {
                    m_iOutputType = OUTPUT_NONE;
                }
                break;
            case OUTPUT_FISCALCASH:
                if ("fiscalcash".equals(qName)) {
                    m_iOutputType = OUTPUT_NONE;
                }
                break;
            case OUTPUT_LABEL:
                if ("label".equals(qName)) {
                    m_oLabelPrinter.endLabel();
                    m_iOutputType = OUTPUT_NONE;
                } else if ("text".equals(qName)) {
                    m_oLabelPrinter.drawText(
                            sLabelTextFontType,
                            sLabelTextOrientation,
                            sLabelTextX,
                            sLabelTextY,
                            sLabelTextFontWidth,
                            sLabelTextFontHeight,
                            sLabelTextFontWeight,
                            readString(text.toString(), "???"));
                    text = null;
                } else if ("barcode".equals(qName)) {                    
                    m_oLabelPrinter.drawBarCode(
                            bctype,
                            bcposition,
                            sLabelBarcodeOrientation,
                            sLabelBarcodeX,
                            sLabelBarcodeY,
                            sLabelBarcodeHeight,
                            sLabelBarcodesDimension,
                            readString(text.toString(), "???"));
                    text = null;
                }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (text != null) {
            text.append(ch, start, length);
        }
    }

    private int parseInt(String sValue, int iDefault) {
        try {
            return Integer.parseInt(sValue);
        } catch (NumberFormatException eNF) {
            return iDefault;
        }
    }

    private int parseInt(String sValue) {
        return parseInt(sValue, 0);
    }

    private Integer parseInteger(String sValue) {
        try {
            return Integer.parseInt(sValue);
        } catch (NumberFormatException eNF) {
            return null;
        }
    }

    private double parseDouble(String sValue, double ddefault) {
        try {
            return Double.parseDouble(sValue);
        } catch (NumberFormatException eNF) {
            return ddefault;
        }
    }

    private double parseDouble(String sValue) {
        return parseDouble(sValue, 0.0);
    }

    private String readString(String sValue) {
        if (sValue == null || sValue.equals("")) {
            return null;
        } else {
            return sValue;
        }
    }

    private String readString(String sValue, String sDefault) {
        if (sValue == null || sValue.equals("")) {
            return sDefault;
        } else {
            return sValue;
        }
    }

    private boolean readBoolean(String sValue, boolean bDefault) {
        if (sValue == null || sValue.equals("")) {
            return bDefault;
        } else {
            return Boolean.parseBoolean(sValue);
        }
    }
}
