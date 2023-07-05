package dpi.atlas.service.cfs.job;

import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Parisa Naeimi
 * Date: Jun 1, 2005
 * Time: 11:38:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class TotalHandler extends BatchUtilHandlerBase {
    private static Log log = LogFactory.getLog(TotalHandler.class);

    private Object[] totalRow;

    public TotalHandler(Object[] row) {
        this.totalRow = row;
    }

    public CMCommand process() throws CFSFault {
        //To change body of implemented methods use File | Settings | File Templates.
        CMCommand command = new CMCommand();
        command.setCommandName(TJCommand.CMD_TOTAL_RECORD);

        command.addHeaderParam(Fields.MESSAGE_ID, TJCommand.OUTGOING);
        command.addHeaderParam(Fields.SERVICE_ID, TJCommand.SERVICE_CFS);

        command.addParam(Fields.BRANCH_CODE, ((Long) this.totalRow[0]).toString());
        command.addParam(Fields.Transaction_Code, (String) totalRow[1]);
        command.addParam(Fields.AMOUNT, ((Long) this.totalRow[2]).toString());
        command.addParam(Fields.Today_Date, getSystemDateDDMM());

        return command;
    }

}
