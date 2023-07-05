package branch.dpi.atlas.service.cm.handler.pg.saf;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

/**
 * Created by Nasiri .
 * Date: April 3, 2022
 * Time: 04:02 PM
 */
public abstract class SAFLoggerBase extends TJServiceHandler implements Configurable {

     Integer waitTime = Integer.valueOf(Constants.DEFAULT_SAF_WAIT_TIME);
     public void doProcess(CMMessage msg, Map holder) throws CMFault {

     }

     public void setConfiguration(Configuration cfg) throws ConfigurationException {

        String safName;
        safName = cfg.get("safName");
        if (safName == null) safName = "TJ_STK_REV";
        if (cfg.get(Constants.WAIT_TIME) != null && !cfg.get(Constants.WAIT_TIME).equals(""))
            waitTime = Integer.valueOf(cfg.get(Constants.WAIT_TIME));
        else
            waitTime = Integer.valueOf(Constants.DEFAULT_SAF_WAIT_TIME);
    }

  protected  String getAttribute(CMMessage cmMessage, Map map, String key) {
        String str = (String) cmMessage.getAttribute(key);
        if (str == null)
            str = (String) map.get(key);
        return str;
    }

   protected  boolean checkCase(String key, String value) {
       return key != null && value.contains(key);
   }

}
