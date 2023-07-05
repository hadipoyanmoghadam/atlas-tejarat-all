package dpi.atlas.service.cm.source.http.tj.sms;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandler;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.monitor.IntegrationServerMonitor;
import dpi.atlas.service.cm.source.CMSource;
import dpi.atlas.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * SMSServlet class
 * with Ahmadi
 *
 * @author <a href="mailto:A_H_Ahmadi@tyahoo.com">Amir hosein Ahmadi</a>
 * @version $Revision: 1.3 $ $Date: 2007/10/29 14:04:31 $
 */
public class SMSServlet extends HttpServlet {
    private static Log log = LogFactory.getLog(dpi.atlas.service.cm.source.http.sms.SMSServlet.class);

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
    }

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        doPost(httpServletRequest, httpServletResponse);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        IntegrationServerMonitor.getInstance().incSMSPullIncoming();
        ServletOutputStream out = res.getOutputStream();

        CMMessage msg = null;
        Map holder = null;
        try {
            CMHandler chainHandler = (CMHandler) getServletContext().getAttribute("chain-handler");


            CMCommand command = new CMCommand();

            String id = chainHandler.getChannelManagerEngine().getUUID();

            String service = req.getParameter("service");
            String request_id = req.getParameter("requestid");
            String account = req.getParameter("account");
            String trans_count = req.getParameter("count");

            msg = new CMMessage();

            msg.setMessageId(id);

            msg.setAttribute(CMMessage.SERVICE_TYPE, CMMessage.SERVICE_SMS);
            msg.setAttribute(CMMessage.OUTPUT_STREAM, out);
            msg.setAttribute(CMMessage.LOCALE, new Locale("en", "US"));

            msg.setAttribute(CMMessage.SERVER_USER, service);
            msg.setAttribute(CMMessage.REQUEST_TYPE, request_id);
            msg.setAttribute(CMMessage.ACCOUNT_NO, account);

            command.addHeaderParam(Fields.SESSION_ID, id);
            command.addHeaderParam(Fields.MESSAGE_ID, Constants.REQUEST);
            command.addHeaderParam(Fields.SERVICE_TYPE, Fields.SERVICE_SMS);

            command.setCommandName(service);
            command.addParam(Fields.MESSAGE_ID, request_id);
            command.addParam(Fields.ACCOUNT_NO, account);
            if (trans_count != null)
                command.addParam(Fields.TRANS_COUNT, trans_count);

            msg.setAttribute(CMMessage.REQUEST, command);

            holder = new HashMap();
            chainHandler.process(msg, holder);


            out.close();

            IntegrationServerMonitor.getInstance().incSMSPushOutgoing();

        } catch (Exception e) {
            log.error(e);
            CMFault fault = CMFault.makeFault(e);
            CMSource cmSource = (CMSource) getServletContext().getAttribute("cm-source");
            CMHandler faultHandler = cmSource.getFaultHandler(fault.getFaultCode());
            try {
                msg.setAttribute(CMMessage.FAULT, fault);
                msg.setAttribute(CMMessage.FAULT_CODE, fault.getFaultCode());
                faultHandler.process(msg, holder);
            } catch (CMFault cmFault) {
                log.error(cmFault);
            }
        }
    }
}
