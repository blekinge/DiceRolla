package dk.blekinge.diceroller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import static android.R.id.message;
import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    public static final String DICEPOOL = "dicepool";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void RollX(View view) {
        Intent intent = new Intent(this, Buckets.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        int dicePool = Integer.parseInt(editText.getText().toString());

        intent.putExtra(DICEPOOL, dicePool);
        startActivity(intent);

    }
}
