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

// TODO: change world characteristics

public class WorldClass
{

    /** Bounds of the world where enemies can spawn     */
    static final float WORLD_RADIUS = 20.0f;

    /** Scale factor used for rendering     */
    static float WORLD_SCALE = 0.25f;

    //=================================================================================================================
    /** Plot a grid on the ZX plane     */
    static void worldPlotFloor()
    {
        float lower = (float) floor(-camera.farPlane) - 0.5f;
        float upper = (float)  ceil(+camera.farPlane) + 0.5f;

        glDisable(GL_LIGHTING);
        {
            glColor4f(0.75f, 0.75f, 0.75f, 0.75f);
            glLineWidth(0.2f);
            glBegin(GL_LINES);
            {
                glNormal3f(0.0f, 1.0f, 0.0f);
                for (float x = lower; x <= upper; x += 1.0f)
                {
                    glVertex3f(x, 0.0f, -camera.farPlane);
                    glVertex3f(x, 0.0f, +camera.farPlane);
                }
                for (float z = lower; z <= upper; z += 1.0f)
                {
                    glVertex3f(-camera.farPlane, 0.0f, z);
                    glVertex3f(+camera.farPlane, 0.0f, z);
                }
            }
            glEnd();
        }
        glEnable(GL_LIGHTING);
    }

    //=================================================================================================================
    /** Camera structure    */
    static class Camera
    {
        // camera's spherical coordinates about the player
        public float azimuth     =  0.0f;
        public float elevation   = -37.5f;
        public float distance    =  8.0f;

        // clipping planes
        public float nearPlane   =  0.1f;
        public float farPlane    =  100.0f;
        public float fieldOfView =  45.0f;
    }

    /** The game camera */
    static final Camera camera = new Camera();

    //=================================================================================================================
    /** Apply camera transformation */
    static void cameraTransformation()
    {
        glTranslatef(0.0f, 0.0f, -camera.distance);
        glRotatef(-camera.elevation, 1.0f, 0.0f, 0.0f);
        glRotatef(-camera.azimuth,   0.0f, 1.0f, 0.0f);
    }

    /** Random number generator */
    static final Random random = new Random(System.currentTimeMillis());

    //=================================================================================================================
    /** A random value uniformly distributed in the interval [lower, upper).
     * @param lower A float.
     * @param upper A float.
     * @return A float          */
    static float randomGen(float lower, float upper,float p)
    {
        float r = random.nextFloat()*(upper - lower) + lower;
        if(r<=p-5 || r>p+5)//checks to see if the enemy is within 5 units of the player
            return r;//if the distance is sufficient, it returns the value
        else
            return randomGen(lower,upper,p);//if it is not, it runs the function again to get a workable value
    }

}