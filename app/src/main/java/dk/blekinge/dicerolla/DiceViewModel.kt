package dk.blekinge.dicerolla

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class DiceViewModel : ViewModel() {

    private val _dicePool = mutableStateOf(0)
    var dicePool: Int
        get() = _dicePool.value
        set(value) {
            _dicePool.value = value
        }

    private val _bucket = MutableStateFlow<Bucket?>(null)
    val bucket: StateFlow<Bucket?>
        get() = _bucket.asStateFlow()

    fun rollBucket(initialBucket: Bucket) {
        _bucket.update { Bucket.roll(initialBucket) }
    }

    fun reset() {
        _bucket.update({ null })
    }
}