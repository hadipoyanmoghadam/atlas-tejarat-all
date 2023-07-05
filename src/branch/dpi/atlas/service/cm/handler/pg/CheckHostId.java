package branch.dpi.atlas.service.cm.handler.pg;

import dpi.atlas.model.ModelException;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.util.Constants;
import sun.awt.ModalExclude;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: April 20, 2020
 * Time: 2:46 PM
 */
public class CheckHostId extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        try {
            CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
            String srcAccountNo = msg.getAttributeAsString(Fields.SRC_ACCOUNT);
            String destAccountNo = msg.getAttributeAsString(Fields.DEST_ACCOUNT);

            String srcHostId = ChannelFacadeNew.findAccountHost(srcAccountNo);
            String destHostId = ChannelFacadeNew.findAccountHost(destAccountNo);

            if (srcHostId.equals(Constants.HOST_ID_SGB) || destHostId.equals(Constants.HOST_ID_SGB)){
                //Invalid operation
                throw new ModelException();
            }

            msg.setAttribute(Fields.TRANSACTION_TYPE, srcHostId+destHostId);
            msg.setAttribute(Fields.HOST_ID, srcHostId);
            command.addParam(Constants.SRC_HOST_ID, srcHostId);
            msg.setAttribute(Constants.SRC_HOST_ID, srcHostId);
            command.addHeaderParam(Fields.SRC_ACCOUNT, srcAccountNo);
            command.addParam(Constants.DEST_HOST_ID, destHostId);
            msg.setAttribute(Constants.DEST_HOST_ID, destHostId);
            command.addHeaderParam(Fields.DEST_ACCOUNT, destAccountNo);

        } catch (ModelException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_OPERATION));
        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.CUSTOMER_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CUSTOMER_NOT_FOUND));
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));

        } catch (Exception e) {
            log.error("ERROR :::Inside HostIdFinderByAccountNo.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}
