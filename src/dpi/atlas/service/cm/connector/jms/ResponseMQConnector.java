package dpi.atlas.service.cm.connector.jms;

import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.mq.mypool.*;
//import dpi.atlas.util.mq.mypool.PooledMQConnection;
import dpi.atlas.util.StringUtils;
import dpi.atlas.util.Constants;
import dpi.atlas.client.messages.BaseMessage;
import dpi.atlas.client.messages.ACHFundTransferMessage;
//import dpi.atlas.model.tj.facade.ChannelFacadeEbanking;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.ISOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * Created by IntelliJ IDEA.
 * User: Shahram Boroon
 * Date: Nov 5, 2005
 * Time: 8:35:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class ResponseMQConnector implements Connector, Configurable {
    private static Log log = LogFactory.getLog(ResponseMQConnector.class);
    private long timeout;
    MQSessionPool mqSessionPool = null;
    long timeToLive;

    public ResponseMQConnector() {
    }

    //TODO sendAsync/SyncText can be changed into more general format such as sendAsync/SyncObject
    public void sendAsync(Object obj) throws Exception {
        CMMessage cmMessage;
        String msgSeq = null;
        MQSession session = null;
        try {
            cmMessage = (CMMessage) obj;
            String MessageStr;
            session = mqSessionPool.getMQSession();
            String correlationID;
            if (cmMessage.hasAttribute("result"))
                if (cmMessage.getAttribute("result") instanceof BaseMessage)
                    MessageStr = ((BaseMessage) cmMessage.getAttribute("result")).getResponseXml();
                else
                    MessageStr = cmMessage.getAttributeAsString("result");

            else {
                MessageStr = cmMessage.getAttributeAsString("REQ-STR");
                log.debug(MessageStr);
            }

            if (cmMessage.getAttributeAsString("JMSCorrelationID") != null && !(cmMessage.getAttributeAsString("JMSCorrelationID")).equals(""))
                correlationID = cmMessage.getAttributeAsString("JMSCorrelationID");
            else
                correlationID = cmMessage.getAttributeAsString(Fields.SESSION_ID);
            session.send(MessageStr, correlationID, timeToLive);
        } catch (JMSException je) {
            log.error(je);
            Exception le = je.getLinkedException();
            if (le != null) log.error("linked exception: " + le);
            throw je;
        }
        catch (Exception e) {
            throw e;
        } finally {
            mqSessionPool.freeMQSession(session);
        }
    }

    public void sendAsyncText(Object obj) throws Exception {
        CMMessage cmMessage;
        MQSession session = null;
        try {
            cmMessage = (CMMessage) obj;
            String MessageStr;
            CMCommand cm_cmd = null; // TODO temporary
            String correlationID;
            session = mqSessionPool.getMQSession();
            if (cmMessage.hasAttribute("result")) {
                MessageStr = (String) cmMessage.getAttribute("result");
                CMResultSet result = new CMResultSet(MessageStr); // TODO temporary
                cm_cmd = new CMCommand(result.getRequest()); // TODO temporary
            } else {
                MessageStr = cmMessage.getAttributeAsString("REQ-STR");
                cm_cmd = (CMCommand) cmMessage.getAttribute("request"); // TODO temporary
                log.debug(MessageStr);
            }
            //  ************* // TODO temporary - Start
            if ((String) cm_cmd.getParam("JMSCorrelationID") != null && !((String) cm_cmd.getParam("JMSCorrelationID")).equals("")) // Sending response
                correlationID = (String) cm_cmd.getParam("JMSCorrelationID");
            else // Sending command
                correlationID = cm_cmd.getHeaderParam(Fields.SESSION_ID);
            //  ************* // TODO temporary - End
            session.send(MessageStr, correlationID, timeToLive);
        } catch (JMSException je) {
            log.error(je);
            Exception le = je.getLinkedException();
            if (le != null) log.error("linked exception: " + le);
            throw je;
        }
        catch (Exception e) {
            throw e;
        } finally {
            mqSessionPool.freeMQSession(session);
        }
    }

    public Object sendSyncText(Object obj) throws Exception {
        MQSession session = null;
        String reply = "";
        try {
            String correlationID;
            String MessageStr = null;
            CMCommand cmCommand = null; // TODO temporary
//TODO Stupid way !!!!!!!!!!!! m
            if (obj instanceof CMMessage) {
                CMMessage cmMessage = (CMMessage) obj;
                if (cmMessage.hasAttribute("result")) {
                    MessageStr = cmMessage.getAttributeAsString("result");
                    CMResultSet result = new CMResultSet((String) cmMessage.getAttribute("result")); // TODO temporary
                    cmCommand = new CMCommand(result.getRequest()); // TODO temporary
                } else {
                    cmCommand = (CMCommand) cmMessage.getAttribute("request"); // TODO temporary
                    if (cmMessage.hasAttribute("dsethostId"))
                        cmCommand.addParam(Constants.DEST_HOST_ID, cmMessage.getAttributeAsString(Constants.DEST_HOST_ID));
                    MessageStr = cmCommand.toString();
                    log.debug(MessageStr);
                }
            } else if (obj instanceof CMCommand) {
                //for saflog
                cmCommand = (CMCommand) obj;
                MessageStr = cmCommand.toString();
            }
            session = mqSessionPool.getMQSession();
//TODO Stupid way !!!!!!!!!!!! must be UNIFED
//TODO temporary - Start
            if ((String) cmCommand.getParam("JMSCorrelationID") != null && !((String) cmCommand.getParam("JMSCorrelationID")).equals("")) // Sending response
                correlationID = (String) cmCommand.getParam("JMSCorrelationID");
            else // Sending command
                correlationID = cmCommand.getHeaderParam(Fields.SESSION_ID);
// TODO temporary - End
            reply = session.sendAndReceive(MessageStr, correlationID, timeToLive);
            return reply;
        } catch (JMSException je) {
            log.error(je);
            Exception le = je.getLinkedException();
            if (le != null) log.error("linked exception: " + le);
            throw je;
        }
        catch (Exception e) {
            throw e;
        } finally {
            mqSessionPool.freeMQSession(session);
        }
    }

    public void sendAsyncByte(Object obj) throws Exception {
        MQSession session = null;
        try {
            CMMessage cmMessage = (CMMessage) obj;
            String response_str = "" + cmMessage.getAttribute(CMMessage.RESPONSE_STRING);
//  ************* // TODO temporary - Start
            String correlationId = "";
            if (cmMessage.hasAttribute("JMSCorrelationID"))
                correlationId = cmMessage.getAttributeAsString("JMSCorrelationID");
//  ************* // TODO temporary - End
            session = mqSessionPool.getMQSession();
            session.sendBytes(response_str.getBytes(), correlationId, 3 * timeout, timeToLive);
        } catch (JMSException je) {
            log.error(je);
            Exception le = je.getLinkedException();
            if (le != null) log.error("linked exception: " + le);
            throw je;
        }
        catch (Exception e) {
            throw e;
        } finally {
            mqSessionPool.freeMQSession(session);
        }
    }

    public Object sendSyncByte(Object obj) throws Exception {
        throw new UnsupportedOperationException("This operation has not been implemented!!!");
    }

    public void sendAsyncObject(Object obj) throws Exception {
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
            session.sendObj(cmMessage, correlationID, timeToLive);
        } catch (JMSException je) {
            log.error(je);
            Exception le = je.getLinkedException();
            if (le != null) log.error("linked exception: " + le);
            throw je;
        }
        catch (Exception e) {
            throw e;
        } finally {
            mqSessionPool.freeMQSession(session);
        }

    }

    public Object sendSyncObject(Object obj) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        int pool_size;
        String queueManagerName;
        String queueManagerHostName;
        int queueManagerPort;
        String sendQueue, receiveQueue, transType;
        int priority;
        boolean isSyncConnector = false;
        try {
            queueManagerName = cfg.get(Constants.QUEUE_MANAGER_NAME);
            StringUtils.checkConfigParameter(queueManagerName);
            queueManagerHostName = cfg.get(Constants.QUEUE_MANAGER_HOST_IP);
            StringUtils.checkConfigParameter(queueManagerHostName);
            sendQueue = cfg.get(Constants.SEND_QUEUE);
            StringUtils.checkConfigParameter(sendQueue);
            receiveQueue = cfg.get(Constants.RECIEVE_QUEUE);
            queueManagerPort = Integer.parseInt(cfg.get(Constants.QUEUE_MANAGER_HOST_PORT));
            StringUtils.checkConfigParameter(cfg.get(Constants.QUEUE_MANAGER_HOST_PORT));
            transType = cfg.get(Constants.QUEUE_MANAGER_TRANS_TYPE) != null ? cfg.get(Constants.QUEUE_MANAGER_TRANS_TYPE) : "";


            try {
                isSyncConnector = Boolean.valueOf(cfg.get("is-sync-connector")).booleanValue();
            } catch (Exception e) {
                log.error("Error in casting value of is-sync-connector into a boolean - error =  " + e);
                throw new ConfigurationException(e);
            }
            StringUtils.checkConfigParameter(cfg.get("pool-size"));
            pool_size = Integer.parseInt(cfg.get("pool-size"));
            priority = Integer.parseInt(cfg.get("priority"));
            timeout = Integer.parseInt((cfg.get("timeout").equalsIgnoreCase("") ? "30000" : cfg.get("timeout")));
            timeToLive = Long.parseLong((cfg.get("time-to-live").equalsIgnoreCase("") ? "0" : cfg.get("time-to-live")));

            MQPoolConfig mqConfig;
// local
//            if (transType.equals("LOCAL"))
//                mqConfig = new MQPoolConfig(queueManagerName, sendQueue, receiveQueue, pool_size, priority, timeout, isSyncConnector);
//            else
//remote
//            MQPoolConfig mqConfig = new MQPoolConfig(queueManagerHostName,"1","","MUSR_MQADMIN","SYSTEM.DEF.SVRCONN",queueManagerPort,sendQueue,timeout,pool_size,1, receiveQueue,queueManagerName,isSyncConnector);
                mqConfig = new MQPoolConfig(queueManagerHostName, "1", "", "MUSR_MQADMIN", "SEPAMSRV", queueManagerPort, sendQueue, timeout, pool_size, 1, receiveQueue, queueManagerName, isSyncConnector);

            mqSessionPool = new MQSessionPool(mqConfig);
        } catch (JMSException e) {
            log.error(e);
            if (e.getLinkedException() != null)
                log.error(e.getLinkedException());
            throw new ConfigurationException(e);
        } catch (Exception e) {
            log.error(e);
            throw new ConfigurationException(e);
        }
    }


}
