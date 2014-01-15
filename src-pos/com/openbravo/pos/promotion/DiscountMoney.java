/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.promotion;

import com.openbravo.pos.ticket.TicketLineInfo;
import java.math.BigDecimal;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
public class DiscountMoney {

    public TicketLineInfo LineDiscountMoney(TicketLineInfo TicketLine, BigDecimal dMoney) {

        BigDecimal linedicountmoney = TicketLine.getDiscountMoney();
        BigDecimal lineprice = TicketLine.getPriceTax();
        BigDecimal linenodisount = TicketLine.getPriceTaxNoDiscount();

        if (linedicountmoney.equals(new BigDecimal(0)) || !linedicountmoney.equals(dMoney)) {
            if (!linedicountmoney.equals(new BigDecimal(0))) {
                TicketLine.setPriceTax(linenodisount.add(dMoney));
            } else {
                TicketLine.setPriceTax(lineprice.subtract(dMoney));
            }
            TicketLine.setProperty("discountmoney", dMoney.toString());
        }

        return TicketLine;
    }
}
