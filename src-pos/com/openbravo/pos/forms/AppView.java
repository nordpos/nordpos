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

package com.openbravo.pos.forms;

import com.openbravo.data.loader.Session;
import com.nordpos.device.plu.DeviceInputOutput;
import com.nordpos.device.ticket.DeviceTicketFactory;
import com.nordpos.device.scale.DeviceScaleFactory;
import java.util.Date;

/**
 *
 * @author adrianromero
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
public interface AppView {

    public DeviceScaleFactory getDeviceScale();
    public DeviceTicketFactory getDeviceTicket();
    public DeviceInputOutput getDevicePLUs();

    public Session getSession();
    public AppProperties getProperties();
//    public HostProperties getHostProperties();
    public Object getBean(String beanfactory) throws BeanFactoryException;

    public void setActiveCash(String value, int iSeq, Date dStart, Date dEnd);
    public String getActiveCashIndex();
    public int getActiveCashSequence();
    public Date getActiveCashDateStart();
    public Date getActiveCashDateEnd();

    public String getInventoryLocation();

    public String getGenerateProductReference();
    public String getGenerateProductBarcode();

    public String getCustomerCard();
    public String getUserCard();

    public String getUserBarcode();
    public String getPriceBarcode();
    public String getUnitBarcode();
    public String getProductPriceBarcode();

    public String getDefaultInventoryLocation();
    public String getDefaultTaxCategory();
    public String getDefaultProductCategory();

    public void waitCursorBegin();
    public void waitCursorEnd();

    public AppUserView getAppUserView();
}

