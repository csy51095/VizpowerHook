package me.lyc8503.vizpowerhook.hook;

import android.app.AndroidAppHelper;
import android.content.ContentResolver;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class HttpLoginHook extends XC_MethodHook {

    public static String TAG = "HttpLoginHook";

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);

        String newListenType = "-1";


        // 用XharedPreferencce 获取 MainActivity 保存的 preference
//        XSharedPreferences preferences = new XSharedPreferences("me.lyc8503.vizpowerhook", "config");
//        preferences.makeWorldReadable();
        // 在安卓 7 以上被弃用( 用ContentProvider 代替)

        ContentResolver resolver = AndroidAppHelper.currentApplication().getApplicationContext().getContentResolver();
        Uri uri = Uri.parse("content://me.lyc8503.vizpowerhook/config");
        Cursor cursor = resolver.query(uri, null, null, null, null);
        XposedBridge.log(TAG + " " + Arrays.toString(cursor.getColumnNames()));
        cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndex("name"));
        boolean bypassSensitive = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("bypassSensitive")));
        boolean showPeople = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("showPeople")));
        cursor.close();

        // 遍历所有课程
        List<Map<String, String>> allClasses = (List<Map<String, String>>) XposedHelpers.getObjectField(param.thisObject, "m_MeetingDataList");
//        List<Map<String, String>> allClassesNew = new ArrayList<>();
        for (Map<String, String> oneClass : allClasses) {
            // XposedBridge.log(TAG + " " + oneClass);
            for (Map.Entry<String, String> entry : oneClass.entrySet()) {
//                if (entry.getKey().equals("listenType")) {
//                    entry.setValue(newListenType);
//                    XposedBridge.log(TAG + " Hook到listenType并改为" + newListenType);
//                }

                // 改名 & 过屏蔽词 & 显示人数 核心代码
                if (entry.getKey().equalsIgnoreCase("NickName")) {
                    if (!TextUtils.isEmpty(name)) {
                        entry.setValue(name);
                        XposedBridge.log(TAG + " Hook到NickName并改为" + name);
                    } else {
                        if (CompileOpt.changeName) entry.setValue("匿名_3984");
                        XposedBridge.log(TAG + " Hook到NickName. 不改名.");
                    }
                }

                if (entry.getKey().equalsIgnoreCase("SensitiveWordsURL")) {
                    if (bypassSensitive) {
                        entry.setValue("http://127.0.0.1/404.txt");
                        XposedBridge.log(TAG + " Hook到敏感词列表并改为" + "http://127.0.0.1/404.txt");
                    } else {
                        XposedBridge.log(TAG + " Hook到敏感词列表. 不修改.");
                    }
                }

                if (entry.getKey().equalsIgnoreCase("ShowUserCount")) {
                    if (showPeople) {
                        entry.setValue("1");
                        XposedBridge.log(TAG + " Hook到ShowUserCount并改为" + 1);
                    } else {
                        XposedBridge.log(TAG + " Hook到ShowUserCount. 不修改.");
                    }
                }

//                if (entry.getKey().equals("NickName")){
//                    if(!TextUtils.isEmpty(MainActivity.preferences.getString("name", ""))){
//                        entry.setValue(MainActivity.preferences.getString("name", ""));
//                    }
//                }
//
//                if (entry.getKey().equals("NickName")){
//                    if(!TextUtils.isEmpty(MainActivity.preferences.getString("name", ""))){
//                        entry.setValue(MainActivity.preferences.getString("name", ""));
//                    }
//                }
            }
        }
    }
}
