package dk.blekinge.diceroller;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


public class Buckets extends AppCompatActivity {

    private final int[] buckets = new int[6];

    private int dicepool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buckets);


        //Get the intent that started this activity and extract the message string
        Intent intent = getIntent();
        dicepool = intent.getIntExtra(MainActivity.DICEPOOL, 0);

        rollDice(dicepool);

        updateReport();
    }

    private void updateReport() {
        TextView report = (TextView) findViewById(R.id.Status);

        ImageButton[] dices = getDice();

        int selected = 0;
        for (int i = 0; i < dices.length; i++) {
            ImageButton dice = dices[i];
            if (dice.isSelected()){
                selected += buckets[i];
            }
        }


        report.setText("Dice pool: "+dicepool+", selected dice: "+selected);
    }

    private void rollDice(int dicepool) {
        for (int i = 0; i < dicepool; i++) {
            buckets[roll()]+=1;
        }

        TextView count1 = (TextView) findViewById(R.id.count1);
        count1.setText(String.format("%d", buckets[0]));

        TextView count2 = (TextView) findViewById(R.id.count2);
        count2.setText(String.format("%d", buckets[1]));

        TextView count3 = (TextView) findViewById(R.id.count3);
        count3.setText(String.format("%d", buckets[2]));

        TextView count4 = (TextView) findViewById(R.id.count4);
        count4.setText(String.format("%d", buckets[3]));

        TextView count5 = (TextView) findViewById(R.id.count5);
        count5.setText(String.format("%d", buckets[4]));

        TextView count6 = (TextView) findViewById(R.id.count6);
        count6.setText(String.format("%d", buckets[5]));
    }

    private ImageButton[] getDice(){
        return new ImageButton[]{
                (ImageButton) findViewById(R.id.D1),
                (ImageButton) findViewById(R.id.D2),
                (ImageButton) findViewById(R.id.D3),
                (ImageButton) findViewById(R.id.D4),
                (ImageButton) findViewById(R.id.D5),
                (ImageButton) findViewById(R.id.D6)
        };
    }

    int roll() {
        return (int)(6.0 * Math.random());
    }

    public void reroll(View rerollButton){
        ImageButton[] dices = getDice();
        int rerollPool = 0;
        for (int i = 0; i < dices.length; i++) {
            ImageButton dice = dices[i];
            if (dice.isSelected()){
                rerollPool += buckets[i];
                buckets[i] = 0;
                toggleSelect(dice);
            }
        }
        rollDice(rerollPool);
    }

    public void rollon(View rollonButton){
        ImageButton[] dices = getDice();
        dicepool = 0;
        for (int i = 0; i < dices.length; i++) {
            ImageButton dice = dices[i];
            if (dice.isSelected()){
                dicepool += buckets[i];
                toggleSelect(dice);
            }
            buckets[i] = 0;
        }
        rollDice(dicepool);

    }




    public void toggleSelect(View view) {
        if (view instanceof ImageButton) {
            ImageButton imageButton = (ImageButton) view;
            if (imageButton.isSelected()){
                imageButton.setBackgroundColor(Color.WHITE);
                imageButton.setSelected(false);
            } else {
                imageButton.setBackgroundColor(Color.BLUE);
                imageButton.setSelected(true);
            }
            updateReport();
        }

    }
}
