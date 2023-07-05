package dpi.atlas.util;



public class ConvertUtil {

    private static String oldfarsi2UTFConvertor[] = {
            "\u0000", //"\u0000
            "\u0001", //"\u0001
            "\u0002", //"\u0002
            "\u0003", //"\u0003
            "\u0004", //"\u0004
            "\u0005", //"\u0005
            "\u0006", //"\u0006
            "\u0007", //"\u0007
            "\u0008", //"\u0008
            "\u0009", //"\u0009
            "" + (char) 0x0A, //"\u000A
            "\u000B", //"\u000B
            "\u000C", //"\u000C
            "" + (char) 0x0D, //"\u000D
            "\u000E", //"\u000E
            "\u000F", //"\u000F
            "\u0010", //"\u0010
            "\u0011", //"\u0011
            "\u0012", //"\u0012
            "\u0013", //"\u0013
            "\u0014", //"\u0014
            "\u0015", //"\u0015
            "\u0016", //"\u0016
            "\u0017", //"\u0017
            "\u0018", //"\u0018
            "\u0019", //"\u0019
            "\u001A", //"\u001A
            "\u001B", //"\u001B
            "\u001C", //"\u001C
            "\u001D", //"\u001D
            "\u001E", //"\u001E
            "\u001F", //"\u001F
            "\u0020", //"\u0020
            "\u00DB", //"\u0021
            "\u00C6", //"\u0022
            "\u00CF", //"\u0023
            "\u00CB", //"\u0024
            "\u00CD", //"\u0025
            "\u002A", //"\u0026
            "\u00D1", //"\u0027
            "\u00C8", //"\u0028
            "\u00CC", //"\u0029
            "\u00CC\u0020", //"\u002A
            "\u0081", //"\u002B//""
            "\u00CD\u0020", //"\u002C
            "\u00DC", //"\u002D
            "\u00C7", //"\u002E
            "\u002F", //"\u002F
            "\u0030", //"\u0030
            "\u0031", //"\u0031
            "\u0032", //"\u0032
            "\u0033", //"\u0033
            "\u0034", //"\u0034
            "\u0035", //"\u0035
            "\u0036", //"\u0036
            "\u0037", //"\u0037
            "\u0038", //"\u0038
            "\u0039", //"\u0039
            "\u003A", //"\u003A
            "\u008D\u0020", //"\u003B
            "\u00C7", //"\u003C
            "\u00D2", //"\u003D
            "\u00CE\u0020", //"\u003E
            "\u00CE", //"\u003F
            "\u00D0", //"\u0040
            "\u00D3", //"\u0041
            "\u00D4", //"\u0042
            "\u00D5", //"\u0043
            "\u00D6", //"\u0044
            "\u00D8", //"\u0045
            "\u00D9", //"\u0046
            "\u00DA\u0020", //"\u0047
            "\u00DA", //"\u0048",//be corrected
            "\u00DB\u0020", //"\u0049
            "\u00DD", //"\u004A
            "\u00DE\u0020", //"\u004B
            "\u00DE", //"\u004C
            "\u00DF", //"\u004D
            "\u0090", //"\u004E
            "\u00E1\u0020", //"\u004F
            "\u00E1", //"\u0050
            "\u00C7\u00E1", //"\u0051>>>??
            "\u00E3", //"\u0052
            "\u00E4\u0020", //"\u0053
            "\u00E4", //"\u0054
            "\u00E6", //"\u0055
            "\u00E5\u0020", //"\u0056
            "\u00E5", //"\u0057
            "\u00ED", //"\u0058
            "\u00ED\u0020", //"\u0059
            "\u002F", //"\u005A
            "\u005B", //"\u005B
            "" + (char) 0x5c, //"\u005C
            "\u005D", //"\u005D
            "\u008D", //"\u005E
            "\u00E3\u0020", //"\u005F
            "\u0091", //"\u0060
            "\u0061", //"\u0061
            "\u0062", //"\u0062
            "\u0063", //"\u0063
            "\u0064", //"\u0064
            "\u0065", //"\u0065
            "\u0066", //"\u0066
            "\u0067", //"\u0067
            "\u0068", //"\u0068
            "\u0069", //"\u0069
            "\u006A", //"\u006A
            "\u006B", //"\u006B
            "\u006C", //"\u006C
            "\u006D", //"\u006D
            "\u006E", //"\u006E
            "\u006F", //"\u006F
            "\u0070", //"\u0070
            "\u0071", //"\u0071
            "\u0072", //"\u0072
            "\u0073", //"\u0073
            "\u0074", //"\u0074
            "\u0075", //"\u0075
            "\u0076", //"\u0076
            "\u0077", //"\u0077
            "\u0078", //"\u0078
            "\u0079", //"\u0079
            "\u007A", //"\u007A
            "\u007B", //"\u007B >>{
            "\u00CA", //"\u007C >>?
            "\u007D", //"\u007D >> } >> ???? ?
            "\u007E", //"\u007E
            "\u007F", //"\u007F
            "\u0080", //"\u0080
            "\u0081", //"\u0081
            "\u0082", //"\u0082
            "\u0083", //"\u0083
            "\u0084", //"\u0084
            "\u0085", //"\u0085
            "\u0086", //"\u0086
            "\u0087", //"\u0087
            "\u0088", //"\u0088
            "\u0089", //"\u0089
            "\u008A", //"\u008A
            "\u008B", //"\u008B
            "\u008C", //"\u008C
            "\u008D", //"\u008D
            "\u008E", //"\u008E
            "\u008F", //"\u008F
            "\u0090", //"\u0090
            "\u0091", //"\u0091
            "\u0092", //"\u0092
            "\u0093", //"\u0093
            "\u0094", //"\u0094
            "\u0095", //"\u0095
            "\u0096", //"\u0096
            "\u0097", //"\u0097
            "\u0098", //"\u0098
            "\u0099", //"\u0099
            "\u009A", //"\u009A
            "\u009B", //"\u009B
            "\u009C", //"\u009C
            "\u009D", //"\u009D
            "\u009E", //"\u009E
            "\u009F", //"\u009F
            "\u00A0", //"\u00A0
            "\u00A1", //"\u00A1
            "\u00A2", //"\u00A2
            "\u00A3", //"\u00A3
            "\u00A4", //"\u00A4
            "\u00A5", //"\u00A5
            "\u00A6", //"\u00A6
            "\u00A7", //"\u00A7
            "\u00A8", //"\u00A8
            "\u00A9", //"\u00A9
            "\u00AA", //"\u00AA
            "\u00AB", //"\u00AB
            "\u00AC", //"\u00AC
            "\u00AD", //"\u00AD
            "\u00AE", //"\u00AE
            "\u00AF", //"\u00AF
            "\u00B0", //"\u00B0
            "\u00B1", //"\u00B1
            "\u00B2", //"\u00B2
            "\u00B3", //"\u00B3
            "\u00B4", //"\u00B4
            "\u00B5", //"\u00B5
            "\u00B6", //"\u00B6
            "\u00B7", //"\u00B7
            "\u00B8", //"\u00B8
            "\u00B9", //"\u00B9
            "\u00BA", //"\u00BA
            "\u00BB", //"\u00BB
            "\u00BC", //"\u00BC
            "\u00BD", //"\u00BD
            "\u00BE", //"\u00BE
            "\u00BF", //"\u00BF
            "\u00C0", //"\u00C0
            "\u00C1", //"\u00C1
            "\u00C2", //"\u00C2
            "\u00C3", //"\u00C3
            "\u00C4", //"\u00C4
            "\u00C5", //"\u00C5
            "\u00C6", //"\u00C6
            "\u00C7", //"\u00C7
            "\u00C8", //"\u00C8
            "\u00C9", //"\u00C9
            "\u00CA", //"\u00CA
            "\u00CB", //"\u00CB
            "\u00CC", //"\u00CC
            "\u00CD", //"\u00CD
            "\u00CE", //"\u00CE
            "\u00CF", //"\u00CF
            "\u00D0", //"\u00D0
            "\u00D1", //"\u00D1
            "\u00D2", //"\u00D2
            "\u00D3", //"\u00D3
            "\u00D4", //"\u00D4
            "\u00D5", //"\u00D5
            "\u00D6", //"\u00D6
            "\u00D7", //"\u00D7
            "\u00D8", //"\u00D8
            "\u00D9", //"\u00D9
            "\u00DA", //"\u00DA
            "\u00DB", //"\u00DB
            "\u00DC", //"\u00DC
            "\u00DD", //"\u00DD
            "\u00DE", //"\u00DE
            "\u00DF", //"\u00DF
            "\u00E0", //"\u00E0
            "\u00E1", //"\u00E1
            "\u00E2", //"\u00E2
            "\u00E3", //"\u00E3
            "\u00E4", //"\u00E4
            "\u00E5", //"\u00E5
            "\u00E6", //"\u00E6
            "\u00E7", //"\u00E7
            "\u00E8", //"\u00E8
            "\u00E9", //"\u00E9
            "\u00EA", //"\u00EA
            "\u00EB", //"\u00EB
            "\u00EC", //"\u00EC
            "\u00ED", //"\u00ED
            "\u00EE", //"\u00EE
            "\u00EF", //"\u00EF
            "\u00F0", //"\u00F0
            "\u00F1", //"\u00F1
            "\u00F2", //"\u00F2
            "\u00F3", //"\u00F3
            "\u00F4", //"\u00F4
            "\u00F5", //"\u00F5
            "\u00F6", //"\u00F6
            "\u00F7", //"\u00F7
            "\u00F8", //"\u00F8
            "\u00F9", //"\u00F9
            "\u00FA", //"\u00FA
            "\u00FB", //"\u00FB
            "\u00FC", //"\u00FC
            "\u00FD", //"\u00FD
            "\u00FE", //"\u00FE
            "\u00FF"//"\u00FF
    };

