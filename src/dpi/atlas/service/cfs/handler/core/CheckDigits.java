package dpi.atlas.service.cfs.handler.core;

import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jan 21, 2008
 * Time: 1:51:46 PM
 * To change this template use File | Settings | File Templates.
 */
//TODO 'CheckDigits' is a general purpose and must ne moved into general package such as 'dpi.atlas.service.GENERAL.handler'
// TODO Instead of 'CFSFault' or 'CMFault' is should be issued a general fault such as 'GeneralFault'
public class CheckDigits extends CFSHandlerBase implements Configurable {
    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        StringBuffer digitsBuffer = new StringBuffer();
        StringBuffer totalDigitBuffer = new StringBuffer();

        //  BillId checking
        String blpyUtilityTypeCode = (String) msg.getAttribute(Fields.BLPY_UTILITY_TYPE_CODE);
        String blpySubSidiaryCompanyCode = (String) msg.getAttribute(Fields.BLPY_SUBSIDIARY_CODE);
        String blpyFileCode = (String) msg.getAttribute(Fields.BLPY_FILE_CODE);

        Integer blpyBillCheckDigit = (Integer) msg.getAttribute(Fields.BLPY_BILL_CHECK_DIGIT);
        Integer blpyPaymentCheckDigit = (Integer) msg.getAttribute(Fields.BLPY_PAYMENT_CHECK_DIGIT);
        if (log.isDebugEnabled()) log.debug("blpyPaymentCheckDigit=" + blpyPaymentCheckDigit);
        Integer totalCheckDigit = (Integer) msg.getAttribute(Fields.BLPY_TOTAL_CHECK_DIGIT);
        if (log.isDebugEnabled()) log.debug("totalCheckDigit=" + totalCheckDigit);

        digitsBuffer.append(blpyFileCode).append(blpySubSidiaryCompanyCode).append(blpyUtilityTypeCode);
        if (log.isDebugEnabled()) log.debug("digitsBuffer = " + digitsBuffer);
        checkDigitValidity(digitsBuffer, blpyBillCheckDigit.intValue(), msg);
        digitsBuffer.append(blpyBillCheckDigit);
        totalDigitBuffer.append(digitsBuffer);

        //  PaymentId checking
        String blpyPeriodCode = (String) msg.getAttribute(Fields.BLPY_PERIOD_CODE);
        String blpyYearCode = (String) msg.getAttribute(Fields.BLPY_YEAR_CODE);

        if (log.isDebugEnabled()) log.debug("msg.getAttribute(Fields.AMOUNT) = " + msg.getAttribute(Fields.AMOUNT));
        String amountStr = (String) msg.getAttribute(Fields.AMOUNT);
        if (log.isDebugEnabled()) log.debug("real amount = " + amountStr);
        amountStr = amountStr.substring(0, amountStr.length() - 3);
        long amount = Long.parseLong(amountStr);
        if (log.isDebugEnabled()) log.debug("final amount = " + amount);

        digitsBuffer.delete(0, digitsBuffer.length());
        digitsBuffer.append(String.valueOf(amount)).append(blpyYearCode).append(blpyPeriodCode);
        if (log.isDebugEnabled()) log.debug(" PaymentBuffer=" + digitsBuffer.toString());
        checkDigitValidity(digitsBuffer, blpyPaymentCheckDigit.intValue(), msg);
        digitsBuffer.append(blpyPaymentCheckDigit);
        totalDigitBuffer.append(digitsBuffer);
        if (log.isDebugEnabled()) log.debug("totalDigitBuffer=" + totalDigitBuffer.toString());

        checkDigitValidity(totalDigitBuffer, totalCheckDigit.intValue(), msg);

    }

    private void checkDigitValidity(StringBuffer billDigitBuffer, int blpyBillCheckDigit, CMMessage msg) throws CFSFault {
        if (log.isDebugEnabled()) log.debug("blpyBillCheckDigit=" + blpyBillCheckDigit);

        int coefficient = 2;
        int criteria = 11;
        int total = 0;
        for (int i = 1; i <= billDigitBuffer.length(); i++) {
            char digit = billDigitBuffer.charAt(billDigitBuffer.length() - i);
            total += Integer.parseInt(String.valueOf(digit)) * coefficient;
            if (log.isDebugEnabled())
                log.debug(" digit=" + digit + " * " + " coefficient=" + coefficient + "==> total= " + total);
            coefficient++;
            if (coefficient >= 8) {
                coefficient = 2;
            }
        }

        if (log.isDebugEnabled()) log.debug("final total=" + total);
        int resultedCheckDigit = total % criteria;
        if (log.isDebugEnabled()) log.debug("resultedCheckDigit=" + resultedCheckDigit);

        if ((resultedCheckDigit == 0) || (resultedCheckDigit == 1)) {
            resultedCheckDigit = 0;
        } else {
            resultedCheckDigit = criteria - resultedCheckDigit;
        }
        if (log.isDebugEnabled()) log.debug("resultedCheckDigit=" + resultedCheckDigit);
        if (resultedCheckDigit != blpyBillCheckDigit) {
            if (log.isDebugEnabled()) log.debug(" Digits are not valid");
            msg.setAttribute(Fields.APPROVED, Constants.NOTAUTHORISED);
            msg.setAttribute(Fields.AUTHORISED, Constants.NOTAUTHORISED);
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.PAYMENT_INVALID_DIGITS);
            throw new CFSFault(CFSFault.FLT_BILL_PAYMENT_INVALID_DIGITS, new Exception(ActionCode.PAYMENT_INVALID_DIGITS));
        }
    }
}
