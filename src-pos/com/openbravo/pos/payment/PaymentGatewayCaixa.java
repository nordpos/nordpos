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
import com.openbravo.pos.util.StringUtils;
import com.openbravo.pos.util.AltEncrypter;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Mikel Irurita
 */
public class PaymentGatewayCaixa implements PaymentGateway {
    
    private static String ENDPOINTADDRESS;
    private static final String SALE = "A";
    private static final String REFUND = "3";
    private static String SALEAPPROVED = "0000";
    private static String REFUNDAPPROVED = "0900";
    
    private String m_sCurrency;
    private String sMerchantCode;
    private String sTerminal;
    private String sCommerceSign;
    private boolean bSha;
    private boolean m_bTestMode;
    
    
    public PaymentGatewayCaixa (AppProperties props) {
        AltEncrypter cypher = new AltEncrypter("cypherkey");
        this.sCommerceSign = cypher.decrypt(props.getProperty("payment.commercesign").substring(6));
        
        this.m_bTestMode = Boolean.valueOf(props.getProperty("payment.testmode")).booleanValue();
        
        //EUR, USD, GPB
        this.m_sCurrency = (Locale.getDefault().getCountry().isEmpty())
            ? Currency.getInstance("EUR").getCurrencyCode()
            : Currency.getInstance(Locale.getDefault()).getCurrencyCode();
        
        this.sTerminal = props.getProperty("payment.terminal");
        this.sMerchantCode = props.getProperty("payment.commerceid");
        this.bSha = Boolean.valueOf(props.getProperty("payment.SHA")).booleanValue();
        
        ENDPOINTADDRESS = (m_bTestMode)
                ? "https://sis-t.sermepa.es:25443/sis/operaciones"
                : "https://sis.sermepa.es/sis/realizarPago";
        
    }
    
    public PaymentGatewayCaixa(){
        
    }
    
    
    private String createOrderId() {
        Random r = new Random();
        NumberFormat nf = new DecimalFormat("0000000000");
        return nf.format( Math.abs(r.nextInt()) + (Math.abs(System.currentTimeMillis()) % 1000000) );
    }
    
