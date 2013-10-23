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

package com.openbravo.data.user;

import java.util.*;
import javax.swing.*;
import java.awt.Component;
import javax.swing.event.EventListenerList;
import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.LocalRes;

public class BrowsableEditableData {
    
    public static final int ST_NORECORD = 0;
    public static final int ST_UPDATE = 1;
    public static final int ST_DELETE = 2;
    public static final int ST_INSERT = 3;
    
    private final static int INX_EOF = -1;
    
    private BrowsableData m_bd;
    
    protected EventListenerList listeners = new EventListenerList();

    private EditorRecord m_editorrecord;
    private DirtyManager m_Dirty;
    private int m_iState; // vinculado siempre al m_editorrecord
//    private DocumentLoader m_keyvalue;
    private int m_iIndex;
    private boolean m_bIsAdjusting;
    
    private boolean iseditable = true;
    
    /** Creates a new instance of BrowsableEditableData */
    public BrowsableEditableData(BrowsableData bd, EditorRecord ed, DirtyManager dirty) {
        m_bd = bd;

        m_editorrecord = ed;
        m_Dirty = dirty;
        m_iState = ST_NORECORD;
        m_iIndex = INX_EOF; // En EOF
        m_bIsAdjusting = false;
//        m_keyvalue = DocumentLoaderBasic.INSTANCE;
        
        // Inicializo ?
        m_editorrecord.writeValueEOF();
        m_Dirty.setDirty(false);
    }
    
    public BrowsableEditableData(ListProvider dataprov, SaveProvider saveprov, Comparator c, EditorRecord ed, DirtyManager dirty) {
        this(new BrowsableData(dataprov, saveprov, c), ed, dirty);
    }
    public BrowsableEditableData(ListProvider dataprov, SaveProvider saveprov, EditorRecord ed, DirtyManager dirty) {
        this(new BrowsableData(dataprov, saveprov, null), ed, dirty);
    }    
 
    public final ListModel getListModel() {
        return m_bd;
    }
    public final boolean isAdjusting() {
        return m_bIsAdjusting || m_bd.isAdjusting();
    }
    
    private final Object getCurrentElement() {           
        return (m_iIndex >= 0 && m_iIndex < m_bd.getSize()) ? m_bd.getElementAt(m_iIndex) : null;
    }    
    public final int getIndex() {
        return m_iIndex;
    }   
    
    public final void addStateListener(StateListener l) {
        listeners.add(StateListener.class, l);
    }
    public final void removeStateListener(StateListener l) {
        listeners.remove(StateListener.class, l);
    }
    public final void addEditorListener(EditorListener l) {
        listeners.add(EditorListener.class, l);
    }
    public final void removeEditorListener(EditorListener l) {
        listeners.remove(EditorListener.class, l);
    }
    public final void addBrowseListener(BrowseListener l) {
        listeners.add(BrowseListener.class, l);
    }
    public final void removeBrowseListener(BrowseListener l) {
        listeners.remove(BrowseListener.class, l);
    }   
    
    public int getState() {
        return m_iState;
    }
    
    private void fireStateUpdate() { 
        EventListener[] l = listeners.getListeners(StateListener.class);
        int iState = getState();
        for (int i = 0; i < l.length; i++) {
            ((StateListener) l[i]).updateState(iState);	       
        }
    }
    protected void fireDataBrowse() { 
        
        m_bIsAdjusting = true;
        // Lanzamos los eventos...
        Object obj = getCurrentElement();
        int iIndex = getIndex();
        int iCount = m_bd.getSize();
        
        // actualizo el registro
        if (obj == null) {
            m_iState = ST_NORECORD;
            m_editorrecord.writeValueEOF();
        } else {
            m_iState = ST_UPDATE;
            m_editorrecord.writeValueEdit(obj);
        }
        m_Dirty.setDirty(false);
        fireStateUpdate();   
        
        // Invoco a los Editor Listener
        EventListener[] l = listeners.getListeners(EditorListener.class);
        for (int i = 0; i < l.length; i++) {
            ((EditorListener) l[i]).updateValue(obj);
        }
        // Y luego a los Browse Listener
        l = listeners.getListeners(BrowseListener.class);
        for (int i = 0; i < l.length; i++) {
            ((BrowseListener) l[i]).updateIndex(iIndex, iCount);
        }
        m_bIsAdjusting = false;
    }
    
    
    public boolean canLoadData() {
        return m_bd.canLoadData();
    }
    
    public void setEditable(boolean value) {
        iseditable = value;
    }
    
    public boolean canInsertData() {
        return iseditable && m_bd.canInsertData();          
    }
    
    public boolean canDeleteData() {
        return iseditable && m_bd.canDeleteData();      
    }
    
