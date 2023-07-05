package branch.dpi.atlas.service.cm.handler.branch.convertor;

import branch.dpi.atlas.model.tj.entity.BlockReport;
import branch.dpi.atlas.service.cm.imf.branch.BranchToIMFFormater;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.CustomerServiceNew;
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
 * Date: April 5, 2015
 * Time: 1:58 PM
 */
public class ConvertorBlockReport extends BranchBaseConvertor implements BranchToIMFFormater {

    public CMCommand format(BranchMessage branchMsg) {
        CMCommand command = super.format(branchMsg);
        return command;
    }

    public String createResponse(CMMessage msg, Map map) throws CMFault {
        List<BlockReport> reports = new ArrayList<BlockReport>();
        try {
            if (map.containsKey(Constants.BLOCK_LIST)) {
                reports = (List<BlockReport>) map.get(Constants.BLOCK_LIST);
            }
            else
                throw new NotFoundException("Block Report does not exist");
            StringBuilder responseStr = new StringBuilder();
            BranchMessage branchMsg = (BranchMessage)msg.getAttribute(CMMessage.COMMAND_OBJ);
            int count=reports.size();
            responseStr.append(branchMsg.createResponseHeader())
                    .append(ISOUtil.padleft(String.valueOf(count), branchMsg.BLOCK_COUNT, '0'));
            for (BlockReport report : reports) {

                if(report.getBranchCode().length()>5)
                    report.setBranchCode(report.getBranchCode().substring(1,6));

                if(report.getDesc().trim().length()>50)
                    report.setDesc(report.getDesc().trim().substring(0,50));

                responseStr.append(ISOUtil.padleft(String.valueOf(report.getAmount()), branchMsg.AMOUNT, '0'))
                        .append(ISOUtil.padleft(report.getDesc().trim(), branchMsg.BLOCK_DESCRIPTION, ' '))
                        .append(ISOUtil.padleft(report.getDate().trim(), branchMsg.TO_DATE, '0'))
                        .append(ISOUtil.padleft(report.getBranchCode().trim(), branchMsg.BRANCH_CODE, '0'))
                        .append(ISOUtil.padleft(report.getBlockRow().trim(), branchMsg.BLOCK_ROW, '0'))
                        .append(ISOUtil.padleft(report.getChnUser().trim(), branchMsg.CHN_USER, ' '));
            }
            return responseStr.toString();

        } catch (NotFoundException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.NO_BLCK_UNBLCK_FOR_THIS_ACCOUNT);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.NO_BLCK_UNBLCK_FOR_THIS_ACCOUNT));
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }

    }
}



