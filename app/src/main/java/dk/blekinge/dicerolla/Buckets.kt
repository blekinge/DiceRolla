package dk.blekinge.dicerolla;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import kotlin.collections.MapsKt;


public class Buckets extends Activity {

    public static final String BUCKETS = "buckets";
    private SortedMap<D6, Integer> buckets;
    private int dicepool;
    private int randomDicerollIndex;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buckets);

        //Get the intent that started this activity and extract the message string
        Intent intent = getIntent();
        dicepool = intent.getIntExtra(MainActivity.DICEPOOL, 0);
        randomDicerollIndex = intent.getIntExtra(MainActivity.RANDOM_DICEROLL_INDEX, 0);
        buckets = MapsKt.toSortedMap(Optional.ofNullable(
                                  (Map<D6, Integer>) intent.getSerializableExtra(BUCKETS))
                          .orElseGet(() -> Map.of(
                                  D6.R1, 0,
                                  D6.R2, 0,
                                  D6.R3, 0,
                                  D6.R4, 0,
                                  D6.R5, 0,
                                  D6.R6, 0)));

        var switched = Collections.synchronizedMap(new HashMap<>(Map.of(
                D6.R1.imageButtonId, false,
                D6.R2.imageButtonId, false,
                D6.R3.imageButtonId, false,
                D6.R4.imageButtonId, false,
                D6.R5.imageButtonId, false,
                D6.R6.imageButtonId, false)));

        ViewGroup diceView = findViewById(R.id.Dice);
        diceView.setOnTouchListener((v, event) -> {
            int x = (int) (event.getX());
            int y = (int) (event.getY());

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                switched.replaceAll((imageButton, aBoolean) -> false);

            }

            ImageButton touchedButton = getTouchedButton(diceView, x, y);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                swiped(switched, touchedButton);
            }

            if (touchedButton != null && event.getAction() == MotionEvent.ACTION_MOVE) {
                swiped(switched, touchedButton);
            }
            return true;
        });


        rollDice(dicepool, "Rolled " + dicepool + " dice BUCKETS");

        updateReport();
    }

    public void reroll(View rerollButton) {
        Intent rerollIntent = new Intent(this, Buckets.class);

        var newBuckets = new TreeMap<>(buckets);
        int selectedRerollPool =
                Arrays.stream(D6.values())
                      .map(this::imageButton)
                      .filter(View::isSelected)
                      .peek(this::toggleSelect)
                      .map(View::getId)
                      .map(D6::fromImageButtonId)
                      .mapToInt(d6 -> newBuckets.replace(d6, 0))
                      .sum();

        rerollIntent.putExtra(MainActivity.DICEPOOL, selectedRerollPool);
        rerollIntent.putExtra(MainActivity.RANDOM_DICEROLL_INDEX, randomDicerollIndex);
        rerollIntent.putExtra(BUCKETS, newBuckets);
        startActivity(rerollIntent);

//        rollDice(selectedRerollPool, getString(R.string.rerolled_message, selected, selectedRerollPool));
    }

    public void rollon(View rollonButton) {

        Intent rollOnIntent = new Intent(this, Buckets.class);

        int selectedDicepool = Arrays.stream(D6.values())
                                     .map(this::imageButton)
                                     .filter(View::isSelected)
                                     .peek(this::toggleSelect)
                                     .map(View::getId)
                                     .map(D6::fromImageButtonId)
                                     .mapToInt(d6 -> buckets.getOrDefault(d6, 0))
                                     .sum();

        rollOnIntent.putExtra(MainActivity.DICEPOOL, selectedDicepool);
        rollOnIntent.putExtra(MainActivity.RANDOM_DICEROLL_INDEX, randomDicerollIndex);
        startActivity(rollOnIntent);
//        rollDice(selectedDicepool, getString(R.string.rolled_on_message, selected, selectedDicepool));
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


    private void swiped(Map<Integer, Boolean> switched, ImageButton imageButton) {
        if (imageButton != null &&
            Objects.equals(Boolean.FALSE, switched.get(imageButton.getId()))) {
            toggleSelect(imageButton);
            switched.replace(imageButton.getId(), true);
        }
    }

    @Nullable
    private ImageButton getTouchedButton(ViewGroup bucketsView, int x, int y) {
        var bounds = new Rect(0, 0, 0, 0);
        return Arrays.stream(D6.values())
                     .map(this::imageButton)
                     .filter(imageButton -> {
                         imageButton.getBackground().copyBounds(bounds);
                         bucketsView.offsetDescendantRectToMyCoords(imageButton, bounds);
                         return bounds.contains(x, y);
                     })
                     .findFirst()
                     .orElse(null);
    }

    private void resetDicerollsToZero() {
        Arrays.stream(D6.values())
              .peek(d6 -> buckets.replace(d6, 0))
              .forEach(d6 -> label(d6).setText(formatD6(buckets.get(d6))));
    }

    @NonNull
    private String formatD6(Integer value) {
        return String.format(Locale.getDefault(), "%s", value == 0 ? "-" : value);
    }

    @NonNull
    private String formatD6Roll(Integer addition, Integer value) {
        return String.format(Locale.getDefault(), "%s\n%s",
                             addition == 0 ? " " : ("+" + addition),
                             value == 0 ? "-" : value);
    }

    private void rollDice(int dicepool, String message) {
        Map<D6, Integer> rolls = roll(dicepool)
                .collect(Collectors.toMap(Function.identity(), x -> 1, Integer::sum));

        Arrays.stream(D6.values())
              .peek(d6 -> rolls.computeIfAbsent(d6, i -> 0))
              .peek(d6 -> label(d6).setText(formatD6Roll(rolls.get(d6), buckets.get(d6))))
              .forEach(d6 -> buckets.merge(d6, Optional.ofNullable(rolls.get(d6)).orElse(0), Integer::sum));

        updateReport();
        // Set a delayed task to wait for 2 seconds before proceeding
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Continue with further actions after waiting
            Arrays.stream(D6.values())
                  .forEach(d6 -> label(d6).setText(formatD6(buckets.get(d6))));
            log(message.replaceAll("BUCKETS", buckets.toString()));
        }, 2000); // 2000 milliseconds = 2 seconds


    }

    @NonNull
    private Stream<D6> roll(int dicepool) {
        return D6.randomDicerolls.subList(randomDicerollIndex, randomDicerollIndex += dicepool)
                                 .stream();
    }

    private void updateReport() {
        TextView report = findViewById(R.id.Status);

        int selected = Arrays.stream(D6.values())
                             .filter(d6 -> imageButton(d6).isSelected())
                             .mapToInt(d6 -> buckets.getOrDefault(d6, 0))
                             .sum();

        findViewById(R.id.Reroll_button).setEnabled(selected > 0);
        findViewById(R.id.Rollon_button).setEnabled(selected > 0);

        report.setText(getString(R.string.dice_pool, dicepool, selected));
    }

    private ImageButton imageButton(D6 d6) {
        return findViewById(d6.imageButtonId);
    }

    private TextView label(D6 d6) {
        return findViewById(d6.labelId);
    }

    private void log(String action) {
        TextView log = findViewById(R.id.Log);
        log.append("\n" + action);
    }

}
