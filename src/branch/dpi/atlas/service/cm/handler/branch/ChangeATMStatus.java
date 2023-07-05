package branch.dpi.atlas.service.cm.handler.branch;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.AtlasModel;
import dpi.atlas.model.NotFoundException;
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
 * Date: August 31 2019
 * Time: 4:23 PM
 */
public class ChangeATMStatus extends CMHandlerBase implements Configurable {

    String driver="";
    String finIp="";
    String finUser="";
    String finPass="";
    String finPort="";
    String finrdbname="";
    String SN10Ip="";
    String SN10User="";
    String SN10Pass="";
    String SN10Port="";
    String SN10rdbname="";
    String finUrl="";
    String SN10Url="";

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        log.debug("Inside ChangeATMStatus:process()");
        Db2DataSource SN10dataSource = null;
        SN10dataSource = (Db2DataSource) AtlasModel.getInstance().getBean("SN10Config");
        SN10Ip = SN10dataSource.getServerName();
        SN10User = SN10dataSource.getUser();
        SN10Pass =SN10dataSource.getPassword();
        SN10rdbname =SN10dataSource.getDatabaseName();
        SN10Port= String.valueOf(SN10dataSource.getPortNumber());

        Db2DataSource FINdataSource = null;
        FINdataSource = (Db2DataSource) AtlasModel.getInstance().getBean("FINConfig");
        finIp = FINdataSource.getServerName();
        finUser = FINdataSource.getUser();
        finPass = FINdataSource.getPassword();
        finrdbname = FINdataSource.getDatabaseName();
        finPort= String.valueOf(FINdataSource.getPortNumber());
        driver="hit.db2.Db2Driver";

        String accountNo="";
        String deviceCode="";
        String tbDeviceAccountNo = "";
        String deviceStatus = "";
        String branchCode="";
        long accountBalance=0L;
        String deviceCreationDate="";
        String deviceCreationTime="";
        String requestType=branchMsg.getRequestType();
        String sessionId = msg.getAttributeAsString(Fields.SESSION_ID);
        String pin="";
        String userId=branchMsg.getUserId();
        String deviceType=branchMsg.getDeviceType().trim().substring(1);
        String tbDeviceCode = "";
        String deviceBranchCode = "";

        finUrl="jdbc:db2://"+finIp+":"+finPort+";rdbname="+finrdbname+";ccsid=1252;connection_timeout=200000";
        SN10Url="jdbc:db2://"+SN10Ip+":"+SN10Port+";rdbname="+SN10rdbname+";ccsid=1252;connection_timeout=200000";

        try {
            accountNo =  ISOUtil.padleft(branchMsg.getAccountNo().trim(), 13, '0');
        } catch (ISOException e) {
            log.error("Can not zeropad account number = '" + accountNo + "' in ChangeATMStatus : " + e.getMessage());
        }
        try {
            deviceCode = ISOUtil.padleft(branchMsg.getDeviceCode().trim(), 8, '0');
        } catch (ISOException e) {
            log.error("Can not zeropad deviceCode = '" + deviceCode + "' in ChangeATMStatus : " + e.getMessage());
        }
        try {
            branchCode = ISOUtil.padleft(branchMsg.getBranchCodeBody().trim(), 5, '0');
        } catch (ISOException e) {
            log.error("Can not zeropad branchCode = '" + branchCode + "' in ChangeATMStatus : " + e.getMessage());
        }



        try {
            String[] deviceExist=ChannelFacadeNew.isExistAtm(accountNo,deviceCode,deviceType);
            tbDeviceAccountNo = deviceExist[0];
            deviceStatus = deviceExist[1];
            if(deviceExist[2]!=null){
                accountBalance= Long.parseLong(deviceExist[2]);
            }
            deviceCreationDate=deviceExist[3];
            deviceCreationTime=deviceExist[4];
            deviceBranchCode=deviceExist[5];

            if (requestType.equals(Constants.ATM_IN_ACTIVE_STATUS)) {
                pin=Constants.INACTIVE_DEVICE;
                if (tbDeviceAccountNo == null) {
                    throw new NotFoundException("Inside ChangeATMStatus--Can Not Find Device!!");
                } else {
                    if(branchCode.equals(deviceBranchCode))
                    {
                    ChannelFacadeNew.InActiveAtm(accountNo, deviceCode, branchCode, sessionId, pin, userId,accountBalance,deviceCreationDate,deviceCreationTime,deviceStatus,deviceType);
                    }else
                    {
                        branchMsg.setExistDeviceBranchCode(deviceBranchCode);
                        msg.setAttribute(Fields.ACTION_CODE, ActionCode.DEVICE_BRANCH_CODE_IS_DIFFERENT); //BRANCH IS DIFFERENT
                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DEVICE_BRANCH_CODE_IS_DIFFERENT));
                    }
                }
            } else if (requestType.equals(Constants.ATM_ACTIVE_STATUS)) {
                pin=Constants.ACTIVE_DEVICE;
                String[] deviceInfo = ChannelFacadeNew.checkATM(deviceCode);
                tbDeviceCode = deviceInfo[7];
                if (tbDeviceCode == null) {
                    String[] deviceHistoryInfo = ChannelFacadeNew.findDeviceInDeviceHistory(pin, deviceCode, accountNo, branchCode, deviceType);

                    String deviceHistoryaccountNo = deviceHistoryInfo[0];
                    String deviceHistorystatus =deviceHistoryInfo[1];
                    if(deviceHistorystatus!=null){
                        deviceHistorystatus.trim();
                        if(deviceHistorystatus.equals(""))
                        {
                            // device last status is online for first time(so status in tbdevicehistory is "" )
                            deviceHistorystatus="1";
                        }
                    }
                    String deviceHistorybalance = deviceHistoryInfo[2];
                    String deviceHistoryBranchCode = deviceHistoryInfo[3];
                    String deviceHistorydeviceType = deviceHistoryInfo[4];

                  if(deviceHistoryaccountNo!=null ||deviceHistoryaccountNo!="")
                  {
                      if(branchCode.equals(deviceHistoryBranchCode))
                      {
                    ChannelFacadeNew.activeDevice(pin, deviceHistoryBranchCode, deviceHistoryaccountNo, deviceCode, deviceHistorydeviceType, Long.parseLong(deviceHistorybalance), deviceHistorystatus, sessionId, userId);
                      }
                      else{
                          branchMsg.setExistDeviceBranchCode(deviceHistoryBranchCode);
                          msg.setAttribute(Fields.ACTION_CODE, ActionCode.DEVICE_BRANCH_CODE_IS_DIFFERENT);
                          throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DEVICE_BRANCH_CODE_IS_DIFFERENT));
                      }
                  }
                    else{
                      msg.setAttribute(Fields.ACTION_CODE, ActionCode.DEVICE_NOT_FOUND);
                      throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DEVICE_NOT_FOUND));
                  }
                } else {
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.DEVICE_IS_ACTIVE);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DEVICE_IS_ACTIVE));
                }
            }
        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DEVICE_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DEVICE_NOT_FOUND));

        }
        catch (SQLException e) {
            log.error("ERROR :::Inside ChangeATMStatus.doProcess(): " + e.getMessage());
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        }
         catch (CMFault e) {
            throw e;
        } catch (Exception e) {
            log.error("ERROR :::Inside ChangeATMStatus.doProcess(): " + e.getMessage());
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }


    }
}
