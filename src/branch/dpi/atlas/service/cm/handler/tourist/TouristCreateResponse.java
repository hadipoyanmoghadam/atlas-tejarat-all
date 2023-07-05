package branch.dpi.atlas.service.cm.handler.tourist;

import branch.dpi.atlas.service.cm.imf.tourist.TouristToIMFFormater;
import branch.dpi.atlas.service.cm.source.branch.message.TouristMessage;
import dpi.atlas.model.AtlasModel;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;


/**
 * User: R.Nasiri
 * Date: April 24, 2017
 * Time: 08:52 AM
 */
public class TouristCreateResponse extends CMHandlerBase implements Configurable {

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
        TouristToIMFFormater convertor =(TouristToIMFFormater) AtlasModel.getInstance().getBean(beanName) ;
        String responseStr ;
        TouristMessage touristMessage = (TouristMessage)msg.getAttribute(CMMessage.COMMAND_OBJ);
        touristMessage.setActionCode(msg.getAttributeAsString(Fields.ACTION_CODE));
        if(!touristMessage.getActionCode().equals(ActionCode.APPROVED))
            responseStr = touristMessage.createResponseHeader();
        else
            responseStr = convertor.createResponse(msg, holder);
        int msgId = 1;
        if (msg.getAttribute(Fields.MESSAGE_ID) != null)
                msgId = Integer.parseInt((String) msg.getAttribute(Fields.MESSAGE_ID)) + 1;
        msg.setAttribute(Fields.MESSAGE_ID, msgId + "");
        msg.setAttribute(CMMessage.RESPONSE, responseStr );
    }
}
