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
 * Time: 11:37:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class DetailHandler extends BatchUtilHandlerBase {
    private static Log log = LogFactory.getLog(DetailHandler.class);

    private Object[] detailRow;

    public DetailHandler(Object[] row) {
        this.detailRow = row;
    }

    public CMCommand process() throws CFSFault {
        //To change body of implemented methods use File | Settings | File Templates.
        CMCommand command = new CMCommand();
        command.setCommandName(TJCommand.CMD_DETAIL_RECORD);

        command.addHeaderParam(Fields.MESSAGE_ID, TJCommand.OUTGOING);
        command.addHeaderParam(Fields.SERVICE_ID, TJCommand.SERVICE_CFS);

        command.addParam(Fields.BRANCH_CODE, ((Long) detailRow[0]).toString());
        command.addParam(Fields.Transaction_Code, (String) detailRow[1]);
        command.addParam(Fields.Account_Amount, ((Long) detailRow[2]).toString());
        command.addParam(Fields.SRC_ACCOUNT, ((Long) detailRow[3]).toString());
        command.addParam(Fields.DEST_ACCOUNT, ((Long) detailRow[4]).toString());
        command.addParam(Fields.Effective_Date, (String) detailRow[5]);
        command.addParam(Fields.Document_No, ((Long) detailRow[6]).toString());
        command.addParam(Fields.Device_Code, ((Long) detailRow[7]).toString());

        return command;
    }
}
