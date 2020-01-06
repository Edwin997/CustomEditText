package com.example.customedittext.validator;

import java.util.regex.Pattern;

public class ValidatorEmail implements IValidator  {
    @Override
    public boolean validateText(String p_strText) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(p_strText).matches();
    }

    @Override
    public String getErrorMessage() {
        return "%d tidak valid";
    }
}
