package ru.nsu.cg.model;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import ru.nsu.cg.Subscriber;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CurveSettings {
    @Getter
    private int m = 10;
    private static final int minM = 2;
    private static final int maxM = 100;
    private int savedM = m;
    @Getter
    private int n = 10;
    private static final int minN = 2;
    private static final int maxN = 100;
    private int savedN = n;
    @Getter
    private int k = 0;
    private static final int minK = 0;
    private static final int maxK = 50;
    private int savedK = k;

    @Getter
    private int tN = 5;
    private static final int minTN = 5;
    private static final int maxTN = 100;
    private int savedTN = tN;

    private static final int minColor = 0;
    private static final int maxColor = 255;

    @Getter
    private int R = 0;
    private int savedR = R;
    @Getter
    private int G = 0;
    private int savedG = G;
    @Getter
    private int B = 0;
    private int savedB = B;

    private final ArrayList<Subscriber> subs = new ArrayList<>();

    public void save() {
        savedN = n;
        savedM = m;
        savedK = k;
        savedTN = tN;
        savedR = R;
        savedG = G;
        savedB = B;
    }

    public void recover() {
        n = savedN;
        m = savedM;
        k = savedK;
        tN = savedTN;
        R = savedR;
        G = savedG;
        B = savedB;
    }

    public void setM(int m) {
        if (m >= minM && m <= maxM) {
            this.m = m;
            notifySubscribers();
        } else {
            throw new IllegalArgumentException("'m' parameter must be between " + minM + " and " + maxM);
        }
    }

    public void setN(int n) {
        if (n >= minN && n <= maxN) {
            this.n = n;
            notifySubscribers();
        } else {
            throw new IllegalArgumentException("'n' parameter must be between " + minN + " and " + maxN);
        }
    }

    public void setK(int k) {
        if (k >= minK && k <= maxK) {
            this.k = k;
            notifySubscribers();
        } else {
            throw new IllegalArgumentException("'k' parameter must be between " + minK + " and " + maxK);
        }
    }

    public void settN(int tN) {
        if (tN >= minTN && tN <= maxTN) {
            this.tN = tN;
            notifySubscribers();
        } else {
            throw new IllegalArgumentException("'tN' parameter must be between " + minTN + " and " + maxTN);
        }
    }

    public void setR(int R) {
        if (R >= minColor && R <= maxColor) {
            this.R = R;
            notifySubscribers();
        } else {
            throw new IllegalArgumentException("'R' parameter must be between " + minColor + " and " + maxColor);
        }
    }

    public void setG(int G) {
        if (G >= minColor && G <= maxColor) {
            this.G = G;
            notifySubscribers();
        } else {
            throw new IllegalArgumentException("'G' parameter must be between " + minColor + " and " + maxColor);
        }
    }

    public void setB(int B) {
        if (B >= minColor && B <= maxColor) {
            this.B = B;
            notifySubscribers();
        } else {
            throw new IllegalArgumentException("'B' parameter must be between " + minColor + " and " + maxColor);
        }
    }

    public void addSubscriber(Subscriber sub) {
        subs.add(sub);
    }

    private void notifySubscribers() {
        for (Subscriber sub : subs) {
            sub.update();
        }
    }
}
