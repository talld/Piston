package Engine.Renderer.Window;

import org.lwjgl.glfw.*;
import org.lwjgl.system.*;
import org.lwjgl.vulkan.VkInstance;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


import static org.lwjgl.vulkan.KHRSurface.vkDestroySurfaceKHR;
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

    public long createSurface(VkInstance instance){
        try(MemoryStack stack = stackPush()){
            LongBuffer pSurface = stack.longs(0);
            if(glfwCreateWindowSurface(instance,window,null,pSurface)!=VK_SUCCESS){
                throw new RuntimeException("Failed to create window surface");
            }
            surface = pSurface.get();
            return surface;
        }
    }

    public void resize(){
        try(MemoryStack stack = stackPush()) {

            IntBuffer top = stack.ints(0);
            glfwGetWindowFrameSize(window,null,top,null,null);

            glfwSetWindowPos(window,0,top.get());
            glfwSetWindowSize(window, windowWidth, windowHeight);
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

    public void destroySurface(VkInstance instance){
        vkDestroySurfaceKHR(instance,surface,null);
    }

    public long getPointer(){
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
