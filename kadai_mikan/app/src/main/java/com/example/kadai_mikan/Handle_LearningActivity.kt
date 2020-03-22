package com.example.kadai_mikan

import android.os.Build
import android.speech.tts.TextToSpeech
import androidx.annotation.RequiresApi
import java.util.*
import kotlin.collections.ArrayList

/**学習画面 ViewModel**/
class Handle_LearningActivity {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    /**機械音声再生**/
    fun startSpeak(textToSpeech: TextToSpeech, text: String){
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
    }
    /**正解不正解の判定**/
    fun CheckAnswer(answerNum: Int, selectNum: Int): Boolean{
        return answerNum == selectNum
    }
    /**問題が十問終わったかの判定**/
    fun CheckEnd(countNum:Int,endNum:Int):Boolean{
        return countNum > endNum
    }
    /**回答の結果(正解、不正解)をデータベースに反映**/
    fun SetCheckAnswerResult(isCorrect:Boolean,word: Word){
        val singletonContext = SingletonContext.applicationContext()
        val database = MyDatabaseOpenHelper(singletonContext)
        database.SetLearningState(singletonContext,isCorrect,word)
    }
    /**四択の単語を選択するためのID生成**/
    fun MakeRandom(): Array<Int> {
        val list = ArrayList<Int>()
        val returnList = arrayOf(0,0,0,0)

        // listに値を入れる。この段階では昇順
        for (i in 1..100) {
            list.add(i)
        }
        // シャッフルして、順番を変える
        Collections.shuffle(list)
        for (i in 0..3){
            returnList[i] = list[i]
            println("${i} : ${returnList[i]}")
        }
        return  returnList
    }
    /**単語を四つ取得**/
    fun GetWords(wordIDs: Array<Int>): Array<Word> {
        val singletonContext = SingletonContext.applicationContext()
        val database = MyDatabaseOpenHelper(singletonContext)
        var words: Array<Word> = emptyArray()
        words = database.getWordsFromDatabase(singletonContext,wordIDs)
        return words
    }
}