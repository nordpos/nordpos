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

package com.openbravo.pos.thirdparties;

import com.openbravo.data.loader.Datas;
import com.openbravo.data.loader.Session;
import com.openbravo.data.loader.TableDefinition;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.BeanFactoryDataSingle;

public class DataLogicThirdParties extends BeanFactoryDataSingle {
    
    private TableDefinition m_tthirdparties;
 
    /** Creates a new instance of DataLogicAdmin */
    public DataLogicThirdParties() {
    }
    
    public void init(Session s){

        m_tthirdparties = new TableDefinition(s,
            "THIRDPARTIES"
            , new String[] {"ID", "CIF", "NAME", "ADDRESS", "CONTACTCOMM", "CONTACTFACT", "PAYRULE", "FAXNUMBER", "PHONENUMBER", "MOBILENUMBER", "EMAIL", "WEBPAGE", "NOTES"}
            , new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING}
            , new Formats[] {Formats.STRING, Formats.STRING, Formats.STRING, Formats.STRING, Formats.STRING, Formats.STRING, Formats.STRING, Formats.STRING, Formats.STRING, Formats.STRING, Formats.STRING, Formats.STRING, Formats.STRING}
            , new int[] {0}
        );   
        
    }
       
    public final TableDefinition getTableThirdParties() {
        return m_tthirdparties;
    }    
}

