package dpi.atlas.model.tj;

import dpi.atlas.service.cm.CMFault;


/**
 * Created by IntelliJ IDEA.
 * User: Shahram Boroon
 * Date: Aug 14, 2005
 * Time: 9:13:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class BusinessException extends RuntimeException {
    private String faultCode;
    Exception nested = null;

    /**
     * Constructs an <code>HSMException</code> with no detail message.
     */
    public BusinessException(String faultCode, String s) {
        super(s);
        this.faultCode = faultCode;
    }

    /**
     * Constructs an <code>HSMException</code> with no detail message.
     */
    public BusinessException(String s) {
        this(CMFault.FAULT_INTERNAL, s);
    }

    /**
     * Constructs an <code>HSMException</code> with a nested
     * exception
     *
     * @param nested another exception
     */
    public BusinessException(String faultCode, Exception nested) {
        super(nested.toString());
        this.nested = nested;
        this.faultCode = faultCode;
    }

    public BusinessException(Exception nested) {
        super(nested.toString());
        this.nested = nested;
        this.faultCode = CMFault.FAULT_INTERNAL;
    }

    /**
     * @return nested exception (may be null)
     */
    public Exception getNested() {
        return nested;
    }

    public String getFaultCode() {
        return faultCode;
    }

//    public static CMFault makeFault(Exception e)
//    {
//        if (e instanceof CMFault) {
//            return (CMFault)e;
//        }
//
//        return new CMFault(FAULT_EXTERNAL_UNKNOWN, e);
//    }
//
//    public boolean isInternal()
//    {
//        return faultCode.startsWith(FAULT_INTERNAL);
//    }


    public String toString() {
        StringBuffer buf = new StringBuffer(super.toString());
        if (nested != null)
            buf.append(" (" + nested.toString() + ")");
        return buf.toString();
    }
}
