package Engine.Renderer;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VKCapabilitiesInstance;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;

import static Engine.Renderer.RenderUtil.VkInit;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


import static org.lwjgl.glfw.GLFW.*;

public class Window {

    private static long context;
    private static GLFWVidMode videoMode;
    public static void createWindow(int width, int height, String title, int vis, int resize){

        if(!glfwInit()) throw new IllegalStateException("GLFW init failed");

        glfwWindowHint(GLFW_CLIENT_API,GLFW_NO_API);
        glfwWindowHint(GLFW_RESIZABLE,GLFW_FALSE);
        context = glfwCreateWindow(width,height,title,NULL,NULL);
        setCurrent();
        if(context == 0l) throw new IllegalStateException("Window init failed");


        GLFW.glfwShowWindow(context); // Make the window visible
    }

    public static void render(){
        glfwPollEvents();

        glfwSwapBuffers(context);

    }

    public static boolean isCloseRequested(){
        return glfwWindowShouldClose(context);
    }

    public static void dispose(){

        glfwDestroyWindow(context);
        glfwTerminate();
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

    }
}
