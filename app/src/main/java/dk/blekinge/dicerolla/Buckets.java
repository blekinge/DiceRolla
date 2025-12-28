package dk.blekinge.dicerolla;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import kotlin.collections.MapsKt;


public class Buckets extends Activity {

    private final Map<D6, Integer> buckets = new TreeMap<>();

    private int dicepool;

    private final Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buckets);


        //Get the intent that started this activity and extract the message string
        Intent intent = getIntent();
        dicepool = intent.getIntExtra(MainActivity.DICEPOOL, 0);

        buckets.putAll(MapsKt.toSortedMap(Map.of(D6.R1, 0,
                D6.R2, 0,
                D6.R3, 0,
                D6.R4, 0,
                D6.R5, 0,
                D6.R6, 0)));


        rollDice(dicepool, "Rolled " + dicepool + " dice BUCKETS");

        updateReport();
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

        rollDice(rerollPool, "Rerolled " + selected + "=" + rerollPool + " dice: BUCKETS");
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

        rollDice(dicepool, "Rolled on with " + selected + "=" + dicepool + " dice: BUCKETS");
    }

    private void rollDice(int dicepool, String message) {

        Map<D6, Integer> rolls = IntStream.range(0, dicepool)
                .mapToObj(ignored -> roll())
                .collect(Collectors.toMap(Function.identity(), x -> 1, Integer::sum));

        rolls.forEach((key, value) -> ((TextView) (findViewById(key.labelId)))
                .setText(String.format(Locale.getDefault(), "+%d\n%d", value, buckets.get(key))));

        // Set a delayed task to wait for 2 seconds before proceeding
        handler.postDelayed(() -> {
            // Continue with further actions after waiting
            Arrays.stream(D6.values())
                    .peek(d6 -> buckets.merge(d6, rolls.getOrDefault(d6, 0), Integer::sum))
                    .forEach(d6 -> ((TextView) (findViewById(d6.labelId)))
                            .setText(String.format(Locale.getDefault(), "%d", buckets.get(d6))));
            log(message.replaceAll("BUCKETS", buckets.toString()));
        }, 2000); // 2000 milliseconds = 2 seconds


    }

    D6 roll() {
        return D6.values()[(int) (6.0 * Math.random())];
    }


    private void updateReport() {
        TextView report = findViewById(R.id.Status);

        int selected = Arrays.stream(D6.values())
                .filter(d6 -> findViewById(d6.imageButtonId).isSelected())
                .mapToInt(d6 -> buckets.getOrDefault(d6, 0))
                .sum();
        report.setText("Dice pool: " + dicepool + ", selected dice: " + selected);
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
