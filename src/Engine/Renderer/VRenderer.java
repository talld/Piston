package Engine.Renderer;

import Engine.Piston;
import org.lwjgl.vulkan.*;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.vulkan.EXTDebugUtils.vkCreateDebugUtilsMessengerEXT;
import static org.lwjgl.vulkan.KHRSurface.vkDestroySurfaceKHR;
import static org.lwjgl.vulkan.VK10.*;

public class VRenderer {

    private static Window window;
    private static long surface;

    private static VInstance vInstance;
    private static long pInstance;
    private static VkInstance instance;

    private static VkPhysicalDevice device;
    private static VLogicalDevice vLogicalDevice;
    private static VkDevice lDevice;

    public VRenderer() {
        vInstance = new VInstance();
        vLogicalDevice = new VLogicalDevice();
    }

    public static void init(){
        glfwInit();
        window = new Window(640, 480, "Window");
        window.create();
        instance = vInstance.create(VValidationLayers.getPointerBuffer());
        surface = window.createSurface(instance);
        VValidationLayers.setupDebugMessenger();
        device = EngineUtilities.selectDevice(surface);
        lDevice = vLogicalDevice.create(device,surface);
    }

    public static void cleanUp(){

        vLogicalDevice.destroy();

        window.destroySurface(instance);

        if(VValidationLayers.ENABLE_VALIDATION_LAYERS) {
            VValidationLayers.destroyDebugUtilsMessengerEXT(instance, null);
        }

        vInstance.destroy();

        window.destroy();

        glfwTerminate();
    }

    public static VkInstance getInstance() {
        return instance;
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
