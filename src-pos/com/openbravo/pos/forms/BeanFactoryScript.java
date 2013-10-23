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

package com.openbravo.pos.forms;

import com.openbravo.pos.scripting.ScriptEngine;
import com.openbravo.pos.scripting.ScriptException;
import com.openbravo.pos.scripting.ScriptFactory;
import com.openbravo.pos.util.StringUtils;
import java.io.IOException;

/**
 *
 * @author adrianromero
 */
public class BeanFactoryScript implements BeanFactoryApp {
    
    private BeanFactory bean = null;
    private String script;
    
    public BeanFactoryScript(String script) {
        this.script = script;
    }
    
    public void init(AppView app) throws BeanFactoryException {
        
        // Resource
        try {
            ScriptEngine eng = ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL);
            eng.put("app", app);
            
            bean = (BeanFactory) eng.eval(StringUtils.readResource(script));
            if (bean == null) {
                // old definition
                bean = (BeanFactory) eng.get("bean");
            }
            
            // todo // llamar al init del bean
            if (bean instanceof BeanFactoryApp) {
                ((BeanFactoryApp) bean).init(app);
            }
        } catch (ScriptException e) {
            throw new BeanFactoryException(e);
        } catch (IOException e) {
            throw new BeanFactoryException(e);
        }     
    }

    public Object getBean() {
        return bean.getBean();
    }
}
