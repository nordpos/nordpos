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
package com.nordpos.payment.gateway;

import com.openbravo.pos.forms.AppProperties;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PaymentGatewayFactory {

    private static final Logger logger = Logger.getLogger(PaymentGatewayFactory.class.getName());

    private PaymentGateway paymentGateway;

    public PaymentGatewayFactory(AppProperties props) {
        ServiceLoader<PaymentGatewayInterface> gatewayLoader = ServiceLoader.load(PaymentGatewayInterface.class);

        for (PaymentGatewayInterface gatewayInterface : gatewayLoader) {
            try {
                paymentGateway = gatewayInterface.getPaymentGateway(props.getProperty("payment.gateway"));
            } catch (Exception e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        }

//        if ("external".equals(sReader)) {
//            return new PaymentGatewayExt();
//        } else if ("PayPoint / SecPay".equals(sReader)) {
//            return new PaymentGatewayPayPoint(props);
//        } else if ("AuthorizeNet".equals(sReader)) {
//            return new PaymentGatewayAuthorizeNet(props);
//        } else if ("La Caixa (Spain)".equals(sReader)) {
//            return new PaymentGatewayCaixa(props);
//        } else if ("Planetauthorize".equals(sReader)) {
//            return new PaymentGatewayPlanetauthorize(props);
//        } else if ("Firs Data / LinkPoint / YourPay".equals(sReader)) {
//            return new PaymentGatewayLinkPoint(props);
//        } else if ("PaymentsGateway.net".equals(sReader)) {
//            return new PaymentGatewayPGNET(props);
//        } else {
//            return null;
//        }
    }

    public PaymentGateway getPaymentGateway() {
        return paymentGateway;
    }
}
