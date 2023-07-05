package dpi.atlas.service.cm.imf;

import dpi.atlas.service.cm.InvalidMessageFormat;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.source.sparrow.message.SparrowMessage;
import org.jpos.iso.ISOMsg;

/**
 * ToIMFFormatterBase class
 * with Ahmadi
 *
 * @author <a href="mailto:A_H_Ahmadi@tyahoo.com">Amir hosein Ahmadi</a>
 * @version $Revision: 1.3 $ $Date: 2007/10/29 14:04:29 $
 */
public abstract class ToIMFFormatterBase implements ToIMFFormatter {
    protected CMCommand command = null;
    protected String message = null;


    public abstract CMCommand format(String message) throws InvalidMessageFormat;

    public abstract CMCommand format(ISOMsg isoMsg);

    public abstract CMCommand format(SparrowMessage message);

    public abstract SparrowMessage format(CMCommand message, SparrowMessage originalMessage);

}