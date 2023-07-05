package dpi.atlas.util;

import dpi.atlas.calendar.Calendar;
import dpi.atlas.service.cfs.common.CFSConstants;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Shahram Boroon
 * Date: Aug 7, 2005
 * Time: 8:48:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class DateUtil {
    public static String getSystemDate() {
        Calendar cal = Calendar.getInstance();

        String strMonth = Integer.toString(cal.get(Calendar.MONTH) + 1).length() < 2 ?
                "0" + Integer.toString(cal.get(Calendar.MONTH) + 1) :
                Integer.toString(cal.get(Calendar.MONTH) + 1);
        String strDay = Integer.toString(cal.get(Calendar.DAY_OF_MONTH)).length() < 2 ?
                "0" + Integer.toString(cal.get(Calendar.DAY_OF_MONTH)) :
                Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
        String strDate = Integer.toString(cal.get(Calendar.YEAR)) +
                strMonth +
                strDay;

        return strDate;
    }

    public static String getSystemTime() {
        Calendar cal = Calendar.getInstance();

        int hour = cal.get(Calendar.HOUR);
        if (cal.get(Calendar.AM_PM) != Calendar.AM)
            hour += 12;

        String strHour = Integer.toString(hour).length() < 2 ?
                "0" + Integer.toString(hour) :
                Integer.toString(hour);
        String strMinute = Integer.toString(cal.get(Calendar.MINUTE)).length() < 2 ?
                "0" + Integer.toString(cal.get(Calendar.MINUTE)) :
                Integer.toString(cal.get(Calendar.MINUTE));
        String strSecond = Integer.toString(cal.get(Calendar.SECOND)).length() < 2 ?
                "0" + Integer.toString(cal.get(Calendar.SECOND)) :
                Integer.toString(cal.get(Calendar.SECOND));
        String strTime = strHour + strMinute + strSecond;

        return strTime;
    }

    public static String getSystemDateDDMM() {
        Calendar cal = Calendar.getInstance();

        String strMonth = Integer.toString(cal.get(Calendar.MONTH) + 1).length() < 2 ?
                "0" + Integer.toString(cal.get(Calendar.MONTH) + 1) :
                Integer.toString(cal.get(Calendar.MONTH) + 1);
        String strDay = Integer.toString(cal.get(Calendar.DAY_OF_MONTH) + 1).length() < 2 ?
                "0" + Integer.toString(cal.get(Calendar.DAY_OF_MONTH) + 1) :
                Integer.toString(cal.get(Calendar.DAY_OF_MONTH) + 1);
        String strDate = strDay +
                strMonth;

        return strDate;
    }

    public static boolean checkDateFormat(String date) {
        short year = Short.parseShort(date.substring(0, 4));
        short month = Short.parseShort(date.substring(4, 6));
        short day = Short.parseShort(date.substring(6, 8));
        int currentYear = Calendar.getInstance().get(java.util.Calendar.YEAR);
        boolean flag = true;
        if ((day < 1) || (month < 1 || month > 12) || (month < 6 && day > 31) || (month >6 && day > 30))
          flag = false;
        if (currentYear == year || currentYear == year+1 || currentYear == year-1)
          flag = true;
        else
          flag = false;
        return flag;
    }

    public static DateFields getPersianDate(int month, int day)
    {
        int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        DateFields df = PersianCalendar.getPesianDate(year, month, day);
        return df;
    }

    /**
     * This method get month and day in a 4 length String parameter in Miladi format and generate current date
     * in String format of yymmdd
     * @param mmdd :  month as mm and day as dd in Miladi  format
     * @return yymmdd : yy: year, mm: month, dd: day in Persian format
     */
    public static String getPersianDate(String mmdd)
    {
        int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        int month = Integer.parseInt(mmdd.substring(0,2));
        int day = Integer.parseInt(mmdd.substring(2,4));
        DateFields df = PersianCalendar.getPesianDate(year, month, day);
        String fullDate = df.toString();
        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf(fullDate.substring(2,4))).
                append(fullDate.substring(5,7)).
                append(fullDate.substring(8,10));
        return sb.toString();
    }
    public static String calculateDate_old(String date, int period) throws ISOException {
        int year = Short.parseShort(date.substring(0, 2));
        int month = Short.parseShort(date.substring(2, 4));
        int day = Short.parseShort(date.substring(4, 6));

        if (period >= 365)
            year = year + period / 365;

        period = period % 365;
        if (period >= 30) {
            month = month + period / 30;
            if (month > 12) {
                month = month - 12;
                year++;
            }
        }
        day = day + period % 30;
        if (day > 30) {
            day = day - 30;
            month++;
            if (month > 12) {
                month = month - 12;
                year++;
            }
        }
        if(year>99){
            year=year - 100;
        }
        date = ISOUtil.zeropad(String.valueOf(year),2) + ISOUtil.zeropad(String.valueOf(month), 2) + ISOUtil.zeropad(String.valueOf(day), 2);
        return date;
    }


    public static String calculateDate(String date, int period)  throws ISOException{
        int year = Short.parseShort(date.substring(0, 2));
        int month = Short.parseShort(date.substring(2, 4)) - 1;
        int day = Short.parseShort(date.substring(4, 6));
        JalaliCalendar jalaliCalendar = new JalaliCalendar();
        if (year > 92) {
            year = year + 1300;
        }
        if (year < 92) {
            year = year + 1400;
        }
        jalaliCalendar.set(year, month, day);
//            if (byMonth) {
//                jalaliCalendar.add(java.util.Calendar.MONTH, period);
//            } else {
        jalaliCalendar.add(java.util.Calendar.DAY_OF_MONTH, period);
//            }
        date = String.valueOf(jalaliCalendar.get(java.util.Calendar.YEAR)).substring(2, 4) + ISOUtil.zeropad(String.valueOf(jalaliCalendar.get(java.util.Calendar.MONTH) + 1), 2) + ISOUtil.zeropad(String.valueOf(jalaliCalendar.get(java.util.Calendar.DAY_OF_MONTH)), 2);

        return date;
    }
    public static String calculateDateByIntervalType(String date, int period, String  intervalType) throws ISOException{
      int year = Short.parseShort(date.substring(0, 4));
      int month = Short.parseShort(date.substring(4, 6)) - 1;
      int day = Short.parseShort(date.substring(6, 8));
      JalaliCalendar jalaliCalendar = new JalaliCalendar();
      jalaliCalendar.set(year, month, day);
      switch (intervalType.trim().charAt(0)) {
          case 'D':
              jalaliCalendar.add(java.util.Calendar.DAY_OF_MONTH, period);
              break;
          case 'W':
              jalaliCalendar.add(java.util.Calendar.WEEK_OF_MONTH, period);
              break;
          case 'M':
              jalaliCalendar.add(java.util.Calendar.MONTH, period);
              break;

      }

      date = String.valueOf(jalaliCalendar.get(java.util.Calendar.YEAR)) + ISOUtil.zeropad(String.valueOf(jalaliCalendar.get(java.util.Calendar.MONTH) + 1), 2) + ISOUtil.zeropad(String.valueOf(jalaliCalendar.get(java.util.Calendar.DAY_OF_MONTH)), 2);
      return date;
  }
}
