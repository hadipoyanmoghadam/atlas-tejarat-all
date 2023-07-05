package dpi.atlas.service.cm.prg;

import dpi.atlas.service.cm.util.CMUtil;

/**
 * Created by IntelliJ IDEA.
 * User: mb
 * Date: Sep 2, 2007
 * Time: 11:49:04 AM
 * To change this template use File | Settings | File Templates.
 */

public class CheckSparrowStateClient {
    public static void main(String[] args) throws Exception {
        System.exit(CMUtil.wrapJob("Change state of sparrow(s)", "CHKSPWST", null, "8003", "CM"));
    }

}
