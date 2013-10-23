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

package com.openbravo.format;

import java.text.ParseException;

public abstract class FormatsConstrain {
   
//    public final static FormatsConstrain NOTNULL = new FormatsConstrainNOTNULL();
    
    public abstract Object check(Object value) throws ParseException;  
    
    public FormatsConstrain() {
    }
    
//    private static class FormatsConstrainNOTNULL extends FormatsConstrain {
//        public Object check(Object value) throws ParseException{
//            if (value == null) {
//                throw new ParseException(LocalRes.getIntString("exception.notnull"), 0);
//            } else {
//                return value;
//            }
//        }    
//    }        
}
