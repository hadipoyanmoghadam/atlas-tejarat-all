package branch.dpi.atlas.service.cm.handler.manzume;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.entity.cms.Cmparam;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.iso2000.ActionCode;
//import dpi.atlas.service.cm.rtgs.message.exception.ValidationException;
import dpi.atlas.model.ModelException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;

import java.sql.SQLException;
import java.util.*;

import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.validator.routines.checkdigit.CheckDigitException;
import org.apache.commons.validator.routines.checkdigit.VerhoeffCheckDigit;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.ISOException;
import dpi.atlas.util.StringUtils;

import java.math.BigInteger;
/**
 * Created by IntelliJ IDEA.
 * User: Sahar Amjadi
 * Date: Oct 5, 2011
 * Time: 10:31:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class CheckDigits4Manzume {
    public static boolean isCheckDigit(String accNo, String[] payCds, String transAmnt)
            throws ISOException, ModelException, SQLException {
        boolean isCheckDigit = false;
        String algNo = ChannelFacadeNew.getAlgorithmNo(accNo).trim();
        if (!"".equals(algNo))
            isCheckDigit = getCaseNo(isCheckDigit, algNo, accNo, payCds, transAmnt);

        else
            throw new ModelException(Constants.NO_RELATED_ALG_EXIST);

        return isCheckDigit;
    }

    public static boolean isCheckDigitNew(String accNo, String[] payCds, String transAmnt, CMMessage msg)
            throws ISOException, ModelException, SQLException, CMFault {
        boolean isCheckDigit = false;
        String algNo = String.valueOf(ChannelFacadeNew.getAlgorithmDetails(accNo).get("algNo")).trim();
        String chnIdChk = String.valueOf(ChannelFacadeNew.getAlgorithmDetails(accNo).get("chnIdChk")).trim();
        String FWSIdChk = chnIdChk.substring(0, 1); //1
        String OTWSIdChk = chnIdChk.substring(1, 2); //2
        String ENEXIdChk = chnIdChk.substring(2, 3); //3
        String CBIdChk = chnIdChk.substring(3, 4); //4

//        String channelType = null != msg.getAttributeAsString(dpi.atlas.service.cm.imf.Fields.CHANNEL_TYPE) && !msg.getAttributeAsString(dpi.atlas.service.cm.imf.Fields.CHANNEL_TYPE).equals("") ? msg.getAttributeAsString(dpi.atlas.service.cm.imf.Fields.CHANNEL_TYPE) : "";
//        if (channelType.equals(Constants.REP_STOCK_BOURCE) && FWSIdChk.equals("1"))
//            return true;
//        if (channelType.equals(dpi.atlas.service.cm.imf.Fields.SERVICE_OTWS) && OTWSIdChk.equals("1"))
//            return true;
//        if (channelType.equals(Constants.ENERGY_BOURCE) && ENEXIdChk.equals("1"))
//            return true;
//        if (channelType.equals(Constants.CORPORATE_BANKING) && CBIdChk.equals("1"))
//            return true;

//        else
        if (!isCheckDigit) {

            String fPayCode = payCds[0];
            String sPayCode = payCds[1];
            if ("".equals(fPayCode.trim()) && "".equals(sPayCode.trim())) {
                msg.setAttribute(dpi.atlas.service.cm.imf.Fields.AUTHORISED, Constants.NOTAUTHORISED);
                msg.setAttribute(Params.ACTION_CODE, ActionCode.DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT);
                msg.setAttribute(Params.ACTION_MESSAGE, "Account number=" + accNo + " is not permitted to be credit");
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT));
            }
            try {
                if ((!payCds[0].equalsIgnoreCase("") && (new BigInteger(payCds[0])).equals(BigInteger.ZERO)) ||
                        (!payCds[1].equalsIgnoreCase("") && (new BigInteger(payCds[1])).equals(BigInteger.ZERO))) {
                    msg.setAttribute(dpi.atlas.service.cm.imf.Fields.AUTHORISED, Constants.NOTAUTHORISED);
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT);
                    msg.setAttribute(Params.ACTION_MESSAGE, "Account number=" + accNo + " is not permitted to be credit");
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT));
                }
            } catch (NumberFormatException e) {
                msg.setAttribute(dpi.atlas.service.cm.imf.Fields.AUTHORISED, Constants.NOTAUTHORISED);
                msg.setAttribute(Params.ACTION_CODE, ActionCode.PAYMENT_CODE_IS_INVALID);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.PAYMENT_CODE_IS_INVALID));
            }

            if (!"".equals(algNo))
                isCheckDigit = getCaseNo(isCheckDigit, algNo, accNo, payCds, transAmnt);

            else
                throw new ModelException(Constants.NO_RELATED_ALG_EXIST);
        }
        return isCheckDigit;
    }

    private static boolean getCaseNo(boolean isCheckDigit, String algNo, String accNo, String[] payCds, String transAmnt) throws ISOException, ModelException {
        int ialgNo = Integer.parseInt(algNo);
        switch (ialgNo) {
            case 1:
                isCheckDigit = CheckDigits4Manzume.taminEjtemaeeAlg1(payCds, transAmnt);
                break;
            case 2:
                isCheckDigit = CheckDigits4Manzume.emdadCommiteeAlg1(payCds, transAmnt, accNo);
                break;
            case 3:
                isCheckDigit = CheckDigits4Manzume.ekramEetamAlg(payCds);
                break;
            case 4:
                isCheckDigit = CheckDigits4Manzume.irancellAlgrthm(payCds);
                break;
            case 5:
                isCheckDigit = CheckDigits4Manzume.isDanaInsurance(payCds);
                break;
            case 6:
                isCheckDigit = CheckDigits4Manzume.isStudntRefahFund(payCds);
                break;
            case 7:
                isCheckDigit = CheckDigits4Manzume.isIranInsurance(payCds, transAmnt);
                break;
            case 8:
                isCheckDigit = CheckDigits4Manzume.isParsianInsurance(payCds);
                break;
            case 9:
                isCheckDigit = CheckDigits4Manzume.isQomSeminary(payCds);
                break;
            case 10:
                isCheckDigit = CheckDigits4Manzume.isMAMKerman(payCds);
                break;
            case 11:
                isCheckDigit = CheckDigits4Manzume.isEmdadCommiteeAlg2(payCds, accNo);
                break;
            case 12:
                isCheckDigit = CheckDigits4Manzume.isZamyadCoAlgorithm(payCds, accNo, transAmnt);
                break;
            case 13:
                isCheckDigit = CheckDigits4Manzume.isMashhadUniAlgorithm(payCds);
                break;
            case 14:
                isCheckDigit = CheckDigits4Manzume.isType1Algorithm(payCds, accNo, transAmnt);
                break;
            case 15:
                isCheckDigit = CheckDigits4Manzume.isType2Algorithm(payCds);
                break;
            case 16:
                isCheckDigit = CheckDigits4Manzume.isType3Algorithm(payCds);
                break;
            case 17:
                isCheckDigit = CheckDigits4Manzume.isType4Algorithm(payCds);
                break;
            case 18:
                isCheckDigit = CheckDigits4Manzume.isType5Algorithm(payCds);
                break;
            case 19:
                isCheckDigit = CheckDigits4Manzume.isType6Algorithm(payCds);
                break;
            case 20:
                isCheckDigit = CheckDigits4Manzume.isType7Algorithm(payCds);
                break;
            case 21:
                isCheckDigit = CheckDigits4Manzume.isType8Algorithm(payCds);
                break;
            case 22:
                isCheckDigit = CheckDigits4Manzume.isIranLeasingAlgorithm(payCds);
                break;
            case 23:
                isCheckDigit = CheckDigits4Manzume.isNationalCodeAlgorithm(payCds);
                break;
            case 24:
                isCheckDigit = CheckDigits4Manzume.isLifeAndSavingAlgorithm(payCds);
                break;
            case 25:
                isCheckDigit = CheckDigits4Manzume.isSajadAlgorithm(payCds);
                break;
            case 26:
                isCheckDigit = CheckDigits4Manzume.isMahanAlgorithm(payCds);
                break;
            case 27:
                isCheckDigit = CheckDigits4Manzume.isIranInsuranceNewAlgorithm(payCds);
                break;
            case 28:
                isCheckDigit = CheckDigits4Manzume.isSaipaAlgorithm(payCds, accNo, transAmnt);
                break;
            case 29:
                isCheckDigit = CheckDigits4Manzume.isLeasingAlgorithm(payCds, accNo, transAmnt);
                break;
            case 30:
                isCheckDigit = CheckDigits4Manzume.isEight1Algorithm(payCds);
                break;
            case 31:
                isCheckDigit = CheckDigits4Manzume.isPeymanrhAlgorithm(payCds);
                break;
            case 32:
                isCheckDigit = CheckDigits4Manzume.isEmamZamanAlgorithm(payCds);
                break;
            case 33:
                isCheckDigit = CheckDigits4Manzume.isElmiKarbordiArakAlgorithm(payCds);
                break;
            case 34:
                isCheckDigit = CheckDigits4Manzume.isOloomTahghighatAlgorithm(payCds);
                break;
            case 35:
                isCheckDigit = CheckDigits4Manzume.isbourseAlgorithm(payCds);
                break;
            case 36:
                isCheckDigit = CheckDigits4Manzume.is4and14Algorithm(payCds);
                break;
            case 37:
                isCheckDigit = CheckDigits4Manzume.is13Algorithm(payCds);
                break;
            case 38:
                isCheckDigit = CheckDigits4Manzume.is17Algorithm(payCds);
                break;
            case 39:
                isCheckDigit = CheckDigits4Manzume.isTabrizPetrochemicalsAlgorithm(payCds, accNo);
                break;
            case 40:
                isCheckDigit = CheckDigits4Manzume.isIKCoAlgorithm(payCds);
                break;
            case 41:
                isCheckDigit = CheckDigits4Manzume.isNationalIdAlgorithm(payCds);
                break;
            case 42:
                isCheckDigit = CheckDigits4Manzume.isNationalIdAndCodeAlgorithm(payCds);
                break;
            case 43:    //1698012758110172 7698013046910146
                isCheckDigit = CheckDigits4Manzume.isStckAlgorithm(payCds, "4646111135");
                break;
            case 44:    //78356629361504928
                isCheckDigit = CheckDigits4Manzume.isGolestanTransport(payCds);
                break;
            case 45:
                isCheckDigit = CheckDigits4Manzume.isType1AlgorithmWithZeroAmount(payCds, accNo, "0");
                break;
            case 46:
                isCheckDigit = CheckDigits4Manzume.isTehranUniversity(payCds, accNo, "0");
                break;
            case 47:
                isCheckDigit = CheckDigits4Manzume.isValidVerhoef(payCds, accNo, transAmnt);
                break;
            case 48:
                isCheckDigit = CheckDigits4Manzume.isAlborzUnivAlgorithm(payCds);
                break;
            case 49:
                isCheckDigit = CheckDigits4Manzume.isGilanAlgorithm(payCds, accNo, transAmnt);
                break;
            case 50:
                isCheckDigit = CheckDigits4Manzume.isTracktorTabrizAlgorithm(payCds, accNo, transAmnt);
                break;
            case 57:
                isCheckDigit = CheckDigits4Manzume.isKordestanAlgorithm(payCds, accNo);
                break;
            case 58:
                isCheckDigit = CheckDigits4Manzume.isPetroshimiTabrizAlgorithm(payCds, accNo, transAmnt);
                break;
            case 59:
                isCheckDigit = CheckDigits4Manzume.isValiasrAccountAlgorithm(payCds);
                break;

            case 60:
                isCheckDigit = CheckDigits4Manzume.isAsiaAlgorithm(payCds);
                break;
            case 61:
                isCheckDigit = CheckDigits4Manzume.isCheckAlgorithm(payCds);
                break;
            case 62:
                isCheckDigit = CheckDigits4Manzume.isKhAlgorithm(payCds);
                break;
            case 63:
                isCheckDigit = CheckDigits4Manzume.isKhhAlgorithm(payCds);
                break;
            case 64:
                isCheckDigit = CheckDigits4Manzume.is11Algorithm(payCds);
                break;
            case 65:
                isCheckDigit = CheckDigits4Manzume.isTaminAlgorithm(payCds);
                break;
            case 66:
                isCheckDigit = CheckDigits4Manzume.isLizAlgorithm(payCds);
                break;
            case 67:
                isCheckDigit = CheckDigits4Manzume.is44Algorithm(payCds, accNo, transAmnt);
                break;
            case 68:
                isCheckDigit = CheckDigits4Manzume.isType31Algorithm(payCds);
                break;
            case 69:
                isCheckDigit = CheckDigits4Manzume.isType35Algorithm(payCds);
                break;
            case 70:
                isCheckDigit = CheckDigits4Manzume.isType19Algorithm(payCds);
                break;
            case 71:
                isCheckDigit = CheckDigits4Manzume.isType37Algorithm(payCds);
                break;
            case 72:
                isCheckDigit = CheckDigits4Manzume.isType23Algorithm(payCds, transAmnt);
                break;
//            case 73:
//                isCheckDigit = CheckDigits4Manzume.isType22Algorithm(payCds, accNo);
//                break;
            case 74:
                isCheckDigit = CheckDigits4Manzume.isBankCardValid(payCds);
                break;
            case 75:
                isCheckDigit = CheckDigits4Manzume.isDebtCodeAlgorithm(payCds);
                break;
            case 76:
                isCheckDigit = CheckDigits4Manzume.isGeneralAlgorithm(payCds);
                break;
            case 77:
                isCheckDigit = CheckDigits4Manzume.isHamrahAvalAlgorithm(payCds);
                break;
            case 78:
                isCheckDigit = CheckDigits4Manzume.isSobhanAlgorithm(payCds);
                break;
            case 79:
                isCheckDigit = CheckDigits4Manzume.isGBitCodeAlgorithm(payCds);
                break;
            case 80:
                isCheckDigit = CheckDigits4Manzume.isType32Algorithm(payCds);
                break;
            case 81:
                isCheckDigit = CheckDigits4Manzume.isFardaCarIndustryAlgorithm(payCds);
                break;
            case 82:
                isCheckDigit = CheckDigits4Manzume.isIRIStudentOrganization(payCds);
                break;
            case 83:
                isCheckDigit = CheckDigits4Manzume.isDamavandMunicipality(payCds);


        }
        return isCheckDigit;
    }


    public static boolean isFardaCarIndustryAlgorithm(String[] paymentId)throws ModelException{

        if (!"".equals(paymentId[0]) && !"".equals(paymentId[1]) && !paymentId[0].equals(paymentId[1]))
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);

        String payCode = paymentId[0];
        if ("".equals(payCode) && !"".equals(paymentId[1])) {
            payCode = paymentId[1];
        }

        int[] coefficient =  {8,7,6,5,4,3,2,1,9,10,11,12,13};
        if(payCode.length() != 15 )
            return false;

        if(!StringUtils.isNumeric(payCode))
            return false;

        String tempStr = payCode.substring(0, 13);
        int sum = 0;

        for(int i=0; i< tempStr.length(); i++ ){
            int b = Integer.parseInt(String.valueOf(tempStr.charAt(i)));
            sum += b * coefficient[i];
        }
        return (sum % 99) == Integer.parseInt(payCode.substring(13));
    }


    public static boolean isIRIStudentOrganization(String[] paymentId)throws ModelException{

        if (!"".equals(paymentId[0]) && !"".equals(paymentId[1]) && !paymentId[0].equals(paymentId[1]))
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);

        String payCode = paymentId[0];
        if ("".equals(payCode) && !"".equals(paymentId[1])) {
            payCode = paymentId[1];
        }
        int[] coefficient =  {6,5,4,3,2,1};
        if(payCode.length() != 8 )
            return false;

        if(!StringUtils.isNumeric(payCode))
            return false;

        String tempStr = payCode.substring(0, 6);
        int sum = 0;

        for(int i=0; i< tempStr.length(); i++ ){
            int b = Integer.parseInt(String.valueOf(tempStr.charAt(i)));
            sum += b * coefficient[i];
        }
        return (sum % 33) == Integer.parseInt(payCode.substring(6));
    }


    public static boolean isDamavandMunicipality(String[] paymentId)throws ModelException{

        if (!"".equals(paymentId[0]) && !"".equals(paymentId[1]) && !paymentId[0].equals(paymentId[1]))
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);

        String payCode = paymentId[0];
        if ("".equals(payCode) && !"".equals(paymentId[1])) {
            payCode = paymentId[1];
        }
        if(payCode.length() != 9 )
            return false;

        if(!StringUtils.isNumeric(payCode))
            return false;

        String currentYear = DateUtil.getSystemDate().substring(1,4);
        return  (payCode.substring(0,3).equals(currentYear) && (Integer.parseInt(payCode.substring(3))>=1));

    }


    //    isType32Algorithm
    public static boolean isType32Algorithm(String[] payCds) throws ModelException, ISOException {
        boolean is32CheckDigit = true;
        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1]))
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }
        payCode = ISOUtil.zeropad(payCode, 13);

        if (!StringUtils.isNumeric(payCode)) {
            is32CheckDigit = false;
            return false;
        }

        if (payCode.length() != 13) {
            is32CheckDigit = false;
            return false;
        }

        int chkDg = Integer.parseInt(payCode.substring(payCode.length() - 1, payCode.length()));
        if (calc32CkDg(payCode) != chkDg) {
            is32CheckDigit = false;
        }

        return is32CheckDigit;
    }

    public static int calc32CkDg(String payId) {
        int rem = 0;
        int checkDigit = 0;
        int sumkargozar = 0;
        int kargozar = 0;
        for (int k = 10; k >= 0; k -= 2) {
            sumkargozar += Integer.parseInt(payId.substring(k, k + 1));
        }
        for (int i = 12; i > 0; i -= 2) {
            int digit = Integer.parseInt(payId.substring(i - 1, i));
            kargozar = 2 * digit;
            if (kargozar == 10)
                sumkargozar += 10;
            else if (kargozar == 12)
                sumkargozar += 3;
            else if (kargozar == 14)
                sumkargozar += 5;
            else if (kargozar == 16)
                sumkargozar += 7;
            else if (kargozar == 18)
                sumkargozar += 9;
            else sumkargozar = sumkargozar + kargozar;
        }
        rem = (sumkargozar % 10);
        if (rem != 0)
            checkDigit = 10 - rem;
        return checkDigit;
    }
//    End of isType32Algorithm

    // SobhanAlgorithm
    public static boolean isSobhanAlgorithm(String[] payCds) throws ModelException, ISOException {
        boolean isSobhanChkDgt = true;
        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() > 7) {
            isSobhanChkDgt = false;
            return false;
        }

        if (payCode.length() < 7)
            payCode = ISOUtil.zeropad(payCode, 7);

        if (!StringUtils.isNumeric(payCode)) {
            isSobhanChkDgt = false;
            return false;
        }

        String chkDgt = payCode.substring(payCode.length() - 2, payCode.length());

        if (!calcSobhanChkDigit(payCode).equals(chkDgt)) {
            isSobhanChkDgt = false;
        }

        return isSobhanChkDgt;

    }
    private static String calcSobhanChkDigit(String payCode) throws ISOException {
        int totalSum = 0;
        String checkDigit = "";
        int[] mazarebs = new int[]{4, 5, 6, 7, 8};
        for (int i = 5; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            totalSum += (digit * mazarebs[i - 1]);
        }
        checkDigit = String.valueOf(totalSum % 99);
        return checkDigit;
    }
    // End Of SobhanAlgorithm

    // GBit Algorithm
    public static boolean isGBitCodeAlgorithm(String[] payCds) throws ModelException, ISOException {
        boolean isGbitChkDgt = true;
        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() > 12) {
            isGbitChkDgt = false;
            return false;
        }

        if (payCode.length() < 12)
            payCode = ISOUtil.zeropad(payCode, 12);

        if (!StringUtils.isNumeric(payCode)) {
            isGbitChkDgt = false;
            return false;
        }


        if (calcGbit(payCode) != true) {
            isGbitChkDgt = false;
        }

        return isGbitChkDgt;

    }

    private static boolean calcGbit(String payCode) {
        int SumC3 = 0;
        int digitC3 = Integer.parseInt(payCode.substring(3, 4));
        int SumC2 = 0;
        int digitC2 = Integer.parseInt(payCode.substring(7, 8));
        int SumC1 = 0;
        int digitC1 = Integer.parseInt(payCode.substring(11, 12));
        boolean checkDigit= false;
        for (int i = 0; i < 3; i++) {
            SumC3 += Integer.parseInt(payCode.substring(i, i + 1));
        }
        for (int i = 4; i < 7; i++) {
            SumC2 += Integer.parseInt(payCode.substring(i, i + 1));
        }
        SumC2 = SumC2 + SumC3;
        for (int i = 8; i < 11; i++) {
            SumC1 += Integer.parseInt(payCode.substring(i, i + 1));
        }
        SumC1 = SumC1 + SumC2;
        if(((SumC3 % 3)== digitC3) && ((SumC2 % 6)== digitC2) && ((SumC1 % 9)==digitC1)){
            checkDigit = true;
        }
        return checkDigit;
    }
//end of GBit Algorithm


    //  HamrahAvalAlgorithm
    public static boolean isHamrahAvalAlgorithm(String[] payCds) throws ModelException, ISOException {
        boolean isHamrahAvalDgt = true;
        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() != 14) {
            isHamrahAvalDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isHamrahAvalDgt = false;
            return false;
        }

        String chkDgt = payCode.substring(payCode.length() - 2, payCode.length());

        if (!calcHamrahAvalChkDigit(payCode).equals(chkDgt)) {
            isHamrahAvalDgt = false;
        }

        return isHamrahAvalDgt;

    }
    private static String calcHamrahAvalChkDigit(String payCode) throws ISOException {
        int dSum = 0;
        String checkDigit;
        int[] mazarebs = {29, 27, 23, 19, 17, 29, 27, 23, 19, 17, 29, 27};
        for (int i = 12; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            dSum += (digit * mazarebs[i - 1]);
        }
        checkDigit = ISOUtil.zeropad(String.valueOf(dSum % 99), 2);
        return checkDigit;
    }
//  End of HamrahAvalAlgorithm

    // Tamin algorithm

    public static boolean taminEjtemaeeAlg1(String[] payCodes, String transAmnt) throws ISOException {
        boolean isTaminCheckDigit = true;
        transAmnt = ISOUtil.zeropad(transAmnt, 12);
        for (int q = 0; q < 2; q++) {
            if (isTaminCheckDigit) {
                String payCodeCD = payCodes[q];
                if (!"".equals(payCodeCD.trim())) {
                    if (payCodeCD.length() != 17) {
                        isTaminCheckDigit = false;
                        return false;
                    }
                    String cqDg = payCodeCD.substring(payCodeCD.length() - 2, payCodeCD.length());
                    String payCode = payCodeCD.substring(0, payCodeCD.length() - 2);
                    if (!StringUtils.isNumeric(payCode)) {
                        isTaminCheckDigit = false;
                        return false;
                    }
                    //Calculate first digit of check digit
                    int remain = calcTaminFtCkDg(payCode);
                    if (remain == Integer.parseInt(cqDg.substring(0, 1))) {
                        //Calculate second digit of check digit
                        String st = payCode + cqDg.substring(0, 1) + transAmnt;
                        int sRemain = calcTaminSdCkDg(st);
                        if (sRemain == Integer.parseInt(cqDg.substring(1, 2)))
                            isTaminCheckDigit = true;
                        else
                            isTaminCheckDigit = false;
                    } else
                        isTaminCheckDigit = false;

                }
            }
        }
        return isTaminCheckDigit;
    }

    public static int calcTaminFtCkDg(String payCode) {
        int remain1;
        int dSum = 0;
        int j = 2;
        for (int i = 0; i < 15; i++) {
            int digit = Integer.parseInt(payCode.substring(i, i + 1));
            dSum = dSum + (digit * j);
            j++;
            if (i == 5 || i == 11)
                j = 2;
        }

        remain1 = ((dSum % 11) == 10) ? 0 : dSum % 11;
        return (remain1);
    }

    public static int calcTaminSdCkDg(String st) {
        int remain2;
        int dSum = 0;
        int k = 3;
        for (int p = 1; p <= st.length(); p++) {
            int digit = Integer.parseInt(st.substring(p - 1, p));
            dSum = dSum + (digit * k);
            k++;
            if (p < 15 && p % 3 == 0)
                k = 3;
            else if ((p > 16 && k > 7) || (p > 22 && p < 26 && k > 4))
                k = 2;
            else if (p == 26)
                k = 3;
        }

        remain2 = ((dSum % 11) == 10) ? 0 : dSum % 11;
        return (remain2);
    }
//    End of Tamin algorithm

    //    Emdad commitee algorithm

    public static boolean emdadCommiteeAlg1(String[] payCodes, String transAmnt, String accNo) throws ModelException, ISOException {
        boolean isImdadCheckDigit = true;
        transAmnt = ISOUtil.zeropad(transAmnt, 15);
        accNo = ISOUtil.zeropad(accNo, 10);
        //This method only have one paycode parameter
        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1]))
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);

        String payCode = payCodes[0];
        if ("".equals(payCode) && !"".equals(payCodes[1]))
            payCode = payCodes[1];

        if (payCode.length() != 17) {
            isImdadCheckDigit = false;
            return false;
        }

        int chkDg = Integer.parseInt(payCode.substring(payCode.length() - 2, payCode.length()));
        if (!StringUtils.isNumeric(payCode)) {
            isImdadCheckDigit = false;
            return false;
        }
        int payCodeDSum = calcEmdadPayCodeDSum(payCode);
        int accNoDSum = calcEmdadAccNoDSum(accNo);
        int amntDSum = calcEmdadAmntNoDSum(transAmnt);

        int totalDSum = (payCodeDSum + accNoDSum + amntDSum) % 99;

        if (totalDSum != chkDg)
            isImdadCheckDigit = false;

        return isImdadCheckDigit;
    }

    public static int calcEmdadPayCodeDSum(String payCode) {
        int dSum = 0;
        int[] primeNums = makePrimeNo();
        for (int i = 15; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            int num = primeNums[i - 1];
            dSum = dSum + (digit * num);
        }
        return dSum;
    }

    public static int calcEmdadAccNoDSum(String accNo) {
        int dSum = 0;
        int[] primeNums = makePrimeNo();
        for (int i = 10; i > 0; i--) {
            int digit = Integer.parseInt(accNo.substring(i - 1, i));
            int num = primeNums[i];
            dSum = dSum + (digit * num);
        }
        return dSum;
    }

    public static int calcEmdadAmntNoDSum(String amnt) {
        int dSum = 0;
        int[] primeNums = makePrimeNo();
        for (int i = 15; i > 0; i--) {
            int digit = Integer.parseInt(amnt.substring(i - 1, i));
            int num = primeNums[i + 1];
            dSum = dSum + (digit * num);
        }
        return dSum;
    }

    public static int[] makePrimeNo() {
        int x, y;
        int[] primeNo = new int[19];
        int primeNoI = 0;
        for (x = 3; x <= 71; x++) {
            if (x % 2 != 0 || x == 2) {
                for (y = 2; y <= x / 2; y++) {
                    if (x % y == 0) {
                        break;
                    }
                }
                if (y > x / 2) {
//                    System.out.println("x is:" + x);
                    primeNo[primeNoI] = x;
                    primeNoI++;
                }
            }
        }
        return primeNo;
    }
//    End of Emdad commitee algorithm

    //    Ekram Eetam algorithm

    public static boolean ekramEetamAlg(String[] payCodes) throws ModelException {
        boolean isEkramCD = true;
        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1]))
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);

        String payCode = payCodes[0];
        if ("".equals(payCode) && !"".equals(payCodes[1]))
            payCode = payCodes[1];

        if (payCode.length() != 11) {
            isEkramCD = false;
            return false;
        }
        int chkDg = Integer.parseInt(payCode.substring(payCode.length() - 2, payCode.length()));
        if (!StringUtils.isNumeric(payCode)) {
            isEkramCD = false;
            return false;
        }
        int ekramEetamDSum = calcEkramEetamDSum(payCode);
        if (ekramEetamDSum != chkDg)
            isEkramCD = false;
        return isEkramCD;
    }

    public static int calcEkramEetamDSum(String payCode) {
        int dSum = 0;
        int[] mazarebs = new int[]{9, 1, 2, 3, 4, 5, 6, 7, 8};
        for (int i = 9; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            dSum = dSum + (digit * mazarebs[i - 1]);
        }
        return (dSum % 99);
    }
//    End of Ekram Eetam algorithm

    //    Irancell Algorithm

    public static boolean irancellAlgrthm(String[] payIds) throws ModelException, ISOException {
        boolean isIrancell = true;
        if (!"".equals(payIds[0]) && !"".equals(payIds[1]) && !payIds[0].equals(payIds[1]))
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);

        String payId = payIds[0];
        if ("".equals(payId) && !"".equals(payIds[1])) {
            payId = payIds[1];
        }
        payId = ISOUtil.zeropad(payId, 13);

        if (!StringUtils.isNumeric(payId)) {
            isIrancell = false;
            return false;
        }

        if (payId.length() != 13) {
            isIrancell = false;
            return false;
        }

        int chkDg = Integer.parseInt(payId.substring(payId.length() - 2, payId.length()));
        if (calcIrancellDSum(payId) != chkDg) {
            isIrancell = false;
        }

        return isIrancell;
    }

    public static int calcIrancellDSum(String payId) {
        int dSum = 0;
        int checkDigit;
        int[] mazarebs = new int[]{11, 10, 9, 1, 2, 3, 4, 5, 6, 7, 8};
        for (int i = 11; i > 0; i--) {
            int digit = Integer.parseInt(payId.substring(i - 1, i));
            dSum = dSum + (digit * mazarebs[i - 1]);
        }
        checkDigit = (dSum % 99);
        return checkDigit;
    }
//    End of Irancell Algorithm

    //    Dana Insurance algorithm

    public static boolean isDanaInsurance(String[] payCds) throws ModelException, ISOException {
        boolean isDanaInsuranceChkDgt = true;
        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() > 17) {
            isDanaInsuranceChkDgt = false;
            return false;
        }

        if (payCode.length() < 17)
            payCode = ISOUtil.zeropad(payCode, 17);

        if (!StringUtils.isNumeric(payCode)) {
            isDanaInsuranceChkDgt = false;
            return false;
        }

        int chkDgt = Integer.parseInt(payCode.substring(payCode.length() - 2, payCode.length()));

        if (calcDanaInsuranceDSum(payCode) != chkDgt) {
            isDanaInsuranceChkDgt = false;
        }

        return isDanaInsuranceChkDgt;

    }

    private static int calcDanaInsuranceDSum(String payCode) {
        int dSum = 0;
        int checkDigit;
        int[] mazarebs = new int[]{15, 14, 13, 12, 11, 10, 9, 1, 2, 3, 4, 5, 6, 7, 8};
        for (int i = 15; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            dSum += (digit * mazarebs[i - 1]);
        }
        checkDigit = (dSum % 99);
        return checkDigit;
    }
//   End of Dana Insurance algorithm

    //   StudntRefahFund Algorithm

    private static boolean isStudntRefahFund(String[] payCds) throws ModelException, ISOException {
        boolean isStdntRefahChkDgt = true;
        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() < 15)
            payCode = ISOUtil.zeropad(payCode, 15);

        if (payCode.length() != 15) {
            isStdntRefahChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isStdntRefahChkDgt = false;
            return false;
        }

        String chkDgt = ISOUtil.zeropad(payCode.substring(payCode.length() - 2, payCode.length()), 2);

        if (!calcStudntRefahFundDSum(payCode).equals(chkDgt)) {
            isStdntRefahChkDgt = false;
        }

        return isStdntRefahChkDgt;
    }

    private static String calcStudntRefahFundDSum(String payCode) throws ISOException {
        int dSum = 0;
        String checkDigit;
        int[] mazarebs = new int[]{7, 5, 3, 2, 21, 19, 17, 13, 11, 7, 5, 3, 2};
        for (int i = 13; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            dSum = dSum + (digit * mazarebs[i - 1]);
        }
        dSum += 26;
        checkDigit = ISOUtil.zeropad(String.valueOf(dSum % 19), 2);
        return checkDigit;
    }
//   End of StudntRefahFund Algorithm

    //   Iran Insurance algoruthm

    private static boolean isIranInsurance(String[] payCds, String transAmnt) throws ModelException, ISOException {
        boolean isIranInsuranceChkDgt = true;

        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() != 16) {
            isIranInsuranceChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isIranInsuranceChkDgt = false;
            return false;
        }

        String amuntChkDgt = ISOUtil.zeropad(payCode.substring(payCode.length() - 4, payCode.length() - 2), 2);
        if (!calcAmntChckDgt(transAmnt).equals(amuntChkDgt)) {
            isIranInsuranceChkDgt = false;
        }

        String chkDgt = ISOUtil.zeropad(payCode.substring(payCode.length() - 2, payCode.length()), 2);

        if (!calcIranInsuranceChkDgt(payCode).equals(chkDgt)) {
            isIranInsuranceChkDgt = false;
        }
        return isIranInsuranceChkDgt;
    }

    private static String calcAmntChckDgt(String transAmnt) throws ISOException {
        int dSum = 0;
        String checkAmnt;
        transAmnt = ISOUtil.zeropad(transAmnt, 7);
        String insuranceStrCd = "99";
        int[] amntMazareb = new int[]{19, 17, 13, 11, 7, 5, 3};
        for (int i = 7; i > 0; i--) {
            int digit = Integer.parseInt(transAmnt.substring(i - 1, i));
            dSum = dSum + (digit * amntMazareb[i - 1]);
        }
        checkAmnt = String.valueOf(dSum % Integer.parseInt(insuranceStrCd));
        return checkAmnt;
    }

    private static String calcIranInsuranceChkDgt(String payCode) throws ISOException {
        int dSum = 0;
        String checkDigit;
        int[] mazarebs = new int[]{47, 43, 41, 37, 31, 29, 23, 19, 17, 13, 11, 7, 5, 3};
        for (int i = 14; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            dSum += (digit * mazarebs[i - 1]);
        }
        checkDigit = ISOUtil.zeropad(String.valueOf(dSum % 99), 2);
        return checkDigit;
    }
//   End of Iran Insurance algorithm

    //   ParsianInsurance Algorithm

    public static boolean isParsianInsurance(String[] payCds) throws ModelException, ISOException {
        boolean isParsianInsuranceChkDgt = true;
        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() > 17) {
            isParsianInsuranceChkDgt = false;
            return false;
        }

        if (payCode.length() < 17)
            payCode = ISOUtil.zeropad(payCode, 17);

        if (!StringUtils.isNumeric(payCode)) {
            isParsianInsuranceChkDgt = false;
            return false;
        }

        int chkDgt = Integer.parseInt(payCode.substring(payCode.length() - 2, payCode.length()));

        if (Integer.parseInt(calcParsianInsuranceDSum(payCode)) != chkDgt) {
            isParsianInsuranceChkDgt = false;
        }

        return isParsianInsuranceChkDgt;
    }

    private static String calcParsianInsuranceDSum(String payCode) throws ISOException {
        int dSum = 0;
        String checkDigit;
        int[] mazarebs = new int[]{15, 14, 13, 12, 11, 10, 9, 1, 2, 3, 4, 5, 6, 7, 8};
        for (int i = 15; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            dSum += (digit * mazarebs[i - 1]);
        }
        checkDigit = ISOUtil.zeropad(String.valueOf(dSum % 99), 2);
        return checkDigit;
    }
//    End of ParsianInsurance Algorithm

    //    isQomSeminary Algorithm

    private static boolean isQomSeminary(String[] payCds) throws ModelException, ISOException {
        boolean isQomSeminaryChkDgt = true;
        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() > 19) {
            isQomSeminaryChkDgt = false;
            return false;
        }

        if (payCode.length() < 19)
            payCode = ISOUtil.zeropad(payCode, 19);

        if (!StringUtils.isNumeric(payCode)) {
            isQomSeminaryChkDgt = false;
            return false;
        }

        int chkDgt = Integer.parseInt(payCode.substring(payCode.length() - 2, payCode.length()));

        if (Integer.parseInt(calcQomSeminaryDSum(payCode)) != chkDgt) {
            isQomSeminaryChkDgt = false;
        }

        return isQomSeminaryChkDgt;
    }

    private static String calcQomSeminaryDSum(String payCode) throws ISOException {
        int dSum = 0;
        String checkDigit;
        int[] mazarebs = new int[]{17, 16, 15, 14, 13, 12, 11, 10, 9, 1, 2, 3, 4, 5, 6, 7, 8};
        for (int i = 17; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            dSum += (digit * mazarebs[i - 1]);
        }
        checkDigit = ISOUtil.zeropad(String.valueOf(dSum % 99), 2);
        return checkDigit;
    }
//    End of isQomSeminary Algorithm

    //    isMAMKerman Algorithm

    private static boolean isMAMKerman(String[] payCodes) throws ModelException, ISOException {
        boolean isMAMKermanChkDgt = true;

        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCodes[0];
        if ("".equals(payCode) && !"".equals(payCodes[1])) {
            payCode = payCodes[1];
        }

        if (payCode.length() > 12) {
            isMAMKermanChkDgt = false;
            return false;
        }

        if (payCode.length() < 12)
            payCode = ISOUtil.zeropad(payCode, 12);

        if (!StringUtils.isNumeric(payCode)) {
            isMAMKermanChkDgt = false;
            return false;
        }

        int chkDgt = Integer.parseInt(payCode.substring(payCode.length() - 1, payCode.length()));

        if (Integer.parseInt(calcMAMKermanDSum(payCode)) != chkDgt) {
            isMAMKermanChkDgt = false;
        }


        return isMAMKermanChkDgt;
    }

    private static String calcMAMKermanDSum(String payCode) throws ISOException {
        int dSum = 0;
        int mazrab;
        String checkDigit = "0";
        int[] mazarebs = new int[]{12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
        for (int i = 11; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            mazrab = mazarebs[i - 1];
            dSum += (digit * mazrab);
        }
        checkDigit = (dSum % 11) > 9 ? String.valueOf(0) : String.valueOf(dSum % 11);
        return checkDigit;
    }
//    End of isMAMKerman Algorithm

    //    Emdad Commitee Algorithm 2

    public static boolean isEmdadCommiteeAlg2(String[] payCds, String accNo) throws ISOException, ModelException {
        boolean isEmdadCheckDigit = true;
        accNo = ISOUtil.zeropad(accNo, 13);
        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1]))
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1]))
            payCode = payCds[1];

        if (payCode.length() > 21) {
            isEmdadCheckDigit = false;
            return false;
        }

        if (payCode.length() < 21)
            payCode = ISOUtil.zeropad(payCode, 21);

        if (!StringUtils.isNumeric(payCode)) {
            isEmdadCheckDigit = false;
            return false;
        }

        int checkDigit = Integer.parseInt(payCode.substring(payCode.length() - 2, payCode.length()));
        int payCodeDSum = calcEmdadPayCodeDSum2(payCode);
        int accNoDSum = calcEmdadAccNoDSum2(accNo);

        int totalDSum = (payCodeDSum + accNoDSum) % 99;


        if (totalDSum != checkDigit)
            isEmdadCheckDigit = false;
        return isEmdadCheckDigit;

    }

    public static int calcEmdadPayCodeDSum2(String payCode) {
        int dSum = 0;
        int[] primeNums = makePrimeNo();
        for (int i = 19; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            int num = primeNums[i - 1];
            dSum = dSum + (digit * num);

        }
        return dSum;
    }

    public static int calcEmdadAccNoDSum2(String accNo) {
        int dSum = 0;
        int[] primeNums = makePrimeNo();
        for (int i = 13; i > 0; i--) {
            int digit = Integer.parseInt(accNo.substring(i - 1, i));
            int num = primeNums[i];
            dSum = dSum + (digit * num);

        }
        return dSum;
    }
//    End of Emdad Commitee Algorithm 2

    //    Zamyad Co. Algorithm

    public static boolean isZamyadCoAlgorithm(String[] payCodes, String accNo, String Amount) throws ISOException, ModelException {
        boolean isZamyadCoChkDgt = true;

        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCodes[0];
        if ("".equals(payCode) && !"".equals(payCodes[1])) {
            payCode = payCodes[1];
        }

        if (payCode.length() != 17) {
            isZamyadCoChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isZamyadCoChkDgt = false;
            return false;
        }

        int currentDate = 0;
        String expDate = payCode.substring(11, 14);
        int currentMonth = (Integer.valueOf(DateUtil.getSystemDate().substring(4, 6)));
        if (currentMonth >= 1 && currentMonth <= 6) {
            currentDate = (currentMonth - 1) * 31 + (Integer.valueOf(DateUtil.getSystemDate().substring(6)));
        } else if (currentMonth >= 7 && currentMonth <= 12) {
            currentDate = 186 + (currentMonth - 1 - 6) * 30 + (Integer.valueOf(DateUtil.getSystemDate().substring(6)));
        }


        if (currentDate > Integer.parseInt(expDate)) {
            isZamyadCoChkDgt = false;
            return false;
        }

        int checkDigit = Integer.parseInt(payCode.substring(payCode.length() - 2, payCode.length()));

        int payCdDSum = calcZamyadPayCodeChkDigit(payCode);
        accNo = ISOUtil.zeropad(ISOUtil.zeroUnPad(accNo), 10);
        int accNoDSum = calcZamyadAccNoChkDigit(accNo);
        int amountDSum = calcZamyadAmountChkDigit(Amount);

        String totalDSum = ISOUtil.zeropad(String.valueOf((payCdDSum + accNoDSum + amountDSum) % 99), 2);

        if (Integer.parseInt(totalDSum) != checkDigit) {
            isZamyadCoChkDgt = false;
            return false;
        }

        return isZamyadCoChkDgt;

    }

    private static int calcZamyadPayCodeChkDigit(String payCode) {
        int dSum = 0;
        int[] primeNums = new int[]{3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53};
        for (int i = 15; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            int num = primeNums[i - 1];
            dSum = dSum + (digit * num);
        }
        return dSum;
    }

    private static int calcZamyadAccNoChkDigit(String accNo) {
        int dSum = 0;
        int[] primeNums = new int[]{5, 7, 11, 13, 17, 19, 23, 29, 31, 37};
        for (int i = 10; i > 0; i--) {
            int digit = Integer.parseInt(accNo.substring(i - 1, i));
            int num = primeNums[i - 1];
            dSum = dSum + (digit * num);
        }
        return dSum;
    }

    private static int calcZamyadAmountChkDigit(String amount) throws ISOException {
        int dSum = 0;
        int[] primeNums = new int[]{7, 11, 13, 7, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61};
        for (int i = 15; i > 0; i--) {
            amount = ISOUtil.zeropad(amount, 15);
            int digit = Integer.parseInt(amount.substring(i - 1, i));
            int num = primeNums[i - 1];
            dSum = dSum + (digit * num);
        }
        return dSum;
    }
//    End of Zamyad Co. Algorithm

    //    Mashhad Uni Algorithm

    public static boolean isMashhadUniAlgorithm(String[] payCodes) throws ModelException {
        boolean isMashhadUniChkDgt = true;

        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCodes[0];
        if ("".equals(payCode) && !"".equals(payCodes[1])) {
            payCode = payCodes[1];
        }

        if (payCode.length() != 9) {
            isMashhadUniChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isMashhadUniChkDgt = false;
            return false;
        }

        String checkDigit = payCode.substring(payCode.length() - 1);

        if (!checkDigit.equals(String.valueOf(calcMashhadUniChkDgt(payCode)))) {
            isMashhadUniChkDgt = false;
            return false;
        }

        return isMashhadUniChkDgt;

    }

    private static int calcMashhadUniChkDgt(String payCode) {
        int dSum = 0;

        int[] mazarebs = new int[]{23, 19, 17, 13, 11, 7, 5, 3};
        for (int i = 0; i < 8; i++) {
            int mazrab = mazarebs[i];
            int digit = Integer.parseInt(payCode.substring(i, i + 1));
            dSum += (digit * mazrab);
        }
        return (((dSum % 11) == 10) ? 0 : dSum % 11);
    }
//    End of Mashhad Uni Algorithm

    //    Type 1 Algorithm ----------------------- Barghe shiraz Co.

    public static boolean isType1Algorithm(String[] payCodes, String accNo, String Amount) throws ISOException, ModelException {
        boolean isType1ChkDgt = true;

        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCodes[0];
        if ("".equals(payCode) && !"".equals(payCodes[1])) {
            payCode = payCodes[1];
        }

//        if (payCode.length() != 17) {
        if (payCode.length() > 17) {
            isType1ChkDgt = false;
            return false;
        }
        if (payCode.length() < 17) {
            payCode = ISOUtil.zeropad(payCode, 17);
        }

        if (!StringUtils.isNumeric(payCode)) {
            isType1ChkDgt = false;
            return false;
        }

        int checkDigit = Integer.parseInt(payCode.substring(payCode.length() - 2, payCode.length()));

        int payCdDSum = calcType1PayCodeChkDigit(payCode);
        accNo = ISOUtil.zeropad(ISOUtil.zeroUnPad(accNo), 10);
        int accNoDSum = calcType1AccNoChkDigit(accNo);
        int amountDSum = calcType1AmountChkDigit(Amount);

        String totalDSum = ISOUtil.zeropad(String.valueOf((payCdDSum + accNoDSum + amountDSum) % 99), 2);

        if (Integer.parseInt(totalDSum) != checkDigit) {
            isType1ChkDgt = false;
            return false;
        }

        return isType1ChkDgt;

    }

    private static int calcType1PayCodeChkDigit(String payCode) {
        int dSum = 0;
        int[] primeNums = new int[]{3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53};
        for (int i = 15; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            int num = primeNums[i - 1];
            dSum = dSum + (digit * num);
        }
        return dSum;
    }

    private static int calcType1AccNoChkDigit(String accNo) {
        int dSum = 0;
        int[] primeNums = new int[]{5, 7, 11, 13, 17, 19, 23, 29, 31, 37};
        for (int i = 10; i > 0; i--) {
            int digit = Integer.parseInt(accNo.substring(i - 1, i));
            int num = primeNums[i - 1];
            dSum = dSum + (digit * num);
        }
        return dSum;
    }

    private static int calcType1AmountChkDigit(String amount) throws ISOException {
        int dSum = 0;
        int[] primeNums = new int[]{7, 11, 13, 7, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61};
        for (int i = 15; i > 0; i--) {
            amount = ISOUtil.zeropad(amount, 15);
            int digit = Integer.parseInt(amount.substring(i - 1, i));
            int num = primeNums[i - 1];
            dSum = dSum + (digit * num);
        }
        return dSum;
    }
//    End of Type 1 Algorithm

    //    Type 2 Algorithm ----------------------- Maharat University

    public static boolean isType2Algorithm(String[] payCodes) throws ModelException, ISOException {
        boolean isType2AlgChkDgt = true;
        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCodes[0];
        if ("".equals(payCode) && !"".equals(payCodes[1])) {
            payCode = payCodes[1];
        }

        if (payCode.length() > 10) {
            isType2AlgChkDgt = false;
            return false;
        }

        if (payCode.length() < 10)
            payCode = ISOUtil.zeropad(payCode, 10);

        if (!StringUtils.isNumeric(payCode)) {
            isType2AlgChkDgt = false;
            return false;
        }

        String checkDigit = payCode.substring(payCode.length() - 2);

        String totalChkDgt = String.valueOf(calcType2AlgChkDg(payCode) % 99);

        if (!checkDigit.equals(ISOUtil.zeropad(totalChkDgt, 2))) {
            isType2AlgChkDgt = false;
            return false;
        }

        return isType2AlgChkDgt;

    }

    private static int calcType2AlgChkDg(String payCode) {
        int dSum = 0;
        for (int i = 1; i <= 8; i++) {
            int digit = Integer.valueOf(payCode.substring(i - 1, i));
            dSum += (i * digit);
        }
        return dSum;
    }
//    End of Type 2 Algorithm

    //    Type 3 Algorithm ----------------------- BaHonar University , Nezam Mohandesi Fars , Mashhad Ferdowsi University

    public static boolean isType3Algorithm(String[] payCds) throws ModelException, ISOException {
        boolean isType3ChkDgt = true;

        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() > 13) {
            isType3ChkDgt = false;
            return false;
        }

        if (payCode.length() < 13)
            payCode = ISOUtil.zeropad(payCode, 13);

        if (!StringUtils.isNumeric(payCode)) {
            isType3ChkDgt = false;
            return false;
        }

        String checkDigit = payCode.substring(payCode.length() - 2);

        String totalChckDgt = String.valueOf(calcType3ChkDgt(payCode) % 99);

        if (!checkDigit.equals(ISOUtil.zeropad(totalChckDgt, 2))) {
            isType3ChkDgt = false;
            return false;
        }

        return isType3ChkDgt;
    }

    private static int calcType3ChkDgt(String payCode) {
        int dSum = 0;
        int[] mazarebs = new int[]{11, 10, 9, 1, 2, 3, 4, 5, 6, 7, 8};
        for (int i = 0; i < 11; i++) {
            int mazrab = mazarebs[i];
            int digit = Integer.parseInt(payCode.substring(i, i + 1));
            dSum += (mazrab * digit);
        }

        return dSum;
    }
//    End of Type 3 Algorithm

    //    Type 4 Algorithm

    public static boolean isType4Algorithm(String[] payCds) throws ModelException, ISOException {
        boolean isType4AlgChkDgt = true;

        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() > 11) {
            isType4AlgChkDgt = false;
            return false;
        }

        if (payCode.length() < 11)
            payCode = ISOUtil.zeropad(payCode, 11);

        if (!StringUtils.isNumeric(payCode)) {
            isType4AlgChkDgt = false;
            return false;
        }

        String checkDigit = payCode.substring(payCode.length() - 2);

        String totalChkDgt = String.valueOf(calcType4CheckDgt(payCode) % 99);

        if (!checkDigit.equals(ISOUtil.zeropad(totalChkDgt, 2))) {
            isType4AlgChkDgt = false;
            return false;
        }

        return isType4AlgChkDgt;
    }

    private static int calcType4CheckDgt(String payCode) {
        int dSum = 0;
        int[] mazarebs = new int[]{9, 1, 2, 3, 4, 5, 6, 7, 8};

        for (int i = 0; i < 9; i++) {
            int digit = Integer.parseInt(payCode.substring(i, i + 1));
            int mazrab = mazarebs[i];
            dSum += (digit * mazrab);
        }

        return dSum;
    }
//    End of Type 4 Algorithm

    //    Type 5 Algorithm

    public static boolean isType5Algorithm(String[] payCodes) throws ModelException {
        boolean isType5ChkDgt = true;

        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCodes[0];
        if ("".equals(payCode) && !"".equals(payCodes[1])) {
            payCode = payCodes[1];
        }

        if (payCode.length() != 8) {
            isType5ChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isType5ChkDgt = false;
            return false;
        }

        String checkDigit = payCode.substring(payCode.length() - 1);

        if (!checkDigit.equals(String.valueOf(calcType5ChkDgt(payCode)))) {
            isType5ChkDgt = false;
            return false;
        }

        return isType5ChkDgt;
    }

    private static int calcType5ChkDgt(String payCode) {
        int dSum = 0;
        int checkDigit = 0;
        for (int i = 1; i <= 7; i++) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            dSum += (i * digit);
        }
        int n1 = dSum % 11;
        if (n1 < 9)
            checkDigit = n1;
        else if (n1 == 10)
            checkDigit = 0;
        else
            checkDigit = n1;
        return checkDigit;
    }
//    End of Type 5 Algorithm

    //    Type 6 Algorithm ----------------------- Bonyad Maskan

    public static boolean isType6Algorithm(String[] payCodes) throws ModelException, ISOException {

        boolean isType6ChkDgt = true;

        if ((!payCodes[0].equals("") && !payCodes[1].equals("")) && !payCodes[0].equals(payCodes[1]))
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);

        String payCode = payCodes[0];
        if (payCode.equals("") && !payCodes[1].equals("")) {
            payCode = payCodes[1];
        }

        if (payCode.length() != 15) {
            isType6ChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isType6ChkDgt = false;
            return false;
        }

        String checkDigit = payCode.substring(payCode.length() - 2, payCode.length());

        String totalDSum = String.valueOf(calcType6ChkDgt(payCode));
        totalDSum = totalDSum.substring(totalDSum.length() - 2, totalDSum.length());

        if (!checkDigit.equals(totalDSum)) {
            isType6ChkDgt = false;
            return false;
        }

        return isType6ChkDgt;
    }

    private static int calcType6ChkDgt(String payCode) {
        int dSum = 0;
        String motamam = makeMotamam(payCode);
        for (int i = 1; i <= 13; i++) {
            int digit = Integer.parseInt(motamam.substring(i - 1, i));
            dSum += (digit * i);
        }

        return dSum;
    }
//    End of Type 6 Algorithm

    //    is Type7 Algorithm ----------------------- BooAliSina University

    public static boolean isType7Algorithm(String[] payCodes) throws ISOException, ModelException {
        boolean isType7ChkDgt = true;

        if ((!"".equals(payCodes[0]) && !"".equals(payCodes[1])) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCodes[0];
        if ("".equals(payCodes[0]) & !"".equals(payCodes[1])) {
            payCode = payCodes[1];
        }

        if (payCode.length() > 19) {
            isType7ChkDgt = false;
            return false;
        }

        if (payCode.length() < 19)
            payCode = ISOUtil.zeropad(payCode, 19);

        if (!StringUtils.isNumeric(payCode)) {
            isType7ChkDgt = false;
            return false;
        }

        String chkDgt = payCode.substring(payCode.length() - 2, payCode.length());
        String totalChkDgt = calcType7ChkDgt(payCode);

        if (!chkDgt.equals(ISOUtil.zeropad(totalChkDgt, 2))) {
            isType7ChkDgt = false;
            return false;
        }
        return isType7ChkDgt;
    }

    private static String calcType7ChkDgt(String payCode) {
        int dSum = 0;
        int mazrab;
        String checkDigit = "0";
        int[] mazarebs = new int[]{17, 16, 15, 14, 13, 12, 11, 10, 9, 1, 2, 3, 4, 5, 6, 7, 8};
        for (int i = 0; i < 17; i++) {
            int digit = Integer.parseInt(payCode.substring(i, i + 1));
            mazrab = mazarebs[i];
            dSum += (digit * mazrab);
        }
        checkDigit = String.valueOf(dSum % 99);
        return checkDigit;
    }
//    End of is Type7 Algorithm

    //    Type 8 Algorithm

    public static boolean isType8Algorithm(String[] payCodes) throws ModelException, ISOException {
        boolean isType8ChkDgt = true;

        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCodes[0];
        if ("".equals(payCode) && !"".equals(payCodes[1])) {
            payCode = payCodes[1];
        }

        if (payCode.length() > 21) {
            isType8ChkDgt = false;
            return false;
        }

        if (payCode.length() < 21)
            payCode = ISOUtil.zeropad(payCode, 21);

        if (!StringUtils.isNumeric(payCode)) {
            isType8ChkDgt = false;
            return false;
        }


        String checkDigit = payCode.substring(payCode.length() - 2);
        String totalChkDgt = String.valueOf(calcAlgorithmType8ChkDgt(payCode));
        if (!checkDigit.equals(ISOUtil.zeropad(totalChkDgt, 2))) {
            isType8ChkDgt = false;
            return false;
        }

        return isType8ChkDgt;

    }

    private static int calcAlgorithmType8ChkDgt(String payCode) {
        int dSum = 0;
        int[] mazarebs = new int[]{19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 1, 2, 3, 4, 5, 6, 7, 8};
        for (int i = 19; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            int mazrab = mazarebs[i - 1];
            dSum += (digit * mazrab);
        }
        int checkDigit = dSum % 99;

        return checkDigit;
    }
//    End of Type 8 Algoritm

    //    Iran Leasing Algoritm

    public static boolean isIranLeasingAlgorithm(String[] payCodes) throws ModelException {
        boolean isIranLeasingChkDgt = true;

        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCodes[0];
        if ("".equals(payCode) && !"".equals(payCodes[1])) {
            payCode = payCodes[1];
        }

        if (payCode.length() != 9) {
            isIranLeasingChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isIranLeasingChkDgt = false;
            return false;
        }

        String chkDgt = payCode.substring(payCode.length() - 1);
        if (!chkDgt.equals(String.valueOf(calIranLeasingChkDgt(payCode)))) {
            isIranLeasingChkDgt = false;
            return false;
        }

        return isIranLeasingChkDgt;
    }

    private static int calIranLeasingChkDgt(String payCode) {
        int dSum = 0;

        int[] mazarebs = new int[]{23, 19, 17, 13, 11, 7, 5, 3};
        for (int i = 0; i < 8; i++) {
            int mazrab = mazarebs[i];
            int digit = Integer.parseInt(payCode.substring(i, i + 1));
            dSum += (digit * mazrab);
        }
        return (((dSum % 10) == 10) ? 0 : dSum % 10);
    }
//    Iran Leasing Algoritm

    //    National Code Algorithm

    public static boolean isNationalCodeAlgorithm(String[] payCodes) throws ModelException {
        boolean isNationalCodeChkDgt = true;

        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCodes[0];
        if ("".equals(payCode) && !"".equals(payCodes[1])) {
            payCode = payCodes[1];
        }

        if (payCode.length() != 10) {
            isNationalCodeChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isNationalCodeChkDgt = false;
            return false;
        }

        String chkDgt = payCode.substring(payCode.length() - 1);
        if (!chkDgt.equals(String.valueOf(calNationalCodeChkDgt(payCode)))) {
            isNationalCodeChkDgt = false;
            return false;
        }

        return isNationalCodeChkDgt;
    }

    private static int calNationalCodeChkDgt(String payCode) {
        int dSum = 0;

        int[] mazarebs = new int[]{10, 9, 8, 7, 6, 5, 4, 3, 2};
        for (int i = 0; i < 9; i++) {
            int mazrab = mazarebs[i];
            int digit = Integer.parseInt(payCode.substring(i, i + 1));
            dSum += (digit * mazrab);
        }
        int remain = (dSum % 11);
        return ((remain == 0 || remain == 1) ? remain : (11 - remain));
    }
//    End of National Code Algorithm

    //    Enex Algorithm

    public static boolean isEnexAlgorithm(String payCode) throws ModelException, ISOException {
        boolean isEnexChkDgt = true;

        if (payCode.length() > 17) {
            isEnexChkDgt = false;
            return false;
        }

        if (payCode.length() < 17)
            payCode = ISOUtil.zeropad(payCode, 17);

        if (!StringUtils.isNumeric(payCode)) {
            isEnexChkDgt = false;
            return false;
        }


        String checkDigit = payCode.substring(payCode.length() - 2);
        String totalChkDgt = String.valueOf(calcAlgorithmEnexChkDgt(payCode));
        if (!checkDigit.equals(ISOUtil.zeropad(totalChkDgt, 2))) {
            isEnexChkDgt = false;
            return false;
        }

        return isEnexChkDgt;

    }

    private static int calcAlgorithmEnexChkDgt(String payCode) {
        int dSum = 0;
        int[] mazarebs = new int[]{15, 14, 13, 12, 11, 10, 9, 1, 2, 3, 4, 5, 6, 7, 8};
        for (int i = 15; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            int mazrab = mazarebs[i - 1];
            dSum += (digit * mazrab);
        }
        int checkDigit = dSum % 99;

        return checkDigit;
    }
//    End of Enex Algoritm

    //    Life And Saving Algorithm

    public static boolean isLifeAndSavingAlgorithm(String[] payCodes) throws ModelException, ISOException {
        boolean isLifeAndSavingChkDgt = true;

        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCodes[0];
        if ("".equals(payCode) && !"".equals(payCodes[1])) {
            payCode = payCodes[1];
        }

        if (payCode.length() != 13) {
            isLifeAndSavingChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isLifeAndSavingChkDgt = false;
            return false;
        }

        String chkDgt = payCode.substring(payCode.length() - 2);
        if (!chkDgt.equals(calcLifeAndSavingChkDgt(payCode))) {
            isLifeAndSavingChkDgt = false;
            return false;
        }

        return isLifeAndSavingChkDgt;
    }

    private static String calcLifeAndSavingChkDgt(String payCode) {
        String dSum = "";
        int sum1 = 0;
        int sum2 = 0;

        for (int i = 1; i < 12; i++) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            sum1 += (digit * i);
            sum2 += (digit * (12 - i));
        }
        sum1 = ((sum1 % 11) > 9) ? 1 : sum1 % 11;
        sum2 = ((sum2 % 11) > 9) ? 1 : sum2 % 11;
        dSum = String.valueOf(sum1) + String.valueOf(sum2);
        return dSum;
    }
//    End of Life And Saving Algorithm

    //    Sajad Algorithm

    public static boolean isSajadAlgorithm(String[] payCodes) throws ModelException, ISOException {
        boolean isSajadChkDgt = true;
        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCodes[0];
        if ("".equals(payCode) && !"".equals(payCodes[1])) {
            payCode = payCodes[1];
        }

        if (payCode.length() != 11) {
            isSajadChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isSajadChkDgt = false;
            return false;
        }

        String chkDgt = payCode.substring(payCode.length() - 2);
        if (!chkDgt.equals(calcSajadChkDgt(payCode))) {
            isSajadChkDgt = false;
            return false;
        }

        return isSajadChkDgt;
    }

    private static String calcSajadChkDgt(String payCode) {
        int dSum = 0;
        int mazrab;
        String checkDigit = "0";
        int[] mazarebs = new int[]{19, 23, 29, 31, 37, 41, 43, 47, 53};
        for (int i = 0; i < 9; i++) {
            int digit = Integer.parseInt(payCode.substring(i, i + 1));
            mazrab = mazarebs[i];
            dSum += (digit * mazrab);
        }
        dSum += 558;
        try {
            checkDigit = ISOUtil.zeropad(String.valueOf(dSum % 99), 2);
        } catch (ISOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return checkDigit;
    }
//    End of Sajad Algorithm

    //    Mahan Algorithm

    public static boolean isMahanAlgorithm(String[] payCodes) throws ModelException, ISOException {
        boolean isMahanChkDgt = true;

        String payCode = payCodes[0];

        if (payCode.length() != 11) {
            isMahanChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isMahanChkDgt = false;
            return false;
        }

        String chkDgt = payCode.substring(payCode.length() - 1);
        if (!chkDgt.equals(calcMahanChkDgt(payCode))) {
            isMahanChkDgt = false;
            return false;
        }

        return isMahanChkDgt;
    }

    private static String calcMahanChkDgt(String payCode) {
        String checkDigit = "";
        payCode = payCode.substring(0, payCode.length() - 1);
        long intPayCode = Long.parseLong(payCode);
        long l = intPayCode / 7;
        long m = l * 7;
        long n = intPayCode - m;
        checkDigit = String.valueOf(n);
        return checkDigit;
    }
//    End of Mahan Algorithm

    //    Iran Insurance New Algorithm

    public static boolean isIranInsuranceNewAlgorithm(String[] payCodes) throws ModelException, ISOException {
        boolean isIranInsuranceNewChkDgt = true;

        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCodes[0];
        if ("".equals(payCode) && !"".equals(payCodes[1])) {
            payCode = payCodes[1];
        }

        if (payCode.length() != 13) {
            isIranInsuranceNewChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isIranInsuranceNewChkDgt = false;
            return false;
        }

        String chkDgt = payCode.substring(payCode.length() - 2);
        if (!chkDgt.equals(calcIranInsuranceNewChkDgt(payCode))) {
            isIranInsuranceNewChkDgt = false;
            return false;
        }

        return isIranInsuranceNewChkDgt;
    }

    private static String calcIranInsuranceNewChkDgt(String payCode) {
        String dSum = "";
        int sum1 = 0;
        int sum2 = 0;

        for (int i = 1; i < 12; i++) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            sum1 += (i * digit);
            sum2 += ((12 - i) * digit);
        }
        sum1 = ((sum1 % 11) == 10) ? 0 : sum1 % 11;
        sum2 = ((sum2 % 11) == 10) ? 0 : sum2 % 11;
        dSum = String.valueOf(sum1) + String.valueOf(sum2);
        return dSum;
    }
//    End of Iran Insurance New Algorithm

    //    Saipa Algorithm

    public static boolean isSaipaAlgorithm(String[] payCodes, String accNo, String amount) throws ModelException, ISOException {
        boolean isSaipaChkDgt = true;

        if ((!payCodes[0].equals("") && !payCodes[1].equals("")) && !payCodes[0].equals(payCodes[1]))
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);

        String payCode = payCodes[0];
        if (payCode.equals("") && !payCodes[1].equals("")) {
            payCode = payCodes[1];
        }

        if (payCode.length() != 17) {
            isSaipaChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isSaipaChkDgt = false;
            return false;
        }

        String checkDigit = payCode.substring(payCode.length() - 2, payCode.length());

        String totalDSum = String.valueOf(calcSaipaChkDgt(payCode, ISOUtil.zeropad(accNo, 10), ISOUtil.zeropad(amount, 15)));
//        totalDSum = totalDSum.substring(totalDSum.length() - 2, totalDSum.length());

        if (!checkDigit.equals(totalDSum)) {
            isSaipaChkDgt = false;
            return false;
        }

        return isSaipaChkDgt;
    }

    private static String calcSaipaChkDgt(String payCode, String accNo, String amount) throws ISOException {
        String dSum = "";
        int payCdSum = 0;
        int accNoSum = 0;
        int amntSum = 0;
        int[] PayCdMazarebs = new int[]{3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53};
        int[] accNoMazarebs = new int[]{5, 7, 11, 13, 17, 19, 23, 29, 31, 37};
        int[] amntMazarebs = new int[]{7, 11, 13, 17, 19, 23, 19, 31, 37, 41, 43, 47, 53, 59, 61};
        int PayCdMazrab;
        int accNoMazrab;
        int amntMazrab;

        for (int i = 0; i < 15; i++) {
            int digit = Integer.parseInt(payCode.substring(i, i + 1));
            PayCdMazrab = PayCdMazarebs[i];
            payCdSum += (digit * PayCdMazrab);
        }

        for (int j = 0; j < 10; j++) {
            int digit = Integer.parseInt(accNo.substring(j, j + 1));
            accNoMazrab = accNoMazarebs[j];
            accNoSum += (digit * accNoMazrab);
        }

        for (int k = 0; k < 15; k++) {
            int digit = Integer.parseInt(amount.substring(k, k + 1));
            amntMazrab = amntMazarebs[k];
            amntSum += (digit * amntMazrab);
        }

        dSum = String.valueOf((payCdSum + accNoSum + amntSum) % 99);
        dSum = ISOUtil.zeropad(dSum, 2);
        return dSum;
    }
//    End of Saipa Algorithm

    //    industry& mine leasing Algorithm  265216

    public static boolean isLeasingAlgorithm(String[] payCodes, String accNo, String amount) throws ModelException, ISOException {
        boolean isLeasingChkDgt = true;
        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCodes[0];
        if ("".equals(payCode) && !"".equals(payCodes[1])) {
            payCode = payCodes[1];
        }

        if (payCode.length() > 17) {
            isLeasingChkDgt = false;
            return false;
        }

        if (payCode.length() < 17)
            payCode = ISOUtil.zeropad(payCode, 17);

        if (!StringUtils.isNumeric(payCode)) {
            isLeasingChkDgt = false;
            return false;
        }

        if (Integer.parseInt(payCode.substring(14, 15)) != 2) {
            isLeasingChkDgt = false;
            return false;
        }
        int chkDgt = Integer.parseInt(payCode.substring(payCode.length() - 2));
        int payCodeDSum = calcLeasingPayCodeDSum(payCode);
        int accNoDSum = calcLeasingAccNoDSum(accNo);
        int amntDSum = calcLeasingAmntDSum(amount);

        int totalDSum = (payCodeDSum + accNoDSum + amntDSum) % 99;
        if (totalDSum != chkDgt) {
            isLeasingChkDgt = false;
            return false;
        }

        return isLeasingChkDgt;
    }

    private static int calcLeasingPayCodeDSum(String payCode) {
        int a = 0;
        int[] mazarebs = new int[]{53, 47, 43, 41, 37, 31, 29, 23, 19, 17, 13, 11, 7, 5, 3};
        int mazrab;

        for (int i = 15; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i, i - 1));
            mazrab = mazarebs[i];
            a += (digit * mazrab);
        }
        return a;
    }

    private static int calcLeasingAccNoDSum(String accNo) throws ISOException {
        accNo = ISOUtil.zeropad(accNo, 13);
        int b = 0;
        int[] mazarebs = new int[]{37, 31, 29, 23, 19, 17, 13, 11, 7, 5, 0, 0, 0};
        int mazrab;
        for (int j = 13; j > 0; j--) {
            int digit = Integer.parseInt(accNo.substring(j, j - 1));
            mazrab = mazarebs[j];
            b += (digit * mazrab);
        }
        return b;

    }

    private static int calcLeasingAmntDSum(String amount) throws ISOException {
        amount = ISOUtil.zeropad(amount, 15);
        int c = 0;
        int[] mazarebs = new int[]{61, 59, 53, 47, 43, 41, 37, 31, 29, 23, 19, 17, 13, 11, 7};
        int mazrab;
        for (int j = 15; j > 0; j--) {
            int digit = Integer.parseInt(amount.substring(j, j - 1));
            mazrab = mazarebs[j];
            c += (digit * mazrab);
        }
        return c;
    }
//    End of industry& mine leasing Algorithm

    //    Eight Algorithm

    public static boolean isEight1Algorithm(String[] payCodes) throws ModelException {
        boolean isIranLeasingChkDgt = true;

        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCodes[0];
        if ("".equals(payCode) && !"".equals(payCodes[1])) {
            payCode = payCodes[1];
        }

        if (payCode.length() != 9) {
            isIranLeasingChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isIranLeasingChkDgt = false;
            return false;
        }

        String chkDgt = payCode.substring(payCode.length() - 1);
        if (!chkDgt.equals(String.valueOf(calcEight1ChekDgt(payCode)))) {
            isIranLeasingChkDgt = false;
            return false;
        }

        return isIranLeasingChkDgt;
    }

    private static int calcEight1ChekDgt(String payCode) {
        int dSum = 0;

        int[] mazarebs = new int[]{23, 19, 17, 13, 11, 7, 5, 3};
        for (int i = 0; i < 8; i++) {
            int mazrab = mazarebs[i];
            int digit = Integer.parseInt(payCode.substring(i, i + 1));
            dSum += (digit * mazrab);
        }
        return (((dSum % 11) == 10) ? 0 : dSum % 10);
    }
//    End Eight Algorithm

    //    Paymaneh Algorithm

    public static boolean isPeymanrhAlgorithm(String[] payCodes) throws ModelException, ISOException {
        boolean isPaymanehChkDgt = true;
        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCodes[0];
        if ("".equals(payCode) && !"".equals(payCodes[1])) {
            payCode = payCodes[1];
        }

        if (payCode.length() > 17) {
            return false;
        }

        if (payCode.length() < 17)
            payCode = ISOUtil.zeropad(payCode, 17);

        if (!StringUtils.isNumeric(payCode)) {
            return false;
        }

        String chkDgt = payCode.substring(payCode.length() - 2);
        if (!chkDgt.equals(ISOUtil.zeropad(String.valueOf(calcPeymanehChkDgt(payCode)), 2))) {
            return false;
        }
        return isPaymanehChkDgt;
    }

    private static int calcPeymanehChkDgt(String payCode) {
        int dSum = 0;
        int[] mazarebs = new int[]{8, 7, 6, 5, 4, 3, 2, 1, 9, 10, 11, 12, 13, 14, 15};
        for (int i = 15; i > 0; i--) {
            int mazrab = mazarebs[15 - i];
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            dSum += (digit * mazrab);
        }
        return (dSum % 99);
    }
//    End of Paymaneh Algorithm

    //    Emam Zaman Algorithm

    public static boolean isEmamZamanAlgorithm(String[] payCodes) throws ModelException {
        boolean isEmamZamanChkDgt = true;
        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCodes[0];
        if ("".equals(payCode) && !"".equals(payCodes[1])) {
            payCode = payCodes[1];
        }

        if (payCode.length() != 8) {
            isEmamZamanChkDgt = false;
            return false;
        }


        String chkDgt = payCode.substring(payCode.length() - 2);
        if (!chkDgt.equals(String.valueOf(calcEmamZamanChkDgt(payCode)))) {
            isEmamZamanChkDgt = false;
            return false;
        }
        return isEmamZamanChkDgt;

    }

    private static int calcEmamZamanChkDgt(String payCode) {
        int dSum = 0;
        int[] mazarebs = new int[]{1, 2, 3, 4, 5, 6};
        for (int i = 5; i >= 0; i--) {
            int mazrab = mazarebs[i];
            int digit = Integer.parseInt(payCode.substring(i, i + 1));
            dSum += (digit * mazrab);
        }
        return (dSum % 99);
    }
//    End of Emam Zaman Algorithm

    //    Elmi karbordi arak

    public static boolean isElmiKarbordiArakAlgorithm(String[] payCds) throws ModelException, ISOException {
        boolean isElmiKarbordiArakChkDgt = true;

        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() != 16) {
            isElmiKarbordiArakChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isElmiKarbordiArakChkDgt = false;
            return false;
        }


        String chkDgt = ISOUtil.zeropad(payCode.substring(payCode.length() - 2, payCode.length()), 2);

        if (!calcElmiKarbordiArakChkDgt(payCode).equals(chkDgt)) {
            isElmiKarbordiArakChkDgt = false;
        }
        return isElmiKarbordiArakChkDgt;
    }

    private static String calcElmiKarbordiArakChkDgt(String payCode) {
        int dSum = 0;
        int[] mazarebs = new int[]{14, 13, 12, 11, 10, 9, 1, 2, 3, 4, 5, 6, 7, 8};
        for (int i = 0; i < 14; i++) {
            int mazrab = mazarebs[i];
            int digit = Integer.parseInt(payCode.substring(i, i + 1));
            dSum += (digit * mazrab);
        }
        return String.valueOf(dSum % 99);
    }
//    End of Elmi karbordi arak

    public static boolean isOloomTahghighatAlgorithm(String[] payCds) throws ModelException, ISOException {
        boolean isOloomTahghighatChkDgt = true;

        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() != 15) {
            isOloomTahghighatChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isOloomTahghighatChkDgt = false;
            return false;
        }


        String chkDgt = ISOUtil.zeropad(payCode.substring(payCode.length() - 2, payCode.length()), 2);

        if (!calcOloomTahghighatChkDgt(payCode).equals(chkDgt)) {
            isOloomTahghighatChkDgt = false;
        }
        return isOloomTahghighatChkDgt;
    }

    private static String calcOloomTahghighatChkDgt(String payCode) throws ISOException {
        int dSum = 0;
        int[] mazarebs = new int[]{7, 5, 3, 2, 21, 19, 17, 13, 11, 7, 5, 3, 2};
        for (int i = 0; i < 13; i++) {
            int mazrab = mazarebs[i];
            int digit = Integer.parseInt(payCode.substring(i, i + 1));
            dSum += (digit * mazrab);
        }
        dSum += 26;
        return ISOUtil.zeropad(String.valueOf(dSum % 19), 2);
    }

    // bourse algorithm
    public static boolean isbourseAlgorithm(String[] payCds) throws ModelException, ISOException {
        boolean isbourseChkDgt = true;
        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() != 15) {
            isbourseChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isbourseChkDgt = false;
            return false;
        }

        String chkDgt = payCode.substring(payCode.length() - 2, payCode.length());

        if (!calcBourseDSum(payCode).equals(chkDgt)) {
            isbourseChkDgt = false;
        }

        return isbourseChkDgt;

    }

    private static String calcBourseDSum(String payCode) throws ISOException {
        int totalOfProducts = 0;
        String checkDigit = "";
        int[] mazarebs = new int[]{13, 12, 11, 10, 9, 1, 2, 3, 4, 5, 6, 7, 8};
        for (int i = 13; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            totalOfProducts += (digit * mazarebs[i - 1]);
        }
        checkDigit = ISOUtil.zeropad(String.valueOf(totalOfProducts % 99), 2);
        return checkDigit;
    }

    // End Of bourse Algorith

    // 4 and 14 algorithm

    public static boolean is4and14Algorithm(String[] payCds) throws ModelException, ISOException {
        boolean is4and14ChkDgt = true;
        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() > 10) {
            is4and14ChkDgt = false;
            return false;
        }
        if (payCode.length() < 10)
            payCode = ISOUtil.zeropad(payCode, 10);

        if (!StringUtils.isNumeric(payCode)) {
            is4and14ChkDgt = false;
            return false;
        }

        String chkDgt = payCode.substring(payCode.length() - 2, payCode.length());

        if (!calc4and14DSum(payCode).equals(chkDgt)) {
            is4and14ChkDgt = false;
        }

        return is4and14ChkDgt;

    }

    private static String calc4and14DSum(String payCode) throws ISOException {
        int totalOfProducts = 0;
        String checkDigit = "";
        int[] mazarebs = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
        for (int i = 8; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            totalOfProducts += (digit * mazarebs[i - 1]);
        }
        checkDigit = String.valueOf(totalOfProducts % 99);
        return checkDigit;
    }

    // End Of 4 and 14 Algorithm

    // 13 algorithm

    public static boolean is13Algorithm(String[] payCds) throws ModelException, ISOException {
        boolean is13ChkDgt = true;
        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() > 8) {
            is13ChkDgt = false;
            return false;
        }
        if (payCode.length() < 8)
            payCode = ISOUtil.zeropad(payCode, 8);

        if (!StringUtils.isNumeric(payCode)) {
            is13ChkDgt = false;
            return false;
        }

        int chkDgt = Integer.parseInt(payCode.substring(payCode.length() - 1, payCode.length()));

        if (calc13DSum(payCode) != chkDgt) {
            is13ChkDgt = false;
        }

        return is13ChkDgt;

    }

    private static int calc13DSum(String payCode) throws ISOException {
        int totalOfProducts = 0;
        int checkDigit;
        int[] mazarebs = new int[]{1, 2, 3, 4, 5, 6, 7};
        for (int i = 7; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            totalOfProducts += (digit * mazarebs[i - 1]);
        }
        int n1 = (totalOfProducts % 11);
        if (n1 < 9)
            checkDigit = n1;
        else
            checkDigit = 0;

        return checkDigit;
    }

    // End Of 13 Algorithm

    // 17 algorithm

    public static boolean is17Algorithm(String[] payCds) throws ModelException, ISOException {
        boolean is17ChkDgt = true;
        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() != 15) {
            is17ChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            is17ChkDgt = false;
            return false;
        }

        int chkDgt = Integer.parseInt(payCode.substring(payCode.length() - 2, payCode.length()));

        if (calc17DSum(payCode) != chkDgt) {
            is17ChkDgt = false;
        }

        return is17ChkDgt;

    }

    private static int calc17DSum(String payCode) throws ISOException {
        int totalOfProducts = 0;
        int checkDigit;
        int[] mazarebs = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
        for (int i = 13; i > 0; i--) {
            int digit = 9 - (Integer.parseInt(payCode.substring(i - 1, i))); //mokamel 9
            totalOfProducts += (digit * mazarebs[i - 1]);
        }
        checkDigit = (totalOfProducts % 100);

        return checkDigit;
    }

    // End Of 17 Algorithm

    public static boolean isTabrizPetrochemicalsAlgorithm(String[] payCds, String accountNo) throws ModelException, ISOException {
        boolean isTabrizPetroChkDgt = true;

        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() != 17) {
            isTabrizPetroChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isTabrizPetroChkDgt = false;
            return false;
        }

        if (null == accountNo || "".equals(accountNo)) {
            isTabrizPetroChkDgt = false;
            return false;
        }

        accountNo = ISOUtil.zeropad(ISOUtil.zeroUnPad(accountNo), 10);
        if (!StringUtils.isNumeric(accountNo)) {
            isTabrizPetroChkDgt = false;
            return false;
        }

        String chkDgt = payCode.substring(payCode.length() - 2, payCode.length());
        if (!StringUtils.isNumeric(payCode)) {
            isTabrizPetroChkDgt = false;
            return false;
        }

        int totalOfId = calcTabrizpetropycddSum(payCode);
        int totalofAccNo = calcTabrizpetroAccNodSum(accountNo);
        if (!calcTabrizPetroSum(totalOfId, totalofAccNo).equals(chkDgt)) {
            isTabrizPetroChkDgt = false;
        }

        return isTabrizPetroChkDgt;

    }

    private static String calcTabrizPetroSum(int totalOfId, int totalofAccNo) throws ISOException {

        int total = 0;
        String checkDigit = "";
        total = totalOfId + totalofAccNo;
        checkDigit = ISOUtil.zeropad(String.valueOf(total % 99), 2);
        return checkDigit;
    }

    private static int calcTabrizpetroAccNodSum(String accountNo) throws ISOException {
        int totalofAccNo = 0;
        int digitB = 0;
        int[] mazarebsB = new int[]{5, 7, 11, 13, 17, 19, 23, 29, 31, 37};
        for (int i = 10; i > 0; i--) {
            digitB = Integer.parseInt(accountNo.substring(i - 1, i));
            totalofAccNo += (digitB * mazarebsB[i - 1]);
        }
        return totalofAccNo;

    }

    private static int calcTabrizpetropycddSum(String payCode) throws ISOException {
        int totalOfId = 0;
        int digitA = 0;

        int[] mazarebsA = new int[]{3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53};
        for (int i = 15; i > 0; i--) {
            digitA = Integer.parseInt(payCode.substring(i - 1, i));
            totalOfId += (digitA * mazarebsA[i - 1]);
        }
        return totalOfId;
    }

    public static boolean isIKCoAlgorithm(String[] payCds) throws ModelException, ISOException {

        boolean isIkcoChkDgt = true;

        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }


        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() != 9) {
            isIkcoChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isIkcoChkDgt = false;
            return false;
        }


        String chkDgt = payCode.substring(payCode.length() - 1, payCode.length());


        if (!calcIKcoChkDgt(payCode).equals(chkDgt)) {
            isIkcoChkDgt = false;
        }


        return isIkcoChkDgt;

    }

    private static String calcIKcoChkDgt(String payCode) throws ISOException {
        int totalOfId = 0;
        int digitA = 0;
        String checkDigit = "";


        int[] mazarebsA = new int[]{23, 19, 17, 13, 11, 7, 5, 3};

        for (int i = 0; i < 8; i++) {
            digitA = Integer.parseInt(payCode.substring(i, i + 1));
            totalOfId += (digitA * mazarebsA[i]);
        }

        checkDigit = String.valueOf(totalOfId % 10);

        return checkDigit;
    }

    private static String makeMotamam(String s) {
        String motamam = "";

        for (int i = 0; i < 13; i++) {
            motamam = motamam + String.valueOf((9 - Integer.parseInt(s.substring(i, i + 1))));
        }
        return motamam;
    }

    public static boolean isMachedbyPattern(String[] payCds, List patterns) throws ModelException, ISOException {
        boolean isMatched = false;
        boolean isDgtMatched;

        if ("".equals(payCds[0]) && "".equals(payCds[1]))
            throw new ModelException(Constants.DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT);

        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1]))
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }
        if (payCode.length() > 17) {
            throw new ModelException(Constants.PAYMENT_CODE_IS_INVALID);
        }

        if (payCode.length() < 17)
            payCode = ISOUtil.zeropad(payCode, 17);

        if (!StringUtils.isNumeric(payCode)) {
            throw new ModelException(Constants.PAYMENT_CODE_IS_INVALID);
        }

        for (int i = 0; i < patterns.size(); i++) {
            isDgtMatched = true;
            String ptrn = (String) patterns.get(i);
            for (int j = 0; j < ptrn.length(); j++) {
                String ptrnDgt = ptrn.substring(j, j + 1);
                if (!ptrnDgt.equals("*")) {

                    String pyCdDgt = payCode.substring(j, j + 1);
                    if (!ptrnDgt.equals(pyCdDgt)) {
                        isDgtMatched = false;
                        break;
                    }
                }

            }
            if (isDgtMatched) isMatched = true;
            if (isMatched) break;
        }

        return isMatched;
    }

    //-------------------------------------------------------------------------
//  national ID

    public static boolean isNationalIdAlgorithm(String[] payCds) throws ModelException, ISOException {
        boolean isNationalIdDgt = true;
        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];

        if (payCode.length() != 11) {
            isNationalIdDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isNationalIdDgt = false;
            return false;
        }

        String chkDgt = payCode.substring(payCode.length() - 1, payCode.length());

        if (!calcNationalIdSum(payCode).equals(chkDgt)) {
            isNationalIdDgt = false;
        }

        return isNationalIdDgt;

    }

    private static String calcNationalIdSum(String payCode) throws ISOException {

        String checkDigit = "";
        int totalOfId = 0;
        int digit = 0;
        int[] mazarebs = {29, 27, 23, 19, 17, 29, 27, 23, 19, 17};
        int dahgan = Integer.parseInt(payCode.substring(9, 10)) + 2;
        for (int i = 0; i < 10; i++) {
            digit = Integer.parseInt(payCode.substring(i, i + 1));
//            System.out.println(" ((" + digit + "+" + dahgan + ") * " + mazarebs[i] + "))");
            totalOfId += ((digit + dahgan) * mazarebs[i]);
        }
//        System.out.println("totalOfId = " + totalOfId);
        checkDigit = String.valueOf(totalOfId % 11 == 10 ? 0 : totalOfId % 11);
        return checkDigit;
    }

    //------------------------------------------------------------------------
    public static boolean isNationalIdAndCodeAlgorithm(String[] payCds) throws ModelException, ISOException {
        boolean isNationalIdCodeDgt = true;
        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];

        if (payCode.length() != 10 && payCode.length() != 11) {
            isNationalIdCodeDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isNationalIdCodeDgt = false;
            return false;
        }


        if (payCode.length() == 10) {
            isNationalIdCodeDgt = isNationalCodeAlgorithm(payCds);
        } else {
            isNationalIdCodeDgt = isNationalIdAlgorithm(payCds);
        }


        return isNationalIdCodeDgt;

    }

    //    Stck Algorithm
    public static boolean isStckAlgorithm(String[] payCds, String accNo) throws ModelException, ISOException {
        boolean isStckCheckDigit = true;
        accNo = ISOUtil.zeropad(ISOUtil.zeroUnPad(accNo), 10);
        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1]))
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1]))
            payCode = payCds[1];

        if (payCode.length() > 17) {
            isStckCheckDigit = false;
            return false;
        }

        if (payCode.length() < 17)
            payCode = ISOUtil.zeropad(payCode, 17);

        if (!payCode.substring(14, 15).equals("1")) {
            isStckCheckDigit = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isStckCheckDigit = false;
            return false;
        }

        int checkDigit = Integer.parseInt(payCode.substring(payCode.length() - 2, payCode.length()));
        int payCodeDSum = calcStckPayCodeSum(payCode.substring(0, 15));
        int accNoDSum = calcStckAccNoSum(accNo);

        int totalDSum = (payCodeDSum + accNoDSum) % 99;

        if (totalDSum != checkDigit)
            isStckCheckDigit = false;
        return isStckCheckDigit;
    }

    public static int calcStckPayCodeSum(String payCode) {
        int dSum = 0;
        int[] primeNums = new int[]{3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53};
        for (int i = 15; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            int num = primeNums[i - 1];
            dSum = dSum + (digit * num);
        }
        return dSum;
    }

    public static int calcStckAccNoSum(String accNo) {
        int dSum = 0;
        int[] primeNums = new int[]{5, 7, 11, 13, 17, 19, 23, 29, 31, 37};
        for (int i = 10; i > 0; i--) {
            int digit = Integer.parseInt(accNo.substring(i - 1, i));
            int num = primeNums[i - 1];
            dSum = dSum + (digit * num);

        }
        return dSum;
    }

//    End of StckAlgorithm


    //    GolestanTransport
    public static boolean isGolestanTransport(String[] payCds) throws ModelException, ISOException {
        boolean isGolestanTransportChkDgt = true;
        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() > 17) {
            isGolestanTransportChkDgt = false;
            return false;
        }

        if (payCode.length() < 17)
            payCode = ISOUtil.zeropad(payCode, 17);

        if (!StringUtils.isNumeric(payCode)) {
            isGolestanTransportChkDgt = false;
            return false;
        }

        int chkDgt = Integer.parseInt(payCode.substring(payCode.length() - 2, payCode.length()));

        if (calcGolestanTransportDSum(payCode) != chkDgt) {
            isGolestanTransportChkDgt = false;
        }

        return isGolestanTransportChkDgt;

    }

    private static int calcGolestanTransportDSum(String payCode) {
        int dSum = 0;
        int checkDigit;
        int[] mazarebs = new int[]{15, 14, 13, 12, 11, 10, 9, 1, 2, 3, 4, 5, 6, 7, 8};
        for (int i = 15; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            dSum += (digit * mazarebs[i - 1]);
        }
        checkDigit = (dSum % 99);
        return checkDigit;
    }
//    End of GolestanTransport


    //    Type1AlgorithmWithZeroAmount
    public static boolean isType1AlgorithmWithZeroAmount(String[] payCodes, String accNo, String Amount) throws ISOException, ModelException {
        boolean isType1ChkDgt = true;

        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCodes[0];
        if ("".equals(payCode) && !"".equals(payCodes[1])) {
            payCode = payCodes[1];
        }

        if (payCode.length() != 17) {
            isType1ChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isType1ChkDgt = false;
            return false;
        }

        int checkDigit = Integer.parseInt(payCode.substring(payCode.length() - 2, payCode.length()));

        int payCdDSum = calcType1PayCodeChkDigit(payCode);
        accNo = ISOUtil.zeropad(ISOUtil.zeroUnPad(accNo), 10);
        int accNoDSum = calcType1AccNoChkDigit(accNo);
        int amountDSum = calcType1AmountChkDigit(Amount);

        String totalDSum = ISOUtil.zeropad(String.valueOf((payCdDSum + accNoDSum + amountDSum) % 99), 2);

        if (Integer.parseInt(totalDSum) != checkDigit) {
            isType1ChkDgt = false;
            return false;
        }

        return isType1ChkDgt;

    }

//    End of Type1AlgorithmWithZeroAmount

    public static boolean isTehranUniversity(String[] payCodes, String accNo, String Amount) throws ISOException, ModelException {
        boolean isType1ChkDgt = true;

        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCodes[0];
        if ("".equals(payCode) && !"".equals(payCodes[1])) {
            payCode = payCodes[1];
        }

        if (payCode.length() > 17) {
            isType1ChkDgt = false;
            return false;
        }

        if (payCode.length() < 17)
            payCode = ISOUtil.zeropad(payCode, 17);


        if (!StringUtils.isNumeric(payCode)) {
            isType1ChkDgt = false;
            return false;
        }

        int checkDigit = Integer.parseInt(payCode.substring(payCode.length() - 2, payCode.length()));

        int payCdDSum = calcType1PayCodeChkDigit(payCode);
        accNo = ISOUtil.zeropad(ISOUtil.zeroUnPad(accNo), 10);
        int accNoDSum = calcType1AccNoChkDigit(accNo);
        int amountDSum = calcType1AmountChkDigit(Amount);

        String totalDSum = ISOUtil.zeropad(String.valueOf((payCdDSum + accNoDSum + amountDSum) % 99), 2);

        if (Integer.parseInt(totalDSum) != checkDigit) {
            isType1ChkDgt = false;
            return false;
        }

        return isType1ChkDgt;

    }


//    End of Type1AlgorithmWithZeroAmount

    // Alborz University Algorithm
    public static boolean isAlborzUnivAlgorithm(String[] payCds) throws ModelException, ISOException {
        boolean isalborz = true;
        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1]))
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }
        payCode = ISOUtil.zeropad(payCode, 13);

        if (!StringUtils.isNumeric(payCode)) {
            isalborz = false;
            return false;
        }

        if (payCode.length() != 13) {
            isalborz = false;
            return false;
        }

        int chkDg = Integer.parseInt(payCode.substring(payCode.length() - 2, payCode.length()));
        if (calcalborzDSum(payCode) != chkDg) {
            isalborz = false;
        }

        return isalborz;
    }

    public static int calcalborzDSum(String payId) {
        int dSum = 0;
        int checkDigit;
        int[] mazarebs = new int[]{13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53};
        for (int i = 11; i > 0; i--) {
            int digit = Integer.parseInt(payId.substring(i - 1, i));
            dSum = dSum + (digit * mazarebs[i - 1]);
        }
        checkDigit = ((dSum + 842) % 99);
        return checkDigit;
    }

    //End of Alborz University Algorithm
    // isValidVerhoef Algorithm

    public static boolean isValidVerhoef(String[] code, String accountNumber, String amount) throws ModelException {
        /**
         * @param trxID         must be 15 digits
         * @param accountNumber must be 13 digits
         * @param amount        must be 15 digits (pad with zero if needed[I dont know]
         * @param code          generated code that must be 15 digits
         * @return true if code is valid for BMI
         * @throws org.apache.commons.validator.routines.checkdigit.CheckDigitException
         */

        accountNumber = ISOUtil.zeroUnPad(accountNumber);
        if (accountNumber.length() > 10)
            return false;

        if (!"".equals(code[0]) && !"".equals(code[1]) && !code[0].equals(code[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = code[0];
        if ("".equals(payCode) && !"".equals(code[1])) {
            payCode = code[1];
        }

        if (payCode.length() != 15)
            return false;

        else {
            String trxID = payCode.substring(0, 13);
            try {
                if (!NumberUtils.isDigits(payCode) || !NumberUtils.isDigits(accountNumber)
                        || !NumberUtils.isDigits(trxID) || !NumberUtils.isDigits(amount)) {

                    throw new CheckDigitException("invalid input:" + "trxID:" + trxID + "accountNumber:" + accountNumber + "amount:" + amount + "code:" + payCode);
                }
                amount = org.apache.commons.lang3.StringUtils.repeat("0", 15 - amount.length()) + amount;
                String trxIDTemp = org.apache.commons.lang3.StringUtils.repeat("0", 2) + trxID;
                String n1 = VerhoeffCheckDigit.VERHOEFF_CHECK_DIGIT.calculate(trxIDTemp + accountNumber + amount);
                String n2 = VerhoeffCheckDigit.VERHOEFF_CHECK_DIGIT.calculate(org.apache.commons.lang3.StringUtils.reverse(trxIDTemp) +
                        org.apache.commons.lang3.StringUtils.reverse(accountNumber) + org.apache.commons.lang3.StringUtils.reverse(amount));
                String calculated = trxID + n1 + n2;
                return calculated.equals(payCode);
            } catch (CheckDigitException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    //end of isValidVerhoef Algorithm

    //isGilanAlgorithm Algorithm
    public static boolean isGilanAlgorithm(String[] payCds, String accountNum, String amount) throws ISOException, ModelException {

        boolean isMoudGilanChkDgt = true;

        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1]))
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (!StringUtils.isNumeric(payCode)) {
            isMoudGilanChkDgt = false;
        }
        int algorithmType = 0;
        amount = ISOUtil.zeropad(amount, 15);
        String totalChkDgt = "";
        if (!StringUtils.isNumeric(payCode)) {
            isMoudGilanChkDgt = false;
        }
        if (payCode.length() > 17) {
            isMoudGilanChkDgt = false;
        }
        if (payCode.length() < 17) {
            payCode = ISOUtil.zeropad(payCode, 17);
        }
        algorithmType = Integer.parseInt(payCode.substring(14, 15));
        String checkDigit = payCode.substring(payCode.length() - 2);
//        System.out.println("checkDigit is : " + checkDigit);
        accountNum = ISOUtil.zeropad(ISOUtil.zeroUnPad(accountNum), 10);


        if (algorithmType == 1) {
            System.out.println("******************payCode = " + payCode);
            totalChkDgt = gilanAlgorithm1(payCode, accountNum);
        } else if (algorithmType == 2) {
            totalChkDgt = gilanAlgorithm2(payCode, accountNum, amount);
        } else if (algorithmType == 3) {
            totalChkDgt = gilanAlgorithm3(payCode, accountNum);
        } else if (algorithmType == 4) {
            totalChkDgt = gilanAlgorithm4(payCode, accountNum, amount);
        }
        if (!checkDigit.equals(totalChkDgt)) {
            isMoudGilanChkDgt = false;
        }
//        System.out.println("check digit is :" + isMoudGilanChkDgt);
        return isMoudGilanChkDgt;

    }

    public static String gilanAlgorithm1(String payCode, String accountNum) throws ISOException {
        System.out.println("**********payCode = " + payCode);
        int SumA = 0;
        int SumB = 809;
        int totalSum = 0;
        int algorithmType = 0;
        String remain = "";
        int mazarebA[] = {3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47};
        int mazarebB[] = {5, 7, 11, 13, 17, 19, 23, 29, 31, 37};

        for (int i = 0; i < 14; i++) {
            int digitA = Integer.parseInt(payCode.substring(i, i + 1));
            SumA += (digitA * mazarebA[i]);
        }
//        for (int j = 0; j < 10; j++) {
//            int digitB = Integer.parseInt(accountNum.substring(j, j + 1));
//            SumB += (digitB * mazarebB[j]);
//        }
        if (payCode.length() == 17) {
            algorithmType = Integer.parseInt(payCode.substring(14, 15));
        }
        totalSum = SumA + SumB + (53 * algorithmType);
        System.out.println("************totalSum = " + totalSum);
        remain = ISOUtil.zeropad(String.valueOf(totalSum % 99), 2);
        System.out.println("************remain = " + remain);
        return remain;
    }

    public static String gilanAlgorithm2(String payCode, String accountNum, String amount) throws ISOException {
        int SumA = 0;
        int SumB = 809;
        int SumC = 0;
        int totalSum = 0;
        int algorithmType = 0;
        String remain = "";
        int mazarebA[] = {3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47};
        int mazarebB[] = {5, 7, 11, 13, 17, 19, 23, 29, 31, 37};
        int mazarebC[] = {7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61};

        for (int i = 0; i < 14; i++) {
            int digitA = Integer.parseInt(payCode.substring(i, i + 1));
            SumA += (digitA * mazarebA[i]);
        }
//        for (int j = 0; j < 10; j++) {
//            int digitB = Integer.parseInt(accountNum.substring(j, j + 1));
//            SumB += (digitB * mazarebB[j]);
//        }


        for (int k = 0; k < 15; k++) {
            int digitC = Integer.parseInt(amount.substring(k, k + 1));
            SumC += (digitC * mazarebC[k]);
        }
        if (payCode.length() == 17) {
            algorithmType = Integer.parseInt(payCode.substring(14, 15));
        }
        totalSum = SumA + SumB + SumC + (53 * algorithmType);
        remain = ISOUtil.zeropad(String.valueOf(totalSum % 99), 2);
        return remain;
    }

    public static String gilanAlgorithm3(String payCode, String accountNum) throws ISOException {
        int SumA = 0;
        int SumB = 809;
        int dayOfYear = 0;
        int totalSum = 0;
        int algorithmType = 0;
        int yearNumber = 0;
        String daySubString = "";
        String remain = "";
        int mazarebA[] = {3, 5, 7, 11, 13, 17, 19, 23, 29, 31};
        int mazarebB[] = {5, 7, 11, 13, 17, 19, 23, 29, 31, 37};
        int mazarebdays[] = {41, 43, 47};

        for (int i = 0; i < 10; i++) {
            int digitA = Integer.parseInt(payCode.substring(i, i + 1));
            SumA += (digitA * mazarebA[i]);
        }
//        for (int j = 0; j < 10; j++) {
//            int digitB = Integer.parseInt(accountNum.substring(j, j + 1));
//            SumB += (digitB * mazarebB[j]);
//        }
        if (payCode.length() == 17) {
            algorithmType = Integer.parseInt(payCode.substring(14, 15));
            yearNumber = Integer.parseInt(payCode.substring(10, 11));
            daySubString = payCode.substring(11, 14);
            for (int k = 0; k < 3; k++) {
                int digitdays = Integer.parseInt(daySubString.substring(k, k + 1));
                dayOfYear += (digitdays * mazarebdays[k]);
            }
        }
        totalSum = SumA + SumB + (53 * algorithmType) + (37 * yearNumber) + dayOfYear;
        remain = ISOUtil.zeropad(String.valueOf(totalSum % 99), 2);
        return remain;
    }

    public static String gilanAlgorithm4(String payCode, String accountNum, String amount) throws ISOException {
        int SumA = 0;
        int SumB = 809;
        int SumC = 0;
        int dayOfYear = 0;
        int totalSum = 0;
        int algorithmType = 0;
        int yearNumber = 0;
        String daySubString = "";
        String remain = "";
        int mazarebA[] = {3, 5, 7, 11, 13, 17, 19, 23, 29, 31};
        int mazarebB[] = {5, 7, 11, 13, 17, 19, 23, 29, 31, 37};
        int mazarebC[] = {7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61};
        int mazarebdays[] = {41, 43, 47};

        for (int i = 0; i < 10; i++) {
            int digitA = Integer.parseInt(payCode.substring(i, i + 1));
            SumA += (digitA * mazarebA[i]);
        }
//        for (int j = 0; j < 10; j++) {
//            int digitB = Integer.parseInt(accountNum.substring(j, j + 1));
//            SumB += (digitB * mazarebB[j]);
//        }
        for (int z = 0; z < 15; z++) {
            int digitC = Integer.parseInt(amount.substring(z, z + 1));
            SumC += (digitC * mazarebC[z]);
        }
        if (payCode.length() == 17) {
            algorithmType = Integer.parseInt(payCode.substring(14, 15));
            yearNumber = Integer.parseInt(payCode.substring(10, 11));
            daySubString = payCode.substring(11, 14);
            for (int k = 0; k < 3; k++) {
                int digitdays = Integer.parseInt(daySubString.substring(k, k + 1));
                dayOfYear += (digitdays * mazarebdays[k]);
            }
        }
        totalSum = SumA + SumB + SumC + (53 * algorithmType) + (37 * yearNumber) + dayOfYear;
        remain = ISOUtil.zeropad(String.valueOf(totalSum % 99), 2);
        return remain;
    }

    //end of isGilanAlgorithm Algorithm

    //isTracktorTabrizAlgorithm Algorithm
    public static boolean isTracktorTabrizAlgorithm(String[] payCds, String accNo, String amount) throws ISOException, ModelException {
        boolean isTracktorTabrizChkDgt = true;
        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }
        if (!StringUtils.isNumeric(payCode)) {
            isTracktorTabrizChkDgt = false;
        }
        String algorithmType = ""; //which kind of Algorithm

        amount = ISOUtil.zeropad(ISOUtil.zeroUnPad(amount), 15);
        accNo = ISOUtil.zeropad(ISOUtil.zeroUnPad(accNo), 10);

        String totalChkDgt = "";
        if (!StringUtils.isNumeric(payCode)) {
            isTracktorTabrizChkDgt = false;
            return false;
        }
        if (payCode.length() > 17) {
            isTracktorTabrizChkDgt = false;
            return false;
        }
        if (payCode.length() < 17) {
            payCode = ISOUtil.zeropad(payCode, 17);
        }

        algorithmType = payCode.substring(14, 15);
        String checkDigit = payCode.substring(payCode.length() - 2);
        if (algorithmType.equals("1")) {
            totalChkDgt = calcTracktorTabrizChkDgt1(payCode, accNo);
        } else if (algorithmType.equals("2")) {
            totalChkDgt = calcTracktorTabrizChkDgt2(payCode, accNo, amount);
        } else if (algorithmType.equals("3")) {
            totalChkDgt = calcTracktorTabrizChkDgt3(payCode, accNo);
        } else if (algorithmType.equals("4")) {
            totalChkDgt = calcTracktorTabrizChkDgt4(payCode, accNo, amount);
        }
        if (!checkDigit.equals(totalChkDgt)) {
            isTracktorTabrizChkDgt = false;
            return false;
        }
        return isTracktorTabrizChkDgt;

    }

    public static String calcTracktorTabrizChkDgt1(String payCode, String accNo) throws ISOException {
        int SumA = 0;
        int SumB = 0;
        int totalSum = 0;
        int remain = 0;
        String strremain;
        int mazarebA[] = {3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53};
        int mazarebB[] = {5, 7, 11, 13, 17, 19, 23, 29, 31, 37};

        for (int i = 0; i < 15; i++) {
            int digitA = Integer.parseInt(payCode.substring(i, i + 1));
            SumA += (digitA * mazarebA[i]);
        }
        for (int j = 0; j < 10; j++) {
            int digitB = Integer.parseInt(accNo.substring(j, j + 1));
            SumB += (digitB * mazarebB[j]);
        }

        totalSum = SumA + SumB;
        remain = totalSum % 99;
        strremain = ISOUtil.zeropad(String.valueOf(remain), 2);
        return strremain;
    }

    public static String calcTracktorTabrizChkDgt2(String payCode, String accNo, String amount) throws ISOException {
        int SumA = 0;
        int SumB = 0;
        int SumC = 0;
        int totalSum = 0;
        int remain = 0;
        String strremain;
        int mazarebA[] = {3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53};
        int mazarebB[] = {5, 7, 11, 13, 17, 19, 23, 29, 31, 37};
        int mazarebC[] = {7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61};

        for (int i = 0; i < 15; i++) {
            int digitA = Integer.parseInt(payCode.substring(i, i + 1));
            SumA += (digitA * mazarebA[i]);
        }
        for (int j = 0; j < 10; j++) {
            int digitB = Integer.parseInt(accNo.substring(j, j + 1));
            SumB += (digitB * mazarebB[j]);
        }
        for (int k = 0; k < 15; k++) {
            int digitC = Integer.parseInt(amount.substring(k, k + 1));
            SumC += (digitC * mazarebC[k]);
        }

        totalSum = SumA + SumB + SumC;
        remain = totalSum % 99;
        strremain = ISOUtil.zeropad(String.valueOf(remain), 2);
        return strremain;
    }

    public static String calcTracktorTabrizChkDgt3(String payCode, String accNo) throws ISOException, ModelException {
        int SumA = 0;
        int SumB = 0;
        int dayOfYear = 0;
        int totalSum = 0;
        int remain = 0;
        String strremain;
        int mazarebA[] = {3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53};
        int mazarebB[] = {5, 7, 11, 13, 17, 19, 23, 29, 31, 37};

        String date = DateUtil.getSystemDate();

        String lastDigitOfCurrentYear = date.substring(3, 4);
        int currentMonth = (Integer.valueOf(date.substring(4, 6)));
        if (currentMonth <= 6) {
            dayOfYear = (currentMonth - 1) * 31 + (Integer.valueOf(date.substring(6, 8)));
        } else if (currentMonth >= 7) {
            dayOfYear = 186 + (currentMonth - 7) * 30 + (Integer.valueOf(date.substring(6, 8)));
        }

        String yearNumber = payCode.substring(10, 11);

        int calcTotalNum = (100 * Integer.parseInt(payCode.substring(11, 12))) + (10 * Integer.parseInt(payCode.substring(12, 13))) + (1 * Integer.parseInt(payCode.substring(13, 14)));
        if (calcTotalNum < dayOfYear) {
            throw new ModelException(Constants.INVALID_PAYMENT_CODE);
        }

        if (!yearNumber.equals(lastDigitOfCurrentYear)) {
            throw new ModelException(Constants.INVALID_PAYMENT_CODE);
        }

        for (int i = 0; i < 15; i++) {
            int digitA = Integer.parseInt(payCode.substring(i, i + 1));
            SumA += (digitA * mazarebA[i]);
        }
        for (int j = 0; j < 10; j++) {
            int digitB = Integer.parseInt(accNo.substring(j, j + 1));
            SumB += (digitB * mazarebB[j]);
        }

        totalSum = SumA + SumB;
        remain = totalSum % 99;
        strremain = ISOUtil.zeropad(String.valueOf(remain), 2);
        return strremain;
    }

    public static String calcTracktorTabrizChkDgt4(String payCode, String accNo, String amount) throws ISOException, ModelException {
        int SumA = 0;
        int SumB = 0;
        int SumC = 0;
        int dayOfYear = 0;
        int totalSum = 0;
        int remain = 0;
        String strremain;
        int mazarebA[] = {3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53};
        int mazarebB[] = {5, 7, 11, 13, 17, 19, 23, 29, 31, 37};
        int mazarebC[] = {7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61};

        String date = DateUtil.getSystemDate();

        String lastDigitOfCurrentYear = date.substring(3, 4);
        int currentMonth = (Integer.valueOf(date.substring(4, 6)));
        if (currentMonth <= 6) {
            dayOfYear = (currentMonth - 1) * 31 + (Integer.valueOf(date.substring(6, 8)));
        } else if (currentMonth >= 7) {
            dayOfYear = 186 + (currentMonth - 7) * 30 + (Integer.valueOf(date.substring(6, 8)));
        }

        String yearNumber = payCode.substring(10, 11);


        int calcTotalNum = (100 * Integer.parseInt(payCode.substring(11, 12))) + (10 * Integer.parseInt(payCode.substring(12, 13))) + (1 * Integer.parseInt(payCode.substring(13, 14)));
        if (calcTotalNum < dayOfYear) {
            throw new ModelException(Constants.INVALID_PAYMENT_CODE);
        }
        if (!yearNumber.equals(lastDigitOfCurrentYear)) {
            throw new ModelException(Constants.INVALID_PAYMENT_CODE);
        }

        for (int i = 0; i < 15; i++) {
            int digitA = Integer.parseInt(payCode.substring(i, i + 1));
            SumA += (digitA * mazarebA[i]);
        }
        for (int j = 0; j < 10; j++) {
            int digitB = Integer.parseInt(accNo.substring(j, j + 1));
            SumB += (digitB * mazarebB[j]);
        }
        for (int k = 0; k < 15; k++) {
            int digitC = Integer.parseInt(amount.substring(k, k + 1));
            SumC += (digitC * mazarebC[k]);
        }

        totalSum = SumA + SumB + SumC;
        remain = totalSum % 99;
        strremain = ISOUtil.zeropad(String.valueOf(remain), 2);
        return strremain;
    }

    //end of isTracktorTabrizAlgorithm Algorithm

    //    isPetroshimiTabrizAlgorithm Algorithm
    public static boolean isPetroshimiTabrizAlgorithm(String[] payCds, String accNo, String amount) throws ISOException, ModelException {
        boolean isPetroshimiTabrizChkDgt = true;
        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }
        if (!StringUtils.isNumeric(payCode)) {
            isPetroshimiTabrizChkDgt = false;
        }
        String algorithmType = ""; //which kind of Algorithm

        amount = ISOUtil.zeropad(ISOUtil.zeroUnPad(amount), 15);
        accNo = ISOUtil.zeropad(ISOUtil.zeroUnPad(accNo), 10);

        String totalChkDgt = "";
        if (!StringUtils.isNumeric(payCode)) {
            isPetroshimiTabrizChkDgt = false;
            return false;
        }
        if (payCode.length() > 17) {
            isPetroshimiTabrizChkDgt = false;
            return false;
        }
        if (payCode.length() < 17) {
            payCode = ISOUtil.zeropad(payCode, 17);
        }

        algorithmType = payCode.substring(14, 15);
        String checkDigit = payCode.substring(payCode.length() - 2);
        if (algorithmType.equals("1")) {
            totalChkDgt = calcPetroshimiTabrizChkDgt1(payCode, accNo);
        } else if (algorithmType.equals("2")) {
            totalChkDgt = calcPetroshimiTabrizChkDgt2(payCode, accNo, amount);
        } else if (algorithmType.equals("3")) {
            totalChkDgt = calcPetroshimiTabrizChkDgt3(payCode, accNo);
        } else if (algorithmType.equals("4")) {
            totalChkDgt = calcPetroshimiTabrizChkDgt4(payCode, accNo, amount);
        }
        if (!checkDigit.equals(totalChkDgt)) {
            isPetroshimiTabrizChkDgt = false;
            return false;
        }
        return isPetroshimiTabrizChkDgt;

    }

    public static String calcPetroshimiTabrizChkDgt1(String payCode, String accNo) throws ISOException {
        int SumA = 0;
        int SumB = 0;
        int totalSum = 0;
        int remain = 0;
        String strremain;
        int mazarebA[] = {3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53};
        int mazarebB[] = {5, 7, 11, 13, 17, 19, 23, 29, 31, 37};

        for (int i = 0; i < 15; i++) {
            int digitA = Integer.parseInt(payCode.substring(i, i + 1));
            SumA += (digitA * mazarebA[i]);
        }
        for (int j = 0; j < 10; j++) {
            int digitB = Integer.parseInt(accNo.substring(j, j + 1));
            SumB += (digitB * mazarebB[j]);
        }

        totalSum = SumA + SumB;
        remain = totalSum % 99;
        strremain = ISOUtil.zeropad(String.valueOf(remain), 2);
        return strremain;
    }

    public static String calcPetroshimiTabrizChkDgt2(String payCode, String accNo, String amount) throws ISOException {
        int SumA = 0;
        int SumB = 0;
        int SumC = 0;
        int totalSum = 0;
        int remain = 0;
        String strremain;
        int mazarebA[] = {3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53};
        int mazarebB[] = {5, 7, 11, 13, 17, 19, 23, 29, 31, 37};
        int mazarebC[] = {7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61};

        for (int i = 0; i < 15; i++) {
            int digitA = Integer.parseInt(payCode.substring(i, i + 1));
            SumA += (digitA * mazarebA[i]);
        }
        for (int j = 0; j < 10; j++) {
            int digitB = Integer.parseInt(accNo.substring(j, j + 1));
            SumB += (digitB * mazarebB[j]);
        }
        for (int k = 0; k < 15; k++) {
            int digitC = Integer.parseInt(amount.substring(k, k + 1));
            SumC += (digitC * mazarebC[k]);
        }

        totalSum = SumA + SumB + SumC;
        remain = totalSum % 99;
        strremain = ISOUtil.zeropad(String.valueOf(remain), 2);
        return strremain;
    }

    public static String calcPetroshimiTabrizChkDgt3(String payCode, String accNo) throws ISOException, ModelException {
        int SumA = 0;
        int SumB = 0;
        int dayOfYear = 0;
        int totalSum = 0;
        int remain = 0;
        String strremain;
        int mazarebA[] = {3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53};
        int mazarebB[] = {5, 7, 11, 13, 17, 19, 23, 29, 31, 37};

        String date = DateUtil.getSystemDate();

        String yearNumber = payCode.substring(10, 11);
        String lastDigitOfCurrentYear = date.substring(3, 4);

        if (lastDigitOfCurrentYear.equals("9")) {
            if (yearNumber.equals("8"))
                throw new ModelException(Constants.INVALID_PAYMENT_CODE);
        } else if (lastDigitOfCurrentYear.equals("0") || lastDigitOfCurrentYear.equals("1") || lastDigitOfCurrentYear.equals("2")
                || lastDigitOfCurrentYear.equals("3") || lastDigitOfCurrentYear.equals("4") || lastDigitOfCurrentYear.equals("5")
                || lastDigitOfCurrentYear.equals("6") || lastDigitOfCurrentYear.equals("7")) {
            if (Integer.parseInt(yearNumber) < Integer.parseInt(lastDigitOfCurrentYear) || yearNumber.equals("8") || yearNumber.equals("9"))
                throw new ModelException(Constants.INVALID_PAYMENT_CODE);

        }

        if (yearNumber.equals(lastDigitOfCurrentYear)) {

            int currentMonth = (Integer.valueOf(date.substring(4, 6)));
            if (currentMonth <= 6) {
                dayOfYear = (currentMonth - 1) * 31 + (Integer.valueOf(date.substring(6, 8)));
            } else if (currentMonth >= 7) {
                dayOfYear = 186 + (currentMonth - 7) * 30 + (Integer.valueOf(date.substring(6, 8)));
            }


            int calcTotalNum = (100 * Integer.parseInt(payCode.substring(11, 12))) + (10 * Integer.parseInt(payCode.substring(12, 13))) + (1 * Integer.parseInt(payCode.substring(13, 14)));
            if (calcTotalNum < dayOfYear) {
                throw new ModelException(Constants.INVALID_PAYMENT_CODE);
            }
        }

        for (int i = 0; i < 15; i++) {
            int digitA = Integer.parseInt(payCode.substring(i, i + 1));
            SumA += (digitA * mazarebA[i]);
        }
        for (int j = 0; j < 10; j++) {
            int digitB = Integer.parseInt(accNo.substring(j, j + 1));
            SumB += (digitB * mazarebB[j]);
        }

        totalSum = SumA + SumB;
        remain = totalSum % 99;
        strremain = ISOUtil.zeropad(String.valueOf(remain), 2);
        return strremain;
    }

    public static String calcPetroshimiTabrizChkDgt4(String payCode, String accNo, String amount) throws ISOException, ModelException {
        int SumA = 0;
        int SumB = 0;
        int SumC = 0;
        int dayOfYear = 0;
        int totalSum = 0;
        int remain = 0;
        String strremain;
        int mazarebA[] = {3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53};
        int mazarebB[] = {5, 7, 11, 13, 17, 19, 23, 29, 31, 37};
        int mazarebC[] = {7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61};

        String date = DateUtil.getSystemDate();

        String yearNumber = payCode.substring(10, 11);
        String lastDigitOfCurrentYear = date.substring(3, 4);

        if (lastDigitOfCurrentYear.equals("9")) {
            if (yearNumber.equals("8"))
                throw new ModelException(Constants.INVALID_PAYMENT_CODE);
        } else if (lastDigitOfCurrentYear.equals("0") || lastDigitOfCurrentYear.equals("1") || lastDigitOfCurrentYear.equals("2")
                || lastDigitOfCurrentYear.equals("3") || lastDigitOfCurrentYear.equals("4") || lastDigitOfCurrentYear.equals("5")
                || lastDigitOfCurrentYear.equals("6") || lastDigitOfCurrentYear.equals("7")) {
            if (Integer.parseInt(yearNumber) < Integer.parseInt(lastDigitOfCurrentYear) || yearNumber.equals("8") || yearNumber.equals("9"))
                throw new ModelException(Constants.INVALID_PAYMENT_CODE);

        }

        if (yearNumber.equals(lastDigitOfCurrentYear)) {

            int currentMonth = (Integer.valueOf(date.substring(4, 6)));
            if (currentMonth <= 6) {
                dayOfYear = (currentMonth - 1) * 31 + (Integer.valueOf(date.substring(6, 8)));
            } else if (currentMonth >= 7) {
                dayOfYear = 186 + (currentMonth - 7) * 30 + (Integer.valueOf(date.substring(6, 8)));
            }


            int calcTotalNum = (100 * Integer.parseInt(payCode.substring(11, 12))) + (10 * Integer.parseInt(payCode.substring(12, 13))) + (1 * Integer.parseInt(payCode.substring(13, 14)));
            if (calcTotalNum < dayOfYear) {
                throw new ModelException(Constants.INVALID_PAYMENT_CODE);
            }
        }

        for (int i = 0; i < 15; i++) {
            int digitA = Integer.parseInt(payCode.substring(i, i + 1));
            SumA += (digitA * mazarebA[i]);
        }
        for (int j = 0; j < 10; j++) {
            int digitB = Integer.parseInt(accNo.substring(j, j + 1));
            SumB += (digitB * mazarebB[j]);
        }
        for (int k = 0; k < 15; k++) {
            int digitC = Integer.parseInt(amount.substring(k, k + 1));
            SumC += (digitC * mazarebC[k]);
        }

        totalSum = SumA + SumB + SumC;
        remain = totalSum % 99;
        strremain = ISOUtil.zeropad(String.valueOf(remain), 2);
        return strremain;
    }
