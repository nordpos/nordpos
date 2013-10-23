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

import java.util.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.openbravo.pos.payment.PaymentInfo;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.SerializableRead;
import com.openbravo.format.Formats;
import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.LocalRes;
import com.openbravo.pos.customers.CustomerInfoExt;
import com.openbravo.pos.payment.PaymentInfoMagcard;
import com.openbravo.pos.util.StringUtils;

/**
 *
 * @author adrianromero
 */
public class TicketInfo implements SerializableRead, Externalizable {

    private static final long serialVersionUID = 2765650092387265178L;

    public static final int RECEIPT_NORMAL = 0;
    public static final int RECEIPT_REFUND = 1;
    public static final int RECEIPT_PAYMENT = 2;

    private static DateFormat m_dateformat = new SimpleDateFormat("hh:mm");

    private String m_sId;
    private int tickettype;
    private int m_iTicketId;
    private java.util.Date m_dDate;
    private Properties attributes;
    private UserInfo m_User;
    private CustomerInfoExt m_Customer;
    private String m_sActiveCash;
    private List<TicketLineInfo> m_aLines;
    private List<PaymentInfo> payments;
    private List<TicketTaxInfo> taxes;
    private String m_sResponse;

    /** Creates new TicketModel */
    public TicketInfo() {
        m_sId = UUID.randomUUID().toString();
        tickettype = RECEIPT_NORMAL;
        m_iTicketId = 0; // incrementamos
        m_dDate = new Date();
        attributes = new Properties();
        m_User = null;
        m_Customer = null;
        m_sActiveCash = null;
        m_aLines = new ArrayList<TicketLineInfo>(); // vacio de lineas

        payments = new ArrayList<PaymentInfo>();
        taxes = null;
        m_sResponse = null;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        // esto es solo para serializar tickets que no estan en la bolsa de tickets pendientes
        out.writeObject(m_sId);
        out.writeInt(tickettype);
        out.writeInt(m_iTicketId);
        out.writeObject(m_Customer);
        out.writeObject(m_dDate);
        out.writeObject(attributes);
        out.writeObject(m_aLines);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // esto es solo para serializar tickets que no estan en la bolsa de tickets pendientes
        m_sId = (String) in.readObject();
        tickettype = in.readInt();
        m_iTicketId = in.readInt();
        m_Customer = (CustomerInfoExt) in.readObject();
        m_dDate = (Date) in.readObject();
        attributes = (Properties) in.readObject();
        m_aLines = (List<TicketLineInfo>) in.readObject();
        m_User = null;
        m_sActiveCash = null;

        payments = new ArrayList<PaymentInfo>();
        taxes = null;
    }

    public void readValues(DataRead dr) throws BasicException {
        m_sId = dr.getString(1);
        tickettype = dr.getInt(2).intValue();
        m_iTicketId = dr.getInt(3).intValue();
        m_dDate = dr.getTimestamp(4);
        m_sActiveCash = dr.getString(5);
        try {
            byte[] img = dr.getBytes(6);
            if (img != null) {
                attributes.loadFromXML(new ByteArrayInputStream(img));
            }
        } catch (IOException e) {
        }
        m_User = new UserInfo(dr.getString(7), dr.getString(8));
        m_Customer = new CustomerInfoExt(dr.getString(9));
        m_aLines = new ArrayList<TicketLineInfo>();

        payments = new ArrayList<PaymentInfo>();
        taxes = null;
    }

    public TicketInfo copyTicket() {
        TicketInfo t = new TicketInfo();

        t.tickettype = tickettype;
        t.m_iTicketId = m_iTicketId;
        t.m_dDate = m_dDate;
        t.m_sActiveCash = m_sActiveCash;
        t.attributes = (Properties) attributes.clone();
        t.m_User = m_User;
        t.m_Customer = m_Customer;

        t.m_aLines = new ArrayList<TicketLineInfo>();
        for (TicketLineInfo l : m_aLines) {
            t.m_aLines.add(l.copyTicketLine());
        }
        t.refreshLines();

        t.payments = new LinkedList<PaymentInfo>();
        for (PaymentInfo p : payments) {
            t.payments.add(p.copyPayment());
        }

        // taxes are not copied, must be calculated again.

        return t;
    }

    public String getId() {
        return m_sId;
    }

    public int getTicketType() {
        return tickettype;
    }

    public void setTicketType(int tickettype) {
        this.tickettype = tickettype;
    }

    public int getTicketId() {
        return m_iTicketId;
    }

    public void setTicketId(int iTicketId) {
        m_iTicketId = iTicketId;
    // refreshLines();
    }

    public String getName(Object info) {

        StringBuffer name = new StringBuffer();

        if (getCustomerId() != null) {
            name.append(m_Customer.toString());
            name.append(" - ");
        }

        if (info == null) {
            if (m_iTicketId == 0) {
                name.append("(" + m_dateformat.format(m_dDate) + " " + Long.toString(m_dDate.getTime() % 1000) + ")");
            } else {
                name.append(Integer.toString(m_iTicketId));
            }
        } else {
            name.append(info.toString());
        }
        
        return name.toString();
    }

    public String getName() {
        return getName(null);
    }

    public java.util.Date getDate() {
        return m_dDate;
    }

    public void setDate(java.util.Date dDate) {
        m_dDate = dDate;
    }

    public UserInfo getUser() {
        return m_User;
    }

    public void setUser(UserInfo value) {
        m_User = value;
    }

    public CustomerInfoExt getCustomer() {
        return m_Customer;
    }

    public void setCustomer(CustomerInfoExt value) {
        m_Customer = value;
    }

    public String getCustomerId() {
        if (m_Customer == null) {
            return null;
        } else {
            return m_Customer.getId();
        }
    }
    
