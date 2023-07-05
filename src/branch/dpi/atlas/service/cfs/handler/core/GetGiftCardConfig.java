package branch.dpi.atlas.service.cfs.handler.core;

import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.util.StringUtils;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

/**
 * @author <a href="mailto:Behnaz@dpi.ir">SH Behnaz</a>
 * @version $Revision: 1.6 $ $Date: 2011/10/30 14:09:50 $
 */

public class GetGiftCardConfig extends CFSHandlerBase implements Configurable {

    int bin=1;
    int pan=2;
    protected String soroshBin;
    protected String soroshPAN;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

/*
        Vector giftCard = CFSFacadeNew.getCFSParam("giftCardConfig");
        if (giftCard == null || giftCard.size() == 0  )
            throw new NotFoundException("giftCardConfig Not Found. ");


        Iterator it = giftCard.iterator();
        while (it.hasNext()) {
            Cmparam param = (Cmparam) it.next();
           if(param.getId().intValue()==bin)
            soroshBin=param.getDescription();
            else if(param.getId().intValue()==pan)
             soroshPAN=param.getDescription();


        }

  */
        msg.setAttribute(CFSConstants.SOROSH_BIN,soroshBin);
        msg.setAttribute(CFSConstants.SOROSH_PAN,soroshPAN);

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        StringUtils.checkConfigParameter(CFSConstants.SOROSH_BIN);
        StringUtils.checkConfigParameter(CFSConstants.SOROSH_PAN);
      soroshBin = cfg.get(CFSConstants.SOROSH_BIN);
      soroshPAN = cfg.get(CFSConstants.SOROSH_PAN);

    }

}
