package branch.dpi.atlas.service.cm.handler.branch;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import branch.dpi.atlas.util.FarsiUtil;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: May 5 2019
 * Time: 8:32 AM
 */
public class ChangeAccountType extends TJServiceHandler implements Configurable {
    public void doProcess(CMMessage msg, Map map) throws CMFault {
        BranchMessage bm = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        try {
            String accountType;
            if (bm.getRequestType().equals("1"))   //normal account
                accountType = Constants.NORMAL_ACCOUNT;
            else if (bm.getRequestType().equals("2"))  //Group Card account
                accountType = Constants.GROUP_CARD_ACCOUNT;
            else {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.UNSUPPORTED_MESSAGE);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.UNSUPPORTED_MESSAGE));
            }

            String sessionId = String.valueOf(msg.getAttribute(Fields.SESSION_ID));
            String channelType=msg.getAttributeAsString(CMMessage.SERVICE_TYPE);
            String desc = "";
            byte[] descByte = {124, 33, 88, 88, 39, 84, 85, 71, 37, 65, 60, 40, 58};
            try {
                String s = new String(descByte, "ISO-8859-1");
                desc = FarsiUtil.convertWindows1256(descByte);
            } catch (UnsupportedEncodingException e) {
                desc = "";
            }

            int result=ChannelFacadeNew.changeAccountType(bm.getAccountNo(), sessionId,
                    bm.getUserId(), bm.getBranchCode(), accountType, channelType, desc,Constants.TERMINAL_ID_NASIM);
            switch (result){
                case 0: //Approve
                    break;
                case 1: // account type has changed before.
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_TYPE_HAS_CHANGED_BEFORE);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_TYPE_HAS_CHANGED_BEFORE));
                case 2: // invalid operation
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_OPERATION));
                case 3: // accountNo does not exist
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
                case 4: //account assigned to card
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_ASSIGNED_TO_CARD);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_ASSIGNED_TO_CARD));
               default:
                    log.error("ERROR:: invalid result");
                    throw new Exception();
            }
        } catch (SQLException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (CMFault e) {
            throw e;
        } catch (Exception e) {
            log.error("ERROR :::Inside SetServiceStatus.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
    }
}
