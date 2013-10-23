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

package com.openbravo.pos.ticket;

import java.awt.image.BufferedImage;

/**
 *
 * @author adrianromero
 * Created on 21 de marzo de 2007, 21:28
 *
 */
public class ProductInfoEdit {
    
    protected String m_ID;
    protected String m_sRef;
    protected String m_sCode;
    protected String m_sName;
    protected boolean m_bCom;
    protected boolean m_bScale;
    protected String m_sCategoryID;    
    protected String m_sTaxID;
    protected String attributeuseid;
    protected double m_dPriceBuy;
    protected double m_dPriceSell; 
    protected BufferedImage m_Image;
    
    protected Double m_dStockCost;
    protected Double m_dStockVolume;
    protected Integer m_iCatalogOrder;
    
    /** Creates a new instance of ProductInfoEdit */
    public ProductInfoEdit() {
        m_ID = null;
        m_sRef = "0000";
        m_sCode = "0000";
        m_sName = null;
        m_bCom = false;
        m_bScale = false;
        m_sCategoryID = null;
        m_sTaxID = null;
        attributeuseid = null;
        m_dPriceBuy = 0.0;
        m_dPriceSell = 0.0;
        m_Image = null;
        m_dStockCost = null;
        m_dStockVolume = null;
        m_iCatalogOrder = null;            
    }
   
    public final String getID() {
        return m_ID;
    }
    
    public final void setID(String id) {
        m_ID = id;
    }
    
    public final String getReference(){
        return m_sRef;
    }
    public final void setReference(String sRef){
        m_sRef = sRef;
    }    
    public final String getCode(){
        return m_sCode;
    }
    public final void setCode(String sCode){
        m_sCode = sCode;
    }
    public final String getName() {            
        return m_sName;
    }
    public final void setName(String sName){            
        m_sName = sName;
    }
    public final boolean isCom() {            
        return m_bCom;
    }
    public final void setCom(boolean bValue){            
        m_bCom = bValue;
    }
    public final boolean isScale() {            
        return m_bScale;
    }
    public final void setScale(boolean bValue){            
        m_bScale = bValue;
    }
    public final String getCategoryID() {
        return m_sCategoryID;
    }
    public final void setCategoryID(String sCategoryID) {
        m_sCategoryID = sCategoryID;
    }
    public final String getTaxID() {
        return m_sTaxID;
    }
    public final void setTaxID(String sTaxID) {
        m_sTaxID = sTaxID;
    }
    public final String getAttributeUseID() {
        return attributeuseid;
    }
    public final void setAttributeUseID(String value) {
        attributeuseid = value;
    }
    public final double getPriceBuy(){
        return m_dPriceBuy;
    }    
    public final void setPriceBuy(double dPrice) {
        m_dPriceBuy = dPrice;
    }        
    public final double getPriceSell(){        
        return m_dPriceSell;
    }
    public final void setPriceSell(double dPrice) {        
        m_dPriceSell = dPrice;
    }      

    public BufferedImage getImage() {
        return m_Image;
    }
    public void setImage(BufferedImage img) {
        m_Image = img;
    }
    
    @Override
    public final String toString() {
        return m_sRef + " - " + m_sName;
    }    
}
