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

package com.openbravo.pos.printer.printer;

import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.pos.forms.AppLocal;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import com.openbravo.pos.printer.DevicePrinter;
import com.openbravo.pos.printer.ticket.BasicTicket;
import com.openbravo.pos.printer.ticket.BasicTicketForPrinter;
import com.openbravo.pos.util.ReportUtils;
import com.openbravo.pos.util.SelectPrinter;
import java.awt.Component;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;

/**
 *Class DevicePrinterPrinter is responsible for printing tickets using system <br>
 * printers. It takes into consideration if a user set a printer as a receipt <br>
 * printer or not. 
 * <p>For receipt printers lenght of a receipt must be calculated in this class.</p>
 * <p>For normal printers number of pages must be calculated dynamically in the <br>
 * class PrintableTicket @see com.openbravo.pos.printer.printer.PrintableTicket
 *  
 * @author jaroslawwozniak
 */
public class DevicePrinterPrinter implements DevicePrinter {

    private static Logger logger = Logger.getLogger("com.openbravo.pos.printer.printer.DevicePrinterPrinter");

    private Component parent;
    /*name of a printer*/
    private String m_sName;
    /*a ticket to print*/
    private BasicTicket m_ticketcurrent;
    /*system printer*/
    private PrintService printservice;

//    // For Page Size 72mm x 200mm && MediaSizeName A4.
//    private static final int imageable_width = 190;
//    private static final int imageable_height = 546;
//    private static final int imageable_x = 10;
//    private static final int imageable_y = 287;
//    private static final Media media = MediaSizeName.ISO_A4;

//    // For Page Size A4 && MediaSizeName A4.
//    private static final int imageable_width = 451;
//    private static final int imageable_height = 698;
//    private static final int imageable_x = 72;
//    private static final int imageable_y = 72;
//    private static final Media media = MediaSizeName.ISO_A4;

    private int imageable_width;
    private int imageable_height;
    private int imageable_x;
    private int imageable_y;
    private Media media;

    private static final HashMap<String, MediaSizeName> mediasizenamemap = new HashMap<String, MediaSizeName>();

    /** 
     * Creates a new instance of DevicePrinterPrinter
     * 
     * @param printername - name of printer that will be called in the system
     * @param isReceiptPrinter - string with boolean values if the printer is a receipt
     */
    public DevicePrinterPrinter(Component parent, String printername, int imageable_x, int imageable_y, int imageable_width, int imageable_height, String mediasizename) {

        this.parent = parent;
        m_sName = "Printer"; // "AppLocal.getIntString("Printer.Screen");
        m_ticketcurrent = null;
        printservice = ReportUtils.getPrintService(printername);

        this.imageable_x = imageable_x;
        this.imageable_y = imageable_y;
        this.imageable_width = imageable_width;
        this.imageable_height = imageable_height;
        this.media = getMedia(mediasizename);
    }

    /**
     * Getter that returns the name of a printer
     *
     * @return m_sName a name of a printer
     */
    @Override
    public String getPrinterName() {
        return m_sName;
    }

    /**
     * Getter that returns the description of a printer
     *
     * @return decription of a printer
     */
    @Override
    public String getPrinterDescription() {
        return null;
    }

    /**
     * Getter that returns the printer's component
     *
     * @return printer's component
     */
    @Override
    public JComponent getPrinterComponent() {
        return null;
    }

    /**
     * Method that sets the current ticket as a null
     */
    @Override
    public void reset() {
        m_ticketcurrent = null;
    }

    /**
     * Method that is responsible for start a new ticket
     */
    @Override
    public void beginReceipt() {
        m_ticketcurrent = new BasicTicketForPrinter();
    }

    /**
     * Method that is responsible for printing an image
     *
     * @param image a buffered image object
     */
    @Override
    public void printImage(BufferedImage image) {
        m_ticketcurrent.printImage(image);
    }

    /**
     * Method that is responsible for printing a barcode
     *
     * @param type a type of a barcode
     * @param position coordinates of a barcode on a receipt
     * @param code the code of a productmiale
     */
    @Override
    public void printBarCode(String type, String position, String code) {
        m_ticketcurrent.printBarCode(type, position, code);
    }

    /**
     * Method that is responsible for starting a new line on a receipt
     *
     * @param iTextSize a size of text in the line
     */
    @Override
    public void beginLine(int iTextSize) {
        m_ticketcurrent.beginLine(iTextSize);
    }

