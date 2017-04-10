package com.project.alertactivity_lasc;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

/**
 * Created by sty on 7/30/16.
 */
public class ReplaceFont {
    public static void replaceDefaultFont(Context context, String nameOfFontBeingReplaced, String nameOfFontInAsset){
        Typeface customFontTypeface= Typeface.createFromAsset(context.getAssets(),nameOfFontInAsset);
        replaceFont(nameOfFontBeingReplaced,customFontTypeface);
    }
    private static void replaceFont(String nameOfFontBeingReplaced, Typeface customFontTypeface){
        try {
            Field myfield = Typeface.class.getDeclaredField(nameOfFontBeingReplaced);
            myfield.setAccessible(true);
            myfield.set(null, customFontTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
