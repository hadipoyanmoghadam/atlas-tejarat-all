package branch.dpi.atlas.model.tj.entity;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import branch.dpi.atlas.util.ImmediateCardUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz
 * Date: Feb 6, 2012
 * Time: 9:54:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class CFDCustomerInfo {

    String sex = null;
    String family_persian = "";
    String name_latin = "";
    String family_latin = "";
    String name_persian = "";
    String address = "";
    String phoneNumber = "";
    String nationalCode = "";
    String IDNumber = "";
    String birthDate = "";

    public CFDCustomerInfo() {
    }

    public String toString() {
        return " sex=" + sex + " family_persian=" + family_persian + " name_latin=" + name_latin + " family_latin=" + family_latin +
                " phoneNumber=" + phoneNumber + " nationalCode=" + nationalCode + " IDNumber=" + IDNumber + " birthDate=" + birthDate +
                " name_persian=" + name_persian + " address=" + address;

    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {

        if (ImmediateCardUtil.validateElement(sex, 0)) {
            sex = "0";
        } else if (!ImmediateCardUtil.validateElement(sex, 1)) {
            sex = "1";
        }
        this.sex = sex;
    }

    public void setNameFamilyLatin(String name_family_latin) throws Exception {
        try {
            if (name_family_latin.length() != 0) {
                if (name_family_latin.indexOf(".MR", 0) != -1) {
                    this.name_latin = name_family_latin.
                            substring(name_family_latin.indexOf("/", 0) + 1, name_family_latin.indexOf(".MR", 0));
                    this.family_latin = name_family_latin.substring(0, name_family_latin.indexOf("/", 0));
                } else {
                    this.name_latin = name_family_latin.
                            substring(name_family_latin.indexOf("/", 0) + 1, name_family_latin.length());
                    this.family_latin = name_family_latin.substring(0, name_family_latin.indexOf("/", 0));
                }
                if (!ImmediateCardUtil.validateElementLessAndEqual(this.name_latin,BranchMessage.EN_FIRST_NAME) || !ImmediateCardUtil.validateElementLessAndEqual(this.family_latin,BranchMessage.EN_LAST_NAME )) {
                    throw new Exception();
                }
            } else {
                this.name_latin = "";
                this.name_latin = "";
            }
        } catch (Exception e) {
            throw new Exception("NAME_FAMILY_LATIN : " + name_family_latin + " in XML File has Error in lenght or other things.");
        }
    }

    public String getName_latin() {
        return name_latin;
    }

    public String getFamily_latin() {
        return family_latin;
    }

    public String getName_persian() {
        return name_persian;
    }

    public void setName_persian(String name_persian) throws Exception {
        try {
            if (!ImmediateCardUtil.validateElementLessAndEqual(name_persian, BranchMessage.FIRST_NAME))
                throw new Exception();
            else
                this.name_persian = name_persian;

        } catch (Exception e) {
            throw new Exception("NAME_PERSIAN : " + name_persian + " in XML File has Error in lenght or other things.");
        }
    }

    public String getFamily_persian() {
        return family_persian;
    }

    public void setFamily_persian(String family_persian) throws Exception {
        try {
            if (!ImmediateCardUtil.validateElementLessAndEqual(family_persian, BranchMessage.LAST_NAME))
                throw new Exception();
            else
                this.family_persian = family_persian;
        } catch (Exception e) {
            throw new Exception("FAMILY_PERSIAN : " + family_persian + " in XML File has Error in lenght or other things.");
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) throws Exception {
        try {
            if (!ImmediateCardUtil.validateElementLessAndEqual(address, BranchMessage.ADDRESS1))
                throw new Exception();
            else
                this.address = address;
        } catch (Exception e) {
            throw new Exception("ADDRESS : " + address.substring(0, 10) + " in XML File has Error in lenght or other things.");
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) throws Exception {
        try {
            if (ImmediateCardUtil.validateElementLessAndEqual(phoneNumber, BranchMessage.TEL_NO1))
                this.phoneNumber = phoneNumber;
            else
                throw new Exception();

        } catch (Exception e) {
            throw new Exception("phoneNumber : " + phoneNumber + " in XML File has Error in lenght or other things.");
        }
    }

    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) throws Exception {

        try {
            if (ImmediateCardUtil.validateElementLessAndEqual(nationalCode, BranchMessage.NATIONAL_CODE))
                this.nationalCode = nationalCode;
            else
                throw new Exception();

        } catch (Exception e) {
            throw new Exception("nationalCode : " + nationalCode + " in XML File has Error in lenght or other things.");
        }

    }

    public String getIDNumber() {
        return IDNumber;
    }

    public void setIDNumber(String IDNumber) throws Exception {
        try {
            if (ImmediateCardUtil.validateElementLessAndEqual(IDNumber, BranchMessage.ID_NUMBER))
                this.IDNumber = IDNumber;
            else
                throw new Exception();

        } catch (Exception e) {
            throw new Exception("IDNumber : " + IDNumber + " in XML File has Error in lenght or other things.");
        }

    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) throws Exception {
        try {
            if (ImmediateCardUtil.validateElementLessAndEqual(birthDate, BranchMessage.BIRTH_DATE))
                this.birthDate = birthDate;
            else
                throw new Exception();

        } catch (Exception e) {
            throw new Exception("birthDate : " + birthDate + " in XML File has Error in lenght or other things.");
        }
    }
}
