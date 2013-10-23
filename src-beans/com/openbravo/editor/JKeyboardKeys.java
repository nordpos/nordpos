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

package com.openbravo.editor;

import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;
import javax.swing.event.EventListenerList;

/**
 *
 * @author adrianromero
 * @author Andrey Svininykh <svininykh@gmail.com>
 */

public final class JKeyboardKeys extends javax.swing.JPanel implements EditorKeys {

    private final static char[] SET_DOUBLE = {'\u007f', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '-'};
    private final static char[] SET_DOUBLE_POSITIVE = {'\u007f', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'};
    private final static char[] SET_INTEGER = {'\u007f', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-'};
    private final static char[] SET_INTEGER_POSITIVE = {'\u007f', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    protected EventListenerList listeners = new EventListenerList();

    private EditorComponent editorcurrent ;

    private char[] keysavailable;
    private boolean m_bMinus;
    private boolean m_bKeyDot;
    private boolean m_bKeyAlphabet;

    /** Creates new form JEditorKeys */
    public JKeyboardKeys() {
        initComponents();

        m_jKey0.addActionListener(new MyKeyNumberListener('0'));
        m_jKey1.addActionListener(new MyKeyNumberListener('1'));
        m_jKey2.addActionListener(new MyKeyNumberListener('2'));
        m_jKey3.addActionListener(new MyKeyNumberListener('3'));
        m_jKey4.addActionListener(new MyKeyNumberListener('4'));
        m_jKey5.addActionListener(new MyKeyNumberListener('5'));
        m_jKey6.addActionListener(new MyKeyNumberListener('6'));
        m_jKey7.addActionListener(new MyKeyNumberListener('7'));
        m_jKey8.addActionListener(new MyKeyNumberListener('8'));
        m_jKey9.addActionListener(new MyKeyNumberListener('9'));
        m_jKeyQ.addActionListener(new MyKeyNumberListener('Q'));
        m_jKeyW.addActionListener(new MyKeyNumberListener('W'));
        m_jKeyE.addActionListener(new MyKeyNumberListener('E'));
        m_jKeyR.addActionListener(new MyKeyNumberListener('R'));
        m_jKeyT.addActionListener(new MyKeyNumberListener('T'));
        m_jKeyY.addActionListener(new MyKeyNumberListener('Y'));
        m_jKeyU.addActionListener(new MyKeyNumberListener('U'));
        m_jKeyI.addActionListener(new MyKeyNumberListener('I'));
        m_jKeyO.addActionListener(new MyKeyNumberListener('O'));
        m_jKeyP.addActionListener(new MyKeyNumberListener('P'));
        m_jKeyA.addActionListener(new MyKeyNumberListener('A'));
        m_jKeyS.addActionListener(new MyKeyNumberListener('S'));
        m_jKeyD.addActionListener(new MyKeyNumberListener('D'));
        m_jKeyF.addActionListener(new MyKeyNumberListener('F'));
        m_jKeyG.addActionListener(new MyKeyNumberListener('G'));
        m_jKeyH.addActionListener(new MyKeyNumberListener('H'));
        m_jKeyJ.addActionListener(new MyKeyNumberListener('J'));
        m_jKeyK.addActionListener(new MyKeyNumberListener('K'));
        m_jKeyL.addActionListener(new MyKeyNumberListener('L'));
        m_jKeyZ.addActionListener(new MyKeyNumberListener('Z'));
        m_jKeyX.addActionListener(new MyKeyNumberListener('X'));
        m_jKeyC.addActionListener(new MyKeyNumberListener('C'));
        m_jKeyV.addActionListener(new MyKeyNumberListener('V'));
        m_jKeyB.addActionListener(new MyKeyNumberListener('B'));
        m_jKeyN.addActionListener(new MyKeyNumberListener('N'));
        m_jKeyM.addActionListener(new MyKeyNumberListener('M'));
        m_jKeyComma.addActionListener(new MyKeyNumberListener(','));
        m_jKeySpace.addActionListener(new MyKeyNumberListener(' '));
        m_jKeyDot.addActionListener(new MyKeyNumberListener('.'));
        m_jKeyAmpersand.addActionListener(new MyKeyNumberListener('&'));
        m_jKeyAt.addActionListener(new MyKeyNumberListener('@'));
        m_jKeyColon.addActionListener(new MyKeyNumberListener(':'));
        m_jKeyLSquareBrack.addActionListener(new MyKeyNumberListener('['));
        m_jKeyRSquareBrack.addActionListener(new MyKeyNumberListener(']'));
        m_jKeyQuotation.addActionListener(new MyKeyNumberListener('"'));
        m_jKeySlash.addActionListener(new MyKeyNumberListener('/'));
        m_jCE.addActionListener(new MyKeyNumberListener('\u007f'));
        m_jMinus.addActionListener(new MyKeyNumberListener('-'));
        m_jBackspace.addActionListener(new MyKeyNumberListener('\u0008'));
        m_jMode.addActionListener(new MyKeyNumberListener('\u0010'));

        editorcurrent = null;
        setMode(MODE_STRING);
        doEnabled(false);
    }

    @Override
    public void setComponentOrientation(ComponentOrientation o) {
        // Nothing to change
    }

    public void addActionListener(ActionListener l) {
        listeners.add(ActionListener.class, l);
    }
    public void removeActionListener(ActionListener l) {
        listeners.remove(ActionListener.class, l);
    }

    protected void fireActionPerformed() {
        EventListener[] l = listeners.getListeners(ActionListener.class);
        ActionEvent e = null;
        for (int i = 0; i < l.length; i++) {
            if (e == null) {
                e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null);
            }
            ((ActionListener) l[i]).actionPerformed(e);
        }
    }

    private void doEnabled(boolean b) {
        m_jKey0.setEnabled(b);
        m_jKey1.setEnabled(b);
        m_jKey2.setEnabled(b);
        m_jKey3.setEnabled(b);
        m_jKey4.setEnabled(b);
        m_jKey5.setEnabled(b);
        m_jKey6.setEnabled(b);
        m_jKey7.setEnabled(b);
        m_jKey8.setEnabled(b);
        m_jKey9.setEnabled(b);
        m_jKeyQ.setEnabled(b && m_bKeyAlphabet);
        m_jKeyW.setEnabled(b && m_bKeyAlphabet);
        m_jKeyE.setEnabled(b && m_bKeyAlphabet);
        m_jKeyR.setEnabled(b && m_bKeyAlphabet);
        m_jKeyT.setEnabled(b && m_bKeyAlphabet);
        m_jKeyY.setEnabled(b && m_bKeyAlphabet);
        m_jKeyU.setEnabled(b && m_bKeyAlphabet);
        m_jKeyI.setEnabled(b && m_bKeyAlphabet);
        m_jKeyO.setEnabled(b && m_bKeyAlphabet);
        m_jKeyP.setEnabled(b && m_bKeyAlphabet);
        m_jKeyA.setEnabled(b && m_bKeyAlphabet);
        m_jKeyS.setEnabled(b && m_bKeyAlphabet);
        m_jKeyD.setEnabled(b && m_bKeyAlphabet);
        m_jKeyF.setEnabled(b && m_bKeyAlphabet);
        m_jKeyG.setEnabled(b && m_bKeyAlphabet);
        m_jKeyH.setEnabled(b && m_bKeyAlphabet);
        m_jKeyJ.setEnabled(b && m_bKeyAlphabet);
        m_jKeyK.setEnabled(b && m_bKeyAlphabet);
        m_jKeyL.setEnabled(b && m_bKeyAlphabet);
        m_jKeyZ.setEnabled(b && m_bKeyAlphabet);
        m_jKeyX.setEnabled(b && m_bKeyAlphabet);
        m_jKeyC.setEnabled(b && m_bKeyAlphabet);
        m_jKeyV.setEnabled(b && m_bKeyAlphabet);
        m_jKeyB.setEnabled(b && m_bKeyAlphabet);
        m_jKeyN.setEnabled(b && m_bKeyAlphabet);
        m_jKeyM.setEnabled(b && m_bKeyAlphabet);
        m_jKeyComma.setEnabled(b && m_bKeyAlphabet);
        m_jKeySpace.setEnabled(b && m_bKeyAlphabet);

        m_jKeyDot.setEnabled(b && m_bKeyDot);
        m_jCE.setEnabled(b);
        m_jBackspace.setEnabled(b);
        m_jMinus.setEnabled(b && m_bMinus);
        m_jMode.setEnabled(b);
    }

    public void setMode(int iMode) {
        switch (iMode) {
            case MODE_DOUBLE:
                m_bMinus = true;
                m_bKeyDot = true;
                m_bKeyAlphabet = false;
                keysavailable = SET_DOUBLE;
                break;
            case MODE_DOUBLE_POSITIVE:
                m_bMinus = false;
                m_bKeyDot = true;
                m_bKeyAlphabet = false;
                keysavailable = SET_DOUBLE_POSITIVE;
                break;
            case MODE_INTEGER:
                m_bMinus = true;
                m_bKeyDot = false;
                m_bKeyAlphabet = false;
                keysavailable = SET_INTEGER;
                break;
           case MODE_INTEGER_POSITIVE:
                m_bMinus = false;
                m_bKeyDot = false;
                m_bKeyAlphabet = false;
                keysavailable = SET_INTEGER_POSITIVE;
                break;
            case MODE_STRING:
            default:
                m_bMinus = true;
                m_bKeyDot = true;
                m_bKeyAlphabet = true;
                keysavailable = null;
                break;
        }
    }

    private class MyKeyNumberListener implements java.awt.event.ActionListener {

        private char m_cCad;

        public MyKeyNumberListener(char cCad){
            m_cCad = cCad;
        }
        public void actionPerformed(java.awt.event.ActionEvent evt) {

            // como contenedor de editores
            if (editorcurrent != null) {
                editorcurrent.transChar(m_cCad);
            }
        }
    }

    public void setActive(EditorComponent e, int iMode) {

        if (editorcurrent != null) {
            editorcurrent.deactivate();
        }
        editorcurrent = e;  // e != null
        setMode(iMode);
        doEnabled(true);

        // keyboard listener activation
        m_txtKeys.setText(null);
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                m_txtKeys.requestFocus();
            }
        });
    }

    public void setInactive(EditorComponent e) {

        if (e == editorcurrent && editorcurrent != null) { // e != null
            editorcurrent.deactivate();
            editorcurrent = null;
            setMode(MODE_STRING);
            doEnabled(false);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jKey1 = new javax.swing.JButton();
        m_jKey2 = new javax.swing.JButton();
        m_jKey3 = new javax.swing.JButton();
        m_jKey4 = new javax.swing.JButton();
        m_jKey5 = new javax.swing.JButton();
        m_jKey6 = new javax.swing.JButton();
        m_jKey7 = new javax.swing.JButton();
        m_jKey8 = new javax.swing.JButton();
        m_jKey9 = new javax.swing.JButton();
        m_jKey0 = new javax.swing.JButton();
        m_jMinus = new javax.swing.JButton();
        m_jKeyDot = new javax.swing.JButton();
        m_txtKeys = new javax.swing.JTextField();
        m_jKeyQ = new javax.swing.JButton();
        m_jKeyW = new javax.swing.JButton();
        m_jKeyE = new javax.swing.JButton();
        m_jKeyR = new javax.swing.JButton();
        m_jKeyT = new javax.swing.JButton();
        m_jKeyY = new javax.swing.JButton();
        m_jKeyU = new javax.swing.JButton();
        m_jKeyI = new javax.swing.JButton();
        m_jKeyO = new javax.swing.JButton();
        m_jKeyP = new javax.swing.JButton();
        m_jKeyA = new javax.swing.JButton();
        m_jKeyZ = new javax.swing.JButton();
        m_jKeySpace = new javax.swing.JButton();
        m_jKeyS = new javax.swing.JButton();
        m_jKeyX = new javax.swing.JButton();
        m_jKeyD = new javax.swing.JButton();
        m_jKeyC = new javax.swing.JButton();
        m_jKeyF = new javax.swing.JButton();
        m_jKeyV = new javax.swing.JButton();
        m_jKeyG = new javax.swing.JButton();
        m_jKeyB = new javax.swing.JButton();
        m_jKeyH = new javax.swing.JButton();
        m_jKeyN = new javax.swing.JButton();
        m_jKeyJ = new javax.swing.JButton();
        m_jKeyM = new javax.swing.JButton();
        m_jKeyK = new javax.swing.JButton();
        m_jKeyL = new javax.swing.JButton();
        m_jKeyComma = new javax.swing.JButton();
        m_jBackspace = new javax.swing.JButton();
        m_jMode = new javax.swing.JButton();
        m_jCE = new javax.swing.JButton();
        m_jKeyLSquareBrack = new javax.swing.JButton();
        m_jKeyRSquareBrack = new javax.swing.JButton();
        m_jKeyColon = new javax.swing.JButton();
        m_jKeyQuotation = new javax.swing.JButton();
        m_jKeySlash = new javax.swing.JButton();
        m_jKeyAmpersand = new javax.swing.JButton();
        m_jKeyAt = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setMinimumSize(new java.awt.Dimension(800, 320));
        setPreferredSize(new java.awt.Dimension(800, 320));

        m_jKey1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/btn1.png"))); // NOI18N
        m_jKey1.setFocusPainted(false);
        m_jKey1.setFocusable(false);
        m_jKey1.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jKey1.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKey1.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKey1.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKey1.setRequestFocusEnabled(false);

        m_jKey2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/btn2.png"))); // NOI18N
        m_jKey2.setFocusPainted(false);
        m_jKey2.setFocusable(false);
        m_jKey2.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jKey2.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKey2.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKey2.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKey2.setRequestFocusEnabled(false);

        m_jKey3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/btn3.png"))); // NOI18N
        m_jKey3.setFocusPainted(false);
        m_jKey3.setFocusable(false);
        m_jKey3.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jKey3.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKey3.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKey3.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKey3.setRequestFocusEnabled(false);

        m_jKey4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/btn4.png"))); // NOI18N
        m_jKey4.setFocusPainted(false);
        m_jKey4.setFocusable(false);
        m_jKey4.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jKey4.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKey4.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKey4.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKey4.setRequestFocusEnabled(false);

        m_jKey5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/btn5.png"))); // NOI18N
        m_jKey5.setFocusPainted(false);
        m_jKey5.setFocusable(false);
        m_jKey5.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jKey5.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKey5.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKey5.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKey5.setRequestFocusEnabled(false);

        m_jKey6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/btn6.png"))); // NOI18N
        m_jKey6.setFocusPainted(false);
        m_jKey6.setFocusable(false);
        m_jKey6.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jKey6.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKey6.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKey6.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKey6.setRequestFocusEnabled(false);

        m_jKey7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/btn7.png"))); // NOI18N
        m_jKey7.setFocusPainted(false);
        m_jKey7.setFocusable(false);
        m_jKey7.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jKey7.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKey7.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKey7.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKey7.setRequestFocusEnabled(false);

        m_jKey8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/btn8.png"))); // NOI18N
        m_jKey8.setFocusPainted(false);
        m_jKey8.setFocusable(false);
        m_jKey8.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jKey8.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKey8.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKey8.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKey8.setRequestFocusEnabled(false);

        m_jKey9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/btn9.png"))); // NOI18N
        m_jKey9.setFocusPainted(false);
        m_jKey9.setFocusable(false);
        m_jKey9.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jKey9.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKey9.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKey9.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKey9.setRequestFocusEnabled(false);

        m_jKey0.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/btn0.png"))); // NOI18N
        m_jKey0.setFocusPainted(false);
        m_jKey0.setFocusable(false);
        m_jKey0.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jKey0.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKey0.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKey0.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKey0.setRequestFocusEnabled(false);

        m_jMinus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/btnminus.png"))); // NOI18N
        m_jMinus.setFocusPainted(false);
        m_jMinus.setFocusable(false);
        m_jMinus.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jMinus.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jMinus.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jMinus.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jMinus.setRequestFocusEnabled(false);

        m_jKeyDot.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/btndot.png"))); // NOI18N
        m_jKeyDot.setFocusPainted(false);
        m_jKeyDot.setFocusable(false);
        m_jKeyDot.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jKeyDot.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyDot.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyDot.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyDot.setRequestFocusEnabled(false);

        m_txtKeys.setPreferredSize(new java.awt.Dimension(0, 0));
        m_txtKeys.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                m_txtKeysFocusLost(evt);
            }
        });
        m_txtKeys.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                m_txtKeysKeyTyped(evt);
            }
        });

        m_jKeyQ.setText("Q");
        m_jKeyQ.setFocusPainted(false);
        m_jKeyQ.setFocusable(false);
        m_jKeyQ.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyQ.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyQ.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyQ.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyQ.setRequestFocusEnabled(false);

        m_jKeyW.setText("W");
        m_jKeyW.setFocusPainted(false);
        m_jKeyW.setFocusable(false);
        m_jKeyW.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyW.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyW.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyW.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyW.setRequestFocusEnabled(false);

        m_jKeyE.setText("E");
        m_jKeyE.setFocusPainted(false);
        m_jKeyE.setFocusable(false);
        m_jKeyE.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyE.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyE.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyE.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyE.setRequestFocusEnabled(false);

        m_jKeyR.setText("R");
        m_jKeyR.setFocusPainted(false);
        m_jKeyR.setFocusable(false);
        m_jKeyR.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyR.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyR.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyR.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyR.setRequestFocusEnabled(false);

        m_jKeyT.setText("T");
        m_jKeyT.setFocusPainted(false);
        m_jKeyT.setFocusable(false);
        m_jKeyT.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyT.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyT.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyT.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyT.setRequestFocusEnabled(false);

        m_jKeyY.setText("Y");
        m_jKeyY.setFocusPainted(false);
        m_jKeyY.setFocusable(false);
        m_jKeyY.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyY.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyY.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyY.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyY.setRequestFocusEnabled(false);

        m_jKeyU.setText("U");
        m_jKeyU.setFocusPainted(false);
        m_jKeyU.setFocusable(false);
        m_jKeyU.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyU.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyU.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyU.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyU.setRequestFocusEnabled(false);

        m_jKeyI.setText("I");
        m_jKeyI.setFocusPainted(false);
        m_jKeyI.setFocusable(false);
        m_jKeyI.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyI.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyI.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyI.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyI.setRequestFocusEnabled(false);

        m_jKeyO.setText("O");
        m_jKeyO.setFocusPainted(false);
        m_jKeyO.setFocusable(false);
        m_jKeyO.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyO.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyO.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyO.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyO.setRequestFocusEnabled(false);

        m_jKeyP.setText("P");
        m_jKeyP.setFocusPainted(false);
        m_jKeyP.setFocusable(false);
        m_jKeyP.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyP.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyP.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyP.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyP.setRequestFocusEnabled(false);

        m_jKeyA.setText("A");
        m_jKeyA.setFocusPainted(false);
        m_jKeyA.setFocusable(false);
        m_jKeyA.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyA.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyA.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyA.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyA.setRequestFocusEnabled(false);

        m_jKeyZ.setText("Z");
        m_jKeyZ.setFocusPainted(false);
        m_jKeyZ.setFocusable(false);
        m_jKeyZ.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyZ.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyZ.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyZ.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyZ.setRequestFocusEnabled(false);

        m_jKeySpace.setFocusPainted(false);
        m_jKeySpace.setFocusable(false);
        m_jKeySpace.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeySpace.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeySpace.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeySpace.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeySpace.setRequestFocusEnabled(false);

        m_jKeyS.setText("S");
        m_jKeyS.setFocusPainted(false);
        m_jKeyS.setFocusable(false);
        m_jKeyS.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyS.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyS.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyS.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyS.setRequestFocusEnabled(false);

        m_jKeyX.setText("X");
        m_jKeyX.setFocusPainted(false);
        m_jKeyX.setFocusable(false);
        m_jKeyX.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyX.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyX.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyX.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyX.setRequestFocusEnabled(false);

        m_jKeyD.setText("D");
        m_jKeyD.setFocusPainted(false);
        m_jKeyD.setFocusable(false);
        m_jKeyD.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyD.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyD.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyD.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyD.setRequestFocusEnabled(false);

        m_jKeyC.setText("C");
        m_jKeyC.setFocusPainted(false);
        m_jKeyC.setFocusable(false);
        m_jKeyC.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyC.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyC.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyC.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyC.setRequestFocusEnabled(false);

        m_jKeyF.setText("F");
        m_jKeyF.setFocusPainted(false);
        m_jKeyF.setFocusable(false);
        m_jKeyF.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyF.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyF.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyF.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyF.setRequestFocusEnabled(false);

        m_jKeyV.setText("V");
        m_jKeyV.setFocusPainted(false);
        m_jKeyV.setFocusable(false);
        m_jKeyV.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyV.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyV.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyV.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyV.setRequestFocusEnabled(false);

        m_jKeyG.setText("G");
        m_jKeyG.setFocusPainted(false);
        m_jKeyG.setFocusable(false);
        m_jKeyG.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyG.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyG.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyG.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyG.setRequestFocusEnabled(false);

        m_jKeyB.setText("B");
        m_jKeyB.setFocusPainted(false);
        m_jKeyB.setFocusable(false);
        m_jKeyB.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyB.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyB.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyB.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyB.setRequestFocusEnabled(false);

        m_jKeyH.setText("H");
        m_jKeyH.setFocusPainted(false);
        m_jKeyH.setFocusable(false);
        m_jKeyH.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyH.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyH.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyH.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyH.setRequestFocusEnabled(false);

        m_jKeyN.setText("N");
        m_jKeyN.setFocusPainted(false);
        m_jKeyN.setFocusable(false);
        m_jKeyN.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyN.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyN.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyN.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyN.setRequestFocusEnabled(false);

        m_jKeyJ.setText("J");
        m_jKeyJ.setFocusPainted(false);
        m_jKeyJ.setFocusable(false);
        m_jKeyJ.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyJ.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyJ.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyJ.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyJ.setRequestFocusEnabled(false);

        m_jKeyM.setText("M");
        m_jKeyM.setFocusPainted(false);
        m_jKeyM.setFocusable(false);
        m_jKeyM.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyM.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyM.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyM.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyM.setRequestFocusEnabled(false);

        m_jKeyK.setText("K");
        m_jKeyK.setFocusPainted(false);
        m_jKeyK.setFocusable(false);
        m_jKeyK.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyK.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyK.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyK.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyK.setRequestFocusEnabled(false);

        m_jKeyL.setText("L");
        m_jKeyL.setFocusPainted(false);
        m_jKeyL.setFocusable(false);
        m_jKeyL.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyL.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyL.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyL.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyL.setRequestFocusEnabled(false);

        m_jKeyComma.setText(",");
        m_jKeyComma.setFocusPainted(false);
        m_jKeyComma.setFocusable(false);
        m_jKeyComma.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyComma.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyComma.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyComma.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyComma.setRequestFocusEnabled(false);

        m_jBackspace.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/btnback.png"))); // NOI18N
        m_jBackspace.setFocusPainted(false);
        m_jBackspace.setFocusable(false);
        m_jBackspace.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jBackspace.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jBackspace.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jBackspace.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jBackspace.setRequestFocusEnabled(false);

        m_jMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/color_line16.png"))); // NOI18N
        m_jMode.setFocusPainted(false);
        m_jMode.setFocusable(false);
        m_jMode.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jMode.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jMode.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jMode.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jMode.setRequestFocusEnabled(false);

        m_jCE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/btnce.png"))); // NOI18N
        m_jCE.setFocusPainted(false);
        m_jCE.setFocusable(false);
        m_jCE.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jCE.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jCE.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jCE.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jCE.setRequestFocusEnabled(false);

        m_jKeyLSquareBrack.setText("[");
        m_jKeyLSquareBrack.setFocusPainted(false);
        m_jKeyLSquareBrack.setFocusable(false);
        m_jKeyLSquareBrack.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyLSquareBrack.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyLSquareBrack.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyLSquareBrack.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyLSquareBrack.setRequestFocusEnabled(false);

        m_jKeyRSquareBrack.setText("]");
        m_jKeyRSquareBrack.setFocusPainted(false);
        m_jKeyRSquareBrack.setFocusable(false);
        m_jKeyRSquareBrack.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyRSquareBrack.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyRSquareBrack.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyRSquareBrack.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyRSquareBrack.setRequestFocusEnabled(false);

        m_jKeyColon.setText(":");
        m_jKeyColon.setFocusPainted(false);
        m_jKeyColon.setFocusable(false);
        m_jKeyColon.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyColon.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyColon.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyColon.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyColon.setRequestFocusEnabled(false);

        m_jKeyQuotation.setText("\"");
        m_jKeyQuotation.setFocusPainted(false);
        m_jKeyQuotation.setFocusable(false);
        m_jKeyQuotation.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyQuotation.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyQuotation.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyQuotation.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyQuotation.setRequestFocusEnabled(false);

        m_jKeySlash.setText("/");
        m_jKeySlash.setFocusPainted(false);
        m_jKeySlash.setFocusable(false);
        m_jKeySlash.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeySlash.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeySlash.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeySlash.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeySlash.setRequestFocusEnabled(false);

        m_jKeyAmpersand.setText("&");
        m_jKeyAmpersand.setFocusPainted(false);
        m_jKeyAmpersand.setFocusable(false);
        m_jKeyAmpersand.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyAmpersand.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyAmpersand.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyAmpersand.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyAmpersand.setRequestFocusEnabled(false);

        m_jKeyAt.setText("@");
        m_jKeyAt.setFocusPainted(false);
        m_jKeyAt.setFocusable(false);
        m_jKeyAt.setMargin(new java.awt.Insets(8, 8, 8, 8));
        m_jKeyAt.setMaximumSize(new java.awt.Dimension(64, 64));
        m_jKeyAt.setMinimumSize(new java.awt.Dimension(64, 64));
        m_jKeyAt.setPreferredSize(new java.awt.Dimension(24, 24));
        m_jKeyAt.setRequestFocusEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jKeyQ, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jKeyAt, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(m_jKeyW, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jKeyZ, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(m_jKeyE, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jKeyX, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(m_jMode, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(m_jKeyR, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jKeyC, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(12, 12, 12)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(m_jKeyT, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jKeyV, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(m_jKeyY, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jKeyB, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(m_jKeyU, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jKeyN, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(m_jKeyI, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jKeyM, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(m_jKeyComma, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jKeyO, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(m_jKeySpace, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jKeyP, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jKeyDot, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jKeyLSquareBrack, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jKeySlash, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jCE, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jKeyRSquareBrack, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jKeyAmpersand, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(m_jKey1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jKeyA, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jKey2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jKey3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jKeyS, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jKey4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jKeyD, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jKey5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jKeyF, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jKey6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jKeyG, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jKey7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jKeyH, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jKey8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jKeyJ, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jKey9, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jKeyK, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jKey0, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jKeyL, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jMinus, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jKeyColon, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jBackspace, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jKeyQuotation, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jKey1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jKey2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jKey3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jKey4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jKey5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jKey6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jKey7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jKey8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jKey9, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jKey0, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jMinus, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jBackspace, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jKeyQ, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jKeyW, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jKeyE, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jKeyR, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jKeyT, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jKeyY, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(m_jKeyO, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jKeyP, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jKeyI, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jKeyU, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jKeyLSquareBrack, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jKeyRSquareBrack, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(m_jKeyD, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jKeyS, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jKeyF, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jKeyA, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(m_jKeyG, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(m_jKeyJ, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jKeyH, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(m_jKeyL, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jKeyK, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jKeyColon, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jKeyQuotation, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(m_jKeyZ, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(m_jKeyAt, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(m_jKeyX, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(m_jKeyC, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(m_jKeyV, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jKeyB, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jKeyN, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(m_jKeyComma, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(m_jKeyM, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(m_jCE, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jKeySpace, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(m_jMode, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(m_jKeySlash, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jKeyAmpersand, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(m_jKeyDot, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void m_txtKeysKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_txtKeysKeyTyped

        // como contenedor de editores solo
        if (editorcurrent != null) {
            m_txtKeys.setText("0");

            // solo lo lanzamos si esta dentro del set de teclas
            char c = evt.getKeyChar();
            if (c == '\n') {
                fireActionPerformed();
            } else {
                if (keysavailable == null) {
                    // todo disponible
                    editorcurrent.typeChar(c);
                } else {
                    for (int i = 0; i < keysavailable.length; i++) {
                        if (c == keysavailable[i]) {
                            // todo disponible
                            editorcurrent.typeChar(c);
                        }
                    }
                }
            }
        }

    }//GEN-LAST:event_m_txtKeysKeyTyped

    private void m_txtKeysFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_m_txtKeysFocusLost

        setInactive(editorcurrent);

    }//GEN-LAST:event_m_txtKeysFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton m_jBackspace;
    javax.swing.JButton m_jCE;
    javax.swing.JButton m_jKey0;
    javax.swing.JButton m_jKey1;
    javax.swing.JButton m_jKey2;
    javax.swing.JButton m_jKey3;
    javax.swing.JButton m_jKey4;
    javax.swing.JButton m_jKey5;
    javax.swing.JButton m_jKey6;
    javax.swing.JButton m_jKey7;
    javax.swing.JButton m_jKey8;
    javax.swing.JButton m_jKey9;
    javax.swing.JButton m_jKeyA;
    javax.swing.JButton m_jKeyAmpersand;
    javax.swing.JButton m_jKeyAt;
    javax.swing.JButton m_jKeyB;
    javax.swing.JButton m_jKeyC;
    javax.swing.JButton m_jKeyColon;
    javax.swing.JButton m_jKeyComma;
    javax.swing.JButton m_jKeyD;
    javax.swing.JButton m_jKeyDot;
    javax.swing.JButton m_jKeyE;
    javax.swing.JButton m_jKeyF;
    javax.swing.JButton m_jKeyG;
    javax.swing.JButton m_jKeyH;
    javax.swing.JButton m_jKeyI;
    javax.swing.JButton m_jKeyJ;
    javax.swing.JButton m_jKeyK;
    javax.swing.JButton m_jKeyL;
    javax.swing.JButton m_jKeyLSquareBrack;
    javax.swing.JButton m_jKeyM;
    javax.swing.JButton m_jKeyN;
    javax.swing.JButton m_jKeyO;
    javax.swing.JButton m_jKeyP;
    javax.swing.JButton m_jKeyQ;
    javax.swing.JButton m_jKeyQuotation;
    javax.swing.JButton m_jKeyR;
    javax.swing.JButton m_jKeyRSquareBrack;
    javax.swing.JButton m_jKeyS;
    javax.swing.JButton m_jKeySlash;
    javax.swing.JButton m_jKeySpace;
    javax.swing.JButton m_jKeyT;
    javax.swing.JButton m_jKeyU;
    javax.swing.JButton m_jKeyV;
    javax.swing.JButton m_jKeyW;
    javax.swing.JButton m_jKeyX;
    javax.swing.JButton m_jKeyY;
    javax.swing.JButton m_jKeyZ;
    javax.swing.JButton m_jMinus;
    javax.swing.JButton m_jMode;
    javax.swing.JTextField m_txtKeys;
    // End of variables declaration//GEN-END:variables

}
