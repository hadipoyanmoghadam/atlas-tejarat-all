package dpi.atlas.service.cm.handler.general.trans;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.imf.Fields;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

/**
 * SetServerID class
 * with Ahmadi
 *
 * @author <a href="mailto:A_H_Ahmadi@tyahoo.com">Amir hosein Ahmadi</a>
 * @version $Revision: 1.9 $ $Date: 2007/10/29 14:04:28 $
 */
public class SetServerID extends TJServiceHandler implements Configurable {
    String service_id = null;
    String service_pwd = null;

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside SetServerID:doProcess()");

        msg.setAttribute(Fields.SERVICE_ID, service_id);
        msg.setAttribute(Fields.SERVICE_PWD, service_pwd);//for DPI

    }

    public void setConfiguration(Configuration configuration) throws ConfigurationException {
//        super.setConfiguration(configuration);
        service_id = configuration.get("service-id");
        service_pwd = configuration.get("service-password");
    }

}
