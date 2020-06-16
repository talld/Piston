package Engine.Renderer;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.glfw.GLFWVulkan.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VKCapabilitiesInstance;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;

import java.nio.LongBuffer;

import static Engine.Renderer.RenderUtil.VkInit;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

public class Window {

    private static long context;
    private static GLFWVidMode videoMode;
    private static long surface;
    private static int windowWidth;
    private static int windowHeight;


    public static void createWindow(int width, int height, String title, int vis, int resize){

        glfwWindowHint(GLFW_CLIENT_API,GLFW_NO_API);                           //disabling the default OpenGL window settings
        glfwWindowHint(GLFW_RESIZABLE,GLFW_FALSE);                             //disabling the ability to resize the window until it can be accounted for
        windowWidth = width;
        windowHeight = height;
        context = glfwCreateWindow(windowWidth,windowHeight,title,NULL,NULL);  //create window and pointer to its context
        setCurrent();
        if(context == 0) throw new IllegalStateException("Window init failed");

        LongBuffer pSurface = memAllocLong(1);
        int check = glfwCreateWindowSurface(RenderUtil.getInstance(),context,null,pSurface);
        if(check != VK_SUCCESS){
            throw new IllegalStateException("Failed to create surface");
        }
        surface = pSurface.get(0); //set surface to Vulkan to use
        memFree(pSurface);
        GLFW.glfwShowWindow(context); // Make the window visible
    }

    public static void update(){
        // Handle window resize TODO window resizing maths
        GLFWWindowSizeCallback windowSizeCallback = new GLFWWindowSizeCallback() {
            public void invoke(long window, int width, int height) {
                if (width <= 0 || height <= 0)
                    return;
                width = width;
                height = height;
            }
        };


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

    public static long getContext(){
        return context;
    }

    public static void setTile(String title){
        GLFW.glfwSetWindowTitle(context,title);
    }

    public static int getWidth(){
        return windowWidth;
    }

    public static int getHeight(){
        return windowHeight;
    }

    public static void setHeight(int height){
        windowHeight = height;
    }

    public static void setWidth(int width){
        windowWidth = width;
    }

    public static void setCurrent(){glfwMakeContextCurrent(context);}

    public static void glfwPreInit(){
        if(!glfwInit()) throw new IllegalStateException("GLFW init failed");
    }

    public static long getSurface() {
        return surface;
    }
}
