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
import java.util.Map;
import java.util.TreeMap;

import kotlin.collections.MapsKt;


public class Buckets extends Activity {

    private final Map<D6, Integer> buckets = new TreeMap<>();

    private int dicepool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buckets);


        //Get the intent that started this activity and extract the message string
        Intent intent = getIntent();
        dicepool = intent.getIntExtra(MainActivity.DICEPOOL, 0);

        buckets.putAll(MapsKt.toSortedMap(Map.of(D6.R1, 0, D6.R2, 0, D6.R3, 0, D6.R4, 0, D6.R5, 0, D6.R6,0)));

        rollDice(dicepool);

        log("Rolled " + dicepool + " dice " + buckets);

        updateReport();
    }

    private void updateReport() {
        TextView report = findViewById(R.id.Status);

        int selected = Arrays.stream(D6.values())
                .filter(d6 -> findViewById(d6.imageButtonId).isSelected())
                .mapToInt(d6 -> buckets.getOrDefault(d6, 0))
                .sum();
        report.setText("Dice pool: " + dicepool + ", selected dice: " + selected);
    }

    private void rollDice(int dicepool) {
        for (int i = 0; i < dicepool; i++) {
            buckets.merge(roll(), 1, Integer::sum);
        }

        TextView count1 = findViewById(R.id.count1);
        count1.setText(String.format("%d", buckets.get(D6.R1)));

        TextView count2 = findViewById(R.id.count2);
        count2.setText(String.format("%d", buckets.get(D6.R2)));

        TextView count3 = findViewById(R.id.count3);
        count3.setText(String.format("%d", buckets.get(D6.R3)));

        TextView count4 = findViewById(R.id.count4);
        count4.setText(String.format("%d", buckets.get(D6.R4)));

        TextView count5 = findViewById(R.id.count5);
        count5.setText(String.format("%d", buckets.get(D6.R5)));

        TextView count6 = findViewById(R.id.count6);
        count6.setText(String.format("%d", buckets.get(D6.R6)));
    }

    D6 roll() {
        return D6.values()[(int) (6.0 * Math.random())];
    }

    public void reroll(View rerollButton) {
        List<D6> selected = new ArrayList<>();

        int rerollPool = Arrays.stream(D6.values())
                .map(d6 -> (ImageButton) findViewById(d6.imageButtonId))
                .filter(View::isSelected)
                .peek(this::toggleSelect)
                .map(View::getId)
                .map(D6::fromImageButtonId)
                .peek(selected::add)
                .mapToInt(d6 -> buckets.replace(d6, 0))
                .sum();

        rollDice(rerollPool);

        log("Rerolled " + selected + "=" + rerollPool + " dice: " + buckets);

    }

    public void rollon(View rollonButton) {
        List<D6> selected = new ArrayList<>();

        int dicepool = Arrays.stream(D6.values())
                .map(d6 -> (ImageButton) findViewById(d6.imageButtonId))
                .filter(View::isSelected)
                .peek(this::toggleSelect)
                .map(View::getId)
                .map(D6::fromImageButtonId)
                .peek(selected::add)
                .mapToInt(d6 -> buckets.getOrDefault(d6, 0))
                .sum();

        buckets.replaceAll((d6, integer) -> 0);

        rollDice(dicepool);

        log("Rolled on with " + selected + "=" + dicepool + " dice: " + buckets);

    }


    public void toggleSelect(View view) {
        if (view instanceof ImageButton imageButton) {
            if (imageButton.isSelected()) {
                imageButton.setBackgroundColor(Color.WHITE);
                imageButton.setSelected(false);
            } else {
                imageButton.setBackgroundColor(Color.BLUE);
                imageButton.setSelected(true);
            }
            updateReport();
        }

    }

    private void log(String action) {
        TextView log = findViewById(R.id.Log);
        log.append("\n" + action);
    }

}
