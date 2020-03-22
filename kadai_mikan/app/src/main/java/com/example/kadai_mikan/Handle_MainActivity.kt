package com.example.kadai_mikan

/**ホーム画面 ViewModel**/
class Handle_MainActivity {
    private val singletonContext = SingletonContext.applicationContext()
    private val myDatabaseOpenHelper = MyDatabaseOpenHelper(singletonContext)
    /**データベースにCSVの単語と意味を登録**/
    fun SetUp(){
        myDatabaseOpenHelper.firstCreateDatabase(singletonContext)
        //myDatabaseOpenHelper.checkDatabase(singletonContext)
    }
    /**データベースから進捗度のパラメータを取得**/
    fun GetProgressBarParameter(): Array<Int> {
        var returnList = arrayOf(0,0,0)
        returnList = myDatabaseOpenHelper.GetLearningState(singletonContext)
        return returnList
    }
}