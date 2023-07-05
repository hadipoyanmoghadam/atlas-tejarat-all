package dpi.atlas.service.cm.source.admin;

import dpi.atlas.model.AtlasModel;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandler;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.source.CMSource;
import dpi.atlas.service.seq.SequenceGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * AdminServlet class
 * with Ahmadi
 *
 * @author <a href="mailto:A_H_Ahmadi@tyahoo.com">Amir hosein Ahmadi</a>
 * @version $Revision: 1.6 $ $Date: 2007/10/29 14:04:28 $
 */
public class AdminServlet extends HttpServlet {
    private static Log log = LogFactory.getLog(AdminServlet.class);


    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        if (log.isInfoEnabled()) log.info("inside AdminServlet::init()");
        int priority = Thread.NORM_PRIORITY;
        try {
            priority = ((Integer) getServletContext().getAttribute("priority")).intValue();
        } catch (Exception e) {
        }
        Thread.currentThread().setPriority(priority);
        if (log.isInfoEnabled()) log.info("priority:" + Thread.currentThread().getPriority());
    }

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        doPost(httpServletRequest, httpServletResponse);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        ServletOutputStream out = res.getOutputStream();

        CMMessage msg = null;
        Map holder = null;

        try {
            String text = req.getParameter("text");

            CMCommand command = new CMCommand(text);

            CMHandler chainHandler = (CMHandler) getServletContext().getAttribute("chain-handler");

            msg = new CMMessage();
            msg.setMessageId(chainHandler.getChannelManagerEngine().getUUID());
            msg.setAttribute(CMMessage.SERVICE_TYPE, "ADMIN");
            msg.setAttribute(CMMessage.REQUEST, command);
            msg.setAttribute(CMMessage.COMMAND, command.getCommandName());
            msg.setAttribute(CMMessage.REQUEST_STR, text);
            msg.setAttribute(CMMessage.OUTPUT_STREAM, out);
            msg.setAttribute(CMMessage.LOCALE, new Locale("en", "US"));

            holder = new HashMap();

            SequenceGenerator sg = SequenceGenerator.getInstance();
            long sequenceNumber = sg.getNextNumberInSequence("SessionId");

            msg.setAttribute(Fields.SESSION_ID, "A" + sequenceNumber);
            command.addHeaderParam(Fields.SESSION_ID, "A" + sequenceNumber);

            chainHandler.process(msg, holder);

        } catch (CMFault fault) {

            CMSource cmSource = (CMSource) getServletContext().getAttribute("cm-source");
            CMHandler faultHandler = cmSource.getFaultHandler(fault.getFaultCode());

            try {
                msg.setAttribute(CMMessage.FAULT, fault);
                msg.setAttribute(CMMessage.FAULT_CODE, fault.getFaultCode());
                faultHandler.process(msg, holder);
            } catch (CMFault cmFault) {
                cmFault.printStackTrace();
            }
        } catch (Exception e) {
            log.error(e);
        }
    }


}

