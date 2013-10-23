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

package com.openbravo.data.loader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.openbravo.basic.BasicException;

public abstract class Datas {
    
    public final static Datas INT = new DatasINT();
    public final static Datas STRING = new DatasSTRING();
    public final static Datas DOUBLE = new DatasDOUBLE();
    public final static Datas BOOLEAN = new DatasBOOLEAN();
    public final static Datas TIMESTAMP = new DatasTIMESTAMP();
    public final static Datas BYTES = new DatasBYTES();
    public final static Datas IMAGE = new DatasIMAGE();
    //public final static Datas INPUTSTREAM = new DatasINPUTSTREAM();
    public final static Datas OBJECT = new DatasOBJECT();
    public final static Datas SERIALIZABLE = new DatasSERIALIZABLE();
    public final static Datas NULL = new DatasNULL();
    
    private static DateFormat tsf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 
    
    /** Creates a new instance of Datas */
    private Datas() {
    }
    
    public abstract Object getValue(DataRead dr, int i) throws BasicException;
    public abstract void setValue(DataWrite dw, int i, Object value) throws BasicException;
    public abstract Class getClassValue();
    protected abstract String toStringAbstract(Object value);
    protected abstract int compareAbstract(Object o1, Object o2);
    
    public String toString(Object value) {
        if (value == null) {
            return "null";
        } else {
            return toStringAbstract(value);
        }
    }
    
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
            return compareAbstract(o1, o2);
        }
    }
    
    private static final class DatasINT extends Datas {
        public Object getValue(DataRead dr, int i) throws BasicException {
            return dr.getInt(i);
        }
        public void setValue(DataWrite dw, int i, Object value) throws BasicException {
            dw.setInt(i, (Integer) value);
        }
        public Class getClassValue() {
            return java.lang.Integer.class;
        }
        protected String toStringAbstract(Object value) {
            return ((Integer) value).toString();
        }
        protected int compareAbstract(Object o1, Object o2) {
            return ((Integer) o1).compareTo((Integer) o2);
        }        
    }
    private static final class DatasSTRING extends Datas {
        public Object getValue(DataRead dr, int i) throws BasicException {
            return dr.getString(i);
        }
        public void setValue(DataWrite dw, int i, Object value) throws BasicException {
            dw.setString(i, (String) value);
        }
        public Class getClassValue() {
            return java.lang.String.class;
        }
        protected String toStringAbstract(Object value) {
            return "\'" + DataWriteUtils.getEscaped((String) value) + "\'";
        }
        protected int compareAbstract(Object o1, Object o2) {
            return ((String) o1).compareTo((String) o2);
        }           
    }
    private static final class DatasDOUBLE extends Datas {
        public Object getValue(DataRead dr, int i) throws BasicException {
            return dr.getDouble(i);
        }
        public void setValue(DataWrite dw, int i, Object value) throws BasicException {
            dw.setDouble(i, (Double) value);
        }
        public Class getClassValue() {
            return java.lang.Double.class;
        }
        protected String toStringAbstract(Object value) {
            return ((Double) value).toString();
        }
        protected int compareAbstract(Object o1, Object o2) {
            return ((Double) o1).compareTo((Double) o2);
        }   
    }
    private static final class DatasBOOLEAN extends Datas {
        public Object getValue(DataRead dr, int i) throws BasicException {
            return dr.getBoolean(i);
        }
        public void setValue(DataWrite dw, int i, Object value) throws BasicException {
            dw.setBoolean(i, (Boolean) value);
        }
        public Class getClassValue() {
            return java.lang.Boolean.class;
        }
        protected String toStringAbstract(Object value) {
            return ((Boolean) value).toString();
        }
        protected int compareAbstract(Object o1, Object o2) {
            return ((Boolean) o1).compareTo((Boolean) o2);
        }   
    }
    private static final class DatasTIMESTAMP extends Datas {
        public Object getValue(DataRead dr, int i) throws BasicException {
            return dr.getTimestamp(i);
        }
         public void setValue(DataWrite dw, int i, Object value) throws BasicException {
            dw.setTimestamp(i, (java.util.Date) value);
        }
        public Class getClassValue() {
            return java.util.Date.class;
        }
        protected String toStringAbstract(Object value) {
            return tsf.format(value);
        }
        protected int compareAbstract(Object o1, Object o2) {
            return ((java.util.Date) o1).compareTo((java.util.Date) o2);
        }   
    }
    private static final class DatasBYTES extends Datas {
        public Object getValue(DataRead dr, int i) throws BasicException {
            return dr.getBytes(i);
        }
        public void setValue(DataWrite dw, int i, Object value) throws BasicException {
            dw.setBytes(i, (byte[]) value);
        }
        public Class getClassValue() {
            return byte[].class;
        }
        protected String toStringAbstract(Object value) {
            return "0x" + ImageUtils.bytes2hex((byte[]) value);
        }
        protected int compareAbstract(Object o1, Object o2) {
            throw new UnsupportedOperationException();
        }   
    }    
    private static final class DatasIMAGE extends Datas {
        public Object getValue(DataRead dr, int i) throws BasicException {
            return ImageUtils.readImage(dr.getBytes(i));
        }
        public void setValue(DataWrite dw, int i, Object value) throws BasicException {
            dw.setBytes(i, ImageUtils.writeImage((java.awt.image.BufferedImage) value));
        }
        public Class getClassValue() {
            return java.awt.image.BufferedImage.class;
        }
        protected String toStringAbstract(Object value) {
            return "0x" + ImageUtils.bytes2hex(ImageUtils.writeImage((java.awt.image.BufferedImage) value));
        }
        protected int compareAbstract(Object o1, Object o2) {
            throw new UnsupportedOperationException();
        }   
    }  
