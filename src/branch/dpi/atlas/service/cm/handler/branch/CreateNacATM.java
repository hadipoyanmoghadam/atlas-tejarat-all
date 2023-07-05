package branch.dpi.atlas.service.cm.handler.branch;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.AtlasModel;
import dpi.atlas.model.ModelException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
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
 * Time: 2:11 PM
 */
public class CreateNacATM extends CMHandlerBase implements Configurable {
    String driver="";
    String SN10Ip="";
    String SN10User="";
    String SN10Pass="";
    String SN10Port="";
    String SN10rdbname="";
    String SN10Url="";

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        log.debug("Inside CreateNacATM:process()");
        Db2DataSource SN10dataSource = null;
        SN10dataSource = (Db2DataSource) AtlasModel.getInstance().getBean("SN10Config");
        SN10Ip = SN10dataSource.getServerName();
        SN10User = SN10dataSource.getUser();
        SN10Pass =SN10dataSource.getPassword();
        SN10rdbname =SN10dataSource.getDatabaseName();
        SN10Port= String.valueOf(SN10dataSource.getPortNumber());

        driver="hit.db2.Db2Driver";
        String accountNo = "";
        String deviceCode = "";
        String branchCode = "";
        String tbDeviceAccountNo = "";
        String deviceStatus = "";
        long accountBalance = 0L;
        String deviceCreationDate = "";
        String deviceCreationTime = "";
        String deviceBranchCode = "";

        String sessionId = msg.getAttributeAsString(Fields.SESSION_ID);
        String channelType = msg.getAttributeAsString(CMMessage.SERVICE_TYPE);
        String userId = branchMsg.getUserId();
        String histCode="";
        String deviceType = branchMsg.getDeviceType().trim().substring(1);
        String existDeviceType = "";
        String tbDeviceCode = "";
        Boolean hasDevice = false;
        String requestAccountType = branchMsg.getAtmAccountType();
        SN10Url="jdbc:db2://"+SN10Ip+":"+SN10Port+";rdbname="+SN10rdbname+";ccsid=1252;connection_timeout=200000";

        try {
            accountNo =  ISOUtil.padleft(branchMsg.getAccountNo().trim(), 13, '0');
        } catch (ISOException e) {
            log.error("Can not zeropad account number = '" + accountNo + "' in CreateNacATM : " + e.getMessage());
        }
        try {
            deviceCode = ISOUtil.padleft(branchMsg.getDeviceCode().trim(), 8, '0');
        } catch (ISOException e) {
            log.error("Can not zeropad deviceCode = '" + deviceCode + "' in CreateNacATM : " + e.getMessage());
        }
        try {
            branchCode = ISOUtil.padleft(branchMsg.getBranchCodeBody().trim(), 5, '0');
        } catch (ISOException e) {
            log.error("Can not zeropad branchCode = '" + branchCode + "' in CreateNacATM : " + e.getMessage());
        }

