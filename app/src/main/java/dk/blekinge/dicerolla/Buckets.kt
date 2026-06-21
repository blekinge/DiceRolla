package dk.blekinge.dicerolla

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import java.io.Serializable
import java.lang.Boolean
import java.util.Collections
import java.util.Locale
import java.util.Map
import java.util.SortedMap
import java.util.TreeMap
import kotlin.Int
import kotlin.String
import kotlin.let

private class BucketPojo : Serializable {
    val dice: SortedMap<D6, Int>;
    val dicepool: Int;
    var randomDicerollIndex: Int;

    constructor(dice: SortedMap<D6, Int>, dicepool: Int, randomDicerollIndex: Int) {
        this.dice = dice
        this.dicepool = dicepool
        this.randomDicerollIndex = randomDicerollIndex
    }

    constructor(dicepool: Int, randomDicerollIndex: Int) : this(
        dice = sortedMapOf(
            Pair(D6.R1, 0),
            Pair(D6.R2, 0),
            Pair(D6.R3, 0),
            Pair(D6.R4, 0),
            Pair(D6.R5, 0),
            Pair(D6.R6, 0)
        ),
        dicepool = dicepool,
        randomDicerollIndex = randomDicerollIndex
    ) {
    }

    constructor() : this(
        dicepool = 0,
        randomDicerollIndex = 0
    ) {
    }
}

