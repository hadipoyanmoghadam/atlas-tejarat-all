package dpi.atlas.util.ThreadPool;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Sep 25, 2009
 * Time: 8:52:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class NoIdleThreadException extends Exception{

    public NoIdleThreadException() {
    }

    public NoIdleThreadException(String message) {
        super(message);
    }

    public NoIdleThreadException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoIdleThreadException(Throwable cause) {
        super(cause);
    }
}
