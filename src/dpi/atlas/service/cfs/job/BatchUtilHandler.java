package dpi.atlas.service.cfs.job;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.ib.format.CMCommand;


/**
 * Created by IntelliJ IDEA.
 * User: Parisa Naeimi
 * Date: Jun 1, 2005
 * Time: 11:30:03 PM
 * To change this template use File | Settings | File Templates.
 */
public interface BatchUtilHandler {
    public CMCommand process() throws CMFault;
}
