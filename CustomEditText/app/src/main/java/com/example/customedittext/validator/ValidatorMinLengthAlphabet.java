package com.example.customedittext.validator;

/**
 * Created by u063490 on 4/23/2018.
 */

public class ValidatorMinLengthAlphabet implements IValidator {
    int minLength = 0;
    public ValidatorMinLengthAlphabet(int minLength) {
        this.minLength = minLength;
    }

    @Override
    public boolean validateText(String p_strText) {
        if(p_strText.length() != minLength)
            return false;
        return true;
    }

    @Override
    public String getErrorMessage() {
        return "%d harus "+minLength+" karakter";
    }
}
