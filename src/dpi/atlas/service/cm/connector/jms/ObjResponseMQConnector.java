package dpi.atlas.service.cm.connector.jms;

import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.mq.mypool.*;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.JMSException;

/**
 * Created by IntelliJ IDEA.
 * User: Shahram Boroon
 * Date: Nov 5, 2005
 * Time: 8:35:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class ObjResponseMQConnector implements Connector, Configurable {
    private static Log log = LogFactory.getLog(ObjResponseMQConnector.class);


    MQSessionPool mqSessionPool = null;
    long timeToLive;

    public ObjResponseMQConnector() {

    }

    public void sendAsync(Object obj) throws Exception {
        CMMessage cmMessage;
        String msgSeq = null;
        MQSession session = null;

        try {
            cmMessage = (CMMessage) obj;
            Object MessageObj;
            session = mqSessionPool.getMQSession();
            String correlationID;
            if (cmMessage.hasAttribute("result"))
                MessageObj = cmMessage.getAttribute("result");
            else {
                MessageObj = cmMessage.getAttributeAsString("REQ-STR");
                log.debug(MessageObj);
            }

            if (cmMessage.getAttributeAsString("JMSCorrelationID") != null && !(cmMessage.getAttributeAsString("JMSCorrelationID")).equals(""))
                correlationID = cmMessage.getAttributeAsString("JMSCorrelationID");
            else
                correlationID = cmMessage.getAttributeAsString(Fields.SESSION_ID);

            session.sendObj(MessageObj, correlationID, timeToLive);

 //************************this is for test tb_log***********************************************************
//            msgSeq = cmMessage.getAttributeAsString(Constants.MESSAGE_SEQUENCE_FOR_tblog);
//            long currentTime = System.currentTimeMillis();
//            long totaltime=(currentTime - Long.parseLong(cmMessage.getAttributeAsString("timetest")));
//            ChannelFacadeNew.insertCMLog(msgSeq, "02",totaltime);
 //****************************************************************************************************

        } catch (JMSException je) {
            ChannelFacadeNew.insertCMLog(msgSeq, "03");
            log.error(je);
            Exception le = je.getLinkedException();
            if (le != null) log.error("linked exception: " + le);
            throw je;
        }
//        catch (SQLException sqlex) {
//            ChannelFacadeEbanking.insertCMLog(msgSeq, "05");
//            log.error(sqlex);
//            throw sqlex;
//
//        }
        catch (Exception e) {
            ChannelFacadeNew.insertCMLog(msgSeq, "04");
            throw e;
        } finally {
            mqSessionPool.freeMQSession(session);
        }
    }

    public void sendACHAsync(Object obj) throws Exception {       
    }

    public String sendSyncACHText(Object obj) throws JMSException, NoIdleSessionException, MQTimeoutException {
        return "";
    }

    public void sendASyncACHText(Object obj) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendASyncACHBytes(Object obj) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendASyncACHByte(Object obj) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        int pool_size;
        String queueManagerName;
        String sendQueue;
        long timeout;
        int priority;
        boolean ASYNC_MQ_POOL = false;


        try {
            queueManagerName = cfg.get("queue-manager");
            sendQueue = cfg.get("send-queue");
            pool_size = Integer.parseInt(cfg.get("pool-size"));
            priority = Integer.parseInt(cfg.get("priority"));
            timeout = Long.parseLong(cfg.get("timeout"));
            MQPoolConfig mqConfig = new MQPoolConfig(queueManagerName, sendQueue, pool_size, priority, timeout, ASYNC_MQ_POOL);
            mqSessionPool = new MQSessionPool(mqConfig);
        } catch (JMSException e) {
            log.error(e);
            if (e.getLinkedException() != null) {
                log.error(e.getLinkedException());
            }
            throw new ConfigurationException(e);
        } catch (Exception e) {
            log.error(e);
            throw new ConfigurationException(e);
        }
    }


    public void sendAsyncText(Object obj) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }    

    public Object sendSyncObject(Object obj) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object sendSyncText(Object obj) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendAsyncByte(Object obj) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object sendSyncByte(Object obj) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendAsyncObject(Object obj) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
