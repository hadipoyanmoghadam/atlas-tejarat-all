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
 * Time: 11:33:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class HeaderHandler extends BatchUtilHandlerBase {

    private static Log log = LogFactory.getLog(HeaderHandler.class);

    private Object[] headerRow;

    public HeaderHandler(Object[] row) {
        this.headerRow = row;
    }

    public CMCommand process() throws CFSFault {
        //To change body of implemented methods use File | Settings | File Templates.
        CMCommand command = new CMCommand();
        command.setCommandName(TJCommand.CMD_HEADER_RECORD);

        command.addHeaderParam(Fields.MESSAGE_ID, TJCommand.OUTGOING);
        command.addHeaderParam(Fields.SERVICE_ID, TJCommand.SERVICE_CFS);

        command.addParam(Fields.BRANCH_CODE, ((Long) this.headerRow[0]).toString());
        command.addParam(Fields.Transaction_Code, (String) this.headerRow[1]);
        command.addParam(Fields.AMOUNT, ((Long) this.headerRow[2]).toString());
        command.addParam(Fields.Today_Date, getSystemDateDDMM());

        return command;
    }
}
