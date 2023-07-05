package branch.dpi.atlas.service.cm.handler.branch;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: F.Heydari
 * Date: sep 19 2021
 * Time: 4:23 PM
 */
public class changeBranchCodeDevice extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        log.debug("Inside changeBranchCodeDevice:process()");
        String deviceCode = "";
        String branchCode = "";
        String deviceType = branchMsg.getDeviceType().trim().substring(1);
        String newBranchCode = branchMsg.getAtmBranchCode();
        String sessionId = msg.getAttributeAsString(Fields.SESSION_ID);
        String userId = branchMsg.getUserId();
        String pin = Constants.CHANGE_DEVICE_CODE;

        try {
            deviceCode = ISOUtil.padleft(branchMsg.getDeviceCode().trim(), 8, '0');
        } catch (ISOException e) {
            log.error("Can not zeropad deviceCode = '" + deviceCode + "' in changeBranchCodeDevice : " + e.getMessage());
        }
        try {
            branchCode = ISOUtil.padleft(branchMsg.getBranchCodeBody().trim(), 5, '0');
        } catch (ISOException e) {
            log.error("Can not zeropad branchCode = '" + branchCode + "' in changeBranchCodeDevice : " + e.getMessage());
        }

        try {
            newBranchCode = ISOUtil.padleft(newBranchCode, 5, '0');
        } catch (ISOException e) {
            log.error("Can not zeropad branchCode = '" + branchCode + "' in changeBranchCodeDevice : " + e.getMessage());
        }

        try {

            String tbDeviceAccountNo = "";
            String deviceStatus = "";
            long accountBalance = 0L;
            String deviceCreationDate = "";
            String deviceCreationTime = "";
            String deviceBranchCode = "";
            String existDeviceType = "";
            String tbDeviceCode = "";

            String[] deviceInfo = ChannelFacadeNew.checkATM(deviceCode);
            tbDeviceAccountNo = deviceInfo[0];
            deviceStatus = deviceInfo[1];
            if (deviceInfo[2] != null) {
                accountBalance = Long.parseLong(deviceInfo[2]);
            }
            deviceCreationDate = deviceInfo[3];
            deviceCreationTime = deviceInfo[4];
            deviceBranchCode = deviceInfo[5];
            existDeviceType = deviceInfo[6];
            tbDeviceCode = deviceInfo[7];

            if (tbDeviceCode == null) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.DEVICE_NOT_FOUND);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DEVICE_NOT_FOUND));
            }
            else{
          if (branchCode.equals(deviceBranchCode) && deviceType.equals(existDeviceType) && deviceCode.equals(tbDeviceCode)) {
                ChannelFacadeNew.changeDeviceBranchCode(newBranchCode, deviceCode, tbDeviceAccountNo, branchCode,
                        deviceType, pin, userId, sessionId, accountBalance
                        , deviceCreationDate, deviceCreationTime, deviceStatus);
          } else {
                if (!deviceType.equals(existDeviceType)) {
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.DEVICE_TYPE_IS_DIFFERENT);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DEVICE_TYPE_IS_DIFFERENT));

                }
              else
              if (!branchCode.equals(deviceBranchCode)) {
                  msg.setAttribute(Fields.ACTION_CODE, ActionCode.DEVICE_BRANCH_CODE_IS_DIFFERENT);
                  throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DEVICE_BRANCH_CODE_IS_DIFFERENT));

              }
            }


        }} catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (CMFault e) {
            throw e;
        } catch (Exception e) {
            log.error("ERROR :::Inside changeBranchCodeDevice.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }


    }
}
