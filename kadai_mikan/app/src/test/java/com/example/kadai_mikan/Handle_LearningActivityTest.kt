package com.example.kadai_mikan

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Handle_LearningActivityTest {
    private val context: Context = InstrumentationRegistry.getInstrumentation().context
    private val handleLearningactivity = Handle_LearningActivity()
    private val myDatabaseOpenHelper = MyDatabaseOpenHelper(context)

    private var words: Array<Word> = emptyArray()

    @Before
    fun setUp() {
        myDatabaseOpenHelper.firstCreateDatabase(context)
        val ec = EditCSV()
        words = ec.readCSV("words.csv")
    }

    @Test
    fun checkAnswerTest() {
        for (i in 0..3){
            for (j in 0..3){
                if(i == j) {
                    assertEquals(handleLearningactivity.CheckAnswer(i,j),true)
                }
                else{
                    assertEquals(handleLearningactivity.CheckAnswer(i,j),false)
                }
            }
        }
    }

    @Test
    fun checkEndTest() {
        val endNum = 10
        for (i in 1..endNum+1){
            if(i <= endNum) {
                assertEquals(handleLearningactivity.CheckEnd(i, endNum), false)
            }
            else{
                assertEquals(handleLearningactivity.CheckEnd(i, endNum), true)
            }
        }

    }

    @Test
    fun MakeRandomTest(){
        var randomList = arrayOf(0,0,0,0)
        var isSame = false
        var is0to100 = false
        for (i in 0..100){
            randomList = handleLearningactivity.MakeRandom()
            for(j in 0..3){
                if(1 <= randomList[j] && randomList[j] <= 100){
                    is0to100 = true
                }
                else{
                    is0to100 = false
                }
                assertEquals(true,is0to100)
                for(k in 0..3){
                    if(j != k && randomList[j] == randomList[k]){
                        isSame = true
                    }
                    else{
                        isSame = false
                    }
                    assertEquals(false,isSame)
                }
            }
        }
    }
    @Test
    fun SetCheckAnswerResultTest(){
        val database = MyDatabaseOpenHelper(context).readableDatabase
        val cursor = database.query("EngrishWord",
            null,
            null,
            null,
            null,
            null,
            null,
            null)
        for(i in 0..words.size - 1){
            handleLearningactivity.SetCheckAnswerResult(true,words[i])
        }
        cursor.use{c ->
            while(c.moveToNext()){
                val state = c.getInt(c.getColumnIndex("state"))
                assertEquals(1,state)
            }
        }
        for(element in words){
            handleLearningactivity.SetCheckAnswerResult(false, element)
        }
        cursor.use{c ->
            while(c.moveToNext()){
                val state = c.getInt(c.getColumnIndex("state"))
                assertEquals(2,state)
            }
        }
        database.close()
    }

    @Test
    fun getWordsTest() {
        var word_4: Array<Word> = emptyArray()
        var wordIDs = arrayOf(1,2,3,4)
        var isGet = false
        for(i in 0..24){
            word_4 = handleLearningactivity.GetWords(wordIDs)
            for(j in 0..3){
                if(word_4[j].word != null){
                    isGet = true
                }
                assertEquals(true,isGet)
                println(wordIDs[j])
                wordIDs[j] += 4
            }
        }
    }
}