package dpi.atlas.service.cm.handler.Exception;

/**
 * Created by IntelliJ IDEA.
 * User: dp-admin
 * Date: Aug 25, 2009
 * Time: 3:25:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParameterValidationException extends Exception{


    public ParameterValidationException() {
    }

    public ParameterValidationException(String message) {
        super(message);
    }

    public ParameterValidationException(Throwable cause) {
        super(cause);
    }

    public ParameterValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
