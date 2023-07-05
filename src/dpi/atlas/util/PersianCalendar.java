

package dpi.atlas.util;




import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *  This class is a subclass of GregorianCalendar, with the added functionality
 *  that it can set/get date in the Persian calendar system.
 *
 * The algorithms for conversion between Persian and Gregorian calendar systems
 * are taken from the <a href="http://couprie.docspages.com/calmath/">
 * "Calendar Math Add-In for Excel"</a> by <a href="mailto:kees@couprie.org">Kees Couprie</a>.
 * As Couprie states, <a href="mailto:mtahani@noornet.net">Mohammad Tahani</a> from the
 * <a href="http://www.noorsoft.org/">Computer Research Center
 * of Islamic Sciences (CRCIS)</a> of I.R. Iran has contributed to
 * the development of the Persian calendar in that package.
 * Their efforts are gratefully acknowledged.

 @author <a href="mailto:ghasemkiani@yahoo.com">Ghasem Kiani</a>
 @version 1.3
 */

public class PersianCalendar extends GregorianCalendar {

    private static String copyright = "Copyright \u00a9 2003 Ghasem Kiani <ghasemkiani@yahoo.com>. All Rights Reserved.";

    /**
     * Constant for the JDN of 1 Farvardin 1
     * @since 1.1
     */
    public static final long PERSIAN_EPOCH = 1948321;

    /**
     * Constant for Farvardin, the 1st month of the Persian year.
     * @since 1.1
     */
    public static final int FARVARDIN = 0;

    /**
     * Constant for Ordibehesht, the 2nd month of the Persian year.
     * @since 1.1
     */
    public static final int ORDIBEHESHT = 1;

    /**
     * Constant for Khordad, the 3rd month of the Persian year.
     * @since 1.1
     */
    public static final int KHORDAD = 2;

    /**
     * Constant for Tir, the 4th month of the Persian year.
     * @since 1.1
     */
    public static final int TIR = 3;

    /**
     * Constant for Mordad, the 5th month of the Persian year.
     * @since 1.1
     */
    public static final int MORDAD = 4;

    /**
     * Constant for Shahrivar, the 6th month of the Persian year.
     * @since 1.1
     */
    public static final int SHAHRIVAR = 5;

    /**
     * Constant for Mehr, the 7th month of the Persian year.
     * @since 1.1
     */
    public static final int MEHR = 6;

    /**
     * Constant for Aban, the 8th month of the Persian year.
     * @since 1.1
     */
    public static final int ABAN = 7;

    /**
     * Constant for Azar, the 9th month of the Persian year.
     * @since 1.1
     */
    public static final int AZAR = 8;

    /**
     * Constant for Dey, the 10th month of the Persian year.
     * @since 1.1
     */
    public static final int DEY = 9;

    /**
     * Constant for Bahman, the 11th month of the Persian year.
     * @since 1.1
     */
    public static final int BAHMAN = 10;

    /**
     * Constant for Esfand, the 12th month of the Persian year.
     * @since 1.1
     */
    public static final int ESFAND = 11;

    /**
     * Persian month names.
     * @since 1.1
     */

    public static final String[] persianMonths = {
            "\u0641\u0631\u0648\u0631\u062F\u064A\u0646",
            "\u0627\u0631\u062F\u064A\u200C\u0628\u0647\u0634\u062A",
            "\u062E\u0631\u062F\u0627\u062F",
            "\u062A\u064A\u0631",
            "\u0627\u0645\u0631\u062F\u0627\u062F",
            "\u0634\u0647\u0631\u064A\u0648\u0631",
            "\u0645\u0647\u0631",
            "\u0622\u0628\u0627\u0646",
            "\u0622\u0630\u0631",
            "\u062F\u064A",
            "\u0628\u0647\u0645\u0646",
            "\u0627\u0633\u0641\u0646\u062F"
    };

    /**
     * Persian week day names.
     * @since 1.1
     */
    public static final String[] persianWeekDays = {
            "\u0634\u0646\u0628\u0647",
            "\u064A\u0643\u0634\u0646\u0628\u0647",
            "\u062F\u0648\u0634\u0646\u0628\u0647",
            "\u0633\u0647\u200C\u0634\u0646\u0628\u0647",
            "\u0686\u0647\u0627\u0631\u0634\u0646\u0628\u0647",
            "\u067E\u0646\u062C\u0634\u0646\u0628\u0647",
            "\u062C\u0645\u0639\u0647"
    };

    /**
     * Sets the date of this calendar object to the specified
     * Persian date (year, month, and day fields)
     * @since 1.0
     */
    public void setDateFields(int year, int month, int day) {
        setDateFields(new DateFields(year, month, day));
    }

    /**
     * Sets the date of this calendar object to the specified
     * Persian date fields
     * @since 1.0
     */
    public void setDateFields(DateFields dateFields) {
        DateFields t = convertJdnToGregorian(convertPersianToJdn(dateFields));
        set(YEAR, t.getYear());
        set(MONTH, t.getMonth());
        set(DAY_OF_MONTH, t.getDay());
    }

