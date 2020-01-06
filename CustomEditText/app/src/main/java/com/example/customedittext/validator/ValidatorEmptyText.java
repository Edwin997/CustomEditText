package com.example.customedittext.validator;

/**
 * Created by u063490 on 4/23/2018.
 */

public class ValidatorEmptyText implements IValidator {

    @Override
    public boolean validateText(String p_strText) {
        if(p_strText.trim().equals(""))
            return false;
        return true;
    }

    @Override
    public String getErrorMessage() {
        return "%d tidak boleh kosong";
    }
}
