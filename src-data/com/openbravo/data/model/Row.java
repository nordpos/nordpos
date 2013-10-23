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

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.ListCellRendererBasic;
import com.openbravo.data.loader.ComparatorCreator;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.DataWrite;
import com.openbravo.data.loader.Datas;
import com.openbravo.data.loader.IRenderString;
import com.openbravo.data.loader.PreparedSentence;
import com.openbravo.data.loader.SentenceExec;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.loader.SerializerRead;
import com.openbravo.data.loader.SerializerWrite;
import com.openbravo.data.loader.Session;
import com.openbravo.data.loader.Vectorer;
import com.openbravo.data.user.FilterEditorCreator;
import com.openbravo.data.user.ListProvider;
import com.openbravo.data.user.ListProviderCreator;
import com.openbravo.data.user.SaveProvider;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.ListCellRenderer;

/**
 *
 * @author adrian
 */
public class Row {
    
    private Field[] fields;
    
    public Row(Field... fields) {
        this.fields = fields;
    }
    
    public Vectorer getVectorer() {
        return new RowVectorer();
    }
    
    public IRenderString getRenderString() {
        return new RowRenderString();
    }    
    
    public ListCellRenderer getListCellRenderer() {  
        return new ListCellRendererBasic(new RowRenderString());  
    }
    
    public ComparatorCreator getComparatorCreator() {
        return new RowComparatorCreator();
    }

    public SentenceExec getExecSentence(Session s, String sql, final int... indexes) {
        return new PreparedSentence(s, sql, 
            new SerializerWrite<Object[]>() {
                public void writeValues(DataWrite dp, Object[] obj) throws BasicException {
                    for (int i = 0; i < indexes.length; i++) {
                        fields[indexes[i]].getData().setValue(dp, i + 1, obj[indexes[i]]);
                    }
                }            
            }
        );
    }
    
    public ListProvider getListProvider(Session s, Table t) {
        return new ListProviderCreator(getListSentence(s, t));        
    }
    
    public SaveProvider getSaveProvider(Session s, Table t) {
        return new SaveProvider(getUpdateSentence(s, t), getInsertSentence(s, t), getDeleteSentence(s, t));
    }
    
    public SentenceList getListSentence(Session s, String sql, SerializerWrite sw) {
        return new PreparedSentence(s, sql, sw, new RowSerializerRead());
    }
    
    public ListProvider getListProvider(Session s, String sql, FilterEditorCreator filter) {
        return new ListProviderCreator(getListSentence(s, sql, filter.getSerializerWrite()), filter);
    }
    
    public SentenceList getListSentence(Session s, Table t) {
        return getListSentence(s, t.getListSQL(), null);
    }
    
    public SentenceExec getInsertSentence(Session s, final Table t) {
        return new PreparedSentence(s,  t.getInsertSQL(), 
            new SerializerWrite<Object[]>() {
                public void writeValues(DataWrite dp, Object[] obj) throws BasicException {
                    for (int i = 0; i < t.getColumns().length; i++) {
                        fields[i].getData().setValue(dp, i + 1, obj[i]);
                    }           
                }            
            }
        );
    }
    
    public SentenceExec getDeleteSentence(Session s, final Table t) {
        return new PreparedSentence(s,  t.getDeleteSQL(), 
            new SerializerWrite<Object[]>() {
                public void writeValues(DataWrite dp, Object[] obj) throws BasicException {
                    int index = 1;
                    for (int i = 0; i < t.getColumns().length; i++) {
                        if (t.getColumns()[i].isPK()) {
                            fields[i].getData().setValue(dp, index++, obj[i]);
                        }
                    }           
                }            
            }
        );        
    }
    
    public SentenceExec getUpdateSentence(Session s, final Table t) {
        return new PreparedSentence(s,  t.getUpdateSQL(), 
            new SerializerWrite<Object[]>() {
                public void writeValues(DataWrite dp, Object[] obj) throws BasicException {
                    int index = 1;
                    for (int i = 0; i < t.getColumns().length; i++) {
                        if (!t.getColumns()[i].isPK()) {
                            fields[i].getData().setValue(dp, index++, obj[i]);
                        }
                    }   
                    for (int i = 0; i < t.getColumns().length; i++) {
                        if (t.getColumns()[i].isPK()) {
                            fields[i].getData().setValue(dp, index++, obj[i]);
                        }
                    }                         
                }            
            }
        );        
    }

    public Datas[] getDatas() {
        Datas[] d = new Datas[fields.length];
        for (int i = 0; i < fields.length; i++) {
            d[i] = fields[i].getData();
        }
        return d;
    }

    public SerializerRead getSerializerRead() {
        return new RowSerializerRead();
    }
    
    private class RowSerializerRead implements SerializerRead {
        public Object readValues(DataRead dr) throws BasicException {             
            Object[] m_values = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                m_values[i] = fields[i].getData().getValue(dr, i + 1);
            }
            return m_values;
        }
    }  
    
    private class RowVectorer implements Vectorer {
        public String[] getHeaders() throws BasicException {
            List<String> l = new ArrayList<String>();
            for (Field f : fields) {
                if (f.isSearchable()) {
                    l.add(f.getLabel());
                }
            }
            return l.toArray(new String[l.size()]);
        }
        public String[] getValues(Object obj) throws BasicException {   
            Object[] values = (Object[]) obj;            
            List<String> l = new ArrayList<String>();
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].isSearchable()) {
                    l.add(fields[i].getFormat().formatValue(values[i]));
                }
            }
            return l.toArray(new String[l.size()]);
        }
    }  
    
    private class RowRenderString implements IRenderString {
        public String getRenderString(Object value) {        
            Object[] values = (Object[]) value;            
            StringBuffer s = new StringBuffer();
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].isTitle()) {
                    if (s.length() > 0) {
                        s.append(" - ");
                    }
                    s.append(fields[i].getFormat().formatValue(values[i]));
                }
            }
            return s.toString();
        }
    }
    
    private class RowComparatorCreator implements ComparatorCreator {
        
        private List<Integer> comparablefields = new ArrayList<Integer>();
        
        public RowComparatorCreator() {
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].isComparable()) {
                    comparablefields.add(i);
                }
            }            
        }
        
        public String[] getHeaders() {
            String [] headers = new String [comparablefields.size()];
            for (int i = 0; i < comparablefields.size(); i++) {
                headers[i] = fields[comparablefields.get(i)].getLabel();
            }
            return headers;
        }   
        
        public Comparator createComparator(final int[] orderby) {
            return new Comparator() {
                public int compare(Object o1, Object o2) {
                    if (o1 == null) {
                        if (o2 == null) {
                            return 0;
                        } else {
                            return -1;
                        }
                    } else if (o2 == null) {
                        return +1;
                    } else {
                        Object[] ao1 = (Object[]) o1;
                        Object[] ao2 = (Object[]) o2;
                        for (int i = 0; i < orderby.length; i++) {
                            int result = fields[comparablefields.get(orderby[i])].getData().compare(
                                    ao1[comparablefields.get(orderby[i])], 
                                    ao2[comparablefields.get(orderby[i])]);
                            if (result != 0) {
                                return result;
                            }
                        }
                        return 0;
                    }
                }
            };
        }        
    }   
}
