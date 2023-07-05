package branch.dpi.atlas.service.cm.handler;

import dpi.atlas.model.ModelException;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.IntermediateAccount;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.sql.SQLException;
import java.util.Map;

/**
 * Created by Behnaz .
  * Date: July 10, 2019
  * Time: 13:46:57 PM
 */

public class AccountNoSetter extends TJServiceHandler implements Configurable {
    private String tx;

    public void doProcess(CMMessage cmMessage, Map holder) throws CMFault {

        String channelId = Constants.SPARROW_ID;
        CMCommand command = (CMCommand) cmMessage.getAttribute(CMMessage.REQUEST);

        int srcHostId = Integer.parseInt(command.getParam(Constants.SRC_HOST_ID));
        int destHostId = Integer.parseInt(command.getParam(Constants.DEST_HOST_ID));
        String channelType = command.getParam(Fields.CHANNEL_TYPE);

        try {

            if (tx.equals("source")) {     //ToDo  Reset the chashhhhhhhhhhhhhhh.......
                IntermediateAccount intermediateAccount = ChannelFacadeNew.findIntermediateAccount(channelId, srcHostId, destHostId, channelType);

                command.addParam(Fields.SRC_ACCOUNT, command.getHeaderParam(Fields.SRC_ACCOUNT));
                command.addParam(Fields.TOTAL_DEST_ACCOUNT, command.getHeaderParam(Fields.DEST_ACCOUNT));
                command.addParam(Fields.DEST_ACCOUNT, intermediateAccount.getAccountNo().trim());   //cfs
//                command.addParam(Fields.DOCUMENT_DESCRIPTION, command.getHeaderParam(Fields.DEST_ACCOUNT));

            } else if (tx.equals("destination")) {
                command.addParam(Fields.SRC_ACCOUNT, command.getParam(Fields.DEST_ACCOUNT));    //faragir
                command.addParam(Fields.DEST_ACCOUNT, command.getHeaderParam(Fields.DEST_ACCOUNT));
//                command.addParam(Fields.DOCUMENT_DESCRIPTION, command.getHeaderParam(Fields.SRC_ACCOUNT));
                command.addParam(Fields.TOTAL_DEST_ACCOUNT, command.getHeaderParam(Fields.SRC_ACCOUNT));
            }

        } catch (NotFoundException e) {
            log.debug("Intermediate Account with channelId = " + channelId + " Not Found");
            cmMessage.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, new CMFault(ActionCode.ACCOUNT_NOT_FOUND));
        } catch (ModelException e) {
            log.debug("General Data Error in Finding Intermediate Account with channelId = " + channelId);
            cmMessage.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_DATA_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new CMFault(ActionCode.GENERAL_DATA_ERROR));
        } catch (SQLException e) {
            log.error("DB has encountered an exception during fething data from TBIntermediateAcc...");
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.GENERAL_DATA_ERROR));
        }

        cmMessage.setAttribute(CMMessage.REQUEST, command);

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        tx = cfg.get("Tx");
    }

}
