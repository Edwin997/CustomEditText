package com.example.customedittext.validator;

import android.util.Log;

import com.example.customedittext.utilities.Utilities;

public class ValidatorCreateCodeAccess implements IValidator {
    @Override
    public boolean validateText(String p_strText) {
        return Utilities.checkContainLetterAndNumber(p_strText) && p_strText.length() == 6;
    }

    @Override
    public String getErrorMessage() {
        return "%d harus 6 karakter huruf dan angka";
    }
}
