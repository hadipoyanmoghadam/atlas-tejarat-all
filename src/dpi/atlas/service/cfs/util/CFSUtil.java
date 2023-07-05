package dpi.atlas.service.cfs.util;

import dpi.atlas.model.ModelException;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.AccountRange;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.ib.format.FormatException;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.DateUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class CFSUtil {
    private static Log log = LogFactory.getLog(CFSUtil.class);

    public static String HTTPSendRecieve(String sendStr, String serverIp, String serverPort, String messageResceiver, String CodePage) throws Exception {
        HttpClient client = new HttpClient();
        String url = "http://" + serverIp + ":" + serverPort + "/";
        System.out.println("url = " + url);
        PostMethod method = new PostMethod(url);

        NameValuePair[] data = {new NameValuePair("text", sendStr)};
        method.setRequestBody(data);
        client.executeMethod(method);
        System.out.println("command sent to " + messageResceiver + ": " + sendStr);
        byte[] responseBody = method.getResponseBody();
        String responseStr;
        if ((CodePage == null) || (CodePage.trim().equals(""))) {
            responseStr = new String(responseBody);
        } else {
            responseStr = new String(responseBody, CodePage);//"8859_1"
        }

        System.out.println("response from CFS: " + responseStr);
        return responseStr;
    }

    public static int wrapJob(String JobName, String JobCode, String fileDate, String serverIp, String serverPort, String messageReceiver, String codePage) {
        try {
            String fileDateStr = "";            
            if (fileDate != null)
                fileDateStr = "//" + Fields.SGB_FILE_DATE + "::" + fileDate;
            String response = CFSUtil.HTTPSendRecieve("messageId::1//serviceType::PRG//"
                    + "time::" + DateUtil.getSystemTime()
                    + "|" + JobCode
                    + "|date::" + DateUtil.getSystemDate().substring(2)
                    + fileDateStr, serverIp, serverPort, messageReceiver, codePage);
            System.out.println("response = " + response);
            CMResultSet result = null;
            try {

                result = new CMResultSet(response);
            } catch (FormatException e) {
                System.out.println(JobName + " job ended with errors. Response = " + response);
                return 8;
            }
            String actionCode = result.getHeaderField(Fields.ACTION_CODE);
            if (actionCode.equals(ActionCode.APPROVED)) {
                System.out.println(JobName + " job completed successfully");
                return 0;
            } else {
                System.out.println(JobName + " job ended with errors. Action Code = " + actionCode);
                return 8;
            }
        } catch (Exception e) {
            System.out.println(JobName + " job ended with errors.");
            e.printStackTrace();
            return 8;
        } 
    }
    public static int wrapJob(String JobName, String JobCode, String fileDate, String serverIp, String serverPort, String messageReceiver, String codePage,String  commitSize) {
        try {
            String fileDateStr = "";
            if (fileDate != null)
                fileDateStr = "//" + Fields.SGB_FILE_DATE + "::" + fileDate;
            String response = CFSUtil.HTTPSendRecieve("messageId::1//serviceType::PRG//"
                    + "time::" + DateUtil.getSystemTime()
                    + "//commitsize::" + commitSize
                    + "|" + JobCode
                    + "|date::" + DateUtil.getSystemDate().substring(2)
                    + fileDateStr, serverIp, serverPort, messageReceiver, codePage);
            System.out.println("response = " + response);
            CMResultSet result = null;
            try {

                result = new CMResultSet(response);
            } catch (FormatException e) {
                System.out.println(JobName + " job ended with errors. Response = " + response);
                return 8;
            }
            String actionCode = result.getHeaderField(Fields.ACTION_CODE);
            if (actionCode.equals(ActionCode.APPROVED)) {
                System.out.println(JobName + " job completed successfully");
                return 0;
            } else {
                System.out.println(JobName + " job ended with errors. Action Code = " + actionCode);
                return 8;
            }
        } catch (Exception e) {
            System.out.println(JobName + " job ended with errors.");
            e.printStackTrace();
            return 8;
        }
    }

    public static ArrayList tokenizString(String str,String delimiter) {
        ArrayList dataArray = new ArrayList();
        if(str!=null && !str.equals("")){
            StringTokenizer st = new StringTokenizer(str);
            while (st.hasMoreTokens()) {
                dataArray.add(st.nextToken(delimiter).trim());
            }
        }
        return dataArray;
    }

}
