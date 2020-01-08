package com.example.customedittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.example.customedittext.validator.IValidator;
import com.example.customedittext.validator.ValidableView;
import com.example.customedittext.validator.ValidatorEmptyText;
import com.example.customedittext.validator.ValidatorMinLength;

import java.util.ArrayList;

public class CustomEditText extends LinearLayout implements ValidableView {

    //Data Member Layout
    private LinearLayout g_layouts;
    private EditText g_edittext;
    private TextView g_tv_Subhint;
    private TextView g_tv_Hint;
    private TextView g_tv_Error;

    private Context g_context;
    private boolean isNeedError = true;
    private boolean isNeedJudul = true;
    private boolean g_hasFocus = false;
    private ArrayList<IValidator> g_validatorArrayList;
    private int g_layout_type = 0;
    private String g_validatorType = getResources().getString(R.string.ValidatorMinLengthTypeAlphabet);;

    //Style Data Member
    @StyleRes int resIdHintWithError = 0;
    @StyleRes int resIdHintWithoutError = 0;
    @StyleRes int resIdError = 0;
    @StyleRes int resIdSubHint = 0;

    public CustomEditText(Context context){
        super(context);
        init(context);
    }

    public CustomEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    //region METHOD Initialize and Set Attributes
    private void initLayout(Context context){
        g_context = context;
        View l_view = LayoutInflater.from(g_context).inflate(R.layout.layout_custom_edittext, this);
        g_layouts = l_view.findViewById(R.id.layout_custom_edittext);
        g_edittext = l_view.findViewById(R.id.et_custom_edittext);

        g_tv_Hint = new TextView(g_context);
        g_tv_Hint.setTag("judul");

        g_tv_Subhint = new TextView(g_context);
        g_tv_Subhint.setTag("subhint");

        g_tv_Error = new TextView(g_context);
        g_tv_Error.setTag("error");
    }

    private void init(Context context){
        initLayout(context);
        g_edittext.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        g_edittext.setOnFocusChangeListener(new CustomFocus(g_edittext));
        g_edittext.addTextChangedListener(new CustomTextWatcher(g_edittext));
        g_edittext.setMaxLines(2);
        g_validatorArrayList = new ArrayList<>();
    }

    private void init(Context context, AttributeSet attrs){
        initLayout(context);
        g_validatorArrayList = new ArrayList<>();
        g_edittext.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        g_edittext.setOnFocusChangeListener(new CustomFocus(g_edittext));
        g_edittext.addTextChangedListener(new CustomTextWatcher(g_edittext));
        g_edittext.setMaxLines(2);
        setAttributes(attrs);
        if(!g_tv_Subhint.getText().toString().trim().isEmpty()){
            addSubHint();
        }

    }

    private void setAttributes(AttributeSet attrs){
        TypedArray arrayStyledAttributes = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CustomEditText,
                0, 0);