//    end of isPetroshimiTabrizAlgorithm Algorithm

    //isKordestanAlgorithm Algorithm
    public static boolean isKordestanAlgorithm(String[] payCds, String accountNo) throws ModelException, ISOException {

        boolean isKordestanDgt = true;
        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }
        if (!StringUtils.isNumeric(payCode)) {
            isKordestanDgt = false;
            return false;
        }

        try {
            Vector checkDigits = ChannelFacadeNew.getCMParam(accountNo);
            Iterator it = checkDigits.iterator();
            while (it.hasNext()) {
                Cmparam param = (Cmparam) it.next();
                if (param.getDescription().trim().equals(payCode)) {
                    isKordestanDgt = true;
                    return isKordestanDgt;
                } else
                    isKordestanDgt = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return isKordestanDgt;

    }

    //end of isKordestanAlgorithm Algorithm

    //    isValiasrAccountAlgorithm
    public static boolean isValiasrAccountAlgorithm(String[] payCds) throws ModelException, ISOException {
        boolean isValiasrAccountChkDgt = true;

        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() > 17) {
            isValiasrAccountChkDgt = false;
            return false;
        }
        if (payCode.length() < 17) {
            payCode = ISOUtil.zeropad(payCode, 17);
        }

        if (!StringUtils.isNumeric(payCode)) {
            isValiasrAccountChkDgt = false;
            return false;
        }


        String chkDgt = ISOUtil.zeropad(payCode.substring(payCode.length() - 2, payCode.length()), 2);

        if (!calcAlgorithmValiasrAccount(payCode).equals(chkDgt)) {
            isValiasrAccountChkDgt = false;
        }
        return isValiasrAccountChkDgt;
    }

    public static String calcAlgorithmValiasrAccount(String payCode) throws ISOException {
        int totalSum = 0;
        int remain = 0;
        String strremain;
        int mazarebA[] = {15, 14, 13, 12, 11, 10, 9, 1, 2, 3, 4, 5, 6, 7, 8};

        for (int i = 0; i < 15; i++) {
            int digitA = Integer.parseInt(payCode.substring(i, i + 1));
            totalSum += (digitA * mazarebA[i]);
        }

        remain = totalSum % 99;
        strremain = ISOUtil.zeropad(String.valueOf(remain), 2);
        return strremain;
    }


