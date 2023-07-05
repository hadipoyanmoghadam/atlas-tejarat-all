package dpi.atlas.service.cm.handler.general;

import dpi.atlas.model.ModelException;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.CMCounter;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.Calendar;

/**
 * Created by R.Nasiri
 * Date: Jun 28, 2022
 * Time: 01:05 PM
 */

public class RefNoGeneratorSharing {
    private static Log log = LogFactory.getLog(RefNoGeneratorSharing.class);

    private static RefNoGeneratorSharing instance;
    CMCounter cmCounter = null;

    RefNo refNo = null;

    private int _blocksize = 1000;

    public synchronized static RefNoGeneratorSharing getInstance() {
// todo: uncomment this...
        if (instance == null) {
            instance = new RefNoGeneratorSharing();
            instance.refNo = new RefNo();
        }
        return instance;
    }

    public String generateRefNo(String name) throws ModelException {
        Calendar calendar = Calendar.getInstance();
        int days = calendar.get(Calendar.DAY_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);

        String year_str = (year + "").substring(3);               //2->3
        String days_str = year_str + zeropad(days + "", 3);

        String out;
        try {
            synchronized (refNo) {
                if (refNo.getRefCounter() < 0)//initialization on system startup, first use...
                {
                    refNo.setRefCounter(ChannelFacadeNew.findAndUpdateCMCounter(name, days_str, _blocksize));
                    refNo.setDayOfYear(days_str);
                } else if (refNo.getRefCounter() % _blocksize == 0)//second use to end...
                    refNo.setRefCounter(ChannelFacadeNew.findAndUpdateCMCounter(name, days_str, _blocksize));

                if (!refNo.getDayOfYear().equalsIgnoreCase(days_str)) {
                    //to send handler in catch section
                    throw new NotFoundException("Not found any row with daystr = " + days_str);
                }
                String refCounter = "" + refNo.incrementAndGetRefCounter();
                out = name + refNo.getDayOfYear() + zeropad(refCounter, 8);  //7->8
            }
        } catch (NotFoundException e) {
            refNo.setRefCounter(0);
            refNo.setDayOfYear(days_str);
            String refCounter = "" + refNo.getRefCounter();
            out = name + refNo.getDayOfYear() + zeropad(refCounter, 8);    //7->8

            cmCounter = new CMCounter(name, days_str, refNo.getRefCounter());
            //start to insert new CMCounter...
            try {
                ChannelFacadeNew.insertCMCounterPlusBlockSize(cmCounter, _blocksize);
            } catch (SQLException e1) {
                try {
                    synchronized (refNo) {

                        refNo.setRefCounter(ChannelFacadeNew.findAndUpdateCMCounter(name, days_str, _blocksize));
                        refCounter = "" + refNo.incrementAndGetRefCounter();
                        out = name + refNo.getDayOfYear() + zeropad(refCounter, 8);  //7->8
                    }
                } catch (NotFoundException e2) {
                    log.error(e2);
                    throw new ModelException(e2.getMessage());
                } catch (SQLException e3) {
                    log.error(e3);
                    throw new ModelException(e3.getMessage());
                }
            }
        } catch (SQLException e) {
            throw new ModelException("DB has encountered an exception during fething data from tbCMCounter...");
        }

        return out;

    }

    private static String zeropad(String s, int i) {
        try {
            return ISOUtil.zeropad(s, i);
        } catch (ISOException e) {
            return s;
        }
    }


    private static class RefNo {
        long refCounter = -1;
        String dayOfYear = "000";


        public long getRefCounter() {
            return refCounter;
        }

        public void setRefCounter(long refCounter) {
            this.refCounter = refCounter;
        }

        public String getDayOfYear() {
            return dayOfYear;
        }

        public void setDayOfYear(String dayOfYear) {
            this.dayOfYear = dayOfYear;
        }

        public long incrementAndGetRefCounter() {
            return refCounter++;
        }

    }
}

