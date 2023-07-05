package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Nov 12, 2013
 * Time: 11:03:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class CheckBlockByBlockNo extends CFSHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(CheckBlockByBlockNo.class);
    protected String accountField;
    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String brokerId="";
        String proverId="";
        String serviceType = (String) msg.getAttribute(Fields.SERVICE_TYPE);
        if (serviceType.equals(Constants.SIMIN_SERVICE)) {
            brokerId = Constants.BROKER_ID_SIMIN;
            proverId = Constants.PROVIDER_ID_SIMIN;
        } else {
            brokerId = Constants.BROKER_ID_BRANCH;
            proverId = Constants.PROVIDER_ID_BRANCH;
        }

        String acc = msg.getAttributeAsString(accountField);
        String blockNo;
        try {
            acc = ISOUtil.zeropad(acc, 13);
            if (log.isDebugEnabled()) log.debug(accountField + "=" + acc);
        } catch (ISOException e) {
            log.error("Can not zeropad account number = '" + acc + "' in CheckBlockByBlockNo : " + e.getMessage());
        }
       try {
            blockNo = msg.getAttributeAsString(Fields.BLOCK_ROW);
            boolean isDuplicateBlckNo = CFSFacadeNew.findBlockNo4BM(blockNo,brokerId,proverId,acc);
            if (isDuplicateBlckNo) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.DUPLICATE_BLCK_NO);
                throw new CFSFault(CFSFault.DUPLICATE_BLCK_NO, new Exception(ActionCode.DUPLICATE_BLCK_NO));
            }
        } catch (SQLException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
        }
        msg.setAttribute(accountField, acc);
    }

    public CheckBlockByBlockNo() {
        accountField = Fields.SRC_ACCOUNT;
    }

    public void setAccountField(String accountField) {
        this.accountField = accountField;
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        accountField = cfg.get(CFSConstants.ACCOUNT_FIELD);
        if ((accountField == null) || (accountField.trim().equals(""))) {
            if (log.isInfoEnabled()) log.info("Account Field is not Specified, set to default value(srcAccount)");
            accountField = Fields.SRC_ACCOUNT;
        }
    }

}
