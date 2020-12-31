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

public class SpyKeyboardView extends android.inputmethodservice.KeyboardView {

    Context context;

    public SpyKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /*List<Keyboard.Key> keys = getKeyboard().getKeys();
        for (Keyboard.Key key : keys) {
            if (key.codes[0] == 7) {
                Log.e("KEY", "Drawing key with code " + key.codes[0]);
                Drawable dr = (Drawable) context.getResources().getDrawable(R.drawable.sym_keyboard_delete);
                dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                dr.draw(canvas);

            } else {
                Drawable dr = (Drawable) context.getResources().getDrawable(R.drawable.sym_keyboard_delete);
                dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                dr.draw(canvas);
            }
        }

         */
    }
}