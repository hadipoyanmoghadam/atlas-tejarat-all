package branch.dpi.atlas.util;


import java.io.UnsupportedEncodingException;

/**
 * User: H.Ghayoumi
 * Date: Sep 9, 2013
 * Time: 6:21:30 PM
 */
public class FarsiUtil {

public static String convertWindows1256ToIranSystem(String src)  throws UnsupportedEncodingException
    {
        src = src.trim();
        byte[] srcByte = src.getBytes("CP1256");
        byte[] destByte = new byte[srcByte.length];
        int i = 0;
        for(byte b:srcByte) {
            switch (b) {
                case (byte)0x20: //space
                    destByte[i] = (byte)255;
                    break;

                case (byte)0x30: //0
                    destByte[i] = (byte)128;
                    break;

                case (byte)0x31: //1
                    destByte[i] = (byte)129;
                    break;

                case (byte)0x32: //2
                    destByte[i] = (byte)130;
                    break;

                case (byte)0x33: //3
                    destByte[i] = (byte)131;
                    break;

                case (byte)0x34: //4
                    destByte[i] = (byte)132;
                    break;

                case (byte)0x35: //5
                    destByte[i] = (byte)133;
                    break;

                case (byte)0x36: //6
                    destByte[i] = (byte)134;
                    break;
                
                case (byte)0x37: //7
                    destByte[i] = (byte)135;
                    break;

                case (byte)0x38: //8
                    destByte[i] = (byte)136;
                    break;

                case (byte)0x39: //9
                    destByte[i] = (byte)137;
                    break;

                case (byte)0xA1: // comma
                    destByte[i] = (byte)138;
                    break;

                case (byte)0xDC:
                    destByte[i] = (byte)139;
                    break;
                case (byte)0xBF:
                    destByte[i] =(byte)140;
                    break;

                case (byte)0xC2: // Aye Ba kola
                    destByte[i] = (byte)141;
                    break;

                case (byte)0xC6: // hamze chasban
                    destByte[i] = (byte)142;
                    break;

                case (byte)0xC1: // hamze chasban aval
                    destByte[i] = (byte)143;
                    break;

                case (byte)0xC7: // alef bedoone kola
                    if(i > 0 && isAttachedNext(srcByte[i-1]))
                        destByte[i] = (byte)145;
                    else
                        destByte[i] = (byte)144;
                    break;

                case (byte)0xC8: //be
                    if(i < (src.length()-1) && isAttachedNext(srcByte[i+1]))
                        destByte[i] = (byte)147;
                    else
                        destByte[i] = (byte)146;
                    break;

                case (byte)0x81: //pe
                    if(i < (src.length()-1) && isAttachedNext(srcByte[i+1]))
                        destByte[i] = (byte)149;
                    else
                        destByte[i] = (byte)148;
                    break;

                case (byte)0xCA: //te
                    if(i < (src.length()-1) && isAttachedNext(srcByte[i+1]))
                        destByte[i] = (byte)151;
                    else
                        destByte[i] = (byte)150;
                    break;

                case (byte)0xCB: //se, 3 noghte
                    if(i < (src.length()-1) && isAttachedNext(srcByte[i+1]))
                        destByte[i] = (byte)153;
                    else
                        destByte[i] = (byte)152;
                    break;

                case (byte)0xCC: //jim
                    if(i < (src.length()-1) && isAttachedNext(srcByte[i+1]))
                        destByte[i] = (byte)155;
                    else
                        destByte[i] = (byte)154;
                    break;

                case (byte)0x8D: //che
                    if(i < (src.length()-1) && isAttachedNext(srcByte[i+1]))
                        destByte[i] = (byte)157;
                    else
                        destByte[i] = (byte)156;
                    break;

                case (byte)0xCD: //he jimi
                    if(i < (src.length()-1) && isAttachedNext(srcByte[i+1]))
                        destByte[i] = (byte)159;
                    else
                        destByte[i] = (byte)158;
                    break;

                case (byte)0xCE: //khe
                    if(i < (src.length()-1) && isAttachedNext(srcByte[i+1]))
                        destByte[i] = (byte)161;
                    else
                        destByte[i] = (byte)160;
                    break;

                case (byte)0xCF: //dal
                    destByte[i] = (byte)162;
                    break;

                case (byte)0xD0: //zal
                    destByte[i] = (byte)163;
                    break;

                case (byte)0xD1: //re
                    destByte[i] = (byte)164;
                    break;

                case (byte)0xD2: //ze
                    destByte[i] = (byte)165;
                    break;

                case (byte)0x8E: //zhe
                    destByte[i] = (byte)166;
                    break;

                case (byte)0xD3: //sin
                    if(i < (src.length()-1) && isAttachedNext(srcByte[i+1]))
                        destByte[i] = (byte)168;
                    else
                        destByte[i] = (byte)167;
                    break;

                case (byte)0xD4: //shin
                    if(i < (src.length()-1) && isAttachedNext(srcByte[i+1]))
                        destByte[i] = (byte)170;
                    else
                        destByte[i] = (byte)169;
                    break;

                case (byte)0xD5: //sad
                    if(i < (src.length()-1) && isAttachedNext(srcByte[i+1]))
                        destByte[i] = (byte)172;
                    else
                        destByte[i] = (byte)171;
                    break;

                case (byte)0xD6: //zad
                    if(i < (src.length()-1) && isAttachedNext(srcByte[i+1]))
                        destByte[i] = (byte)174;
                    else
                        destByte[i] = (byte)173;
                    break;

                case (byte)0xD8: //ta
                        destByte[i] = (byte)175;
                        break;

                case (byte)0xD9: //za
                        destByte[i] = (byte)224;
                        break;

                case (byte)0xDA: //ain
                    if((i >0) && i<(src.length()-1) && isAttachedNext(srcByte[i+1]) && isAttachedPrevious(srcByte[i-1]))
                        destByte[i] = (byte)227; //ain vasat
                    else if(i>=0 && i<(src.length()-1) && isAttachedNext(srcByte[i+1]))
                        destByte[i] = (byte)228; //ain aval
                    else if(i>0 && isAttachedPrevious(srcByte[i-1]))
                        destByte[i] = (byte)226; //ain akhar chasban
                    else
                        destByte[i] = (byte)225; //ain akhar joda
                    break;

                case (byte)0xDB: //ghain
                    if((i >0) && i<(src.length()-1) && isAttachedNext(srcByte[i+1]) && isAttachedPrevious(srcByte[i-1]))
                        destByte[i] = (byte)231;   // ghain vasat
                    else if(i>=0 && i<(src.length()-1) && isAttachedNext(srcByte[i+1]))
                        destByte[i] = (byte)232;  //ghain aval
                    else if(i>0 && isAttachedPrevious(srcByte[i-1]))
                        destByte[i] = (byte)230; //ghain akhar chasban
                    else
                        destByte[i] = (byte)229; //ghain akhar joda
                    break;

                case (byte)0xDD: //fe
                    if(i < (src.length()-1) && isAttachedNext(srcByte[i+1]))
                        destByte[i] = (byte)234;
                    else
                        destByte[i] = (byte)233;
                    break;

                case (byte)0xDE: //ghaf
                    if(i < (src.length()-1) && isAttachedNext(srcByte[i+1]))
                        destByte[i] = (byte)236;
                    else destByte[i] = (byte)235;
                    break;

                case (byte)0x98: //kaf
                    if(i < (src.length()-1) && isAttachedNext(srcByte[i+1]))
                        destByte[i] = (byte)238;
                    else
                        destByte[i] = (byte)237;
                    break;

                case (byte)0x90: //gaf
                    if(i < (src.length()-1) && isAttachedNext(srcByte[i+1]))
                        destByte[i] = (byte)240;
                    else
                        destByte[i] = (byte)239;
                    break;

                case (byte)0xE1: //lam
                    if(i < (src.length()-1) && isAttachedNext(srcByte[i+1]))
                        destByte[i] = (byte)243;
                    else
                        destByte[i] = (byte)241;
                    break;

                case (byte)0xE3: //mim
                    if(i < (src.length()-1) && isAttachedNext(srcByte[i+1]))
                        destByte[i] = (byte)245;
                    else
                        destByte[i] = (byte)244;
                    break;

                case (byte)0xE4: //noon
                    if(i < (src.length()-1) && isAttachedNext(srcByte[i+1]))
                        destByte[i] = (byte)247;
                    else
                        destByte[i] = (byte)246;
                    break;

                case (byte)0xE5: //he
                    if((i >0) && i<(src.length()-1) && isAttachedNext(srcByte[i+1]) && isAttachedPrevious(srcByte[i-1]))
                        destByte[i] = (byte)250;   // he vasat
                    else if(i>=0 && i<(src.length()-1) && isAttachedNext(srcByte[i+1]))
                        destByte[i] = (byte)251;  //he aval
                    else
                        destByte[i] = (byte)249; //he akhar chasban
                    break;

                case (byte)0xE6: //vav
                    destByte[i] = (byte)248;
                    break;

                case (byte)0xED: //ye
                    if(i < src.length()-1 && isAttachedNext(srcByte[i+1]))
                        destByte[i] = (byte)254;
                    else if(i>0 && isAttachedPrevious(srcByte[i-1]))
                        destByte[i] = (byte)252;
                    else
                        destByte[i] = (byte)253;
                    break;

                default  :
                    destByte[i] = b;
            }
            i++;
        }
        String res = new String(destByte, "ISO-8859-1");
        return res;

    }

