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

import java.io.UnsupportedEncodingException;
import java.security.*;
import javax.crypto.*;

public class AltEncrypter {
    
    private Cipher cipherDecrypt;
    private Cipher cipherEncrypt;
    
    /** Creates a new instance of Encrypter */
    public AltEncrypter(String passPhrase) {
        
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(passPhrase.getBytes("UTF8"));
            KeyGenerator kGen = KeyGenerator.getInstance("DESEDE");
            kGen.init(168, sr);
            Key key = kGen.generateKey();

            cipherEncrypt = Cipher.getInstance("DESEDE/ECB/PKCS5Padding");
            cipherEncrypt.init(Cipher.ENCRYPT_MODE, key);
            
            cipherDecrypt = Cipher.getInstance("DESEDE/ECB/PKCS5Padding");
            cipherDecrypt.init(Cipher.DECRYPT_MODE, key);
        } catch (UnsupportedEncodingException e) {
        } catch (NoSuchPaddingException e) {
        } catch (NoSuchAlgorithmException e) {
        } catch (InvalidKeyException e) {
        }
    }
    
    public String encrypt(String str) {
        try {
            return StringUtils.byte2hex(cipherEncrypt.doFinal(str.getBytes("UTF8")));
        } catch (UnsupportedEncodingException e) {
        } catch (BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        }
        return null;
    }
    
    public String decrypt(String str) {
        try {
            return new String(cipherDecrypt.doFinal(StringUtils.hex2byte(str)), "UTF8");
        } catch (UnsupportedEncodingException e) {
        } catch (BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        }
        return null;
    }    
}
