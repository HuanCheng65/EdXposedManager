package com.huanchengfly.edxp.interfaces;

import android.content.DialogInterface;

public abstract class ButtonCallback {

    public ButtonCallback() {
        super();
    }

    public void onAny(DialogInterface dialog) {
    }

    public void onPositive(DialogInterface dialog) {
    }

    public void onNegative(DialogInterface dialog) {
    }

    // The overidden methods below prevent Android Studio from suggesting that they are overidden by developers
    public void onNeutral(DialogInterface dialog) {
    }

    @Override
    protected final Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public final boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    protected final void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final String toString() {
        return super.toString();
    }
}