    private static boolean isAttachedNext(byte b)
    {
        boolean result;
        switch(b){
            case (byte)0xC3: // hamze rooye alef
                result = true;
                break;
            case (byte)0xC4: // hamze rooye vav
                result = true;
                break;
            case (byte)0xC5: // hamze zire alef
                result = true;
                break;
            case (byte)0xC6: //hamze chasboon
                result = true;
                break;
            case (byte)0xC7: //alef bedoone kola
                result = true;
                break;
            case (byte)0xC8: //be
                result = true;
                break;
            case (byte)0x81: //pe
                result = true;
                break;
            case (byte)0xCA: //te
                result = true;
                break;
            case (byte)0xCB: //se 3 noghte
                result = true;
                break;
            case (byte)0xCC: //jim
                result = true;
                break;
            case (byte)0x8D: //che
                result = true;
                break;
            case (byte)0xCD: //he jimi
                result = true;
                break;
            case (byte)0xCE: //khe
                result = true;
                break;
            case (byte)0xCF: //dal
                result = true;
                break;
            case (byte)0xD0: //zal
                result = true;
                break;
            case (byte)0xD1: //re
                result = true;
                break;
            case (byte)0xD2: //ze
                result = true;
                break;
            case (byte)0x8E: //zhe
                result = true;
                break;
            case (byte)0xD3: //sin
                result = true;
                break;
            case (byte)0xD4: //shin
                result = true;
                break;
            case (byte)0xD5: //sad
                result = true;
                break;
            case (byte)0xD6: //zad
                result = true;
                break;
            case (byte)0xD8: //ta
                result = true;
                break;
            case (byte)0xD9: //za
                result = true;
                break;
            case (byte)0xDA: //ain
                result = true;
                break;
            case (byte)0xDB: //ghain
                result = true;
                break;
            case (byte)0xDD: //fe
                result = true;
                break;
            case (byte)0xDE: //ghaf
                result = true;
                break;
            case (byte)0x98: //kaf
                result = true;
                break;
            case (byte)0x90: //gaf
                result = true;
                break;
            case (byte)0xE1: //lam
                result = true;
                break;
            case (byte)0xE3: //mim
                result = true;
                break;
            case (byte)0xE4: //noon
                result = true;
                break;
            case (byte)0xE6: //vav
                result = true;
                break;
            case (byte)0xE5: //he
                result = true;
                break;
            case (byte)0xEC: //ye
                result = true;
                break;
            case (byte)0xED: //ye (shift + x)
                result = true;
                break;
            default:
                result = false;
        }
        return result;
    }

    private static boolean isAttachedPrevious(byte b)
    {
        boolean result;
        switch(b){

            case (byte)0xC6: //hamze chasboon
                result = true;
                break;
            case (byte)0xC8: //be
                result = true;
                break;
            case (byte)0x81: //pe
                result = true;
                break;
            case (byte)0xCA: //te
                result = true;
                break;
            case (byte)0xCB: //se 3 noghte
                result = true;
                break;
            case (byte)0xCC: //jim
                result = true;
                break;
            case (byte)0x8D: //che
                result = true;
                break;
            case (byte)0xCD: //he jimi
                result = true;
                break;
            case (byte)0xCE: //khe
                result = true;
                break;
            case (byte)0xD3: //sin
                result = true;
                break;
            case (byte)0xD4: //shin
                result = true;
                break;
            case (byte)0xD5: //sad
                result = true;
                break;
            case (byte)0xD6: //zad
                result = true;
                break;
            case (byte)0xD8: //ta
                result = true;
                break;
            case (byte)0xD9: //za
                result = true;
                break;
            case (byte)0xDA: //ain
                result = true;
                break;
            case (byte)0xDB: //ghain
                result = true;
                break;
            case (byte)0xDD: //fe
                result = true;
                break;
            case (byte)0xDE: //ghaf
                result = true;
                break;
            case (byte)0x98: //kaf
                result = true;
                break;
            case (byte)0x90: //gaf
                result = true;
                break;
            case (byte)0xE1: //lam
                result = true;
                break;
            case (byte)0xE3: //mim
                result = true;
                break;
            case (byte)0xE4: //noon
                result = true;
                break;
            case (byte)0xE5: //he
                result = true;
                break;
            case (byte)0xED: //ye (shift + x)
                result = true;
                break;
            default:
                result = false;
        }
        return result;
    }

    public static String convertIranSystemToWindows1256(String src)  throws UnsupportedEncodingException
    {
        byte[] srcByte = src.getBytes("ISO-8859-1");
        byte[] destByte = new byte[src.length()*2];  // for possible la
        int i = 0;
        int j = 0;

        for(byte b: srcByte){
            switch (b) {
                case (byte)128: //0
                    destByte[j] = (byte)0x30;
                    break;
                case (byte)129: //1
                    destByte[j] = (byte)0x31;
                    break;
                case (byte)130: //2
                    destByte[j] = (byte)0x32;
                    break;
                case (byte)131: //3
                    destByte[j] = (byte)0x33;
                    break;
                case (byte)132: //4
                    destByte[j] = (byte)0x34;
                    break;
                case (byte)133: //5
                    destByte[j] = (byte)0x35;
                    break;
                case (byte)134: //6
                    destByte[j] = (byte)0x36;
                    break;
                case (byte)135: //7
                    destByte[j] = (byte)0x37;
                    break;
                case (byte)136: //8
                    destByte[j] = (byte)0x38;
                    break;
                case (byte)137: //9
                    destByte[j] = (byte)0x39;
                    break;
                case (byte)138: //comma
                    destByte[j] = (byte)0xA1;
                    break;
                case (byte)139: //keshidegi
                    destByte[j] = (byte)0xDC;
                    break;
                case (byte)140: //?   Question Sign
                    destByte[j] = (byte)0xBF;
                    break;
                case (byte)141: // aye ba kola
                    destByte[j] = (byte)0xC2;
                    break;
                case (byte)142: //hamze chasban
                    destByte[j] = (byte)0xC6;
                    break;
                case (byte)143: //hamze tanha
                    destByte[j] = (byte)0xC1;
                    break;
                case (byte)144 : //aye bi kola
                    destByte[j] =  (byte)0xC7;
                    break;
                case (byte)145: //aye bikola chasban
                    destByte[j] = (byte)0xC7;
                    break;
                case (byte)146: //be akhar
                    destByte[j] = (byte)0xC8;
                    break;
                case (byte)147: //be chasban
                    destByte[j] = (byte)0xC8;
                    break;
                case (byte)148: //pe akhar
                    destByte[j] = (byte)0x81;
                    break;
                case (byte)149:  //pe aval
                    destByte[j] = (byte)0x81;
                    break;
                case (byte)150: //te akhar
                    destByte[j] = (byte)0xCA;
                    break;
                case (byte)151: //te chasban
                    destByte[j] = (byte)0xCA;
                    break;
                case (byte)152: // se se noghte akhar
                    destByte[j] = (byte)0xCB;
                    break;
                case (byte)153: // se se noghte chasban
                    destByte[j] = (byte)0xCB;
                    break;
                case (byte)154: // jim
                    destByte[j] = (byte)0xCC;
                    break;
                case (byte)155: //jim chasban
                    destByte[j] = (byte)0xCC;
                    break;
                case (byte)156: //che
                    destByte[j] = (byte)0x8D;
                    break;
                case (byte)157: //che chasban
                    destByte[j] = (byte)0x8D;
                    break;
                case (byte)158: //he jimi
                    destByte[j] = (byte)0xCD;
                    break;
                case (byte)159: //he jimi chasban
                    destByte[j] = (byte)0xCD;
                    break;
                case (byte)160: //khe
                    destByte[j]= (byte)0xCE;
                    break;
                case (byte)161: //khe chasban
                    destByte[j]= (byte)0xCE;
                    break;
                case (byte)162: //dal
                    destByte[j] = (byte)0xCF;
                    break;
                case (byte)163: //zal
                    destByte[j] = (byte)0xD0;
                    break;
                case (byte)164: //re
                    destByte[j] = (byte)0xD1;
                    break;
                case (byte)165: //ze
                    destByte[j] = (byte)0xD2;
                    break;
                case (byte)166: //zhe
                    destByte[j] = (byte)0x8E;
                    break;
                case (byte)167: //sin
                    destByte[j] = (byte)0xD3;
                    break;
                case (byte)168: //sin chasban
                    destByte[j] = (byte)0xD3;
                    break;
                case (byte)169: //shin
                    destByte[j] = (byte)0xD4;
                    break;
                case (byte)170: //shin chasban
                    destByte[j] = (byte)0xD4;
                    break;
                case (byte)171: //sad
                    destByte[j] = (byte)0xD5;
                    break;
                case (byte)172: //sad chasban
                    destByte[j] = (byte)0xD5;
                    break;
                case (byte)173: //zad
                    destByte[j] = (byte)0xD6;
                    break;
                case (byte)174: //zad chasban
                    destByte[j] = (byte)0xD6;
                    break;
                case (byte)175: //ta
                    destByte[j] = (byte)0xD8;
                    break;
                case (byte)176: //-                     was added at 94-09-25
                    destByte[j] = (byte)0x96;
                    break;
                case (byte)224: //za
                    destByte[j] = (byte)0xD9;
                    break;
                case (byte)225: //ain
                    destByte[j] = (byte)0xDA;
                    break;
                case (byte)226: //ain
                    destByte[j] = (byte)0xDA;
                    break;
                case (byte)227: //ain
                    destByte[j] = (byte)0xDA;
                    break;
                case (byte)228: //ain
                    destByte[j] = (byte)0xDA;
                    break;
                case (byte)229: //ghain
                    destByte[j] = (byte)0xDB;
                    break;
                case (byte)230: //ghain
                    destByte[j] = (byte)0xDB;
                    break;
                case (byte)231: //ghain
                    destByte[j] = (byte)0xDB;
                    break;
                case (byte)232: //ghain
                    destByte[j] = (byte)0xDB;
                    break;
                case (byte)233: //fe
                    destByte[j] = (byte)0xDD;
                    break;
                case (byte)234: //fe chasban
                    destByte[j] = (byte)0xDD;
                    break;
                case (byte)235: //ghaf
                    destByte[j] = (byte)0xDE;
                    break;
                case (byte)236: //ghaf chasban
                    destByte[j] = (byte)0xDE;
                    break;
                case (byte)237: //kaf
                    destByte[j] = (byte)0x98;
                    break;
                case (byte)238: //kaf chasban
                    destByte[j] = (byte)0xDF;
                    break;
                case (byte)239: //gaf
                    destByte[j] = (byte)0x90;
                    break;
                case (byte)240: //gaf chasban
                    destByte[j] = (byte)0x90;
                    break;
                case (byte)241: //lam
                    destByte[j] = (byte)0xE1;
                    break;
                case (byte)242: // la
                    destByte[j] = (byte)0xE1; j++; destByte[j] = (byte)0xC7;
                    break;
                case (byte)243: //lam chasban
                    destByte[j] = (byte)0xE1;
                    break;
                case (byte)244: //mim
                    destByte[j] = (byte)0xE3;
                    break;
                case (byte)245: //mim chasban
                    destByte[j] = (byte)0xE3;
                    break;
                case (byte)246: //noon
                    destByte[j] = (byte)0xE4;
                    break;
                case (byte)247: //noon chasban
                    destByte[j] = (byte)0xE4;
                    break;
                case (byte)248: //vav
                    destByte[j] = (byte)0xE6;
                    break;
                case (byte)249: //he
                    destByte[j] = (byte)0xE5;
                    break;
                case (byte)250: //he
                    destByte[j] = (byte)0xE5;
                    break;
                case (byte)251: //he
                    destByte[j] = (byte)0xE5;
                    break;
                case (byte)252: //ye
                    destByte[j] = (byte)0xED;
                    break;
                case (byte)253: //ye
                    destByte[j] = (byte)0xED;
                    break;
                case (byte)254: //ye chasban
                    destByte[j] = (byte)0xED;
                    break;
                case (byte)255: //space
                    destByte[j] = (byte)0x20;
                    break;
                    
                default:
            }
            i++; j++;
        }

      return (new String (destByte, "CP1256")).trim();
    }

