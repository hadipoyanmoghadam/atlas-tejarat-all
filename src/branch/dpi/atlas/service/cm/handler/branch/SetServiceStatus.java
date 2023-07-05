package branch.dpi.atlas.service.cm.handler.branch;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.facade.cm.ServerAuthenticationException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import java.sql.SQLException;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Sep 17 2014
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class SetServiceStatus extends TJServiceHandler implements Configurable {
    public void doProcess(CMMessage msg, Map map) throws CMFault {
        BranchMessage bm = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        try {
            String serviceStatus;
            if (bm.getRequestType().equals("1"))   //active
                serviceStatus = "1";
            else if (bm.getRequestType().equals("2"))  //inactive
                serviceStatus = "0";
            else {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.UNSUPPORTED_MESSAGE);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.UNSUPPORTED_MESSAGE));
            }
            String sessionId = String.valueOf(msg.getAttribute(Fields.SESSION_ID));
            String channelType=msg.getAttributeAsString(CMMessage.SERVICE_TYPE);
            String description=bm.getDocumentDescription();
            if(description!=null && !description.equalsIgnoreCase(""))
                description=description.trim();

            if(channelType.equals(Constants.BRANCH_SERVICE)){
                channelType= Constants.CHN_USER_BRANCH;
                description="";
            }

            ChannelFacadeNew.updateServiceStatus(bm.getAccountNo(), sessionId, bm.getTerminalId(),
                    bm.getUserId(), bm.getBranchCode(), serviceStatus,channelType,description);
            if(channelType.equals(Constants.SIMIN_SERVICE)&& bm.getRequestType().equals("2")){  //send sms for inactive SIMIN
            ChannelFacadeNew.insertTbmsgtrn(bm.getAccountNo(),channelType);
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
