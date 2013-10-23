//    Исходный код для Openbravo POS, автоматизированной системы продаж для работы
//    с сенсорным экраном, предоставлен ТОО "Норд-Трейдинг ЛТД", Республика Казахстан,
//    в период 2008-2010 годов на условиях лицензионного соглашения GNU LGPL.
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

package com.openbravo.pos.panels;

import com.openbravo.format.Formats;
import com.openbravo.pos.util.StringUtils;
import java.util.Date;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
public class PaymentReasonRecord {

    private Date m_Date;
    private String m_sReason;
    private Double m_dTotal;

    public PaymentReasonRecord(Date PaymentData, String PaymentReason, Double PaymentTotal) {
        m_Date = PaymentData;
        m_sReason = PaymentReason;
        m_dTotal = PaymentTotal;
    }

    public Date getDate() {
        return m_Date;
    }

    public String getReason() {
        return m_sReason;
    }

    public Double getTotal() {
        return m_dTotal;
    }

    public String printDate() {
        return Formats.TIMESTAMP.formatValue(m_Date);
    }

    public String printReason() {
        return StringUtils.encodeXML(m_sReason.toString());
    }

    public String printTotal() {
        return Formats.CURRENCY.formatValue(m_dTotal);
    }
}
