package com.reassel.calculator;

import android.os.Bundle;
import android.os.Debug;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.google.android.material.button.MaterialButton;

import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int defaultTextSize = 50;
    private int scaledTextSize = 35;
    private int scaleLen = 12;
    private int maxLen = 15;

    private String errorMsg = "Ошибка";
    private String bigNumErrorMsg = "Большое число";

    MaterialButton button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;
    MaterialButton buttonC, buttonBracketOpen, getButtonBracketClose;
    MaterialButton buttonAC, buttonDot;
    MaterialButton buttonDivide, buttonMultiply, buttonPlus, buttonMines, buttonResult;

    LinearLayout resultContainer;

    LinearLayout switchTheme;

    ResultView textView1, textView2;

    String regex = "[+\\-*/]+";

    private boolean isLightTheme = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animateAppLaunch();

        assignId(button0, R.id.button_zero);
        assignId(button1, R.id.button_one);
        assignId(button2, R.id.button_two);
        assignId(button3, R.id.button_three);
        assignId(button4, R.id.button_four);
        assignId(button5, R.id.button_five);
        assignId(button6, R.id.button_six);
        assignId(button7, R.id.button_seven);
        assignId(button8, R.id.button_eight);
        assignId(button9, R.id.button_nine);

        assignId(buttonC, R.id.button_c);
        assignId(buttonBracketOpen, R.id.button_open_bracket);
        assignId(getButtonBracketClose, R.id.button_close_bracket);

        assignId(buttonAC, R.id.button_AC);
        assignId(buttonDot, R.id.button_dot);

        assignId(buttonDivide, R.id.button_division);
        assignId(buttonMultiply, R.id.button_multiplication);
        assignId(buttonPlus, R.id.button_plus);
        assignId(buttonMines, R.id.button_mines);
        assignId(buttonResult, R.id.button_result);

        resultContainer = findViewById(R.id.result_container);
//        switchTheme = findViewById(R.id.theme_switch_container);

        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
    }



    private void animateAppLaunch() {
        LinearLayout textView = findViewById(R.id.buttons_layout);

        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        textView.startAnimation(slideIn);
    }

    void assignId(MaterialButton btn, int id) {
        btn = findViewById(id);
        btn.setOnClickListener(this);
    }

    private void switchTheme(boolean isNightMode) {
        setTheme(isNightMode ? R.style.AppThemeNight : R.style.AppTheme);
        recreate();
    }


    @Override
    public void onClick(View view) {

        MaterialButton button = (MaterialButton) view;

        String buttonText = button.getText().toString();

        String dataToCalc = textView2.getText().toString();


        if(buttonText.equals(".")){

            String[] parts = dataToCalc.split(regex);

            if(parts[parts.length-1].contains(".")) {
                return;
            }
        }

        if(buttonText.equals("(")){
            char lastSymbol = dataToCalc.charAt(dataToCalc.length() -1);
            if(! dataToCalc.equals("0") && ! isArithmetic(lastSymbol)) {
                return;
            }
        }

        if(buttonText.equals("*") || buttonText.equals("/") || buttonText.equals("+") || buttonText.equals("-")) {
            char lastSymbol = dataToCalc.charAt(dataToCalc.length() -1);
            if(isArithmetic(lastSymbol) || lastSymbol == '(') {
                return;
            }
        }

        if(Character.isDigit(buttonText.charAt(0))) {
            char lastSymbol = dataToCalc.charAt(dataToCalc.length() -1);
            if(lastSymbol == ')') {
                return;
            }
        }

        if(buttonText.equals(")")){
            char lastSymbol = dataToCalc.charAt(dataToCalc.length() -1);
            if(! checkParentheses(dataToCalc + buttonText) || isArithmetic(lastSymbol)) {
                return;
            }
        }

        if(dataToCalc.equals(errorMsg) || dataToCalc.equals(bigNumErrorMsg)) {
            dataToCalc = "0";
        }

        if(buttonText.equals("AC")){
            textView2.setText("0");
            textView1.setText("");
            return;
        }

        if(buttonText.equals("=")){
            char lastSymbol = dataToCalc.charAt(dataToCalc.length()-1);
            if( ! (Character.isDigit(lastSymbol) || lastSymbol == ')')) {
                return;
            }
            textView1.setText(dataToCalc);
            textView2.setText(getResult(dataToCalc));
            return;
        }

        if(buttonText.equals("C")) {
            dataToCalc = dataToCalc.length() == 1 ? "0" : dataToCalc.substring(0, dataToCalc.length()-1);

        } else {
            if(dataToCalc.length() > maxLen) {
                return;
            }

            if(dataToCalc.equals("0") && ! isArithmetic(buttonText.charAt(0)) && ! buttonText.equals(".")) {
                dataToCalc = buttonText;
                textView1.setText("");
            }
            else {
                dataToCalc += buttonText;
            }
        }

        if(dataToCalc.length() > scaleLen) {

            textView2.setTextSize(scaledTextSize);
        }
        else {
            textView2.setTextSize(defaultTextSize);
        }

        textView2.setText(dataToCalc);

    }

    String getResult(String data) {
        try{
            Context context = Context.enter();
            context.setOptimizationLevel(-1);
            Scriptable scriptable = context.initSafeStandardObjects();
            String result = context.evaluateString(scriptable, data, "Javascript", 1, null).toString();

            return !result.contains("E") ? result : bigNumErrorMsg;
        }
        catch (Exception e) {
            return errorMsg;
        }
    }


    private boolean checkParentheses(String str) {
        Stack<Character> stack = new Stack<>();

        for (char ch : str.toCharArray()) {
            if (ch == '(') {
                stack.push(ch);
            } else if (ch == ')') {
                if (stack.isEmpty()) {
                    return false;
                }
                stack.pop();
            }
        }

        return true;
    }

    private Boolean isArithmetic(char symbol ) {
        return symbol == '+' || symbol == '-' || symbol == '/' || symbol == '*';
    }


}