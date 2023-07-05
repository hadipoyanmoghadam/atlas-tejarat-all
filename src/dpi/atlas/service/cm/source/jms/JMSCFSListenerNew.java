package dpi.atlas.service.cm.source.jms;

import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.handler.CMHandler;
import dpi.atlas.service.cm.handler.CMHandlerAware;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.source.CMSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.util.ThreadPool;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class JMSCFSListenerNew implements MessageListener, CMHandlerAware, MQListner { // sazegar added for Mqlistener Implimentation (EBanking 86/09/10)
    private static Log log = LogFactory.getLog(JMSCFSListenerNew.class);
    static int counter = 0;

    private CMHandler handler;
    private CMSource cmSource;
    ThreadPool pool;
    private Connector connector;
    boolean useInternalConnectionPool = false;
    String securityClassName = null;
    private int threadPoolMaxSize;

    public void setSecurityClassName(String securityClassName) {
        this.securityClassName = securityClassName;
    }


    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    public void setThreadPoolMaxSize(int threadPoolMaxSize) {
        this.threadPoolMaxSize = threadPoolMaxSize;
    }

    public ThreadPool getPool() {
        return pool;
    }

    public void setPool(ThreadPool pool) {
        this.pool = pool;
    }

    public JMSCFSListenerNew() {
        if (log.isInfoEnabled()) log.info("JMSCFSListener()...");
    }

    public void setHandler(CMHandler handler) {
        if (log.isDebugEnabled()) log.debug("setHandler");
        this.handler = handler;
    }

    public CMHandler getChainHandler() {
        if (log.isDebugEnabled()) log.debug("getHandler");
        return handler;
    }

    public void setCMSource(CMSource cmSource) {
        if (log.isDebugEnabled()) log.debug("getHandler");
        this.cmSource = cmSource;
    }

    public void onMessage(Message message) {
        if (log.isDebugEnabled()) log.debug("onMessage: " + message);
        if (log.isInfoEnabled()) log.info("getIdleCount() = " + getIdleCount());
        if (log.isInfoEnabled()) log.info("getJobCount() = " + getJobCount());
        if (log.isInfoEnabled()) log.info("getMaxPoolSize() = " + getMaxPoolSize());
        if (log.isInfoEnabled()) log.info("getPendingCount() = " + getPendingCount());
        if (log.isInfoEnabled()) log.info("getPoolSize() = " + getPoolSize());

        if (counter++ % 1000 == 0) {
            log.fatal("ThreadPool available count: " + pool.getAvailableCount());
            log.fatal("ThreadPool active count: " + pool.activeCount());
            log.fatal("ThreadPool idle count: " + getIdleCount());
            log.fatal("ConnectionPool available count: " + CFSFacadeNew.getDbConnectionPool().getAvailableConnectionPoole());
            log.fatal("ConnectionPool active count: " + CFSFacadeNew.getDbConnectionPool().getActiveConnectionPoole());
            log.fatal("ConnectionPool max active count: " + CFSFacadeNew.getDbConnectionPool().getMaxActiveConnections());

        }

        if (pool.getIdleCount() == 0 && pool.activeCount() >= threadPoolMaxSize) {
            log.fatal("ThreadPool actives more than max size, Active Threads = " + pool.activeCount() + ", Idle Threads = " + pool.getIdleCount() + " , MQListener threw away the request.");
            return;
        }

        TextMessage textMessage = null;
        CMMessage msg = new CMMessage();
        HashMap holder = new HashMap();


        try {
            textMessage = (TextMessage) message;
            String text = textMessage.getText();
//            System.out.println(">>>>>> cm 2 cfs in cfs: "+ text +
//                    " date/Time: " + DateUtil.getSystemDate() + DateUtil.getSystemTime());
            if (log.isInfoEnabled()) log.info("message arrived : " + text);
            if (log.isDebugEnabled()) log.debug(message.getJMSCorrelationID());

            if (message.getJMSCorrelationID() != null && !message.getJMSCorrelationID().equals(""))
                msg.setAttribute("JMSCorrelationID", message.getJMSCorrelationID());

            if (log.isDebugEnabled()) log.debug("message.getJMSReplyTo() = " + message.getJMSReplyTo());

            if (message.getJMSReplyTo() != null) {
                msg.setAttribute("JMSReplyTo", message.getJMSReplyTo());
                if (log.isInfoEnabled()) log.info("JMSReplyTo = " + message.getJMSReplyTo());
            }

            CMCommand command = new CMCommand(text);

            msg.setMessageId(handler.getChannelManagerEngine().getUUID());
            msg.setAttribute(CMMessage.SERVICE_TYPE, "CFS");
            msg.setAttribute(CMMessage.REQUEST, command);
            msg.setAttribute(CMMessage.COMMAND, command.getCommandName());
            msg.setAttribute(CMMessage.REQUEST_STR, text);
            msg.setAttribute(CMMessage.LOCALE, new Locale("en", "US"));

            pool.execute(new Session(msg, holder));

        } catch (Exception e) {
            log.error(e);
        }
    }


    public int getJobCount() {
        return pool.getJobCount();
    }

    public int getPoolSize() {
        return pool.getPoolSize();
    }

    public int getMaxPoolSize() {
        return pool.getMaxPoolSize();
    }

    public int getIdleCount() {
        return pool.getIdleCount();
    }

    public int getPendingCount() {
        return pool.getPendingCount();
    }

    protected class Session implements Runnable {
        String realm;
        CMMessage msg;
        Map holder;


        public Session(CMMessage msg, Map holder) {
            this.msg = msg;
            this.holder = holder;
        }

        public void run() {
            try {

                getChainHandler().process(msg, holder);

            } catch (CMFault fault) {
                if (log.isInfoEnabled()) log.info("faultCode:" + fault.getFaultCode());

                CMHandler faultHandler = cmSource.getFaultHandler(fault.getFaultCode());

                if (log.isInfoEnabled()) log.info(faultHandler.getClass().getName());
                try {
                    msg.setAttribute(CMMessage.FAULT, fault);
                    msg.setAttribute(CMMessage.FAULT_CODE, fault.getFaultCode());

                    faultHandler.process(msg, holder);

                } catch (CMFault cmFault) {
                    log.error(cmFault);
                }
            }

        }
    }

}
