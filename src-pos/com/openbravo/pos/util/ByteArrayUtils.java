//    Исходный код для Openbravo POS, автоматизированной системы продаж для работы
//    с сенсорным экраном, предоставлен ТОО "Норд-Трейдинг ЛТД", Республика Казахстан,
//    в период 2008-2011 годов на условиях лицензионного соглашения GNU LGPL.
//
//    Исходный код данного файл разработан в рамках проекта Openbravo POS ru
//    для использования системы Openbravo POS на территории бывшего СССР
//    <http://code.google.com/p/openbravoposru/>.
//
//    Openbravo POS является свободным программным обеспечением. Вы имеете право
//    любым доступным образом его распространять и/или модифицировать соблюдая
//    условия изложенные в GNU Lesser General Public License версии 3 и выше.
//
//    Данный исходный распространяется как есть, без каких либо гарантий на его
//    использование в каких либо целях, включая коммерческое применение. Данный
//    исход код может быть использован для связи с сторонними библиотеками
//    распространяемыми под другими лицензионными соглашениями. Подробности
//    смотрите в описании лицензионного соглашение GNU Lesser General Public License.
//
//    Ознакомится с условиями изложенными в GNU Lesser General Public License
//    вы можете в файле lgpl-3.0.txt каталога licensing проекта Openbravo POS ru.
//    А также на сайте <http://www.gnu.org/licenses/>.
package com.openbravo.pos.util;

/**
 * @author Andrey Svininykh svininykh@gmail.com
 */
public class ByteArrayUtils {

    private ByteArrayUtils() {
    }

    public static String getHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += " " + Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public static final byte[] convertIntegerToByteArray(int value, int bytes, boolean revers) {
        if (bytes == 1 && !revers) {
            return new byte[]{
                        (byte) value
                    };
        } else if (bytes == 1 && revers) {
            return new byte[]{
                        (byte) (value >>> 24)
                    };
        } else if (bytes == 2 && !revers) {
            return new byte[]{
                        (byte) (value >>> 8),
                        (byte) value};
        } else if (bytes == 2 && revers) {
            return new byte[]{
                        (byte) (value >>> 24),
                        (byte) (value >>> 16)
                    };
        } else if (bytes == 3 && !revers) {
            return new byte[]{
                        (byte) (value >>> 16),
                        (byte) (value >>> 8),
                        (byte) value};
        } else if (bytes == 3 && revers) {
            return new byte[]{
                        (byte) (value >>> 24),
                        (byte) (value >>> 16),
                        (byte) (value >>> 8)};
        } else {
            return new byte[]{
                        (byte) (value >>> 24),
                        (byte) (value >>> 16),
                        (byte) (value >>> 8),
                        (byte) value};
        }
    }
}
