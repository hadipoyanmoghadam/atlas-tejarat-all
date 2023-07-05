package branch.dpi.atlas.service.cm.handler.cms;

import branch.dpi.atlas.service.cm.handler.cms.jointAccountInfo.JointAccountInfoReq;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: July 19, 2017
 * Time: 9:45:27 AM
 */
public class FindCustomerID extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        JointAccountInfoReq accInfoMsg = (JointAccountInfoReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String accountNo = msg.getAttributeAsString(Fields.ACCOUNT_NO);
        String nationalCode = accInfoMsg.getNationalCode();
        String frgCode = accInfoMsg.getFrgCode();

        try {
            //TODO:: Moshtarak 960426
            String withdrawType=String.valueOf(holder.get(Fields.WITHDRAW_TYPE));
            if (withdrawType!=null &&  !withdrawType.equalsIgnoreCase("1")) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_OPERATION);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_OPERATION));
            }

            //TODO:: Moshtarak 960426
            String[] custacc = ChannelFacadeNew.findCustomerID_getSubTitle(accountNo, nationalCode, frgCode);


            if (custacc[0] != null && !custacc[0].equals("")) {
                holder.put(Fields.BRANCH_CUSTOMER_ID, custacc[0]);
                holder.put(Fields.ACCOUNT_TITLE, custacc[1]);
                if (Long.parseLong(custacc[2]) != Constants.ACTIVE_ROW_STATUS) {
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.ACCOUNT_BAD_STATUS);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_BAD_STATUS));
                }
                                                  //todo : set constant for sub title
                if (Long.parseLong(custacc[1]) == 0) {
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_OPERATION);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_OPERATION));
                }

            } else {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.ACCOUNT_NOT_FOUND);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_NOT_FOUND));
            }

        } catch (CMFault e) {
            throw e;
//        } catch (NotFoundException e) {
//            msg.setAttribute(Fields.ACTION_CODE, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
//            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside FindCustomerID.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }
}