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

package com.openbravo.pos.payment;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.SerializableRead;
import com.openbravo.format.Formats;

public class PaymentInfoTicket extends PaymentInfo implements SerializableRead  {
    
    private static final long serialVersionUID = 8865238639097L;
    private double m_dTicket;
    private String m_sName;
    private String m_transactionID;
    
    /** Creates a new instance of PaymentInfoCash */
    public PaymentInfoTicket(double dTicket, String sName) {
        m_sName = sName;
        m_dTicket = dTicket;
    }
    
    public PaymentInfoTicket(double dTicket, String sName, String transactionID) {
        m_sName = sName;
        m_dTicket = dTicket;
        m_transactionID = transactionID;
    }
    
    public PaymentInfoTicket() {
        m_sName = null;
        m_dTicket = 0.0;
        m_transactionID = null;
     }
    
    public void readValues(DataRead dr) throws BasicException {
        m_sName = dr.getString(1);
        m_dTicket = dr.getDouble(2).doubleValue();
        m_transactionID = dr.getString(3);
    }
    
    public PaymentInfo copyPayment(){
        return new PaymentInfoTicket(m_dTicket, m_sName);
    }
    public String getName() {
        return m_sName;
    }   
    public double getTotal() {
        return m_dTicket;
    }
    public String getTransactionID(){
        return m_transactionID;
    }
    public String printPaid() {
        return Formats.CURRENCY.formatValue(new Double(m_dTicket));
    }
    
    // Especificas
    public String printPaperTotal() {
        // En una devolucion hay que cambiar el signo al total
        return Formats.CURRENCY.formatValue(new Double(-m_dTicket));
    }          
}
