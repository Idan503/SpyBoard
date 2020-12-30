package com.idankorenisraeli.spyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class MyInputMethodService extends android.inputmethodservice.InputMethodService {

    KeyboardView keyboardView;

    @Override
    public View onCreateInputView() {
        // get the KeyboardView and add our Keyboard layout to it
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view, null);
        Keyboard keyboard = new Keyboard(this, R.xml.keyboard_qwerty);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(keyBoardAction);

        keyboardView.setPreviewEnabled(false);



        return keyboardView;
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
                case Keyboard.KEYCODE_MODE_CHANGE:
                    //change to hebrew
                    break;
                default:
                    char code = (char) primaryCode;
                    ic.commitText(String.valueOf(code), 1);
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