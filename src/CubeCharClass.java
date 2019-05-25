import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import sun.awt.image.PNGImageDecoder;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
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
public class CubeCharClass
{

    /** Plot a unit cube (i.e, a cube spanning the [-1, 1] interval on the X, Y, and Z axes)    */
    static void plotSolidCube()
    {

        // set flat shading
        glShadeModel(GL_FLAT);

        // drawing quads (squares)
        glBegin(GL_QUADS);
        {
            // front x face
            glNormal3f( 1.0f, 0.0f, 0.0f);
            glVertex3f( 1.0f, -1.0f, -1.0f);
            glVertex3f( 1.0f, 1.0f, -1.0f);
            glVertex3f( 1.0f, 1.0f, 1.0f);
            glVertex3f( 1.0f, -1.0f, 1.0f);

            // back x face
            glNormal3f(-1.0f, 0.0f, 0.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);

            // front y face
            glNormal3f(0.0f, 1.0f, 0.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);
            glVertex3f( 1.0f, 1.0f, -1.0f);
            glVertex3f( 1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);

            // back y face
            glNormal3f(0.0f, -1.0f, 0.0f);
            glVertex3f( 1.0f, -1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f( 1.0f, -1.0f, -1.0f);

            // front z face
            glNormal3f(0.0f, 0.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            glVertex3f( 1.0f, -1.0f, 1.0f);
            glVertex3f( 1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);

            // back z face
            glNormal3f(0.0f, 0.0f, -1.0f);
            glVertex3f( 1.0f, 1.0f, -1.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f( 1.0f, -1.0f, -1.0f);

        }
        glEnd();
    }

    //=================================================================================================================
    /** Plot a unit cube (i.e, a cube spanning the [-1, 1] interval on the X, Y, and Z axes)    */
    static void plotWireFrameCube()
    {
        // set flat shading
        glShadeModel(GL_FLAT);

        // front x face
        glBegin(GL_LINE_LOOP);
        {
            glNormal3f(1.0f, 0.0f, 0.0f);
            glVertex3f(1.0f, -1.0f, -1.0f);
            glVertex3f(1.0f, 1.0f, -1.0f);
            glVertex3f(1.0f, 1.0f, 1.0f);
            glVertex3f(1.0f, -1.0f, 1.0f);
        }
        glEnd();

        // back x face
        glBegin(GL_LINE_LOOP);
        {
            glNormal3f(-1.0f, 0.0f, 0.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);
        }
        glEnd();

        // front y face
        glBegin(GL_LINE_LOOP);
        {
            glNormal3f(0.0f, 1.0f, 0.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);
            glVertex3f(1.0f, 1.0f, -1.0f);
            glVertex3f(1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
        }
        glEnd();

        // back y face
        glBegin(GL_LINE_LOOP);
        {
            glNormal3f(0.0f, -1.0f, 0.0f);
            glVertex3f(1.0f, -1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f(1.0f, -1.0f, -1.0f);
        }
        glEnd();

        // front z face
        glBegin(GL_LINE_LOOP);
        {
            glNormal3f(0.0f, 0.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            glVertex3f(1.0f, -1.0f, 1.0f);
            glVertex3f(1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
        }
        glEnd();

        // back z face
        glBegin(GL_LINE_LOOP);
        {
            glNormal3f(0.0f, 0.0f, -1.0f);
            glVertex3f( 1.0f, 1.0f, -1.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f( 1.0f, -1.0f, -1.0f);
        }
        glEnd();
    }
}
