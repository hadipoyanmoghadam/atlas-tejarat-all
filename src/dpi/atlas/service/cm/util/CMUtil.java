package dpi.atlas.service.cm.util;

import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.ib.format.FormatException;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: mb
 * Date: Aug 30, 2007
 * Time: 8:07:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class CMUtil {

    //1386/04/26 Boroon added for running CutOver,SGBFileReader & SGBFileApplier fby user request - START
    public static String HTTPSendRecieve(String sendStr, String serverIP, String messageResceiver) throws Exception {
        HttpClient client = new HttpClient();
// Live
        String url = "http://localhost:" + serverIP + "/";
// Test
//        String url = "http://localhost:8001/";
        System.out.println("url = " + url);
        PostMethod method = new PostMethod(url);
//            method.addRequestHeader("Content-Type", "text/html; charset=UTF-8");
//            method.setParameter("text", command.toString());

        NameValuePair[] data = {new NameValuePair("text", sendStr)};
        method.setRequestBody(data);
        client.executeMethod(method);
        System.out.println("command sent to " + messageResceiver + ": " + sendStr);
//            System.out.println("response charset:"+method.getResponseCharSet());
        byte[] responseBody = method.getResponseBody();
//        String responseStr = new String(responseBody, "UTF-8");
        String responseStr = new String(responseBody);
        System.out.println("response from CM: " + responseStr);
        return responseStr;
    }

    public static int wrapJob(String JobName, String JobCode, String SparrowState, String serverIP, String messageReceiver) {
        try {

            String response = CMUtil.HTTPSendRecieve("messageId::1//serviceType::PRG//"
                    + "time::" + DateUtil.getSystemTime()
                    + "|" + JobCode
                    + "|date::" + DateUtil.getSystemDate().substring(2)
                    + "NoFileDate", serverIP, messageReceiver);
            System.out.println("response = " + response);
            CMResultSet result = null;
            try {
                result = new CMResultSet(response);
            } catch (FormatException e) {
                System.out.println(JobName + " job ended with errors. Response = " + response);
//1386/05/22 Boroon changed to return action code to JCL - START
                return 8;
//1386/05/22 Boroon changed to return action code to JCL - END
            }
            String actionCode = result.getHeaderField(Fields.ACTION_CODE);
//1386/05/22 Boroon changed to return action code to JCL - START
            if (actionCode.equals(ActionCode.APPROVED)) {
                System.out.println(JobName + " job completed successfully");
                return 0;
            } else {
                System.out.println(JobName + " job ended with errors. Action Code = " + actionCode);
                return 8;
            }
        } catch (Exception e) {
            System.out.println(JobName + " job ended with errors.");
            e.toString();

//1386/05/22 Boroon added to return action code to JCL - START
            return 8;
//1386/05/22 Boroon added to return action code to JCL - END
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

    public static boolean checkCase(String key, String value) {
        StringTokenizer tokenizer = new StringTokenizer(value, ",");
        boolean existanceFlag = false;
        while (tokenizer.hasMoreTokens()) {
            String val = tokenizer.nextToken().trim();
            if (val.equalsIgnoreCase(key)) {
                existanceFlag = true;
                break;
            }
        }
        return existanceFlag;
    }

    public static boolean isTejaratBin(String bin) {
        if (bin.equalsIgnoreCase(Constants.BANKE_TEJARAT_BIN) || bin.equalsIgnoreCase(Constants.BANKE_TEJARAT_BIN_NEW))
            return true;
        else
            return false;
    }


}
//1386/04/26 Boroon added for running CutOver,SGBFileReader & SGBFileApplier fby user request - END }
