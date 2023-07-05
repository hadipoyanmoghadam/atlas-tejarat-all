package branch.dpi.atlas.service.cm.handler.cms;


import branch.dpi.atlas.service.cm.handler.cms.cancellation.CANCELLATIONType;
import branch.dpi.atlas.service.cm.handler.cms.childCard.ChildCardReq;
import branch.dpi.atlas.service.cm.handler.cms.customer.CUSTOMERType;
import branch.dpi.atlas.service.cm.handler.cms.giftCharge.GIFTCHARGEType;
import branch.dpi.atlas.service.cm.handler.cms.giftDeposit.GiftDepositReq;
import branch.dpi.atlas.service.cm.handler.cms.giftDisCharge.GIFTDISCHARGEType;
import branch.dpi.atlas.service.cm.handler.cms.groupCharge.DChargeReq;
import branch.dpi.atlas.service.cm.handler.cms.groupCharge.ImmediateChargeReq;
import branch.dpi.atlas.service.cm.handler.cms.reCreate.RECREATEType;
import branch.dpi.atlas.service.cm.handler.cms.touristCard.TouristCardReq;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import branch.dpi.atlas.util.ImmediateCardUtil;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import branch.dpi.atlas.service.cm.handler.cms.jointAccountInfo.JointAccountInfoReq;
import branch.dpi.atlas.service.cm.handler.cms.jointCard.JointCardReq;
import branch.dpi.atlas.service.cm.handler.cms.createBatch.CreateGroupCardReq;
import branch.dpi.atlas.service.cm.handler.cms.groupCharge.CardPolicy;

import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: April 28, 2015
 * Time: 17:04:03 PM
 */
public class CMSValidateMessage extends TJServiceHandler {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {
            String requestType = msg.getAttributeAsString(CMMessage.REQUEST_TYPE);

            String rrn = msg.getAttributeAsString(Fields.RRN);
            String cardNo = msg.getAttributeAsString(Fields.PAN);
            String accountNo = msg.getAttributeAsString(Fields.ACCOUNT_NO);

