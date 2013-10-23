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
//    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-130

package com.openbravo.pos.inventory;

import javax.swing.table.AbstractTableModel;
import com.openbravo.basic.BasicException;
import com.openbravo.data.user.BrowsableData;
import com.openbravo.format.Formats;

/**
 *
 * @author adrianromero
 */
public class StockModel extends AbstractTableModel {
    
    private BrowsableData m_bd;
    private Formats[] m_formats;
    private boolean[] m_bedit;
    
    /** Creates a new instance of StockModel */
    public StockModel(BrowsableData bd, Formats[] f, boolean[] bedit) {
        m_bd = bd;
        m_formats = f;
        m_bedit = bedit;
    }
    public int getRowCount() {
        return m_bd.getSize();
    }
    public int getColumnCount() {
        return m_formats.length;
    }
    public Object getValueAt(int row, int column) {
        return m_formats[column].formatValue(
                ((Object[]) m_bd.getElementAt(row))[column]);
    }     
    public boolean isCellEditable(int row, int column) {
        return m_bedit[column];
    }
    public void setValueAt(Object aValue, int row, int column) {
        Object[] record = (Object[]) m_bd.getElementAt(row);
        try {
            record[column] = m_formats[column].parseValue((String) aValue);           
            m_bd.updateRecord(row, record);
        } catch (BasicException e) {           
        }
    }
}
