package dk.blekinge.dicerolla;

import java.util.Arrays;

public enum D6 implements Comparable<D6>{

    R1(R.id.D1),

    R2(R.id.D2),

    R3(R.id.D3),

    R4(R.id.D4),

    R5(R.id.D5),

    R6(R.id.D6);

    final int imageButtonId;

    D6(int imageButtonId) {
        this.imageButtonId = imageButtonId;
    }

    public static D6 fromImageButtonId(int imageButtonId) {
        return Arrays.stream(values()).filter(a -> a.imageButtonId == imageButtonId).findFirst().orElse(null);
    }

}
