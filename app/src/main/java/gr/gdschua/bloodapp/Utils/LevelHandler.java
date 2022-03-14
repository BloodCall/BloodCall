package gr.gdschua.bloodapp.Utils;

public class LevelHandler {

    public static int MAX_LEVEL=30;
    public static int MAX_XP=10000;

    public static int getLevel(int xp){
        int level=1;
        while(xp-getXpForLvl(level)>=0) {
            level++;
        }
        return level;
    }

    static public int getXpForLvl(int level){
        return (int) Math.pow(level/0.3,2);
    }
}
