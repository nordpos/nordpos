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

package com.openbravo.pos.ticket;

import com.openbravo.format.Formats;
import java.math.BigDecimal;


public class TicketTaxInfo {

    private TaxInfo tax;

    private BigDecimal subtotal;
    private BigDecimal taxtotal;

    public TicketTaxInfo(TaxInfo tax) {
        this.tax = tax;

        subtotal = new BigDecimal(0.0);
        taxtotal = new BigDecimal(0.0);
    }

    public TaxInfo getTaxInfo() {
        return tax;
    }

    public void add(BigDecimal dValue) {
        subtotal = subtotal.add(dValue);
        taxtotal = subtotal.multiply(new BigDecimal(tax.getRate()));
    }

    public BigDecimal getSubTotal() {
        return subtotal;
    }

    public BigDecimal getTax() {
        return taxtotal;
    }

    public BigDecimal getTotal() {
        return subtotal.add(taxtotal);
    }

    public String printSubTotal() {
        return Formats.CURRENCY.formatValue(getSubTotal());
    }
    public String printTax() {
        return Formats.CURRENCY.formatValue(getTax());
    }
    public String printTotal() {
        return Formats.CURRENCY.formatValue(getTotal());
    }
}
