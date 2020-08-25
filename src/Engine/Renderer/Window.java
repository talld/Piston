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

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

public class Window {

    private static long context;
    private static long surface;
    private static int windowWidth;
    private static int windowHeight;


    public Window(int width, int height, String title){

    }

    public static boolean isCloseRequested(){
        return glfwWindowShouldClose(context);
    }

    public static void destroy(){

        glfwDestroyWindow(context);
        glfwTerminate();
    }

    public static long getContext(){
        return context;
    }

    public static void setTitle(String title){
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

    public static long getSurface() {
        return surface;
    }
}
