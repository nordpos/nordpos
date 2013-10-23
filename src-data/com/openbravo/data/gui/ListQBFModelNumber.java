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


package com.openbravo.data.gui;

import javax.swing.*;
import com.openbravo.data.loader.QBFCompareEnum;
/**
 *
 * @author  adrian
 */
public class ListQBFModelNumber extends AbstractListModel implements ComboBoxModel {
    
    private Object[] m_items;
    private Object m_sel;
    
    /** Creates a new instance of ListQBFModelNumber */
    private ListQBFModelNumber(Object... items) {
        m_items = items;
        m_sel = m_items[0];
    }

    public static ListQBFModelNumber getMandatoryString() {
        return new ListQBFModelNumber(
            QBFCompareEnum.COMP_NONE,
            QBFCompareEnum.COMP_EQUALS,
            QBFCompareEnum.COMP_RE,
            QBFCompareEnum.COMP_DISTINCT,
            QBFCompareEnum.COMP_GREATER,
            QBFCompareEnum.COMP_LESS,
            QBFCompareEnum.COMP_GREATEROREQUALS,
            QBFCompareEnum.COMP_LESSOREQUALS
        );
    }

    public static ListQBFModelNumber getMandatoryNumber() {
        return new ListQBFModelNumber(
            QBFCompareEnum.COMP_NONE,
            QBFCompareEnum.COMP_EQUALS,
            QBFCompareEnum.COMP_DISTINCT,
            QBFCompareEnum.COMP_GREATER,
            QBFCompareEnum.COMP_LESS,
            QBFCompareEnum.COMP_GREATEROREQUALS,
            QBFCompareEnum.COMP_LESSOREQUALS
        );
    }

    public static ListQBFModelNumber getNonMandatoryString() {
        return new ListQBFModelNumber(
            QBFCompareEnum.COMP_NONE,
            QBFCompareEnum.COMP_EQUALS,
            QBFCompareEnum.COMP_RE,
            QBFCompareEnum.COMP_DISTINCT,
            QBFCompareEnum.COMP_GREATER,
            QBFCompareEnum.COMP_LESS,
            QBFCompareEnum.COMP_GREATEROREQUALS,
            QBFCompareEnum.COMP_LESSOREQUALS,
            QBFCompareEnum.COMP_ISNULL,
            QBFCompareEnum.COMP_ISNOTNULL
        );
    }

    public static ListQBFModelNumber getNonMandatoryNumber() {
        return new ListQBFModelNumber(
            QBFCompareEnum.COMP_NONE,
            QBFCompareEnum.COMP_EQUALS,
            QBFCompareEnum.COMP_DISTINCT,
            QBFCompareEnum.COMP_GREATER,
            QBFCompareEnum.COMP_LESS,
            QBFCompareEnum.COMP_GREATEROREQUALS,
            QBFCompareEnum.COMP_LESSOREQUALS,
            QBFCompareEnum.COMP_ISNULL,
            QBFCompareEnum.COMP_ISNOTNULL  
        );
    }
    
    public Object getElementAt(int index) {
        return m_items[index];
    }
   
    public int getSize() {
        return m_items.length;
    }
    
    public Object getSelectedItem() {
        return m_sel;
    }
     
    public void setSelectedItem(Object anItem) {
        m_sel = anItem;
    }
}
