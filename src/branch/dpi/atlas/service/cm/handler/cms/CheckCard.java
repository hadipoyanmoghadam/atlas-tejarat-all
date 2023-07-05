package branch.dpi.atlas.service.cm.handler.cms;

import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.util.CMUtil;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by SH.Behnaz  on 11/14/16.
 */
public class CheckCard extends CMHandlerBase implements Configurable {

    ArrayList Checked_array = new ArrayList();

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        String messageType = msg.getAttributeAsString(Fields.MESSAGE_TYPE);
        if (!Checked_array.contains(messageType))
            return;

        try {

            if (!ChannelFacadeNew.ExistCardInDB(msg.getAttributeAsString(Fields.PAN))) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_CARD_NUMBER);
                throw new CMFault(CMFault.FAULT_INTERNAL, ActionCode.INVALID_CARD_NUMBER);
            }

        } catch (SQLException e) {
            log.error(e);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, ActionCode.DATABASE_ERROR);
        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        String Checked = cfg.get(Fields.CHECKED);
        Checked_array = CMUtil.tokenizString(Checked, ",");

    }
}



