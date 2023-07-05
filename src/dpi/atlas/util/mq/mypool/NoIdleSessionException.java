package dpi.atlas.util.mq.mypool;

/**
 * Created by IntelliJ IDEA.
 * User: dp-admin
 * Date: Aug 29, 2009
 * Time: 10:31:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class NoIdleSessionException extends Exception{

    public NoIdleSessionException() {
    }

    public NoIdleSessionException(String message) {
        super(message);
    }

    public NoIdleSessionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoIdleSessionException(Throwable cause) {
        super(cause);
    }
}
