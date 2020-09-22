package Engine.Renderer;

import Engine.Renderer.Commands.CommandBuffers;
import Engine.Renderer.Commands.CommandPool;
import Engine.Renderer.FrameBuffer.FrameBuffers;
import Engine.Renderer.GraphicsPipeline.GraphicsPipeline;
import Engine.Renderer.Instance.Instance;
import Engine.Renderer.LogicalDevice.LogicalDevice;
import Engine.Renderer.PhysicalDevice.PhysicalDevice;
import Engine.Renderer.RenderPass.RenderPass;
import Engine.Renderer.RenderUpdater.RenderUpdater;
import Engine.Renderer.Swapchain.Swapchain;
import Engine.Renderer.Sync.Sync;
import Engine.Renderer.ValidationLayers.ValidationLayers;
import Engine.Renderer.Window.Window;
import org.lwjgl.vulkan.*;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.vulkan.EXTDebugUtils.vkCreateDebugUtilsMessengerEXT;
import static org.lwjgl.vulkan.VK10.*;

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
    private static ArrayList<Long> swapchainImagesViews;

    private static RenderPass renderPass;
    private static long vkRenderpass;

    private static GraphicsPipeline graphicsPipeline;
    private st  atic long vkGraphicsPipeline;

    private static FrameBuffers frameBuffers;
    private static ArrayList<Long> vkframebuffers;

    private static CommandPool commandPool;
    private static long vkCommandPool;

    private static CommandBuffers commandBuffers;
    private static ArrayList<VkCommandBuffer> vkCommandBuffers;

    private static Sync sync;

    private static RenderUpdater renderUpdater;

    private static int width = 640;
    private static int height = 480;

    public Renderer() {
        instance = new Instance();
        logicalDevice = new LogicalDevice();
        physicalDevice = new PhysicalDevice();
        swapchain = new Swapchain();
        graphicsPipeline = new GraphicsPipeline();
        renderPass = new RenderPass();
        frameBuffers = new FrameBuffers();
        commandPool = new CommandPool();
        commandBuffers = new CommandBuffers();
        renderUpdater = new RenderUpdater();
        sync = new Sync();
    }

    public static void init(){
        glfwInit();
        window = new Window(width, height, "Window");
        window.create();
        vkInstance = instance.create(ValidationLayers.getPointerBuffer());
        surface = window.createSurface(vkInstance);
        ValidationLayers.setupDebugMessenger();
        pDevice = physicalDevice.selectDevice(surface);
        lDevice = logicalDevice.create(physicalDevice, surface);
        vkSwapchain = swapchain.create(physicalDevice,lDevice,window,VK_NULL_HANDLE);
        swapchainImagesViews = swapchain.createSwapchainImageViews(lDevice);
        vkRenderpass = renderPass.create(lDevice, swapchain);
        vkGraphicsPipeline = graphicsPipeline.create(lDevice, swapchain, renderPass);
        vkframebuffers = frameBuffers.create(lDevice, swapchain, renderPass);
        vkCommandPool = commandPool.create(physicalDevice, lDevice);
        vkCommandBuffers = commandBuffers.create(lDevice,swapchain,vkCommandPool);
        commandBuffers.record(swapchain,renderPass,graphicsPipeline,frameBuffers);
        sync.create(lDevice,swapchain , renderUpdater.getMaxFrames());
        renderUpdater.create(logicalDevice,swapchain,commandBuffers,sync);
    }

    public static void resizeWindow(int nWidth, int nHeight){

        renderUpdater.destroy();
        commandPool.destroy(lDevice);
        frameBuffers.destroy(lDevice);
        graphicsPipeline.destroy(lDevice);
        renderPass.destroy(lDevice);
        swapchain.destroy(lDevice);

        window.setWidth(nWidth);
        window.setHeight(nHeight);
        window.resize();

        vkSwapchain = swapchain.create(physicalDevice,lDevice,window,VK_NULL_HANDLE);
        swapchainImagesViews = swapchain.createSwapchainImageViews(lDevice);
        vkRenderpass = renderPass.create(lDevice, swapchain);
        vkGraphicsPipeline = graphicsPipeline.create(lDevice, swapchain, renderPass);
        vkframebuffers = frameBuffers.create(lDevice, swapchain, renderPass);
        vkCommandPool = commandPool.create(physicalDevice, lDevice);
        vkCommandBuffers = commandBuffers.create(lDevice,swapchain,vkCommandPool);
        commandBuffers.record(swapchain,renderPass,graphicsPipeline,frameBuffers);

        renderUpdater.create(logicalDevice,swapchain,commandBuffers,sync);
    }

    public static void cleanUp(){

        renderUpdater.destroy();

        sync.destroy(lDevice);

        commandPool.destroy(lDevice);

        frameBuffers.destroy(lDevice);

        graphicsPipeline.destroy(lDevice);

        renderPass.destroy(lDevice);

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

    public static void render(){
        renderUpdater.update();
    }

    public static Window getWindow() {
        return window;
    }
}
