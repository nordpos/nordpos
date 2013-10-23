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

package com.openbravo.pos.payment;

import java.util.Iterator;
import java.util.LinkedList;

public class PaymentInfoList {
    
    private LinkedList<PaymentInfo> m_apayment;
    
    /** Creates a new instance of PaymentInfoComposed */
    public PaymentInfoList() {
        m_apayment = new LinkedList<PaymentInfo>();
    }
        
    public double getTotal() {
        
        double dTotal = 0.0;
        Iterator i = m_apayment.iterator();
        while (i.hasNext()) {
            PaymentInfo p = (PaymentInfo) i.next();
            dTotal += p.getTotal();
        }
        
        return dTotal;
    }     
    
    public boolean isEmpty() {
        return m_apayment.isEmpty();
    }
    
    public void add(PaymentInfo p) {
        m_apayment.addLast(p);
    }
    
    public void removeLast() {
        m_apayment.removeLast();
    }
    
    public LinkedList<PaymentInfo> getPayments() {
        return m_apayment;
    }
}
