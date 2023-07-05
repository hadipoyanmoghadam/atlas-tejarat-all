package branch.dpi.atlas.service.cm.handler.branch;

import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.iso2000.ActionCode;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.service.cm.imf.Fields;
import branch.dpi.atlas.service.cm.imf.branch.BranchToIMFFormater;
import dpi.atlas.model.AtlasModel;

import java.util.Map;

import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.core.Configurable;

/**
 * User: H.Ghayoumi
 * Date: Apr 17, 2013
 * Time: 12:22:21 PM
 */
public class BranchCreateResponse extends CMHandlerBase implements Configurable {

      private String action_code;

      public void setConfiguration(Configuration configuration) throws ConfigurationException {
         action_code = configuration.get("action-code");
        if ((action_code == null))
            action_code = "";
    }

    public void doProcess(CMMessage msg, Map holder ) throws CMFault
    {
        if (log.isInfoEnabled()) log.info("Inside BranchCreateResponse:process()");
        if(!action_code.equals(""))
            msg.setAttribute(Fields.ACTION_CODE, action_code);
        String pin = msg.getAttributeAsString(Fields.PIN);
        String beanName = "convertor" + pin;
        BranchToIMFFormater convertor =(BranchToIMFFormater)AtlasModel.getInstance().getBean(beanName) ;
        String responseStr ;
        BranchMessage branchMsg = (BranchMessage)msg.getAttribute(CMMessage.COMMAND_OBJ);
        branchMsg.setActionCode(msg.getAttributeAsString(Fields.ACTION_CODE));
        if(!branchMsg.getActionCode().equals(ActionCode.APPROVED))
            responseStr = branchMsg.createResponseHeader();
        else
            responseStr = convertor.createResponse(msg, holder);
        int msgId = 1;
        if (msg.getAttribute(Fields.MESSAGE_ID) != null)
                msgId = Integer.parseInt((String) msg.getAttribute(Fields.MESSAGE_ID)) + 1;
        msg.setAttribute(Fields.MESSAGE_ID, msgId + "");
        msg.setAttribute(CMMessage.RESPONSE, responseStr );
    }
}
