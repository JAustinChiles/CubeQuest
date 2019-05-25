import java.util.*;
import java.io.*;

public class CurrentLevel

    //Increases the level and difficulty of the game overtime

{
    static int level = 1;
    static int scoreToModBy = 500;
    static int tempScore = PlayerClass.points;

    //Determines the level based on the current score

    static void whatLevel(int tempScore){
        level = (1+ tempScore/500);
        }



    //Increases the enemies health and speed as the game progresses

    static void enemyIncrease(int level){
        EnemyClass.ENEMY_SPEED = 1.5f * (1+ (float) level / 10.0f);
        EnemyClass.ENEMY_MAX_HEALTH = 10.0f * (1+ (float) level / 10.0f);

        BossEnemyClass.BOSS_SPEED = 2.0f * (1+ (float) level / 10.0f);
        BossEnemyClass.BOSS_MAX_HEALTH = 30.f * (1+ (float) level / 10.0f);
    }

    }