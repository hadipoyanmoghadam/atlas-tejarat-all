package branch.dpi.atlas.service.cm.handler.exception;

import java.net.SocketException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 14, 2007
 * Time: 6:27:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class SocketConnectionException extends SocketException {
    public SocketConnectionException() {
        super();
    }

    public SocketConnectionException(String msg) {
        super(msg);
    }

}
