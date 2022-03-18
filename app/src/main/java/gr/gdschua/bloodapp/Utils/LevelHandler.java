package gr.gdschua.bloodapp.Utils;

public class LevelHandler {

    public static int MAX_LEVEL = 30;
    public static int MAX_XP = 10000;

    public static int getLevel(int xp) {
        int level = 1;
        while (xp - getLvlXpCap(level) >= 0) {
            level++;
        }
        return level;
    }

    static public int getLvlXpCap(int level) {
        return (int) Math.pow(level / 0.3, 2);
    }

    static public int getXpToNextLvl(int currXp, int currLvl) {
        int currLvlCap = getLvlXpCap(currLvl);
        return currLvlCap - currXp;
    }

    static public int getLvlCompletionPercentage(int currXp, int currLvl) {
        int currLvlXpCap = getLvlXpCap(currLvl);
        int prevLvlXpCap = getLvlXpCap(currLvl - 1);
        float lvlXpDiff = currLvlXpCap - prevLvlXpCap;
        float xpAbovePrevCap = currXp - prevLvlXpCap;

        return (int) ((xpAbovePrevCap / lvlXpDiff) * 100);
    }
}
