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

package com.openbravo.pos.sales;

import com.openbravo.pos.sales.simple.JTicketsBagSimple;
import com.openbravo.pos.forms.*; 
import javax.swing.*;
import com.openbravo.pos.sales.restaurant.JTicketsBagRestaurantMap;
import com.openbravo.pos.sales.shared.JTicketsBagShared;

public abstract class JTicketsBag extends JPanel {
    
    protected AppView m_App;     
    protected DataLogicSales m_dlSales;
    protected TicketsEditor m_panelticket;    
    
    /** Creates new form JTicketsBag */
    public JTicketsBag(AppView oApp, TicketsEditor panelticket) {        
        m_App = oApp;     
        m_panelticket = panelticket;        
        m_dlSales = (DataLogicSales) m_App.getBean("com.openbravo.pos.forms.DataLogicSales");
    }
    
    public abstract void activate();
    public abstract boolean deactivate();
    public abstract void deleteTicket();
    
    protected abstract JComponent getBagComponent();
    protected abstract JComponent getNullComponent();
    
    public static JTicketsBag createTicketsBag(String sName, AppView app, TicketsEditor panelticket) {
        
        if ("standard".equals(sName)) {
            // return new JTicketsBagMulti(oApp, user, panelticket);
            return new JTicketsBagShared(app, panelticket);
        } else if ("restaurant".equals(sName)) {
            return new JTicketsBagRestaurantMap(app, panelticket);
        } else { // "simple"
            return new JTicketsBagSimple(app, panelticket);
        }
    }   
}
