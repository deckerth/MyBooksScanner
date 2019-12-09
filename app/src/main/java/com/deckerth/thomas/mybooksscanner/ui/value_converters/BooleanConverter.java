package com.deckerth.thomas.mybooksscanner.ui.value_converters;

import android.databinding.InverseMethod;

public class BooleanConverter {

    @SuppressWarnings("UnnecessaryUnboxing")
    @InverseMethod("boolBox")
    public static boolean boolUnbox(Boolean b) {
        return (b != null) && b.booleanValue();
    }

    public static Boolean boolBox(boolean b) {
        return b ? Boolean.TRUE : Boolean.FALSE;
    }

}
