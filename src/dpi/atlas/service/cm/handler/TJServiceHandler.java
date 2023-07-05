package dpi.atlas.service.cm.handler;

import dpi.atlas.model.AtlasModel;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.UtilityFunctionBase;

/**
 * TJServiceHandler class
 *
 * @author <a href="mailto:alireza@taherkordi.com">Alireza Taherkordi</a>
 * @version $Revision: 1.5 $ $Date: 2007/10/29 14:04:30 $
 */
public abstract class TJServiceHandler extends CMHandlerBase {
    protected UtilityFunctionBase utilityCore;
    
    protected TJServiceHandler() {
        if (utilityCore == null) {
            utilityCore =new dpi.atlas.service.UtilityFunctions();

        }
        
    }

}
