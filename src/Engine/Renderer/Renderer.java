package Engine.Renderer;

import Engine.Instance.Instance;
import Engine.LogicalDevice.LogicalDevice;
import Engine.Utilities.EngineUtilities;
import Engine.ValidationLayers.ValidationLayers;
import Engine.Window.Window;
import org.lwjgl.vulkan.*;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.vulkan.EXTDebugUtils.vkCreateDebugUtilsMessengerEXT;

public class Renderer {

    private static Window window;
    private static long surface;

    private static Instance instance;
    private static long pInstance;
    private static VkInstance vkInstance;

    private static VkPhysicalDevice device;
    private static LogicalDevice logicalDevice;
    private static VkDevice lDevice;

    public Renderer() {
        instance = new Instance();
        logicalDevice = new LogicalDevice();
    }

    public static void init(){
        glfwInit();
        window = new Window(640, 480, "Window");
        window.create();
        vkInstance = instance.create(ValidationLayers.getPointerBuffer());
        surface = window.createSurface(vkInstance);
        ValidationLayers.setupDebugMessenger();
        device = EngineUtilities.selectDevice(surface);
        lDevice = logicalDevice.create(device,surface);
    }

    public static void cleanUp(){

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
