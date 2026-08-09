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
import java.util.Collections
import java.util.Locale
import java.util.TreeMap


class Buckets : Activity() {
    private lateinit var bucket: Bucket


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buckets)

        //Retrieve the Dicebucket from the state or create a new
        bucket = getIntent().getSerializableExtra(BUCKETS, Bucket::class.java) ?: Bucket()

        handleTouchInteraction()

        rollDice()

        updateReport()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun handleTouchInteraction() {
        //A synchronized map switched is created to track the state of each die button (ID: D1-D6). It stores whether a button has been "swiped" or not.
        val switched = Collections.synchronizedMap(
            D6.entries.asSequence().associate { Pair(it.imageButtonId, false) })

        //diceView refers to the container (ConstraintLayout) holding the dice buttons (D1-D6).
        val diceView = findViewById<ViewGroup>(R.id.Dice)

        fun swiped(switched: MutableMap<Int?, kotlin.Boolean?>, imageButton: ImageButton?) {
            if ((imageButton != null) && (switched[imageButton.id] == false)) {
                imageButton.toggleSelect()
                switched.replace(imageButton.id, true)
            }
        }

        fun getTouchedButton(bucketsView: ViewGroup, x: Int, y: Int): ImageButton? {
            val bounds = Rect(0, 0, 0, 0)
            return D6.entries.stream()
                .toList()
                .asSequence()
                .mapNotNull { it.imageButton() }
                .firstOrNull {
                    it.background.copyBounds(bounds)
                    bucketsView.offsetDescendantRectToMyCoords(it, bounds)
                    bounds.contains(x, y)
                }
        }

        //A setOnTouchListener is set on diceView to handle touch events:
        //ACTION_DOWN: Resets all buttons in switched to false.
        //ACTION_UP or ACTION_MOVE: Calls swiped() to toggle the selection state of the touched button.
        //The listener returns true, indicating the event was handled.
        diceView.setOnTouchListener { _: View?, event: MotionEvent? ->
            val x = (event!!.x).toInt()
            val y = (event.y).toInt()

            val touchedButton = getTouchedButton(diceView, x, y)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    switched.replaceAll { _: Int?, _: kotlin.Boolean? -> false }
                }

                MotionEvent.ACTION_UP -> {
                    swiped(switched, touchedButton)
                }

                MotionEvent.ACTION_MOVE -> {
                    touchedButton?.let { swiped(switched, it) }
                }
            }
            true
        }

    }


    fun reroll(rerollButton: View?) {
        val rerollIntent = Intent(this, Buckets::class.java)

        val newBuckets = TreeMap(bucket.dice)
        val selectedRerollPool =
            D6.entries.stream()
                .toList()
                .asSequence()
                .mapNotNull { it.imageButton() }
                .filter { it.isSelected }
                .onEach { it.toggleSelect() }
                .mapNotNull { it.d6() }
                .sumOf { d6: D6 -> newBuckets.replace(d6, 0) ?: 0 }

        rerollIntent.putExtra(
            BUCKETS,
            Bucket(newBuckets, selectedRerollPool, bucket.randomDicerollIndex)
        )
        startActivity(rerollIntent)
    }

    fun rollon(rollonButton: View?) {
        val rollOnIntent = Intent(this, Buckets::class.java)

        val selectedDicepool = D6.entries
            .asSequence()
            .mapNotNull { it.imageButton() }
            .filter { it.isSelected }
            .onEach { it.toggleSelect() }
            .mapNotNull { it.d6() }
            .map { bucket.diceCount(it) }
            .sum()

        rollOnIntent.putExtra(
            BUCKETS,
            Bucket(
                dicepool = selectedDicepool,
                randomDicerollIndex = bucket.randomDicerollIndex
            )
        )

        startActivity(rollOnIntent)
    }


    //-------------- Utility methods ----------


    fun ImageButton.toggleSelect() {
        if (this.isSelected) {
            this.setBackgroundColor(Color.WHITE)
            this.setSelected(false)
        } else {
            this.setBackgroundColor(Color.BLUE)
            this.setSelected(true)
        }
        updateReport()
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

    private fun formatD6Roll(addition: Int?, value: Int?): String {
        return String.format(
            locale = Locale.getDefault(),
            format = "%s\n%s",
            when {
                (addition ?: 0) == 0 -> " "
                else -> ("+$addition")
            },
            when {
                (value ?: 0) == 0 -> "-"
                else -> value
            }
        )
    }

    private fun rollDice() {

        val message = "Rolled ${bucket.dicePoolSize} dice ${bucket.dice}"

        val rolls = bucket.rollDice()

        rolls.asSequence()
            .onEach { (d6, count) ->
                d6.label()?.let {
                    it.text = formatD6Roll(
                        addition = count,
                        value = bucket.dice.getValue(d6)
                    )
                }
            }
            .forEach { (d6, count) ->
                bucket.dice.merge(d6, count, Integer::sum)
            }

        updateReport()
        // Set a delayed task to wait for 2 seconds before proceeding
        Handler(Looper.getMainLooper())
            .postDelayed({
                // Continue with further actions after waiting
                D6.sequence()
                    .forEach { it.label()?.text = formatD6(bucket.dice[it]!!) }

                log(message)

            }, 2000) // 2000 milliseconds = 2 seconds
    }

    private fun updateReport() {
        val report = findViewById<TextView>(R.id.Status)

        val selected = D6.entries.asSequence()
            .filter { d6: D6 -> d6.imageButton()?.isSelected ?: false }
            .sumOf { d6: D6 -> bucket.diceCount(d6) }

        findViewById<View>(R.id.Reroll_button).setEnabled(selected > 0)
        findViewById<View>(R.id.Rollon_button).setEnabled(selected > 0)

        report.text = getString(R.string.dice_pool, bucket.dice.asSequence().sumOf { it.value }, selected)
    }

    private fun Bucket.diceCount(d6: D6): Int = this.dice.getOrDefault(d6, 0)

    private fun D6.imageButton(): ImageButton? {
        return findViewById(this.imageButtonId)
    }

    private fun ImageButton.d6(): D6? {
        return D6.entries.firstOrNull { d6 -> this.id == d6.imageButtonId }
    }

    private fun D6.label(): TextView? {
        return findViewById(this.labelId)
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
