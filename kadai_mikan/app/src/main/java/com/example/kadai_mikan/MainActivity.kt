package com.example.kadai_mikan

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

/**ホーム画面 View**/
class MainActivity : AppCompatActivity() {
    private val preName = "MAIN_SETTING"
    private var dataInt = 0
    private val handleMainActivity = Handle_MainActivity()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SetUp()
        SetProgressBar()
        var isStart = false
        val startButton = findViewById<Button>(R.id.StartButton)
        startButton.setOnClickListener {
            if(!isStart) {
                isStart = true
                val intent = Intent(this, LearningActivity::class.java)
                startActivity(intent)
            }
        }
    }
    override fun onBackPressed() {
        //中身を空にする ⇒　戻るボタンが使えないようになる
    }
    /**初回起動時のデータベースセットアップ**/
    private fun SetUp(){
        val sharedPreferences = getSharedPreferences(preName, Context.MODE_PRIVATE)
        dataInt = sharedPreferences.getInt("key",0)
        if(dataInt == 0){
        handleMainActivity.SetUp()
        }
        dataInt++
        sharedPreferences.edit().putInt("key",dataInt).apply()
    }
    /**進捗度の横棒のパラメータ取得とプログレスバーへの反映**/
    private fun SetProgressBar(){
        var learningStateList = handleMainActivity.GetProgressBarParameter()
        progressBar.progress = learningStateList[0]
        progressBar.secondaryProgress = learningStateList[0] + learningStateList[1]
    }

}
