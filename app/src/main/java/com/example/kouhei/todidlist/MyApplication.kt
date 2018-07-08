package com.example.kouhei.todidlist

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.os.Bundle

/**
 * グローバルアプリケーション状態を維持するための基本クラス。
 * 完全修飾名をAndroidManifest.xmlの<application>タグの "android：name"属性として指定することで、
 * 独自の実装を提供できる。
 */
open class MyApplication : Application(), Application.ActivityLifecycleCallbacks {

    private var isAppHidden = true

    override fun onCreate() {
        super.onCreate()
        // 登録しないとonActivityStarted() が働かない。
        registerActivityLifecycleCallbacks(this)
    }

    /**
     * onTrimMemory() はandroid がMemory をTrim するタイミングで呼ばれる。
     * TRIM_MEMORY_UI_HIDDEN はUI が隠された時、つまりアプリがバックグラウンドに行った時。
     * その時にisAppHidden をtrueにすることで、次にアプリをフォアグラウンドにした時に
     * パスコードを要求するActivity に遷移する。
     */
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            isAppHidden = true
        }
    }

    /**
     * Activity が開始されるときに呼ばれる。
     * アプリが一度バックグラウンドに移行してisAppHidden がtrueになり、
     * かつパスコードを”必要“に設定してる（APP_NEES_PASSCODE == true）ときは
     * パスコードを要求するActivity に遷移する
     */
    override fun onActivityStarted(activity: Activity?) {
        if (isAppHidden && ApplicationUtil.APP_NEES_PASSCODE) {
//            activity?.startActivity()
        }
        isAppHidden = false
    }

    override fun onActivityResumed(activity: Activity?) {}
    override fun onActivityDestroyed(activity: Activity?) {}
    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}
    override fun onActivityStopped(activity: Activity?) {}
    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {}
    override fun onActivityPaused(activity: Activity?) {}
}