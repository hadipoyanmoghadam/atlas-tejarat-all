

package dpi.atlas.util;

/**
 * 	This class holds the fields of Persian date, namely the Persian year,
 * 	month, and day. <code>PersianCalendar</code> uses this class to
 * 	set/get the Persian date.
 *
 * 	@author <a href="mailto:ghasemkiani@yahoo.com">Ghasem Kiani</a>
 * 	@version 1.0
 */

public class DateFields {

    /**
     * This field denotes the Persian year.
     */
    private int year;

    /**
     * This field denotes the Persian month.
     * <p>Note: month is zero-based.
     * See constants in <code>PersianCalendar</code>.
     */
    private int month;

    /**
     * This field denotes the Persian day.
     */
    private int day;

    /**
     * Constructs a <code>DateFields</code> object with the given date fields.
     * @param year
     * @param month
     * @param day
     */
    public DateFields(int year, int month, int day)	{
        super();
        setYear(year);
        setMonth(month);
        setDay(day);
    }


    /**
     * Constructs a <code>DateFields</code> object with the date fields initialized to 0.
     */
    public DateFields() {
        this(0, 0, 0);
    }

    /**
     * Accessor method to assign a new value to year.
     * @param year The new value to be assigned to year.
     */

    public void setYear(int year){
        this.year = year;
    }

    /**
     * Accessor method to fetch the value of year.
     * @return The value of year.
     */
    public int getYear(){
        return year;
    }

    /**
     * Accessor method to assign a new value to month.
     * @param month The new value to be assigned to month.
     */
    public void setMonth(int month){
        this.month = month;
    }

    /**
     * Accessor method to fetch the value of month.
     * @return The value of month.
     */
    public int getMonth(){
        return month;
    }

    /**
     * Accessor method to assign a new value to day.
     * @param day The new value to be assigned to day.
     */
    public void setDay(int day){
        this.day = day;
    }

    /**
     * Accessor method to fetch the value of day.
     * @return The value of day.
     */
    public int getDay(){
        return day;
    }

    /**
     * This method returns a usable string representation of this object.
     * Month is incremented to show one-based Persian month index.
     * @return A usable string representation of this object.
     */
    public String toString(){
        String lYear  = String.valueOf(this.year);
        String lMonth = ((month + 1) < 10) ? ("0" + (month + 1)) : ("" + (month + 1)) ;
        String lDay  =  ((day < 10) ? ("0" + day ) : ("" + day));
        return "" + lYear + "/" + lMonth + "/" + lDay;
    }
}
