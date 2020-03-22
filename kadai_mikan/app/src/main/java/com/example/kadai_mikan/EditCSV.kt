package com.example.kadai_mikan

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class EditCSV{
    var words: Array<Word> = emptyArray()
    private var singletonContext = SingletonContext.applicationContext()
    fun readCSV(filename: String): Array<Word>{
        try{

            val file = singletonContext.resources.assets.open(filename)
            val fileReader = BufferedReader(InputStreamReader(file))
            var i = 0
            fileReader.forEachLine {
                if(it.isNotBlank()){
                    val line = it.split(",").toTypedArray()
                    fetchCSV(line)
                }
                i++;
            }
        }catch (e: IOException){
            print(e)
        }
        return words
    }
    private fun fetchCSV(line: Array<String>){
        val word = Word(
            word = line[0],
            meaning = line[1]
        )
        //print("${word.word} : ${word.meaning} \n")
        words += word
        //print("aaa${words[0].word} : ${words[0].meaning} \n")
    }
}