    public String getTransactionID(){
        return (getPayments().size()>0)
            ? ( getPayments().get(getPayments().size()-1) ).getTransactionID()
            : StringUtils.getCardNumber(); //random transaction ID
    }
    
    public String getReturnMessage(){
        return ( (getPayments().get(getPayments().size()-1)) instanceof PaymentInfoMagcard )
            ? ((PaymentInfoMagcard)(getPayments().get(getPayments().size()-1))).getReturnMessage()
            : LocalRes.getIntString("button.ok");
    }

    public void setActiveCash(String value) {
        m_sActiveCash = value;
    }

    public String getActiveCash() {
        return m_sActiveCash;
    }

    public String getProperty(String key) {
        return attributes.getProperty(key);
    }

    public String getProperty(String key, String defaultvalue) {
        return attributes.getProperty(key, defaultvalue);
    }

    public void setProperty(String key, String value) {
        attributes.setProperty(key, value);
    }

    public Properties getProperties() {
        return attributes;
    }

    public TicketLineInfo getLine(int index) {
        return m_aLines.get(index);
    }

    public void addLine(TicketLineInfo oLine) {

        oLine.setTicket(m_sId, m_aLines.size());
        m_aLines.add(oLine);
    }

    public void insertLine(int index, TicketLineInfo oLine) {
        m_aLines.add(index, oLine);
        refreshLines();
    }

    public void setLine(int index, TicketLineInfo oLine) {
        oLine.setTicket(m_sId, index);
        m_aLines.set(index, oLine);
    }

    public void removeLine(int index) {
        m_aLines.remove(index);
        refreshLines();
    }

    private void refreshLines() {
        for (int i = 0; i < m_aLines.size(); i++) {
            getLine(i).setTicket(m_sId, i);
        }
    }

    public int getLinesCount() {
        return m_aLines.size();
    }
    
    public double getArticlesCount() {
        double dArticles = 0.0;
        TicketLineInfo oLine;

        for (Iterator<TicketLineInfo> i = m_aLines.iterator(); i.hasNext();) {
            oLine = i.next();
            dArticles += oLine.getMultiply();
        }

        return dArticles;
    }

    public double getSubTotal() {
        double sum = 0.0;
        for (TicketLineInfo line : m_aLines) {
            sum += line.getSubValue();
        }
        return sum;
    }

    public double getTax() {

        double sum = 0.0;
        if (hasTaxesCalculated()) {
            for (TicketTaxInfo tax : taxes) {
                sum += tax.getTax(); // Taxes are already rounded...
            }
        } else {
            for (TicketLineInfo line : m_aLines) {
                sum += line.getTax();
            }
        }
        return sum;
    }

    public double getTotal() {
        
        return getSubTotal() + getTax();
    }

    public double getTotalPaid() {

        double sum = 0.0;
        for (PaymentInfo p : payments) {
            if (!"debtpaid".equals(p.getName())) {
                sum += p.getTotal();
            }
        }
        return sum;
    }

    public List<TicketLineInfo> getLines() {
        return m_aLines;
    }

    public void setLines(List<TicketLineInfo> l) {
        m_aLines = l;
    }

    public List<PaymentInfo> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentInfo> l) {
        payments = l;
    }

    public void resetPayments() {
        payments = new ArrayList<PaymentInfo>();
    }

    public List<TicketTaxInfo> getTaxes() {
        return taxes;
    }

    public boolean hasTaxesCalculated() {
        return taxes != null;
    }

    public void setTaxes(List<TicketTaxInfo> l) {
        taxes = l;
    }

    public void resetTaxes() {
        taxes = null;
    }

    public TicketTaxInfo getTaxLine(TaxInfo tax) {

        for (TicketTaxInfo taxline : taxes) {
            if (tax.getId().equals(taxline.getTaxInfo().getId())) {
                return taxline;
            }
        }

        return new TicketTaxInfo(tax);
    }

    public TicketTaxInfo[] getTaxLines() {

        Map<String, TicketTaxInfo> m = new HashMap<String, TicketTaxInfo>();

        TicketLineInfo oLine;
        for (Iterator<TicketLineInfo> i = m_aLines.iterator(); i.hasNext();) {
            oLine = i.next();

            TicketTaxInfo t = m.get(oLine.getTaxInfo().getId());
            if (t == null) {
                t = new TicketTaxInfo(oLine.getTaxInfo());
                m.put(t.getTaxInfo().getId(), t);
            }
            t.add(oLine.getSubValue());
        }

        // return dSuma;       
        Collection<TicketTaxInfo> avalues = m.values();
        return avalues.toArray(new TicketTaxInfo[avalues.size()]);
    }

    public String printId() {
        if (m_iTicketId > 0) {
            // valid ticket id
            return Formats.INT.formatValue(new Integer(m_iTicketId));
        } else {
            return "";
        }
    }

    public String printDate() {
        return Formats.TIMESTAMP.formatValue(m_dDate);
    }

    public String printUser() {
        return m_User == null ? "" : m_User.getName();
    }

    public String printCustomer() {
        return m_Customer == null ? "" : m_Customer.getName();
    }

    public String printArticlesCount() {
        return Formats.DOUBLE.formatValue(new Double(getArticlesCount()));
    }

    public String printSubTotal() {
        return Formats.CURRENCY.formatValue(new Double(getSubTotal()));
    }

    public String printTax() {
        return Formats.CURRENCY.formatValue(new Double(getTax()));
    }

    public String printTotal() {
        return Formats.CURRENCY.formatValue(new Double(getTotal()));
    }

    public String printTotalPaid() {
        return Formats.CURRENCY.formatValue(new Double(getTotalPaid()));
    }
}
