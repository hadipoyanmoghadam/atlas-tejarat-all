package branch.dpi.atlas.service.cm.handler.groupWeb.cardStatement;

import branch.dpi.atlas.service.cm.handler.pg.cardStatement.CardStatementReq;
import branch.dpi.atlas.service.cm.handler.pg.cardStatement.StatementTx;
import branch.dpi.atlas.service.cm.handler.pg.cardStatement.StatementTxList;
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
public class cardStatementResponse extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            CardStatementReq statementMsg = (CardStatementReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
            String actionCode = msg.getAttributeAsString(Params.ACTION_CODE);
            StringBuilder responseStr = new StringBuilder();

            //STATEMENT_GROUPCARD_RESPONSE#ActionCode#RRN#CardNo#FromDate#ToDate#Respdatetime#TransCount#OpCode#Amount#CRDB#Date#Time#DocNo#AcquirerId#Row

            responseStr.append("STATEMENT_GROUPCARD_RESPONSE")
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(actionCode)
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(statementMsg.getRrn())
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(statementMsg.getCardno())
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(statementMsg.getFromDate())
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(statementMsg.getToDate())
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(DateUtil.getSystemDate() + DateUtil.getSystemTime());

            if (actionCode.equals(ActionCode.APPROVED)) {
                responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(msg.getAttributeAsString(Fields.TRANS_COUNT).trim());
                StatementTxList transaction=(StatementTxList) msg.getAttribute(Fields.TRANSACTION);
                List<StatementTx> tx=transaction.getTx();
                for(int i=0;i<tx.size();i++){
                    responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(tx.get(i).getOpCode())
                                .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(tx.get(i).getAmount())
                                .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(tx.get(i).getCrdb())
                                .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(tx.get(i).getDate())
                                .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(tx.get(i).getTime())
                                .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(tx.get(i).getDocNo())
                                .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(tx.get(i).getAcquirerId())
                                .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(tx.get(i).getRow());
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

