package dpi.atlas.service.cm.prg;

import dpi.atlas.service.cfs.util.CFSUtil;

import java.util.Properties;
import java.io.FileInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Shahram Boroon
 * Date: Jul 8, 2007
 * Time: 11:24:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class CutOverClient {
    public static void main(String[] args) throws Exception {
//1386/05/10 Boroon changed to provide reversing a SGB file facility - START
//1386/05/22 Boroon changed to return action code to JCL - START
//        CFSUtil.wrapJob("CutOver", "CUTOVR", null);
        String serverIp = "localhost";
        if (args.length > 0)
            serverIp = args[0];
        Properties properties = new Properties();
        properties.load(new FileInputStream("CMClientPort.properties"));
        String CM_PORT = properties.getProperty("CM_PORT");
        System.out.println("CM_PORT = " + CM_PORT);

        System.exit(CFSUtil.wrapJob("CutOver", "CUTOVR", null, serverIp, CM_PORT, "CM", ""));
//        System.exit(CFSUtil.wrapJob("CutOver", "CUTOVR", null, CFSConstants.CM_PORT, "CFS",""));
//1386/05/22 Boroon changed to return action code to JCL - END
//        CFSUtil.wrapJob("CutOver", "CUTOVR");
//1386/05/10 Boroon changed to provide reversing a SGB file facility - END
    }
}



