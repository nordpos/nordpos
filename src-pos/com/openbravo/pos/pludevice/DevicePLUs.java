/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.pludevice;

/**
 *
 * @author administrator
 */
public interface DevicePLUs {
    public void connectDevice() throws DevicePLUsException;
    public void disconnectDevice();

    public void startDownloadProduct() throws DevicePLUsException;
    public ProductDownloaded recieveProduct() throws DevicePLUsException;
    
    public void startUploadProduct() throws DevicePLUsException;
    public void sendProduct(String sName, String sCode, Double dPriceBuy, Double dPriceSell, int iCurrentPLU, int iTotalPLUs, String sBarcode) throws DevicePLUsException;
    public void stopUploadProduct() throws DevicePLUsException;    
}