    /**
     *
     * @param src
     * @return  String in Iran System encoding
     * @throws UnsupportedEncodingException
     */

    public static String convertEBCDIC2IranSystem(String src)  throws UnsupportedEncodingException
    {
//        src = eliminateSpace(src.trim());
        src = src.trim();
        byte[] srcByte = src.getBytes("ISO-8859-1");
        byte[] destByte = new byte[srcByte.length*2];
        short i = 0;
        short j = 0;
        for(byte b: srcByte){
            switch (b) {

                case 91:   // '[' : zhe
                    destByte[j] = (byte)166;
                    break;
                case 46:   // '.' alef
                    destByte[j] = (byte)144;
                    break;
                case 60:   // '<' : alef chasban
                    destByte[j] = (byte)145;
                    break;
                case 40:  // '(' : be
                    if (i<(srcByte.length-1) && srcByte[i+1] == 58) {
                        destByte[j] = (byte)146; j++; destByte[j] = (byte)255;
                    }
                    else
                        destByte[j] = (byte)147;
                    break;
                case 43: // '+' : pe
                    if (i<(srcByte.length-1) && srcByte[i+1] == 58) {
                        destByte[j] = (byte)148; j++; destByte[j] = (byte)255;
                    }
                    else
                        destByte[j] = (byte)149;
                    break;
                case 124: // '|' : te
                    if (i<(srcByte.length-1) && srcByte[i+1] == 58){
                        destByte[j] = (byte)150; j++;    destByte[j] = (byte)255;
                    }
                    else
                        destByte[j] = (byte)151;
                    break;
                case 33: // '!' : ghain
                    if((i >0) && i<(src.length()-1) && isAttachedNextEBCDIC(srcByte[i + 1]) && isAttachedPreviousEBCDIC(srcByte[i - 1]))
                        destByte[j] = (byte)231;   // ghain vasat
                    else
                        destByte[j] = (byte)232;  //ghain aval
                    break;
                case 36:  // '$' : se 3 noghte
                    if (i<(srcByte.length-1) && srcByte[i+1] == 58){
                        destByte[j] = (byte)152; j++; destByte[j] = (byte)255;
                    }
                    else
                        destByte[j] = (byte)153;
                    break;
                case 42:  // '*' : jim akhar
                    destByte[j] = (byte)154; j++; destByte[j] = (byte)255;
                    break;
                case 41:  //')' :  jim aval
                    destByte[j] = (byte)155;
                    break;
                case 59: //';' : che akhar
                    destByte[j] = (byte)156; j++; destByte[j] = (byte)255;
                    break;
                case 94: //'^' : che aval
                    destByte[j] = (byte)157;
                    break;
                case 47:  // '/' : kase
                    // do nothing just just decrement dest index
                    j--;
                    break;
                case 44:  //'<' : he jimi akhar
                    destByte[j] = (byte)158; j++; destByte[j] = (byte)255;
                    break;
                case 37:   // '%' : he jimi aval
                    destByte[j] = (byte)159;
                    break;
                case 95: //'_' : mim akhar
                    destByte[j] = (byte)244; j++; destByte[j] = (byte)255;
                    break;
                case 62: //'>' : khe akhar
                    destByte[j] = (byte)160; j++; destByte[j] = (byte)255;
                    break;
                case 63: //'?': khe aval
                    destByte[j] = (byte)161;
                    break;
                case 58:  //':' : dome horoofe : be, pe, te, se senoghte, fe, kaf, gaf
                    //do notthing just decrement dest index
                    j--;
                    break;
                case 35:  //'#' : dal
                    destByte[j] = (byte)162;
                    break;
                case 64:  //'@' : zal
                    destByte[j] = (byte)163;
                    break;
                case 39:  //''' : re
                    destByte[j] = (byte)164;
                    break;
                case 61: // '=' : ze
                    destByte[j] = (byte)165;
                    break;
                case 34: //'"' : hamze chasban
                    destByte[j] = (byte)142;
                    break;
                case 65:  // 'A' : sin
                    if(i<(srcByte.length-1) && srcByte[i+1] == 47){
                        destByte[j] = (byte)167; j++; destByte[j] = (byte)255;
                    }
                    else
                        destByte[j] = (byte)168;
                     break;
                case 66:  //'B' : shin
                    if(i<(srcByte.length-1) && srcByte[i+1] == 47){
                        destByte[j] = (byte) 169; j++; destByte[j] = (byte)255;
                    }
                    else
                        destByte[j] = (byte) 170;
                    break;
                case 67:  //'C' : sad
                    if(i<(srcByte.length-1) && srcByte[i+1] == 47){
                        destByte[j] = (byte)171; j++; destByte[j] = (byte)255;
                    }
                    else
                        destByte[j] = (byte)172;
                    break;
                case 68:  //'D' : zad
                    if(i<(srcByte.length-1) && srcByte[i+1] == 47){
                        destByte[j] = (byte)173; j++; destByte[j] = (byte)255;
                    }
                    else
                        destByte[j] = (byte)174;
                    break;
                case 69:  //'E' : ta
                    destByte[j] = (byte)175;
                    break;
                case 70:  //'F' : za
                    destByte[j] = (byte)224;
                    break;
                case 71:  //'G' : ain akhar
                    if(i>0 && isAttachedPreviousEBCDIC(srcByte[i - 1])){
                        destByte[j] = (byte)226; j++; destByte[j] = (byte)255;  // ain akhar chasban
                    }
                    else{
                        destByte[j] = (byte)225; j++; destByte[j] = (byte)255;  // ain akhar joda
                    }
                    break;
                case 72:  //'H' : ain aval
                    if((i >0) && i<(src.length()-1) && isAttachedNextEBCDIC(srcByte[i + 1]) && isAttachedPreviousEBCDIC(srcByte[i - 1]))
                        destByte[j] = (byte)227;   // ain vasat
                    else
                        destByte[j] = (byte)228;  //ain aval
                    break;
                case 73: //'I' : ghain akhar:
                    if(isAttachedPreviousEBCDIC(b)){
                        destByte[j] = (byte)230; j++; destByte[j] = (byte)255; // ghain akhar chasban
                    }
                    else{
                        destByte[j] = (byte)229; j++; destByte[j] = (byte)255; // ghain akhar joda
                    }
                    break;
                case 74: //'J' : fe
                    if((i< srcByte.length-1) && srcByte[i+1]== 58){
                        destByte[j] = (byte)233; j++; destByte[j] = (byte)255;
                    }
                    else
                        destByte[j] = (byte)234;
                    break;
                case 75: //'K' : ghaf akhar
                    destByte[j] = (byte)235; j++; destByte[j] = (byte)255;
                    break;
                case 76: //'L': ghaf aval
                    destByte[j] = (byte)236;
                    break;
                case 77: // 'M': kaf
                    if((i< srcByte.length-1) && srcByte[i+1]== 58){
                        destByte[j] = (byte)237; j++; destByte[j] = (byte)255;
                    }
                    else
                        destByte[j] = (byte)238;
                    break;
                case 78: //'N' : gaf
                    if((i< srcByte.length-1) && srcByte[i+1]== 58){
                        destByte[j] = (byte)239; j++; destByte[j] = (byte)255;
                    }
                    else
                        destByte[j] = (byte)240;
                    break;
                case 79:  //'O': lam akhar
                    destByte[j] = (byte)241; j++; destByte[j] = (byte)255;
                    break;
                case 80: //'P': lam aval
                    destByte[j] = (byte)243;
                    break;
                case 81: //'Q': la
                    destByte[j] = (byte)242;
                    break;
                case 82: //'R': mim aval
                    destByte[j] = (byte)245;
                    break;
                case 83: //'S': noon akhar
                    destByte[j] = (byte)246; j++; destByte[j] = (byte)255;
                    break;
                case 84: //'T' : noon aval
                    destByte[j] = (byte)247;
                    break;
                case 85: //'U' : vav
                    destByte[j] = (byte)248;
                    break;
                case 86: //'V' : he akhar
                    destByte[j] = (byte)249; j++; destByte[j] = (byte)255;
                    break;
                case 87: //'W': he aval, vasat
                    if((i >0) && i<(src.length()-1) && isAttachedNextEBCDIC(srcByte[i + 1]) && isAttachedPreviousEBCDIC(srcByte[i - 1]))
                        destByte[j] = (byte)250;   // he vasat
                    else
                        destByte[j] = (byte)251;  //he aval
                    break;
                case 88: //'X' : ye aval
                    destByte[j] = (byte)254;
                    break;
                case 89: //'Y' : ye akhar
                     if(i>0 && isAttachedPreviousEBCDIC(srcByte[i-1])){
                        destByte[j] = (byte)252; j++; destByte[j] = (byte)255; //ye akhar chasban
                     }
                    else{
                        destByte[j] = (byte)253; j++; destByte[j] = (byte)255; //ye akhar joda
                     }
                    break;

                case 48: //0
                    destByte[i] = (byte)128;
                    break;

                case 49: //1
                    destByte[i] = (byte)129;
                    break;

                case 50: //2
                    destByte[i] = (byte)130;
                    break;

                case 51: //3
                    destByte[i] = (byte)131;
                    break;

                case 52: //4
                    destByte[i] = (byte)132;
                    break;

                case 53: //5
                    destByte[i] = (byte)133;
                    break;

                case 54: //6
                    destByte[i] = (byte)134;
                    break;

                case 55: //7
                    destByte[i] = (byte)135;
                    break;

                case 56: //8
                    destByte[i] = (byte)136;
                    break;

                case 57: //9
                    destByte[i] = (byte)137;
                    break;
                case 45: // dash
                    destByte[i] = (byte)176;
                    break;
                default:
                    destByte[j] = (byte)255;
            }
            i++; j++;
        }

        String res = new String(destByte, "ISO-8859-1").trim();
        return res;

    }

