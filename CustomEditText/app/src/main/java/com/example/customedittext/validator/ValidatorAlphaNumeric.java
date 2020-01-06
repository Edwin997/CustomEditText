package com.example.customedittext.validator;

import com.utilities.Utilities;

public class ValidatorAlphaNumeric implements IValidator {
    @Override
    public boolean validateText(String p_strText) {
        return Utilities.checkContainLetterAndNumber(p_strText);
    }

    @Override
    public String getErrorMessage() {
        return "Format %d tidak sesuai";
    }
}
