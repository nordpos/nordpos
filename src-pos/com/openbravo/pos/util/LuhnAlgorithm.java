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

package com.openbravo.pos.util;

/**
 *
 * @author Mikel Irurita
 */
public class LuhnAlgorithm {
    
    /** Creates a new instance of LuhnAlgorithm */
    private LuhnAlgorithm() {
    } 
    
    public static boolean checkCC(String cardNumber){
        int sum = 0;
        int flip = 0;

        if ( !StringUtils.isNumber(cardNumber) ){
            return false;
        }

        for (int i = cardNumber.length() -1; i >= 0; i--) {
            int k = Character.digit(cardNumber.charAt(i), 10);
            flip ++;

            if ( flip % 2 == 0 ) {
                k *= 2;
                if (k > 9) {
                    k -= 9;
                }
            }
            sum += k;
        }
        return (sum % 10 == 0);
    }

//    public static void main(String[] args) {
//
//        // Testing sample numbers
//
//        System.out.println(LuhnAlgorithm.checkCC("4111111111111111")); // Visa
//        System.out.println(LuhnAlgorithm.checkCC("5500000000000004")); // Master card
//        System.out.println(LuhnAlgorithm.checkCC("340000000000009")); // AMEX
//        System.out.println(LuhnAlgorithm.checkCC("30000000000004")); // Dinners
//        System.out.println(LuhnAlgorithm.checkCC("30000000000004")); // Carte blanche
//        System.out.println(LuhnAlgorithm.checkCC("6011000000000004")); // Discover
//        System.out.println(LuhnAlgorithm.checkCC("201400000000009")); // EnRoute
//        System.out.println(LuhnAlgorithm.checkCC("2131000000000008")); // JCB
//        System.out.println(LuhnAlgorithm.checkCC("6334000000000004")); // Solo
//        System.out.println(LuhnAlgorithm.checkCC("4903010000000009")); // Switch
//    }
}
