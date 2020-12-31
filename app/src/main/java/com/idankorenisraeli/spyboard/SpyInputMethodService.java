package com.idankorenisraeli.spyboard;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputConnection;

/**
 * This class will simulate the functionality of an android keyboard,
 * With custom tracking of all the pressed keys
 */
public class SpyInputMethodService extends android.inputmethodservice.InputMethodService {

    SpyKeyboardView keyboardView;
    
    boolean caps = false;
    boolean hebrewMode = false;

    Keyboard engKeyboard, hebKeyboard;


    private final static int WHITE_THEME = 1;
    private final static int GRAY_THEME = 2;
    private final static int BLUE_THEME = 3;



    @Override
    public View onCreateInputView() {
        // get the KeyboardView and add our Keyboard layout to it
        keyboardView = (SpyKeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view_blue, null);
        engKeyboard = new Keyboard(this, R.xml.qwerty_eng);
        hebKeyboard = new Keyboard(this, R.xml.qwerty_heb);

        keyboardView.setKeyboard(hebrewMode ? hebKeyboard : engKeyboard);
        keyboardView.setOnKeyboardActionListener(keyBoardAction);

        keyboardView.setPreviewEnabled(false);



        return keyboardView;
    }

    private void switchLanguage(){
        hebrewMode = !hebrewMode;
        keyboardView.setKeyboard(hebrewMode ? hebKeyboard : engKeyboard);
        // Updating the keyboard view based on the current mode
    }


    private void pressedDelete(InputConnection ic){
        CharSequence selectedText = ic.getSelectedText(0);
        if (TextUtils.isEmpty(selectedText)) {
            // no selection, so delete previous character
            ic.deleteSurroundingText(1, 0);
        } else {
            // delete the selection
            ic.commitText("", 1);
        }
    }

    private void pressedShift(){
        caps = !caps;
        keyboardView.setShifted(caps);
    }
    

    private void pressedCharacter(int primaryCode, InputConnection ic){

        char code;
        if(hebrewMode){
            code = engToHeb(primaryCode);
        }
        else{
            primaryCode = caps ? primaryCode + ('A' - 'a') : primaryCode; // converting to upper case when shift
            code = (char) primaryCode; // converting to char
        }

        ic.commitText(String.valueOf(code), 1); //writing to screen

        // TODO: 30/12/2020 Add tacking of this key
    }

    private char engToHeb(int primaryCode){
        switch (primaryCode){
            case 'q':
                return '/';
            case 'w':
                return '\'';
            case 'e':
                return 'ק';
            case 'r':
                return 'ר';
            case 't':
                return 'א';
            case 'y':
                return 'ט';
            case 'u':
                return 'ו';
            case 'i':
                return 'ן';
            case 'o':
                return 'ם';
            case 'p':
                return 'פ';
            case 'a':
                return 'ש';
            case 's':
                return 'ד';
            case 'd':
                return 'ג';
            case 'f':
                return 'כ';
            case 'g':
                return 'ע';
            case 'h':
                return 'י';
            case 'j':
                return 'ח';
            case 'k':
                return 'ל';
            case 'l':
                return 'ך';
            case 878:
                return 'ף';
            case 'z':
                return 'ז';
            case 'x':
                return 'ס';
            case 'c':
                return 'ב';
            case 'v':
                return 'ה';
            case 'b':
                return 'נ';
            case 'n':
                return 'מ';
            case 'm':
                return 'צ';
            case 879:
                return 'ת';
            case 880:
                return 'ץ';
            case 881:
                return '-';


        }
        return (char)primaryCode; // couldn't find any heb key
    }


    // TODO: 30/12/2020 Add spacebar tacking 
    
    KeyboardView.OnKeyboardActionListener keyBoardAction = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {

        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Log.i("pttt", "primary: " + primaryCode + " | keycodes: " + keyCodes.length);
            InputConnection ic = getCurrentInputConnection();
            if (ic == null) return;
            switch (primaryCode) {
                case Keyboard.KEYCODE_DELETE:
                    pressedDelete(ic);
                    break;
                case Keyboard.KEYCODE_SHIFT:
                    pressedShift();
                    break;
                case Keyboard.KEYCODE_MODE_CHANGE:
                    switchLanguage();
                    break;
                default:
                    pressedCharacter(primaryCode, ic);
            }
        }
        


        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };
}