    static char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    static char KASE = 47;
    static char DANDANE = 58;
    static char KESHIDE = 0x2D;


    public static String getUTF8Str(String str2convert) {
        if (str2convert == null)
            return null;

        String str = "";

        for (int i = 0; i < str2convert.length(); i++) {
            if (str2convert.charAt(i) != KASE &&
                    str2convert.charAt(i) != DANDANE) {
                if (str2convert.charAt(i) != KESHIDE && str2convert.charAt(i) <= 0xff) {
                    String k = oldfarsi2UTFConvertor[str2convert.charAt(i)];

                    str += k;
                }
            } else
                str += " ";

        }

        return str;
    }

    public static String getHexString(String str) {
        String strOut = "";
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) > 0xff)
                strOut += charToHex(str.charAt(i) / 0x100);
            strOut += charToHex(str.charAt(i));
            if (i != str.length() - 1)
                strOut += "-";
        }
        return strOut;
    }

    /**
     * Convert a byte(b) in hexadecimal format
     *
     * @param b : Byte that must be converted haxadecimaly
     *          return
     */
    public static String charToHex(int b) {
        char[] a = {hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f]};
        return new String(a);
    }

    public static String reverse(String str) {
        if (str == null)
            return null;

        String str_converted = "";
        for (int i = str.length() - 1; i > -1; i--)
            if (!((i == str.length() - 1 || i == 0) && str.charAt(i) == 0x20))
                str_converted += str.charAt(i);

        //System.out.println("getHexString(str_converted) = " + getHexString(str_converted));
        return str_converted;
    }

    public static String ConvertStrNum(char []data){
          char temp;
          int i=0,NumLen=0;

          while(i<data.length){
              NumLen=0;
              if((data[i]>='0')&&(data[i]<='9')){
                  NumLen++;
                  for(;((i+NumLen<data.length)&&(data[i+NumLen]>='0')&&(data[i+NumLen]<='9'));NumLen++);
              }
              if(NumLen>0){

                  for(int j=0 ; j<NumLen/2 ; j++){
                      temp=data[i+j];
                      data[i+j]=data[i+NumLen-1-j];
                      data[i+NumLen-1-j]=temp;
                  }
                  NumLen--;
              }
              i=i+NumLen+1;
          }
          return String.copyValueOf(data);
      }


    public static String StringToHexConvert(String str) {
        StringBuffer hexBuffer = new StringBuffer();
        for (int i = 0; i < str.length(); i++)
            hexBuffer.append(Integer.toHexString((int) str.charAt(i)).toUpperCase());
        return hexBuffer.toString();
    }

    public static byte[] hex2Byte(String str) {
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer
                    .parseInt(str.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    // Convert Byte Arrary to Hex String
    public static String byte2hex(byte[] b) {
        // String Buffer can be used instead
        String hs = "";
        String stmp = "";

        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
            if (n < b.length - 1)
                hs = hs + "";
        }
        return hs.toUpperCase();
    }
}
