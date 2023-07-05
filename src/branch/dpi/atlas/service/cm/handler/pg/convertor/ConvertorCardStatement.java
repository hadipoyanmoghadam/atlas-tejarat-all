package branch.dpi.atlas.service.cm.handler.pg.convertor;

import branch.dpi.atlas.service.cm.handler.pg.cardStatement.CardStatementReq;
import branch.dpi.atlas.service.cm.imf.cms.CMSToIMFFormater;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;

/**
 * User: SH.Behnaz
 * Date: MARCH 29, 2017
 * Time: 9:21:45 AM
 */
public class ConvertorCardStatement implements CMSToIMFFormater {

    public CMCommand format(Object o) {
        CardStatementReq statementMsg = (CardStatementReq) o;
        CMCommand command = new CMCommand();

        command.setCommandName(TJCommand.CMD_CARD_STATEMENT);
        command.addParam(Fields.FROM_DATE, statementMsg.getFromDate());
        command.addParam(Fields.TO_DATE, statementMsg.getToDate());
        command.addParam(Fields.PAN,statementMsg.getCardno());
        command.addParam(Fields.SRC_ACCOUNT,statementMsg.getAccountNo());
        command.addParam(Constants.SRC_HOST_ID, Constants.HOST_ID_CFS);
        command.addParam(Fields.TRANS_COUNT,Constants.MAX_TRANS_NO_IN_LIST);
        return command;
    }

}

