package branch.dpi.atlas.service.cm.handler.AMX;

import branch.dpi.atlas.service.cm.source.branch.message.AmxMessage;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
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
 * User: R.Nasiri
 * Date: Dec 10, 2018
 * Time: 04:50 PM
 */
public class FindCustomerID extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        AmxMessage amxMessage = (AmxMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String accountNo = amxMessage.getAccountNo();
        String customerId = "";
        String accountTitle="";
        try {
            if (amxMessage.getPin().equalsIgnoreCase(Constants.PIN_CREATE_NAC_AMX)){
                if(amxMessage.getOldNationalCode()!=null && !amxMessage.getOldNationalCode().trim().equalsIgnoreCase("")){
                    String[] accountInfo = ChannelFacadeNew.findCustomerID(accountNo,amxMessage.getOldNationalCode(),amxMessage.getOld_ext_IdNumber());
                    customerId=accountInfo[0];
                    accountTitle=accountInfo[1];
                }else{
                    String[] accountInfo = ChannelFacadeNew.findCustomerID(accountNo,amxMessage.getNationalCode(),amxMessage.getExt_IdNumber());
                    customerId=accountInfo[0];
                    accountTitle=accountInfo[1];
                }
                if (accountTitle != null && !accountTitle.equals(""))
                    msg.setAttribute(Fields.ORIG_ACCOUNT_TYPE,accountTitle);
            }
            if (customerId != null && !customerId.equals("")) {
                holder.put(Fields.BRANCH_CUSTOMER_ID, customerId);
            }else{
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.ACCOUNT_NOT_FOUND);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_NOT_FOUND));
            }
        }catch (CMFault e){
            throw e;
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