            if (!ImmediateCardUtil.validateElement(rrn, BranchMessage.MESSAGE_SEQUENCE)) {
                msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID RRN!");
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
            if (requestType.equals(Constants.CUSTOMER_ELEMENT)) {
                CUSTOMERType customerMsg = (CUSTOMERType) msg.getAttribute(CMMessage.COMMAND_OBJ);

                if (!ImmediateCardUtil.validateElement(customerMsg.getAccountgroup().trim(), BranchMessage.ACCOUNT_GROUP)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountGroup!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(cardNo, BranchMessage.CARD_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID CardNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(accountNo, BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElement(customerMsg.getCardType().trim(), BranchMessage.ACCOUNT_TYPE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID Card Type!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if ((customerMsg.getCardType().trim().equalsIgnoreCase(Constants.GROUP_CARD_TYPE) &&
                        ImmediateCardUtil.isChild(Integer.parseInt(customerMsg.getRow()))) ||
                        (customerMsg.getCardType().trim().equalsIgnoreCase(Constants.NORMAL_CARD_TYPE) && Integer.parseInt(customerMsg.getRow().trim()) >0))
                {                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID RowNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }

                if (!ImmediateCardUtil.validateElement(customerMsg.getEditdate().trim(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID Date!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }

            } else if (requestType.equals(Constants.TOURISTCARD_ELEMENT)) {
                TouristCardReq touristMsg = (TouristCardReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
                if (Integer.parseInt(touristMsg.getRow().trim()) > 2)
                    touristMsg.setRow(Constants.GROUP_CARD_CHILD);

                if (!ImmediateCardUtil.validateElementNotZero(cardNo, BranchMessage.CARD_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID CardNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(accountNo, BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(touristMsg.getEditdate(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID Date!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
            } else if (requestType.equals(Constants.CHILDCARD_ELEMENT)) {
                ChildCardReq childMsg = (ChildCardReq) msg.getAttribute(CMMessage.COMMAND_OBJ);

                if (!ImmediateCardUtil.validateElement(childMsg.getAccountgroup().trim(), BranchMessage.ACCOUNT_GROUP)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountGroup!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(cardNo, BranchMessage.CARD_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID CardNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(accountNo, BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (childMsg.getRow().trim().length() > BranchMessage.ID_SERIES)
                    childMsg.setRow(Constants.GROUP_CARD_CHILD);
                else if (!ImmediateCardUtil.validateElement(childMsg.getRow().trim(), BranchMessage.ID_SERIES)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID RowNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }

                if ((childMsg.getAccount_type()==null) || (!ImmediateCardUtil.validateElement(childMsg.getAccount_type().trim(), BranchMessage.ACCOUNT_TYPE))) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountGroup!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }

                if (!ImmediateCardUtil.validateElementNotZero(childMsg.getEditdate(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID Date!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }

                if (!ImmediateCardUtil.validateElementNotZero(childMsg.getBirthDate(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID BirthDate!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }

            } else if (requestType.equals(Constants.RE_CREATE_ELEMENT)) {
                RECREATEType reCreateMsg = (RECREATEType) msg.getAttribute(CMMessage.COMMAND_OBJ);

                if (!ImmediateCardUtil.validateElementNotZero(cardNo, BranchMessage.CARD_NO) && !ImmediateCardUtil.validateElement(cardNo, 19)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID CardNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }

                if (!ImmediateCardUtil.validateElementNotZero(reCreateMsg.getNewcardno().trim(), BranchMessage.CARD_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID New CardNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }

                if (!ImmediateCardUtil.validateElement(reCreateMsg.getEditdate(), BranchMessage.CHANGE_DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID Edit Date!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }

            } else if (requestType.equals(Constants.ACCOUNTINFO_ELEMENT)) {
                if (!ImmediateCardUtil.validateElementNotZero(accountNo, BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }

            } else if (requestType.equals(Constants.CARDINFO_ELEMENT) || requestType.equals(Constants.REVOKE_ELEMENT)) {
                if (!ImmediateCardUtil.validateElementNotZero(cardNo, BranchMessage.CARD_NO) && !ImmediateCardUtil.validateElement(cardNo, 19)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID CardNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
            } else if (requestType.equals(Constants.CHARGE_GIFTCARD_ELEMENT)) {
                GIFTCHARGEType giftChargeMsg = (GIFTCHARGEType) msg.getAttribute(CMMessage.COMMAND_OBJ);

                if (!ImmediateCardUtil.validateElementNotZero(cardNo, BranchMessage.CARD_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID CardNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementLessAndEqual(giftChargeMsg.getRequestNo().trim(), BranchMessage.MESSAGE_SEQUENCE) || giftChargeMsg.getRequestNo().trim().equals("")) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID RequestNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(giftChargeMsg.getCreationDate(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID Creation Date!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
            } else if (requestType.equals(Constants.DISCHARGE_GIFTCARD_ELEMENT)) {
                GIFTDISCHARGEType giftDisChargeMsg = (GIFTDISCHARGEType) msg.getAttribute(CMMessage.COMMAND_OBJ);

                if (!ImmediateCardUtil.validateElementNotZero(cardNo, BranchMessage.CARD_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID CardNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementLessAndEqual(giftDisChargeMsg.getRequestNo().trim(), BranchMessage.MESSAGE_SEQUENCE) || giftDisChargeMsg.getRequestNo().trim().equals("")) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID RequestNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }

                if (!ImmediateCardUtil.validateElementNotZero(giftDisChargeMsg.getCreationDate(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID Creation Date!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }

            } else if (requestType.equals(Constants.DEPOSIT_GIFTCARD_ELEMENT)) {
                GiftDepositReq giftDepositMsg = (GiftDepositReq) msg.getAttribute(CMMessage.COMMAND_OBJ);

                if (!ImmediateCardUtil.validateElementLessAndEqual(giftDepositMsg.getRequestNo().trim(), BranchMessage.MESSAGE_SEQUENCE) || giftDepositMsg.getRequestNo().trim().equals("")) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID RequestNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElement(giftDepositMsg.getBranchDocNo().trim(), BranchMessage.DOCUMENT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID Branch DocumentNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(giftDepositMsg.getTransDate(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID TransDate!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
            } else if (requestType.equals(Constants.JOINTACCOUNT_ELEMENT)) {
                JointCardReq customerMsg = (JointCardReq) msg.getAttribute(CMMessage.COMMAND_OBJ);

                if (!ImmediateCardUtil.validateElement(customerMsg.getAccountgroup().trim(), BranchMessage.ACCOUNT_GROUP)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountGroup!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(cardNo, BranchMessage.CARD_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID CardNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(accountNo, BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.checkNationalCodeValidity(customerMsg.getNationalCode())) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID NationalCode!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (customerMsg.getNationalCode().equalsIgnoreCase("9999999999") && !ImmediateCardUtil.validateElement(customerMsg.getFrgCode(), BranchMessage.EXT_ID_NUMBER)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID External Code!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }

                if (!ImmediateCardUtil.validateElementNotZero(customerMsg.getEditdate(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID Date!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
            } else if (requestType.equals(Constants.JOINTACCOUNTINFO_ELEMENT)) {
                JointAccountInfoReq customerMsg = (JointAccountInfoReq) msg.getAttribute(CMMessage.COMMAND_OBJ);

                if (!ImmediateCardUtil.validateElementNotZero(accountNo, BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.checkNationalCodeValidity(customerMsg.getNationalCode())) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID NationalCode!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (customerMsg.getNationalCode().equalsIgnoreCase("9999999999") && !ImmediateCardUtil.validateElement(customerMsg.getFrgCode(), BranchMessage.EXT_ID_NUMBER)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID External Code!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
            } else if (requestType.equals(Constants.POLICY_PRESENTATION_ELEMENT) || requestType.equals(Constants.POLICY_ENDED_PRESENTATION_ELEMENT) || requestType.equals(Constants.POLICY_ENDED_ELEMENT)) {
                if (!ImmediateCardUtil.validateElementNotZero(cardNo, BranchMessage.CARD_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID CardNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(accountNo, BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }

            } else if (requestType.startsWith(Constants.POLICY_ELEMENT)) {
                CardPolicy policyMsg = (CardPolicy) msg.getAttribute(Fields.POLICY_CLASS);
                if (!requestType.startsWith(Constants.POLICY_UPDATE_ALL_ELEMENT) && !requestType.startsWith(Constants.POLICY_UPDATE_COLLECTION_ELEMENT) && !ImmediateCardUtil.validateElementNotZero(cardNo, BranchMessage.CARD_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID CardNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(accountNo, BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }

                if ((ImmediateCardUtil.validateElementISZero(policyMsg.getAmount()) && !policyMsg.getType().equalsIgnoreCase(Constants.NON_AGGREGATE_TYPE)) || ImmediateCardUtil.validateElementISZero(String.valueOf(policyMsg.getCount()))
                        || ImmediateCardUtil.validateElementISZero(String.valueOf(policyMsg.getInterval())) || !ImmediateCardUtil.validateElement(policyMsg.getIntervalType(),1))
                {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID Policy Data!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }

                if (!policyMsg.getType().equals("0") && !policyMsg.getType().equals("1"))

                {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID Policy Charge Type!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!policyMsg.getIntervalType().equals("D") && !policyMsg.getIntervalType().equals("W") && !policyMsg.getIntervalType().equals("M"))

                {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID Policy Charge Interval Type!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }

                if (!ImmediateCardUtil.validateElement(policyMsg.getStartDate(), BranchMessage.REQUEST_DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID Charge Start Date!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }


                if (requestType.equals(Constants.POLICY_CREATE_BATCH_ELEMENT)) {

                    CreateGroupCardReq creategroupCardMsg = (CreateGroupCardReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
                    if (!ImmediateCardUtil.validateElement(creategroupCardMsg.getAccountgroup().trim(), BranchMessage.ACCOUNT_GROUP)) {
                        msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountGroup!");
                        throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                    }

                    if (creategroupCardMsg.getRow().trim().length() > BranchMessage.ID_SERIES)
                        creategroupCardMsg.setRow(Constants.GROUP_CARD_CHILD);
                    else if (!ImmediateCardUtil.validateElement(creategroupCardMsg.getRow().trim(), BranchMessage.ID_SERIES)  || !ImmediateCardUtil.isChild(Integer.parseInt(creategroupCardMsg.getRow().trim()))) {
                        msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID RowNo!");
                        throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                    }

                    if (!ImmediateCardUtil.validateElement(creategroupCardMsg.getEditdate(), BranchMessage.DATE)) {
                        msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID EditDate!");
                        throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                    }

                    if (!ImmediateCardUtil.validateElement(creategroupCardMsg.getBirthDate(), BranchMessage.DATE)) {
                        msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID BirthDate!");
                        throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                    }
                }

            } else if (requestType.equals(Constants.DCHARGE_ELEMENT)) {
                DChargeReq dChargeMsg = (DChargeReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
                String  type=dChargeMsg.getType().trim();
                if (!ImmediateCardUtil.validateElementNotZero(accountNo, BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElement(type, BranchMessage.REQUEST_TYPE) || (!type.equalsIgnoreCase(Constants.DCHARGE_AMOUNT) &&  !type.equalsIgnoreCase(Constants.DCHARGE_LAST_CHARGE) && !type.equalsIgnoreCase(Constants.DCHARGE_BALANCE)   )) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID DCharge Type!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }

            } else if (requestType.equals(Constants.IMMEDIATE_CHARGE_ELEMENT)) {
                ImmediateChargeReq immediateChargeMsg = (ImmediateChargeReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
                if (!ImmediateCardUtil.validateElementNotZero(accountNo, BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (ImmediateCardUtil.validateElementISZero(immediateChargeMsg.getAmount())) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID Amount!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
            } else if (requestType.equals(Constants.CANCELLATION_ELEMENT)) {
                CANCELLATIONType cancellationMsg = (CANCELLATIONType) msg.getAttribute(CMMessage.COMMAND_OBJ);

                if (!ImmediateCardUtil.validateElementNotZero(cancellationMsg.getCreationDate(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID Creation Date!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
            }

        } catch (Exception e) {
            log.error("ERROR :::Inside CMSValidateMessage.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.FORMAT_ERROR));
        }

    }
}
