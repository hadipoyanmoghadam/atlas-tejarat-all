package dpi.atlas.model.tj.facade;

/**
 * Created by IntelliJ IDEA.
 * User: dp-admin
 * Date: Aug 25, 2009
 * Time: 7:23:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class DBConnectionPoolException extends Exception {

    public DBConnectionPoolException() {
    }

    public DBConnectionPoolException(String message) {
        super(message);
    }

    public DBConnectionPoolException(Throwable cause) {
        super(cause);
    }

    public DBConnectionPoolException(String message, Throwable cause) {
        super(message, cause);
    }
}
