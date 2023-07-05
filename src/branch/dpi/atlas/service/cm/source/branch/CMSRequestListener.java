package branch.dpi.atlas.service.cm.source.branch;

import branch.dpi.atlas.model.tj.entity.cms.RevokeReq;
import branch.dpi.atlas.service.cm.handler.cms.accountInfo.ACCOUNTINFOType;
import branch.dpi.atlas.service.cm.handler.cms.balance.BALANCEType;
import branch.dpi.atlas.service.cm.handler.cms.cancellation.CANCELLATIONType;
import branch.dpi.atlas.service.cm.handler.cms.cardInfo.CardInfoReq;
import branch.dpi.atlas.service.cm.handler.cms.childCard.ChildCardReq;
import branch.dpi.atlas.service.cm.handler.cms.customer.CUSTOMERType;
import branch.dpi.atlas.service.cm.handler.cms.giftBalance.GiftBalanceReq;
import branch.dpi.atlas.service.cm.handler.cms.giftCharge.GIFTCHARGEType;
import branch.dpi.atlas.service.cm.handler.cms.giftDeposit.GiftDepositReq;
import branch.dpi.atlas.service.cm.handler.cms.giftDisCharge.GIFTDISCHARGEType;
import branch.dpi.atlas.service.cm.handler.cms.jointAccountInfo.JointAccountInfoReq;
import branch.dpi.atlas.service.cm.handler.cms.reCreate.RECREATEType;
import branch.dpi.atlas.service.cm.handler.cms.touristCard.TouristCardReq;
import branch.dpi.atlas.service.cm.source.branch.socket.BranchSource;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.source.CMSource;
import dpi.atlas.service.cm.source.sparrow.message.FormatException;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import branch.dpi.atlas.service.cm.handler.cms.jointAccountInfo.JointAccountInfoReq;
import branch.dpi.atlas.service.cm.handler.cms.jointCard.JointCardReq;
import branch.dpi.atlas.service.cm.handler.cms.createBatch.CreateGroupCardReq;
import branch.dpi.atlas.service.cm.handler.cms.groupCharge.*;

