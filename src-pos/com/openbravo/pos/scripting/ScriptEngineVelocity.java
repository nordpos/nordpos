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

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;

/**
 *
 * @author adrianromero
 * Created on 5 de marzo de 2007, 19:57
 *
 */
class ScriptEngineVelocity implements ScriptEngine {
    
    private static VelocityEngine m_ve = null;

    private VelocityContext c = null;
    
    /** Creates a new instance of ScriptEngineVelocity */
    public ScriptEngineVelocity() throws ScriptException {
        
        if (m_ve == null) {
            // Inicializo Velocity
            m_ve = new VelocityEngine();
            // ve.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, this);

    //        m_ve.setProperty(VelocityEngine.RESOURCE_LOADER, "class");
    //        // m_ve.setProperty("class.resource.loader.description", "Velocity Classpath Resource Loader");
    //        // m_ve.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    //        m_ve.setProperty("class.resource.loader.class", "com.openbravo.pos.forms.SystemResourceLoader");
    //        m_ve.setProperty("class.resource.loader.description", "Velocity Resource Loader");
    //        m_ve.setProperty("class.resource.loader.appresources", this);

            m_ve.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.NullLogSystem");
            m_ve.setProperty(VelocityEngine.ENCODING_DEFAULT, "UTF-8");
            m_ve.setProperty(VelocityEngine.INPUT_ENCODING, "UTF-8");
            try {
                m_ve.init();
            } catch (Exception e) {
                throw new ScriptException("Cannot initialize Velocity Engine", e);
            } 
        }
         c = new VelocityContext();
    }
    
    public void put(String key, Object value) {
        c.put(key, value);
    }
    public Object get(String key) {
        return c.get(key);
    }
    
    public Object eval(String src) throws ScriptException {
        
        if (m_ve == null) {
            throw new ScriptException("Velocity engine not initialized.");
        } else {
            Writer w = new StringWriter();
            try {
                if (m_ve.evaluate(c, w, "log", new StringReader(src))) {
                    return w.toString();
                } else {
                    throw new ScriptException("Velocity engine unexpected error.");
                }
            } catch (ParseErrorException e) {
                throw new ScriptException(e.getMessage(), e);
            } catch (MethodInvocationException e) {
                throw new ScriptException(e.getMessage(), e);
            } catch (Exception e) {
                throw new ScriptException(e.getMessage(), e);
            }
        }
    }
}
