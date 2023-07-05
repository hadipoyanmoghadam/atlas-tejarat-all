package dpi.atlas.service.cm.connector;

import dpi.atlas.util.mq.mypool.MQTimeoutException;
import dpi.atlas.util.mq.mypool.NoIdleSessionException;

import javax.jms.JMSException;

/**
 * Connector interface
 * 
 * @author <a href="mailto:alireza@taherkordi.com">Alireza Taherkordi</a>
 * @version $Revision: 1.1.1.1 $ $Date: 2005/05/07 12:08:30 $
 */
public interface Connector {
    public void sendAsync(Object obj) throws Exception;
    
    public void sendAsyncText(Object obj) throws Exception;

    public Object sendSyncText(Object obj) throws Exception;

    public void sendAsyncByte(Object obj) throws Exception;
    public Object sendSyncByte(Object obj) throws Exception;

    public void sendAsyncObject(Object obj) throws Exception;
    public Object sendSyncObject(Object obj) throws Exception;
}
