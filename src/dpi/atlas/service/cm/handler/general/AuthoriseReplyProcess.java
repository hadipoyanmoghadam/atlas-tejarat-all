package dpi.atlas.service.cm.handler.general;

import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.model.facade.sw.SwitchFacade;
import dpi.atlas.model.AtlasModel;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

/**
 * Created by Behnaz (EBanking 86/10/01).
 * To change this template use File | Settings | File Templates.
 *
 */
public class AuthoriseReplyProcess extends TJServiceHandler implements Configurable {

    SwitchFacade switchFacade;



    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        if (log.isInfoEnabled()) log.info("Inside AuthoriseReplyProcess:process()  -- " + this.getClass().getName());
        CMCommand cmCommand = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

        try {
            String reply = (String) holder.get("connector-reply");
//            String reply = msg.getAttributeAsString(CMMessage.RESPONSE_STRING);

            CMResultSet result = null;
            if (reply != null) {
                if (log.isInfoEnabled()) log.info("receiving 3: " + reply);
                result = new CMResultSet(reply);
                //holder.put(CMMessage.RESPONSE, result);
//                msg.setAttribute(CMMessage.RESPONSE, result);

            } else {
                if (log.isInfoEnabled()) log.info("timeout expired...");
                result = new CMResultSet();
                result.setHeaderField(Fields.ACTION_CODE, ActionCode.SYSTEM_NOT_AVAILABLE);
                result.setHeaderField(Fields.ACTION_MESSAGE, "no response from Authorise server whitin timeout");
            }

            if (log.isInfoEnabled()) log.info("result = " + result);

            msg.setAttribute(CMMessage.RESPONSE, result);
            msg.setAttribute(Fields.AUTHORISED,result.getHeaderField(Fields.AUTHORISED));

            if (result.getHeaderField(Fields.AUTHORISED).equals(Constants.NOTAUTHORISED))
            {   if(result.getHeaderField(Params.ACTION_CODE)==null)
                  {result.setHeaderField(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
                   result.setHeaderField(Fields.ACTION_MESSAGE, "customer is not Authorise!");}
               throw new CMFault(CMFault.FAULT_INTERNAL, "customer is not Authorise!");
            }
            else{
                msg.setAttribute(Constants.SRC_HOST_ID,result.getHeaderField(Constants.SRC_HOST_ID));
                log.info("host_ID="+msg.getAttribute(Constants.SRC_HOST_ID));
                if(msg.hasAttribute(Fields.DEST_ACCOUNT_NO))
                { msg.setAttribute(Constants.DEST_HOST_ID,result.getHeaderField(Constants.DEST_HOST_ID));
                    log.info("Dest_host_ID="+msg.getAttribute(Constants.DEST_HOST_ID));}
            }



        } catch (Exception e) {
            log.error(e);
            CMResultSet result = new CMResultSet();
            result.setHeaderField(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            result.setHeaderField(Params.ACTION_MESSAGE, e.getMessage());
            msg.setAttribute(CMMessage.RESPONSE_STRING, result.toString());
            throw new CMFault(CMFault.FAULT_INTERNAL, e);
        }
    }



    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        switchFacade = AtlasModel.getInstance().getSwitchFacade();

    }


}
