package com.example.customedittext.validator;

/**
 * Created by u063490 on 4/23/2018.
 */

public class ValidatorDigitOnly implements IValidator {
    
    @Override
    public boolean validateText(String p_strText) {
        if(p_strText.matches(".*[a-zA-Z]+.*"))
            return false;
        return true;
    }

    @Override
    public String getErrorMessage() {
        return "Format %d tidak sesuai";
    }
}
