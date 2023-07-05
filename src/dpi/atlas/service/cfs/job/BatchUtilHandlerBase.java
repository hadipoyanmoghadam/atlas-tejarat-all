package dpi.atlas.service.cfs.job;

import dpi.atlas.calendar.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: Parisa Naeimi
 * Date: Jun 11, 2005
 * Time: 11:11:13 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BatchUtilHandlerBase implements BatchUtilHandler {
    private String year;
    private String month;
    private String day;

    private String hour;
    private String minute;
    private String second;

    private void setSystemDate() {
        Calendar cal = Calendar.getInstance();

        month = Integer.toString(cal.get(Calendar.MONTH) + 1).length() < 2 ?
                "0" + Integer.toString(cal.get(Calendar.MONTH) + 1) :
                Integer.toString(cal.get(Calendar.MONTH) + 1);
        day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH) + 1).length() < 2 ?
                "0" + Integer.toString(cal.get(Calendar.DAY_OF_MONTH) + 1) :
                Integer.toString(cal.get(Calendar.DAY_OF_MONTH) + 1);
        year = Integer.toString(cal.get(Calendar.YEAR));

        hour = Integer.toString(cal.get(Calendar.HOUR)).length() < 2 ?
                "0" + Integer.toString(cal.get(Calendar.HOUR)) :
                Integer.toString(cal.get(Calendar.HOUR));
        minute = Integer.toString(cal.get(Calendar.MINUTE)).length() < 2 ?
                "0" + Integer.toString(cal.get(Calendar.MINUTE)) :
                Integer.toString(cal.get(Calendar.MINUTE));
        second = Integer.toString(cal.get(Calendar.SECOND)).length() < 2 ?
                "0" + Integer.toString(cal.get(Calendar.SECOND)) :
                Integer.toString(cal.get(Calendar.SECOND));
    }

    protected String getSystemDateYYMMDD() {
        setSystemDate();
        return this.year + this.month + this.day;
    }

    protected String getSystemDateDDMM() {
        setSystemDate();
        return this.day + this.month;
    }

    protected String getSystemTime() {
        setSystemDate();
        return this.hour + this.minute + this.second;
    }

    protected String getBlankPart(int no_of_blanks) {
        String str_blank = "";
        for (int i = 0; i < no_of_blanks; i++) {
            str_blank += " ";
        }
        return str_blank;
    }
}
