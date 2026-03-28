package dk.blekinge.dicerolla;

import android.app.Activity;
import android.widget.TextView;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public enum D6 implements Comparable<D6>, Serializable {

    R1(R.id.D1, R.id.count1),

    R2(R.id.D2, R.id.count2),

    R3(R.id.D3, R.id.count3),

    R4(R.id.D4, R.id.count4),

    R5(R.id.D5, R.id.count5),

    R6(R.id.D6, R.id.count6);

    final int imageButtonId;
    final int labelId;


    static final Random random = new Random(new Date().getTime());
    static final List<D6> randomDicerolls= random
                .ints(10_000, 0, D6.values().length)
                .mapToObj(i -> D6.values()[i])
                .collect(Collectors.toCollection(() -> new CircularList<>()));


    static int getRandomOffset(){
        return random.nextInt(randomDicerolls.size());
    }

    D6(int imageButtonId, int labelId) {
        this.imageButtonId = imageButtonId;
        this.labelId = labelId;
    }

    public static D6 fromImageButtonId(int imageButtonId) {
        return Arrays.stream(values()).filter(a -> a.imageButtonId == imageButtonId).findFirst().orElse(null);
    }

}
