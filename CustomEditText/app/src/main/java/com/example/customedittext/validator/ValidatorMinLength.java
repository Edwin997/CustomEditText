package com.example.customedittext.validator;

import android.content.Context;
import android.util.Log;

import com.example.customedittext.R;

/**
 * Created by u063490 on 4/23/2018.
 */

public class ValidatorMinLength implements IValidator {
    int minLength = 0;
    String minLengthType;
    Context context;
    public ValidatorMinLength(Context context, int minLength, String minLengthType) {
        this.context = context;
        this.minLength = minLength;
        this.minLengthType = minLengthType;
    }

    @Override
    public boolean validateText(String p_strText) {
        if(p_strText.length() != minLength)
            return false;
        return true;
    }

    @Override
    public String getErrorMessage() {
        if(minLengthType.equals(context.getResources().getString(R.string.ValidatorMinLengthTypeAlphabet))){
            return "%d harus "+minLength+" karakter";
        }else if(minLengthType.equals(context.getResources().getString(R.string.ValidatorMinLengthTypeDigit))){
            return "%d harus "+minLength+" digit";
        }else{
            return "%d harus "+minLength+" digit / karakter";
        }

    }
}
