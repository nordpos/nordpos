/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.promotion;

import com.openbravo.pos.ticket.TicketLineInfo;
import com.openbravo.pos.util.RoundUtils;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 *
 * @author svininykh-av
 */
public class DiscountPercent {


    public TicketLineInfo LineDiscountPercent(TicketLineInfo TicketLine, BigDecimal dDiscount) {

        BigDecimal linediscount = TicketLine.getDiscountRate();
        BigDecimal lineprice = TicketLine.getPriceTax();
        BigDecimal linenodisount = TicketLine.getPriceTaxNoDiscount();

        if (linediscount.equals(new BigDecimal(0)) || !linediscount.equals(dDiscount)) {
            if (!linediscount.equals(new BigDecimal(0))) {
                TicketLine.setPriceTax(linenodisount.subtract(linenodisount.multiply(dDiscount), MathContext.DECIMAL32));
            } else {
                TicketLine.setPriceTax(lineprice.subtract(lineprice.multiply(dDiscount), MathContext.DECIMAL32));
            }
            TicketLine.setProperty("discountrate", dDiscount.toString());
        }

        return TicketLine;
    }
}
