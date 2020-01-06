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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.example.customedittext.validator.IValidator;
import com.example.customedittext.validator.OnCheckValidatorListener;
import com.example.customedittext.validator.ValidableView;
import com.example.customedittext.validator.ValidatorAlphaNumeric;
import com.example.customedittext.validator.ValidatorContainAlphaNumeric;
import com.example.customedittext.validator.ValidatorCreateCodeAccess;
import com.example.customedittext.validator.ValidatorDigitOnly;
import com.example.customedittext.validator.ValidatorEmail;
import com.example.customedittext.validator.ValidatorEmptyText;
import com.example.customedittext.validator.ValidatorMatcherText;
import com.example.customedittext.validator.ValidatorMinLengthAlphabet;
import com.example.customedittext.validator.ValidatorMinLengthDigit;

import java.util.ArrayList;
import java.util.regex.Matcher;

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
    private ArrayList<IValidator> g_menuValidator;
    private int g_layout_type = 0;

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

        g_menuValidator = new ArrayList<>();
        g_menuValidator.add(new ValidatorEmptyText());
        g_menuValidator.add(new ValidatorCreateCodeAccess());
        g_menuValidator.add(new ValidatorAlphaNumeric());
        g_menuValidator.add(new ValidatorContainAlphaNumeric());
        g_menuValidator.add(new ValidatorDigitOnly());
        g_menuValidator.add(new ValidatorEmail());
    }

    private void init(Context context){
        initLayout(context);
        g_edittext.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        g_edittext.setOnFocusChangeListener(new CustomFocus(g_edittext));
        g_edittext.addTextChangedListener(new CustomTextWatcher(g_edittext));
        g_validatorArrayList = new ArrayList<>();
    }

    private void init(Context context, AttributeSet attrs){
        initLayout(context);
        g_validatorArrayList = new ArrayList<>();
        g_edittext.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        g_edittext.setOnFocusChangeListener(new CustomFocus(g_edittext));
        g_edittext.addTextChangedListener(new CustomTextWatcher(g_edittext));
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

            int keyboard = arrayStyledAttributes.getInteger(R.styleable.CustomEditText_keyboardType, -1);
            if(keyboard != -1){
                if(keyboard == 1){
                    setAlphaNumeric();
                }else if(keyboard == 2){
                    setAlphaNumericComaAndDot();
                }else if(keyboard == 3){
                    setNumeric();
                }else if(keyboard == 4){
                    setAlphaNumericComaSlashAndDot();
                }else if(keyboard == 5){
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

            if (arrayStyledAttributes.getString(R.styleable.CustomEditText_validatorType) != null) {
                String[] validatorArray = arrayStyledAttributes.getString(R.styleable.CustomEditText_validatorType).split("|");
                if (validatorArray.length > 0) {
                    setValidator(validatorArray);
                }
            }

            int minLengthDigit = arrayStyledAttributes.getInteger(R.styleable.CustomEditText_minLengthDigit, -1);
            if(minLengthDigit != -1){
                setMinLengthDigit(minLengthDigit);
            }

            int minLengthAlphabet = arrayStyledAttributes.getInteger(R.styleable.CustomEditText_minLengthAlphabet, -1);
            if(minLengthAlphabet != -1){
                setMinLengthAlphabet(minLengthAlphabet);
            }

            int matcherEditText = arrayStyledAttributes.getResourceId(R.styleable.CustomEditText_matchText, -1);
            if(matcherEditText != -1){
                setMatcherText(matcherEditText);
            }

            String message = arrayStyledAttributes.getString(R.styleable.CustomEditText_message);
            if(!TextUtils.isEmpty(message)){
                setText(message);
            }

            int roundedType = arrayStyledAttributes.getInteger(R.styleable.CustomEditText_isRounded, -1);
            if(roundedType != -1){
                setRoundedEdittext(roundedType);
            }

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
                    g_tv_Hint.setLayoutParams(getLayoutParamsWeight2());
                    g_tv_Hint.setGravity(Gravity.BOTTOM);
                    g_edittext.setLayoutParams(getLayoutParamsWeight2());
                    g_edittext.setGravity(Gravity.TOP);
                    g_edittext.setHint("");
                } else {
                    g_tv_Hint.setLayoutParams(getLayoutParamsWeight1());
                    g_edittext.setLayoutParams(getLayoutParamsWeight2());
                }

                g_layouts.addView(g_tv_Hint, 0);
            }
        }
    }

    private void removeJudul(){
        if(!isNeedJudul) {
            if(g_layouts.findViewWithTag("judul") != null) {
                isNeedJudul = true;
                g_edittext.setGravity(Gravity.CENTER_VERTICAL);
                if (isNeedError) {
                    g_edittext.setLayoutParams(getLayoutParamsWeight4());
                } else {
                    g_edittext.setLayoutParams(getLayoutParamsWeight3());
                }
                g_edittext.setHint(g_tv_Hint.getText().toString());
                g_layouts.removeView(g_tv_Hint);
            }
        }
    }

    private void addError(){
        if(isNeedError) {
            if(g_layouts.findViewWithTag("error") == null) {
                isNeedError = false;
                setHintApperance();

                setErrorTextAppearance();
                if(g_layout_type == 1){
                    g_tv_Hint.setTextSize(getResources().getFloat(R.dimen.SizeTextSize8));
                } else if(g_layout_type == 2){
                    g_tv_Hint.setTextSize(getResources().getFloat(R.dimen.SizeTextSize10));
                }else if(g_layout_type == 3){
                    g_tv_Hint.setTextSize(getResources().getFloat(R.dimen.SizeTextSize12));
                }

                g_tv_Error.setLayoutParams(getLayoutParamsWeight1());
                g_tv_Hint.setLayoutParams(getLayoutParamsWeight1());
                g_tv_Hint.setGravity(Gravity.CENTER_VERTICAL);
                g_edittext.setLayoutParams(getLayoutParamsWeight2());
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
                if(g_layout_type == 1){
                    g_tv_Hint.setTextSize(getResources().getFloat(R.dimen.SizeTextSize10));
                }else if(g_layout_type == 2){
                    g_tv_Hint.setTextSize(getResources().getFloat(R.dimen.SizeTextSize13));
                }else if(g_layout_type == 3){
                    g_tv_Hint.setTextSize(getResources().getFloat(R.dimen.SizeTextSize17));
                }
                g_tv_Hint.setLayoutParams(getLayoutParamsWeight2());
                g_tv_Hint.setGravity(Gravity.BOTTOM);
                g_edittext.setLayoutParams(getLayoutParamsWeight2());
                g_edittext.setGravity(Gravity.TOP);

                g_layouts.removeView(g_tv_Error);
            }
        }
    }

    private void addSubHint(){
        if(!TextUtils.isEmpty(g_tv_Subhint.getText())) {
            if (g_layouts.findViewWithTag("subhint") == null) {
                setSubHintTextAppearance();
                g_edittext.setLayoutParams(getLayoutParamsWeight2());
                g_tv_Subhint.setLayoutParams(getLayoutParamsWeight2());
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
    private LayoutParams getLayoutParamsWeight1(){
        return new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                0,
                getResources().getFloat(R.dimen.CustomEditTextSize1));
    }

    private LayoutParams getLayoutParamsWeight2(){
        return new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                0,
                getResources().getFloat(R.dimen.CustomEditTextSize2));
    }

    private LayoutParams getLayoutParamsWeight3(){
        return new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                0,
                getResources().getFloat(R.dimen.CustomEditTextSize3));
    }

    private LayoutParams getLayoutParamsWeight4(){
        return new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                0,
                getResources().getFloat(R.dimen.CustomEditTextFull));
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
        g_tv_Hint.setTextSize(getResources().getFloat(R.dimen.SizeTextSize17));
        g_edittext.setTextSize(getResources().getFloat(R.dimen.SizeTextSize17));
        g_tv_Subhint.setTextSize(getResources().getFloat(R.dimen.SizeTextSize12));
        g_tv_Error.setTextSize(getResources().getFloat(R.dimen.SizeTextSize12));
        g_layouts.setLayoutParams(getLayoutParamsHeight((int)getResources().getFloat(R.dimen.SizeLayoutL)));
    }

    public void setSizeM(){
        g_tv_Hint.setTextSize(getResources().getFloat(R.dimen.SizeTextSize13));
        g_edittext.setTextSize(getResources().getFloat(R.dimen.SizeTextSize13));
        g_tv_Subhint.setTextSize(getResources().getFloat(R.dimen.SizeTextSize10));
        g_tv_Error.setTextSize(getResources().getFloat(R.dimen.SizeTextSize10));
        g_layouts.setLayoutParams(getLayoutParamsHeight((int)getResources().getFloat(R.dimen.SizeLayoutM)));
    }

    public void setSizeS(){
        g_tv_Hint.setTextSize(getResources().getFloat(R.dimen.SizeTextSize10));
        g_edittext.setTextSize(getResources().getFloat(R.dimen.SizeTextSize10));
        g_tv_Subhint.setTextSize(getResources().getFloat(R.dimen.SizeTextSize8));
        g_tv_Error.setTextSize(getResources().getFloat(R.dimen.SizeTextSize8));
        g_layouts.setLayoutParams(getLayoutParamsHeight((int)getResources().getFloat(R.dimen.SizeLayoutS)));
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
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
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
                return InputType.TYPE_CLASS_NUMBER ;
            }
        });
    }

    public void enabledTextPassword(){
        g_edittext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        g_edittext.setSelection(g_edittext.getText().length());
    }

    public void setLength(int p_intLength){ g_edittext.setFilters(new InputFilter[] {new InputFilter.LengthFilter(p_intLength)}); }

    private void setTextSize(int p_unit, float p_size){
        g_edittext.setTextSize(p_unit, p_size);
    }

    private void setValidator(String[] p_validatorList){
        for(int idx = 0; idx < p_validatorList.length; idx++){
            try {
                addValidator(g_menuValidator.get(Integer.parseInt(p_validatorList[idx])));
            }catch (Exception ex){
                Log.d("EXCEPTION", "Set validator gagal pada validator ke-" + idx + " ERROR : " + ex.getMessage());
            }
        }
    }

    private void setMinLengthAlphabet(int p_minLength){
        addValidator(new ValidatorMinLengthAlphabet(p_minLength));
    }

    private void setMinLengthDigit(int p_minLength){
        addValidator(new ValidatorMinLengthDigit(p_minLength));
    }

    private void setMatcherText(int p_idEditText){
        try {

            CustomEditText l_matcher = findViewById(p_idEditText);
            if(l_matcher != null){
                addValidator(new ValidatorMatcherText(l_matcher));
            }
        }catch (Exception ex){

            Log.d("EXCEPTION", "Set Matcher Text gagal, ERROR : " + ex.getMessage());
        }
    }

    public void setText(String p_text){
        if(TextUtils.isEmpty(p_text))
            return;

        g_edittext.setText(p_text);
    }

    public String getText(){
        return g_edittext.getText().toString();
    }

    public void setRoundedEdittext(int p_type){
        if(p_type == 1){
            g_layouts.setBackground(getResources().getDrawable(R.drawable.style_background_custom_edittext));
        }
        else if(p_type == 2){
            g_layouts.setBackground(getResources().getDrawable(R.drawable.style_background_custom_edittext_top));
        }
        else if(p_type == 3){
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

    public void setErrorTextAppearance(){
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
