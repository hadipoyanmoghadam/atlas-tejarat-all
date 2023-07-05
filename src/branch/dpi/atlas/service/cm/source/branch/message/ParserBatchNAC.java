package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;


/**
 * User:R.Nasiri
 * Date: April 04, 2015
 * Time: 03:43 PM
 */
public class ParserBatchNAC extends BranchMessage {

    public ParserBatchNAC(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.BATCH_NAC_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }


    protected void unpackBody() {
        int range = 0;
        requestType = txString.substring(headerLength, range = headerLength + REQUEST_TYPE);
        accountType = txString.substring(range, range += ACCOUNT_TYPE);
        ownerIndex = txString.substring(range, range += OWNER_INDEX);
        accountGroup = txString.substring(range, range += ACCOUNT_GROUP);
        statementType = txString.substring(range, range += STATEMENT_TYPE);
        createDate = txString.substring(range, range += CREATE_DATE);
        changeDate = txString.substring(range, range += CHANGE_DATE);
        firstName = txString.substring(range, range += FIRST_NAME);
        lastName = txString.substring(range, range += LAST_NAME);
        fatherName = txString.substring(range, range += FATHER_NAME);
        gender = txString.substring(range, range += GENDER);
        nationalCode = txString.substring(range, range += NATIONAL_CODE);
        birthDate = txString.substring(range, range += BIRTH_DATE);
        ID_Number = txString.substring(range, range += ID_NUMBER);
        ID_SerialNumber = txString.substring(range, range += ID_SERIAL_NUMBER);
        ID_Series = txString.substring(range, range += ID_SERIES);
        ID_IssueDate = txString.substring(range, range += ID_ISSUE_DATE);
        ID_IssueCode = txString.substring(range, range += ID_ISSUE_CODE);
        ID_IssuePlace = txString.substring(range, range += ID_ISSUE_PLACE);
        en_FirstName = txString.substring(range, range += EN_FIRST_NAME);
        en_LastName = txString.substring(range, range += EN_LAST_NAME);
        ext_IdNumber = txString.substring(range, range += EXT_ID_NUMBER);
        if (Long.parseLong(ext_IdNumber) == 0)
            ext_IdNumber = "";
        foreignCountryCode = txString.substring(range, range += FOREIGN_COUNTRY_CODE);
        if (Long.parseLong(foreignCountryCode) == 0)
            foreignCountryCode = "";
        tel_Number1 = txString.substring(range, range += TEL_NO1);
        tel_Number2 = txString.substring(range, range += TEL_NO2);
        if (Long.parseLong(tel_Number2) == 0)
            tel_Number2 = "";
        mob_Number = txString.substring(range, range += MOBILE_NO);
        if (Long.parseLong(mob_Number) == 0)
            mob_Number = "";
        fax_Number = txString.substring(range, range += FAX_NO);
        if (Long.parseLong(fax_Number) == 0)
            fax_Number = "";
        address1 = txString.substring(range, range += ADDRESS1);
        address2 = txString.substring(range, range += ADDRESS2);
        postalCode = txString.substring(range, range += POSTAL_CODE);
        if (Long.parseLong(postalCode) == 0)
            postalCode = "";
        currencyCode = txString.substring(range, range += CURRENCY_CODE);
        nationalCodeValid = txString.substring(range, range += NATIONAL_CODE_VALID);
        emailAddress = txString.substring(range, range += E_MAIL);
        withdrawType = txString.substring(range, range +=WITHDRAW_TYPE);
        accountOpenerName = txString.substring(range,range += ACCOUNT_OPENER_NAME);
    }
}
