import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

/**
 * CSC 322: Introduction to Computer Graphics, Fall 2017
 *
 * Brad Taylor, DSc Electrical Engineering and Computer Science The Catholic
 * University of America
 */
// =====================================================================================================================
public class CubeQuest {

    /** Locked out constructor; this class is static */
    private CubeQuest() {
    }

    // =================================================================================================================
    // GAME FRAMEWORK
    // =================================================================================================================

    /** Application title (shown on window bar) */
    static final String APP_TITLE = CubeQuest.class.getName();

    /** Target frame rate */
    static final int FRAME_RATE = 120;

    /** Light position (in camera space) */
    static final FloatBuffer lightPosition = floatBuffer(3.0f, 4.0f, 5.0f, 1.0f);

    /** Ambient component of light */
    static final FloatBuffer lightAmbient = floatBuffer(0.2f, 0.2f, 0.2f, 1.0f);

    /** Diffuse component of light */
    static final FloatBuffer lightDiffuse = floatBuffer(0.5f, 0.5f, 0.5f, 1.0f);

    /** Specular component of light */
    static final FloatBuffer lightSpecular = floatBuffer(0.1f, 0.1f, 0.1f, 1.0f);

    /** Ambient component of material */
    static final FloatBuffer materialAmbient = floatBuffer(1.0f, 1.0f, 1.0f, 1.0f);

    /** Diffuse component of material */
    static final FloatBuffer materialDiffuse = floatBuffer(1.0f, 1.0f, 1.0f, 1.0f);

    /** Specular component of material */
    static final FloatBuffer materialSpecular = floatBuffer(1.0f, 1.0f, 1.0f, 1.0f);

    /** Material shininess (specular exponent) */
    static final float materialShininess = 8.0f;

    /** Exit flag (application will finish when set to true) */
    static boolean finished;


    // =================================================================================================================

    /**
     * Initialize display and OpenGL properties
     *
     * @throws Exception
     */
    static void gameInit() throws Exception {

        // System.setProperty("org.lwjgl.opengl.Display.enableHighDPI", "true");

        // initialize the display
        Display.setTitle(APP_TITLE);
        Display.setFullscreen(false);
        Display.setVSyncEnabled(true);
        Display.setResizable(true);
        Display.create();

        // get display size
        int width = Display.getDisplayMode().getWidth();
        int height = Display.getDisplayMode().getHeight();

        // viewport
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glViewport(0, 0, width, height);

        // perspective transformation
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float aspectRatio = ((float) width) / height;
        gluPerspective(WorldClass.camera.fieldOfView, aspectRatio, WorldClass.camera.nearPlane,
                WorldClass.camera.farPlane);

        // background color
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        // lighting
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glLight(GL_LIGHT0, GL_AMBIENT, lightAmbient);
        glLight(GL_LIGHT0, GL_DIFFUSE, lightDiffuse);
        glLight(GL_LIGHT0, GL_SPECULAR, lightSpecular);
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        glEnable(GL_NORMALIZE);
        glEnable(GL_AUTO_NORMAL);

        // material
        glMaterial(GL_FRONT, GL_AMBIENT, materialAmbient);
        glMaterial(GL_FRONT, GL_DIFFUSE, materialDiffuse);
        glMaterial(GL_FRONT, GL_SPECULAR, materialSpecular);
        glMaterialf(GL_FRONT, GL_SHININESS, materialShininess);

        // allow changing colors while keeping the above material
        glEnable(GL_COLOR_MATERIAL);

        // depth testing
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        // transparency
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // antialiasing
        glEnable(GL_POINT_SMOOTH);
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_POLYGON_SMOOTH);

        // fog
        glEnable(GL_FOG);
        glFog(GL_FOG_COLOR, floatBuffer(1.0f, 1.0f, 1.0f, 1.0f));
        glFogi(GL_FOG_MODE, GL_EXP2);
        glFogf(GL_FOG_DENSITY, 0.01f);

        // TODO: initialize game elements

