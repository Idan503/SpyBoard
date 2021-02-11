package com.idankorenisraeli.spyboard.input;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.inputmethodservice.Keyboard;
import android.util.AttributeSet;
import android.util.Log;

import java.util.List;

/**
 * Provides a distinctive keyboard class view to the keyboard of this application
 */
public class SpyKeyboardView extends android.inputmethodservice.KeyboardView {


    public SpyKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


}