package dpi.atlas.util.mq.mypool;

/**
 * Created by IntelliJ IDEA.
 * User: dp-admin
 * Date: Aug 29, 2009
 * Time: 1:40:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class MQTimeoutException extends Exception{

    public MQTimeoutException() {
    }

    public MQTimeoutException(String message) {
        super(message);
    }

    public MQTimeoutException(Throwable cause) {
        super(cause);
    }

    public MQTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
