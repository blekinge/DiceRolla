package dk.blekinge.dicerolla

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun RollX(view: View?) {
        val buckets = Intent(this, Buckets::class.java)

        val editText = findViewById<EditText>(R.id.editText)
        val dicePool = editText.getText().toString().toInt()

        buckets.putExtra("buckets", Bucket(dicePool, D6.randomOffset))

        startActivity(buckets)
    }
}
