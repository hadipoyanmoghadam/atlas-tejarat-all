package dpi.atlas.service.cm.handler.general;

import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.util.CMUtil;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.util.Constants;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.*;

/**
  * Created by Behnaz.
 * To change this template use File | Settings | File Templates.
 */

public class CheckTransactionPermission extends TJServiceHandler implements Configurable {

    ArrayList Transaction_Not_Permitted = new ArrayList();

    public void setConfiguration(Configuration configuration) throws ConfigurationException {
        String transactions = configuration.get("TransactionNotPermitted");
        Transaction_Not_Permitted= CMUtil.tokenizString(transactions,",");
        
    }

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        log.debug("inside CheckTransactionPermission.doProcess(CMMessage msg, Map holder)");

        List   TransactionPermitted= Arrays.asList(new String[]{"NBAL","POSNBAL","NFCSH","NCSHW","NSLCSHR"
                ,"CSHR","SALR","SALCSHR","NFTT2NT","NFTNT2T","NFTT2T","NFTNT2TR","NFTT2TR","NBLPY"});


        try {

            String commandName = msg.getAttributeAsString(Fields.COMMAND);
            String messageType = getAttribute(msg, holder, "messageType");

            if ((Transaction_Not_Permitted.contains(messageType) && !TransactionPermitted.contains(commandName))) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));
            }

        if ( msg.getAttributeAsString(Constants.SRC_ACCOUNT_GROUP).equalsIgnoreCase(Constants.GIFT_CARD_007)  ||
            (msg.hasAttribute(Constants.DEST_ACCOUNT_GROUP) && msg.getAttributeAsString(Constants.DEST_ACCOUNT_GROUP).equalsIgnoreCase(Constants.GIFT_CARD_007))){

            msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_OPERATION));
        }
        //group card
         if (ChannelFacadeNew.isGroupCard(msg.getAttributeAsString(Constants.SRC_ACCOUNT_GROUP))) {
             msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_OPERATION));
        }
        } catch (Exception ex) {
            log.error(ex);
            ex.toString();  //To change body of catch statement use File | Settings | File Templates.
            throw new CMFault(CMFault.FAULT_EXTERNAL);
        }
    }

    private String getAttribute(CMMessage cmMessage, Map map, String key) {
        String str = (String) cmMessage.getAttribute(key);
        if (str == null)
            str = (String) map.get(key);
        return str;
    }

}
