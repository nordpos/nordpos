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
package com.openbravo.pos.inventory;

import com.openbravo.format.Formats;
import com.openbravo.pos.ticket.ProductInfoExt;
import com.openbravo.pos.ticket.TaxInfo;
import com.openbravo.pos.util.StringUtils;

/**
 *
 * @author adrianromero
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3
 */
public class InventoryLine {

    private double m_dMultiply;
    private double m_dPriceBuy;
    private final double m_dPriceSell;
    private double m_dStockQty;

    private final String m_sProdID;
    private String m_sProdName;
    private final String m_sProdCode;
    private final String m_sProdRef;

    private final String attsetid;
    private String attsetinstid;
    private String attsetinstdesc;

    public InventoryLine(ProductInfoExt oProduct, TaxInfo tax, double dpor, double dprice, double dstockQty) {
        m_sProdID = oProduct.getID();
        m_sProdName = oProduct.getName();
        m_sProdCode = oProduct.getCode();
        m_sProdRef = oProduct.getReference();
        m_dPriceSell = oProduct.getPriceSellTax(tax);
        m_dStockQty = dstockQty;
        m_dMultiply = dpor;
        m_dPriceBuy = dprice;
        attsetid = oProduct.getAttributeSetID();
        attsetinstid = null;
        attsetinstdesc = null;
    }

    public String getProductID() {
        return m_sProdID;
    }

    public String getProductName() {
        return m_sProdName;
    }

    public String getProductCode() {
        return m_sProdCode;
    }

    public String getProductReference() {
        return m_sProdRef;
    }

    public void setProductName(String sValue) {
        if (m_sProdID == null) {
            m_sProdName = sValue;
        }
    }

    public double getMultiply() {
        return m_dMultiply;
    }

    public void setMultiply(double dValue) {
        m_dMultiply = dValue;
    }

    public double getStockQty() {
        return m_dStockQty;
    }

    public void setStockQty(double m_dStockQty) {
        this.m_dStockQty = m_dStockQty;
    }
    
    public double getPriceBuy() {
        return m_dPriceBuy;
    }

    public void setPriceBuy(double dValue) {
        m_dPriceBuy = dValue;
    }

    public double getPriceSell() {
        return m_dPriceSell;
    }

    public double getSubValue() {
        return m_dMultiply * m_dPriceBuy;
    }

    public String getProductAttSetInstId() {
        return attsetinstid;
    }

    public void setProductAttSetInstId(String value) {
        attsetinstid = value;
    }

    public String getProductAttSetId() {
        return attsetid;
    }

    public String getProductAttSetInstDesc() {
        return attsetinstdesc;
    }

    public void setProductAttSetInstDesc(String value) {
        attsetinstdesc = value;
    }

    public String printName() {
        return StringUtils.encodeXML(m_sProdName);
    }

    public String printCode() {
        return StringUtils.encodeXML(m_sProdCode);
    }

    public String printReference() {
        return StringUtils.encodeXML(m_sProdRef);
    }

    public String printPriceBuy() {
        return Formats.CURRENCY.formatValue(getPriceBuy());
    }

    public String printPriceSell() {
        return Formats.CURRENCY.formatValue(getPriceSell());
    }

    public String printMultiply() {
        return Formats.DOUBLE.formatValue(m_dMultiply);
    }

    public String printStockQty() {
        return Formats.DOUBLE.formatValue(getStockQty());
    }
    
    public String printSubValue() {
        return Formats.CURRENCY.formatValue(getSubValue());
    }
}
