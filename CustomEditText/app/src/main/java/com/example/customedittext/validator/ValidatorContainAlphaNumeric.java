package com.example.customedittext.validator;

public class ValidatorContainAlphaNumeric implements IValidator {
    @Override
    public boolean validateText(String p_strText) {
        boolean isContainDigit = false;
        boolean isContainletter = false;
        for (int tmpI = 0; tmpI < p_strText.length(); tmpI++) {
            char tmpC = p_strText.charAt(tmpI);
            if (Character.isDigit(tmpC))
                isContainDigit = true;
            else if (Character.isLetter(tmpC))
                isContainletter =true;
        }

        return (isContainDigit && isContainletter);
    }

    @Override
    public String getErrorMessage() {
        return "Format %d harus kombinasi huruf dan angka";
    }
}
