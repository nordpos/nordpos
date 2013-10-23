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
import com.openbravo.data.user.EditorRecord;
import com.openbravo.data.user.ListProvider;
import com.openbravo.data.user.SaveProvider;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.panels.JPanelTable;

/**
 *
 * @author adrianromero
 */
public class StockDiaryPanel extends JPanelTable {
    
    private StockDiaryEditor jeditor;    
    private DataLogicSales m_dlSales;
    
    /** Creates a new instance of JPanelDiaryEditor */
    public StockDiaryPanel() {
    }
    
    protected void init() {
        m_dlSales = (DataLogicSales) app.getBean("com.openbravo.pos.forms.DataLogicSales");
        jeditor = new StockDiaryEditor(app, dirty); 
    }
    
    public ListProvider getListProvider() {
        return null;
    }
    
    public SaveProvider getSaveProvider() {
        return  new SaveProvider(null
                , m_dlSales.getStockDiaryInsert()
                , m_dlSales.getStockDiaryDelete());      
    }
    
    public EditorRecord getEditor() {
        return jeditor;
    }
    
    public String getTitle() {
        return AppLocal.getIntString("Menu.StockDiary");
    }     
    
        
    public void activate() throws BasicException {
        jeditor.activate(); // primero activo el editor 
        super.activate();   // segundo activo el padre        
    } 
}