        PlayerClass.playerInit();
        EnemyClass.enemiesInit();
        BossEnemyClass.bossInit();

    }

    // =================================================================================================================

    /**
     * Main loop of the application. Repeats until finished variable takes on true
     */
    static void gameRun() {
        long timeStamp = System.currentTimeMillis();
        while (PlayerClass.player.playerDead() && !finished) {
            // perform time step and render
            float dt = 0.001f * (System.currentTimeMillis() - timeStamp);
            {

                gameHandleInput();
                gameUpdate(dt);
                gameHandleCollisions();
                gameRenderFrame();
                showPoints();
            }
            timeStamp = System.currentTimeMillis();
            Display.sync(FRAME_RATE);

            // make sure display is updated
            Display.update();
            if (Display.isCloseRequested())
                finished = true;
        }
    }

    // =================================================================================================================
    /** Handle input to the game */
    static void gameHandleInput() {

        // TODO: Modify as needed to handle game input.

        // escape to quit
        if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
        {
            finished = true;
        }

        // arrow keys
        PlayerClass.player.dx = 0.0f;
        PlayerClass.player.dz = 0.0f;

        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            PlayerClass.player.facing = PlayerClass.Direction.SOUTH;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            PlayerClass.player.facing = PlayerClass.Direction.NORTH;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            PlayerClass.player.facing = PlayerClass.Direction.WEST;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            PlayerClass.player.facing = PlayerClass.Direction.EAST;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            PlayerClass.player.dx = -1.0f;
            PlayerClass.player.facing = PlayerClass.Direction.WEST;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            PlayerClass.player.dx = +1.0f;
            PlayerClass.player.facing = PlayerClass.Direction.EAST;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            PlayerClass.player.dz = -1.0f;
            PlayerClass.player.facing = PlayerClass.Direction.NORTH;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            PlayerClass.player.dz = +1.0f;
            PlayerClass.player.facing = PlayerClass.Direction.SOUTH;
        }

        // space bar
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            PlayerClass.playerShoot();
        }
        if (!Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            PlayerClass.playerShoot();
        }

    }

    // =================================================================================================================
    /** Handle input and update scene */
    static void gameUpdate(float dt) {
        // TODO: add updates to all game elements.

        PlayerClass.player.update(dt);
        PlayerClass.player.updateBoss(dt);
        // EnemyClass.enemies.update(dt);
        PlayerClass.playerUpdate(dt);
        EnemyClass.enemiesUpdate(dt);
        BossEnemyClass.bossUpdate(dt);
        CurrentLevel.whatLevel(PlayerClass.points);
        CurrentLevel.enemyIncrease(CurrentLevel.level);
    }

    // =================================================================================================================
    /** Check for relevant collisions and handle them */
    static void gameHandleCollisions() {
        // TODO: add necessary collision checks and behaviors.

        collisionShotsAndEnemies();
        collisionShotsandBossEnemies();
        CollisionProperties.col.checkCollisionPlayer(PlayerClass.player, EnemyClass.enemies[0]);
        CollisionProperties.col.checkCollisionEnemies(EnemyClass.enemies, EnemyClass.enemies);
        CollisionProperties.col.checkCollisionPlayerBoss(PlayerClass.player, BossEnemyClass.bigEnemies[0]);

    }

    // =================================================================================================================
    /** Check for collisions between player shots and enemies */

    static void collisionShotsandBossEnemies() {
        for (PlayerClass.PlayerShot shot : PlayerClass.player.shots) {
            if (shot.t < PlayerClass.PLAYER_SHOT_DURATION) {
                List<BossEnemyClass.Boss> list = BossEnemyClass.bossFind(shot.x, shot.z, PlayerClass.PLAYER_SHOT_SIZE);
                for (BossEnemyClass.Boss be : list) {
                    be.health -= PlayerClass.PLAYER_SHOT_DAMAGE;
                    if (be.health <= 0) {
                        PlayerClass.points += PlayerClass.perBossKill;
                        BossEnemyClass.bossRespawn(be);
                    }
                    else {
                        PlayerClass.points += PlayerClass.perBossHit;
                    }

                    shot.t = PlayerClass.PLAYER_SHOT_DURATION;
                }
            }
        }
    }

    static void collisionShotsAndEnemies() {
        // for each active shot...
        for (PlayerClass.PlayerShot shot : PlayerClass.player.shots) {
            if (shot.t < PlayerClass.PLAYER_SHOT_DURATION) {
                // check for shot collision with enemy
                List<EnemyClass.Enemy> list = EnemyClass.enemiesFind(shot.x, shot.z, PlayerClass.PLAYER_SHOT_SIZE);
                for (EnemyClass.Enemy e : list) {
                    // register damage to enemy
                    e.health -= PlayerClass.PLAYER_SHOT_DAMAGE;
                    if (e.health <= 0) {
                        PlayerClass.points += PlayerClass.perKill;
                        EnemyClass.enemiesRespawn(e);
                    }
                    else {
                        PlayerClass.points += PlayerClass.perHit;
                    }


                    // disable shot
                    shot.t = PlayerClass.PLAYER_SHOT_DURATION;
                }
            }
        }
    }

    // =================================================================================================================

    // =================================================================================================================
    /** Render the scene from the current view */
    static void gameRenderFrame() {

        // clear the screen and depth buffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        displayHUD();

        // viewing transformation (bottom of the model-view stack)
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glPushMatrix();
        {

            // go to 3rd person view of player
            WorldClass.cameraTransformation();
            glScalef(WorldClass.WORLD_SCALE, WorldClass.WORLD_SCALE, WorldClass.WORLD_SCALE);
            PlayerClass.playerPlotAvatar();

            // undo player location
            glTranslatef(-PlayerClass.player.x, 0.0f, -PlayerClass.player.z);

            // TODO: plot all game elements

            WorldClass.worldPlotFloor();
            PlayerClass.playerPlotShots();
            EnemyClass.enemiesPlot();
            BossEnemyClass.bossPlot();
        }
        glPopMatrix();

    }

    // =================================================================================================================
    /** Clean up before exit */
    static void gameCleanup() {
        // Close the window
        Display.destroy();
    }

    // =========================================================================
    // UTILITY AND MISC.
    // =========================================================================

    static void displayHUD() {
        float w = Display.getWidth();
        float h = Display.getHeight();

        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        {
            glLoadIdentity();
            glTranslatef(-1.0f, -1.0f, -1.0f);
            glScalef(1 / (w / 2.0f), 1 / (h / 2.0f), 1.0f);
            glDisable(GL_LIGHTING);
            displayHealth(w, h);
        }
        glMatrixMode(GL_PROJECTION);
        glPopMatrix();
        glMatrixMode(GL_MODELVIEW);
        glPopMatrix();
    }

    static void displayHealth(float w, float h) {
        float margin = 50.0f;
        float maxBarHeight = 200.0f;
        float barWidth = 50.0f;

        float barHeight = maxBarHeight * PlayerClass.player.health / PlayerClass.player.maxHealth;

        glPushMatrix();
        glTranslatef(margin, margin, 0.0f);
        glBegin(GL_QUADS);
        {
            glColor3f(0.5f, 0.5f, 0.5f);
            glVertex2d(0.0f, barHeight);
            glVertex2d(barWidth, barHeight);
            glVertex2d(barWidth, maxBarHeight);
            glVertex2d(0.0f, maxBarHeight);

            glColor3f(1.0f, 0.0f, 0.0f);
            glVertex2d(0.0f, 0.0f);
            glVertex2d(barWidth, 0.0f);
            glVertex2d(barWidth, barHeight);
            glVertex2d(0.0f, barHeight);
        }
        glEnd();
        glPopMatrix();
    }

    // =================================================================================================================
    /**
     * Utility function to easily create float buffers.
     *
     * @param f1
     *            A float.
     * @param f2
     *            A float.
     * @param f3
     *            A float.
     * @param f4
     *            A float.
     * @return A float buffer.
     */
    static FloatBuffer floatBuffer(float f1, float f2, float f3, float f4) {

        FloatBuffer fb = BufferUtils.createFloatBuffer(4);
        fb.put(f1).put(f2).put(f3).put(f4).flip();
        return fb;

    }

    static void showPoints() {
        if(PlayerClass.points < 0) {
            PlayerClass.points = 0;
        }
        String text = "ABCD";
        int s = 256; //Take whatever size suits you.
        BufferedImage b = new BufferedImage(s, s, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = b.createGraphics();
        g.setColor(Color.black);
        g.drawString(text, 0, 0);

        int co = b.getColorModel().getNumComponents();

        byte[] data = new byte[co * s * s];
        b.getRaster().getDataElements(0, 0, s, s, data);

        ByteBuffer pixels = BufferUtils.createByteBuffer(data.length);
        pixels.put(data);
        pixels.rewind();

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 256, 256, 0, GL_RGB,  GL_UNSIGNED_BYTE, pixels);


        //glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 256, 256,
        //0, GL_RGB, GL_UNSIGNED_BYTE, pixels);


        Display.setTitle("Score: " + Integer.toString(PlayerClass.points) + "   Level: " + Integer.toString(CurrentLevel.level));
    }
    // =================================================================================================================
    // MAIN
    // =================================================================================================================
    public static void main(String[] args) {
        try {
            gameInit();
            gameRun();
        } catch (Exception e) {
            System.out.println("Fatal error: " + e.getMessage());
        } finally {
            gameCleanup();
        }
    }}