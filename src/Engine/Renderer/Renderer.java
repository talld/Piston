package Engine.Renderer;

import Engine.Renderer.GraphicsPipeline.GraphicsPipeline;
import Engine.Renderer.Instance.Instance;
import Engine.Renderer.LogicalDevice.LogicalDevice;
import Engine.Renderer.PhysicalDevice.PhysicalDevice;
import Engine.Renderer.Swapchain.Swapchain;
import Engine.Renderer.ValidationLayers.ValidationLayers;
import Engine.Renderer.Window.Window;
import Game.Game;
import org.lwjgl.vulkan.*;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.vulkan.EXTDebugUtils.vkCreateDebugUtilsMessengerEXT;
import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;

public class Renderer {

    private static Window window;
    private static long surface;

    private static Instance instance;
    private static VkInstance vkInstance;

    private static PhysicalDevice physicalDevice;
    private static VkPhysicalDevice pDevice;

    private static LogicalDevice logicalDevice;
    private static VkDevice lDevice;

    private static Swapchain swapchain;
    private static long vkSwapchain;

    private static GraphicsPipeline graphicsPipeline;
    private static long vkGraphicsPipeline;


    public Renderer() {
        instance = new Instance();
        logicalDevice = new LogicalDevice();
        physicalDevice = new PhysicalDevice();
        swapchain = new Swapchain();
        graphicsPipeline = new GraphicsPipeline();
    }

    public static void init(){

            glfwInit();
            window = new Window(640, 480, "Window");
            window.create();
            vkInstance = instance.create(ValidationLayers.getPointerBuffer());
            surface = window.createSurface(vkInstance);
            ValidationLayers.setupDebugMessenger();
            pDevice = physicalDevice.selectDevice(surface);
            lDevice = logicalDevice.create(physicalDevice, surface);
            vkSwapchain = swapchain.create(physicalDevice,lDevice,window,VK_NULL_HANDLE);
            swapchain.createSwapchainImageViews(lDevice);
            graphicsPipeline.create(lDevice,swapchain);
    }

    public static void resizeWindow(int width, int height){

        graphicsPipeline.destroy(lDevice);

        swapchain.destroy(lDevice);

        window.setWidth(width);
        window.setHeight(height);
        window.resize();
        swapchain.create(physicalDevice,lDevice,window,VK_NULL_HANDLE);
        swapchain.createSwapchainImageViews(lDevice);
        graphicsPipeline.create(lDevice,swapchain);
    }

    public static void cleanUp(){

        graphicsPipeline.destroy(lDevice);

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
