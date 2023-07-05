package branch.dpi.atlas.service.cm.handler.saf;

import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.source.CMSourceBase;
import branch.dpi.atlas.service.cm.handler.saf.SAFSourceMBean;
import dpi.atlas.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

/**
 * SAFSource class
 *
 * @author <a href="mailto:Behnaz@dpi.ir">SH Behnaz</a>
 * @version $Revision: 1.0 $ $Date: 2019/21/07 11:36:08 $
 */

public class SAFSource extends CMSourceBase implements SAFSourceMBean {
    private static Log log = LogFactory.getLog(SAFSource.class);
    long delay = 120000;
    SAF2Job saf2Job = new SAF2Job();

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        String str = cfg.get("delay");
        StringUtils.checkConfigParameter(str);
        delay = Long.parseLong(str);

        StringUtils.checkConfigParameter(cfg.get("host-agent"));
        StringUtils.checkConfigParameter(cfg.get("cfs-connector"));
        StringUtils.checkConfigParameter(cfg.get("max-try-count"));
        StringUtils.checkConfigParameter(cfg.get("pick-num"));
        StringUtils.checkConfigParameter(cfg.get("wait-time"));
        StringUtils.checkConfigParameter(cfg.get(Fields.ACTIONCODE_TO_BE_CONSIDERED));

        SAFThread safThread = new SAFThread(cfg);
        safThread.start();

    }

    public void setPickNum(int pickNum) {
        saf2Job.setPickNum(pickNum);
    }

    public int getPickNum() {
        return saf2Job.getPickNum();
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public long getDelay() {
        return this.delay;
    }

    private class SAFThread extends Thread {

        Configuration configuration;


        public SAFThread(Configuration configuration) {
            this.configuration = configuration;
        }

        public void run() {
            try {
                saf2Job.setConfiguration(configuration);
            } catch (ConfigurationException e) {
                e.printStackTrace();
            };

            super.run();

            while(ChannelFacadeNew.getDbConnectionPool()==null){   try {
                    sleep(delay);
                } catch (InterruptedException e) {
                    log.error(e);
                }
            }

            while (true) {
                try {
                    sleep(delay);
                } catch (InterruptedException e) {
                    log.error(e);
                }

                try {
                    log.info("Executing SAF2Job...");
                    saf2Job.doWork();
                } catch (Exception e) {
                    log.error(e);
                }
            }

        }
    }
}
