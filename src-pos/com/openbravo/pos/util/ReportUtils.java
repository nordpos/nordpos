//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2008-2009 Openbravo, S.L.
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


package com.openbravo.pos.util;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

/**
 *
 * @author adrianromero
 */
public class ReportUtils {
    
    private ReportUtils() {
    }
    
    public static PrintService getPrintService(String printername) {
        
        // Initalize print service
        
        if (printername == null) {
            return PrintServiceLookup.lookupDefaultPrintService();       
        } else {
            
            if ("(Show dialog)".equals(printername)) {
                return null; // null means "you have to show the print dialog"
            } else if ("(Default)".equals(printername)) {
                return PrintServiceLookup.lookupDefaultPrintService(); 
            } else {
                PrintService[] pservices = 
                        PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PRINTABLE , null);
                for (PrintService s : pservices) {    
                    if (printername.equals(s.getName())) {
                        return s;
                    }
                }
                return PrintServiceLookup.lookupDefaultPrintService();       
            }                
        }                 
    }
    
    public static String[] getPrintNames() {
        PrintService[] pservices = 
                PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PRINTABLE , null);
        
        String printers[] = new String[pservices.length];
        for (int i = 0; i < pservices.length; i++) {    
            printers[i] = pservices[i].getName();
        }
        
        return printers;
    }

}
