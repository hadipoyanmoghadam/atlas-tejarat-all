package dpi.atlas.service.cm.source.jms;

import org.jpos.util.ThreadPool;

import javax.sql.DataSource;

import dpi.atlas.service.cm.connector.Connector;

/**
 * Created by IntelliJ IDEA.
 * User: sazegar (EBanking)
 * Date: Nov 20, 2007
 * Time: 10:13:53 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MQListner {
    void setPool(ThreadPool pool);

    void setThreadPoolMaxSize(int size);

    void setSecurityClassName(String securityClassName);

}
