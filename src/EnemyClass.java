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

public class EnemyClass
{
    /** Maximum number of enemies   */
    static int ENEMY_COUNT = 50;

    /** Size of the enemies */
    static final float ENEMY_SIZE = 0.5f;

    /** Enemy speed in distance per second  */
    static float ENEMY_SPEED = 1.5f;

    /** Time it takes for enemy to spawn in seconds */
    static final float ENEMY_SPAWN_TIME = 1.0f;

    /** Starting health of enemies  */
    static float ENEMY_MAX_HEALTH = 10.0f;

    //=================================================================================================================

    /** Enemy structure */
    static class Enemy
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

    //=================================================================================================================
    /** All enemies */
    static final Enemy[] enemies = new Enemy[ENEMY_COUNT];
    //static final Enemy[] bigenemies = new Enemy[BOSS_ENEMY_COUNT];

    //=================================================================================================================

    /** Initialize enemy locations  */
    static void enemiesInit()
    {
        // for each enemy
        for (int i = 0; i < ENEMY_COUNT; i++)
        {
            // place it in a random world location
            enemies[i] = new Enemy();
            enemiesRespawn(enemies[i]);
        }
    }

    //=================================================================================================================
    /** Update enemies based on dt, the time transpired in seconds since the last enemy update
     * @param dt A float    */

    static void enemiesUpdate(float dt)
    {
        // for each enemy...
        for (int i = 0; i < ENEMY_COUNT; i++)
        {
            Enemy e = enemies[i];

            // update t
            e.t += dt;

            // if enemy is finished spawning...
            if (e.t >= 0.0f)
            {
                // Collision property with a player callback to CollisionProperties class
                if(CollisionProperties.col.checkCollisionPlayer(PlayerClass.player, enemies[i]) == true) {}
                else
                {
                    // set direction of motion toward player
                    e.dx = signum(PlayerClass.player.x - e.x);
                    e.dz = signum(PlayerClass.player.z - e.z);

                    // update location
                    e.x += e.dx * ENEMY_SPEED * dt;
                    e.z += e.dz * ENEMY_SPEED * dt;
                }
            }
        }
    }

    //=================================================================================================================
    /** Plot the current state of the enemies   */


    static void enemiesPlot()
    {
        // for each enemy...
        for (int i = 0; i < ENEMY_COUNT; i++)
        {
            // consider current enemy
            Enemy e = enemies[i];

            glPushMatrix();
            {
                // if enemy is spawning...
                if (e.t < 0)
                    // color is blue
                    glColor3f(0.0f, 0.0f, 1.0f);

                else
                    // color is green
                    glColor3f(0.5f, 0.5f, 0.0f);

                // plot cube at enemy location
                glTranslatef(e.x, 0.0f, e.z);
                float h = (ENEMY_SIZE * e.health) / ENEMY_MAX_HEALTH;
                glPushMatrix();
                {
                    glScalef(ENEMY_SIZE, h, ENEMY_SIZE);
                    glTranslatef(0.0f, 1.0f, 0.0f);
                    CubeCharClass.plotSolidCube();
                }
                glPopMatrix();
                glPushMatrix();
                {
                    glScalef(ENEMY_SIZE, ENEMY_SIZE, ENEMY_SIZE);
                    glTranslatef(0.0f, 1.0f, 0.0f);
                    CubeCharClass.plotWireFrameCube();
                }
                glPopMatrix();
            }
            glPopMatrix();
        }
    }

    //=================================================================================================================
    /** Find the list of all enemies that intersect the axis-aligned box centered at (x, z) and having radius r.
     * @param x A float.
     * @param z A float.
     * @param r A float.
     * @return A list of enemies    */
    static List<Enemy> enemiesFind(float x, float z, float r)
    {
        List<Enemy> list = new ArrayList<>(ENEMY_COUNT);

        float d = r + ENEMY_SIZE;
        for (Enemy e : enemies)
        {
            if (max(abs(x - e.x), abs(z - e.z)) < d)
                list.add(e);
        }
        return list;
    }

    //=================================================================================================================
    /** Spawn enemy e to new location.
     * @param e */

    static void enemiesRespawn(Enemy e)
    {
        e.x = WorldClass.randomGen(-WorldClass.WORLD_RADIUS, +WorldClass.WORLD_RADIUS,PlayerClass.player.x);
        e.z = WorldClass.randomGen(-WorldClass.WORLD_RADIUS, +WorldClass.WORLD_RADIUS,PlayerClass.player.z);//passes in the player value to ensure it does not spawn on the player
        e.t = -ENEMY_SPAWN_TIME;
        e.health = ENEMY_MAX_HEALTH;
    }
}