/**
 * User: R.Nasiri
 * Update User: SH.Behnaz
 * Date: Jan 7, 2015
 * Time: 11:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class CMSRequestListener implements BranchRequestListener {
    private static Log log = LogFactory.getLog(CMSRequestListener.class);

    private CMHandler cmHandler;

    public CMSRequestListener() {
    }

    public boolean process(BranchSource source, String msg_str) {
        if (log.isInfoEnabled()) log.info("Inside CMSRequestListener::process()");
        if (log.isInfoEnabled()) log.info("req len:" + msg_str.length());
        if (log.isInfoEnabled()) log.info("Incoming CMSMessage:" + msg_str);

        CMMessage msg = new CMMessage();
        HashMap holder = new HashMap();
        try {

            msg_str = msg_str.replaceAll("'", " ");
            if (log.isInfoEnabled()) log.info("********message arrived : " + msg_str);

            processMessage(msg, msg_str, source);
            getChainHandler().process(msg, holder);

        } catch (CMFault fault) {
            if (log.isInfoEnabled()) log.info("faultCode:" + fault.getFaultCode());
            CMSource cmSource = getChainHandler().getCMSource();
            CMHandler faultHandler = cmSource.getFaultHandler(fault.getFaultCode());

            if (log.isInfoEnabled()) log.info("FaultHandler : " + faultHandler.getClass().getName());
            try {
                msg.setAttribute(CMMessage.FAULT, fault);
                msg.setAttribute(CMMessage.FAULT_CODE, fault.getFaultCode());
                faultHandler.process(msg, holder);
            } catch (CMFault cmFault) {
                //TODO: handle Exception
                log.error(cmFault);
                try {
                    source.sendBranch(msg_str);
                } catch (IOException e) {
                    log.error("Can not send response to Client: " + e);
                }
            }

        } catch (Exception e) {
            //TODO: handle Exception
            e.printStackTrace();
            log.error(e);
            try {
                source.sendBranch(msg_str);
            } catch (IOException e1) {
                log.error("Can not send response to Client: " + e1);
            }
        }
        return true;
    }

    private void processMessage(CMMessage msg, String msg_str, BranchSource source) {
        try {
            if (msg_str.contains(Constants.POLICY_ELEMENT)) {
                parsePolicyMessages(msg, msg_str);

            } else if (msg_str.contains(Constants.CUSTOMER_ELEMENT)) {
                JAXBContext context = JAXBContext.newInstance("branch.dpi.atlas.service.cm.handler.cms.customer", CUSTOMERType.class.getClassLoader());
                StringReader stringReader = new StringReader(msg_str);
                Unmarshaller u = context.createUnmarshaller();
                CUSTOMERType customerType = (CUSTOMERType) u.unmarshal(stringReader);
                log.info("PARSED");
                msg.setAttribute(CMMessage.COMMAND_OBJ, customerType);
                msg.setAttribute(Fields.RRN, customerType.getRrn().trim());
                msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.CUSTOMER_ELEMENT);

                if (customerType.getRecordflag().equals(Constants.CREATE_CARD_RECORDFLAG))
                     msg.setAttribute(Fields.MESSAGE_TYPE, Constants.CREATE_MSG_TYPE);
                else if (customerType.getRecordflag().equals(Constants.CONNECTIVITY_RECORDFLAG))
                    msg.setAttribute(Fields.MESSAGE_TYPE, Constants.UPDATE_MSG_TYPE);
                else if (customerType.getRecordflag().equals(Constants.SEPARAT_RECORDFLAG))
                    msg.setAttribute(Fields.MESSAGE_TYPE, Constants.SEPARAT_MSG_TYPE);
                else
                    throw new FormatException("Record flag is invalid");

                msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
                msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                msg.setAttribute(Fields.PAN, customerType.getCardno().trim());
                msg.setAttribute(Fields.ACCOUNT_NO, customerType.getAccountno().trim());

            } else if (msg_str.contains(Constants.RE_CREATE_ELEMENT)) {
                JAXBContext context = JAXBContext.newInstance("branch.dpi.atlas.service.cm.handler.cms.reCreate", RECREATEType.class.getClassLoader());
                StringReader stringReader = new StringReader(msg_str);
                Unmarshaller u = context.createUnmarshaller();
                RECREATEType recreateType = (RECREATEType) u.unmarshal(stringReader);
                log.info("PARSED");
                msg.setAttribute(CMMessage.COMMAND_OBJ, recreateType);
                msg.setAttribute(Fields.RRN, recreateType.getRrn().trim());
                msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.RE_CREATE_ELEMENT);
                msg.setAttribute(Fields.MESSAGE_TYPE, Constants.RECREATE_MSG_TYPE);
                msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
                msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                msg.setAttribute(Fields.PAN, recreateType.getCardno().trim());
                msg.setAttribute(Fields.ACCOUNT_NO, "");

            } else if (msg_str.contains(Constants.JOINTACCOUNTINFO_ELEMENT)) {
                JAXBContext context = JAXBContext.newInstance(JointAccountInfoReq.class);
                StringReader stringReader = new StringReader(msg_str);
                Unmarshaller u = context.createUnmarshaller();
                JointAccountInfoReq accountInfoReq = (JointAccountInfoReq) u.unmarshal(stringReader);
                log.info("PARSED");
                msg.setAttribute(CMMessage.COMMAND_OBJ, accountInfoReq);
                msg.setAttribute(Fields.RRN, accountInfoReq.getRrn().trim());
                msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.JOINTACCOUNTINFO_ELEMENT);
                msg.setAttribute(Fields.MESSAGE_TYPE, Constants.JOINT_ACCOUNTINFO_MSG_TYPE);
                msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
                msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                msg.setAttribute(Fields.PAN, "");
                msg.setAttribute(Fields.ACCOUNT_NO, accountInfoReq.getAccountno().trim());
            } else if (msg_str.contains(Constants.ACCOUNTINFO_ELEMENT)) {
                JAXBContext context = JAXBContext.newInstance("branch.dpi.atlas.service.cm.handler.cms.accountInfo", ACCOUNTINFOType.class.getClassLoader());
                StringReader stringReader = new StringReader(msg_str);
                Unmarshaller u = context.createUnmarshaller();
                ACCOUNTINFOType accountinfoType = (ACCOUNTINFOType) u.unmarshal(stringReader);
                log.info("PARSED");
                msg.setAttribute(CMMessage.COMMAND_OBJ, accountinfoType);
                msg.setAttribute(Fields.RRN, accountinfoType.getRrn().trim());
                msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.ACCOUNTINFO_ELEMENT);
                msg.setAttribute(Fields.MESSAGE_TYPE, Constants.ACCOUNTINFO_MSG_TYPE);
                msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
                msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                msg.setAttribute(Fields.PAN, "");
                msg.setAttribute(Fields.ACCOUNT_NO, accountinfoType.getAccountno().trim());

            } else if (msg_str.contains(Constants.CARDINFO_ELEMENT)) {
                JAXBContext context = JAXBContext.newInstance(CardInfoReq.class);
                StringReader stringReader = new StringReader(msg_str);
                Unmarshaller u = context.createUnmarshaller();
                CardInfoReq cardinfoType = (CardInfoReq) u.unmarshal(stringReader);
                log.info("PARSED");
                msg.setAttribute(CMMessage.COMMAND_OBJ, cardinfoType);
                msg.setAttribute(Fields.RRN, cardinfoType.getRrn().trim());
                msg.setAttribute(Fields.PAN, cardinfoType.getCardno().trim());
                msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.CARDINFO_ELEMENT);
                msg.setAttribute(Fields.MESSAGE_TYPE, Constants.CARDINFO_MSG_TYPE);
                msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
                msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                msg.setAttribute(Fields.ACCOUNT_NO, "");
            } else if (msg_str.contains(Constants.REVOKE_ELEMENT)) {
                JAXBContext context = JAXBContext.newInstance(RevokeReq.class);
                StringReader stringReader = new StringReader(msg_str);
                Unmarshaller u = context.createUnmarshaller();
                RevokeReq revokeType = (RevokeReq) u.unmarshal(stringReader);

                log.info("PARSED");
                msg.setAttribute(CMMessage.COMMAND_OBJ, revokeType);
                msg.setAttribute(Fields.RRN, revokeType.getRrn().trim());
                msg.setAttribute(Fields.PAN, revokeType.getCardno().trim());
                msg.setAttribute(Fields.ROW, revokeType.getRow().trim());
                msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.REVOKE_ELEMENT);
                msg.setAttribute(Fields.MESSAGE_TYPE, Constants.REVOKE_MSG_TYPE);
                msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
                msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                msg.setAttribute(Fields.ACCOUNT_NO, "");
            } else if (msg_str.contains(Constants.BALANCE_GIFTCARD_ELEMENT)) {
                JAXBContext context = JAXBContext.newInstance(GiftBalanceReq.class);
                StringReader stringReader = new StringReader(msg_str);
                Unmarshaller u = context.createUnmarshaller();
                GiftBalanceReq giftBalanceType = (GiftBalanceReq) u.unmarshal(stringReader);
                log.info("PARSED");
                msg.setAttribute(CMMessage.COMMAND_OBJ, giftBalanceType);
                msg.setAttribute(Fields.RRN, giftBalanceType.getRrn().trim());
                msg.setAttribute(Fields.BRANCH_CODE, giftBalanceType.getBranchCode().trim());
                msg.setAttribute(Fields.ACCOUNT_NO, "");
                msg.setAttribute(Fields.PAN, "");
                msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.BALANCE_GIFTCARD_ELEMENT);
                msg.setAttribute(Fields.MESSAGE_TYPE, Constants.BALANCE_GIFTCARD_MSG_TYPE);
                msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
                msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
            } else if (msg_str.contains(Constants.BALANCE_ELEMENT)) {
                JAXBContext context = JAXBContext.newInstance("branch.dpi.atlas.service.cm.handler.cms.balance", BALANCEType.class.getClassLoader());
                StringReader stringReader = new StringReader(msg_str);
                Unmarshaller u = context.createUnmarshaller();
                BALANCEType balanceType = (BALANCEType) u.unmarshal(stringReader);
                log.info("PARSED");
                msg.setAttribute(CMMessage.COMMAND_OBJ, balanceType);
                msg.setAttribute(Fields.RRN, balanceType.getRrn().trim());
                msg.setAttribute(Fields.BRANCH_CODE, balanceType.getBranchCode().trim());
                msg.setAttribute(Fields.PAN, "");
                msg.setAttribute(Fields.ACCOUNT_NO, "");
                msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.BALANCE_ELEMENT);
                msg.setAttribute(Fields.MESSAGE_TYPE, Constants.BALANCE_MSG_TYPE);
                msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
                msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
            } else if (msg_str.contains(Constants.DISCHARGE_GIFTCARD_ELEMENT)) {
                JAXBContext context = JAXBContext.newInstance("branch.dpi.atlas.service.cm.handler.cms.giftDisCharge", GIFTDISCHARGEType.class.getClassLoader());
                StringReader stringReader = new StringReader(msg_str);
                Unmarshaller u = context.createUnmarshaller();
                GIFTDISCHARGEType giftDisChargeType = (GIFTDISCHARGEType) u.unmarshal(stringReader);
                log.info("PARSED");
                msg.setAttribute(CMMessage.COMMAND_OBJ, giftDisChargeType);
                msg.setAttribute(Fields.RRN, giftDisChargeType.getRrn().trim());
                msg.setAttribute(Fields.BRANCH_CODE, giftDisChargeType.getBranchCode().trim());
                msg.setAttribute(Fields.PAN, giftDisChargeType.getCardno());
                msg.setAttribute(Fields.ACCOUNT_NO, "");
                msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.DISCHARGE_GIFTCARD_ELEMENT);
                msg.setAttribute(Fields.MESSAGE_TYPE, Constants.DISCHARGE_GIFTCARD_MSG_TYPE);
                msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
                msg.setAttribute(CMMessage.REQUEST_STR, msg_str);

            } else if (msg_str.contains(Constants.CHARGE_GIFTCARD_ELEMENT)) {
                JAXBContext context = JAXBContext.newInstance("branch.dpi.atlas.service.cm.handler.cms.giftCharge", GIFTCHARGEType.class.getClassLoader());
                StringReader stringReader = new StringReader(msg_str);
                Unmarshaller u = context.createUnmarshaller();
                GIFTCHARGEType giftChargeType = (GIFTCHARGEType) u.unmarshal(stringReader);
                log.info("PARSED");
                msg.setAttribute(CMMessage.COMMAND_OBJ, giftChargeType);
                msg.setAttribute(Fields.RRN, giftChargeType.getRrn().trim());
                msg.setAttribute(Fields.BRANCH_CODE, giftChargeType.getBranchCode().trim());
                msg.setAttribute(Fields.PAN, giftChargeType.getCardno());
                msg.setAttribute(Fields.ACCOUNT_NO, "");
                msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.CHARGE_GIFTCARD_ELEMENT);
                msg.setAttribute(Fields.MESSAGE_TYPE, Constants.CHARGE_GIFTCARD_MSG_TYPE);
                msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
                msg.setAttribute(CMMessage.REQUEST_STR, msg_str);

            } else if (msg_str.contains(Constants.CANCELLATION_ELEMENT)) {
                JAXBContext context = JAXBContext.newInstance("branch.dpi.atlas.service.cm.handler.cms.cancellation", CANCELLATIONType.class.getClassLoader());
                StringReader stringReader = new StringReader(msg_str);
                Unmarshaller u = context.createUnmarshaller();
                CANCELLATIONType cancellationType = (CANCELLATIONType) u.unmarshal(stringReader);
                log.info("PARSED");
                msg.setAttribute(CMMessage.COMMAND_OBJ, cancellationType);
                msg.setAttribute(Fields.RRN, cancellationType.getRrn().trim());
                msg.setAttribute(Fields.BRANCH_CODE, cancellationType.getBranchCode().trim());
                msg.setAttribute(Fields.PAN, "");
                msg.setAttribute(Fields.ACCOUNT_NO, "");
                msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.CANCELLATION_ELEMENT);
                msg.setAttribute(Fields.MESSAGE_TYPE, Constants.CANCELLATION_MSG_TYPE);
                msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
                msg.setAttribute(CMMessage.REQUEST_STR, msg_str);

            } else if (msg_str.contains(Constants.DEPOSIT_GIFTCARD_ELEMENT)) {
                JAXBContext context = JAXBContext.newInstance(GiftDepositReq.class);
                StringReader stringReader = new StringReader(msg_str);
                Unmarshaller u = context.createUnmarshaller();
                GiftDepositReq giftDepositType = (GiftDepositReq) u.unmarshal(stringReader);
                log.info("PARSED");
                msg.setAttribute(CMMessage.COMMAND_OBJ, giftDepositType);
                msg.setAttribute(Fields.RRN, giftDepositType.getRrn().trim());
                msg.setAttribute(Fields.BRANCH_CODE, giftDepositType.getBranchCode().trim());
                msg.setAttribute(Fields.ACCOUNT_NO, "");
                msg.setAttribute(Fields.PAN, "");
                msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.DEPOSIT_GIFTCARD_ELEMENT);
                msg.setAttribute(Fields.MESSAGE_TYPE, Constants.DEPOSIT_GIFTCARD_MSG_TYPE);
                msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
                msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
              } else if (msg_str.contains(Constants.TOURISTCARD_ELEMENT)) {

                JAXBContext context = JAXBContext.newInstance(TouristCardReq.class);
                StringReader stringReader = new StringReader(msg_str);
                Unmarshaller u = context.createUnmarshaller();
                TouristCardReq touristReq = (TouristCardReq) u.unmarshal(stringReader);
                log.info("PARSED");
                msg.setAttribute(CMMessage.COMMAND_OBJ, touristReq);
                msg.setAttribute(Fields.RRN, touristReq.getRrn().trim());
                msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.TOURISTCARD_ELEMENT);

                if (touristReq.getRecordflag().equals(Constants.CREATE_CARD_RECORDFLAG))
                    msg.setAttribute(Fields.MESSAGE_TYPE, Constants.CREATE_MSG_TYPE);
                else if (touristReq.getRecordflag().equals(Constants.CONNECTIVITY_RECORDFLAG))
                    msg.setAttribute(Fields.MESSAGE_TYPE, Constants.UPDATE_MSG_TYPE);
                else
                    throw new FormatException("Record flag is invalid");

                msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
                msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                msg.setAttribute(Fields.PAN, touristReq.getCardno().trim());
                msg.setAttribute(Fields.ACCOUNT_NO, touristReq.getAccountno().trim());
            } else if (msg_str.contains(Constants.JOINTACCOUNT_ELEMENT)) {
                JAXBContext context = JAXBContext.newInstance(JointCardReq.class);
                StringReader stringReader = new StringReader(msg_str);
                Unmarshaller u = context.createUnmarshaller();
                JointCardReq jointCardReq = (JointCardReq) u.unmarshal(stringReader);
                log.info("PARSED");
                msg.setAttribute(CMMessage.COMMAND_OBJ, jointCardReq);
                msg.setAttribute(Fields.RRN, jointCardReq.getRrn().trim());
                msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.JOINTACCOUNT_ELEMENT);

                if (jointCardReq.getRecordflag().equals(Constants.CREATE_CARD_RECORDFLAG))
                    msg.setAttribute(Fields.MESSAGE_TYPE, Constants.JOINT_CREATE_MSG_TYPE);
                else if (jointCardReq.getRecordflag().equals(Constants.CONNECTIVITY_RECORDFLAG))
                    msg.setAttribute(Fields.MESSAGE_TYPE, Constants.JOINT_UPDATE_MSG_TYPE);
                else if (jointCardReq.getRecordflag().equals(Constants.SEPARAT_RECORDFLAG))
                    msg.setAttribute(Fields.MESSAGE_TYPE, Constants.SEPARAT_MSG_TYPE);
                else
                    throw new FormatException("Record flag is invalid");

                msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
                msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                msg.setAttribute(Fields.PAN, jointCardReq.getCardno().trim());
                msg.setAttribute(Fields.ACCOUNT_NO, jointCardReq.getAccountno().trim());
            } else if (msg_str.contains(Constants.CHILDCARD_ELEMENT)) {

              JAXBContext context = JAXBContext.newInstance(ChildCardReq.class);
              StringReader stringReader = new StringReader(msg_str);
              Unmarshaller u = context.createUnmarshaller();
              ChildCardReq childReq = (ChildCardReq) u.unmarshal(stringReader);
              log.info("PARSED");
              msg.setAttribute(CMMessage.COMMAND_OBJ, childReq);
              msg.setAttribute(Fields.RRN, childReq.getRrn().trim());
              msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.CHILDCARD_ELEMENT);

              if (childReq.getRecordflag().equals(Constants.CREATE_CARD_RECORDFLAG))
                  msg.setAttribute(Fields.MESSAGE_TYPE, Constants.CREATE_CHILD_MSG_TYPE);
              else if (childReq.getRecordflag().equals(Constants.CONNECTIVITY_RECORDFLAG))
                  msg.setAttribute(Fields.MESSAGE_TYPE, Constants.UPDATE_CHILD_MSG_TYPE);
              else
                  throw new FormatException("Record flag is invalid");
              msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
              msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
              msg.setAttribute(Fields.PAN, childReq.getCardno().trim());
              msg.setAttribute(Fields.ACCOUNT_NO, childReq.getAccountno().trim());
              msg.setAttribute(Fields.RECORD_FLAG, childReq.getRecordflag().trim());

            } else if (msg_str.contains(Constants.DCHARGE_ELEMENT)) {
                JAXBContext context = JAXBContext.newInstance(DChargeReq.class);
                StringReader stringReader = new StringReader(msg_str);
                Unmarshaller u = context.createUnmarshaller();
                DChargeReq dChargeReq = (DChargeReq) u.unmarshal(stringReader);
                log.info("PARSED");
                msg.setAttribute(CMMessage.COMMAND_OBJ, dChargeReq);
                msg.setAttribute(Fields.RRN, dChargeReq.getRrn().trim());
                msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.DCHARGE_ELEMENT);
                msg.setAttribute(Fields.MESSAGE_TYPE, Constants.DCHARGE_MSG_TYPE);
                msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
                msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                msg.setAttribute(Fields.PAN, dChargeReq.getCardNo().trim());
                msg.setAttribute(Fields.ACCOUNT_NO, dChargeReq.getAccountNo().trim());
            } else if (msg_str.contains(Constants.IMMEDIATE_CHARGE_ELEMENT)) {
                JAXBContext context = JAXBContext.newInstance(ImmediateChargeReq.class);
                StringReader stringReader = new StringReader(msg_str);
                Unmarshaller u = context.createUnmarshaller();
                ImmediateChargeReq immediateChargeReq = (ImmediateChargeReq) u.unmarshal(stringReader);
                log.info("PARSED");
                msg.setAttribute(CMMessage.COMMAND_OBJ, immediateChargeReq);
                msg.setAttribute(Fields.RRN, immediateChargeReq.getRrn().trim());
                msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.IMMEDIATE_CHARGE_ELEMENT);
                msg.setAttribute(Fields.MESSAGE_TYPE, Constants.IMMEDIATE_CHARGE_MSG_TYPE);
                msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
                msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                msg.setAttribute(Fields.PAN, immediateChargeReq.getCardNo().trim());
                msg.setAttribute(Fields.ACCOUNT_NO, immediateChargeReq.getAccountNo().trim());
            } else {
                msg.setAttribute(Params.ACTION_MESSAGE, "Invalid format of Request ");
                msg.setAttribute(Params.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new FormatException(ActionCode.FORMAT_ERROR);
            }

            msg.setAttribute(CMMessage.MESSAGE_SOURCE, source);

        } catch (FormatException e) {
            log.error(e);
            try {
                source.sendBranch(GenerateErrorXML(ActionCode.FORMAT_ERROR, "Invalid format of Request "));
            } catch (Exception e1) {
                log.error("Can not send response to Client: " + e1);
            }
        } catch (Exception e) {
            log.error(e);
            try {
                source.sendBranch(msg_str);
            } catch (IOException e1) {
                log.error("Can not send response to Client: " + e1);
            }

        }

    }

    private void parsePolicyMessages(CMMessage msg, String msg_str) throws FormatException, Exception {
        CardPolicy cardPolicy = new CardPolicy();
        if (msg_str.contains(Constants.POLICY_CREATE_BATCH_ELEMENT)) {
            JAXBContext context = JAXBContext.newInstance(CreateGroupCardReq.class);
            StringReader stringReader = new StringReader(msg_str);
            Unmarshaller u = context.createUnmarshaller();
            CreateGroupCardReq creategroupCardReq = (CreateGroupCardReq) u.unmarshal(stringReader);
            cardPolicy.getPolicyMsg(creategroupCardReq, Constants.POLICY_CREATE_BATCH_ELEMENT);
            log.info("PARSED");
            msg.setAttribute(CMMessage.COMMAND_OBJ, creategroupCardReq);
            msg.setAttribute(Fields.RRN, creategroupCardReq.getRrn().trim());
            msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.POLICY_CREATE_BATCH_ELEMENT);
            msg.setAttribute(Fields.MESSAGE_TYPE, Constants.POLICY_CREATE_BATCH_MSG_TYPE);
            msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
            msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
            msg.setAttribute(Fields.POLICY_CLASS, cardPolicy);
            msg.setAttribute(Fields.PAN, creategroupCardReq.getCardno().trim());
            msg.setAttribute(Fields.ACCOUNT_NO, creategroupCardReq.getAccountNo().trim());

        }else if (msg_str.contains(Constants.POLICY_CREATE_ELEMENT)) {
            JAXBContext context = JAXBContext.newInstance(CreatePolicyReq.class);
            StringReader stringReader = new StringReader(msg_str);
            Unmarshaller u = context.createUnmarshaller();
            CreatePolicyReq createPolicyReq = (CreatePolicyReq) u.unmarshal(stringReader);
            cardPolicy.getPolicyMsg(createPolicyReq, Constants.POLICY_CREATE_ELEMENT);
            log.info("PARSED");
            msg.setAttribute(CMMessage.COMMAND_OBJ, createPolicyReq);
            msg.setAttribute(Fields.RRN, createPolicyReq.getRrn().trim());
            msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.POLICY_CREATE_ELEMENT);
            msg.setAttribute(Fields.MESSAGE_TYPE, Constants.POLICY_CREATE_MSG_TYPE);
            msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
            msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
            msg.setAttribute(Fields.POLICY_CLASS, cardPolicy);
            msg.setAttribute(Fields.PAN, createPolicyReq.getCardno().trim());
            msg.setAttribute(Fields.ACCOUNT_NO, createPolicyReq.getAccountNo().trim());

        } else if (msg_str.contains(Constants.POLICY_UPDATE_ALL_ELEMENT)) {
            JAXBContext context = JAXBContext.newInstance(UpdateAllPolicyReq.class);
            StringReader stringReader = new StringReader(msg_str);
            Unmarshaller u = context.createUnmarshaller();
            UpdateAllPolicyReq updatePolicyReq = (UpdateAllPolicyReq) u.unmarshal(stringReader);
            cardPolicy.getPolicyMsg(updatePolicyReq, Constants.POLICY_UPDATE_ALL_ELEMENT);
            log.info("PARSED");
            msg.setAttribute(CMMessage.COMMAND_OBJ, updatePolicyReq);
            msg.setAttribute(Fields.RRN, updatePolicyReq.getRrn().trim());
            msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.POLICY_UPDATE_ALL_ELEMENT);
            msg.setAttribute(Fields.MESSAGE_TYPE, Constants.POLICY_UPDATE_ALL_MSG_TYPE);
            msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
            msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
            msg.setAttribute(Fields.POLICY_CLASS, cardPolicy);
            msg.setAttribute(Fields.PAN,"");
            msg.setAttribute(Fields.ACCOUNT_NO, updatePolicyReq.getAccountNo().trim());
        } else if (msg_str.contains(Constants.POLICY_UPDATE_COLLECTION_ELEMENT)) {
            JAXBContext context = JAXBContext.newInstance(UpdateCollectionPolicyReq.class);
            StringReader stringReader = new StringReader(msg_str);
            Unmarshaller u = context.createUnmarshaller();
            UpdateCollectionPolicyReq updatePolicyReq = (UpdateCollectionPolicyReq) u.unmarshal(stringReader);
            cardPolicy.getPolicyMsg(updatePolicyReq, Constants.POLICY_UPDATE_COLLECTION_ELEMENT);
            log.info("PARSED");
            msg.setAttribute(CMMessage.COMMAND_OBJ, updatePolicyReq);
            msg.setAttribute(Fields.RRN, updatePolicyReq.getRrn().trim());
            msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.POLICY_UPDATE_COLLECTION_ELEMENT);
            msg.setAttribute(Fields.MESSAGE_TYPE, Constants.POLICY_UPDATE_COLLECTION_MSG_TYPE);
            msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
            msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
            msg.setAttribute(Fields.POLICY_CLASS, cardPolicy);
//                msg.setAttribute(Fields.PAN,updatePolicyReq.getCardno());
            msg.setAttribute(Fields.PAN,"");
            msg.setAttribute(Fields.ACCOUNT_NO, updatePolicyReq.getAccountNo().trim());
        } else if (msg_str.contains(Constants.POLICY_UPDATE_ELEMENT)) {
            JAXBContext context = JAXBContext.newInstance(UpdatePolicyReq.class);
            StringReader stringReader = new StringReader(msg_str);
            Unmarshaller u = context.createUnmarshaller();
            UpdatePolicyReq updatePolicyReq = (UpdatePolicyReq) u.unmarshal(stringReader);
            cardPolicy.getPolicyMsg(updatePolicyReq, Constants.POLICY_UPDATE_ELEMENT);
            log.info("PARSED");
            msg.setAttribute(CMMessage.COMMAND_OBJ, updatePolicyReq);
            msg.setAttribute(Fields.RRN, updatePolicyReq.getRrn().trim());
            msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.POLICY_UPDATE_ELEMENT);
            msg.setAttribute(Fields.MESSAGE_TYPE, Constants.POLICY_UPDATE_MSG_TYPE);
            msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
            msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
            msg.setAttribute(Fields.POLICY_CLASS, cardPolicy);
            msg.setAttribute(Fields.PAN, updatePolicyReq.getCardno().trim());
            msg.setAttribute(Fields.ACCOUNT_NO, updatePolicyReq.getAccountNo().trim());


        } else if (msg_str.contains(Constants.POLICY_PRESENTATION_ELEMENT)) {
            JAXBContext context = JAXBContext.newInstance(PresentationPolicyReq.class);
            StringReader stringReader = new StringReader(msg_str);
            Unmarshaller u = context.createUnmarshaller();
            PresentationPolicyReq presentationPolicyReq = (PresentationPolicyReq) u.unmarshal(stringReader);
            cardPolicy.getPolicyMsg(presentationPolicyReq, Constants.POLICY_PRESENTATION_ELEMENT);
            log.info("PARSED");
            msg.setAttribute(CMMessage.COMMAND_OBJ, presentationPolicyReq);
            msg.setAttribute(Fields.RRN, presentationPolicyReq.getRrn().trim());
            msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.POLICY_PRESENTATION_ELEMENT);
            msg.setAttribute(Fields.MESSAGE_TYPE, Constants.POLICY_PRESENTATION_MSG_TYPE);
            msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
            msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
            msg.setAttribute(Fields.POLICY_CLASS, cardPolicy);
            msg.setAttribute(Fields.PAN, presentationPolicyReq.getCardno().trim());
            msg.setAttribute(Fields.ACCOUNT_NO, presentationPolicyReq.getAccountNo().trim());
        } else if (msg_str.contains(Constants.POLICY_ENDED_PRESENTATION_ELEMENT)) {
            JAXBContext context = JAXBContext.newInstance(PresentationEndedPolicyReq.class);
            StringReader stringReader = new StringReader(msg_str);
            Unmarshaller u = context.createUnmarshaller();
            PresentationEndedPolicyReq presentationPolicyReq = (PresentationEndedPolicyReq) u.unmarshal(stringReader);
            cardPolicy.getPolicyMsg(presentationPolicyReq, Constants.POLICY_ENDED_PRESENTATION_ELEMENT);
            log.info("PARSED");
            msg.setAttribute(CMMessage.COMMAND_OBJ, presentationPolicyReq);
            msg.setAttribute(Fields.RRN, presentationPolicyReq.getRrn().trim());
            msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.POLICY_ENDED_PRESENTATION_ELEMENT);
            msg.setAttribute(Fields.MESSAGE_TYPE, Constants.POLICY_ENDED_PRESENTATION_MSG_TYPE);
            msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
            msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
            msg.setAttribute(Fields.POLICY_CLASS, cardPolicy);
            msg.setAttribute(Fields.PAN, presentationPolicyReq.getCardno().trim());
            msg.setAttribute(Fields.ACCOUNT_NO, presentationPolicyReq.getAccountNo().trim());
        } else if (msg_str.contains(Constants.POLICY_ENDED_ELEMENT)) {
            JAXBContext context = JAXBContext.newInstance(EndedPolicyReq.class);
            StringReader stringReader = new StringReader(msg_str);
            Unmarshaller u = context.createUnmarshaller();
            EndedPolicyReq endedPolicyReq = (EndedPolicyReq) u.unmarshal(stringReader);
            cardPolicy.getPolicyMsg(endedPolicyReq, Constants.POLICY_ENDED_ELEMENT);
            log.info("PARSED");
            msg.setAttribute(CMMessage.COMMAND_OBJ, endedPolicyReq);
            msg.setAttribute(Fields.RRN, endedPolicyReq.getRrn().trim());
            msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.POLICY_ENDED_ELEMENT);
            msg.setAttribute(Fields.MESSAGE_TYPE, Constants.POLICY_ENDED_MSG_TYPE);
            msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_CMS);
            msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
            msg.setAttribute(Fields.POLICY_CLASS, cardPolicy);
            msg.setAttribute(Fields.PAN, endedPolicyReq.getCardno().trim());
            msg.setAttribute(Fields.ACCOUNT_NO, endedPolicyReq.getAccountNo().trim());
        } else {
            msg.setAttribute(Params.ACTION_MESSAGE, "Invalid format of Request ");
            msg.setAttribute(Params.ACTION_CODE, ActionCode.FORMAT_ERROR);
            throw new FormatException(ActionCode.FORMAT_ERROR);
        }
    }

    public void setChainHandler(CMHandler cmHandler) {
        this.cmHandler = cmHandler;
    }

    public CMHandler getChainHandler() {
        return cmHandler;
    }

    public static String GenerateErrorXML(String actionCodeStr, String desc_str) throws Exception {
        String xmlstr = "";
        Element root = new Element("root");
        Document doc = new Document(root);
        String comment = " Generated: " + DateUtil.getSystemDate() + " " + DateUtil.getSystemTime();
        doc.getContent().add(0, new Comment(comment));

        Element actionCode = new Element("ACTIONCODE");
        actionCode.setText(actionCodeStr);
        root.addContent(actionCode);
        Element desc = new Element("DESC");
        desc.setText(desc_str);
        root.addContent(desc);

        XMLOutputter out = new XMLOutputter();

        xmlstr = out.outputString(doc);
        return xmlstr;
    }
}
