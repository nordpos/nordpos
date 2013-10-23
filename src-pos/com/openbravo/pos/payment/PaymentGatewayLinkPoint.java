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

import com.openbravo.data.loader.LocalRes;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppProperties;
import com.openbravo.pos.util.AltEncrypter;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Mikel Irurita
 */
public class PaymentGatewayLinkPoint implements PaymentGateway {
    
    private static final String SALE = "SALE";
    private static final String REFUND = "CREDIT";
    
    private static String HOST;
    private final static int PORT = 1129;
    private String sClientCertPath; //Path of the .12 file

    private String sPasswordCert; //Password used creating .12 file

    private String sConfigfile; //StoreName

    private boolean m_bTestMode;
    private static String APPROVED = "APPROVED";

    public PaymentGatewayLinkPoint(AppProperties props) {

        
        this.m_bTestMode = Boolean.valueOf(props.getProperty("payment.testmode")).booleanValue();
        this.sConfigfile = props.getProperty("payment.commerceid");
        this.sClientCertPath = props.getProperty("payment.certificatePath");
        AltEncrypter cypher = new AltEncrypter("cypherkey");
        this.sPasswordCert = cypher.decrypt(props.getProperty("payment.certificatePassword").substring(6));
        
        HOST = (m_bTestMode)
                ? "staging.linkpt.net"
                : "secure.linkpt.net";
    }
    
    public PaymentGatewayLinkPoint() {
        
    }

    @Override
    public void execute(PaymentInfoMagcard payinfo) {
        String sReturned="";
        URL url;
        
        System.setProperty("javax.net.ssl.keyStore", sClientCertPath);
        System.setProperty("javax.net.ssl.keyStorePassword", sPasswordCert);
        System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");        
        
        
        
        try {
            url = new URL("https://"+HOST+":"+PORT);
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            connection.setHostnameVerifier(new NullHostNameVerifier());
            
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            
            StringBuilder xml = createOrder(payinfo);
            String a = xml.toString();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(xml.toString().getBytes());
            out.flush();
            
            // process and read the gateway response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            sReturned = in.readLine();
            
        } catch (IOException exIoe) {
            payinfo.paymentError(LocalRes.getIntString("exception.iofile"), exIoe.getMessage());
        }

        LinkPointParser lpp = new LinkPointParser(sReturned);
        Map props = lpp.splitXML();
        
        if (lpp.getResult().equals(LocalRes.getIntString("button.ok"))) {
            //printResponse(props);

            if (APPROVED.equals(props.get("r_approved"))) {
                //Transaction approved
                payinfo.paymentOK((String) props.get("r_code"), (String) props.get("r_ordernum"), sReturned);
            } else {
                //Transaction declined
                payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), (String)props.get("r_error"));
            }
        }
        else {
            payinfo.paymentError(lpp.getResult(), "");
        }
    }
    
    private static class NullHostNameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
    
    private StringBuilder createOrder(PaymentInfoMagcard payinfo) {
        StringBuilder moreInfo = new StringBuilder();
        StringBuilder xml = new StringBuilder();
        
        String sTransactionType = (payinfo.getTotal()>0.0)
            ? SALE
            : REFUND;
        
        NumberFormat formatter = new DecimalFormat("0000.00");
        String amount = formatter.format(Math.abs(payinfo.getTotal()));

        String tmp = payinfo.getExpirationDate();
        
        String refundLine = (sTransactionType.equals("CREDIT"))
               ? "<oid>"+ payinfo.getTransactionID() +"</oid>"
               :  "";
        
        try {
            
        if (payinfo.getTrack1(true) == null){
            moreInfo.append("<creditcard>");
            moreInfo.append("<cardnumber>"+ payinfo.getCardNumber() +"</cardnumber> ");
            moreInfo.append("<cardexpmonth>" + tmp.charAt(0) + "" + tmp.charAt(1) + "</cardexpmonth>");
            moreInfo.append("<cardexpyear>" + tmp.charAt(2) + "" + tmp.charAt(3) + "</cardexpyear>");
            moreInfo.append("</creditcard>");
            
        } else {
            moreInfo.append("<creditcard>");
            moreInfo.append("<track>");
            //moreInfo.append("%B4111111111111111^PADILLA VISDOMINE/LUIS ^1206120000000000000000999000000?");
            moreInfo.append(payinfo.getTrack1(true));
            //moreInfo.append(payinfo.getTrack2(true));
            moreInfo.append("</track>");
            moreInfo.append("</creditcard>");
        }
        
        //Construct the order
        xml.append("<order>");
        xml.append("<merchantinfo><configfile>"+ sConfigfile +"</configfile></merchantinfo>");
        xml.append("<orderoptions><ordertype>"+ sTransactionType +"</ordertype><result>LIVE</result></orderoptions>");
        xml.append("<payment><chargetotal>"+ URLEncoder.encode(amount.replace(',', '.'), "UTF-8") +"</chargetotal></payment>");
        xml.append(moreInfo);
        xml.append("<transactiondetails>");
        xml.append(refundLine);
        xml.append("<transactionorigin>RETAIL</transactionorigin><terminaltype>POS</terminaltype></transactiondetails>");
        xml.append("</order>");
        
        } catch (UnsupportedEncodingException ex) {
            payinfo.paymentError(AppLocal.getIntString("message.paymentexceptionservice"), ex.getMessage());
        }
        
        return xml;
        
    }
    
    private class LinkPointParser extends DefaultHandler {
    
    private SAXParser m_sp = null;
    private Map props = new HashMap();
    private String text;
    private InputStream is;
    private String result;
    
    public LinkPointParser(String in) {
        String input = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><returned>" + in + "</returned>";
        is = new ByteArrayInputStream(input.getBytes());
    }
    
    public Map splitXML(){
        try {
            if (m_sp == null) {
                SAXParserFactory spf = SAXParserFactory.newInstance();
                m_sp = spf.newSAXParser();
            }
            m_sp.parse(is, this);
        } catch (ParserConfigurationException ePC) {
            result = LocalRes.getIntString("exception.parserconfig");
        } catch (SAXException eSAX) {
            result = LocalRes.getIntString("exception.xmlfile");
        } catch (IOException eIO) {
            result = LocalRes.getIntString("exception.iofile");
        }
        result = LocalRes.getIntString("button.ok");
        return props;
    }
      
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            if (qName.equals("r_csp")) {
                props.put("r_csp", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("r_time")){
                props.put("r_time", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("r_ref")) {
                props.put("r_ref", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("r_error")) {
                props.put("r_error", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("r_ordernum")) {
                props.put("r_ordernum", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("r_message")) {
                props.put("r_message", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("r_code")) {
                props.put("r_code", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("r_tdate")) {
                props.put("r_tdate", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("r_score")) {
                props.put("r_score", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("r_authresponse")) {
                props.put("r_authresponse", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("r_approved")) {
                props.put("r_approved", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("r_avs")) {
                props.put("r_avs", URLDecoder.decode(text, "UTF-8"));
                text="";
            }
        }
        catch(UnsupportedEncodingException eUE){
            result = eUE.getMessage();
        }
    }
    
    @Override
    public void startDocument() throws SAXException {
        text = new String();
    }

    @Override
    public void endDocument() throws SAXException {
    }
    
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (text!=null) {
            text = new String(ch, start, length);
        }
    }
    
    public String getResult(){
        return this.result;
    }
    
 }
    
}
