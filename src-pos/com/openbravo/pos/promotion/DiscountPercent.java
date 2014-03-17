/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.promotion;

import com.openbravo.pos.ticket.TicketLineInfo;
import com.openbravo.pos.util.RoundUtils;

/**
 *
 * @author svininykh-av
 */
public class DiscountPercent {
    
    
    public TicketLineInfo LineDiscountPercent(TicketLineInfo TicketLine, Double dDiscount) {

        double linediscount = TicketLine.getDiscountRate();
        double lineprice = TicketLine.getPriceTax();
        double linenodisount = TicketLine.getPriceTaxNoDiscount();

        if (linediscount == 0.0 || linediscount != dDiscount) {
            if (linediscount != 0.0) {
                TicketLine.setPriceTax(RoundUtils.getValue(linenodisount - linenodisount * dDiscount));
            } else {
                TicketLine.setPriceTax(RoundUtils.getValue(lineprice - lineprice * dDiscount));
            }
            TicketLine.setProperty("discountrate", Double.toString(dDiscount));
        }
        
        return TicketLine;
    }
}
