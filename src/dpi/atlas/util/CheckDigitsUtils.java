package dpi.atlas.util;

import dpi.atlas.model.ModelException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.validator.routines.checkdigit.CheckDigitException;
import org.apache.commons.validator.routines.checkdigit.VerhoeffCheckDigit;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.ISOException;

/**
 * Created by IntelliJ IDEA.
 * User: Sahar Amjadi
 * Date: Oct 5, 2011
 * Time: 10:31:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class CheckDigitsUtils {
    public static boolean isCheckDigit(String accNo, String[] payCds, String transAmnt)
            throws ISOException, SQLException,ModelException {
        boolean isCheckDigit = false;
        String algNo = ChannelFacadeNew.getAlgorithmNo(accNo);
        if (!"".equals(algNo)) {
            int ialgNo = Integer.parseInt(algNo.trim());
            switch (ialgNo) {
                case 1:
                    isCheckDigit = CheckDigitsUtils.taminEjtemaeeAlg1(payCds, transAmnt);
                    break;
                case 2:
                    isCheckDigit = CheckDigitsUtils.emdadCommiteeAlg1(payCds, transAmnt, accNo);
                    break;
                case 3:
                    isCheckDigit = CheckDigitsUtils.ekramEetamAlg(payCds);
                    break;
                case 4:
                    isCheckDigit = CheckDigitsUtils.irancellAlgrthm(payCds);
                    break;
                case 5:
                    isCheckDigit = CheckDigitsUtils.isDanaInsurance(payCds);
                    break;
                case 6:
                    isCheckDigit = CheckDigitsUtils.isStudntRefahFund(payCds);
                    break;
                case 7:
                    isCheckDigit = CheckDigitsUtils.isIranInsurance(payCds, transAmnt);
                    break;
                case 8:
                    isCheckDigit = CheckDigitsUtils.isParsianInsurance(payCds);
                    break;
                case 9:
                    isCheckDigit = CheckDigitsUtils.isQomSeminary(payCds);
                    break;
                case 10:
                    isCheckDigit = CheckDigitsUtils.isMAMKerman(payCds);
                    break;
                case 11:
                    isCheckDigit = CheckDigitsUtils.isEmdadCommiteeAlg2(payCds, accNo);
                    break;
                case 12:
                    isCheckDigit = CheckDigitsUtils.isZamyadCoAlgorithm(payCds, accNo, transAmnt);
                    break;
                case 13:
                    isCheckDigit = CheckDigitsUtils.isMashhadUniAlgorithm(payCds);
                    break;
                case 14:
                    isCheckDigit = CheckDigitsUtils.isType1Algorithm(payCds, accNo, transAmnt);
                    break;
                case 15:
                    isCheckDigit = CheckDigitsUtils.isType2Algorithm(payCds);
                    break;
                case 16:
                    isCheckDigit = CheckDigitsUtils.isType3Algorithm(payCds);
                    break;
                case 17:
                    isCheckDigit = CheckDigitsUtils.isType4Algorithm(payCds);
                    break;
                case 18:
                    isCheckDigit = CheckDigitsUtils.isType5Algorithm(payCds);
                    break;
                case 19:
                    isCheckDigit = CheckDigitsUtils.isType6Algorithm(payCds);
                    break;
                case 20:
                    isCheckDigit = CheckDigitsUtils.isType7Algorithm(payCds);
                    break;
                case 21:
                    isCheckDigit = CheckDigitsUtils.isType8Algorithm(payCds);
                    break;
                case 22:
                    isCheckDigit = CheckDigitsUtils.isIranLeasingAlgorithm(payCds);
                    break;
                case 23:
                    isCheckDigit = CheckDigitsUtils.isNationalCodeAlgorithm(payCds);
                    break;
                case 24:
                    isCheckDigit = CheckDigitsUtils.isLifeAndSavingAlgorithm(payCds);
                    break;
                case 25:
                    isCheckDigit = CheckDigitsUtils.isSajadAlgorithm(payCds);
                    break;
                case 26:
                    isCheckDigit = CheckDigitsUtils.isMahanAlgorithm(payCds);
                    break;
                case 27:
                    isCheckDigit = CheckDigitsUtils.isIranInsuranceNewAlgorithm(payCds);
                    break;
                case 28:
                    isCheckDigit = CheckDigitsUtils.isSaipaAlgorithm(payCds, accNo, transAmnt);
                    break;
                case 29:
                    isCheckDigit = CheckDigitsUtils.isLeasingAlgorithm(payCds, accNo, transAmnt);
                    break;
                case 30:
                    isCheckDigit = CheckDigitsUtils.isEight1Algorithm(payCds);
                    break;

                case 31:
                    isCheckDigit = CheckDigitsUtils.isPeymanrhAlgorithm(payCds);
                    break;
                case 32:
                    isCheckDigit = CheckDigitsUtils.isEmamZamanAlgorithm(payCds);
                    break;

                case 33:
                    isCheckDigit = CheckDigitsUtils.isElmiKarbordiArakAlgorithm(payCds);
                    break;
                case 34:
                    isCheckDigit = CheckDigitsUtils.isOloomTahghighatAlgorithm(payCds);
                    break;
                case 35:
                    isCheckDigit = CheckDigitsUtils.isbourseAlgorithm(payCds);
                    break;
                case 36:
                    isCheckDigit = CheckDigitsUtils.is4and14Algorithm(payCds);
                    break;
                case 37:
                    isCheckDigit = CheckDigitsUtils.is13Algorithm(payCds);
                    break;
                case 38:
                    isCheckDigit = CheckDigitsUtils.is17Algorithm(payCds);
                    break;
            }
        } else
            throw new ModelException(Constants.NO_RELATED_ALG_EXIST);

        return isCheckDigit;
    }

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

    private static boolean isDanaInsurance(String[] payCds) throws ModelException, ISOException {
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

    private static boolean isParsianInsurance(String[] payCds) throws ModelException, ISOException {
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

    private static boolean isIranLeasingAlgorithm(String[] payCodes) throws ModelException {
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



    private static String makeMotamam(String s) {
        String motamam = "";

        for (int i = 0; i < 13; i++) {
            motamam = motamam + String.valueOf((9 - Integer.parseInt(s.substring(i, i + 1))));
        }
        return motamam;
    }

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
        if (!chkDgt.equals(String.valueOf(calcPeymanehChkDgt(payCode)))) {
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

        if (payCode.length() != 8) {
            is13ChkDgt = false;
            return false;
        }

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

    // Verhoef  algorithm
    public static boolean isValidVerhoef(String trxID, String accountNumber, String amount, String code) throws CheckDigitException {
        if (trxID.length() != 13 || code.length() != 15 || !NumberUtils.isDigits(code) || !NumberUtils.isDigits(accountNumber.trim()) || !NumberUtils.isDigits(trxID)) {
            return false;
        }
        String calculated = calculateVerhoef(trxID, accountNumber.trim(), amount);
        return calculated.equals(code);
    }
    // specialVerhoef  algorithm
    public static boolean isValidVerhoefSpecial(String trxID, String accountNumber, String amount, String code) throws CheckDigitException, ISOException {
        if (trxID.length() != 13 || code.length() != 15 || !NumberUtils.isDigits(code) || !NumberUtils.isDigits(accountNumber.trim()) || !NumberUtils.isDigits(trxID)) {
            return false;
        }
        accountNumber=ISOUtil.zeropad(accountNumber.trim(),16);
        String calculated = calculateVerhoef(trxID, accountNumber.trim(), amount);
        return calculated.equals(code);
    }

    public static String calculateVerhoef(String trxID, String accountNumber, String amount) throws CheckDigitException {

        if (trxID.length() != 13 || !NumberUtils.isDigits(amount) || !NumberUtils.isDigits(accountNumber) || !NumberUtils.isDigits(trxID)) {
            return "";

        }
        amount = org.apache.commons.lang3.StringUtils.repeat("0", 15 - amount.length()) + amount;
        String trxIDTemp = org.apache.commons.lang3.StringUtils.repeat("0", 2) + trxID;
        String n1 = VerhoeffCheckDigit.VERHOEFF_CHECK_DIGIT.calculate(trxIDTemp + accountNumber.trim() + amount);
        String n2 = VerhoeffCheckDigit.VERHOEFF_CHECK_DIGIT.calculate(org.apache.commons.lang3.StringUtils.reverse(trxIDTemp) + org.apache.commons.lang3.StringUtils.reverse(accountNumber.trim()) + org.apache.commons.lang3.StringUtils.reverse(amount));
        return trxID + n1 + n2;
    }

    // ExpensVerhoef  algorithm
    public static boolean isValidExpenseVerhoef(String trxID, String accountNumber, String amount, String code, String chequeNumber, String dueDate) throws CheckDigitException {

        if (trxID.length() != 13 || code.length() != 15 || dueDate.length() != 8 || !NumberUtils.isDigits(code) || !NumberUtils.isDigits(amount) || !NumberUtils.isDigits(accountNumber) || !NumberUtils.isDigits(trxID)) {
            return false;
        }
        String calculated = calculateExpenseVerhoef(trxID, accountNumber, amount, chequeNumber, dueDate);
        return calculated.equals(code);
    }


    public static String calculateExpenseVerhoef(String trxID, String accountNumber, String amount, String chequeNumber, String dueDate) throws CheckDigitException {

        if (trxID.length() != 13 || dueDate.length() != 8 || !NumberUtils.isDigits(amount) || !NumberUtils.isDigits(accountNumber) || !NumberUtils.isDigits(trxID)) {
            return "";
        }
        amount = org.apache.commons.lang3.StringUtils.repeat("0", 15 - amount.length()) + amount;
        String trxIDTemp = org.apache.commons.lang3.StringUtils.repeat("0", 2) + trxID;
        String n1 = VerhoeffCheckDigit.VERHOEFF_CHECK_DIGIT.calculate(trxIDTemp + accountNumber + amount + chequeNumber + dueDate);
        String n2 = VerhoeffCheckDigit.VERHOEFF_CHECK_DIGIT.calculate(org.apache.commons.lang3.StringUtils.reverse(trxIDTemp) + org.apache.commons.lang3.StringUtils.reverse(accountNumber) + org.apache.commons.lang3.StringUtils.reverse(amount) + org.apache.commons.lang3.StringUtils.reverse(chequeNumber) + org.apache.commons.lang3.StringUtils.reverse(dueDate));
        return trxID + n1 + n2;

    }

    public static boolean isMatchedByPattern(String[] payCds, List patterns) throws ModelException, ISOException {
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


}
