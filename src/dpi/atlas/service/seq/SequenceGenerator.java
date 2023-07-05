package dpi.atlas.service.seq;

import dpi.atlas.model.facade.common.CommonFacade;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Hashtable;

/**
 * SequenceGenerator class
 * with Ahmadi
 *
 * @author <a href="mailto:A_H_Ahmadi@tyahoo.com">Amir hosein Ahmadi</a>
 * @version $Revision: 1.14 $ $Date: 2007/11/01 08:26:28 $
 */
public class SequenceGenerator {
    private static Log log = LogFactory.getLog(SequenceGenerator.class);
    private static SequenceGenerator instance;

    private Hashtable _entries;
    private int _blocksize = 1000;

    CommonFacade commonFacade = null;


    private SequenceGenerator() {
        _entries = new Hashtable();
    }

    public synchronized static SequenceGenerator getInstance() {
        if (instance == null)
            instance = new SequenceGenerator();
        return instance;
    }




    public synchronized long getNextNumberInSequence(String name) {
        Entry entry = (Entry) _entries.get(name);
        if (entry == null) {
            entry = new Entry();
            entry.last = 0;
            _entries.put(name, entry);
        }

        synchronized (entry) {
            if (entry.last % _blocksize == 0) {

                long newSequence = 0;
                try {
                    newSequence = ChannelFacadeNew.findAndUpdateSequence(name, _blocksize);
                } catch (Exception e) {
                    throw new RuntimeException(":::Inside SequenceGenerator.getNextNumberInSequence >> ERROR : " + e.getMessage()); //todo: throw it
                }
                entry.last = newSequence;
            }

            return entry.last++;
        }
    }

    private class Entry {
        long last;
    }


}

