//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2008-2009 Openbravo, S.L.
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

package com.openbravo.data.model;

/**
 *
 * @author adrian
 */
public class Table {
    
    private String name;
    private Column[] columns;
    
    public Table(String name, Column... columns) {
        this.name = name;
        this.columns = columns;
    }
    
    public String getName() {
        return name;
    }
    
    public Column[] getColumns() {
        return columns;
    }
    
    public String getListSQL() {
        StringBuffer sent = new StringBuffer();
        sent.append("select ");

        for (int i = 0; i < columns.length; i ++) {
            if (i > 0) {
                sent.append(", ");
            }
            sent.append(columns[i].getName());
        }        
        
        sent.append(" from ");        
        sent.append(name);
        
        return sent.toString();          
    }   
    
    public String getInsertSQL() {
        
        StringBuffer sent = new StringBuffer();
        StringBuffer values = new StringBuffer();
        
        sent.append("insert into ");
        sent.append(name);
        sent.append(" (");        
        
        for (int i = 0; i < columns.length; i ++) {
            if (i > 0) {
                sent.append(", ");
                values.append(", ");
            }
            sent.append(columns[i].getName());
            values.append("?");
        }
        
        sent.append(") values (");
        sent.append(values.toString());
        sent.append(")");

        return sent.toString();       
    }    
    
    public String getUpdateSQL() {
        
        StringBuffer values = new StringBuffer();
        StringBuffer filter = new StringBuffer();
        
        for (int i = 0; i < columns.length; i ++) {
            if (columns[i].isPK()) {
                if (filter.length() == 0) {
                    filter.append(" where ");
                } else  {
                    filter.append(" and ");
                }
                filter.append(columns[i].getName());
                filter.append(" = ?");                
            } else {
                if (values.length() > 0) {
                    values.append(", ");
                }
                values.append(columns[i].getName());
                values.append(" = ?");                
            }
        }
        
        return "update " + name + " set " + values + filter;             
    }   
    
    public String getDeleteSQL() {
        
        StringBuffer filter = new StringBuffer();

        for (int i = 0; i < columns.length; i ++) {
            if (columns[i].isPK()) {
                if (filter.length() == 0) {
                    filter.append(" where ");
                } else  {
                    filter.append(" and ");
                }
                filter.append(columns[i].getName());
                filter.append(" = ?"); 
            }
        }
        
        return "delete from " + name + filter;     
    }    
}
