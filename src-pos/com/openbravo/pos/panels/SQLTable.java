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

import java.util.Enumeration;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;

/**
 *
 * @author adrianromero
 */
public class SQLTable implements TreeNode { 
    
    private SQLDatabase m_db;
    private String m_sName;
    
    private ArrayList m_aColumns;
    
    /** Creates a new instance of SQLTable */
    public SQLTable(SQLDatabase db, String name) {
        m_db = db;
        m_sName = name;
        m_aColumns = new ArrayList();
    }
    
    public String getName() {
        return m_sName;
    }
    
    public void addColumn(String name) {
        SQLColumn c = new SQLColumn(this, name);
        m_aColumns.add(c);
    }
    
    public String toString() {
        return m_sName;
    }
    
    public Enumeration children(){
        return new EnumerationIter(m_aColumns.iterator());
    }
    public boolean getAllowsChildren() {
        return true;
    }
    public TreeNode getChildAt(int childIndex) {
        return (TreeNode) m_aColumns.get(childIndex);
    }
    public int getChildCount() {
        return m_aColumns.size();
    }
    public int getIndex(TreeNode node){
        return m_aColumns.indexOf(node);
    }
    public TreeNode getParent() {
        return m_db;
    }
    public boolean isLeaf() {
        return m_aColumns.size() == 0;
    }   
//    public Enumeration children(){
//    }
//    public boolean getAllowsChildren() {
//    }
//    public TreeNode getChildAt(int childIndex) {
//    }
//    public int getChildCount() {
//    }
//    public int getIndex(TreeNode node){
//    }
//    public TreeNode getParent() {
//    }
//    public boolean isLeaf() {
//    }  
}
