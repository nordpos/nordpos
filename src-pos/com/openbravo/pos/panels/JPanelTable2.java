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

package com.openbravo.pos.panels;

import com.openbravo.data.loader.ComparatorCreator;
import com.openbravo.data.loader.Vectorer;
import com.openbravo.data.model.Row;
import com.openbravo.data.user.ListProvider;
import com.openbravo.data.user.SaveProvider;
import javax.swing.ListCellRenderer;

/**
 *
 * @author adrianromero
 */
public abstract class JPanelTable2 extends JPanelTable {
   
    protected Row row;
    protected ListProvider lpr;
    protected SaveProvider spr;   
    
    @Override
    public final ListProvider getListProvider() {
        return lpr;
    }

    @Override
    public final SaveProvider getSaveProvider() {
        return spr;
    }
    
    @Override
    public final Vectorer getVectorer() {
        return row.getVectorer();
    }
    
    @Override
    public final ComparatorCreator getComparatorCreator() {
        return row.getComparatorCreator();
    }
    
    @Override
    public final ListCellRenderer getListCellRenderer() {
        return row.getListCellRenderer();
    } 
}
