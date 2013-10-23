/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.promotion;

import com.openbravo.pos.ticket.TicketLineInfo;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
public class DiscountMoney {
    
    public TicketLineInfo LineDiscountMoney(TicketLineInfo TicketLine, Double dMoney) {

        double linedicountmoney = TicketLine.getDiscountMoney();
        double lineprice = TicketLine.getPriceTax();
        double linenodisount = TicketLine.getPriceTaxNoDiscount();

        if (linedicountmoney == 0.0 || linedicountmoney != dMoney) {
            if (linedicountmoney != 0.0) {
                TicketLine.setPriceTax(linenodisount + dMoney);
            } else {
                TicketLine.setPriceTax(lineprice - dMoney);
            }
            TicketLine.setProperty("discountmoney", Double.toString(dMoney));
        }
        
        return TicketLine;
    }    
}
