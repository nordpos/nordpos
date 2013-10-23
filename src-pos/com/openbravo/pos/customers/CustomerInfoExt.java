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

package com.openbravo.pos.customers;

import com.openbravo.format.Formats;
import com.openbravo.pos.util.RoundUtils;
import java.util.Date;

/**
 *
 * @author adrianromero
 */
public class CustomerInfoExt extends CustomerInfo {
    
    protected String taxcustomerid;
    protected String notes;
    protected boolean visible;
    protected String card;
    protected Double maxdebt;
    protected Date curdate;
    protected Double curdebt;
    protected String firstname;
    protected String lastname;
    protected String email;
    protected String phone;
    protected String phone2;
    protected String fax;
    protected String address;
    protected String address2;
    protected String postal;
    protected String city;
    protected String region;
    protected String country;
    
    /** Creates a new instance of UserInfoBasic */
    public CustomerInfoExt(String id) {
        super(id);
    } 
  
    public String getTaxCustCategoryID() {
        return taxcustomerid;
    }
    
    public void setTaxCustomerID(String taxcustomerid) {
        this.taxcustomerid = taxcustomerid;
    }
    
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public Double getMaxdebt() {
        return maxdebt;
    }
    
    public String printMaxDebt() {       
        return Formats.CURRENCY.formatValue(RoundUtils.getValue(getMaxdebt()));
    }
    
    public void setMaxdebt(Double maxdebt) {
        this.maxdebt = maxdebt;
    }

    public Date getCurdate() {
        return curdate;
    }

    public void setCurdate(Date curdate) {
        this.curdate = curdate;
    }

    public Double getCurdebt() {
        return curdebt;
    }
    
    public String printCurDebt() {       
        return Formats.CURRENCY.formatValue(RoundUtils.getValue(getCurdebt()));
    }
    
    public void setCurdebt(Double curdebt) {
        this.curdebt = curdebt;
    }
    
    public void updateCurDebt(Double amount, Date d) {
        
        curdebt = curdebt == null ? amount : curdebt + amount;

        if (RoundUtils.compare(curdebt, 0.0) > 0) {
            if (curdate == null) {
                // new date
                curdate = d;
            }
        } else if (RoundUtils.compare(curdebt, 0.0) == 0) {
            curdebt = null;
            curdate = null;
        } else { // < 0
            curdate = null;
        }
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