//end of isValiasrAccountAlgorithm


    // Asia Algorithm
    public static boolean isAsiaAlgorithm(String[] payCodes) throws ModelException, ISOException {
        boolean isAsiaChkDgt = true;
        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }
        String payCode = payCodes[0];
        if (payCode.length() > 12) {
            isAsiaChkDgt = false;
            return false;
        }
        if (payCode.length() < 12)
            payCode = ISOUtil.zeropad(payCode, 12);

        if (!StringUtils.isNumeric(payCode)) {
            isAsiaChkDgt = false;
            return false;
        }

        String chkDgt = payCode.substring(payCode.length() - 2);
        if (!chkDgt.equals(String.valueOf(calcCheckAsiaDgt(payCode)))) {
            isAsiaChkDgt = false;
            return false;
        }
        return isAsiaChkDgt;

    }

    public static String calcCheckAsiaDgt(String payCode) throws ISOException {

        String stringRemainZero;
        String stringRemain;
        int totalSum = 0;
        int remainTamin = 0;
        int mazareb[] = {10, 9, 1, 2, 3, 4, 5, 6, 7, 8};

        for (int i = 10; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            totalSum += (digit * mazareb[i - 1]);
        }
        remainTamin = (totalSum % 9);
        stringRemain = String.valueOf(remainTamin);
        stringRemainZero = ISOUtil.zeropad(stringRemain, 2);
        return stringRemainZero;
    }
    //end of Asia Algorithm

    //Check Algorithm
    public static boolean isCheckAlgorithm(String[] payCodes) throws ModelException, ISOException {
        boolean isCheckChkDgt = true;

        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCodes[0];
        if ("".equals(payCode) && !"".equals(payCodes[1])) {
            payCode = payCodes[1];
        }

        if (payCode.length() != 9) {
            isCheckChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isCheckChkDgt = false;
            return false;
        }

        String checkDigit = payCode.substring(payCode.length() - 1);

        if (!checkDigit.equals(String.valueOf(calcCheckChkDgt(payCode)))) {
            isCheckChkDgt = false;
            return false;
        }

        return isCheckChkDgt;

    }

    private static int calcCheckChkDgt(String payCode) throws ISOException {
        int dSum = 0;
        int checkDigit;
        int[] mazarebs = new int[]{23, 19, 17, 13, 11, 7, 5, 3};
        String[] accountNos = {"45074242", "72719476", "82015000", "72721241", "285412048", "401525300", "419527904", "285414199"};


        for (int i = 0; i < 8; i++) {
            int mazrab = mazarebs[i];
            int digit = Integer.parseInt(payCode.substring(i, i + 1));
            dSum += (digit * mazrab);
        }
        checkDigit = accountNos.equals(payCode) ? dSum % 11 : dSum % 10;
        return checkDigit;
    }
    // ond of Check Algorithm

    //Kh Algorithm
    public static Boolean isKhAlgorithm(String[] payCodes) throws ModelException, ISOException {
        boolean isKhChkDgt = true;
        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }
        int totalSum = 0;
        String checkpoint;
        String payCode = payCodes[0];

        if ("".equals(payCode) && !"".equals(payCodes[1])) {
            payCode = payCodes[1];
        }

        if (payCode.length() != 8) {
            isKhChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isKhChkDgt = false;
            return false;
        }

        String chkDgt = payCode.substring(payCode.length() - 1);
        if (!chkDgt.equals(String.valueOf(calcKhChkDgt(payCode)))) {
            return false;
        }
        return isKhChkDgt;
    }

    private static int calcKhChkDgt(String payCode) throws ISOException {
        int totalSum = 0;
        int checkDigit;
        int inputs[] = {1, 2, 3, 4, 5, 6, 7};
        for (int i = 7; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            totalSum += (digit * inputs[i - 1]);
        }
        checkDigit = (totalSum % 11) == 10 ? 0 : totalSum % 11;
        return checkDigit;
    }
    // end of Kh Algorithm

    // Khh Algorithm
    public static boolean isKhhAlgorithm(String[] payCodes) throws ModelException, ISOException {

        boolean isKhhChkDgt = true;

        if ((!payCodes[0].equals("") && !payCodes[1].equals("")) && !payCodes[0].equals(payCodes[1]))
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);

        String payCode = payCodes[0];
        if (payCode.equals("") && !payCodes[1].equals("")) {
            payCode = payCodes[1];
        }

        if (payCode.length() != 12) {
            isKhhChkDgt = false;
            return false;
        }

        if (payCode.length() < 12)
            payCode = ISOUtil.zeropad(payCode, 12);

        if (!StringUtils.isNumeric(payCode)) {
            isKhhChkDgt = false;
            return false;
        }

        String checkDigit = payCode.substring(payCode.length() - 1, payCode.length());

        String totalDSum = String.valueOf(calcKhhChkDgt(payCode));
        totalDSum = totalDSum.substring(totalDSum.length() - 1, totalDSum.length());

        if (!checkDigit.equals(totalDSum)) {
            isKhhChkDgt = false;
            return false;
        }

        return isKhhChkDgt;
    }

    private static int calcKhhChkDgt(String payCode) throws ISOException {
        int dSum = 0;
        for (int i = 11; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            dSum += ((9 - digit) * i);
        }
        return dSum;
    }
    // end of Khh Algorithm

    public static boolean isLizAlgorithm(String[] payIds) throws ModelException, ISOException {
        boolean isLizChkDgt = true;

        if (!"".equals(payIds[0]) && !"".equals(payIds[1]) && !payIds[0].equals(payIds[1]))
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);

        String payId = payIds[0];

        if ("".equals(payId) && !"".equals(payIds[1])) {
            payId = payIds[1];
        }
        payId = ISOUtil.zeropad(payId, 9);

        if (!StringUtils.isNumeric(payId)) {
            isLizChkDgt = false;
            return false;
        }

        if (payId.length() != 9) {
            isLizChkDgt = false;
            return false;
        }

        int chkDg = Integer.parseInt(payId.substring(payId.length() - 1, payId.length()));
        if (calcLizDSum(payId) != chkDg) {
            isLizChkDgt = false;
        }

        return isLizChkDgt;
    }

    public static int calcLizDSum(String payId) throws ISOException {
        int dSum = 0;
        int checkDigit;
        int[] mazarebs = new int[]{8, 7, 6, 5, 4, 3, 2, 1};
        for (int i = 8; i > 0; i--) {
            int digit = Integer.parseInt(payId.substring(i - 1, i));
            dSum = dSum + (digit * mazarebs[i - 1]);
        }
        checkDigit = (dSum % 11);
        return checkDigit;
    }
