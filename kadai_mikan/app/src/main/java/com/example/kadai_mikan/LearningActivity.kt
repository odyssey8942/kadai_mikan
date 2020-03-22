package com.example.kadai_mikan

import android.content.Intent
import android.os.*
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale.ENGLISH
import kotlin.random.Random

/**学習画面 View**/
class LearningActivity() : AppCompatActivity(), OnInitListener, Parcelable {
    private val handleLearningactivity = Handle_LearningActivity()
    private val handler = Handler()
    private var textToSpeech: TextToSpeech? = null
    private var words: Array<Word> = emptyArray()
    private var answerNum = Random.nextInt(4)
    private var countNum = 1
    private val endNum = 10
    private var isCorrect = false
    private var isWait = false
    /**十秒ごとに問題を更新するタスク**/
    private val task = object : Runnable{
        @RequiresApi(LOLLIPOP)
        override fun run() {
            handler.postDelayed(this,10000)
            Over10Second()
        }
    }
    constructor(parcel: Parcel) : this() {
        answerNum = parcel.readInt()
        countNum = parcel.readInt()
        isCorrect = parcel.readByte() != 0.toByte()
    }

    @RequiresApi(LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning)

        textToSpeech = TextToSpeech(this,this)
        doTimerTask()
        Set(countNum)
    }

    @RequiresApi(LOLLIPOP)
    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        textToSpeech?.shutdown()
        super.onDestroy()
    }
    override fun onBackPressed() {
        //中身を空にする ⇒　戻るボタンが使えないようになる
    }
    /**機械音声の初期設定**/
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.let { tts ->
                val locale = ENGLISH
                if (tts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
                    tts.language = ENGLISH
                } else {
                    // 言語の設定に失敗
                }
            }
            //doTimerTask()
        } else {
            // Tts init 失敗
        }
    }
    /**十秒ごとのタスクをハンドラーに渡す**/
    private fun doTimerTask(){
        handler.postDelayed(task,10000)
    }
    /**回答したとき十秒ごとのタスクのカウントをリセット**/
    private fun doTimerReset(){
        handler.removeCallbacks(task)
        doTimerTask()
    }

    @RequiresApi(LOLLIPOP)
    /**ボタンを押したときの処理**/
    private fun OnClickTask(selectNum: Int){
        if(!isWait) {
            isCorrect = handleLearningactivity.CheckAnswer(answerNum, selectNum)
            handleLearningactivity.SetCheckAnswerResult(isCorrect,words[answerNum])
            WaitAndSetUI(isCorrect)
            doTimerReset()
        }
    }
    /**正解、不正解の画像の表示、非表示**/
    private fun handleImage(isCorrect: Boolean,isVisible: Boolean){
        val correctImage = findViewById<ImageView>(R.id.correct_image)
        val incorrectImage = findViewById<ImageView>(R.id.incorrect_image)
        if(isCorrect and isVisible){
            correctImage.bringToFront()
            correctImage.alpha = 1F
        }
        else if(isCorrect and !isVisible) {
            correctImage.alpha = 0F
        }
        else if(!isCorrect and isVisible){
            incorrectImage.bringToFront()
            incorrectImage.alpha = 1F
        }
        else{
            incorrectImage.alpha = 0F
        }
    }

    /**UIのテキストのセットアップと問題の更新**/
    @RequiresApi(LOLLIPOP)
    private fun Set(countNum: Int){

        val countLabel = findViewById<TextView>(R.id.countLabel)
        val questionLabel = findViewById<TextView>(R.id.questionLabel)
        val answerBtn1 = findViewById<Button>(R.id.answerBtn1)
        val answerBtn2 = findViewById<Button>(R.id.answerBtn2)
        val answerBtn3 = findViewById<Button>(R.id.answerBtn3)
        val answerBtn4 = findViewById<Button>(R.id.answerBtn4)
        if(handleLearningactivity.CheckEnd(countNum,endNum)){
            handler.removeCallbacks(task)
            ReturnMainActivity()
            this.finish()
        }
        else {
            answerNum = Random.nextInt(4)
            words = handleLearningactivity.GetWords(handleLearningactivity.MakeRandom())

            countLabel.text = "Q${countNum}"
            questionLabel.text = words[answerNum].word
            answerBtn1.text = words[0].meaning
            answerBtn2.text = words[1].meaning
            answerBtn3.text = words[2].meaning
            answerBtn4.text = words[3].meaning

            val SpeakAfter1Sec = Thread(Runnable {
                try {
                    Thread.sleep(200)
                    handler.post(){
                        textToSpeech?.let { handleLearningactivity.startSpeak(
                            it,
                            words[answerNum].word.toString()
                        ) }
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            })
            SpeakAfter1Sec.start()
            answerBtn1.setOnClickListener {
                OnClickTask(0)
            }
            answerBtn2.setOnClickListener{
                OnClickTask(1)
            }
            answerBtn3.setOnClickListener {
                OnClickTask(2)
            }
            answerBtn4.setOnClickListener{
                OnClickTask(3)
            }
        }
    }
    /**ホーム画面に戻る**/
    private fun ReturnMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    /**十秒経過後の処理**/
    @RequiresApi(LOLLIPOP)
    private fun Over10Second(){
        isCorrect = handleLearningactivity.CheckAnswer(answerNum, -1)
        handleLearningactivity.SetCheckAnswerResult(isCorrect,words[answerNum])
        WaitAndSetUI(isCorrect)
    }
    /**遅延処理を行った後にSetUp関数呼び出し**/
    @RequiresApi(LOLLIPOP)
    private fun WaitAndSetUI(isCorrect: Boolean){
        val handler = Handler(Looper.getMainLooper())
        var waitTime = 0
        if (isCorrect) {
            waitTime = 500
        } else {
            waitTime = 1000
        }
        val thread = Thread(Runnable {
            try {
                isWait = true
                Thread.sleep(waitTime.toLong())
                handler.post(){
                    handleImage(isCorrect,false)
                    Set(++countNum)
                }
                isWait = false
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        })
        handleImage(isCorrect,true)
        thread.start()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(answerNum)
        parcel.writeInt(countNum)
        parcel.writeByte(if (isCorrect) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LearningActivity> {
        override fun createFromParcel(parcel: Parcel): LearningActivity {
            return LearningActivity(parcel)
        }

        override fun newArray(size: Int): Array<LearningActivity?> {
            return arrayOfNulls(size)
        }
    }
}
