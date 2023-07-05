package branch.dpi.atlas.service.cm.handler.cms;

import branch.dpi.atlas.service.cm.imf.cms.CMSToIMFFormater;
import dpi.atlas.model.AtlasModel;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;

import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: June 07, 2015
 * Time: 3:22:06 PM
 */
public class ToIMFHandler extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        String messageType = msg.getAttributeAsString(Fields.MESSAGE_TYPE);
        String beanName = "convertor" + messageType;
        int msgId;
        try {

            try {
                msgId = Integer.parseInt(msg.getAttributeAsString(Fields.MESSAGE_ID));
            } catch (NumberFormatException e) {
                throw new RuntimeException("ERROR :: ToIMFHandler >> Invalid MESSAGE_ID ::: " + e.getMessage());
            }

            CMSToIMFFormater convertor = (CMSToIMFFormater) AtlasModel.getInstance().getBean(beanName);
            CMCommand command = convertor.format(msg.getAttribute(CMMessage.COMMAND_OBJ));

            command.addHeaderParam(Fields.SERVICE_TYPE, msg.getAttributeAsString(CMMessage.SERVICE_TYPE));
            command.addHeaderParam(Fields.SESSION_ID, msg.getAttributeAsString(Fields.SESSION_ID));
            command.addHeaderParam(Fields.MESSAGE_ID, msgId + "");
            command.addParam(Fields.MN_RRN, msg.getAttributeAsString(Fields.RRN));
            command.addParam(Fields.BRANCH_CODE, msg.getAttributeAsString(Fields.BRANCH_CODE));

            msg.setAttribute(CMMessage.REQUEST, command);

        } catch (Exception e) {
            log.error("convertor not found:" + e.getMessage());
            msg.setAttribute(Params.ACTION_CODE, ActionCode.UNSUPPORTED_MESSAGE);
            throw new CMFault(CMFault.FAULT_EXTERNAL_INVALID_MSG);
        }
    }
}
