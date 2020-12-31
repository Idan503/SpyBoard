package com.idankorenisraeli.spyboard.input;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import com.idankorenisraeli.spyboard.utils.KeycodeDictionary;
import com.idankorenisraeli.spyboard.R;

/**
 * This class will simulate the functionality of an android keyboard,
 * With custom tracking of all the pressed keys
 */
public class SpyInputMethodService extends android.inputmethodservice.InputMethodService {

    SpyKeyboardView keyboardView;

    KeycodeDictionary dictionary;

    boolean caps = false;
    boolean hebrewMode = false;

    Keyboard engKeyboard, hebKeyboard;


    interface KEYS{
        int ENTER = -10;
    }

    @Override
    public View onCreateInputView() {
        // get the KeyboardView and add our Keyboard layout to it
        keyboardView = (SpyKeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view_blue, null);
        engKeyboard = new Keyboard(this, R.xml.qwerty_eng);
        hebKeyboard = new Keyboard(this, R.xml.qwerty_heb);

        dictionary = KeycodeDictionary.getInstance();

        keyboardView.setKeyboard(hebrewMode ? hebKeyboard : engKeyboard);
        keyboardView.setOnKeyboardActionListener(keyBoardAction);

        keyboardView.setPreviewEnabled(false);



        return keyboardView;
    }

    private void languageAction(){
        hebrewMode = !hebrewMode;
        keyboardView.setKeyboard(hebrewMode ? hebKeyboard : engKeyboard);
        // Updating the keyboard view based on the current mode
    }


    private void deleteAction(InputConnection ic){
        CharSequence selectedText = ic.getSelectedText(0);
        if (TextUtils.isEmpty(selectedText)) {
            // no selection, so delete previous character
            ic.deleteSurroundingText(1, 0);
        } else {
            // delete the selection
            ic.commitText("", 1);
        }
    }

    private void shiftAction(){
        caps = !caps;
        keyboardView.setShifted(caps);
    }

    private void enterAction(InputConnection ic){
        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
    }


    private void charAction(int primaryCode, InputConnection ic){

        char code;
        if(hebrewMode){
            code = dictionary.engToHeb(primaryCode);
        }
        else{
            primaryCode = caps ? primaryCode + ('A' - 'a') : primaryCode; // converting to upper case when shift
            code = (char) primaryCode; // converting to char
        }

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
            InputConnection ic = getCurrentInputConnection();
            if (ic == null) return;
            switch (primaryCode) {
                case Keyboard.KEYCODE_DELETE:
                    deleteAction(ic);
                    break;
                case Keyboard.KEYCODE_SHIFT:
                    shiftAction();
                    break;
                case Keyboard.KEYCODE_MODE_CHANGE:
                    languageAction();
                    break;
                case KEYS.ENTER:
                    enterAction(ic);
                    break;

                default:
                    charAction(primaryCode, ic);
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