package branch.dpi.atlas.service.cm.handler.branch.convertor;

import branch.dpi.atlas.service.cm.imf.branch.BranchToIMFFormater;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.CustomerServiceNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
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
 * Date: Apr 11, 2015
 * Time: 1:48 PM
 */
public class ConvertorAccountToCard extends BranchBaseConvertor implements BranchToIMFFormater {

    public CMCommand format(BranchMessage branchMsg) {
        CMCommand command = super.format(branchMsg);
        return command;
    }

    public String createResponse(CMMessage msg, Map holder) throws CMFault {
        try {
            List<String> list = new ArrayList<String>();
                if (holder.containsKey(Constants.CARD_ACCOUNT_LIST)) {
                    list = (List<String>) holder.get(Constants.CARD_ACCOUNT_LIST);
                }
                else
                    throw new NotFoundException("card/account not found");

            StringBuilder responseStr = new StringBuilder();
            BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            responseStr.append(branchMsg.createResponseHeader());
            if (msg.getAttributeAsString(Fields.REQUEST_IS_ACCOUNT_BASED).equals("0")){
                if (branchMsg.getCardNo().trim().length()>16)
                    responseStr.append(branchMsg.getCardNo().substring(0,16));
                else
                    responseStr.append(ISOUtil.padleft(branchMsg.getCardNo(), branchMsg.CARD_NO2, '0'));
            }else
                responseStr.append(ISOUtil.padleft("", branchMsg.CARD_NO2, '0'));

            responseStr.append(ISOUtil.padleft(String.valueOf(list.size()), branchMsg.ACCOUNT_CARD_COUNT, '0'));

            for (String object : list) {
                responseStr.append(ISOUtil.padleft(object.trim(), branchMsg.CARD_NO3, ' '));
            }
            return responseStr.toString();
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }

    }


}
