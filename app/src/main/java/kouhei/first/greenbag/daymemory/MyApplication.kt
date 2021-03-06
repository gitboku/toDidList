package kouhei.first.greenbag.daymemory

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity

/**
 * グローバルアプリケーション状態を維持するための基本クラス。
 * 完全修飾名をAndroidManifest.xmlの<application>タグの "android：name"属性として指定することで、
 * 独自の実装を提供できる。
 */
open class MyApplication : Application(), Application.ActivityLifecycleCallbacks {

    companion object {
        // アプリ上でパスコードを要求するかどうかのPreferenceのキー
        var APP_NEED_PASSCODE = "app_needs_passcode"

        // パスコードのPreferenceのキー
        var PASSCODE = "passcode"

        /**
         * 画像を読み込む権限があるかを判定するフラグ
         * 0: Permission granted
         */
        var isGrantedReadStorage = 0

        /**
         * インテントに日付を渡すときのキーとなる文字列
         */
        val SELECTED_DATE = "com.example.todidList.SELECTED_DATE"

        /**
         * インテントに日記のIDを渡すときのキーとなる文字列
         */
        val SELECTED_DIARY_ID = "com.example.todidList.SELELCTED_DIARY_ID"
    }

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
        val data = getSharedPreferences(getString(R.string.preference_file_name), AppCompatActivity.MODE_PRIVATE)
        val isPassCodeNeed = data.getBoolean(APP_NEED_PASSCODE, false)
        val passCode = data.getString(PASSCODE, null)

        // 画像を読み込む権限の可否情報を取得
        isGrantedReadStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        // ActivityをStartした時、以下の条件を満たしていればPassCodeConfirmActivityに移動する。
        // 　・アプリがHidden
        // 　・パスコードを使うように設定されている
        // 　・パスコードの桁が４桁
        if (isAppHidden && isPassCodeNeed && passCode.length == 4) {
            val mIntent = Intent(this, PassCodeConfirmActivity::class.java)
            mIntent.putExtra(PASSCODE, passCode)
            startActivity(mIntent)
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