    /**
     * Retrieves the date of this calendar object as the
     * Persian date fields
     * @since 1.0
     */
    public DateFields getDateFields(){
        if(isSet(YEAR) && isSet(MONTH) && isSet(DAY_OF_MONTH)){
            return convertJdnToPersian(convertGregorianToJdn(get(YEAR), get(MONTH), get(DAY_OF_MONTH)));
        } else {
            return new DateFields();
        }
    }

    /**
     * Gives the name of the specified Persian month.
     * @since 1.1
     */
    public static String getPersianMonthName(int month){
        return persianMonths[month];
    }

    /**
     * Gives the name of the current Persian month for this calendar's date.
     * @since 1.3
     */
    public String getPersianMonthName(){
        return getPersianMonthName(getDateFields().getMonth());
    }

    /**
     * Gives the Persian name of the specified day of the week.
     * @since 1.1
     */
    public static String getPersianWeekDayName(int weekDay){
        switch(weekDay)
        {
            case SATURDAY: return persianWeekDays[0];
            case SUNDAY: return persianWeekDays[1];
            case MONDAY: return persianWeekDays[2];
            case TUESDAY: return persianWeekDays[3];
            case WEDNESDAY: return persianWeekDays[4];
            case THURSDAY: return persianWeekDays[5];
            case FRIDAY: return persianWeekDays[6];
        }
        return "";
    }

    /**
     * Gives the Persian name of the current day of the week for this calendar's date.
     * @since 1.3
     */
    public String getPersianWeekDayName() {
        return getPersianWeekDayName(get(DAY_OF_WEEK));
    }

    // Correspondence between original VB functions and Java methods:
    // ====================
    // VB === Java
    // --------------------
    // int === floor
    // fix === myfloor
    // ceil === myceil
    // ====================

    private static long mysign(double x) {
        return x < 0? -1: (x > 0? 1: 0);
    }

    private static long myfloor(double x){
        double y = Math.abs(x);
        double f = Math.floor(y);
        return (long)(mysign(x) * f);
    }

    private static long myceil(double x){
        double y = Math.abs(x) * -1.0;
        double f = Math.floor(y);
        return (long)(-mysign(x) * f);
    }

    /**
     * Is "year" a leap year in the Persian calendar?
     */
    private static boolean leapYear(long year){
        long lYear = year;
        if(lYear > 0)
            return ((((((lYear - (474)) % 2820) + 474) + 38) * 682) % 2816) < 682;
        else
            return ((((((lYear - (473)) % 2820) + 474) + 38) * 682) % 2816) < 682;
    }

    private static long convertPersianToJdn(DateFields t) {
        return convertPersianToJdn(t.getYear(), t.getMonth(), t.getDay());
    }

    private static long convertPersianToJdn(int iYear, int iMonth, int iDay){
        long lYear = iYear;
        long lMonth = iMonth + 1; // Calculations assume 1-based months
        long lDay = iDay;

        long epbase, epyear, mdays;
        if(lYear >= 0)
            epbase = lYear - 474;
        else
            epbase = lYear - 473;
        epyear = 474 + (epbase % 2820);
        if(lMonth <= 7)
            mdays = (lMonth - 1) * 31;
        else
            mdays = (lMonth - 1) * 30 + 6;

        long jdn = lDay + mdays + myfloor(((epyear * 682) - 110) /*double*// 2816.0) + (epyear - 1) * 365 + myfloor(epbase /*double*// 2820.0) * 1029983 + (PERSIAN_EPOCH - 1);

        return jdn;
    }
    private static DateFields convertJdnToPersian(long jdn)	{
        long lYear;
        long lMonth;
        long lDay;

        long depoch, cycle, cyear, ycycle, aux1, aux2, yday;
        depoch = jdn - convertPersianToJdn(475, FARVARDIN /*Changed by ghasemkiani*/, 1);
        cycle = myfloor(depoch /*double*// 1029983.0);
        cyear = depoch % 1029983;
        if(cyear == 1029982)
            ycycle = 2820;
        else {
            aux1 = myfloor(cyear /*double*// 366.0);
            aux2 = cyear % 366;
            ycycle = (long)Math.floor(((2134 * aux1) + (2816 * aux2) + 2815) /*double*// 1028522.0) + aux1 + 1;
        }

        lYear = ycycle + (2820 * cycle) + 474;
        if(lYear <= 0)
            lYear = lYear - 1;
        yday = (jdn - convertPersianToJdn((int)lYear, FARVARDIN /*Changed by ghasemkiani*/, 1)) + 1;
        if(yday <= 186)
            lMonth = myceil(yday /*double*// 31.0);
        else
            lMonth = myceil((yday - 6) /*double*// 30.0);

        lDay = (jdn - convertPersianToJdn((int)lYear, (int)lMonth - 1 /*Changed by ghasemkiani*/, 1)) + 1;

        DateFields t = new DateFields();
        t.setYear((int)lYear);
        t.setMonth((int)lMonth - 1); // Calculations assume 1-based months
        t.setDay((int)lDay);
        return t;
    }

