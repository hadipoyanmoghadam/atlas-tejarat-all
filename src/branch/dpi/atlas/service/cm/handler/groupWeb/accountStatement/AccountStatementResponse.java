package branch.dpi.atlas.service.cm.handler.groupWeb.accountStatement;

import branch.dpi.atlas.service.cm.handler.pg.accountStatement.AccountStatementReq;
import branch.dpi.atlas.service.cm.handler.pg.accountStatement.AccountStatementTx;
import branch.dpi.atlas.service.cm.handler.pg.accountStatement.AccountStatementTxList;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import java.util.List;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: March 13, 2020
 * Time: 07:51 PM
 */
public class AccountStatementResponse extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            String actionCode = msg.getAttributeAsString(Params.ACTION_CODE);
            StringBuilder responseStr = new StringBuilder();
            AccountStatementReq statementMsg = (AccountStatementReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
            //STATEMENT_RESPONSE#ActionCode#RRN#AccountNo#FromDate#ToDate#Respdatetime
            // #TransCount#OpCode#Amount#CRDB#Date#Time#DocNo#BranchCode#LastBalance#CardNo#Row

            responseStr.append("STATEMENT_RESPONSE")
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(actionCode)
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(statementMsg.getRrn())
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(statementMsg.getAccountNo())
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(statementMsg.getFromDate())
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(statementMsg.getToDate())
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(DateUtil.getSystemDate() + DateUtil.getSystemTime());

            if (actionCode.equals(ActionCode.APPROVED)) {
                responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(msg.getAttributeAsString(Fields.TRANS_COUNT).trim());

                AccountStatementTxList transaction=(AccountStatementTxList) msg.getAttribute(Fields.TRANSACTION);
                List<AccountStatementTx> list=transaction.getTx();

                for(int i=0;i<list.size();i++){
                    responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(list.get(i).getOpCode());
                    responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(list.get(i).getAmount());
                    responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(list.get(i).getCrdb());
                    responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(list.get(i).getDate());
                    responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(list.get(i).getTime());
                    responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(list.get(i).getDocNo());
                    responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(list.get(i).getBrCode());
                    responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(list.get(i).getLastBal());
                    responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(list.get(i).getCardNo());
                    responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(list.get(i).getRow());
                }

            }

            msg.setAttribute(CMMessage.RESPONSE, responseStr.toString());
        } catch (Exception e) {
            log.error("ERROR :::Inside cardStatementResponse.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }


    }
}

