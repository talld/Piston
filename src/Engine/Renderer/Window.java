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

    private long window;
    private long surface;
    private int windowWidth;
    private int windowHeight;
    private String title;


    public Window(int width, int height, String title){
        this.windowWidth = width;
        this.windowHeight = height;
        this.title = title;
    }

    public void create(){
        glfwWindowHint(GLFW_CLIENT_API,GLFW_NO_API);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(windowWidth,windowHeight,title,NULL,NULL);

    }

    public void createSurface(){
        try(MemoryStack stack = stackPush()){
            LongBuffer pSurface = stack.longs(0);
            if(glfwCreateWindowSurface(VRenderer.getInstance(),window,null,pSurface)!=VK_SUCCESS){
                throw new RuntimeException("Failed to create window surface");
            }
            surface = pSurface.get();
        }
    }

    public void update(){
        glfwPollEvents();
    }

    public boolean isCloseRequested(){
        return glfwWindowShouldClose(window);
    }

    public void destroy(){
        glfwDestroyWindow(window);
    }

    public long getWindow(){
        return window;
    }

    public void setTitle(String title){
        GLFW.glfwSetWindowTitle(window,title);
    }

    public int getWidth(){
        return windowWidth;
    }

    public int getHeight(){
        return windowHeight;
    }

    public void setHeight(int height){
        windowHeight = height;
    }

    public void setWidth(int width){
        windowWidth = width;
    }

    public long getSurface() {
        return surface;
    }
}
