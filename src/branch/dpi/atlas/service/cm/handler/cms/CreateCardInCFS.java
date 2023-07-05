package branch.dpi.atlas.service.cm.handler.cms;

import branch.dpi.atlas.service.cm.handler.cms.customer.CUSTOMERType;
import branch.dpi.atlas.service.cm.handler.cms.touristCard.TouristCardReq;
import branch.dpi.atlas.util.ImmediateCardUtil;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.util.Constants;
import branch.dpi.atlas.service.cm.handler.cms.jointCard.JointCardReq;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: jan 12, 2015
 * Time: 10:04:03 PM
 */
public class CreateCardInCFS extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        String accountNo;
        String accountGroup;
        String cardNo;
        String editDate;
        String row = "";
        String nationalCode = "";
        String frgCode = "";
        String latinName = "";
        long maxTransLimit = Constants.IGNORE_MAX_TRANS_LIMIT;

        try {

            String requestType = msg.getAttributeAsString(CMMessage.REQUEST_TYPE);


            if (requestType.equals(Constants.TOURISTCARD_ELEMENT)) {

                TouristCardReq customerMsg = (TouristCardReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
                accountNo = customerMsg.getAccountno();
                accountGroup = customerMsg.getAccountgroup().trim();
                cardNo = customerMsg.getCardno();
                editDate = customerMsg.getEditdate();
                latinName = customerMsg.getNamefamilylatin();
                row = customerMsg.getRow().trim();

            } else if (requestType.equals(Constants.JOINTACCOUNT_ELEMENT)) {

                JointCardReq customerMsg = (JointCardReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
                accountNo = customerMsg.getAccountno();
                accountGroup = customerMsg.getAccountgroup().trim();
                cardNo = customerMsg.getCardno();
                editDate = customerMsg.getEditdate();
                frgCode = customerMsg.getFrgCode().trim();
                nationalCode = customerMsg.getNationalCode().trim();
                latinName = customerMsg.getNamefamilylatin();
                row = Constants.NORMAL_CARD_SERIES;

            } else {
                CUSTOMERType customerMsg = (CUSTOMERType) msg.getAttribute(CMMessage.COMMAND_OBJ);
                accountNo = customerMsg.getAccountno();
                accountGroup = customerMsg.getAccountgroup().trim();
                cardNo = customerMsg.getCardno();
                editDate = customerMsg.getEditdate();
                latinName = customerMsg.getNamefamilylatin();
                row = customerMsg.getRow().trim();
            }

            if (ChannelFacadeNew.ExistCardInDB(cardNo)) {

                String[] custacc = ChannelFacadeNew.findCustomerID(accountNo,nationalCode,frgCode);
                if (custacc[0] == null || custacc[0].equals("")) {
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.ACCOUNT_NOT_FOUND);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_NOT_FOUND));
                }
                if (custacc[1].equals(Constants.SHARED_ACCOUNT_TYPE)) {
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_OPERATION);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_OPERATION));
                }

                if (custacc[3].equals(Constants.GROUP_CARD_TYPE_STR) ||  requestType.equals(Constants.JOINTACCOUNT_ELEMENT)) {
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.CARD_HAS_BEEN_DEFINED_BEFORE);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CARD_HAS_BEEN_DEFINED_BEFORE));
                }
                ChannelFacadeNew.UpdateCardAndCardAccount(cardNo, accountNo, editDate, accountGroup,row,maxTransLimit,custacc[3]);
                msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.SUCCESSFULL_WITH_CARD_EXISTANCE_MESSAGE);
            } else {
                String[] custacc = ChannelFacadeNew.findCustomerID(accountNo,nationalCode,frgCode);
                if (custacc[0] == null || custacc[0].equals("")) {
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.ACCOUNT_NOT_FOUND);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_NOT_FOUND));
                }
                if (custacc[3].equals(Constants.GROUP_CARD_TYPE_STR)) {
                    if (ImmediateCardUtil.isParent(Integer.parseInt(row))) {
                    } else if (ImmediateCardUtil.isChild(Integer.parseInt(row)) && requestType.equals(Constants.TOURISTCARD_ELEMENT))
                        maxTransLimit = 0;
                    else if (ImmediateCardUtil.isChild(Integer.parseInt(row))) {
                        msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_OPERATION);
                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_OPERATION));
                    } else {
                        msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_ROW);
                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_ROW));
                    }
                } else if (requestType.equals(Constants.TOURISTCARD_ELEMENT)) {
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_OPERATION);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_OPERATION));
                } else if (custacc[3].equals(Constants.NORMAL_CARD_TYPE_STR)) {
                    if (ImmediateCardUtil.isParent(Integer.parseInt(row)) || ImmediateCardUtil.isChild(Integer.parseInt(row))) {
                        msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_ROW);
                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_ROW));
                    }
                }
                ChannelFacadeNew.InsertCardAndCardAccount(cardNo, accountNo, editDate, accountGroup, custacc[0], row, maxTransLimit, latinName,custacc[3]);
            }


        } catch (CMFault e) {
            throw e;
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside CreateCardInCFS.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}

