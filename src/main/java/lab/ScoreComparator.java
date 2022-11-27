package lab;

import lab.gui.Score;

import java.util.Comparator;

public class ScoreComparator implements Comparator<Score> {
    @Override
    public int compare(Score o1, Score o2) {
        return Integer.compare(o1.getAmount(), o2.getAmount());
    }
}
