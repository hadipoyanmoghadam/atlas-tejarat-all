package branch.dpi.atlas.service.cm.handler.branch;

import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.Params;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import branch.dpi.atlas.service.cm.imf.branch.BranchToIMFFormater;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.model.AtlasModel;

import java.util.Map;

/**
 * User: H.Ghayoumi
 * Date: Apr 15, 2013
 * Time: 5:22:06 PM
 */
public class ToIMFHandler extends CMHandlerBase  {

    public void doProcess(CMMessage msg, Map holder) throws CMFault{

        BranchMessage branchMsg = (BranchMessage)msg.getAttribute(CMMessage.COMMAND_OBJ);
        String pin = msg.getAttributeAsString(Fields.PIN);
        String beanName = "convertor" + pin;
        int msgId;
        try {

            try {
                msgId = Integer.parseInt(msg.getAttributeAsString(Fields.MESSAGE_ID));
            } catch (NumberFormatException e) {
                throw new RuntimeException("ERROR :: ToIMFHandler >> Invalid MESSAGE_ID ::: " + e.getMessage());
            }

            BranchToIMFFormater convertor = (BranchToIMFFormater) AtlasModel.getInstance().getBean(beanName);
            CMCommand command = convertor.format(branchMsg);
            command.addHeaderParam(Fields.SERVICE_TYPE, msg.getAttributeAsString(CMMessage.SERVICE_TYPE));
            command.addHeaderParam(Fields.SESSION_ID, msg.getAttributeAsString(Fields.SESSION_ID));
            command.addHeaderParam(Fields.MESSAGE_ID, msgId + "");
            command.addParam(Fields.REQUEST_IS_ACCOUNT_BASED, msg.getAttributeAsString(Fields.REQUEST_IS_ACCOUNT_BASED));
            command.addParam(Fields.ISREVERSED, (Boolean)msg.getAttribute(Fields.ISREVERSED) ? "1" : "0");
            msg.setAttribute(CMMessage.REQUEST, command);

            
        } catch (Exception e) {
            log.error("convertor not found:" + e.getMessage());
            msg.setAttribute(Params.ACTION_CODE, ActionCode.UNSUPPORTED_MESSAGE);
            throw new CMFault(CMFault.FAULT_EXTERNAL_INVALID_MSG);
        }
    }
}
