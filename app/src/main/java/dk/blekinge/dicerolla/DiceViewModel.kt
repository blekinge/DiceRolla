package dk.blekinge.dicerolla

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class DiceViewModel : ViewModel() {
    private val _dicePool = mutableStateOf(0)
    var dicePool: Int
        get() = _dicePool.value
        set(value) {
            _dicePool.value = value
        }

    private val _bucket = mutableStateOf<Bucket?>(null)
    val bucket
        get() = _bucket

    fun rollBucket(initialBucket: Bucket) {
        _bucket.value = Bucket.roll(initialBucket)
    }

    fun reset() {
        _bucket.value = null
    }
}