    private static long convertGregorianToJdn(DateFields t)	{
        return convertGregorianToJdn(t.getYear(), t.getMonth(), t.getDay());
    }

    private static long convertGregorianToJdn(int iYear, int iMonth, int iDay) {
        long lYear = iYear;
        long lMonth = iMonth + 1; // Calculations assume 1-based months
        long lDay = iDay;

        if((lYear > 1582) || ((lYear == 1582) && (lMonth > 10)) || ((lYear == 1582) && (lMonth == 10) && (lDay > 14))){
            return ((1461 * (lYear + 4800 + ((lMonth - 14) /*long*// 12))) /*long*// 4) + ((367 * (lMonth - 2 - 12 * (((lMonth - 14) /*long*// 12)))) /*long*// 12) - ((3 * (((lYear + 4900 + ((lMonth - 14) /*long*// 12)) /*long*// 100))) /*long*// 4) + lDay - 32075;
        } else {
            return convertJulianToJdn(iYear, iMonth, iDay);
        }
    }

    private static DateFields convertJdnToGregorian(long jdn) {
        long lYear;
        long lMonth;
        long lDay;
        long l, k, n, i, j;

        if(jdn > 2299160){
            l = jdn + 68569;
            n = ((4 * l) /*long*// 146097);
            l = l - ((146097 * n + 3) /*long*// 4);
            i = ((4000 * (l + 1)) /*long*// 1461001);
            l = l - ((1461 * i) /*long*// 4) + 31;
            j = ((80 * l) /*long*// 2447);
            lDay = l - ((2447 * j) /*long*// 80);
            l = (j /*long*// 11);
            lMonth = j + 2 - 12 * l;
            lYear = 100 * (n - 49) + i + l;

            DateFields t = new DateFields();
            t.setYear((int)lYear);
            t.setMonth((int)lMonth - 1); // Calculations assume 1-based months
            t.setDay((int)lDay);
            return t;
        } else {
            return convertJdnToJulian(jdn);
        }
    }

    private static long convertJulianToJdn(DateFields t) {
        return convertJulianToJdn(t.getYear(), t.getMonth(), t.getDay());
    }

    private static long convertJulianToJdn(int iYear, int iMonth, int iDay)	{
        long lYear = iYear;
        long lMonth = iMonth + 1; // Calculations assume 1-based months
        long lDay = iDay;
        return 367 * lYear - ((7 * (lYear + 5001 + ((lMonth - 9) /*long*// 7))) /*long*// 4) + ((275 * lMonth) /*long*// 9) + lDay + 1729777;
    }

    private static DateFields convertJdnToJulian(long jdn) {
        long lYear;
        long lMonth;
        long lDay;
        long l, k, n, i, j;

        j = jdn + 1402;
        k = ((j - 1) /*long*// 1461);
        l = j - 1461 * k;
        n = ((l - 1) /*long*// 365) - (l /*long*// 1461);
        i = l - 365 * n + 30;
        j = ((80 * i) /*long*// 2447);
        lDay = i - ((2447 * j) /*long*// 80);
        i = (j /*long*// 11);
        lMonth = j + 2 - 12 * i;
        lYear = 4 * k + n + i - 4716;

        DateFields t = new DateFields();
        t.setYear((int)lYear);
        t.setMonth((int)lMonth - 1); // Calculations assume 1-based months
        t.setDay((int)lDay);
        return t;
    }

    /**
     * A sample application using PersianCalendar.
     * <p>
     * Please redirect the output to a file if your console
     * does not support Persian characters (Code Page 1256).
     * (The default encoding of your system must be capable
     * of encoding Persian characters, anyway.)
     *
     * For example, write:
     * <pre>java com.ghasemkiani.util.SimplePersianCalendar > output.txt</pre>
     * Then, inspect the file "output.txt" to see the results.
     */

    /**
     *
     * @param year
     * @param month
     * @param day
     * @return  DateFields, added by Habib Ghayoumi
     */
    public static DateFields getPesianDate(int year, int month, int day)
    {
        month = month - 1;       //month is 0-based
        DateFields dateFields = convertJdnToPersian(convertGregorianToJdn(year, month, day));
        return dateFields;
    }


    public static void main(String[] args){
        PersianCalendar c = new PersianCalendar();
        DateFields df = c.getDateFields();
        System.out.println(df);
        // Comment is in "windows-1256" encoding, but the code is essentially ASCII.
        System.out.println("\u0627\u0645\u0631\u0648\u0632: " + getPersianWeekDayName(c.get(DAY_OF_WEEK)) + " " + df.getDay() + " " + getPersianMonthName(df.getMonth()) + " " + df.getYear());

    }
}