    private static boolean isAttachedPreviousEBCDIC(byte b)
    {
        boolean result;
        switch(b){

            case 22:  // '"' :  hamze
                result = true;
                break;
            case 40:  // '(' :  be
                result = true;
                break;
            case 43:  // '+' :  pe
                result = true;
                break;
            case 124: // '|' "  te
                result = true;
                break;
            case 33:  // '!' :  ghain aval, vasat
                result = true;
                break;
            case 36:  // '$' :  se 3 noghte
                result = true;
                break;
            case 41:  // ')' :  jim  aval
                result = true;
                break;
            case 94:  // '^' : che aval
                result = true;
                break;
            case 37:  // '%' : he jimi aval
                result = true;
                break;
            case 63:  // '?' : khe aval
                result = true;
                break;
            case 65: // 'A' : sin
                result = true;
                break;
            case 66: // 'B' : shin
                result = true;
                break;
            case 67: // 'C' : sad
                result = true;
                break;
            case 68: // 'D' : zad
                result = true;
                break;
            case 69: // 'E' : ta
                result = true;
                break;
            case 70: // 'F' : za
                result = true;
                break;
            case 72: // 'H' : ain aval
                result = true;
                break;
            case 74: // 'J' : fe
                result = true;
                break;
            case 76: // 'L' : ghaf aval
                result = true;
                break;
            case 77: // 'M' : kaf
                result = true;
                break;
            case 78: // 'N' : gaf
                result = true;
                break;
            case 80: // 'P' : lam aval
                result = true;
                break;
            case 82: // 'R' : mim aval
                result = true;
                break;
            case 84: // 'T' : noon aval
                result = true;
                break;
            case 87: // he aval
                result = true;
                break;
            case 88: // ye aval
                result = true;
                break;
            default:
                result = false;
        }
        return result;
    }

    private static boolean isAttachedNextEBCDIC(byte b)
    {

        boolean result = true;
        if (b == 46)  // '.' : alef joda
            result = false;
        return result;
    }

    /**
     * Due to limitation in ATM screen width, 
     * This method eliminates space characters and single cotation characters that exists in src parameter
     * To one corresponding character 
     * @param src
     * @return
     */

    private static String eliminateSpace (String src)
    {
        StringBuilder sb = new StringBuilder();
        int spaceCount = 0;
        for(int i = 0; i < src.length(); i++)
        {
            if(src.charAt(i) != 32)
            {
                sb.append(src.charAt(i));
                if(src.charAt(i) == 39)  //  singlr cotation  ' ' '  character
                    if(i < src.length() && src.charAt(i+1) == 39)
                        i++;
                spaceCount = 0;
                continue;
            }
            spaceCount++;
            if(spaceCount == 1)
                sb.append(src.charAt(i));
        }
        return sb.toString();
    }