//    end of  Liz Algorithm

    public static boolean is44Algorithm(String[] payCodes, String accNo, String amount) throws ModelException, ISOException {
        boolean is44ChkDgt = true;

        if ((!payCodes[0].equals("") && !payCodes[1].equals("")) && !payCodes[0].equals(payCodes[1]))
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);

        String payCode = payCodes[0];
        if (payCode.equals("") && !payCodes[1].equals("")) {
            payCode = payCodes[1];
        }

        if (payCode.length() != 19) {
            is44ChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            is44ChkDgt = false;
            return false;
        }

        String checkDigit = payCode.substring(payCode.length() - 2, payCode.length());

        String totalDSum = String.valueOf(calc44ChkDgt(payCode, ISOUtil.zeropad(accNo, 10), ISOUtil.zeropad(amount, 15)));

        if (!checkDigit.equals(totalDSum)) {
            is44ChkDgt = false;
            return false;
        }

        return is44ChkDgt;
    }

    private static String calc44ChkDgt(String payCode, String accNo, String amount) throws ISOException {
        String dSum = "";
        int payCdSum = 0;
        int accNoSum = 0;
        int amntSum = 0;
        int[] PayCdMazarebs = new int[]{3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61};
        int[] accNoMazarebs = new int[]{5, 7, 11, 13, 17, 19, 23, 29, 31, 37};
        int[] amntMazarebs = new int[]{7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61};
        int PayCdMazrab;
        int accNoMazrab;
        int amntMazrab;

        for (int i = 0; i < 17; i++) {
            int digit = Integer.parseInt(payCode.substring(i, i + 1));
            PayCdMazrab = PayCdMazarebs[i];
            payCdSum += (digit * PayCdMazrab);
        }

        for (int j = 0; j < 10; j++) {
            int digit = Integer.parseInt(accNo.substring(j, j + 1));
            accNoMazrab = accNoMazarebs[j];
            accNoSum += (digit * accNoMazrab);
        }

        for (int k = 0; k < 15; k++) {
            int digit = Integer.parseInt(amount.substring(k, k + 1));
            amntMazrab = amntMazarebs[k];
            amntSum += (digit * amntMazrab);
        }

        dSum = String.valueOf((payCdSum + accNoSum + amntSum) % 99);
        dSum = ISOUtil.zeropad(dSum, 2);
        return dSum;
    }

    //    isType31Algorithm-----------------------
    public static boolean isType31Algorithm(String[] payCds) throws ModelException, ISOException {
        boolean isType31Chk = true;

        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() != 8) {
            isType31Chk = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isType31Chk = false;
            return false;
        }

        return isType31Chk;
    }
