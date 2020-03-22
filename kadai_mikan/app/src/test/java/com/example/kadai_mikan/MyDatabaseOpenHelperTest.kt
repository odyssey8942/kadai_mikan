package com.example.kadai_mikan

import android.content.ContentValues
import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MyDatabaseOpenHelperTest {
    private val context: Context = InstrumentationRegistry.getInstrumentation().context
    private val myDatabaseOpenHelper = MyDatabaseOpenHelper(context)

    private var words: Array<Word> = emptyArray()
    @Before
    fun setUp() {
        myDatabaseOpenHelper.firstCreateDatabase(context)
        val ec = EditCSV()
        words = ec.readCSV("words.csv")
    }
    /**
     * データベースにデータを登録するテスト
    @Test
    fun firstCreateDatabaseTest() {
        myDatabaseOpenHelper.firstCreateDatabase(context)
        val database = MyDatabaseOpenHelper(context).readableDatabase
        var actualWords: Array<Word> = emptyArray()
        val cursor = database.query("EngrishWord",
            null,
            null,
            null,
            null,
            null,
            null,
            null)
        cursor.use{c ->
            while(c.moveToNext()) {
                val word = Word(
                    word = c.getString(c.getColumnIndex("word")),
                    meaning = c.getString(c.getColumnIndex("meaning"))
                )
                println("${word.word} : ${word.meaning}")
                actualWords += word
            }
        }
        assertEquals(words.size,actualWords.size)
        for(i in 0..actualWords.size-1) {
            assertNotEquals(null,actualWords[i].word)
            assertNotEquals(null,actualWords[i].meaning)
        }
    }
    **/

    @Test
    fun getWordsFromDatabaseTest() {
        //myDatabaseOpenHelper.firstCreateDatabase(context)
        var words: Array<Word> = emptyArray()
        var wordIDs = arrayOf(1,2,3,4)
        for(i in 0..24) {
            words = myDatabaseOpenHelper.getWordsFromDatabase(context, wordIDs)
            assertEquals(4,words.size)
            for(j in 0..3){
                assertNotEquals(null,words[j].word)
                assertNotEquals(null,words[j].meaning)
                wordIDs[j] += 4
            }
        }
        /*
        wordIDs = arrayOf(1,2,3,4)
        words = myDatabaseOpenHelper.getWordsFromDatabase(context,wordIDs)
        assertEquals("premium",words[0].word)
        assertEquals("memorandum",words[1].word)
        assertEquals("directory",words[2].word)
        assertEquals("exactitude",words[3].word)
        */
    }

    @Test
    fun setLearningStateTest() {
        //myDatabaseOpenHelper.firstCreateDatabase(context)
        val database = MyDatabaseOpenHelper(context).readableDatabase
        val cursor = database.query("EngrishWord",
            null,
            null,
            null,
            null,
            null,
            null,
            null)
        for(i in 0..words.size-1) {
            myDatabaseOpenHelper.SetLearningState(context, true, words[i])
        }
        cursor.use{c ->
            while(c.moveToNext()) {
                val learningStateNum = c.getInt(c.getColumnIndex("state"))
                assertEquals(1,learningStateNum)
            }
        }
        for(i in 0..words.size-1) {
            myDatabaseOpenHelper.SetLearningState(context, false, words[i])
        }
        cursor.use{c ->
            while(c.moveToNext()) {
                val learningStateNum = c.getInt(c.getColumnIndex("state"))
                assertEquals(2,learningStateNum)
            }
        }
    }

    @Test
    fun getLearningStateTest() {
        //myDatabaseOpenHelper.firstCreateDatabase(context)
        var stateList = arrayOf(0,0,0)
        val database = MyDatabaseOpenHelper(context).writableDatabase
        var update = ContentValues().apply {
            put("state",1)
        }
        for(i in 0..words.size-1) {
            database.update("EngrishWord", update, "word like ?", arrayOf(words[i].word))
        }
        stateList = myDatabaseOpenHelper.GetLearningState(context)
        assertEquals(arrayOf(0,100,0),stateList)

        update = ContentValues().apply {
            put("state",2)
        }
        for(i in 0..words.size-1) {
            database.update("EngrishWord", update, "word like ?", arrayOf(words[i].word))
        }
        stateList = myDatabaseOpenHelper.GetLearningState(context)
        assertEquals(arrayOf(0,0,100),stateList)

        update = ContentValues().apply {
            put("state",0)
        }
        for(i in 0..words.size-1) {
            database.update("EngrishWord", update, "word like ?", arrayOf(words[i].word))
        }
        stateList = myDatabaseOpenHelper.GetLearningState(context)
        assertEquals(arrayOf(100,0,0),stateList)

        database.close()
    }
}