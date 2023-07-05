package dpi.atlas.service.cfs.job;

import dpi.atlas.service.cm.source.CMSourceBase;
import dpi.atlas.service.cfs.job.refresh.RefreshJobMBean;
import dpi.atlas.service.cfs.job.refresh.RefreshJob;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Feb 2, 2008
 * Time: 11:38:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class RefreshBaseDataHandler extends CMSourceBase {
    private static final String REFRESH_TIME = "230000";

    String timeToBeingUpdated = REFRESH_TIME;
    private static Log log = LogFactory.getLog(RefreshBaseDataHandler.class);
    long delay = 120000;
    RefreshJob refreshJob = new RefreshJob();


    public void setConfiguration(Configuration configuration) throws ConfigurationException {
        //To change body of implemented methods use File | Settings | File Templates.
        String str = configuration.get("timeToBeingUpdated");
        if (str != null)
            timeToBeingUpdated = String.valueOf(Long.parseLong(str));

        SAFThread safThread = new SAFThread(configuration);
        safThread.start();
    }

    public void setRefreshTime(String timeToBeingUpdated) {
        this.timeToBeingUpdated = timeToBeingUpdated;
    }

    public String getRefreshTime() {
        return this.timeToBeingUpdated;
    }

private class SAFThread extends Thread {

        Configuration configuration;


        public SAFThread(Configuration configuration) {
            this.configuration = configuration;
        }

        public void run() {
        }
    }
}
