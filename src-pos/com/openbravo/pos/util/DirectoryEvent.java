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

package com.openbravo.pos.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.JTextComponent;
import com.openbravo.pos.forms.AppLocal;

public class DirectoryEvent implements ActionListener {
    
    private JTextComponent m_jTxtField;
    private JFileChooser m_fc;
    
    /** Creates a new instance of DirectoryChooser */
    public DirectoryEvent(JTextComponent TxtField) {
        m_jTxtField = TxtField;
        m_fc = new JFileChooser();
        
        m_fc.resetChoosableFileFilters();
        m_fc.addChoosableFileFilter(new FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    String filename = f.getName();
                    return filename.endsWith(".jar")
                        || filename.endsWith(".JAR")
                        || filename.endsWith(".zip")
                        || filename.endsWith(".ZIP");
                }
            }
            public String getDescription() {
                return AppLocal.getIntString("filter.dbdriverlib");
            }
        });
        m_fc.setFileSelectionMode(JFileChooser.FILES_ONLY );
    }
       
    public void actionPerformed(ActionEvent actionEvent) {
        
        m_fc.setCurrentDirectory(new File(m_jTxtField.getText()));      
        if (m_fc.showOpenDialog(m_jTxtField) == JFileChooser.APPROVE_OPTION) {
            m_jTxtField.setText(m_fc.getSelectedFile().getAbsolutePath());
        }
    }       
    
}
