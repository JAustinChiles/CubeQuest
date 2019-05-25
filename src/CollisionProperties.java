import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.Sphere;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

/** ADD NAME FOR CHANGES:
 *
 *
 * */

// TODO: not sure if the collision properties actually affect the player health like it should
public class CollisionProperties
{
    public boolean checkCollisionPlayer(PlayerClass.Player p, EnemyClass.Enemy e)
    {
        boolean collision = false;

        for(int i = 0; i < EnemyClass.ENEMY_COUNT; i++)
        {
            if((abs(p.x - e.x) < 1.0f) && abs(p.z - e.z) < 1.0f)
            {
                collision = true;
                p.health -= 0.1f;
            }
            else
                collision = false;
        }
        return collision;
    }
    /*
    public boolean checkCollisionEnemiesTest(EnemyClass.Enemy a, EnemyClass.Enemy b)
    {
        boolean collision = false;
        for(int i = 0; i < EnemyClass.ENEMY_COUNT; i++)
        {
            if((abs(a.x - b.x) < 1.0f) && abs(a.z - b.z) < 1.0f)
                collision = true;
            else
                collision = false;
        }
        return collision;
    }
    */

    public boolean checkCollisionEnemies(EnemyClass.Enemy[] a, EnemyClass.Enemy[] b)
    {
        boolean collision = false;
        for(int i = 0; i < EnemyClass.ENEMY_COUNT; i++)
        {
            if((abs(a[i].x - b[i].x) < 1.0f) && abs(a[i].z - b[i].z) < 1.0f)
                collision = true;
            else
                collision = false;
        }
        return collision;
    }

    public boolean checkCollisionBoss(BossEnemyClass.Boss[] a, BossEnemyClass.Boss[] b)
    {
        boolean collision = false;
        for(int i = 0; i < BossEnemyClass.BOSS_COUNT; i++)
        {
            if((abs(a[i].x -b[i].x) < 1.0f) && abs(a[i].z - b[i].z) < 1.0f)
                collision = true;
            else
                collision = false;
        }
        return collision;
    }

    public boolean checkCollisionPlayerBoss(PlayerClass.Player p, BossEnemyClass.Boss be)
    {
        boolean collision = false;

        for(int i = 0; i < BossEnemyClass.BOSS_COUNT; i++)
        {
            if((abs(p.x - be.x) < BossEnemyClass.BOSS_SIZE + 0.5f) && abs(p.z - be.z) < BossEnemyClass.BOSS_SIZE + 0.5f)
            {
                collision = true;
                p.health -= 0.1f;
            }
            else
                collision = false;
        }
        return collision;
    }

    final static CollisionProperties col = new CollisionProperties();
}