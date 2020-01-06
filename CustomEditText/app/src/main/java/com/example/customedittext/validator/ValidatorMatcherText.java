package com.example.customedittext.validator;

import android.widget.EditText;

import com.example.customedittext.CustomEditText;

/**
 * Created by u063490 on 4/23/2018.
 */

public class ValidatorMatcherText implements IValidator {
    EditText editText;
    CustomEditText customEditText;
    public ValidatorMatcherText(EditText editText) {
        this.editText = editText;
    }
    public ValidatorMatcherText(CustomEditText customEditText) {
        this.customEditText = customEditText;
    }

    @Override
    public boolean validateText(String p_strText) {

        if(editText != null) {
            if (!p_strText.equals(editText.getText().toString())) {
                return false;
            }
        }
        else if(customEditText != null){
            if (!p_strText.equals(customEditText.getText())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String getErrorMessage() {
        return "%d yang Anda masukkan tidak sesuai";
    }
}