    public static String makeMeWindows(String myStr) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < myStr.length(); i++) {
            switch (myStr.charAt(i)) {
                case 32: // space
                    sb.append((char) 32);
                    break;
                case 128:// 0
                    sb.append("0");
                    break;
                case 129:// 1
                    sb.append("1");
                    break;
                case 130:// 2
                    sb.append("2");
                    break;
                case 131:// 3
                    sb.append("3");
                    break;
                case 132:// 4
                    sb.append("4");
                    break;
                case 133:// 5
                    sb.append("5");
                    break;
                case 134:// 6
                    sb.append("6");
                    break;
                case 135:// 7
                    sb.append("7");
                    break;
                case 136:// 8
                    sb.append("8");
                    break;
                case 137:// 9
                    sb.append("9");
                    break;
                case 138:// ¡
                    sb.append((char) 1644);
                    break;
                case 139: //taghsim
                    sb.append('%');
                    break;
                case 140:// ?
                    sb.append((char) 1567);
                    break;
                case 141:// alef kola dar
                    sb.append((char) 1570);
                    break;
                case 142:// ye kochek
                    sb.append((char) 1610);
                    break;
                case 143://
                    sb.append((char) 32);
                    break;
                case 144:// 9
                    sb.append((char) 32);
                    break;
                case 145: //keshidegi
                    sb.append((char) 1600);
                    break;
                case 146: //alef kolah dar 1570
                    sb.append((char) 1570);
                    break;
                case 147: // alef 1575
                    sb.append((char) 1575);
                    break;
                case 148: // alef 1575
                    sb.append((char) 1575);
                    break;
                case 149: //hamze
                    sb.append((char) 1569);
                    break;
                case 150: //B   1576
                    sb.append((char) 1576);
                    break;
                case 151: //P   1662
                    sb.append((char) 1662);
                    break;
                case 152: //T 1578
                    sb.append((char) 1578);
                    break;
                case 153: //se laSe  1579
                    sb.append((char) 1579);
                    break;
                case 154: //jim    1580
                    sb.append((char) 1580);
                    break;
                case 155: //jim  akhar  1580
                    sb.append((char) 1580);
                    sb.append((char) 32);
                    break;
                case 156: //che 1670
                    sb.append((char) 1670);
                    break;
                case 157: //che  akhar 1670
                    sb.append((char) 1670);
                    sb.append((char) 32);
                    break;
                case 158: //he Hamal       1581
                    sb.append((char) 1581);
                    break;
                case 159: //he akhar       1581
                    sb.append((char) 1581);
                    sb.append((char) 32);
                    break;
                case 160: //khe      1582
                    sb.append((char) 1582);
                    break;
                case 161: //khe     1582
                    sb.append((char) 1582);
                    sb.append((char) 32);
                    break;
                case 162: //dal   1583
                    sb.append((char) 1583);
                    break;
                case 163: //1584
                    sb.append((char) 1584);
                    break;
                case 164: //r     1585
                    sb.append((char) 1585);
                    break;
                case 165: //z    1586
                    sb.append((char) 1586);
                    break;
                case 166: //zh     1688
                    sb.append((char) 1688);
                    break;
                case 167: //sin  1587
                    sb.append((char) 1587);
                    break;
                case 168: //sin  1587
                    sb.append((char) 1587);
                    break;
                case 169: //shin  1588
                    sb.append((char) 1588);
                    break;
                case 170: //shin  1588
                    sb.append((char) 1588);
                    break;
                case 171: //sad  1589
                    sb.append((char) 1589);
                    break;
                case 172: //sad  1589
                    sb.append((char) 1589);
                    sb.append((char) 32);
                    break;
                case 173: //zad  1590
                    sb.append((char) 1590);
                    break;
                case 174: //zad  1590
                    sb.append((char) 1590);
                    sb.append((char) 32);
                    break;
                case 175: //ta (t daste dar) - t Tanab  1591
                    sb.append((char) 1591);
                    break;
                case 224: //za v(z daste dar) - z Zohr   1592
                    sb.append((char) 1592);
                    break;
                case 225: //ein   1593
                    sb.append((char) 1593);
                    break;
                case 226: //ein   1593
                    sb.append((char) 1593);
                    break;
                case 227: //ein   1593
                    sb.append((char) 1593);
                    sb.append((char) 32);
                    break;
                case 228: //ein   1593
                    sb.append((char) 1593);
                    sb.append((char) 32);
                    break;
                case 229: //ghein   1594
                    sb.append((char) 1594);
                    break;
                case 230: //ghein   1594
                    sb.append((char) 1594);
                    break;
                case 231: //ghein   1594
                    sb.append((char) 1594);
                    sb.append((char) 32);
                    break;
                case 232: //ghein   1594
                    sb.append((char) 1594);
                    sb.append((char) 32);
                    break;
                case 233: //faa   1601
                    sb.append((char) 1601);
                    break;
                case 234: //ghaf   1602
                    sb.append((char) 1602);
                    break;
                case 235: //ghaf   1602
                    sb.append((char) 1602);
                    sb.append((char) 32);
                    break;
                case 236: //jim   1602
                    sb.append((char) 1580);
                    break;
                case 237: //kaf   1705 -  1603
                    sb.append((char) 1705);
                    break;
                case 238: //gaf   1711
                    sb.append((char) 1711);
                    break;
                case 239: //La   1711
                    sb.append((char) 1604);
                    sb.append((char) 1575);
                    break;
                case 240: //Lam   1604
                    sb.append((char) 1604);
                    sb.append((char) 1575);
                    break;
                case 241: //La   1604
                    sb.append((char) 1604);
                    break;
                case 242: //Lam   1604
                    sb.append((char) 1604);
                    sb.append((char) 32);
                    break;
                case 243: // mim 1605
                    sb.append((char) 1605);
                    break;
                case 244: // mim 1605
                    sb.append((char) 1605);
                    sb.append((char) 32);
                    break;
                case 245: // noon 1605
                    sb.append((char) 1606);
                    break;
                case 246: // noon 1605
                    sb.append((char) 1606);
                    sb.append((char) 32);
                    break;
                case 247: //vav     1608
                    sb.append((char) 1608);
                    break;
                case 248:  //he
                    sb.append((char) 1607);
                    break;
                case 249: // he
                    sb.append((char) 1607);
                    break;
                case 250:  //he 2 cheshm - Hooshang 1607
                    sb.append((char) 1607);
                    sb.append((char) 32);
                    break;
                case 251: //hamze - maEde  1574
                    sb.append((char) 1574);
                    break;
                case 252:  //ye - D
                    sb.append((char) 1610);
                    break;
                case 253:  //ye - D
                    sb.append((char) 1610);
                    sb.append((char) 32);
                    break;
                case 254:  //ye - D
                    sb.append((char) 1610);
                    sb.append((char) 32);
                    break;

                default:
                    int asc = myStr.charAt(i);
                    if (asc >= 32 && asc < 126)
                        sb.append((char) asc);
                    else {
                        sb.append("");
//                         System.out.println("asc of not known " + asc + ">" + myStr);
                    }
                     /*    else
                    sb.append((char) 32);*/


            }

        }
        return sb.toString();
    }
    public static String makeMeUnix(String myStr) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < myStr.length(); i++) {
            switch (myStr.charAt(i)) {
                case 1575: // alef
                    if ((i == 0) || (myStr.charAt(i - 1) == 1583) ||
                            (myStr.charAt(i - 1) == (char) 1584) ||
                            (myStr.charAt(i - 1) == (char) 1585) ||
                            (myStr.charAt(i - 1) == (char) 1586) ||
                            (myStr.charAt(i - 1) == (char) 1688) ||
                            (myStr.charAt(i - 1) == (char) 1608) ||
                            (myStr.charAt(i - 1) == (char) 32) ||
                            (myStr.charAt(i - 1) == (char) 1575) ||
                            (myStr.charAt(i - 1) ==
                                    (char) 1570)) { // alef tanha - baraye dal, zal, re, ze, zhe, vav, space va (i==0)
                        //                 int asc = myStr.charAt(i-1);
                        //                 System.out.println("alef gheire chasban be khater harfe ghabli ast : %d" + myStr.charAt(i-1) + " : " + asc);

                        sb.append((char) 147);
                    } else { // alef chasban baraye baghiye horoof va ( i > 0 )
                        //                int asc = myStr.charAt(i-1);
                        //                System.out.println("alef chasban be khater harfe ghabli ast : %d" + myStr.charAt(i-1) + " : " + asc);

                        sb.append((char) 148);
                    }
                    break;

                case 1570: //alef kolah dar
                    sb.append((char) 146);
                    break;
                case 1569: //Hamze
                    sb.append((char) 149);
                    break;
                case 1576: //B
                    if ((i == myStr.length() - 1) ||
                            (myStr.charAt(i + 1) == (char) 32)) {
                        // System.out.println("dd-ye akhar + " + myStr.charAt(i+1));
                        sb.append((char) 150);
                        sb.append((char) 143);
                        i++;
                    } else { // be vasat
                        // System.out.println("dd-ye vasat");
                        sb.append((char) 150);
                    }
                    break;
                case 1662: //P
                    if ((i == myStr.length() - 1) ||
                            (myStr.charAt(i + 1) == (char) 32)) {
                        // System.out.println("dd-ye akhar + " + myStr.charAt(i+1));
                        sb.append((char) 151);
                        sb.append((char) 143);
                        i++;
                    } else { // be vasat
                        // System.out.println("dd-ye vasat");
                        sb.append((char) 151);
                    }
                    break;
                case 1578: //T
                    if ((i == myStr.length() - 1) ||
                            (myStr.charAt(i + 1) == 32)) {
                        // System.out.println("dd-ye akhar + " + myStr.charAt(i+1));
                        sb.append((char) 152);
                        sb.append((char) 143);
                        i++;
                    } else { // be vasat
                        // System.out.println("dd-ye vasat");
                        sb.append((char) 152);
                    }
                    break;
                case 1579: //se laSe
                    if ((i == myStr.length() - 1) ||
                            (myStr.charAt(i + 1) == (char) 32)) {
                        // System.out.println("dd-ye akhar + " + myStr.charAt(i+1));
                        sb.append((char) 153);
                        sb.append((char) 143);
                        i++;
                    } else { // be vasat
                        // System.out.println("dd-ye vasat");
                        sb.append((char) 153);
                    }
                    break;
                case 1580: //jim
                    if ((i == myStr.length() - 1) ||
                            (myStr.charAt(i + 1) == (char) 32)) { // jim bozorg
                        sb.append((char) 155);
                        i++;
                    } else { // jim kocholoo
                        sb.append((char) 154);
                    }

                    break;
                case 1670: //che
                    if ((i == myStr.length() - 1) ||
                            (myStr.charAt(i + 1) == (char) 32)) { // che bozorg
                        sb.append((char) 157);
                        i++;
                    } else { // che kocholoo
                        sb.append((char) 156);
                    }

                    break;
                case 1581: //he Hamal
                    if ((i == myStr.length() - 1) ||
                            (myStr.charAt(i + 1) == (char) 32)) { // heye hamal bozorg
                        sb.append((char) 159);
                        i++;
                    } else { // heye hamal kocholoo
                        sb.append((char) 158);
                    }

                    break;
                case 1582: //khe
                    if ((i == myStr.length() - 1) ||
                            (myStr.charAt(i + 1) == (char) 32)) { // khe bozorg
                        sb.append((char) 161);
                        i++;
                    } else { // khe kocholoo
                        sb.append((char) 160);
                    }

                    break;
                case 1583: //dal
                    sb.append((char) 162);
                    break;
                case 1584: //zal
                    sb.append((char) 163);
                    break;
                case 1585: //r
                    sb.append((char) 164);
                    break;
                case 1586: //z
                    //System.out.println("zeeee");
                    sb.append((char) 165);
                    break;
                case 1688: //zh
                    sb.append((char) 166);
                    break;
                case 1587: //sin
                    if ((i == myStr.length() - 1) ||
                            (myStr.charAt(i + 1) == (char) 32)) { // sin bozorg
                        sb.append((char) 168);
                        //   sb.append((char)178);
//                        i++;                               //habib
                    } else {
                        sb.append((char) 167);
                    }
                    break;
                case 1588: //shin
                    if ((i == myStr.length() - 1) ||
                            (myStr.charAt(i + 1) == (char) 32)) { // shin bozorg
                        sb.append((char) 170);
                        //  sb.append((char)178);
//                        i++;                    //habib
                    } else {
                        sb.append((char) 169);
                    }
                    break;
                case 1589: //sad
                    if ((i == myStr.length() - 1) ||
                            (myStr.charAt(i + 1) == (char) 32)) { // sin bozorg
                        sb.append((char) 172);
                        //  sb.append((char)178);
                        i++;
                    } else {
                        sb.append((char) 171);
                    }
                    break;
                case 1590: //zad
                    if ((i == myStr.length() - 1) ||
                            (myStr.charAt(i + 1) == (char) 32)) { // sin bozorg
                        sb.append((char) 174);
                        // sb.append((char)178);
                        i++;
                    } else {
                        sb.append((char) 173);
                    }
                    break;

                case 1591: //ta (t daste dar) - t Tanab
                    sb.append((char) 175);
                    break;
                case 1592: //za v(z daste dar) - z Zohr
                    sb.append((char) 224);
                    break;
                case 1593: //ein
                    if ((i == myStr.length() - 1) ||
                            (myStr.charAt(i + 1) == 32)) { // eine akhar - mesle zare' ya  rabi'
                        if (( i==0 )|| (myStr.charAt(i - 1) == (char) 32) ||
                                (myStr.charAt(i - 1) == (char) 1583) ||
                                (myStr.charAt(i - 1) == (char) 1584) ||
                                (myStr.charAt(i - 1) == (char) 1585) ||
                                (myStr.charAt(i - 1) == (char) 1586) ||
                                (myStr.charAt(i - 1) == (char) 1688) ||
                                (myStr.charAt(i - 1) == (char) 1608) ||
                                (myStr.charAt(i - 1) == (char) 1575) ||
                                myStr.charAt(i - 1) ==
                                        (char) 1570) { // ghabli harfe gheire chasban ast : eine akhar zare' : 227
                            sb.append((char) 227);
                        } else { //chasbanha - mesle rabi'
                            sb.append((char) 228);
                        }
                        i++;
                    } else { // eine aval
                        if ((i == 0) ||
                                (myStr.charAt(i - 1) == (char) 32) ||
                                (myStr.charAt(i - 1) == (char) 1583) ||
                                (myStr.charAt(i - 1) == (char) 1584) ||
                                (myStr.charAt(i - 1) == (char) 1585) ||
                                (myStr.charAt(i - 1) == (char) 1586) ||
                                (myStr.charAt(i - 1) == (char) 1688) ||
                                (myStr.charAt(i - 1) == (char) 1608) ||
                                (myStr.charAt(i - 1) == (char) 1575) ||
                                myStr.charAt(i - 1) ==
                                        (char) 1570) { // eine aval - ya avale ya ghabli space as
                            sb.append((char) 225); // eine aval - mesle edalat
                        } else {
                            sb.append((char) 226); // eine vasat mesle ta'lim
                        }
                    }
                    break;
                case 1594: //ghein
                    if ((i == myStr.length() - 1) ||
                            (myStr.charAt(i + 1) == (char) 32)) { // gheine akhar - mesle dagh' ya  tigh
                        if (( i==0 )||
                                (myStr.charAt(i - 1) == (char) 32) ||
                                (myStr.charAt(i - 1) == (char) 1583) ||
                                (myStr.charAt(i - 1) == (char) 1584) ||
                                (myStr.charAt(i - 1) == (char) 1585) ||
                                (myStr.charAt(i - 1) == (char) 1586) ||
                                (myStr.charAt(i - 1) == (char) 1688) ||
                                (myStr.charAt(i - 1) == (char) 1608) ||
                                (myStr.charAt(i - 1) == (char) 1575) ||
                                myStr.charAt(i - 1) ==
                                        (char) 1570) { // ghabli harfe gheire chasban ast : gheine akhar dagh : 227
                            sb.append((char) 231); // mesle dagh
                        } else { //chasbanha - mesle tigh
                            sb.append((char) 232);
                        }
                        i++;
                    } else { // gheine aval
                        if ((i == 0) ||
                                (myStr.charAt(i - 1) == (char) 32) ||
                                (myStr.charAt(i - 1) == (char) 1583) ||
                                (myStr.charAt(i - 1) == (char) 1584) ||
                                (myStr.charAt(i - 1) == (char) 1585) ||
                                (myStr.charAt(i - 1) == (char) 1586) ||
                                (myStr.charAt(i - 1) == (char) 1688) ||
                                (myStr.charAt(i - 1) == (char) 1608) ||
                                (myStr.charAt(i - 1) == (char) 1575) ||
                                myStr.charAt(i - 1) ==
                                        (char) 1570) { // gheine aval - ya avale ya ghabli space as
                            sb.append((char) 229); // gheine aval - mesle ghazal
                        } else {
                            sb.append((char) 230); // gheine vasat mesle taghliz
                        }
                    }
                    break;
                case 1601: //f
                    //sb.append((char)233);
                    if ((i == myStr.length() - 1) ||
                            (myStr.charAt(i + 1) == (char) 32)) { // fa bozorg
                        sb.append((char) 233);
                        sb.append((char) 143);

                        i++;
                    } else {
                        sb.append((char) 233);
                    }
                    break;
                case 1602: //ghaf
                    if ((i == myStr.length() - 1) ||
                            (myStr.charAt(i + 1) == (char) 32)) { // ghaf bozorg
                        sb.append((char) 235);
                        i++;
                    } else {
                        sb.append((char) 234);
                    }
                    break;
                case 1705: //kaf
                case 1603: //kaf - hamze dar
                    if ((i == myStr.length() - 1) ||
                            (myStr.charAt(i + 1) == (char) 32)) {
                        //System.out.println("kaf akhar + ");
                        sb.append((char) 237); // kaf akhar
                        sb.append((char) 143);
                        i++;
                    } else { // kaf vasat
                        // System.out.println("kaf vasat");
                        sb.append((char) 237);
                    }

                    break;
                case 1711: //gaf
                    if ((i == myStr.length() - 1) ||
                            (myStr.charAt(i + 1) == 32)) {
                        // System.out.println("dd-ye akhar + " + myStr.charAt(i+1));
                        sb.append((char) 238); // gaf akhar
                        sb.append((char) 143);
                        i++;
                    } else { // gaf vasat
                        // System.out.println("dd-ye vasat");
                        sb.append((char) 238);
                    }
                    break;
                case 1604: //Lam
                    if ((i == myStr.length() - 1) ||
                            (myStr.charAt(i + 1) == (char) 32)) { // lam bozorg
                        sb.append((char) 242);
                        i++;
                    } else {
                        if ((myStr.charAt(i + 1) == (char) 1575)) { //la
                            sb.append((char) 240);
                            i++;
                        } else {
                            sb.append((char) 241); // ma'mool
                        }
                    }
                    break;
                case 1605: //mim
                    if ((i == myStr.length() - 1) ||
                            (myStr.charAt(i + 1) == (char) 32)) { // mim bozorg
                        sb.append((char) 244);
                        i++;
                    } else {
                        sb.append((char) 243);
                    }
                    break;
                case 1606: //noon
                    if ((i == myStr.length() - 1) ||
                            (myStr.charAt(i + 1) == (char) 32)) { // noone bozorg
                        sb.append((char) 246);
                        i++;
                    } else { // noone vasat
                        sb.append((char) 245);
                    }
                    break;
                case 1608: //vav
                    sb.append((char) 247);
                    break;
                case 1607: //he 2 cheshm - Hooshang
                    if ((i == myStr.length() - 1) ||
                            (myStr.charAt(i + 1) == (char) 32)) { // he akhar
                        sb.append((char) 250);
                        i++;
                    } else { // noone aval
                        if ((i == 0) ||
                                (myStr.charAt(i - 1) == (char) 32) ||
                                (myStr.charAt(i - 1) == (char) 1583) ||
                                (myStr.charAt(i - 1) == (char) 1584) ||
                                (myStr.charAt(i - 1) == (char) 1585) ||
                                (myStr.charAt(i - 1) == (char) 1586) ||
                                (myStr.charAt(i - 1) == (char) 1688) ||
                                (myStr.charAt(i - 1) == (char) 1608) ||
                                (myStr.charAt(i - 1) == (char) 1575) ||
                                myStr.charAt(i - 1) ==
                                        (char) 1570) { // noone aval - ya avale ya ghabli space as
                            sb.append((char) 248); // noone aval - mesle Hooshang
                        } else {
                            sb.append((char) 249); // noone vasat mesle shaHid
                        }
                    }

                    break;
                case 1740: //ye - D
                    if (i == 0) { // harfe aval
                        if (myStr.charAt(i + 1) ==
                                (char) 32) { // harfe ba'di space ast: ye akhar (254)
                            sb.append((char) 254); // ye akhar
                            i++;
                        } else {
                            sb.append((char) 252); // ye aval

                        }

                    } else if (i == myStr.length() - 1) { // harfe akhar
                        if (( i==0 )||
                                (myStr.charAt(i - 1) == (char) 32) ||
                                (myStr.charAt(i - 1) == (char) 1583) ||
                                (myStr.charAt(i - 1) == (char) 1584) ||
                                (myStr.charAt(i - 1) == (char) 1585) ||
                                (myStr.charAt(i - 1) == (char) 1586) ||
                                (myStr.charAt(i - 1) == (char) 1688) ||
                                (myStr.charAt(i - 1) == (char) 1608) ||
                                (myStr.charAt(i - 1) == (char) 1575) ||
                                myStr.charAt(i - 1) ==
                                        (char) 1570) { // ghabli harfe gheire chasban ast : ye akhar : 254
                            sb.append((char) 254); // ye khar
                        } else {
                            sb.append((char) 253); // ye akhar e chasbede be harfe ghabl
                        }


                    } else { // vasat
                        if (myStr.charAt(i + 1) ==
                                (char) 32) { // harfe ba'di space : pas ya ye khar ya ye akhare chasbide be harfe ghabl
                            if ( ( i==0 )||
                                    (myStr.charAt(i - 1) == (char) 32) ||
                                    (myStr.charAt(i - 1) == (char) 1583) ||
                                    (myStr.charAt(i - 1) == (char) 1584) ||
                                    (myStr.charAt(i - 1) == (char) 1585) ||
                                    (myStr.charAt(i - 1) == (char) 1586) ||
                                    (myStr.charAt(i - 1) == (char) 1688) ||
                                    (myStr.charAt(i - 1) == (char) 1608) ||
                                    (myStr.charAt(i - 1) == (char) 1575) ||
                                    myStr.charAt(i - 1) ==
                                            (char) 1570) { // ghabli harfe gheire chasban ast : ye akhar : 254
                                sb.append((char) 254);
                            } else { // harfe ghabli chasban : ye akhare chasbide be harfe ghabl
                                sb.append((char) 253);

                            }
                            i++;
                        } else { // ye aval

                            sb.append((char) 252);
                        }

                    }


                    break;
                case 1610: //ye - shift + x
                    if (i == 0) { // harfe aval
                        if (myStr.charAt(i + 1) ==
                                (char) 32) { // harfe ba'di space ast: ye akhar (254)
                            sb.append((char) 254); // ye akhar
                            i++;
                        } else {
                            sb.append((char) 252); // ye aval

                        }

                    } else if (i == myStr.length() - 1) { // harfe akhar
                        if (( i==0 )||
                                (myStr.charAt(i - 1) == (char) 32) ||
                                (myStr.charAt(i - 1) == (char) 1583) ||
                                (myStr.charAt(i - 1) == (char) 1584) ||
                                (myStr.charAt(i - 1) == (char) 1585) ||
                                (myStr.charAt(i - 1) == (char) 1586) ||
                                (myStr.charAt(i - 1) == (char) 1688) ||
                                (myStr.charAt(i - 1) == (char) 1608) ||
                                (myStr.charAt(i - 1) == (char) 1575) ||
                                myStr.charAt(i - 1) ==
                                        (char) 1570) { // ghabli harfe gheire chasban ast : ye akhar : 254
                            sb.append((char) 254); // ye khar
                        } else {
                            sb.append((char) 253); // ye akhar e chasbede be harfe ghabl
                        }


                    } else { // vasat
                        if (myStr.charAt(i + 1) ==
                                (char) 32) { // harfe ba'di space : pas ya ye khar ya ye akhare chasbide be harfe ghabl
                            if (( i==0 )||
                                    (myStr.charAt(i - 1) == (char) 32) ||
                                    (myStr.charAt(i - 1) == (char) 1583) ||
                                    (myStr.charAt(i - 1) == (char) 1584) ||
                                    (myStr.charAt(i - 1) == (char) 1585) ||
                                    (myStr.charAt(i - 1) == (char) 1586) ||
                                    (myStr.charAt(i - 1) == (char) 1688) ||
                                    (myStr.charAt(i - 1) == (char) 1608) ||
                                    (myStr.charAt(i - 1) == (char) 1575) ||
                                    myStr.charAt(i - 1) ==
                                            (char) 1570) { // ghabli harfe gheire chasban ast : ye akhar : 254
                                sb.append((char) 254);
                            } else { // harfe ghabli chasban : ye akhare chasbide be harfe ghabl
                                sb.append((char) 253);

                            }
                            i++;
                        } else { // ye aval

                            sb.append((char) 252);
                        }

                    }

                    break;
                case 1600: //keshidegi
                    sb.append("-");
                    break;
                case 1574: //hamze - maEde
                    sb.append((char) 251);
                    break;

                default:
                    int asc = myStr.charAt(i);
                    //System.out.println("asc of not known " + asc);
                    sb.append(myStr.charAt(i));
            }

        }
        return sb.toString();
    }

    public static String convertEBCDIC2Windows1256(String src)  throws UnsupportedEncodingException
    {
        src = src.trim();
        StringBuilder sb = new StringBuilder();
        byte[] srcByte = src.getBytes("ISO-8859-1");
        int i = 0;
        for(byte b: srcByte){
            switch (b) {

                case 48: //0
                    sb.append((char)0x30);
                    break;

                case 49: //1
                    sb.append((char)0x31);
                    break;

                case 50: //2
                    sb.append((char)0x32);
                    break;

                case 51: //3
                    sb.append((char)0x33);
                    break;

                case 52: //4
                    sb.append((char)0x34);
                    break;

                case 53: //5
                    sb.append((char)0x35);
                    break;

                case 54: //6
                    sb.append((char)0x36);
                    break;

                case 55: //7
                    sb.append((char)0x37);
                    break;

                case 56: //8
                    sb.append((char)0x38);
                    break;

                case 57: //9
                    sb.append((char)0x39);
                    break;
                case 45: // dash
//                    sb.append((char)0x96);
                    sb.append((char)0x2D);
                    break;

                case 91:   // '[' : zhe
                    sb.append((char)0x8E);
                    break;
                case 46:   // '.' alef
                    sb.append((char)0xC7);
                    break;
                case 60:   // '<' : alef chasban
                    sb.append((char)0xC7);
                    break;
                case 40:  // '(' : be
                    sb.append((char)0xC8);
                    break;
                case 43: // '+' : pe
                    sb.append((char)0x81);
                    break;
                case 124: // '|' : te
                    sb.append((char)0xCA);
                    break;
                case 33: // '!' : ghain
                    sb.append((char)0xDB);
                    break;
                case 36:  // '$' : se 3 noghte
                    sb.append((char)0xCB);
                    break;
                case 42:  // '*' : jim akhar
                    sb.append((char)0xCC);
                    if(i< srcByte.length-1 && srcByte[i+1] != 32)  sb.append((char)0x20);
                    break;
                case 41:  //')' :  jim aval
                    sb.append((char)0xCC);
                    break;
                case 59: //';' : che akhar
                    sb.append((char)0x8D);
                    if(i< srcByte.length-1 && srcByte[i+1] != 32)  sb.append((char)0x20);
                    break;
                case 94: //'^' : che aval
                    sb.append((char)0x8D);
                    break;
                case 47:  // '/' : kase chasban be horoofe: sin, shin, sad, zad
                    sb.append((char)0x20);
                    break;
                case 44:  //'<' : he jimi akhar
                    sb.append((char)0xCD);
                    if(i< srcByte.length-1 && srcByte[i+1] != 32)  sb.append((char)0x20);
                    break;
                case 37:   // '%' : he jimi aval
                    sb.append((char)0xCD);
                    break;
                case 95: //'_' : mim akhar
                    sb.append((char)0xE3);
                    if(i< srcByte.length-1 && srcByte[i+1] != 32)  sb.append((char)0x20);
                    break;
                case 62: //'>' : khe akhar
                    sb.append((char)0xCE);
                    if(i< srcByte.length-1 && srcByte[i+1] != 32)  sb.append((char)0x20);
                    break;
                case 63: //'?': khe aval
                    sb.append((char)0xCE);
                    break;
                case 58:  //':' : dome horoofe : be, pe, te, se senoghte, fe, kaf, gaf
                    sb.append((char)0x20);
                    break;
                case 35:  //'#' : dal
                    sb.append((char)0xCF);
                    break;
                case 64:  //'@' : zal
                    sb.append((char)0xD0);
                    break;
                case 39:  //''' : re
                    sb.append((char)0xD1);
                    break;
                case 61: // '=' : ze
                    sb.append((char)0xD2);
                    break;
                case 34: //'"' : hamze chasban
                    sb.append((char)0xC6);
                    break;
                case 65:  // 'A' : sin
                    sb.append((char)0xD3);
                    break;
                case 66:  //'B' : shin
                    sb.append((char)0xD4);
                    break;
                case 67:  //'C' : sad
                    sb.append((char)0xD5);
                    break;
                case 68:  //'D' : zad
                    sb.append((char)0xD6);
                    break;
                case 69:  //'E' : ta
                    sb.append((char)0xD8);
                    break;
                case 70:  //'F' : za
                    sb.append((char)0xD9);
                    break;
                case 71:  //'G' : ain akhar
                    sb.append((char)0xDA);
                    if(i< srcByte.length-1 && srcByte[i+1] != 32)  sb.append((char)0x20);
                    break;
                case 72:  //'H' : ain aval
                    sb.append((char)0xDA);
                    break;
                case 73: //'I' : ghain akhar:
                    sb.append((char)0xDB);
                    if(i< srcByte.length-1 && srcByte[i+1] != 32)  sb.append((char)0x20);
                    break;
                case 74: //'J' : fe
                    sb.append((char)0xDD);
                    break;
                case 75: //'K' : ghaf akhar
                    sb.append((char)0xDE);
                    if(i< srcByte.length-1 && srcByte[i+1] != 32)  sb.append((char)0x20);
                    break;
                case 76: //'L': ghaf aval
                    sb.append((char)0xDE);
                    break;
                case 77: // 'M': kaf
                    sb.append((char)0xDF);
                    break;
                case 78: //'N' : gaf
                    sb.append((char)0x90);
                    break;
                case 79:  //'O': lam akhar
                    sb.append((char)0xE1);
                    if(i< srcByte.length-1 && srcByte[i+1] != 32)  sb.append((char)0x20);
                    break;
                case 80: //'P': lam aval
                    sb.append((char)0xE1);
                    break;
                case 81: //'Q': la
                    sb.append((char)0xE1).append((char)0xC7);
                    break;
                case 82: //'R': mim aval
                    sb.append((char)0xE3);
                    break;
                case 83: //'S': noon akhar
                    sb.append((char)0xE4);
                    if(i< srcByte.length-1 && srcByte[i+1] != 32)  sb.append((char)0x20);
                    break;
                case 84: //'T' : noon aval
                    sb.append((char)0xE4);
                    break;
                case 85: //'U' : vav
                    sb.append((char)0xE6);
                    break;
                case 86: //'V' : he akhar
                    sb.append((char)0xE5);
                    if(i< srcByte.length-1 && srcByte[i+1] != 32)  sb.append((char)0x20);
                    break;
                case 87: //'W': he aval, vasat
                    sb.append((char)0xE5);
                    break;
                case 88: //'X' : ye aval
                    sb.append((char)0xED);
                    break;
                case 89: //'Y' : ye akhar
                    sb.append((char)0xED);
                    if(i< srcByte.length-1 && srcByte[i+1] != 32)  sb.append((char)0x20);
                    break;
                case 32:
                    sb.append((char)0x20);
                    break;
                case -84:  // che !
//                    sb.append((char)0x8D);   //چ
                    sb.append((char)0xCA);     //ت
//                    sb.append((char)0xCA);
                    break;
                default:
                    sb.append((char)srcByte[i]);

            }
            i++;
        }
        byte[] b = sb.toString().getBytes("ISO-8859-1");
        String s = new String(b, "CP1256");
        return s;
    }

    public static String convertWindows1256(byte[] srcByte)  throws UnsupportedEncodingException
    {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for(byte b: srcByte){
            switch (b) {

                case 48: //0
                    sb.append((char)0x30);
                    break;

                case 49: //1
                    sb.append((char)0x31);
                    break;

                case 50: //2
                    sb.append((char)0x32);
                    break;

                case 51: //3
                    sb.append((char)0x33);
                    break;

                case 52: //4
                    sb.append((char)0x34);
                    break;

                case 53: //5
                    sb.append((char)0x35);
                    break;

                case 54: //6
                    sb.append((char)0x36);
                    break;

                case 55: //7
                    sb.append((char)0x37);
                    break;

                case 56: //8
                    sb.append((char)0x38);
                    break;

                case 57: //9
                    sb.append((char)0x39);
                    break;
                case 45: // dash
//                    sb.append((char)0x96);
                    sb.append((char)0x2D);
                    break;

                case 91:   // '[' : zhe
                    sb.append((char)0x8E);
                    break;
                case 46:   // '.' alef
                    sb.append((char)0xC7);
                    break;
                case 60:   // '<' : alef chasban
                    sb.append((char)0xC7);
                    break;
                case 40:  // '(' : be
                    sb.append((char)0xC8);
                    break;
                case 43: // '+' : pe
                    sb.append((char)0x81);
                    break;
                case 124: // '|' : te
                    sb.append((char)0xCA);
                    break;
                case 33: // '!' : ghain
                    sb.append((char)0xDB);
                    break;
                case 36:  // '$' : se 3 noghte
                    sb.append((char)0xCB);
                    break;
                case 42:  // '*' : jim akhar
                    sb.append((char)0xCC);
                    if(i< srcByte.length-1 && srcByte[i+1] != 32)  sb.append((char)0x20);
                    break;
                case 41:  //')' :  jim aval
                    sb.append((char)0xCC);
                    break;
                case 59: //';' : che akhar
                    sb.append((char)0x8D);
                    if(i< srcByte.length-1 && srcByte[i+1] != 32)  sb.append((char)0x20);
                    break;
                case 94: //'^' : che aval
                    sb.append((char)0x8D);
                    break;
                case 47:  // '/' : kase chasban be horoofe: sin, shin, sad, zad
                    sb.append((char)0x20);
                    break;
                case 44:  //'<' : he jimi akhar
                    sb.append((char)0xCD);
                    if(i< srcByte.length-1 && srcByte[i+1] != 32)  sb.append((char)0x20);
                    break;
                case 37:   // '%' : he jimi aval
                    sb.append((char)0xCD);
                    break;
                case 95: //'_' : mim akhar
                    sb.append((char)0xE3);
                    if(i< srcByte.length-1 && srcByte[i+1] != 32)  sb.append((char)0x20);
                    break;
                case 62: //'>' : khe akhar
                    sb.append((char)0xCE);
                    if(i< srcByte.length-1 && srcByte[i+1] != 32)  sb.append((char)0x20);
                    break;
                case 63: //'?': khe aval
                    sb.append((char)0xCE);
                    break;
                case 58:  //':' : dome horoofe : be, pe, te, se senoghte, fe, kaf, gaf
                    sb.append((char)0x20);
                    break;
                case 35:  //'#' : dal
                    sb.append((char)0xCF);
                    break;
                case 64:  //'@' : zal
                    sb.append((char)0xD0);
                    break;
                case 39:  //''' : re
                    sb.append((char)0xD1);
                    break;
                case 61: // '=' : ze
                    sb.append((char)0xD2);
                    break;
                case 34: //'"' : hamze chasban
                    sb.append((char)0xC6);
                    break;
                case 65:  // 'A' : sin
                    sb.append((char)0xD3);
                    break;
                case 66:  //'B' : shin
                    sb.append((char)0xD4);
                    break;
                case 67:  //'C' : sad
                    sb.append((char)0xD5);
                    break;
                case 68:  //'D' : zad
                    sb.append((char)0xD6);
                    break;
                case 69:  //'E' : ta
                    sb.append((char)0xD8);
                    break;
                case 70:  //'F' : za
                    sb.append((char)0xD9);
                    break;
                case 71:  //'G' : ain akhar
                    sb.append((char)0xDA);
                    if(i< srcByte.length-1 && srcByte[i+1] != 32)  sb.append((char)0x20);
                    break;
                case 72:  //'H' : ain aval
                    sb.append((char)0xDA);
                    break;
                case 73: //'I' : ghain akhar:
                    sb.append((char)0xDB);
                    if(i< srcByte.length-1 && srcByte[i+1] != 32)  sb.append((char)0x20);
                    break;
                case 74: //'J' : fe
                    sb.append((char)0xDD);
                    break;
                case 75: //'K' : ghaf akhar
                    sb.append((char)0xDE);
                    if(i< srcByte.length-1 && srcByte[i+1] != 32)  sb.append((char)0x20);
                    break;
                case 76: //'L': ghaf aval
                    sb.append((char)0xDE);
                    break;
                case 77: // 'M': kaf
                    sb.append((char)0xDF);
                    break;
                case 78: //'N' : gaf
                    sb.append((char)0x90);
                    break;
                case 79:  //'O': lam akhar
                    sb.append((char)0xE1);
                    if(i< srcByte.length-1 && srcByte[i+1] != 32)  sb.append((char)0x20);
                    break;
                case 80: //'P': lam aval
                    sb.append((char)0xE1);
                    break;
                case 81: //'Q': la
                    sb.append((char)0xE1).append((char)0xC7);
                    break;
                case 82: //'R': mim aval
                    sb.append((char)0xE3);
                    break;
                case 83: //'S': noon akhar
                    sb.append((char)0xE4);
                    if(i< srcByte.length-1 && srcByte[i+1] != 32)  sb.append((char)0x20);
                    break;
                case 84: //'T' : noon aval
                    sb.append((char)0xE4);
                    break;
                case 85: //'U' : vav
                    sb.append((char)0xE6);
                    break;
                case 86: //'V' : he akhar
                    sb.append((char)0xE5);
                    if(i< srcByte.length-1 && srcByte[i+1] != 32)  sb.append((char)0x20);
                    break;
                case 87: //'W': he aval, vasat
                    sb.append((char)0xE5);
                    break;
                case 88: //'X' : ye aval
                    sb.append((char)0xED);
                    break;
                case 89: //'Y' : ye akhar
                    sb.append((char)0xED);
                    if(i< srcByte.length-1 && srcByte[i+1] != 32)  sb.append((char)0x20);
                    break;
                case 32:
                    sb.append((char)0x20);
                    break;
                case -84:  // che !
//                    sb.append((char)0x8D);   //چ
                    sb.append((char)0xCA);     //ت
//                    sb.append((char)0xCA);
                    break;
                default:
                    sb.append((char)srcByte[i]);

            }
            i++;
        }
        byte[] b = sb.toString().getBytes("ISO-8859-1");
        String s = new String(b, "CP1256");
        return s;
    }


}


