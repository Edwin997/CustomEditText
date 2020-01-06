package com.example.customedittext.validator;

/**
 * Created by u063490 on 4/23/2018.
 */

public interface IValidator {
    boolean validateText(String p_strText);
    String getErrorMessage();
}
