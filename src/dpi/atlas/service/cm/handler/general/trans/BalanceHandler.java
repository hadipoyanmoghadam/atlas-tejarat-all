package dpi.atlas.service.cm.handler.general.trans;

import dpi.atlas.core.ISOFields;
import dpi.atlas.isocode.ActionCode;
import dpi.atlas.isocode.ReasonCode;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.host.HostAgent;
import dpi.atlas.service.cm.host.HostException;
import dpi.atlas.service.cm.host.HostInterface;
import dpi.atlas.service.cm.host.HostResultSet;
import dpi.atlas.service.cm.host.globus.ofs.convertor.NumberConvertor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;

import java.util.Map;

/**
 * BalanceHandler class
 * with Ahmadi
 *
 * @author <a href="mailto:A_H_Ahmadi@tyahoo.com">Amir hosein Ahmadi</a>
 * @version $Revision: 1.7 $ $Date: 2007/10/29 14:04:28 $
 */
public class BalanceHandler extends CMHandlerBase implements Configurable {

    private static Log log = LogFactory.getLog(BalanceHandler.class);

    private HostInterface hostInterface;
    private HostAgent hostAgent;
/*
    private String user;
    private String pass;
*/

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside BalanceHandler:process()");

        hostInterface = hostAgent.getHostInterface();

        ISOMsg isoMsg = null;


        try {
            isoMsg = (ISOMsg) msg.getAttribute(CMMessage.REQUEST);

            if (!isoMsg.hasField(2)) {
                isoMsg.setResponseMTI();
//                Ahmadi comment this on 85-07-20
                // isoMsg.set(new ISOField(39, "902"));
                isoMsg.set(new ISOField(39, ActionCode.F900T999.INVALID_TRANSACTION));

            } else {
                String fromAccount = isoMsg.getString(2);

                HostResultSet hrs = hostInterface.getBalance("0000000", fromAccount, "CM", "000001", "", "");

                if (hrs.next()) {
                    String bal = hrs.getString("AVAILABLEBALANCE");
                    bal = NumberConvertor.convertFromISO(bal);
                    isoMsg.set(new ISOField(4, bal));
                }

                isoMsg.setResponseMTI();
                isoMsg.set(new ISOField(39, "000"));

                isoMsg.set(new ISOField(ISOFields.HOST_MODE, hostAgent.getHostModeAsString()));
            }

            holder.put("iso-res", isoMsg);
        } catch (HostException e) {
            log.error(e);

            try {

                isoMsg.setResponseMTI();


                if (e.getErrorCode() == HostException.CONNECTION_TIMEOUT)
                    isoMsg.set(new ISOField(39, ActionCode.F900T999.CARD_ISSUER_OR_SWITCH_INOPERATIVE));
                else if (e.getErrorCode() == HostException.HOST_UNAVAILABLE) {
                    isoMsg.set(new ISOField(25, ReasonCode.HOST_UNAVAILABLE));
                    isoMsg.set(new ISOField(39, ActionCode.F900T999.HOST_UNAVAILABLE));
                } else if (e.getErrorCode() == HostException.ACCOUNT_MISSING) {
                    isoMsg.set(new ISOField(39, ActionCode.F100T199.NO_ACCOUNT_OF_TYPE_REQUEST));
                } else
                    isoMsg.set(new ISOField(39, ActionCode.F900T999.HOST_UNAVAILABLE));
                //isoMsg.set(new ISOField(39, ActionCode.F900T999.SYSTEM_MALFUNCTION));
                holder.put("iso-res", isoMsg);
            } catch (ISOException e1) {
                throw new CMFault(CMFault.FAULT_INTERNAL, e.getMessage());
            }
            //throw new CMFault(CMFault.FAULT_EXTERNAL, e.getMessage());
        } catch (Exception e) {
            log.error(e);
            try {
                isoMsg.setResponseMTI();
                //isoMsg.set(new ISOField(39, ActionCode.F900T999.SYSTEM_MALFUNCTION));
                isoMsg.set(new ISOField(39, ActionCode.F900T999.HOST_UNAVAILABLE));
                holder.put("iso-res", isoMsg);
            } catch (ISOException e1) {
                throw new CMFault(CMFault.FAULT_INTERNAL, e.getMessage());
            }
            //throw new CMFault(e);
        }
    }


    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        String host_agent_name = cfg.get("host-agent");
        hostAgent = getChannelManagerEngine().getHostAgent(host_agent_name);
        if (hostAgent == null) {
            ConfigurationException ce = new ConfigurationException("No Host Agent found referred by 'host-agent' property, name:" + host_agent_name);
            log.error(ce);
            throw ce;
        }

/*
        user = cfg.get("nab-username");
        pass = cfg.get("nab-password");
*/

    }
}

