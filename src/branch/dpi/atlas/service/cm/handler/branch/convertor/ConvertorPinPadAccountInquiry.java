package branch.dpi.atlas.service.cm.handler.branch.convertor;

import branch.dpi.atlas.service.cm.imf.branch.BranchToIMFFormater;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.iso.ISOUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * User: R.Nasiri
 * Date: Sep 25, 2017
 * Time: 03:04 PM
 */
public class ConvertorPinPadAccountInquiry extends BranchBaseConvertor implements BranchToIMFFormater {

    public CMCommand format(BranchMessage branchMsg) {
        CMCommand command = super.format(branchMsg);
        return command;
    }

    public String createResponse(CMMessage msg, Map map) throws CMFault {
        List<String> accountList = new ArrayList<String>();
        try {
            if (map.containsKey(Constants.ACCOUNT_LIST)) {
                accountList = (List<String>) map.get(Constants.ACCOUNT_LIST);
            }
            else
                throw new NotFoundException("Branch Code does not exist");
            StringBuilder responseStr = new StringBuilder();
            BranchMessage branchMsg = (BranchMessage)msg.getAttribute(CMMessage.COMMAND_OBJ);
            int accountCount=accountList.size();
            responseStr.append(branchMsg.createResponseHeader())
                    .append(ISOUtil.padleft(String.valueOf(accountCount), branchMsg.ACCOUNT_COUNT, '0'));
            for (String account : accountList) {
                responseStr = responseStr.append(ISOUtil.padleft(account.trim(), branchMsg.ACCOUNT_NO, '0'));
            }
            return responseStr.toString();

        } catch (NotFoundException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.BRANCH_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.BRANCH_NOT_FOUND));
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }

    }
}



