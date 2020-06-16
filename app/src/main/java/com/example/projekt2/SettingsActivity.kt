package com.example.projekt2

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter


class SettingsActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        var legosettings = load()
        editURL.setText(legosettings[0])
        if(legosettings[1] == "SHOW"){
            switch1.isChecked = true
        }



    }

    //SAVE APP PREFERENCES
    fun save(v: View) {
        try{
            val filename = "legosettings.txt"
            val file = OutputStreamWriter(openFileOutput(filename, Context.MODE_PRIVATE))
            var show_archives : String = "HIDE"
            if(switch1.isChecked){
                show_archives = "SHOW"
            }
            else{
                show_archives = "HIDE"
            }
            var notes : String = editURL.text.toString() + "\n" + show_archives

            file.write(notes)
            file.flush()
            file.close()
            Toast.makeText(this,"Preferences saved!", Toast.LENGTH_SHORT).show()
        }
        catch (e: Exception) {
            Toast.makeText(this,"Saving preferences failed!", Toast.LENGTH_SHORT).show()
        }
    }

    //READ PREFERENCES FROM FILE
    fun load():Array<String> {
        var archived = "HIDE"
        var urlprefix = "http://fcds.cs.put.poznan.pl/MyWeb/BL/"
        try {
            val filename = "legosettings.txt"
            val file = InputStreamReader(openFileInput(filename))
            val br = BufferedReader(file)
            urlprefix = br.readLine().toString()
            archived = br.readLine().toString()
            file.close()
        }
        catch(e: Exception){

        }
        return arrayOf(urlprefix,archived)
    }

}



