package branch.dpi.atlas.service.cm.handler.branch;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.AtlasModel;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import hit.db2.Db2DataSource;
import org.jpos.core.Configurable;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: F.Heydari
 * Date: August 28 2019
 * Time: 3:16 PM
 */
public class OfflineNacATM extends CMHandlerBase implements Configurable {
    String driver="";
    String SN10Ip="";
    String SN10User="";
    String SN10Pass="";
    String SN10Port="";
    String SN10rdbname="";
    String SN10Url="";

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        log.debug("Inside OfflineNacATM:process()");
        Db2DataSource SN10dataSource = null;
        SN10dataSource = (Db2DataSource) AtlasModel.getInstance().getBean("SN10Config");
        SN10Ip = SN10dataSource.getServerName();
        SN10User = SN10dataSource.getUser();
        SN10Pass =SN10dataSource.getPassword();
        SN10rdbname =SN10dataSource.getDatabaseName();
        SN10Port= String.valueOf(SN10dataSource.getPortNumber());
        driver="hit.db2.Db2Driver";

        String accountNo="";
        String branchCode="";
        String deviceCode="";
        String sessionId = msg.getAttributeAsString(Fields.SESSION_ID);
        String channelType = msg.getAttributeAsString(CMMessage.SERVICE_TYPE);
        String userId=branchMsg.getUserId();
        String pin= Constants.OFFLINE_DEVICE;
        String deviceType=branchMsg.getDeviceType().trim().substring(1);


        try {
            accountNo =  ISOUtil.padleft(branchMsg.getAccountNo().trim(), 13, '0');
        } catch (ISOException e) {
            log.error("Can not zeropad account number = '" + accountNo + "' in OfflineNacATM : " + e.getMessage());
        }
        try {
            deviceCode = ISOUtil.padleft(branchMsg.getDeviceCode().trim(), 8, '0');
        } catch (ISOException e) {
            log.error("Can not zeropad deviceCode = '" + deviceCode + "' in OfflineNacATM : " + e.getMessage());
        }
        try {
            branchCode = ISOUtil.padleft(branchMsg.getBranchCodeBody().trim(), 5, '0');
        } catch (ISOException e) {
            log.error("Can not zeropad branchCode = '" + branchCode + "' in OfflineNacATM : " + e.getMessage());
        }

        try {
            SN10Url="jdbc:db2://"+SN10Ip+":"+SN10Port+";rdbname="+SN10rdbname+";ccsid=1252;connection_timeout=200000";
            int result = ChannelFacadeNew.offlineNacATM(accountNo, branchCode,deviceCode,sessionId,channelType,pin,userId,deviceType
                    ,SN10Url,SN10Pass,SN10User,driver);
            switch (result) {
                case 0: //Approve
                    break;
                case 2: // invalid operation
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_OPERATION));
                case 4: //Account Not Found in customeraccount
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
                case 5: //deviceCode Not Found in tbdevice
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.DEVICE_NOT_FOUND);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DEVICE_NOT_FOUND));
                default:
                    log.error("ERROR:: invalid result");
                    throw new Exception();

            }

        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        }catch (CMFault e) {
            throw e;
        } catch (Exception e) {
            log.error("ERROR :::InsideOfflineATM.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }


    }
}

