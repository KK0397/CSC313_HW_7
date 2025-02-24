// javac -classpath ".;C:\Program Files\lwjgl-release-3.3.4-custom\*" CubeSpinner.java
// java -classpath ".;C:\Program Files\lwjgl-release-3.3.4-custom\*" CubeSpinner

// To add lwjgl jars: ctrl+alt+shift+s -> modules -> + -> select folder -> "apply"

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class CubeSpinner {
    private long window;
    private float angleX = 0.0f;
    private float angleY = 0.0f;
    private float speed = 0.1f;

    public void run() {
        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalArgumentException("Unable to initialize GLFW");
        }
            // Configure GLFW
            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2); //3);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1); //2);
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

            // Create the window
            window = glfwCreateWindow(300, 300, "Cube Spinner", NULL, NULL);
            if (window == NULL) {
                throw new RuntimeException("Failed to create the GLFW window");
            }
            // Setupt a key callback. It will be called every time a key is pressed, repeated or released.
            glfwSetKeyCallback(window, (window, key, scancode, action, mods) ->
            {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    glfwSetWindowShouldClose(window, true);
                }
            });

            // Get the thread stack and push a new frame
            try (MemoryStack stack = stackPush()) {
                IntBuffer pWidth = stack.mallocInt(1);
                IntBuffer pHeight = stack.mallocInt(1);

                // Get the window size passed to glfwCreateWindow
                glfwGetWindowSize(window, pWidth, pHeight);

                // Get the resolution of the primary monitor
                GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

                // Center the window
                glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2,
                        (vidmode.height() - pHeight.get(0)) / 2);
            }

            glfwMakeContextCurrent(window);
            GL.createCapabilities();

            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            gluPerspective(45.0f, (float) 300 / (float) 300, 0.1f, 100.0f);
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();

            int errorCode = glGetError();
            if (errorCode != GL_NO_ERROR) {
                throw new RuntimeException("OpenGL error after context creation: " + errorCode);
            }
            glEnable(GL_DEPTH_TEST);
            glDepthFunc(GL_LEQUAL);
            glClearDepth(1.0f);
            glfwSwapInterval(1);

            // Make the window visible
            glfwShowWindow(window);
    }

    private void loop() {
        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        while (!glfwWindowShouldClose(window)) {
            handleInput();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glLoadIdentity();

            glTranslatef(0.0f, 0.0f, -5.0f);
            glRotatef(angleX, 1.0f, 0.0f, 0.0f);
            glRotatef(angleY, 0.0f, 1.0f, 0.0f);

            drawCube();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void gluPerspective(float fovY, float aspect, float zNear, float zFar) {
        float fH = (float) Math.tan(fovY / 360 * Math.PI) * zNear;
        float fW = fH * aspect;
        glFrustum(-fW, fW, -fH, fH, zNear, zFar);
    }

    private void handleInput() {
        if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS) {
            angleX -= speed;
        }
        if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS) {
            angleX += speed;
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS) {
            angleY -= speed;
        }
        if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS) {
            angleY += speed;
        }
    }

    private void drawCube() {
        glBegin(GL_QUADS);

        // Front face
        glColor3f(1.0f, 0.0f, 0.0f);
        glVertex3f(-1.0f, -1.0f, 1.0f);
        glVertex3f(1.0f, -1.0f, 1.0f);
        glVertex3f(1.0f, 1.0f, 1.0f);
        glVertex3f(-1.0f, 1.0f, 1.0f);

        // Back face
        glColor3f(0.0f, 1.0f, 0.0f);
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glVertex3f(-1.0f, 1.0f, -1.0f);
        glVertex3f(1.0f, 1.0f, -1.0f);
        glVertex3f(1.0f, -1.0f, -1.0f);

        // Top face
        glColor3f(0.0f, 0.0f, 1.0f);
        glVertex3f(-1.0f, 1.0f, -1.0f);
        glVertex3f(-1.0f, 1.0f, 1.0f);
        glVertex3f(1.0f, 1.0f, 1.0f);
        glVertex3f(1.0f, 1.0f, -1.0f);

        // Bottom face
        glColor3f(1.0f, 1.0f, 0.0f);
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glVertex3f(1.0f, -1.0f, -1.0f);
        glVertex3f(1.0f, -1.0f, 1.0f);
        glVertex3f(-1.0f, -1.0f, 1.0f);

        // Right face
        glColor3f(1.0f, 0.0f, 1.0f);
        glVertex3f(1.0f, -1.0f, -1.0f);
        glVertex3f(1.0f, 1.0f, -1.0f);
        glVertex3f(1.0f, 1.0f, 1.0f);
        glVertex3f(1.0f, -1.0f, 1.0f);

        // Left face
        glColor3f(0.0f, 1.0f, 1.0f);
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glVertex3f(-1.0f, -1.0f, 1.0f);
        glVertex3f(-1.0f, 1.0f, 1.0f);
        glVertex3f(-1.0f, 1.0f, -1.0f);

        glEnd();
    }

    public static void main(String[] args) {
        new CubeSpinner().run();
    }
}