    public boolean canUpdateData() {
        return iseditable && m_bd.canUpdateData();      
    }
        
    public void refreshCurrent() {
        baseMoveTo(m_iIndex);
    }    

    public void refreshData() throws BasicException {
        saveData();
        m_bd.refreshData();
        m_editorrecord.refresh();
        baseMoveTo(0);
    }    
    public void loadData() throws BasicException {
        saveData();
        m_bd.loadData();
        m_editorrecord.refresh();
        baseMoveTo(0);
    }
    public void unloadData() throws BasicException {
        saveData();
        m_bd.unloadData();
        m_editorrecord.refresh();
        baseMoveTo(0);
    }
  
    public void sort(Comparator c) throws BasicException {
        saveData();
        m_bd.sort(c);
        baseMoveTo(0);
    }
    
    public void moveTo(int i) throws BasicException {        
        saveData();
        if (m_iIndex != i) {
            baseMoveTo(i);
        }
    }    
    
    public final void movePrev() throws BasicException {
        saveData();
        if (m_iIndex > 0) {        
            baseMoveTo(m_iIndex - 1);
        }
    }
    public final void moveNext() throws BasicException {
        saveData();
        if (m_iIndex < m_bd.getSize() - 1) {        
            baseMoveTo(m_iIndex + 1);
        }
    }
    public final void moveFirst() throws BasicException {
        saveData();
        if (m_bd.getSize() > 0) {
            baseMoveTo(0);
        }
    }
    public final void moveLast() throws BasicException {
        saveData();
        if (m_bd.getSize() > 0) {
            baseMoveTo(m_bd.getSize() - 1);
        }
    }
    public final int findNext(Finder f) throws BasicException {
        return m_bd.findNext(m_iIndex, f);
    }
    
    public void saveData() throws BasicException {
            
        if (m_Dirty.isDirty()) {
            if (m_iState == ST_UPDATE) {
                int i = m_bd.updateRecord(m_iIndex, m_editorrecord.createValue());
                m_editorrecord.refresh();
                baseMoveTo(i);
            } else if (m_iState == ST_INSERT) {
                int i = m_bd.insertRecord(m_editorrecord.createValue());
                m_editorrecord.refresh();
                baseMoveTo(i);
            } else if (m_iState == ST_DELETE) {
                int i = m_bd.removeRecord(m_iIndex);
                m_editorrecord.refresh();
                baseMoveTo(i);
            } // queda ST_NORECORD  
        }   
    }
      
    public void actionReloadCurrent(Component c) {        
        if (!m_Dirty.isDirty() ||
                JOptionPane.showConfirmDialog(c, LocalRes.getIntString("message.changeslost"), LocalRes.getIntString("title.editor"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {  
            refreshCurrent();
        }             
    }
  
    public boolean actionClosingForm(Component c) throws BasicException {
        if (m_Dirty.isDirty()) {
            int res = JOptionPane.showConfirmDialog(c, LocalRes.getIntString("message.wannasave"), LocalRes.getIntString("title.editor"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (res == JOptionPane.YES_OPTION) {
                saveData();
                return true;
            } else if (res == JOptionPane.NO_OPTION) {
                refreshCurrent();
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    /*
     * Metodos publicos finales (algunos protegidos que podrian ser finales
     */
    
    public final void actionLoad() throws BasicException {
        loadData();
        if (m_bd.getSize() == 0) {
            actionInsert();
        }
    }
    
    public final void actionInsert() throws BasicException {
        // primero persistimos
        saveData();
        
        if (canInsertData()) {       
            // Y nos ponemos en estado de insert
            m_iState = ST_INSERT;
            m_editorrecord.writeValueInsert();
            m_Dirty.setDirty(false);
            fireStateUpdate(); // ?
        }
    }
    
    public final void actionDelete() throws BasicException {
        // primero persistimos
        saveData();
        
        if (canDeleteData()) {
        
            // Y nos ponemos en estado de delete
            Object obj = getCurrentElement();
            int iIndex = getIndex();
            int iCount = m_bd.getSize();
            if (iIndex >= 0 && iIndex < iCount) {
                m_iState = ST_DELETE;
                m_editorrecord.writeValueDelete(obj);
                m_Dirty.setDirty(true);
                fireStateUpdate(); // ?
            }
        }
    }   
    
    private final void baseMoveTo(int i) {
    // Este senor y el constructor a INX_EOF, son los unicos que tienen potestad de modificar m_iIndex.
        if (i >= 0 && i < m_bd.getSize()) {
            m_iIndex = i;
        } else {
            m_iIndex = INX_EOF;
        }
        fireDataBrowse();
    }    
}
