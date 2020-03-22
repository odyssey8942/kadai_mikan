package com.example.kadai_mikan

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DB_NAME = "EngrishWordDatabase"
private const val DB_VERSION = 1

/**ホーム画面、学習画面 Model**/
class MyDatabaseOpenHelper(context: Context)
    : SQLiteOpenHelper(context, DB_NAME,null, DB_VERSION){
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("""
            CREATE TABLE EngrishWord(
            _id INTEGER PRIMARY KEY AUTOINCREMENT,
            word TEXT NOT NULL,
            meaning  TEXT NOT NULL,
            state INTEGER NOT NULL);
            """)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //not yet
    }
    /**データベースに単語登録**/
    fun firstCreateDatabase(context: Context){
        val database = MyDatabaseOpenHelper(context).writableDatabase
        val ec = EditCSV()
        var words: Array<Word> = emptyArray()
        words = ec.readCSV("words.csv")
        for(word in words){
            val record = ContentValues().apply {
                put("word",word.word)
                put("meaning",word.meaning)
                put("state",0)
            }
            database.insert("EngrishWord",null,record)
        }
        database.close()
    }
    /**データベースのデータチェック**/
    fun checkDatabase(context: Context){
        val database = MyDatabaseOpenHelper(context).readableDatabase
        val cursor = database.query("EngrishWord",
            null,
            null,
            null,
            null,
            null,
            null,
            null)

        cursor.use{c ->
            while(c.moveToNext()){
                val id = c.getLong(c.getColumnIndex("_id"))
                val word = c.getString(c.getColumnIndex("word"))
                val meaning = c.getString(c.getColumnIndex("meaning"))
                val state = c.getInt(c.getColumnIndex("state"))
                print("${id} : ${word} : ${meaning} : ${state} \n")
            }
        }
        database.close()
    }
    /**データベースから単語テスト用の四つの単語取得**/
    fun getWordsFromDatabase(context: Context,wordIDs: Array<Int>): Array<Word>{
        val database = MyDatabaseOpenHelper(context).readableDatabase
        var words: Array<Word> = emptyArray()
        for (i in 0..3){
            val cursor = database.query("EngrishWord",
                null,
                "_id == ${wordIDs[i]}",
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
                    words += word
                }
            }
        }
        database.close()
        return  words
    }
    /**テストの正解、不正解のパラメータをデータベースに反映**/
    fun SetLearningState(context: Context,isCorrect: Boolean,word: Word){
        val database = MyDatabaseOpenHelper(context).writableDatabase
        var update = ContentValues().apply {
            if(isCorrect) {
                put("state",1)
            }
            else{
                put("state",2)
            }
        }
        database.update("EngrishWord", update, "word like ?", arrayOf(word.word))
        checkDatabase(context)
        database.close()
    }
    /**テストの未回答、正解、不正解の数を取得**/
    fun GetLearningState(context: Context): Array<Int> {
        val database = MyDatabaseOpenHelper(context).readableDatabase
        val returnList = arrayOf(0,0,0)
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
                val learningStateNum = c.getInt(c.getColumnIndex("state"))
                returnList[learningStateNum]++
            }
        }
        println("${returnList[0]} : ${returnList[1]} : ${returnList[2]}")
        database.close()
        return returnList
    }
}