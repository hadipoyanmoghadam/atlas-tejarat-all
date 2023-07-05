package branch.dpi.atlas.service.cm.handler.manzume;

import branch.dpi.atlas.service.cm.source.manzume.message.ManzumeMessage;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;


import java.sql.SQLException;
import java.util.Map;

/**
 * User: F.Heydari
 * Date: NOV 1 2022
 * Time: 10:37 AM
 */
public class CheckBranchCode extends TJServiceHandler implements Configurable {
    protected String accountField;

    public void doProcess(CMMessage msg, Map map) throws CMFault {

        String accountNo = "";
        String branchCode = "";

        ManzumeMessage manzumeMessage = (ManzumeMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        try {

            try {
                accountNo = ISOUtil.padleft(manzumeMessage.getAccountNo().trim(), 13, '0');
            } catch (ISOException e) {
                log.error("Can not zeropad accountNo = '" + accountNo + "' in CheckBranchCode : " + e.getMessage());
            }

            try {
                branchCode = ISOUtil.padleft(manzumeMessage.getBranchCode().trim(), 5, '0');
            } catch (ISOException e) {
                log.error("Can not zeropad branchCode = '" + branchCode + "' in CheckBranchCode : " + e.getMessage());
            }

            boolean checkBranchCode = ChannelFacadeNew.getBranchCode(Constants.MANZUME_CMPARAM_MODIFIRE, accountNo);

            if (checkBranchCode == true) {

                //get branchCode from cif  for primary person

                String cifBranchCode = ChannelFacadeNew.checkBranchCodeFromCif(accountNo);

                if (!branchCode.equals(cifBranchCode)) {
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_OPERATION);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_OPERATION));
                }

            } else {
                return;
            }

        } catch (CMFault e) {
            throw e;
        } catch (NotFoundException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside CheckBranchCode.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }


}
