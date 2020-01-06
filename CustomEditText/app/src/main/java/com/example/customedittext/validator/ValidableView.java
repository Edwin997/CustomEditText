package com.example.customedittext.validator;

public interface ValidableView {
    void addValidator(IValidator p_validator);
    boolean checkValidator();
    boolean checkValidatorWithoutErrorMessage();
}
