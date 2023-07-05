package dpi.atlas.util;

/**
 * Created by IntelliJ IDEA.
 * Developed by: Moin Ayazifar
 * Creation Date: Jul 3, 2004
 * Creation Time: 2:11:32 AM
 * Last Update: Jul 3, 2004
 */

import com.opensymphony.util.TextUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.ConfigurationException;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    private static Log log = LogFactory.getLog(StringUtils.class);


    public static final void checkConfigParameter(String param) throws ConfigurationException {
        if ( (param == null) || (param.trim().equalsIgnoreCase("")))
            throw new ConfigurationException(param + " is null ");
    }
    

    private static final void initMapping(String encoding) {
        if ("UTF-8".equalsIgnoreCase(encoding) || "Big5".equalsIgnoreCase(encoding) || "Windows-1252".equalsIgnoreCase(encoding)) {
            addMapping(8216, "'");
            addMapping(8217, "'");
            addMapping(8220, "\"");
            addMapping(8221, "\"");
            addMapping(8230, "...");
            addMapping(8211, "-");
            addMapping(183, "- ");
        } else if ("ISO-8859-1".equalsIgnoreCase(encoding)) {
            addMapping(145, "'");
            addMapping(146, "'");
            addMapping(147, "\"");
            addMapping(148, "\"");
            addMapping(133, "...");
            addMapping(150, "-");
            addMapping(183, "- ");
        }
        for (int i = 0; i < 32; i++)
            if (i != 9 && i != 10 && i != 13)
                addMapping(i, "");

    }

    private static final void addMapping(int charsNumericValue, String replaceStr) {
        _stringChars[charsNumericValue] = replaceStr.toCharArray();
    }

    public static final String escapeCP1252(String s, String encoding) {
        if (s == null)
            return null;
        int len = s.length();
        if (len == 0)
            return s;
        String trimmed = s.trim();
        if (trimmed.length() == 0 || "\"\"".equals(trimmed))
            return trimmed;
        initMapping(encoding);
        int i = 0;
        do {
            int index = s.charAt(i);
            if (index >= MAX_LENGTH || _stringChars[index] == null)
                continue;
            log.info("Found a problematic char with unicode: " + index);
            break;
        } while (++i < len);
        if (i == len)
            return s;
        StringBuffer sb = new StringBuffer(len + 40);
        char chars[] = new char[len];
        s.getChars(0, len, chars, 0);
        sb.append(chars, 0, i);
        int last = i;
        for (; i < len; i++) {
            char c = chars[i];
            int index = c;
            char subst[] = _stringChars[index];
            if (subst != null) {
                if (i > last)
                    sb.append(chars, last, i - last);
                sb.append(subst);
                last = i + 1;
            }
        }

        if (i > last)
            sb.append(chars, last, i - last);
        return sb.toString();
    }

    public static String crop(String original, int cropAt, String suffix) {
        if (original.length() > cropAt)
            original = original.substring(0, cropAt) + suffix;
        return original;
    }

    public static boolean contains(String value, List possiblyContains) {
        if (value == null)
            return possiblyContains == null || possiblyContains.isEmpty();
        if (possiblyContains == null || possiblyContains.isEmpty())
            return false;
        for (int i = 0; i < possiblyContains.size(); i++)
            if (value.indexOf((String) possiblyContains.get(i)) > -1)
                return true;

        return false;
    }


    public static String replaceAll(String str, String oldPattern, String newPattern) {
        if (!TextUtils.stringSet(str) || str.indexOf(oldPattern) == -1)
            return str;
        DPIStringTokenizer st = new DPIStringTokenizer(str);
        String result;
        for (result = str.startsWith(oldPattern) ? newPattern : ""; st.hasMoreTokens(); result += new StringBuffer().append(st.nextToken(oldPattern)).append(!st.hasMoreTokens() && !str.endsWith(oldPattern) ? "" : newPattern).toString())
            ;
        return result;
    }

    public static String replaceAllChars(String str, String oldPattern, String newPattern) {
        char oldPatternc = oldPattern.charAt(0);
        char newPatternc = (char) '0';
        if (newPattern != null && !newPattern.equals(""))
            newPatternc = newPattern.charAt(0);
        StringBuffer sb = new StringBuffer();
        for (int k = 0; k < str.length(); k++) {
            if (str.charAt(k) == oldPatternc)
                sb.append(newPatternc);
            else
                sb.append(str.charAt(k));
        }
        return sb.toString();
    }

    public static String replaceAllChars(String str, char oldPattern, char newPattern) {
        StringBuffer sb = new StringBuffer();
        for (int k = 0; k < str.length(); k++) {
            if (str.charAt(k) == oldPattern)
                sb.append(newPattern);
            else
                sb.append(str.charAt(k));
        }
        return sb.toString();
    }

    public StringUtils() {
    }

    private static int MAX_LENGTH;
    protected static char _stringChars[][];

    static {
        MAX_LENGTH = 9000;
        _stringChars = new char[MAX_LENGTH][];
    }

    public static String replacePattern(String str, String oldPattern, String newPattern) {
        String new_str = str;
        while (new_str.indexOf(oldPattern) >= 0) {
            int i = new_str.indexOf(oldPattern);
            String str1 = new_str.substring(i + oldPattern.length());
            new_str = new_str.substring(0, i);
            new_str = (new_str.concat(newPattern)).concat(str1);
        }
        return new_str;
    }

    public static String[] split(String in_str, String delimiter, int limit) {
        String str = in_str;

        String[] new_str = new String[limit];
        int index = 0;
        while (str.indexOf(delimiter) >= 0 && index < limit - 1) {
            int i = str.indexOf(delimiter);
            new_str[index] = str.substring(0, i);
            str = str.substring(i + delimiter.length());
            index++;
        }
        new_str[index] = str;

        if (index == limit) {
            return new_str;
        } else {
            String[] temp_str = new String[index + 1];
            for (int j = 0; j <= index; j++)
                temp_str[j] = new_str[j];
            return temp_str;
        }
    }

    public static String[] split(String in_str, String delimiter) {
        String str = in_str;

        ArrayList new_str_col = new ArrayList();
        int index = 0, i;

        while (str.indexOf(delimiter) >= 0) {
            i = str.indexOf(delimiter);
            new_str_col.add(str.substring(0, i));
            str = str.substring(i + delimiter.length());
            index++;
        }
        new_str_col.add(str);

        String[] new_str = new String[index + 1];
        for (i = 0; i <= index; i++)
            new_str[i] = (String) new_str_col.get(i);

        return new_str;
    }

    public static String replaceFirstPattern(String str, String oldPattern, String newPattern) {
        String new_str = str;

        int i = new_str.indexOf(oldPattern);
        String str1 = new_str.substring(i + oldPattern.length());
        new_str = new_str.substring(0, i);
        new_str = (new_str.concat(newPattern)).concat(str1);

        return new_str;
    }

    public static String bytes2String(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++)
            ret += (char) b[i];
        return ret;
    }


   public static boolean isNumeric(String str)
    {
         String  list = "1234567890";
         char  alpha;

         for (int i=0; i < str.length(); i++)
         {
           alpha=str.charAt(i);
           if (list.indexOf(alpha)== -1)
           {
                  return false;
           }

         }
           return true;
    }

    public static boolean isAlphabetic(String str)
    {
         String  list = "1234567890";
         char  alpha;

         for (int i=0; i < str.length(); i++)
         {
           alpha=str.charAt(i);
           if (list.indexOf(alpha)!= -1)
           {
                  return false;
           }

         }
           return true;
    }

}