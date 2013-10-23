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

package com.openbravo.pos.scripting;

/**
 *
 * @author adrianromero
 * Created on 5 de marzo de 2007, 19:56
 *
 */
public class ScriptFactory {
    
    public static final String VELOCITY = "velocity";
    public static final String BEANSHELL = "beanshell";
    public static final String RHINO = "rhino";
    
    /** Creates a new instance of ScriptFactory */
    private ScriptFactory() {
    }
    
    public static ScriptEngine getScriptEngine(String name) throws ScriptException {
        if (VELOCITY.equals(name)) {
            return new ScriptEngineVelocity();
        } else if (BEANSHELL.equals(name)) {
            return new ScriptEngineBeanshell();
//        } else if (RHINO.equals(name)) {
//            return new ScriptEngineRhino();
//        } else if (name.startsWith("generic:")) {
//            return new ScriptEngineGeneric(name.substring(8));            
        } else {
            throw new ScriptException("Script engine not found: " + name);
        }
    }    
}
