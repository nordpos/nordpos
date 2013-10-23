/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.pludevice;

/**
 *
 * @author administrator
 */
public class DevicePLUsException extends java.lang.Exception { 
    public DevicePLUsException() {
    }
    
    public DevicePLUsException(String msg) {
        super(msg);
    }

    public DevicePLUsException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    public DevicePLUsException(Throwable cause) {
        super(cause);
    }
    
}
