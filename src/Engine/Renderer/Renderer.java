package Engine.Renderer;

import Engine.Renderer.Instance.Instance;
import Engine.Renderer.LogicalDevice.LogicalDevice;
import Engine.Renderer.PhysicalDevice.PhysicalDevice;
import Engine.Renderer.Swapchain.Swapchain;
import Engine.Renderer.ValidationLayers.ValidationLayers;
import Engine.Renderer.Window.Window;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.EXTDebugUtils.vkCreateDebugUtilsMessengerEXT;

public class Renderer {

    private static Window window;
    private static long surface;

    private static Instance instance;
    private static long pInstance;
    private static VkInstance vkInstance;

    private static PhysicalDevice physicalDevice;
    private static VkPhysicalDevice pDevice;

    private static LogicalDevice logicalDevice;
    private static VkDevice lDevice;

    private static Swapchain swapchain;

    public Renderer() {
        instance = new Instance();
        logicalDevice = new LogicalDevice();
        physicalDevice = new PhysicalDevice();
        swapchain = new Swapchain();
    }

    public static void init(){
        try(MemoryStack stack = stackPush()) {//final safety stack just to ensure no memory leaks (as (try stack) automatically de-allocs buffers it allocates)
            glfwInit();
            window = new Window(640, 480, "Window");
            window.create();
            vkInstance = instance.create(ValidationLayers.getPointerBuffer());
            surface = window.createSurface(vkInstance);
            ValidationLayers.setupDebugMessenger();
            pDevice = physicalDevice.selectDevice(surface);
            lDevice = logicalDevice.create(physicalDevice, surface);
            swapchain.create(physicalDevice,lDevice,window);
        }
    }

    public static void cleanUp(){

        swapchain.destroy(lDevice);

        logicalDevice.destroy();

        window.destroySurface(vkInstance);

        if(ValidationLayers.ENABLE_VALIDATION_LAYERS) {
            ValidationLayers.destroyDebugUtilsMessengerEXT(vkInstance, null);
        }

        instance.destroy();

        window.destroy();

        glfwTerminate();
    }

    public static VkInstance getVkInstance() {
        return vkInstance;
    }


    public static boolean running() {
        return !window.isCloseRequested();
    }

    public static void update() {
        window.update();
    }

    public static Window getWindow() {
        return window;
    }
}
