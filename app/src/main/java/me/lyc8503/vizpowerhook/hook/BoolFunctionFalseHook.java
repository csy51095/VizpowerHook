package me.lyc8503.vizpowerhook.hook;

import de.robv.android.xposed.XC_MethodHook;

public class BoolFunctionFalseHook extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
        param.setResult(false);
    }
}