    public void execute(PaymentInfoMagcard payinfo) {
        //merchantCode = "999008881";
        //terminal = "1";
        //sign = "qwertyasdf0123456789";
        
        StringBuffer sb = new StringBuffer();
        String currency = "978"; //default euros
        String xml="";
        if (m_sCurrency.equals("USD")) {
            currency = "840"; //dollars
        } else if (m_sCurrency.equals("GPD")) {
            currency = "826"; 
        }
        
        NumberFormat nf = new DecimalFormat("00");
        String amount = nf.format( Math.abs(payinfo.getTotal())*100 );
        String orderid = createOrderId();

        try {

        if (payinfo.getTotal() > 0.0) {
            String firma = amount + orderid + sMerchantCode + currency + payinfo.getCardNumber() + SALE + sCommerceSign;
 
            xml = "<DATOSENTRADA>" +
            "<DS_Version>0.1</DS_Version>" +
            "<DS_MERCHANT_AMOUNT>"+ amount +"</DS_MERCHANT_AMOUNT>" +
            "<DS_MERCHANT_CURRENCY>"+currency+"</DS_MERCHANT_CURRENCY>" +
            "<DS_MERCHANT_ORDER>"+ orderid +"</DS_MERCHANT_ORDER>" +
            "<DS_MERCHANT_MERCHANTCODE>"+ sMerchantCode +"</DS_MERCHANT_MERCHANTCODE>" +
            "<DS_MERCHANT_MERCHANTURL></DS_MERCHANT_MERCHANTURL>" + 
            //"<DS_MERCHANT_MERCHANTNAME>sample merchant</DS_MERCHANT_MERCHANTNAME>" + //Optional
            "<DS_MERCHANT_MERCHANTSIGNATURE>"+getSHA1(firma)+"</DS_MERCHANT_MERCHANTSIGNATURE>" +
            "<DS_MERCHANT_TERMINAL>"+ sTerminal +"</DS_MERCHANT_TERMINAL>" +
            "<DS_MERCHANT_TRANSACTIONTYPE>"+SALE+"</DS_MERCHANT_TRANSACTIONTYPE>" +
            //"<DS_MERCHANT_MERCHANTDATA>sample data</DS_MERCHANT_MERCHANTDATA>" + //Optional
            "<DS_MERCHANT_PAN>"+ payinfo.getCardNumber() +"</DS_MERCHANT_PAN>" +
            "<DS_MERCHANT_EXPIRYDATE>"+ payinfo.getExpirationDate() +"</DS_MERCHANT_EXPIRYDATE>" +
            //"<DS_MERCHANT_CVV2>sample cvv</DS_MERCHANT_CVV2>" + //Optional
            "</DATOSENTRADA>";
        } else {
            String firma = amount + payinfo.getTransactionID() + sMerchantCode + currency + REFUND + sCommerceSign;

            xml = "<DATOSENTRADA>" +
            "<DS_Version>0.1</DS_Version>" +
            "<DS_MERCHANT_AMOUNT>"+ amount +"</DS_MERCHANT_AMOUNT>" +
            "<DS_MERCHANT_CURRENCY>"+currency+"</DS_MERCHANT_CURRENCY>" +
            "<DS_MERCHANT_ORDER>"+ payinfo.getTransactionID() +"</DS_MERCHANT_ORDER>" +
            "<DS_MERCHANT_MERCHANTCODE>"+ sMerchantCode +"</DS_MERCHANT_MERCHANTCODE>" +
            "<DS_MERCHANT_MERCHANTURL></DS_MERCHANT_MERCHANTURL>" + 
            "<DS_MERCHANT_MERCHANTSIGNATURE>"+getSHA1(firma)+"</DS_MERCHANT_MERCHANTSIGNATURE>" +
            "<DS_MERCHANT_TERMINAL>"+ sTerminal +"</DS_MERCHANT_TERMINAL>" +
            "<DS_MERCHANT_TRANSACTIONTYPE>"+REFUND+"</DS_MERCHANT_TRANSACTIONTYPE>" +
            "</DATOSENTRADA>";
        }
        
        sb.append("entrada=" + URLEncoder.encode(xml, "UTF-8"));

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
        String sReturned = in.readLine();

        in.close();
        
        if (sReturned == null) {
            payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Response empty.");
        } else {
            LaCaixaParser lpp = new LaCaixaParser(sReturned);
            Map props = lpp.splitXML();

            if (lpp.getResult().equals(LocalRes.getIntString("button.ok"))) {
                //printResponse(props);

                if (SALEAPPROVED.equals(props.get("Ds_Response")) || REFUNDAPPROVED.equals(props.get("Ds_Response"))) {
                    //Transaction approved
                    payinfo.paymentOK((String) props.get("Ds_AuthorisationCode"), (String) props.get("Ds_Order"), sReturned);
                } else {
                   
                    String sCode = (String) props.get("Ds_Response");
                    if ("0101".equals(sCode)) {
                        payinfo.paymentError(AppLocal.getIntString("message.paymentnotauthorised"), "Card date expired");
                    } else if ("0102".equals(sCode)) {
                        payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Tarjeta en excepción transitoria o bajo sospecha de fraude.");
                    } else if ("0104".equals(sCode)) {
                        payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Operación no permitida para esa tarjeta o terminal.");
                    } else if ("0116".equals(sCode)) {
                        payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Disponible insuficiente.");
                    } else if ("0118".equals(sCode)) {
                        payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Tarjeta no registrada.");
                    } else if ("0129".equals(sCode)) {
                         payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "CVV2 security code invalid. Amount not supplied or invalid.");
                    } else if ("0180".equals(sCode)) {
                         payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Tarjeta ajena al servicio.");
                    } else if ("0184".equals(sCode)) {
                         payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Cardholder authentication error.");
                    } else if ("0190".equals(sCode)) {
                         payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Denegation of service without reason.");
                    } else if ("0191".equals(sCode)) {
                         payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Expiry date invalid.");
                    } else if ("0202".equals(sCode)) {
                         payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Tarjeta en excepción transitoria o bajo sospecha de fraude con retirada de tarjeta.");
                    } else if ("0904".equals(sCode)) {
                         payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Comercio no registrado en FUC.");
                    } else if ( ("9912".equals(sCode)) || ("912".equals(sCode)) ) {
                         payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Emisor no disponible.");
                    } else {
                         payinfo.paymentError(AppLocal.getIntString("message.paymenterrorunknown"), "");
                    }
                    
                    sCode = (String)props.get("CODIGO");
                    if ("SIS0054".equals(sCode)) {
                    payinfo.paymentError(AppLocal.getIntString("message.paymentnotauthorised"), "Pedido repetido.");
                    } else if ("SIS0078".equals(sCode)) {
                        payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Método de pago no disponible para su tarjeta.");
                    } else if ("SIS0093".equals(sCode)) {
                         payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Tarjeta no válida.");
                    } else if ("SIS0094".equals(sCode)) {
                         payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Error en la llamada al MPI sin controlar.");
                    }
                }
            }
            else {
                payinfo.paymentError(lpp.getResult(), "");
            }
        } 
    } catch (UnsupportedEncodingException eUE) {
            payinfo.paymentError(AppLocal.getIntString("message.paymentexceptionservice"), eUE.getMessage());
        } catch (MalformedURLException eMURL) {
            payinfo.paymentError(AppLocal.getIntString("message.paymentexceptionservice"), eMURL.getMessage());
        } catch(IOException e){
            payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), e.getMessage());
    }
        
    }
        
    public String getSHA1(String input){
        byte[] output = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(input.getBytes()); 
            output = md.digest();
        } catch (Exception e) {
            System.out.println("Exception: "+e);
        }
        return StringUtils.byte2hex(output);
    }

    public class LaCaixaParser extends DefaultHandler {
    
    private SAXParser m_sp = null;
    private Map props = new HashMap();
    private String text;
    private InputStream is;
    private String result;
    
    public LaCaixaParser(String in) {
        is = new ByteArrayInputStream(in.getBytes());
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
            if (qName.equals("CODIGO")) {
                props.put("CODIGO", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("Ds_Version")){
                props.put("Ds_Version", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("Ds_Amount")) {
                props.put("Ds_Amount", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("Ds_Currency")) {
                props.put("Ds_Currency", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("Ds_Order")) {
                props.put("Ds_Order", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("Ds_Signature")) {
                props.put("Ds_Signature", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("Ds_Terminal")) {
                props.put("Ds_Terminal", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("Ds_Response")) {
                props.put("Ds_Response", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("Ds_AuthorisationCode")) {
                props.put("Ds_AuthorisationCode", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("Ds_TransactionType")) {
                props.put("Ds_TransactionType", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("Ds_SecurePayment")) {
                props.put("Ds_SecurePayment", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("Ds_Language")) {
                props.put("Ds_Language", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("Ds_MerchantData")) {
                props.put("Ds_MerchantData", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("Ds_Card_Country")) {
                props.put("Ds_Card_Country", URLDecoder.decode(text, "UTF-8"));
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