class Buckets : Activity() {
    private lateinit var bucket: BucketPojo


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buckets)

        //Get the intent that started this activity and extract the message string
        val intent = getIntent()

        bucket = intent.getSerializableExtra(BUCKETS, BucketPojo::class.java) ?: BucketPojo()


        val switched = Collections.synchronizedMap<Int?, kotlin.Boolean?>(
            HashMap<Int?, kotlin.Boolean?>(
                Map.of<Int?, kotlin.Boolean?>(
                    D6.R1.imageButtonId, false,
                    D6.R2.imageButtonId, false,
                    D6.R3.imageButtonId, false,
                    D6.R4.imageButtonId, false,
                    D6.R5.imageButtonId, false,
                    D6.R6.imageButtonId, false
                )
            )
        )

        val diceView = findViewById<ViewGroup>(R.id.Dice)
        diceView.setOnTouchListener { _: View?, event: MotionEvent? ->
            val x = (event!!.x).toInt()
            val y = (event.y).toInt()

            if (event.action == MotionEvent.ACTION_DOWN) {
                switched.replaceAll { _: Int?, _: kotlin.Boolean? -> false }
            }

            val touchedButton = getTouchedButton(diceView, x, y)
            if (event.action == MotionEvent.ACTION_UP) {
                swiped(switched, touchedButton)
            }

            if (touchedButton != null && event.action == MotionEvent.ACTION_MOVE) {
                swiped(switched, touchedButton)
            }
            true
        }


        rollDice(bucket.dicepool, "Rolled " + bucket?.dicepool + " dice BUCKETS")

        updateReport()
    }

    fun reroll(rerollButton: View?) {
        val rerollIntent = Intent(this, Buckets::class.java)

        val newBuckets = TreeMap(bucket.dice)
        val selectedRerollPool =
            D6.entries.stream()
                .toList()
                .asSequence()
                .mapNotNull { d6: D6 -> this.imageButton(d6) }
                .filter { obj -> obj.isSelected }
                .onEach { view -> this.toggleSelect(view) }
                .map { D6.fromImageButtonId(it.id) }
                .map { d6: D6 -> newBuckets.replace(d6, 0) ?: 0 }
                .sum()

        rerollIntent.putExtra(
            BUCKETS,
            BucketPojo(newBuckets, selectedRerollPool, bucket.randomDicerollIndex)
        )
        startActivity(rerollIntent)
    }

    fun rollon(rollonButton: View?) {
        val rollOnIntent = Intent(this, Buckets::class.java)

        val selectedDicepool = D6.entries.stream().toList()
            .asSequence()
            .mapNotNull { this.imageButton(it) }
            .filter { it.isSelected }
            .onEach { this.toggleSelect(it) }
            .map { D6.fromImageButtonId(it.id) }
            .map { bucket.dice[it] ?: 0 }
            .sum()

        rollOnIntent.putExtra(
            BUCKETS,
            BucketPojo(
                dicepool = selectedDicepool,
                randomDicerollIndex = bucket.randomDicerollIndex
            )
        )

        startActivity(rollOnIntent)
    }

    fun toggleSelect(view: View?) {
        if (view is ImageButton) {
            if (view.isSelected) {
                view.setBackgroundColor(Color.WHITE)
                view.setSelected(false)
            } else {
                view.setBackgroundColor(Color.BLUE)
                view.setSelected(true)
            }
            updateReport()
        }
    }


    private fun swiped(switched: MutableMap<Int?, kotlin.Boolean?>, imageButton: ImageButton?) {
        if ((imageButton != null) && (Boolean.FALSE == switched[imageButton.id])) {
            toggleSelect(imageButton)
            switched.replace(imageButton.id, true)
        }
    }

    private fun getTouchedButton(bucketsView: ViewGroup, x: Int, y: Int): ImageButton? {
        val bounds = Rect(0, 0, 0, 0)
        return D6.entries.stream()
            .toList()
            .asSequence()
            .mapNotNull { this.imageButton(it) }
            .firstOrNull {
                it.background.copyBounds(bounds)
                bucketsView.offsetDescendantRectToMyCoords(it, bounds)
                bounds.contains(x, y)
            }
    }

    private fun formatD6(value: Int): String {
        return String.format(
            locale = Locale.getDefault(),
            format = "%s",
            when {
                value == 0 -> "-"
                else -> value
            }
        )
    }

    private fun formatD6Roll(addition: Int, value: Int): String {
        return String.format(
            locale = Locale.getDefault(),
            format = "%s\n%s",
            when {
                addition == 0 -> " "
                else -> ("+$addition")
            },
            when {
                value == 0 -> "-"
                else -> value
            }
        )
    }

    private fun rollDice(dicepool: Int, message: String) {
        val rolls = roll(dicepool)
            .groupingBy { it }
            .fold(0, { acc, e -> acc + 1 })


        D6.entries.toList()
            .onEach { d6: D6 ->
                label(d6)?.text = formatD6Roll(
                    addition = rolls[d6] ?: 0, value = bucket.dice[d6] ?: 0
                )
            }
            .forEach { d6: D6 ->
                bucket.dice.merge(d6, rolls[d6] ?: 0) { a, b -> a + b }
            }

        updateReport()
        // Set a delayed task to wait for 2 seconds before proceeding
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            // Continue with further actions after waiting
            D6.entries.toList()
                .forEach { d6: D6 -> label(d6)?.text = formatD6(bucket.dice[d6]!!) }
            log(message.replace("BUCKETS".toRegex(), bucket.dice.toString()))
        }, 2000) // 2000 milliseconds = 2 seconds
    }

    private fun roll(dicepool: Int): List<D6> {
        return D6.randomDicerolls.subList(
            bucket.randomDicerollIndex,
            dicepool.let { bucket.randomDicerollIndex += it; bucket.randomDicerollIndex })

    }

    private fun updateReport() {
        val report = findViewById<TextView>(R.id.Status)

        val selected = D6.entries.toList()
            .filter { d6: D6 -> imageButton(d6)?.isSelected ?: false }
            .map { d6: D6 -> bucket.dice[d6] ?: 0 }
            .sum()

        findViewById<View>(R.id.Reroll_button).setEnabled(selected > 0)
        findViewById<View>(R.id.Rollon_button).setEnabled(selected > 0)

        report.text = getString(R.string.dice_pool, bucket.dicepool, selected)
    }

    private fun imageButton(d6: D6): ImageButton? {
        return findViewById(d6.imageButtonId)
    }

    private fun label(d6: D6): TextView? {
        return findViewById(d6.labelId)
    }

    private infix fun log(action: String?) {
        findViewById<TextView>(R.id.Log).apply {
            append("\n")
            append(action)
        }
    }

    companion object {
        const val BUCKETS: String = "buckets"
    }
}
