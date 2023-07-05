package branch.dpi.atlas.service.cm.handler.manzume;

import branch.dpi.atlas.service.cm.source.manzume.message.ManzumeMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import org.jpos.core.Configurable;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: dpi04
 * Date: Jan 9, 2017
 * Time: 8:03:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class GetVirtualAccounts extends TJServiceHandler implements Configurable {
    private static Log log = LogFactory.getLog(GetVirtualAccounts.class);

    public static List messageType;

    static {
        String[] messageTypes = {TJCommand.CMD_TRANSFER_FUND};
        messageType = Arrays.asList(messageTypes);
    }

    public static String _doDstAccConversion(CMMessage msg) throws CMFault {
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        log.debug("inside GetVirtualAccounts._doDstAccConversion(CMMessage msg)");
        msg.setAttribute(Fields.AUTHORISED, Constants.AUTHORISED);

        ManzumeMessage manzumeMessage = (ManzumeMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);

        String dstAccNo = manzumeMessage.getAccountNo() != null ? manzumeMessage.getAccountNo() : "";

        try {
            dstAccNo = ISOUtil.padleft(dstAccNo.trim(), 13, '0');
        } catch (ISOException e) {
            log.error("Error in GetVirtualAccounts()! Error:" + e);
        }

//        String dstAccNo = msg.getAttributeAsString("destAccountNo") != null ? msg.getAttributeAsString("destAccountNo") : "";

        //String virtualDstAcc = msg.getAttributeAsString("destAccountNo") != null ? msg.getAttributeAsString("destAccountNo") : "";
        String virtualDstAcc = dstAccNo;
        try {
            Map accounts = ChannelFacadeNew.getVirtualAcc();
            if (accounts.containsKey(ISOUtil.zeroUnPad(dstAccNo)))
                dstAccNo = (String) accounts.get(ISOUtil.zeroUnPad(virtualDstAcc));
            if(dstAccNo!=null){
            dstAccNo=ISOUtil.padleft(dstAccNo.trim(),13,'0');}
            msg.setAttribute(Fields.DEST_ACCOUNT_NO, dstAccNo);
            msg.setAttribute(Fields.DEST_ACCOUNT, dstAccNo);
//            command.addParam(Fields.DEST_ACCOUNT_NO,dstAccNo);
//            command.addParam(Fields.DEST_ACCOUNT, dstAccNo);
//            command.addParam(Fields.DEST_ACCOUNT_NO, dstAccNo);


        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (ISOException e) {
          log.error("Exception in CheckVirtualAccount()!!");
        }
        return dstAccNo;

    }


    public void doProcess(CMMessage msg, Map map) throws CMFault {
//        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
//        if (!messageType.contains(command.getCommandName()))
//            return;
        _doDstAccConversion(msg);
    }
}

