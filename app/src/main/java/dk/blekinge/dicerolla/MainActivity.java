package dk.blekinge.dicerolla;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

    public static final String DICEPOOL = "dicepool";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void RollX(View view) {
        Intent intent = new Intent(this, Buckets.class);
        EditText editText = findViewById(R.id.editText);
        int dicePool = Integer.parseInt(editText.getText().toString());

        intent.putExtra(DICEPOOL, dicePool);
        startActivity(intent);

    }
}
