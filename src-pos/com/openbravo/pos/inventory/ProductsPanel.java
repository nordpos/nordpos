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

import com.openbravo.basic.BasicException;
import com.openbravo.data.user.EditorListener;
import com.openbravo.data.user.EditorRecord;
import com.openbravo.data.user.ListProviderCreator;
import com.openbravo.data.user.SaveProvider;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.panels.JPanelTableExt;
import com.openbravo.pos.ticket.ProductFilter;
import java.awt.Component;
import javax.swing.JButton;

/**
 *
 * @author adrianromero
 * Created on 1 de marzo de 2007, 22:15
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3
 */
public class ProductsPanel extends JPanelTableExt implements EditorListener {

    private ProductsEditor jeditor;
    private ProductFilter jproductfilter;

    private DataLogicSales m_dlSales = null;

    public ProductsPanel() {
    }

    @Override
    protected void init() {
        m_dlSales = (DataLogicSales) app.getBean(DataLogicSales.class.getName());

        jproductfilter = new ProductFilter();
        jproductfilter.init(app);

        row = m_dlSales.getProductsRow();

        lpr = new ListProviderCreator(m_dlSales.getProductCatQBF(), jproductfilter);

        spr = new SaveProvider(
                m_dlSales.getProductCatUpdate(),
                m_dlSales.getProductCatInsert(),
                m_dlSales.getProductCatDelete());

        // el panel del editor
        jeditor = new ProductsEditor(app, m_dlSales, dirty);
    }

    @Override
    public EditorRecord getEditor() {
        return jeditor;
    }

    @Override
    public Component getFilter() {
        return jproductfilter.getComponent();
    }

    @Override
    public Component getToolbarExtrasDevicePLUs() {

        JButton btnDevicePLUs = new JButton();
        btnDevicePLUs.setText(AppLocal.getIntString("button.uploadplu"));
        btnDevicePLUs.setVisible(app.getDevicePLUs() != null);
        btnDevicePLUs.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExtrasDevicePLUsActionPerformed(evt);
            }
        });

        return btnDevicePLUs;
    }

    private void btnExtrasDevicePLUsActionPerformed(java.awt.event.ActionEvent evt) {
        JDlgUploadProducts.showMessage(this, this.app, bd, m_dlSales);
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Products");
    }

    @Override
    public void activate() throws BasicException {

        jeditor.activate();
        jproductfilter.activate();

        super.activate();
    }

    @Override
    public void updateValue(Object value) {
    }
}
