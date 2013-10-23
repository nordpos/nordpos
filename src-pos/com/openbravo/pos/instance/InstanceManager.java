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

package com.openbravo.pos.instance;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author adrianromero
 */
public class InstanceManager {
    
    private Registry m_registry;
    private AppMessage m_message;
    
    /** Creates a new instance of InstanceManager */
    public InstanceManager(AppMessage message) throws RemoteException, AlreadyBoundException {

        m_registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);

        m_message = message;

        AppMessage stub = (AppMessage) UnicastRemoteObject.exportObject(m_message, 0);
        m_registry.bind("AppMessage", stub); 

        // jLabel1.setText("Server ready");
    }    
}