    /**
     * Method that is responsible for printing text
     *
     * @param iStyle style of text
     * @param sText text to print
     */
    @Override
    public void printText(int iStyle, String sText) {
        m_ticketcurrent.printText(iStyle, sText);
    }

    /**
     * Method that is responsible for ending a line
     */
    @Override
    public void endLine() {
        m_ticketcurrent.endLine();
    }

    /**
     * Method that is responsible for ending and printing a ticket<br>
     * It manages to get a printerJob, set the name of the job, get a Book object<br>
     * and print the receipt
     */
    @Override
    public void endReceipt() {

        try {

            PrintService ps;

            if (printservice == null) {
                String[] printers = ReportUtils.getPrintNames();
                if (printers.length == 0) {
                    logger.warning(AppLocal.getIntString("message.noprinters"));
                    ps = null;
                } else {
                    SelectPrinter selectprinter = SelectPrinter.getSelectPrinter(parent, printers);
                    selectprinter.setVisible(true);
                    if (selectprinter.isOK()) {
                        ps = ReportUtils.getPrintService(selectprinter.getPrintService());
                    } else {
                        ps = null;
                    }
                }
            } else {
                ps = printservice;
            }

            if (ps != null)  {

                PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
                aset.add(OrientationRequested.PORTRAIT);
                aset.add(new JobName(AppLocal.APP_NAME + " - Document", null));
                aset.add(media);

                DocPrintJob printjob = ps.createPrintJob();
                Doc doc = new SimpleDoc(new PrintableBasicTicket(m_ticketcurrent, imageable_x, imageable_y, imageable_width, imageable_height), DocFlavor.SERVICE_FORMATTED.PRINTABLE, null);

                printjob.print(doc, aset);
            }

        } catch (PrintException ex) {
            logger.log(Level.WARNING, AppLocal.getIntString("message.printererror"), ex);
            JMessageDialog.showMessage(parent, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.printererror"), ex));
        }

        //ticket is not needed any more
        m_ticketcurrent = null;
    }

    /**
     * Method that is responsible for opening a drawer
     */
    @Override
    public void openDrawer() {
        // Una simulacion
        Toolkit.getDefaultToolkit().beep();
    }

    private static MediaSizeName getMedia(String mediasizename) {
        return mediasizenamemap.get(mediasizename);
    }

    static {
      mediasizenamemap.put("Postcard", MediaSizeName.JAPANESE_POSTCARD);
      mediasizenamemap.put("Statement", MediaSizeName.INVOICE);

      mediasizenamemap.put("Letter", MediaSizeName.NA_LETTER);
      mediasizenamemap.put("Executive", MediaSizeName.EXECUTIVE);
      mediasizenamemap.put("Legal", MediaSizeName.NA_LEGAL);

      mediasizenamemap.put("A0", MediaSizeName.ISO_A0);
      mediasizenamemap.put("A1", MediaSizeName.ISO_A1);
      mediasizenamemap.put("A2", MediaSizeName.ISO_A2);
      mediasizenamemap.put("A3", MediaSizeName.ISO_A3);
      mediasizenamemap.put("A4", MediaSizeName.ISO_A4);
      mediasizenamemap.put("A5", MediaSizeName.ISO_A5);
      mediasizenamemap.put("A6", MediaSizeName.ISO_A6);
      mediasizenamemap.put("A7", MediaSizeName.ISO_A7);
      mediasizenamemap.put("A8", MediaSizeName.ISO_A8);
      mediasizenamemap.put("A9", MediaSizeName.ISO_A9);
      mediasizenamemap.put("A10", MediaSizeName.ISO_A10);

      mediasizenamemap.put("B0", MediaSizeName.JIS_B0);
      mediasizenamemap.put("B1", MediaSizeName.JIS_B1);
      mediasizenamemap.put("B2", MediaSizeName.JIS_B2);
      mediasizenamemap.put("B3", MediaSizeName.JIS_B3);
      mediasizenamemap.put("B4", MediaSizeName.JIS_B4);
      mediasizenamemap.put("B5", MediaSizeName.JIS_B5);
      mediasizenamemap.put("B6", MediaSizeName.JIS_B6);
      mediasizenamemap.put("B7", MediaSizeName.JIS_B7);
      mediasizenamemap.put("B8", MediaSizeName.JIS_B8);
      mediasizenamemap.put("B9", MediaSizeName.JIS_B9);
      mediasizenamemap.put("B10", MediaSizeName.JIS_B10);

      mediasizenamemap.put("ISOB0", MediaSizeName.ISO_B0);
      mediasizenamemap.put("ISOB1", MediaSizeName.ISO_B1);
      mediasizenamemap.put("ISOB2", MediaSizeName.ISO_B2);
      mediasizenamemap.put("ISOB3", MediaSizeName.ISO_B3);
      mediasizenamemap.put("ISOB4", MediaSizeName.ISO_B4);
      mediasizenamemap.put("ISOB5", MediaSizeName.ISO_B5);
      mediasizenamemap.put("ISOB6", MediaSizeName.ISO_B6);
      mediasizenamemap.put("ISOB7", MediaSizeName.ISO_B7);
      mediasizenamemap.put("ISOB8", MediaSizeName.ISO_B8);
      mediasizenamemap.put("ISOB9", MediaSizeName.ISO_B9);
      mediasizenamemap.put("ISOB10", MediaSizeName.ISO_B10);
      mediasizenamemap.put("EnvISOB0", MediaSizeName.ISO_B0);
      mediasizenamemap.put("EnvISOB1", MediaSizeName.ISO_B1);
      mediasizenamemap.put("EnvISOB2", MediaSizeName.ISO_B2);
      mediasizenamemap.put("EnvISOB3", MediaSizeName.ISO_B3);
      mediasizenamemap.put("EnvISOB4", MediaSizeName.ISO_B4);
      mediasizenamemap.put("EnvISOB5", MediaSizeName.ISO_B5);
      mediasizenamemap.put("EnvISOB6", MediaSizeName.ISO_B6);
      mediasizenamemap.put("EnvISOB7", MediaSizeName.ISO_B7);
      mediasizenamemap.put("EnvISOB8", MediaSizeName.ISO_B8);
      mediasizenamemap.put("EnvISOB9", MediaSizeName.ISO_B9);
      mediasizenamemap.put("EnvISOB10", MediaSizeName.ISO_B10);

      mediasizenamemap.put("C0", MediaSizeName.ISO_C0);
      mediasizenamemap.put("C1", MediaSizeName.ISO_C1);
      mediasizenamemap.put("C2", MediaSizeName.ISO_C2);
      mediasizenamemap.put("C3", MediaSizeName.ISO_C3);
      mediasizenamemap.put("C4", MediaSizeName.ISO_C4);
      mediasizenamemap.put("C5", MediaSizeName.ISO_C5);
      mediasizenamemap.put("C6", MediaSizeName.ISO_C6);

      mediasizenamemap.put("EnvPersonal", MediaSizeName.PERSONAL_ENVELOPE);
      mediasizenamemap.put("EnvMonarch", MediaSizeName.MONARCH_ENVELOPE);
      mediasizenamemap.put("Monarch", MediaSizeName.MONARCH_ENVELOPE);
      mediasizenamemap.put("Env9", MediaSizeName.NA_NUMBER_9_ENVELOPE);
      mediasizenamemap.put("Env10", MediaSizeName.NA_NUMBER_10_ENVELOPE);
      mediasizenamemap.put("Env11", MediaSizeName.NA_NUMBER_11_ENVELOPE);
      mediasizenamemap.put("Env12", MediaSizeName.NA_NUMBER_12_ENVELOPE);
      mediasizenamemap.put("Env14", MediaSizeName.NA_NUMBER_14_ENVELOPE);
      mediasizenamemap.put("c8x10", MediaSizeName.NA_8X10);

      mediasizenamemap.put("EnvDL", MediaSizeName.ISO_DESIGNATED_LONG);
      mediasizenamemap.put("DL", MediaSizeName.ISO_DESIGNATED_LONG);
      mediasizenamemap.put("EnvC0", MediaSizeName.ISO_C0);
      mediasizenamemap.put("EnvC1", MediaSizeName.ISO_C1);
      mediasizenamemap.put("EnvC2", MediaSizeName.ISO_C2);
      mediasizenamemap.put("EnvC3", MediaSizeName.ISO_C3);
      mediasizenamemap.put("EnvC4", MediaSizeName.ISO_C4);
      mediasizenamemap.put("EnvC5", MediaSizeName.ISO_C5);
      mediasizenamemap.put("EnvC6", MediaSizeName.ISO_C6);
    }
}