//    End of isType31Algorithm


    //    isType35Algorithm-----------------------
    public static boolean isType35Algorithm(String[] payCds) throws ModelException, ISOException {
        boolean isType35chk = true;

        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() != 7) {
            isType35chk = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isType35chk = false;
            return false;
        }
        int s1 = 80001;
        int s2 = 800099;

        if (Long.parseLong(payCode) < s1 || Long.parseLong(payCode) > s2) {
            isType35chk = false;
        }

        return isType35chk;
    }

    //    End of isType35Algorithm
    public static boolean isType19Algorithm(String[] payCds) throws ModelException, ISOException {
        boolean isType19 = true;

        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() != 13) {
            isType19 = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isType19 = false;
            return false;
        }
        Long l1 = 110000000000L;
        Long l2 = 159999999999L;

        if (Long.parseLong(payCode.substring(0, (payCode.length() - 1))) < l1 ||
                Long.parseLong(payCode.substring(0, (payCode.length() - 1))) > l2) {
            isType19 = false;
        }

        int chkDg = Integer.parseInt(payCode.substring(payCode.length() - 1, payCode.length()));
        if (calcType19ChkDgt(payCode) != chkDg) {
            isType19 = false;
        }

        return isType19;
    }

    private static int calcType19ChkDgt(String payCode) {
        int dSum = 0;
        int mazrab;
        int checkDigit = 0;
        int[] mazarebs = new int[]{12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
        for (int i = 0; i < 11; i++) {
            mazrab = mazarebs[i];
            int digit = Integer.parseInt(payCode.substring(i, i + 1));
            dSum += (mazrab * digit);
        }
        checkDigit = (dSum % 11) > 8 ? 0 : (dSum % 11);
        return checkDigit;
    }
//    End of Type 19 Algorithm


    //    isType37Algorithm----------------------- 61 in table
    public static boolean isType37Algorithm(String[] payCds) throws ModelException, ISOException {
        boolean is37CheckDigit = true;
        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1]))
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }
        payCode = ISOUtil.zeropad(payCode, 13);

        if (!StringUtils.isNumeric(payCode)) {
            is37CheckDigit = false;
            return false;
        }

        if (payCode.length() != 13) {
            is37CheckDigit = false;
            return false;
        }

        int chkDg = Integer.parseInt(payCode.substring(payCode.length() - 2, payCode.length()));
        if (calc37CkDg(payCode) != chkDg) {
            is37CheckDigit = false;
        }

        return is37CheckDigit;
    }

    public static int calc37CkDg(String payId) {
        int dSum = 0;
        int checkDigit;
        int[] mazarebs = new int[]{0, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        for (int i = 11; i > 0; i--) {
            int digit = Integer.parseInt(payId.substring(i - 1, i));
            dSum = dSum + (digit * mazarebs[i - 1]);
        }
        checkDigit = (dSum % 99);
        return checkDigit;
    }
//    End of isType37Algorithm (//    isType37Algorithm----------------------- 61 in table)

    //    isType23Algorithm-----------------------
    public static boolean isType23Algorithm(String[] payCodes, String transAmnt) throws ISOException {
        boolean is23CheckDigit = true;
        transAmnt = ISOUtil.zeropad(transAmnt, 12);

        String payCode = payCodes[0];
        if ("".equals(payCode) && !"".equals(payCodes[1]))
            payCode = payCodes[1];

        if (payCode.length() != 10) {
            is23CheckDigit = false;
            return false;
        }
        String cqDg = payCode.substring(payCode.length() - 2, payCode.length());
        String pyCde = payCode.substring(0, payCode.length() - 2);
        if (!StringUtils.isNumeric(pyCde)) {
            is23CheckDigit = false;
            return false;
        }
        //Calculate first digit of check digit
        int remain = calc23FirstCkDg(pyCde);
        if (remain == Integer.parseInt(cqDg.substring(0, 1))) {
            //Calculate second digit of check digit
            String st = pyCde + cqDg.substring(0, 1) + transAmnt;
            int sRemain = calc23SecondCkDg(st);
            if (sRemain == Integer.parseInt(cqDg.substring(1, 2)))
                is23CheckDigit = true;
            else
                is23CheckDigit = false;
        } else
            is23CheckDigit = false;


        return is23CheckDigit;
    }

    public static int calc23FirstCkDg(String payCode) { //todo D9
        int dSum = 0;
        int[] mazarebs = new int[]{2, 3, 3, 5, 6, 7, 8, 4};
        for (int i = 8; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            dSum = dSum + (digit * mazarebs[i - 1]);
        }
        return (dSum % 11);
    }

    public static int calc23SecondCkDg(String payCode) { //todo D11
        int dSum = 0;
        int[] mazarebs = new int[]{3, 4, 5, 6, 7, 3, 4, 5, 6, 7, 3, 4, 5, 5, 7, 3, 4, 5, 6, 7, 8};
        for (int i = 21; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            dSum = dSum + (digit * mazarebs[i - 1]);
        }
        return (dSum % 11);
    }

    // 11 Algorithm
    public static boolean is11Algorithm(String[] payCodes) throws ModelException, ISOException {
        boolean isMoud11ChkDgt = true;

        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCodes[0];

        if (payCode.length() > 10) {
            isMoud11ChkDgt = false;
            return false;
        }
        if (payCode.length() < 10) {
            payCode = ISOUtil.zeropad(payCode, 10);
        }

        if (!StringUtils.isNumeric(payCode)) {
            isMoud11ChkDgt = false;
            return false;
        }
        String checkDigit = payCode.substring(payCode.length() - 1);
        String totalChkDgt = String.valueOf(calc11ChkDgt(payCode));
        if (!checkDigit.equals(totalChkDgt)) {
            isMoud11ChkDgt = false;
            return false;
        }
        return isMoud11ChkDgt;

    }

    public static int calc11ChkDgt(String payCode) throws ISOException {
        int totalSum = 0;
        int checkDigit = 0;
        int mazareb3[] = {4, 3, 2, 7, 6, 5, 4, 3, 2};

        for (int i = 0; i < 9; i++) {
            int digit = Integer.parseInt(payCode.substring(i, i + 1));
            totalSum += (digit * mazareb3[i]);
        }
        checkDigit = totalSum % 11;
        if (!(checkDigit == 0))
            checkDigit = 11 - checkDigit;
        return checkDigit;

    }
    //end of 11 Algorithm

    // Tamin Algorithm
    public static boolean isTaminAlgorithm(String[] payCodes) throws ModelException, ISOException {
        boolean isTaminChkDgt = true;

        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCodes[0];
        if (payCode.length() > 17) {
            isTaminChkDgt = false;
            return false;
        }

        String checkDigit = payCode.substring(payCode.length() - 2);
        String totalChkDgt = String.valueOf(calcTaminChkDgt(payCode));
        if (!checkDigit.equals(totalChkDgt))
            isTaminChkDgt = false;


        return isTaminChkDgt;

    }

    private static int calcTaminChkDgt(String payCode) throws ISOException {
        int dSum = 0;

        if (!StringUtils.isNumeric(payCode)) {
            if (payCode.length() < 17) {
                payCode = ISOUtil.zeropad(payCode, 17);
            }
            String paycode12 = payCode.substring(11, 12);
            String term12 = null;
            if (paycode12.equals("a") || paycode12.equals("A"))
                term12 = "10";
            else if (paycode12.equals("b") || paycode12.equals("B"))
                term12 = "11";
            else if (paycode12.equals("c") || paycode12.equals("C"))
                term12 = "12";
            else if (paycode12.equals("d") || paycode12.equals("D"))
                term12 = "13";
            else if (paycode12.equals("e") || paycode12.equals("E"))
                term12 = "14";
            else if (paycode12.equals("f") || paycode12.equals("F"))
                term12 = "15";
            else if (paycode12.equals("f") || paycode12.equals("G"))
                term12 = "16";

            payCode = payCode.substring(0, 11) + String.valueOf(term12) + payCode.substring(13);
        }

        int[] mazarebs = new int[]{15, 14, 13, 12, 11, 10, 9, 1, 2, 3, 4, 5, 6, 7, 8};
        for (int i = 15; i > 0; i--) {
            int digit = Integer.parseInt(payCode.substring(i - 1, i));
            int mazrab = mazarebs[i - 1];
            dSum += (digit * mazrab);
        }
        int checkDigit = dSum % 99;
        return checkDigit;
    }
    // end of Tamin Algorithm


    // BankCardValidation Algorithm
    public static boolean isBankCardValid(String[] payCodes) throws ModelException, ISOException {
        boolean isBankCardChkDgt = true;

        if (!"".equals(payCodes[0]) && !"".equals(payCodes[1]) && !payCodes[0].equals(payCodes[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCodes[0];

        if (payCode.length() != 16) {
            isBankCardChkDgt = false;
            return false;
        }

        if (!StringUtils.isNumeric(payCode)) {
            isBankCardChkDgt = false;
            return false;
        }
        String totalChkDgt = String.valueOf(calcValidationCardBankChkDgt(payCode));
        if (!totalChkDgt.equals("0")){
            isBankCardChkDgt = false;
            return false;
        }
        return isBankCardChkDgt;

    }

    public static int calcValidationCardBankChkDgt(String payCode) throws ISOException {
        int totalSum = 0;
        int sum = 0;
        int checkDigit = 0;
        int mazareb[] = {2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1};

        for (int i = 0; i < 16; i++) {
            int digit = Integer.parseInt(payCode.substring(i, i + 1));
            sum = digit * mazareb[i];
            if(sum > 9){
                sum -= 9;
            }
            totalSum += sum;
        }
        checkDigit = totalSum % 10;
        return checkDigit;

    }
    //end of BankCardValidation Algorithm

    // DebtCode Algorithm
    public static boolean isDebtCodeAlgorithm(String[] payCds) throws ModelException, ISOException {
        boolean isDebtCodeChkDgt = true;
        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() > 17) {
            isDebtCodeChkDgt = false;
            return false;
        }

        if (payCode.length() < 17)
            payCode = ISOUtil.zeropad(payCode, 17);

        if (!StringUtils.isNumeric(payCode)) {
            isDebtCodeChkDgt = false;
            return false;
        }

        int chkDgt = Integer.parseInt(payCode.substring(payCode.length() - 2, payCode.length()));

        if (calcDebtCode(payCode) != chkDgt) {
            isDebtCodeChkDgt = false;
        }

        return isDebtCodeChkDgt;

    }

    private static int calcDebtCode(String payCode) {
        int dSum = 0;
        int checkDigit;
        int[] mazarebs = new int[]{15, 14, 13, 12, 11, 10, 9, 1, 2, 3, 4, 5, 6, 7, 8, 0, 0};
        for (int i = 0; i < 17; i++) {
            int digit = Integer.parseInt(payCode.substring(i, i + 1));
            dSum += (digit * mazarebs[i]);
        }
        checkDigit = (dSum % 99);
        return checkDigit;
    }
    //end of DebtCode Algorithm

    // General Algorithm
    public static boolean isGeneralAlgorithm(String[] payCds) throws ModelException, ISOException {
        boolean isGeneralChkDgt = true;
        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
        }

        String payCode = payCds[0];
        if ("".equals(payCode) && !"".equals(payCds[1])) {
            payCode = payCds[1];
        }

        if (payCode.length() != 17) {
            isGeneralChkDgt = false;
            return false;
        }

        return isGeneralChkDgt;
    }


//    // Emirate Flight Algorithm
//    public static boolean isType22Algorithm(String[] payCds,String accountNo) throws ModelException, ISOException {
//        boolean isEmirateChkDgt = true;
//        if (!"".equals(payCds[0]) && !"".equals(payCds[1]) && !payCds[0].equals(payCds[1])) {
//            throw new ModelException(Constants.INVALID_NUMBER_OF_PAYCODES);
//        }
//
//        String payCode = payCds[0];
//        if ("".equals(payCode) && !"".equals(payCds[1])) {
//            payCode = payCds[1];
//        }
//
//        if (payCode.length() != 8) {
//            isEmirateChkDgt = false;
//            return false;
//        }
//
//        if (!StringUtils.isNumeric(payCode)) {
//            isEmirateChkDgt = false;
//            return false;
//        }
//        int pyCode = Integer.valueOf(payCode);
//
//        try {
//            Vector checkDigits = ChannelFacadeNew.getEmirateValidID(pyCode);
//            Iterator it = checkDigits.iterator();
//            while (it.hasNext()) {
//                Cmparam param = (Cmparam) it.next();
//                if (param.getId().toString().equals(payCode)) {
//                    String description = param.getDescription().trim();
//                    isEmirateChkDgt = true;
//                    return isEmirateChkDgt;
//                } else
//                    isEmirateChkDgt = false;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (NotFoundException e) {
//            e.printStackTrace();
//        }
//
//        return isEmirateChkDgt;
//
//    }



}
