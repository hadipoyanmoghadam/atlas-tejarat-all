package branch.dpi.atlas.service.cm.handler.pg;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import org.jpos.core.Configurable;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: March 24, 2020
 * Time: 04:43 PM
 */
public class GetWageInformation extends TJServiceHandler implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

        try {

            Map<Long, String> map = ChannelFacadeNew.fillCMParamMap("StockDeposit");

            if(map.size()==0){
                throw new NotFoundException("Channel can not find anu information about StockDeposit");
            }else {
                command.addParam(Fields.FEE_ACCOUNT, map.get(Long.valueOf(0)));
                command.addParam(Fields.FEE_AMOUNT, map.get(Long.valueOf(1)));
                msg.setAttribute(Fields.FEE_AMOUNT, map.get(Long.valueOf(1)));
                command.addParam(Fields.BRANCH_CODE, map.get(Long.valueOf(2)));
                msg.setAttribute(Fields.BRANCH_CODE, map.get(Long.valueOf(2)));
            }
        } catch (SQLException e) {
            log.error("Data Base error in get information about StockDeposit with modifier= StockDeposit from tbcmparams");
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.WAGE_INFORMATION_NOT_FOUND);
            msg.setAttribute(Params.ACTION_MESSAGE, ActionCodeMsg.WAGE_INFORMATION_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.WAGE_INFORMATION_NOT_FOUND));
        } catch (Exception e) {
            log.error("Exception ::" + e.getMessage());
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }
}