        try{
            String hint = arrayStyledAttributes.getString(R.styleable.CustomEditText_hint);
            if(!TextUtils.isEmpty(hint)){
                setHint(hint);
            }

            String subHint = arrayStyledAttributes.getString(R.styleable.CustomEditText_subHint);
            if(!TextUtils.isEmpty(subHint)){
                setSubHint(subHint);
            }

            String error = arrayStyledAttributes.getString(R.styleable.CustomEditText_error);
            if(!TextUtils.isEmpty(error)){
                setError(error);
            }

            int size = arrayStyledAttributes.getInteger(R.styleable.CustomEditText_size, -1);
            if(size != -1){
                g_layout_type = size;
                if(size == 1){
                    setSizeS();
                }else if(size == 2){
                    setSizeM();
                }else if(size == 3){
                    setSizeL();
                }
            }

            if(arrayStyledAttributes.getBoolean(R.styleable.CustomEditText_singleLine, false)){
                setSingleLine();
            }

            String keyboard = arrayStyledAttributes.getString(R.styleable.CustomEditText_keyboardType);
            if(keyboard != null) {
                if (keyboard.equals(getResources().getString(R.string.KeyboardTypeAlphaNumeric))) {
                    setAlphaNumeric();
                } else if (keyboard.equals(getResources().getString(R.string.KeyboardTypeAlphaNumericComaAndDot))) {
                    setAlphaNumericComaAndDot();
                } else if (keyboard.equals(getResources().getString(R.string.KeyboardTypeNumericOnly))) {
                    g_validatorType = getResources().getString(R.string.ValidatorMinLengthTypeDigit);
                    setNumeric();
                } else if (keyboard.equals(getResources().getString(R.string.KeyboardTypeAlphaNumericComaSlashAndDot))) {
                    setAlphaNumericComaSlashAndDot();
                } else if (keyboard.equals(getResources().getString(R.string.KeyboardTypeAlphabetOnly))) {
                    setAlphabet();
                }
            }

            if(arrayStyledAttributes.getBoolean(R.styleable.CustomEditText_isPassword,false)){
                enabledTextPassword();
            }

            int maxLength = arrayStyledAttributes.getInteger(R.styleable.CustomEditText_maxLength, -1);
            if(maxLength != -1){
                setLength(maxLength);
            }

            float textSize = arrayStyledAttributes.getDimensionPixelSize(R.styleable.CustomEditText_messageSize, -1);
            if(textSize != -1){
                setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            if(arrayStyledAttributes.getBoolean(R.styleable.CustomEditText_noEmptyText,false)){
                addValidator(new ValidatorEmptyText());
            }

            int minLength = arrayStyledAttributes.getInteger(R.styleable.CustomEditText_minLength, -1);
            Log.d("MINLENGTH", g_validatorType);
            if(minLength != -1){
                setMinLength(minLength, g_validatorType);
            }

            String message = arrayStyledAttributes.getString(R.styleable.CustomEditText_message);
            if(!TextUtils.isEmpty(message)){
                setText(message);
            }

            String roundedType = arrayStyledAttributes.getString(R.styleable.CustomEditText_isRounded);
            setRoundedEdittext(roundedType);

            int hintAppearanceWithoutError = arrayStyledAttributes.getResourceId(R.styleable.CustomEditText_hintApperanceWithoutError, -1);
            if(hintAppearanceWithoutError != -1){
                resIdHintWithoutError = hintAppearanceWithoutError;
            }

            int hintAppearanceWithError = arrayStyledAttributes.getResourceId(R.styleable.CustomEditText_hintApperanceWithError, -1);
            if(hintAppearanceWithError != -1){
                resIdHintWithError = hintAppearanceWithError;
            }

            int subHintAppearance = arrayStyledAttributes.getResourceId(R.styleable.CustomEditText_subHintApperance, -1);
            if(subHintAppearance != -1){
                resIdSubHint = subHintAppearance;
            }

            int errorAppearance = arrayStyledAttributes.getResourceId(R.styleable.CustomEditText_errorApperance, -1);
            if(errorAppearance != -1){
                resIdError = errorAppearance;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            arrayStyledAttributes.recycle();
        }
    }
    //endregion

    //region METHOD + - (Judul, Error, SubHint)
    private void addJudul(){
        if(isNeedJudul) {
            if(g_layouts.findViewWithTag("judul") == null) {
                isNeedJudul = false;

                if (isNeedError) {
                    setHintApperance();
                    g_tv_Hint.setLayoutParams(getLayoutParamsWeightMultiLine());
                    g_tv_Hint.setGravity(Gravity.BOTTOM);
                    g_edittext.setLayoutParams(getLayoutParamsWeightMultiLine());
                    g_edittext.setGravity(Gravity.TOP);
                    g_edittext.setHint("");
                } else {
                    g_tv_Hint.setLayoutParams(getLayoutParamsWeightMultiLine());
                    g_edittext.setLayoutParams(getLayoutParamsWeightMultiLine());
                }

                g_layouts.addView(g_tv_Hint, 0);
            }
        }
    }

//    private void removeJudul(){
//        if(!isNeedJudul) {
//            if(g_layouts.findViewWithTag("judul") != null) {
//                isNeedJudul = true;
//                g_edittext.setGravity(Gravity.CENTER_VERTICAL);
//                if (isNeedError) {
//                    g_edittext.setLayoutParams(getLayoutParamsWeight4());
//                } else {
//                    g_edittext.setLayoutParams(getLayoutParamsWeight3());
//                }
//                g_edittext.setHint(g_tv_Hint.getText().toString());
//                g_layouts.removeView(g_tv_Hint);
//            }
//        }
//    }

    private void addError(){
        if(isNeedError) {
            if(g_layouts.findViewWithTag("error") == null) {
                isNeedError = false;
                setHintApperance();

                setErrorTextAppearance();
//                if(g_layout_type == 1){
//                    g_tv_Hint.setTextSize(getResources().getFloat(R.dimen.SizeTextSize8));
//                } else if(g_layout_type == 2){
//                    g_tv_Hint.setTextSize(getResources().getFloat(R.dimen.SizeTextSize10));
//                }else if(g_layout_type == 3){
//                    g_tv_Hint.setTextSize(getResources().getFloat(R.dimen.SizeTextSize12));
//                }

                g_tv_Error.setLayoutParams(getLayoutParamsWeightMultiLine());
                g_tv_Hint.setLayoutParams(getLayoutParamsWeightMultiLine());
                g_tv_Hint.setGravity(Gravity.CENTER_VERTICAL);
                g_edittext.setLayoutParams(getLayoutParamsWeightMultiLine());
                g_edittext.setGravity(Gravity.CENTER_VERTICAL);
                g_layouts.addView(g_tv_Error);
            }
        }
    }

    private void removeError(){
        if(!isNeedError) {
            if(g_layouts.findViewWithTag("error") != null) {
                isNeedError = true;
                setHintApperance();
//                if(g_layout_type == 1){
//                    g_tv_Hint.setTextSize(getResources().getFloat(R.dimen.SizeTextSize10));
//                }else if(g_layout_type == 2){
//                    g_tv_Hint.setTextSize(getResources().getFloat(R.dimen.SizeTextSize13));
//                }else if(g_layout_type == 3){
//                    g_tv_Hint.setTextSize(getResources().getFloat(R.dimen.SizeTextSize17));
//                }
                g_tv_Hint.setLayoutParams(getLayoutParamsWeightMultiLine());
                g_tv_Hint.setGravity(Gravity.BOTTOM);
                g_edittext.setLayoutParams(getLayoutParamsWeightMultiLine());
                g_edittext.setGravity(Gravity.TOP);

                g_layouts.removeView(g_tv_Error);
            }
        }
    }

    private void addSubHint(){
        if(!TextUtils.isEmpty(g_tv_Subhint.getText())) {
            if (g_layouts.findViewWithTag("subhint") == null) {
                setSubHintTextAppearance();
                g_edittext.setLayoutParams(getLayoutParamsWeightMultiLine());
                g_tv_Subhint.setLayoutParams(getLayoutParamsWeightMultiLine());
                g_layouts.addView(g_tv_Subhint,1);
                g_edittext.setGravity(Gravity.BOTTOM);
            }
        }
    }

    private void removeSubHint(){
        if (g_layouts.findViewWithTag("subhint") != null) {
            g_layouts.removeView(g_tv_Subhint);
            g_edittext.setGravity(Gravity.CENTER_VERTICAL);
        }
    }
    //endregion

    //region METHOD get LayoutParams
//    private LayoutParams getLayoutParamsWeight1(){
//        return getLayoutParamsWeightMultiLine();
////        return new LinearLayout.LayoutParams(
////                LayoutParams.MATCH_PARENT,
////                0,
////                getResources().getFloat(R.dimen.CustomEditTextSize1));
//    }
//
//    private LayoutParams getLayoutParamsWeight2(){
//        return getLayoutParamsWeightMultiLine();
////        return new LinearLayout.LayoutParams(
////                LayoutParams.MATCH_PARENT,
////                0,
////                getResources().getFloat(R.dimen.CustomEditTextSize2));
//    }

    private LayoutParams getLayoutParamsWeightMultiLine(){
        return new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,
                getResources().getInteger(R.integer.CustomEditTextSize1));
    }

