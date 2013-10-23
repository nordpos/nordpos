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
public class PaymentGatewayPlanetauthorize implements PaymentGateway {
    
    private static final String ENDPOINTADDRESS = "https://secure.planetauthorizegateway.com/api/transact.php";
    private static final String OPERATIONVALIDATE = "sale";
    private static final String OPERATIONREFUND = "refund";

    private String m_sCommerceID;
    private String m_sCommercePassword;
    private boolean m_bTestMode;
    
    public PaymentGatewayPlanetauthorize (AppProperties props) {
        m_sCommerceID = props.getProperty("payment.commerceid");

        AltEncrypter cypher = new AltEncrypter("cypherkey" + props.getProperty("payment.commerceid"));
        this.m_sCommercePassword = cypher.decrypt(props.getProperty("payment.commercepassword").substring(6));

        m_bTestMode = Boolean.valueOf(props.getProperty("payment.testmode")).booleanValue();
    }
    
    public PaymentGatewayPlanetauthorize() {
        
    }
    
    @Override
    public void execute(PaymentInfoMagcard payinfo) {
        StringBuffer sb = new StringBuffer();
        
        try {
            sb.append("username="); //test=demo
            sb.append(m_sCommerceID);

            sb.append("&password="); //test=password
            sb.append(m_sCommercePassword);

            sb.append("&amount=");
            NumberFormat formatter = new DecimalFormat("0000.00");
            String amount = formatter.format(Math.abs(payinfo.getTotal()));
            sb.append(URLEncoder.encode(amount.replace(',', '.'), "UTF-8"));

            //sb.append("&cvv="); //card security code
            
            if (payinfo.getTrack1(true) == null){
                sb.append("&ccnumber="); //test=4111111111111111 (visa)
                sb.append(URLEncoder.encode(payinfo.getCardNumber(), "UTF-8"));

                sb.append("&ccexp="); //expiration date  (MM/YY)
                sb.append(payinfo.getExpirationDate());

                String[] cc_name = payinfo.getHolderName().split(" ");
                sb.append("&firstname=");
                if (cc_name.length > 0) {
                    sb.append(URLEncoder.encode(cc_name[0], "UTF-8"));
                }
                sb.append("&lastname=");
                if (cc_name.length > 1) {
                    sb.append(URLEncoder.encode(cc_name[1], "UTF-8"));
                }
                
            } else {             
                //String track_1 = "%B4111111111111111^PADILLA VISDOMINE/LUIS ^0509120000000000000000999000000?";
                //String track_2 = ";4111111111111111=05091200333300000000?";
                //String track_3 = ";4111111111111111=7247241000000000000303009046040400005090=111111234564568798543654==1=0000000000000000?";
                sb.append("&track_1="+ URLEncoder.encode(payinfo.getTrack1(true),"UTF-8"));
                sb.append("&track_2=" + URLEncoder.encode(payinfo.getTrack2(true), "UTF-8"));
                
            }
            
            if (payinfo.getTotal() > 0.0) { //SALE
                sb.append("&type=");
                sb.append(OPERATIONVALIDATE);
                
            } else { // REFUND
                sb.append("&type=");
                sb.append(OPERATIONREFUND);
                sb.append("&transactionid="); //transaction ID
                sb.append(payinfo.getTransactionID());
                //payinfo.paymentError(AppLocal.getIntString("message.paymentrefundsnotsupported"));
            }

        // open secure connection
        URL url = new URL(ENDPOINTADDRESS);
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setAllowUserInteraction(false);

        // not necessarily required but fixes a bug with some servers
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

        // POST the data in the string buffer
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.write(sb.toString().getBytes());
        out.flush();
        out.close();

        // process and read the gateway response
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String returned = in.readLine();
        
        //RESPONSE
        //response=1&responsetext=SUCCESS&authcode=123456&transactionid=849066017&avsresponse=&cvvresponse=M&orderid=&type=sale&response_code=100
        payinfo.setReturnMessage(returned);
        in.close(); // fin
        
        if (returned == null) {
            payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Response empty.");
        } else {
            Map props = new HashMap();
            StringTokenizer tk = new StringTokenizer(returned, "?&");
            while(tk.hasMoreTokens()) {
                String sToken = tk.nextToken();
                int i = sToken.indexOf('=');
                if (i >= 0) {
                    props.put(URLDecoder.decode(sToken.substring(0, i), "UTF-8"), URLDecoder.decode(sToken.substring(i + 1), "UTF-8"));
                } else {
                    props.put(URLDecoder.decode(sToken, "UTF-8"), null);
                }                   
            }

            if ("100".equals(props.get("response_code"))){
                //Transaction approved
                payinfo.paymentOK((String)props.get("authcode"), (String)props.get("transactionid"), returned);
            } else {
                //Transaction declined
                payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), (String)props.get("responsetext"));
            }
        }
    } catch (UnsupportedEncodingException eUE) {
        //no pasa nunca
        payinfo.paymentError(AppLocal.getIntString("message.paymentexceptionservice"), eUE.getMessage());
    } catch (MalformedURLException eMURL) {
        // no pasa nunca    
        payinfo.paymentError(AppLocal.getIntString("message.paymentexceptionservice"), eMURL.getMessage());
    } catch(IOException e){
        payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), e.getMessage());
    }
        
    }

}
