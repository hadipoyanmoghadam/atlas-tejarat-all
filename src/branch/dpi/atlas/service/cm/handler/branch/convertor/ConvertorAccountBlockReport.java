package branch.dpi.atlas.service.cm.handler.branch.convertor;

import branch.dpi.atlas.model.tj.entity.BlockReport;
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

import java.util.Map;


/**
 * User: R.Nasiri
 * Date: April 5, 2015
 * Time: 3:23 PM
 */
public class ConvertorAccountBlockReport extends BranchBaseConvertor implements BranchToIMFFormater {

    public CMCommand format(BranchMessage branchMsg) {
        CMCommand command = super.format(branchMsg);
        return command;
    }

    public String createResponse(CMMessage msg, Map map) throws CMFault {
        BlockReport report = new BlockReport();
        try {
            if (map.containsKey(Constants.BLOCK_LIST)) {
                report = (BlockReport) map.get(Constants.BLOCK_LIST);
            }
            else
                throw new NotFoundException("Account Block Report does not exist");
            StringBuilder responseStr = new StringBuilder();
            BranchMessage branchMsg = (BranchMessage)msg.getAttribute(CMMessage.COMMAND_OBJ);
            responseStr.append(branchMsg.createResponseHeader());

                if(report.getBranchCode().length()>5)
                    report.setBranchCode(report.getBranchCode().substring(1,6));

                if(report.getDesc().trim().length()>50)
                    report.setDesc(report.getDesc().trim().substring(0,50));

            responseStr.append(ISOUtil.padleft(report.getDesc().trim(), branchMsg.BLOCK_DESCRIPTION, ' '))
                        .append(ISOUtil.padleft(report.getDate().trim(), branchMsg.TO_DATE, '0'))
                        .append(ISOUtil.padleft(report.getBranchCode().trim(), branchMsg.BRANCH_CODE, '0'))
                        .append(ISOUtil.padleft(report.getChnUser().trim(), branchMsg.CHN_USER, ' '));

            return responseStr.toString();

        } catch (NotFoundException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.ACCOUNT_ENABLED_BEFORE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_ENABLED_BEFORE));
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }

    }
}