        try {
            String[] deviceInfo = ChannelFacadeNew.checkATM(deviceCode);
            tbDeviceAccountNo = deviceInfo[0];
            deviceStatus = deviceInfo[1];
            if(deviceStatus==null){deviceStatus="";}
            if (deviceInfo[2] != null) {
                accountBalance = Long.parseLong(deviceInfo[2]);
            }
            deviceCreationDate = deviceInfo[3];
            deviceCreationTime = deviceInfo[4];
            deviceBranchCode = deviceInfo[5];
            existDeviceType = deviceInfo[6];
            tbDeviceCode = deviceInfo[7];

            if (deviceType.equals(Constants.ATM_TYPE)) {
                if (tbDeviceCode != null) { //darim

                    if (accountNo.equals(tbDeviceAccountNo) && branchCode.equals(deviceBranchCode) && deviceType.equals(existDeviceType) && deviceCode.equals(tbDeviceCode)) {
                        if (requestAccountType.equals(Constants.DEVICE_ONLINE_ACC_REQUEST)) { // wants with online acc
                            histCode=Constants.CREATE_DEVICE_ONLINE;
                            if (deviceStatus.equals(Constants.ONLINE_ATM_STATUS)) {
                                msg.setAttribute(Fields.ACTION_CODE, ActionCode.DEVICE_HAS_BEEN_ONLINED_BEFORE);
                                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DEVICE_HAS_BEEN_ONLINED_BEFORE));
                            } else if (deviceStatus.equals(Constants.OFFLINE_ATM_STATUS)) {
                                String[] accountInfo = ChannelFacadeNew.findAccountNo(accountNo);
                                String account_no = accountInfo[0];
                                String lockStatus = accountInfo[1];
                                if (account_no != null) //hesab ro darim
                                {
                                    if (lockStatus.equals("1")) {
                                        msg.setAttribute(Fields.ACTION_CODE, ActionCode.DEVICE_HAS_BEEN_ONLINED_BEFORE);
                                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DEVICE_HAS_BEEN_ONLINED_BEFORE));
                                    } else if (lockStatus.equals("9")) {
                                        hasDevice = true;
                                        ChannelFacadeNew.createDeviceATM(accountNo, deviceCode, branchCode, sessionId, channelType, histCode, userId, deviceType
                                                , SN10Url, SN10Pass, SN10User, driver, lockStatus, hasDevice, requestAccountType,deviceStatus);
                                    }
                                } else { //hesab ro nadarim
                                    hasDevice = true;
                                    lockStatus = Constants.NO_LOCKSTATUS_FLAG;
                                    ChannelFacadeNew.createDeviceATM(accountNo, deviceCode, branchCode, sessionId, channelType, histCode, userId, deviceType
                                            , SN10Url, SN10Pass, SN10User, driver, lockStatus, hasDevice, requestAccountType,deviceStatus);
                                }
                            }
                        } else if (requestAccountType.equals(Constants.DEVICE_OFFLINE_ACC_REQUEST)) {
                            histCode=Constants.CREATE_DEVICE_OFFLINE;
                            if (deviceStatus.equals(Constants.ONLINE_ATM_STATUS)) {
                                msg.setAttribute(Fields.ACTION_CODE, ActionCode.DEVICE_HAS_BEEN_ONLINED_BEFORE);
                                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DEVICE_HAS_BEEN_ONLINED_BEFORE));
                            } else if (deviceStatus.equals(Constants.OFFLINE_ATM_STATUS)) {

                                String[] accountInfo = ChannelFacadeNew.findAccountNo(accountNo);
                                String account_no = accountInfo[0];
                                String lockStatus = accountInfo[1];
                                if (account_no != null) {
                                    if (lockStatus.equals("1")) {
                                        msg.setAttribute(Fields.ACTION_CODE, ActionCode.DEVICE_HAS_BEEN_ONLINED_BEFORE);
                                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DEVICE_HAS_BEEN_ONLINED_BEFORE));
                                    } else if (lockStatus.equals("9")) {
                                        hasDevice = true;
                                        ChannelFacadeNew.createDeviceATM(accountNo, deviceCode, branchCode, sessionId, channelType, histCode, userId, deviceType
                                                , SN10Url, SN10Pass, SN10User, driver, lockStatus, hasDevice, requestAccountType,deviceStatus);
                                    }
                                } else { //hesab ro nadarim
                                    hasDevice = true;
                                    lockStatus = Constants.NO_LOCKSTATUS_FLAG;
                                    ChannelFacadeNew.createDeviceATM(accountNo, deviceCode, branchCode, sessionId, channelType, histCode, userId, deviceType
                                            , SN10Url, SN10Pass, SN10User, driver, lockStatus, hasDevice, requestAccountType,deviceStatus);
                                }
                            }

                        }
                    } else { //if  req values != tbdevice values
                        if (accountNo.equals(tbDeviceAccountNo) && branchCode.equals(deviceBranchCode) && !deviceType.equals(existDeviceType)) {
                            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DEVICE_TYPE_IS_DIFFERENT);
                            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DEVICE_TYPE_IS_DIFFERENT));

                        } else if (!accountNo.equals(tbDeviceAccountNo) || !branchCode.equals(deviceBranchCode)) {
                            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DEVICE_IS_ONLINE_WITH_ANOTHER_ACCOUNT);
                            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DEVICE_IS_ONLINE_WITH_ANOTHER_ACCOUNT));

                        }


                    }

                } else { //not exist deviceCode in tbdevice
                    String[] accountInfo = ChannelFacadeNew.findAccountNo(accountNo);
                    String account_no = accountInfo[0];
                    String lockStatus = accountInfo[1];


                    if (account_no != null) //hesab darim
                    {
                        if (requestAccountType.equals(Constants.DEVICE_ONLINE_ACC_REQUEST)) {
                            histCode=Constants.CREATE_DEVICE_ONLINE;
                            if (lockStatus.equals("1")) {
                                msg.setAttribute(Fields.ACTION_CODE, ActionCode.ATM_INVALID_OPERATION);
                                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ATM_INVALID_OPERATION));
                            } else if (lockStatus.equals("9")) {
                                hasDevice = false;
                                ChannelFacadeNew.createDeviceATM(accountNo, deviceCode, branchCode, sessionId, channelType, histCode, userId, deviceType
                                        , SN10Url, SN10Pass, SN10User, driver, lockStatus, hasDevice, requestAccountType,deviceStatus);
                            }
                        } else if (requestAccountType.equals(Constants.DEVICE_OFFLINE_ACC_REQUEST)) {
                            histCode=Constants.CREATE_DEVICE_OFFLINE;
                            if (lockStatus.equals("1")) {
                                msg.setAttribute(Fields.ACTION_CODE, ActionCode.ATM_INVALID_OPERATION);
                                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ATM_INVALID_OPERATION));
                            } else if (lockStatus.equals("9")) {
                                hasDevice = false;
                                ChannelFacadeNew.createDeviceATM(accountNo, deviceCode, branchCode, sessionId, channelType, histCode, userId, deviceType
                                        , SN10Url, SN10Pass, SN10User, driver, lockStatus, hasDevice, requestAccountType,deviceStatus);
                            }
                        }


                    } else {//hesab nadarim
                        lockStatus=Constants.NO_LOCKSTATUS_FLAG;
                        if (requestAccountType.equals(Constants.DEVICE_ONLINE_ACC_REQUEST)) {
                            histCode=Constants.CREATE_DEVICE_ONLINE;
                            hasDevice = false;
                            ChannelFacadeNew.createDeviceATM(accountNo, deviceCode, branchCode, sessionId, channelType, histCode, userId, deviceType
                                    , SN10Url, SN10Pass, SN10User, driver, lockStatus, hasDevice, requestAccountType,deviceStatus);
                        } else if (requestAccountType.equals(Constants.DEVICE_OFFLINE_ACC_REQUEST)) {
                            histCode=Constants.CREATE_DEVICE_OFFLINE;
                            hasDevice = false;

                            ChannelFacadeNew.createDeviceATM(accountNo, deviceCode, branchCode, sessionId, channelType, histCode, userId, deviceType
                                    , SN10Url, SN10Pass, SN10User, driver, lockStatus, hasDevice, requestAccountType,deviceStatus);
                        }

                    }


                }
            } else if (deviceType.equals(Constants.PINPAD_TYPE)) {
                histCode=Constants.CREATE_DEVICE_OFFLINE;
                if (tbDeviceCode != null) {
                    if( requestAccountType.equals(Constants.DEVICE_OFFLINE_ACC_REQUEST)){
                    if (deviceStatus.equals(Constants.ONLINE_ATM_STATUS)) {
                        if (accountNo.equals(tbDeviceAccountNo) && branchCode.equals(deviceBranchCode) && !deviceType.equals(existDeviceType)) {
                            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DEVICE_TYPE_IS_DIFFERENT);
                            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DEVICE_TYPE_IS_DIFFERENT));

                        } else if (accountNo.equals(tbDeviceAccountNo) && branchCode.equals(deviceBranchCode) && deviceType.equals(existDeviceType)) {

                            ChannelFacadeNew.changePinpadStatus(accountNo, deviceCode, branchCode, accountBalance,
                                    sessionId, channelType, histCode, userId, deviceCreationDate, deviceCreationTime, deviceStatus
                                    , deviceType);

                        } else if (!accountNo.equals(tbDeviceAccountNo) || !branchCode.equals(deviceBranchCode)) {
                            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DEVICE_IS_ONLINE_WITH_ANOTHER_ACCOUNT);
                            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DEVICE_IS_ONLINE_WITH_ANOTHER_ACCOUNT));


                        }
                    } else if (deviceStatus.equals(Constants.OFFLINE_ATM_STATUS)) {
                        if (!accountNo.equals(tbDeviceAccountNo) || !branchCode.equals(deviceBranchCode)) {
                            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DEVICE_ACCOUNTNO_IS_DIFFERENT);
                            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DEVICE_ACCOUNTNO_IS_DIFFERENT));

                        } else if (!deviceType.equals(existDeviceType)) {
                            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DEVICE_TYPE_IS_DIFFERENT);
                            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DEVICE_TYPE_IS_DIFFERENT));

                        } else if (accountNo.equals(tbDeviceAccountNo) && branchCode.equals(deviceBranchCode) && deviceType.equals(existDeviceType)) {

                        //EXP: pinpad define before  with offline mode
                            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DEVICE_IS_ACTIVE);
                            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DEVICE_IS_ACTIVE));
                        }
                    }
                } else {  //for O or other types
                        msg.setAttribute(Fields.ACTION_CODE, ActionCode.ATM_INVALID_OPERATION);
                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ATM_INVALID_OPERATION));
                    }

                } else {
                   if( requestAccountType.equals(Constants.DEVICE_OFFLINE_ACC_REQUEST)){
                    //insert in to tbdevice with offline mode
                    ChannelFacadeNew.createDevicePinpad(accountNo, deviceCode, branchCode, accountBalance,
                            sessionId, channelType, histCode, userId, deviceStatus);
                   }
                    else{
                       msg.setAttribute(Fields.ACTION_CODE, ActionCode.ATM_INVALID_OPERATION);
                       throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ATM_INVALID_OPERATION));
                   }
                }

            }

        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (ModelException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.ATM_INVALID_OPERATION);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ATM_INVALID_OPERATION));
        } catch (CMFault e) {
            throw e;
        } catch (Exception e) {
            log.error("ERROR :::Inside CreateNACATM.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }
}


