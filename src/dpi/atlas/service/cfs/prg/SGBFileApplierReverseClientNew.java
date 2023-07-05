package dpi.atlas.service.cfs.prg;

import dpi.atlas.service.cfs.util.CFSUtil;

import java.util.Properties;
import java.io.FileInputStream;

/**
 * User: Shahram Boroon
 * Date: Jul 8, 2007
 * Time: 11:22:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class SGBFileApplierReverseClientNew {
    public static void main(String[] args) throws Exception {
        String serverIp = "localhost";
        if (args.length > 0)
            serverIp = args[0];
        String commitSize="1000";
        if (args.length > 1 )
           commitSize=args[1];
        Properties properties = new Properties();
        properties.load(new FileInputStream("CFSClientPort.properties"));
        String CFS_PORT = properties.getProperty("CFS_PORT");
        System.out.println("CFS_PORT = " + CFS_PORT);
        System.exit(CFSUtil.wrapJob("SGB File Applier Reverse", "SGBFARN", "", serverIp, CFS_PORT, "CFS", "",commitSize));
    }
}
