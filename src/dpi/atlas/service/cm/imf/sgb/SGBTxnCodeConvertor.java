package dpi.atlas.service.cm.imf.sgb;

import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cm.core.TJCommand;

/**
 * Created by IntelliJ IDEA.
 * User: Shahram Boroon
 * Date: Oct 16, 2005
 * Time: 10:18:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class SGBTxnCodeConvertor {
    public static String imfToSGBTxCodeConvertor(String imfRxCode) {

        if (imfRxCode.equals(TJCommand.CMD_FAST_CASH) ||
                imfRxCode.equals(TJCommand.CMD_NETWORK_FAST_CASH))
            return SGBTxn.FAST_CASH;

        else if (imfRxCode.equals(TJCommand.CMD_CASH_REQUEST) ||
                imfRxCode.equals(TJCommand.CMD_SALE_REQUEST) ||
                imfRxCode.equals(TJCommand.CMD_SALE_WITH_CASH_REQUEST))
            return SGBTxn.POS_TXN;

        else if (imfRxCode.equals(TJCommand.CMD_TRANSFER_FUND) ||
                imfRxCode.equals(TJCommand.CMD_NETWORK_FUND_TRANSFER_TEJ2TEJ)||
                imfRxCode.equals(TJCommand.CMD_NETWORK_FUND_TRANSFER_NONTEJ2TEJ)||
                imfRxCode.equals(TJCommand.CMD_NETWORK_FUND_TRANSFER_TEJ2NONTEJ))

            return SGBTxn.TRANSFER_FUND;

        else if (imfRxCode.equals(TJCommand.CMD_CASH_WITHDRAWAL) ||
                imfRxCode.equals(TJCommand.CMD_NETWORK_CASH_WITHDRAWAL))
            return SGBTxn.CASH_WITHDRAWAL;

        else if (imfRxCode.equals(TJCommand.CMD_TERMINAL_RECONCILIATION))
            return SGBTxn.TERMINAL_RECONCILIATION;

        else if (imfRxCode.equals(TJCommand.CMD_FAST_CASH_REVERSAL) ||
                imfRxCode.equals(TJCommand.CMD_NETWORK_FAST_CASH_REVERSAL))
            return SGBTxn.FAST_CASH_REVERSAL;

        else if (imfRxCode.equals(TJCommand.CMD_CASH_REQUEST_REVERSAL) ||
                imfRxCode.equals(TJCommand.CMD_SALE_REQUEST_REVERSAL) ||
                imfRxCode.equals(TJCommand.CMD_SALE_WITH_CASH_REQUEST_REVERSAL))
            return SGBTxn.POS_TXN_REVERAL;

        else if (imfRxCode.equals(TJCommand.CMD_TRANSFER_FUND_REVERSAL) ||
                imfRxCode.equals(TJCommand.CMD_NETWORK_FUND_TRANSFER_TEJ2TEJ_REVERSAL)||
                imfRxCode.equals(TJCommand.CMD_NETWORK_FUND_TRANSFER_TEJ2NONTEJ_REVERSAL)||
                imfRxCode.equals(TJCommand.CMD_NETWORK_FUND_TRANSFER_NONTEJ2TEJ_REVERSAL))
            return SGBTxn.TRANSFER_FUND_REVERSAL;

        else if (imfRxCode.equals(TJCommand.CMD_CASH_WITHDRAWAL_REVERSAL) ||
                imfRxCode.equals(TJCommand.CMD_NETWORK_CASH_WITHDRAWAL_REVERSAL))
            return SGBTxn.CASH_WITHDRAWAL_REVERSAL;

        else // This transaction type is not placed in SGB file, so return a null value
            return null;

    }

    public static String extractDebitCredit(String imfRxCode) throws Exception {

        if (imfRxCode.equals(TJCommand.CMD_FAST_CASH) ||
                imfRxCode.equals(TJCommand.CMD_NETWORK_FAST_CASH))
            return CFSConstants.SGB_DEBIT_STR;

        else if (imfRxCode.equals(TJCommand.CMD_CASH_REQUEST) ||
                imfRxCode.equals(TJCommand.CMD_SALE_REQUEST) ||
                imfRxCode.equals(TJCommand.CMD_SALE_WITH_CASH_REQUEST))
            return CFSConstants.SGB_DEBIT_STR;

//        else if (imfRxCode.equals(CMCommand.CMD_TRANSFER_FUND) ||
//                 imfRxCode.equals(CMCommand.CMD_NETWORK_FUND_TRANSFER_TEJ2TEJ))
//            return debit_credit;

        else if (imfRxCode.equals(TJCommand.CMD_CASH_WITHDRAWAL) ||
                imfRxCode.equals(TJCommand.CMD_NETWORK_CASH_WITHDRAWAL))
            return CFSConstants.SGB_DEBIT_STR;

//        else if (imfRxCode.equals(CMCommand.CMD_TERMINAL_RECONCILIATION))
//            return CFSConstants.SGB_CREDIT_STR;

        else if (imfRxCode.equals(TJCommand.CMD_FAST_CASH_REVERSAL) ||
                imfRxCode.equals(TJCommand.CMD_NETWORK_FAST_CASH_REVERSAL))
            return CFSConstants.SGB_CREDIT_STR;

        else if (imfRxCode.equals(TJCommand.CMD_CASH_REQUEST_REVERSAL) ||
                imfRxCode.equals(TJCommand.CMD_SALE_REQUEST_REVERSAL) ||
                imfRxCode.equals(TJCommand.CMD_SALE_WITH_CASH_REQUEST_REVERSAL))
            return CFSConstants.SGB_CREDIT_STR;

//        else if (imfRxCode.equals(CMCommand.CMD_TRANSFER_FUND_REVERSAL) ||
//                 imfRxCode.equals(CMCommand.CMD_NETWORK_FUND_TRANSFER_TEJ2TEJ_REVERSAL))
//            return debit_credit;

        else if (imfRxCode.equals(TJCommand.CMD_CASH_WITHDRAWAL_REVERSAL) ||
                imfRxCode.equals(TJCommand.CMD_NETWORK_CASH_WITHDRAWAL_REVERSAL))
            return CFSConstants.SGB_CREDIT_STR;

        else if (imfRxCode.equals(TJCommand.CMD_LORO_REVERSAL) )
            return CFSConstants.SGB_CREDIT_STR;

        else
            throw new Exception("Invalid transaction code: " + imfRxCode);
    }
}