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

import com.openbravo.basic.BasicException;
import com.openbravo.pos.ticket.ProductInfoExt;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 *
 * @author adrianromero
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
public class JPanelTicketEdits extends JPanelTicket {

    private JTicketCatalogLines m_catandlines;

    /** Creates a new instance of JPanelTicketRefunds */
    public JPanelTicketEdits() {
    }

    public String getTitle() {
        return null;
    }

    @Override
    public void activate() throws BasicException {
        super.activate();
        if (!"false".equals(panelconfig.getProperty("catvisible"))) {
            m_catandlines.loadCatalog();
        }
    }

    public void showCatalog() {
        m_jbtnconfig.setVisible(true);
        if (!"false".equals(panelconfig.getProperty("catvisible")) && (m_catandlines != null)) {
            catcontainer.setVisible(true);
            m_catandlines.showCatalog();
        } else {
            catcontainer.setVisible(false);
        }
    }

    public void showRefundLines(List aRefundLines) {
        m_jbtnconfig.setVisible(false);
        catcontainer.setVisible(true);
        if (m_catandlines == null) {
            catcontainer.add(getSouthComponent(), BorderLayout.CENTER);
        }
        m_catandlines.showRefundLines(aRefundLines);
    }

    protected JTicketsBag getJTicketsBag() {
        return new JTicketsBagTicket(m_App, this);
    }

    protected Component getSouthComponent() {

        m_catandlines = new JTicketCatalogLines(m_App, this, panelconfig);
        m_catandlines.setPreferredSize(new Dimension(
                0,
                Integer.parseInt(panelconfig.getProperty("cat-height", "200"))));
        m_catandlines.addActionListener(new CatalogListener());
        return m_catandlines;
    }

    protected void resetSouthComponent() {
    }

    private class CatalogListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            buttonTransition((ProductInfoExt) e.getSource());
        }
    }
}
