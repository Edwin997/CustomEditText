package com.example.customedittext.validator;

import android.widget.EditText;

/**
 * Created by u063490 on 4/23/2018.
 */

public class ValidatorMatcherText implements IValidator {
    EditText editText;
    public ValidatorMatcherText(EditText editText) {
        this.editText = editText;
    }

    @Override
    public boolean validateText(String p_strText) {
        if(!p_strText.equals(editText.getText().toString()))
            return false;
        return true;
    }

    @Override
    public String getErrorMessage() {
        return "%d yang Anda masukkan tidak sesuai";
    }
}
