import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

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

/** DUPLICATE OF ENEMY CLASS TO FIT CHARACTERISTICS OF BOSS ENEMY   */
public class BossEnemyClass
{
    static int BOSS_COUNT = 5;
    static final float BOSS_SIZE = 2.0f;
    static float BOSS_SPEED = 2.0f;
    static final float BOSS_SPAWN_TIME = 1.0f;
    static float BOSS_MAX_HEALTH = 30.0f;

    static class Boss
    {
        // position in the zx plane
        float x;
        float z;

        // direction of movement (+/- 1)
        float dx;
        float dz;

        // age (in seconds)
        float t;

        // health remaining
        float health;
    }

    static final Boss[] bigEnemies = new Boss[BOSS_COUNT];

    static void bossInit()
    {
        for( int i  = 0; i < BOSS_COUNT; i++)
        {
            bigEnemies[i] = new Boss();
            bossRespawn(bigEnemies[i]);
        }
    }

    static void bossUpdate(float dt)
    {
        for(int i = 0; i < BOSS_COUNT; i++)
        {
            Boss be = bigEnemies[i];
            be.t += dt;
            if(be.t >= 0.0f)
                if(CollisionProperties.col.checkCollisionPlayerBoss(PlayerClass.player, bigEnemies[i]) == true);
                else
                {
                    be.dx = signum(PlayerClass.player.x - be.x);
                    be.dz = signum(PlayerClass.player.z - be.z);

                    // update location
                    be.x += be.dx * BOSS_SPEED * dt;
                    be.z += be.dz * BOSS_SPEED * dt;
                }
        }
    }

    static void bossPlot()
    {
        for (int i = 0; i < BOSS_COUNT; i++)
        {
            // consider current enemy
            Boss be = bigEnemies[i];

            glPushMatrix();
            {
                // if enemy is spawning...
                if (be.t < 0)
                    // color is blue
                    glColor3f(0.0f, 0.0f, 1.0f);
                else
                    // color is green
                    glColor3f(0.8f, 1.0f, 0.0f);

                // plot cube at enemy location
                glTranslatef(be.x, 0.0f, be.z);
                float h = (BOSS_SIZE * be.health) / BOSS_MAX_HEALTH;
                glPushMatrix();
                {
                    glScalef(BOSS_SIZE, h, BOSS_SIZE);
                    glTranslatef(0.0f, 1.0f, 0.0f);
                    CubeCharClass.plotSolidCube();
                }
                glPopMatrix();
                glPushMatrix();
                {
                    glScalef(BOSS_SIZE, BOSS_SIZE, BOSS_SIZE);
                    glTranslatef(0.0f, 1.0f, 0.0f);
                    CubeCharClass.plotWireFrameCube();
                }
                glPopMatrix();
            }
            glPopMatrix();
        }
    }

    static List<Boss> bossFind(float x, float z, float r)
    {
        List<Boss> list = new ArrayList<>(BOSS_COUNT);
        float d = r + BOSS_SIZE;
        for(Boss be : bigEnemies)
        {
            if (max(abs(x - be.x), abs(z - be.z)) < d)
                list.add(be);
        }
        return list;
    }

    static void bossRespawn(Boss be)
    {
        be.x = WorldClass.randomGen(-WorldClass.WORLD_RADIUS, +WorldClass.WORLD_RADIUS,PlayerClass.player.x);
        be.z = WorldClass.randomGen(-WorldClass.WORLD_RADIUS, +WorldClass.WORLD_RADIUS,PlayerClass.player.z);//inputs the player's location to ensure it does not spawn on the player
        be.t = -BOSS_SPAWN_TIME;
        be.health = BOSS_MAX_HEALTH;
    }

}