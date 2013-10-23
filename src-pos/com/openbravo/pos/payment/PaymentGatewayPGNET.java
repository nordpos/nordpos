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
package com.openbravo.pos.payment;

import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppProperties;
import com.openbravo.pos.util.AltEncrypter;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author Mikel Irurita
 */
public class PaymentGatewayPGNET implements PaymentGateway {
    
    private static String ENDPOINTADDRESS;
    private static final String SALE = "10";
    private static final String REFUND = "13";
    private static final String APPROVED = "A";
    
    private String m_sCommerceID;
    private String m_sCommercePassword;
    private boolean m_bTestMode;
    
    public PaymentGatewayPGNET(AppProperties props) {
        // Grab some configuration variables
        m_sCommerceID = props.getProperty("payment.commerceid");
        
        AltEncrypter cypher = new AltEncrypter("cypherkey" + props.getProperty("payment.commerceid"));
        this.m_sCommercePassword = cypher.decrypt(props.getProperty("payment.commercepassword").substring(6));
        
        m_bTestMode = Boolean.valueOf(props.getProperty("payment.testmode")).booleanValue();
        
        ENDPOINTADDRESS = (m_bTestMode) 
                ? "https://www.paymentsgateway.net/cgi-bin/posttest.pl"
                : "https://www.paymentsgateway.net/cgi-bin/postauth.pl";
    }
    
    public PaymentGatewayPGNET(){
        
    }
    
    public void execute(PaymentInfoMagcard payinfo) {

        StringBuffer sb = new StringBuffer();
        try {
            
            sb.append("pg_merchant_id=");
            sb.append(m_sCommerceID);

            sb.append("&pg_password=");
            sb.append(m_sCommercePassword);
            
            sb.append("&pg_total_amount=");
            NumberFormat formatter = new DecimalFormat("0000.00");
            String amount = formatter.format(Math.abs(payinfo.getTotal()));
            sb.append(URLEncoder.encode(amount.replace(',', '.'), "UTF-8"));
            
            if (payinfo.getTrack1(true) != null){
                sb.append("&pg_cc_swipe_data=");
                sb.append(URLEncoder.encode(payinfo.getTrack1(true), "UTF-8"));
                
            } else {
                sb.append("&ecom_payment_card_type=");
                sb.append(getCardType(payinfo.getCardNumber()));

                sb.append("&ecom_payment_card_number=");
                sb.append(URLEncoder.encode(payinfo.getCardNumber(), "UTF-8"));

                sb.append("&ecom_payment_card_expdate_month=");
                String tmp = payinfo.getExpirationDate();
                sb.append(tmp.substring(0, 2));

                sb.append("&ecom_payment_card_expdate_year=");
                sb.append(tmp.substring(2, tmp.length()));

                String[] cc_name = payinfo.getHolderName().split(" ");
                sb.append("&ecom_billto_postal_name_first=");
                if (cc_name.length > 0) {
                sb.append(URLEncoder.encode(cc_name[0], "UTF-8"));
                }
                sb.append("&ecom_billto_postal_name_last=");
                if (cc_name.length > 1) {
                sb.append(URLEncoder.encode(cc_name[1], "UTF-8"));
                }

                sb.append("&ecom_payment_card_name=");
                sb.append(payinfo.getHolderName());
            }
            //sb.append("&ecom_payment_card_verification=");
            
            //PAYMENT
            if (payinfo.getTotal() >= 0.0) {
                sb.append("&pg_transaction_type=");
                sb.append(SALE);
                //sb.append("&x_card_code=340");
            }
            //REFUND
            else {
                sb.append("&pg_transaction_type=");
                sb.append(REFUND);
            }
            
            sb.append("&endofdata");

            // open secure connection
            URL url = new URL(ENDPOINTADDRESS);

            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // not necessarily required but fixes a bug with some servers
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

            // POST the data in the string buffer
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(sb.toString().getBytes());
            out.flush();
            out.close();

            // process and read the gateway response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            
            String returned="", aux;
            
            while ((aux = in.readLine()) != null) {
                    returned += "&" + aux;
            }

            payinfo.setReturnMessage(returned);
            in.close();	                     // fin

            Map props = new HashMap();
            StringTokenizer tk = new java.util.StringTokenizer(returned, "&");
            while(tk.hasMoreTokens()) {
                String sToken = tk.nextToken();
                int i = sToken.indexOf('=');
                if (i >= 0) {
                    props.put(URLDecoder.decode(sToken.substring(0, i), "UTF-8"), URLDecoder.decode(sToken.substring(i + 1), "UTF-8"));
                } else {
                    props.put(URLDecoder.decode(sToken, "UTF-8"), null);
                }                   
            }
            
            if (APPROVED.equals(props.get("pg_response_type"))) {
                payinfo.paymentOK((String)props.get("pg_authorization_code"), (String)props.get("pg_trace_number"), returned);
            } else {
                
                String sCode = (String)props.get("pg_response_description");
                sCode = sCode.replace("F01", "\nMANDITORY FIELD MISSING");
                sCode = sCode.replace("F03", "\nINVALID FIELD NAME");
                sCode = sCode.replace("F04", "\nINVALID FIELD VALUE");
                sCode = sCode.replace("F05", "\nDUPLICATE FIELD");
                sCode = sCode.replace("F07", "\nCONFLICTING FIELD");
                                 
                payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), sCode);
            }
            
        } catch (UnsupportedEncodingException eUE) {
            payinfo.paymentError(AppLocal.getIntString("message.paymentexceptionservice"), eUE.getMessage());
        } catch (MalformedURLException eMURL) {
            payinfo.paymentError(AppLocal.getIntString("message.paymentexceptionservice"), eMURL.getMessage());
        } catch(IOException e){
            payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), e.getMessage());
        }
        
    }
    
    private String getCardType(String sCardNumber){
        String c = "UNKNOWN";
        if (sCardNumber.startsWith("4")) {
            c = "VISA";
        } else if (sCardNumber.startsWith("6")) {
            c = "DISC";
        } else if (sCardNumber.startsWith("5")) {
            c = "MAST";
        } else if (sCardNumber.startsWith("34") || sCardNumber.startsWith("37")) {
            c = "AMER";
        } else if (sCardNumber.startsWith("3528") || sCardNumber.startsWith("3589")) {
            c = "JCB";
        } else if (sCardNumber.startsWith("3")) {
            c = "DINE";
        }  
        return c;
    }

}