    private LayoutParams getLayoutParamsHeight(int height){
        return new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                height);
    }
    //endregion

    //region METHOD SETTER & GETTER Attributes
    public void setHint(String p_strText){
        g_tv_Hint.setText(p_strText);
        g_edittext.setHint(p_strText);
    }

    public String getHint(){
        return g_tv_Hint.getText().toString();
    }

    public void setSubHint(String subhint){
        g_tv_Subhint.setText(subhint);
        g_tv_Subhint.setVisibility(VISIBLE);
    }

    public void setError(String p_strText){
        g_tv_Error.setText(p_strText);
    }

    public void setSizeL(){
        g_tv_Hint.setTextSize(getResources().getInteger(R.integer.SizeTextSize17));//:1
        g_edittext.setTextSize(getResources().getInteger(R.integer.SizeTextSize17));
        g_tv_Subhint.setTextSize(getResources().getInteger(R.integer.SizeTextSize12)); //:1.4
        g_tv_Error.setTextSize(getResources().getInteger(R.integer.SizeTextSize12)); //:1.4
        g_layouts.setMinimumHeight(getResources().getInteger(R.integer.SizeLayoutL));
        g_layouts.setLayoutParams(getLayoutParamsHeight(LayoutParams.WRAP_CONTENT));//x12
    }

    public void setSizeM(){
        g_tv_Hint.setTextSize(getResources().getInteger(R.integer.SizeTextSize13));//:1
        g_edittext.setTextSize(getResources().getInteger(R.integer.SizeTextSize13));
        g_tv_Subhint.setTextSize(getResources().getInteger(R.integer.SizeTextSize10)); //:1.4
        g_tv_Error.setTextSize(getResources().getInteger(R.integer.SizeTextSize10)); //:1.4
        g_layouts.setMinimumHeight(getResources().getInteger(R.integer.SizeLayoutM));
        g_layouts.setLayoutParams(getLayoutParamsHeight(LayoutParams.WRAP_CONTENT));//x12
    }

    public void setSizeS(){
        g_tv_Hint.setTextSize(getResources().getInteger(R.integer.SizeTextSize10));//:1
        g_edittext.setTextSize(getResources().getInteger(R.integer.SizeTextSize10));
        g_tv_Subhint.setTextSize(getResources().getInteger(R.integer.SizeTextSize8)); //:1.4
        g_tv_Error.setTextSize(getResources().getInteger(R.integer.SizeTextSize8)); //:1.4
        g_layouts.setMinimumHeight(getResources().getInteger(R.integer.SizeLayoutS));
        g_layouts.setLayoutParams(getLayoutParamsHeight(LayoutParams.WRAP_CONTENT));//x12
    }

    public void setSingleLine(){
        g_edittext.setSingleLine();
    }

    public void setAlphaNumeric(){
        g_edittext.setKeyListener(new NumberKeyListener()
        {

            protected char[] getAcceptedChars()
            {
                return new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                        'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
                        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ' '};
            }

            public int getInputType()
            {
                return InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
            }
        });
    }

    public void setAlphaNumericComaAndDot(){
        g_edittext.setKeyListener(new NumberKeyListener()
        {

            protected char[] getAcceptedChars()
            {
                return new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                        'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
                        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ' ', ',','.'};
            }

            public int getInputType()
            {
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
            }
        });
    }

    public void setAlphaNumericComaSlashAndDot(){
        g_edittext.setKeyListener(new NumberKeyListener()
        {

            protected char[] getAcceptedChars()
            {
                return new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                        'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
                        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ' ', '/' , ',','.','\'','-'};
            }

            public int getInputType()
            {
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
            }
        });
    }

    public void setAlphabet(){
        g_edittext.setKeyListener(new NumberKeyListener()
        {

            protected char[] getAcceptedChars()
            {
                return new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                        'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
                        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ' '};
            }

            public int getInputType()
            {
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
            }
        });
    }

    public void setNumeric(){
        g_edittext.setKeyListener(new NumberKeyListener()
        {

            protected char[] getAcceptedChars()
            {
                return new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };
            }

            public int getInputType()
            {
                return InputType.TYPE_CLASS_NUMBER;
            }
        });
    }

    private void enabledTextPassword(){
        g_edittext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        g_edittext.setSelection(g_edittext.getText().length());
    }

    public void setLength(int p_intLength){ g_edittext.setFilters(new InputFilter[] {new InputFilter.LengthFilter(p_intLength)}); }

    private void setTextSize(int p_unit, float p_size){
        g_edittext.setTextSize(p_unit, p_size);
    }

    private void setMinLength(int p_minLength, String p_validatorType){
        addValidator(new ValidatorMinLength(getContext(), p_minLength, p_validatorType));
    }

    public void setText(String p_text){
        if(TextUtils.isEmpty(p_text))
            return;

        g_edittext.setText(p_text);
    }

    public String getText(){
        return g_edittext.getText().toString();
    }

    private void setRoundedEdittext(String p_type){
        if(p_type.equals(getResources().getString(R.string.isRoundedAll))){
            g_layouts.setBackground(getResources().getDrawable(R.drawable.style_background_custom_edittext));
        }
        else if(p_type.equals(getResources().getString(R.string.isRoundedTop))){
            g_layouts.setBackground(getResources().getDrawable(R.drawable.style_background_custom_edittext_top));
        }
        else if(p_type.equals(getResources().getString(R.string.isRoundedBottom))){
            g_layouts.setBackground(getResources().getDrawable(R.drawable.style_background_custom_edittext_bottom));
        }

    }

    private void setHintApperance()
    {

        if(isNeedError){
            if(resIdHintWithoutError != 0){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    g_tv_Hint.setTextAppearance(resIdHintWithoutError);
                }
            }
            else {
                g_tv_Hint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                g_tv_Hint.setTextColor(getResources().getColor(R.color.colorNoError));
            }
        }
        else{
            if(resIdHintWithError != 0){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    g_tv_Hint.setTextAppearance(resIdHintWithError);
                }
            }
            else {
                g_tv_Hint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                g_tv_Hint.setTextColor(getResources().getColor(R.color.colorError));
            }
        }
    }

    private void setSubHintTextAppearance(){
        if(resIdSubHint != 0){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                g_tv_Subhint.setTextAppearance(resIdSubHint);
            }
        }
        else{
            g_tv_Subhint.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        }
    }

    private void setErrorTextAppearance(){
        if(resIdError != 0){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                g_tv_Error.setTextAppearance(resIdError);
            }
        }else{
            g_tv_Error.setTextColor(getResources().getColor(R.color.colorError));
        }
    }
    //endregion

    //region METHOD Validator
    @Override
    public void addValidator(IValidator p_validator){
        g_validatorArrayList.add(p_validator);
    }

    @Override
    public boolean checkValidator(){
        boolean isValid;

        for(int i = 0; i < g_validatorArrayList.size(); i++){
            isValid = g_validatorArrayList.get(i).validateText(g_edittext.getText().toString());
            if(!isValid){
                g_tv_Error.setText(g_validatorArrayList.get(i).getErrorMessage().replace("%d", getHint()));
                addError();

                return false;
            }
            else{
                g_tv_Error.setText(g_validatorArrayList.get(i).getErrorMessage().replace("%d", getHint()));
                removeError();
            }
        }
        return true;
    }

    @Override
    public boolean checkValidatorWithoutErrorMessage(){
        boolean isValid;
        for(int i = 0; i < g_validatorArrayList.size(); i++){
            isValid = g_validatorArrayList.get(i).validateText(g_edittext.getText().toString());
            if(!isValid){
                return false;
            }
        }
        return true;
    }
    //endregion

    //region SUBCLASS
    private class CustomFocus implements OnFocusChangeListener{

        private EditText l_text;

        private CustomFocus(EditText p_text){
            l_text = p_text;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            g_hasFocus = hasFocus;
            if(g_hasFocus){
                removeSubHint();
                addJudul();
            }
            //untuk menghilangkan error dan judul ketika lost focus
            else {
                boolean cek = checkValidator();
//                if(l_text.getText().toString().trim().isEmpty()){
//                    removeError();
//                    removeJudul();
//
//                    if(!g_tv_Subhint.getText().toString().trim().isEmpty()){
//                        addSubHint();
//                    }else{
//                        g_edittext.setLayoutParams(getLayoutParamsWeight4());
//                    }
//                }
            }

        }
    }

    private class CustomTextWatcher implements TextWatcher {

        private EditText l_edittext;

        private CustomTextWatcher(EditText p_edittext){
            l_edittext = p_edittext;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //##1.Check Validator if it has some focus
            if(this.l_edittext.hasFocus()){
                boolean valid = checkValidator();
            }
            //##2. Force first character can't be started with space
            if(l_edittext.getText().toString().trim().equals("")){
                l_edittext.getText().clear();
            }

//            validateText(l_edittext);
        }

        private boolean validateText(EditText p_edittext) {

            if (p_edittext.getText().length() < 16 && g_hasFocus)
            {
                addError();
                return false;
            }
            else {
                removeError();
            }
            return true;
        }
    }
    //endregion

}