//    private static final class DatasINPUTSTREAM extends Datas {
//        public Object getValue(DataRead dr, int i) throws DataException {
//            byte[] b = dr.getBytes(i);
//            return b == null ? null : new java.io.ByteArrayInputStream(b);
//        }
//        public void setValue(DataWrite dw, int i, Object value) throws DataException {
//            // TODO: Please implement this method
//        }
//    }  
    private static final class DatasOBJECT extends Datas {
        public Object getValue(DataRead dr, int i) throws BasicException {
            return dr.getObject(i);
        }
        public void setValue(DataWrite dw, int i, Object value) throws BasicException {
            dw.setObject(i, value);
        }
        public Class getClassValue() {
            return java.lang.Object.class;
        }
        protected String toStringAbstract(Object value) {
            return "0x" + ImageUtils.bytes2hex(ImageUtils.writeSerializable(value));
        }
        protected int compareAbstract(Object o1, Object o2) {
            throw new UnsupportedOperationException();
        }   
    }
    
    private static final class DatasSERIALIZABLE extends Datas {
        public Object getValue(DataRead dr, int i) throws BasicException {
            return ImageUtils.readSerializable(dr.getBytes(i));
        }
        public void setValue(DataWrite dw, int i, Object value) throws BasicException {
            dw.setBytes(i, ImageUtils.writeSerializable(value));
        }
        public Class getClassValue() {
            return java.lang.Object.class;
        }
        protected String toStringAbstract(Object value) {
            return "0x" + ImageUtils.bytes2hex(ImageUtils.writeSerializable(value));
        }
        protected int compareAbstract(Object o1, Object o2) {
            throw new UnsupportedOperationException();
        }   
    }       
    
    private static final class DatasNULL extends Datas {
        public Object getValue(DataRead dr, int i) throws BasicException {
            return null;
        }
        public void setValue(DataWrite dw, int i, Object value) throws BasicException {
            // No asigno null, no asigno nada.
        }
        public Class getClassValue() {
            return java.lang.Object.class;
        }
        protected String toStringAbstract(Object value) {
            return "null";
        }
        protected int compareAbstract(Object o1, Object o2) {
            throw new UnsupportedOperationException();
        }   
    }    
}
