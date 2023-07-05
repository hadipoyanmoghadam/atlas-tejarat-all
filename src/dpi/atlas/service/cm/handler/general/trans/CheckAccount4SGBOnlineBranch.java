package dpi.atlas.service.cm.handler.general.trans;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.sql.*;
import java.util.Map;

/**
 * CheckAccount4SGBOnlineBranch class
 * with Ahmadi
 *
 * @author <a href="mailto:A_H_Ahmadi@tyahoo.com">Amir hosein Ahmadi</a>
 * @version $Revision: 1.8 $ $Date: 2007/10/29 14:04:28 $
 */
public class CheckAccount4SGBOnlineBranch extends TJServiceHandler {
    public void doProcess(CMMessage cmMessage, Map map) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside CheckAccount4SGBOnlineBranch:doProcess()");

        String host_id = (String) cmMessage.getAttribute(Constants.SRC_HOST_ID);

        String accountNo = (String) cmMessage.getAttribute(Fields.ACCOUNT_NO);

        try {
            accountNo = ISOUtil.zeropad(accountNo, 10);
        } catch (ISOException e) {
            e.toString();  //To change body of catch statement use File | Settings | File Templates.
        }


        try {
            Class.forName("hit.db2.Db2Driver").newInstance();
        } catch (java.lang.Exception ex) {
            log.error("Error : Loading Driver" + ex.getMessage());
        }

        try {
            Connection conn = DriverManager.getConnection("jdbc:db2://10.0.0.11:453;rdbname=ATLS", "atlas", "sw1");
//            Connection conn = DriverManager.getConnection("jdbc:db2://172.16.209.225:449;rdbname=TICALOC", "atlas", "switch2");
            Statement stmt = conn.createStatement();
            String branchCode = "";
            ResultSet resultSet = stmt.executeQuery("select * from BRANCHRANGE where range = substr('" + accountNo + "',1,5)");

            if (resultSet.next()) {
                branchCode = resultSet.getString(1);
            } else
                return;


            cmMessage.setAttribute(Fields.BRANCH_CODE, branchCode);
            branchCode = ISOUtil.zeroUnPad(branchCode);

            if (host_id != null)
                if (host_id.compareToIgnoreCase("2") != 0)//If account's Host_id <> SGB then return; else Throw new Exception 
                    return;

            resultSet = stmt.executeQuery("select * from IPBranch where BranchCode = '" + branchCode + "'");
            String ip = null;

            CMResultSet result = new CMResultSet();

            if (resultSet.next()) {
                ip = resultSet.getString(2);
            } else
                return;
            /*{
                log.error("Account '" + accountNo + "' has BranchCode with id = '" + branchCode + "'");
                result.setHeaderField(Params.ACTION_CODE, ActionCode.GENERAL_DATA_ERROR);
                result.setHeaderField(Params.ACTION_MESSAGE, "Account '" + accountNo + "' has BranchCode with id = '" + branchCode + "'");
                cmMessage.setAttribute(CMMessage.RESPONSE_STRING, result.toString());
                throw new CMFault(CMFault.FAULT_INTERNAL, "Account '" + accountNo + "' has BranchCode with id = '" + branchCode + "'");
            }*/

            conn.close();

            result.setHeaderField(Params.ACTION_CODE, ActionCode.ACCOUNT_IS_IN_SGB_ONLINE_BRANCH);
            result.setHeaderField(Params.ACTION_MESSAGE, "Account is in SGB Online Branch");
            result.setHeaderField("IP", ip);
            cmMessage.setAttribute(CMMessage.RESPONSE, result);
            cmMessage.setAttribute(CMMessage.RESPONSE_STRING, result.toString());
            throw new CMFault(CMFault.FAULT_AUTH_SERVER, new Exception(ActionCode.ACCOUNT_IS_IN_SGB_ONLINE_BRANCH));

        } catch (SQLException e) {
            log.error(e);
        }

    }
}
