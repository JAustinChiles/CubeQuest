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

public class PlayerClass
{
    enum Direction {NORTH, SOUTH, EAST, WEST}

    /** The points scored in this round*/
    static int points;

    /**The number of points scored when a bullet strikes an enemy*/
    static final int perHit = 2;

    /**The number of points scored when a shot hits a boss*/
    static final int perBossHit = 4;

    /**The number of points scored when an block is completely destroyed*/
    static final int perKill = 10;

    /**The number of points for killing a boss*/
    static final int perBossKill = 25;

    /**The number of points deducted when a block strikes us*/
    static final int collision = -2;

    /** Player speed (distance per second) */
    static final float PLAYER_SPEED = 10.0f;

    /** Player's shot speed (distance per second) */
    static final float PLAYER_SHOT_SPEED = 30.0f;

    /** Player's shot damage (health decrease to enemy on collision)  */
    static final float PLAYER_SHOT_DAMAGE = 1.0f;

    /** Player's shot size.    */
    static final float PLAYER_SHOT_SIZE = 0.1f;

    /** Player's shot duration in seconds   */
    static final float PLAYER_SHOT_DURATION = 1.0f;

    /** Maximum number of active shots player may have  */
    static final int PLAYER_SHOT_MAX = 4;

    /** Enforced delay between consecutive player shots (in seconds)    */
    static final float PLAYER_SHOT_DELAY = PLAYER_SHOT_DURATION/
            PLAYER_SHOT_MAX;

    //=================================================================================================================
    static class Player
    {
        float maxHealth = 100;
        float health = 100;
        boolean isAlive = true;
        boolean playerDead()
        {
            if(player.health < 0)
                System.exit(0);
            return true;
        }

        void update(float dt)
        {
            for(int i = 0; i < EnemyClass.ENEMY_COUNT - 1; i++)
            {
                if(CollisionProperties.col.checkCollisionPlayer(player, EnemyClass.enemies[i]))
                {
                    dx = -1 * (EnemyClass.enemies[i].dx * 0.01f);
                    dz = -1 * (EnemyClass.enemies[i].dz * 0.01f);
                    x -= dx * 5.0f;
                    z -= dz * 5.0f;
                }
            }
        }
        void updateBoss(float dt)
        {
            for(int i = 0; i < BossEnemyClass.BOSS_COUNT - 1; i++)
            {
                if(CollisionProperties.col.checkCollisionPlayerBoss(player, BossEnemyClass.bigEnemies[i]))
                {
                    dx = -1 * (BossEnemyClass.bigEnemies[i].dx * 0.01f);
                    dz = -1 * (BossEnemyClass.bigEnemies[i].dz * 0.01f);
                    x -= dx * 5.0f;
                    z -= dz * 5.0f;
                }
            }
        }


        //=================================================================================================================
        // position in the zx plane
        float x = 0.0f;
        float z = 0.0f;

        // direction of movement (+/- 1)
        float dx = 0.0f;
        float dz = 0.0f;

        // facing direction
        Direction facing = Direction.SOUTH;

        // shots
        final PlayerShot[] shots = new PlayerShot[PLAYER_SHOT_MAX];

        // age (in seconds)
        float t = 0.0f;

    }
    //=================================================================================================================
    /** Player shot structure   */
    static class PlayerShot
    {
        // location in the zx plane
        public float x  = 0.0f;
        public float z  = 0.0f;

        // direction of movement (+/- 1)
        public float dx = 0.0f;
        public float dz = 0.0f;

        // age (in seconds); if >= PLAYER_SHOT_DURATION, shot is inactive
        public float t  = PLAYER_SHOT_DURATION;
    }

    /** The player  */
    static final Player player = new Player();

    //=================================================================================================================

    /** Initialize player   */
    static void playerInit()
    {
        for (int i = 0; i < PLAYER_SHOT_MAX; i++)
            player.shots[i] = new PlayerShot();
    }
    //=================================================================================================================
    /** Update player given dt, the number of seconds since last update
     * @param dt A float    */
    static void playerUpdate(float dt)
    {
        // update player position
        player.x += player.dx*PLAYER_SPEED*dt;
        player.z += player.dz*PLAYER_SPEED*dt;
        player.t += dt;

        // update player shots (if active)
        for (PlayerShot shot : player.shots)
        {
            if (shot.t < PLAYER_SHOT_DURATION)
            {
                shot.t += dt;
                shot.x += shot.dx*PLAYER_SHOT_SPEED*dt;
                shot.z += shot.dz*PLAYER_SHOT_SPEED*dt;
            }
        }
    }
    //=================================================================================================================

    /** Plot the player avatar. Plots at center of screen since we assume camera is following   */
    static void playerPlotAvatar()
    {
        // plot player avatar
        glPushMatrix();
        {
            glColor3f(1.0f, 0.0f, 0.0f);
            glTranslatef(0.0f, 0.5f, 0.0f);
            glScalef(0.5f, 0.5f, 0.5f);
            CubeCharClass.plotSolidCube();
        }
        glPopMatrix();

    }
    //=================================================================================================================
    /** Plot the player's currently active shots (those with a t <= PLAYER_SHOT_DURATION)   */
    static void playerPlotShots()
    {
        // for each shot...
        for (PlayerShot shot : player.shots)
        {
            // if it is active, plot it
            if (shot.t < PLAYER_SHOT_DURATION)
            {
                glPushMatrix();
                {
                    glColor3f(1.0f, 1.0f, 0.0f);
                    glTranslatef(shot.x, 0.5f, shot.z);
                    glScalef(PLAYER_SHOT_SIZE, PLAYER_SHOT_SIZE,
                            PLAYER_SHOT_SIZE);
                    CubeCharClass.plotSolidCube();
                }
                glPopMatrix();
            }
        }
    }
    //=================================================================================================================

    /** Set a new active shot given the player's position and
     * facing direction. If all available shots are currently active, the call has no effect    */
    static void playerShoot()
    {
        for (PlayerShot shot : player.shots)
        {
            // shot fired too recently to fire another
            if (shot.t < PLAYER_SHOT_DELAY)
                return;

            // found re-usable shot slot
            if (shot.t >= PLAYER_SHOT_DURATION)
            {
                // activate it
                shot.t = 0.0f;
                shot.x = player.x;
                shot.z = player.z;
                shot.dx = 0.0f;
                shot.dz = 0.0f;


                // set velocity according to player facing direction
                switch (player.facing)
                {
                    case EAST:  shot.dx = +1.0f;
                        break;
                    case WEST:  shot.dx = -1.0f;
                        break;
                    case NORTH: shot.dz = -1.0f;
                        break;
                    case SOUTH: shot.dz = +1.0f;
                        break;
                }
                // if slot is found, we're done;
                return;
            }
        }
    }
    //==================================================================================================================
}
