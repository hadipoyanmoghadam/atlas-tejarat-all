package dpi.atlas.service.cm.imf;

import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.source.sparrow.message.SparrowMessage;
import org.jpos.iso.ISOMsg;

/**
 * @author <a href="mailto:rampalo@yahoo.com">Ramin Abdollahi</a>
 * @version $Revision: 1.4 $ $Date: 2007/10/29 14:04:29 $
 */

public interface ToIMFFormatter {
    public CMCommand format(SparrowMessage message);

    public CMCommand format(String message);

    public CMCommand format(ISOMsg isoMsg);

    public SparrowMessage format(CMCommand message, SparrowMessage originalMessage);
}
