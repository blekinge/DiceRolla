package dk.blekinge.dicerolla;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Buckets extends Activity {

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

        log("Rolled "+dicepool+" dice "+Arrays.toString(buckets));

        updateReport();
    }

    private void updateReport() {
        TextView report = findViewById(R.id.Status);

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

        TextView count1 = findViewById(R.id.count1);
        count1.setText(String.format("%d", buckets[0]));

        TextView count2 = findViewById(R.id.count2);
        count2.setText(String.format("%d", buckets[1]));

        TextView count3 = findViewById(R.id.count3);
        count3.setText(String.format("%d", buckets[2]));

        TextView count4 = findViewById(R.id.count4);
        count4.setText(String.format("%d", buckets[3]));

        TextView count5 = findViewById(R.id.count5);
        count5.setText(String.format("%d", buckets[4]));

        TextView count6 = findViewById(R.id.count6);
        count6.setText(String.format("%d", buckets[5]));
    }

    private ImageButton[] getDice(){
        return new ImageButton[]{
                findViewById(R.id.D1),
                findViewById(R.id.D2),
                findViewById(R.id.D3),
                findViewById(R.id.D4),
                findViewById(R.id.D5),
                findViewById(R.id.D6)
        };
    }

    int roll() {
        return (int)(6.0 * Math.random());
    }

    public void reroll(View rerollButton){
        ImageButton[] dices = getDice();
        int rerollPool = 0;

        List<Integer> selected = new ArrayList<>();
        for (int i = 0; i < dices.length; i++) {
            ImageButton dice = dices[i];
            if (dice.isSelected()){
                rerollPool += buckets[i];
                buckets[i] = 0;
                toggleSelect(dice);
                selected.add(i+1);
            }
        }
        rollDice(rerollPool);

        log("Rerolled "+ selected + "="+rerollPool+" dice: "+Arrays.toString(buckets));

    }

    public void rollon(View rollonButton){
        ImageButton[] dices = getDice();
        dicepool = 0;
        List<Integer> selected = new ArrayList<>();
        for (int i = 0; i < dices.length; i++) {
            ImageButton dice = dices[i];
            if (dice.isSelected()){
                dicepool += buckets[i];
                toggleSelect(dice);
                selected.add(i+1);
            }
            buckets[i] = 0;
        }
        rollDice(dicepool);
        log("Rolled on with "+ selected + "="+dicepool+" dice: "+Arrays.toString(buckets));

    }




    public void toggleSelect(View view) {
        if (view instanceof ImageButton imageButton) {
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

    private void log(String action){
        TextView log = findViewById(R.id.Log);
        log.append("\n"+action);
    }

}
