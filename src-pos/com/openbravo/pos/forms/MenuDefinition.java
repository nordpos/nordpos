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

import java.util.ArrayList;
import javax.swing.Action;

/**
 *
 * @author adrianromero
 */
public class MenuDefinition {
    
    private String m_sKey;
    
    private ArrayList m_aMenuElements;
    
    /** Creates a new instance of MenuDefinition */
    public MenuDefinition(String skey) {
        m_sKey = skey;
        m_aMenuElements = new ArrayList();
    }
    
    public String getKey() {
        return m_sKey;
    }
    
    public String getTitle() {
        return AppLocal.getIntString(m_sKey);
    }   
    
    public void addMenuItem(Action act) {
        MenuItemDefinition menuitem = new MenuItemDefinition(act);
        m_aMenuElements.add(menuitem);
    }
    
    public void addMenuTitle(String keytext) {
        MenuTitleDefinition menutitle = new MenuTitleDefinition();
        menutitle.KeyText = keytext;
        m_aMenuElements.add(menutitle);
    }
    
    public MenuElement getMenuElement(int i) {
        return (MenuElement) m_aMenuElements.get(i);
    }
    public int countMenuElements() {
        return m_aMenuElements.size();
    }

}
