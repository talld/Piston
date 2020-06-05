package Engine;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


import static org.lwjgl.glfw.GLFW.*;

public class Window {

    private static long context;
    private static GLFWVidMode videoMode;
    public static void createWindow(int width, int height, String title, int vis, int resize){

        if(!glfwInit()) throw new IllegalStateException("GLFW init failed");
        videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE,vis);
        glfwWindowHint(GLFW_RESIZABLE,resize);
        context = glfwCreateWindow(width,height,title,NULL,NULL);
        if(context == 0l) throw new IllegalStateException("Window init failed");
        // Make the window visible
        GLFW.glfwShowWindow(context);
    }

    public static void render(){
        glfwPollEvents();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        GLFW.glfwSwapBuffers(context);
    }

    public static boolean isCloseRequested(){
        return glfwWindowShouldClose(context);
    }

    public static void dispose(){
        GLFW.glfwDestroyWindow(context);
    }

    public static long getContex(){
        return context;
    }

    public static void setTile(String title){
        GLFW.glfwSetWindowTitle(context,title);
    }

    public static int getWidth(){
        return videoMode.width();
    }

    public static int getheight(){
        return videoMode.height();
    }

    public static void setCurrent(){
        glfwMakeContextCurrent(context);
        GL.createCapabilities();
    }
}
