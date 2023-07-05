package branch.dpi.atlas.service.cm.handler.AMX;

import branch.dpi.atlas.service.cm.source.branch.message.AmxMessage;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;


/**
 * User: R.Nasiri
 * Date: Sep 17, 2019
 * Time: 10:43 AM
 */

public class CheckSafLog4FollowUp extends TJServiceHandler implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        String session_id = msg.getAttributeAsString(Fields.SESSION_ID_ORIG);

        try {
            List<String> transaction = ChannelFacadeNew.checkSafLogBR(session_id);
            if(transaction==null || transaction.size()<1){
                //transaction not found in saf_log
                msg.setAttribute(Fields.FOLLOWUP_ACTION_CODE, ActionCode.FUND_TRANSFER_MUST_BE_REVERSED);
            }else{

                String safName=transaction.get(1);
                String handled=transaction.get(2);
                if(safName.trim().equalsIgnoreCase(Constants.REVERSE_SAF_NAME)){
                    //this transaction is internal reverse
                    msg.setAttribute(Fields.FOLLOWUP_ACTION_CODE, ActionCode.FLW_HAS_NO_ORIGINAL);
                }else {
                    if(handled.equalsIgnoreCase(Constants.PENDING_HANDLED))
                        msg.setAttribute(Fields.FOLLOWUP_ACTION_CODE, ActionCode.TRANSACTION_PENDING);
                    else if(handled.equalsIgnoreCase(Constants.SUCCESS_HANDLED))
                        msg.setAttribute(Fields.FOLLOWUP_ACTION_CODE, ActionCode.APPROVED);
                    else if(handled.equalsIgnoreCase(Constants.ERROR_HANDLED))
                        msg.setAttribute(Fields.FOLLOWUP_ACTION_CODE, ActionCode.DEST_TRANSACTION_NOT_FOUND);
                }
            }

        } catch (SQLException e) {
            log.error(e);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Params.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_EXTERNAL, ActionCode.DATABASE_ERROR);
        } catch (Exception e) {
            log.error(":::Inside CheckTransaction4FollowUp.doProcess >>" + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, ActionCode.GENERAL_ERROR);
        }
    }

}