package branch.dpi.atlas.service.cm.handler.block;

import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.source.CMSourceBase;
import dpi.atlas.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

/**
 * User: F.Heydari
 * Date: Jun 9, 2020
 * Time: 10:17 AM
 */
public class BlockSource extends CMSourceBase implements BlockSourceBean {
    private static Log log = LogFactory.getLog(BlockSource.class);

    long delay=0;
    BlockJob blockJob = new BlockJob();


    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        String str = cfg.get("delay");
        StringUtils.checkConfigParameter(str);
        delay = Long.parseLong(str);

        StringUtils.checkConfigParameter(cfg.get("host-agent"));
        StringUtils.checkConfigParameter(cfg.get("cfs-connector"));
        StringUtils.checkConfigParameter(cfg.get("pick-num"));

        BlockThread blockThread = new BlockThread(cfg);
        blockThread.start();
    }

    public void setPickNum(int pickNum) {
        blockJob.setPickNum(pickNum);
    }

    public int getPickNum() {
        return blockJob.getPickNum();
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public long getDelay() {
        return this.delay;
    }

    private class BlockThread extends Thread {

        Configuration configuration;

        public BlockThread(Configuration configuration) {
            this.configuration = configuration;
        }

        public void run() {
            try {
                blockJob.setConfiguration(configuration);
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
            ;

            super.run();

            while (ChannelFacadeNew.getDbConnectionPool() == null) {
                try {
                    sleep(delay);
                } catch (InterruptedException e) {
                    log.error("###Error in BlockSource::"+e);
                }
            }

            while (true) {
                try {
                    sleep(delay);
                } catch (InterruptedException e) {
                    log.error("Error in BlockSource::"+e);
                }

                try {
                    log.info("Executing Block Scheduler...");

                    blockJob.doWork();
                } catch (Exception e) {
                    log.error("Error in BlockSource!!:::"+e);
                }
            }

        }
    }


}
