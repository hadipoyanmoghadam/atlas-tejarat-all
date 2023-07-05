package dpi.atlas.service.cm.source.jms;

import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandler;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jpos.iso.ISOUtil;
import org.xml.sax.InputSource;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * User: r.Nasiri
 * Date: Aug 09, 2020
 * Time: 02:47 PM
 */
public class PAYAMessageProcessor implements Runnable {
    private static Log log = LogFactory.getLog(PAYAMessageProcessor.class);

    Message message;
    CMMessage msg;
    Map holder;
    private CMHandler handler;


    public PAYAMessageProcessor(Message message, CMHandler handler) {
        this.message = message;
        this.handler = handler;
    }

    public void processMessage() throws Exception {
        msg = new CMMessage();
        holder = new HashMap();
        TextMessage textMessage;
        String msg_str = "";
        String statusx="1";
        String expCode="";
        String expDescription="";
        String cycleNo="0";
        String effectiveDate="";
        String refId="";

        try {
            if (message instanceof TextMessage) {
                textMessage = (TextMessage) message;
                msg_str = textMessage.getText();
                log.debug("message arrived : " + msg_str);
            }

            //Pars Message

                SAXBuilder builder = new SAXBuilder();
                 Document document = (Document) builder.build(new InputSource(new StringReader(msg_str)));
            Element rootNode = document.getRootElement();
            //**
            Attribute attribute = rootNode.getChild("Exceptions").getAttribute("count");
            String exceptionsCount=attribute.getValue();
            Attribute attributeResultCount = rootNode.getChild("Results").getAttribute("count");
            String resultsCount=attributeResultCount.getValue();

            if (exceptionsCount.equals("1")) {
                Element Exception = rootNode.getChild("Exceptions").getChild("Exception");

                expCode=Exception.getChildText("code");
                expDescription=Exception.getChildText("reason");

                refId=rootNode.getChild("Exceptions").getChildText("referenceID");
                effectiveDate= DateUtil.getSystemDate();
                statusx= Constants.PAYA_EXCEPTION;

            }else if(resultsCount.equals("1")) {

                Element issuedPaymentOrder = rootNode.getChild("Results").getChild("Object").getChild("issuedPaymentOrder");
                refId=issuedPaymentOrder.getChildText("referenceID");

                Element paymentOrder = issuedPaymentOrder.getChild("paymentOrder");
                effectiveDate=paymentOrder.getChildText("effectiveDate").replaceAll("-","");

                Element achCycle = paymentOrder.getChild("achCycle");
                cycleNo=achCycle.getChildText("cycleNumber");
            }else {
                throw new Exception("Message  is not valid");
            }

            if(refId!=null && refId.trim().length()<Constants.REF_ID_LEN)
                refId= ISOUtil.padleft(refId,Constants.REF_ID_LEN,'0');
            ChannelFacadeNew.UpdateAchStatus(statusx,expCode,expDescription,cycleNo,effectiveDate,refId);

        } catch (Exception e) {
            log.error("<<<<<message arrived >>>> " + msg_str);
            log.error(e.getMessage());
            throw e;
        }
    }

    public void run() {
        try {
            processMessage();
            getChainHandler().process(msg, holder);
        } catch (CMFault fault) {
                log.error(fault);
            log.error(message);
        } catch (Exception e) {
            log.error(e);
            log.error(message);
        } catch (Throwable e) {
            log.error(e);
            log.error(message);
        }
    }

    public CMHandler getChainHandler() {
        log.debug("getHandler");
        return handler;
    }

}
