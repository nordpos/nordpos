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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.Datas;
import com.openbravo.data.loader.PreparedSentence;
import com.openbravo.data.loader.SentenceExec;
import com.openbravo.data.loader.SentenceExecTransaction;
import com.openbravo.data.loader.SerializerRead;
import com.openbravo.data.loader.SerializerWriteBasicExt;
import com.openbravo.data.model.Field;
import com.openbravo.data.model.Row;
import com.openbravo.data.user.EditorRecord;
import com.openbravo.data.user.ListProviderCreator;
import com.openbravo.data.user.SaveProvider;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.panels.JPanelTable2;
import com.openbravo.pos.reports.JParamsLocation;
import java.util.UUID;

/**
 *
 * @author adrianromero
 */
public class ProductsWarehousePanel extends JPanelTable2 {

    private JParamsLocation m_paramslocation;    
    private ProductsWarehouseEditor jeditor;
    
    /** Creates a new instance of ProductsWarehousePanel */
    public ProductsWarehousePanel() {
    }

    protected void init() {   
               
        m_paramslocation =  new JParamsLocation();
        m_paramslocation.init(app);
        m_paramslocation.addActionListener(new ReloadActionListener());

        row = new Row(
                new Field("ID", Datas.STRING, Formats.STRING),
                new Field("PRODUCT_ID", Datas.STRING, Formats.STRING),
                new Field(AppLocal.getIntString("label.prodref"), Datas.STRING, Formats.STRING, true, true, true),
                new Field(AppLocal.getIntString("label.prodname"), Datas.STRING, Formats.STRING, true, true, true),
                new Field("LOCATION", Datas.STRING, Formats.STRING),
                new Field("STOCKSECURITY", Datas.DOUBLE, Formats.DOUBLE),
                new Field("STOCKMAXIMUM", Datas.DOUBLE, Formats.DOUBLE),
                new Field("UNITS", Datas.DOUBLE, Formats.DOUBLE)
        );

        lpr = new ListProviderCreator(new PreparedSentence(app.getSession(),
                "SELECT L.ID, P.ID, P.REFERENCE, P.NAME," +
                "L.STOCKSECURITY, L.STOCKMAXIMUM, COALESCE(S.SUMUNITS, 0) " +
                "FROM PRODUCTS P " +
                "LEFT OUTER JOIN (SELECT ID, PRODUCT, LOCATION, STOCKSECURITY, STOCKMAXIMUM FROM STOCKLEVEL WHERE LOCATION = ?) L ON P.ID = L.PRODUCT " +
                "LEFT OUTER JOIN (SELECT PRODUCT, SUM(UNITS) AS SUMUNITS FROM STOCKCURRENT WHERE LOCATION = ? GROUP BY PRODUCT) S ON P.ID = S.PRODUCT " +
                "ORDER BY P.NAME",
                new SerializerWriteBasicExt(new Datas[] {Datas.OBJECT, Datas.STRING}, new int[]{1, 1}),
                new WarehouseSerializerRead()
                ),
                m_paramslocation);
        
        
        SentenceExec updatesent =  new SentenceExecTransaction(app.getSession()) {
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                if (values[0] == null)  {
                    // INSERT
                    values[0] = UUID.randomUUID().toString();
                    return new PreparedSentence(app.getSession()
                        , "INSERT INTO STOCKLEVEL (ID, LOCATION, PRODUCT, STOCKSECURITY, STOCKMAXIMUM) VALUES (?, ?, ?, ?, ?)"
                        , new SerializerWriteBasicExt(row.getDatas(), new int[] {0, 4, 1, 5, 6})).exec(params);
                } else {
                    // UPDATE
                    return new PreparedSentence(app.getSession()
                        , "UPDATE STOCKLEVEL SET STOCKSECURITY = ?, STOCKMAXIMUM = ? WHERE ID = ?"
                        , new SerializerWriteBasicExt(row.getDatas(), new int[] {5, 6, 0})).exec(params);
                }
            }
        };     
        
        spr = new SaveProvider(updatesent, null, null);
         
        jeditor = new ProductsWarehouseEditor(dirty);   
    }

       
    @Override
    public Component getFilter() {
        return m_paramslocation.getComponent();
    }  
    
    public EditorRecord getEditor() {
        return jeditor;
    }  
    
    @Override
    public void activate() throws BasicException {
        
        m_paramslocation.activate(); 
        super.activate();
    }     
    
    public String getTitle() {
        return AppLocal.getIntString("Menu.ProductsWarehouse");
    }      
    
    private class ReloadActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                ProductsWarehousePanel.this.bd.actionLoad();
            } catch (BasicException w) {
            }
        }
    }

    private class WarehouseSerializerRead implements SerializerRead {
        public Object readValues(DataRead dr) throws BasicException {
            return new Object[] {
                dr.getString(1),
                dr.getString(2),
                dr.getString(3),
                dr.getString(4),
                ((Object[]) m_paramslocation.createValue())[1],
                dr.getDouble(5),
                dr.getDouble(6),
                dr.getDouble(7)
            };
        }
    }
}
