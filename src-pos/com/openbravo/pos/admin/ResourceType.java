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

package com.openbravo.pos.admin;

import com.openbravo.pos.panels.ComboItemLocal;

/**
 *
 * @author adrianromero
 * Created on February 12, 2007, 11:50 PM
 *
 */
public class ResourceType extends ComboItemLocal {
    
    public static final ResourceType TEXT = new ResourceType(0, "resource.text");
    public static final ResourceType IMAGE = new ResourceType(1, "resource.image");
    public static final ResourceType BINARY = new ResourceType(2, "resource.binary");
    
    private ResourceType(Integer iKey, String sKeyValue) {
        super(iKey, sKeyValue);
    }     
}
