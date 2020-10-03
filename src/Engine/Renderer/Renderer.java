package Engine.Renderer;

import Engine.Geometry.Vertex;
import Engine.Memory.MemoryUtillities;
import Engine.Objects.Camera.Camera;
import Engine.Objects.Mesh.Mesh;
import Engine.Renderer.Commands.CommandBuffers;
import Engine.Renderer.Commands.CommandPool;
import Engine.Renderer.DescriptorSet.DescriptorPool;
import Engine.Renderer.DescriptorSet.DescriptorSetLayout;
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
import org.joml.Vector3f;
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

    private static DescriptorSetLayout descriptorSetLayout;
    private static long vkDescriptorSetLayout;

    private static GraphicsPipeline graphicsPipeline;
    private static long vkGraphicsPipeline;

    private static FrameBuffers frameBuffers;
    private static ArrayList<Long> vkframebuffers;

    private static DescriptorPool descriptorPool;
    private static long vkDescriptorPool;

    private static CommandPool graphicsCommandPool;
    private static long vkGraphicsCommandPool;

    private static CommandPool transferCommandPool;
    private static long vkTransferCommandPool;

    private static CommandBuffers commandBuffers;
    private static ArrayList<VkCommandBuffer> vkCommandBuffers;

    private static Sync sync;

    private static RenderUpdater renderUpdater;

    private static int width = 640;
    private static int height = 480;

    private static final Vertex[] vertices = new Vertex[]{
            new Vertex(new Vector3f(-0.5f, -0.75f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f)),
            new Vertex(new Vector3f(0f, 0.5f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f)),
            new Vertex(new Vector3f(-1f, 0.5f, 0.0f), new Vector3f(0.0f, 0.0f, 1.0f)),
    };

    private static final short[] indices = new short[]{
            0,1,2
    };

    private static final Vertex[] vertices2 = new Vertex[]{
            new Vertex(new Vector3f(0.5f, -0.75f, 0f), new Vector3f(1.0f, 0.0f, 0.0f)),
            new Vertex(new Vector3f(1f, 0.5f, 0f), new Vector3f(0.0f, 1.0f, 0.0f)),
            new Vertex(new Vector3f(0f, 0.5f, 0f), new Vector3f(0.0f, 0.0f, 1.0f)),
    };

    private static final short[] indices2 = new short[]{
            0,1,2
    };

    private static final Vertex[] vertices3 = new Vertex[]{
            new Vertex(new Vector3f(0f, 0.5f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f)),
            new Vertex(new Vector3f(-0.5f, -0.75f, 0f), new Vector3f(0.0f, 1.0f, 0.0f)),
            new Vertex(new Vector3f(0.5f, -0.75f, 0f), new Vector3f(0.0f, 0.0f, 1.0f)),
    };

    private static final short[] indices3 = new short[]{
            0,1,2
    };

    private static ArrayList<Mesh> meshes;
    private static Camera camera;


    public Renderer() {
        instance = new Instance();
        logicalDevice = new LogicalDevice();
        physicalDevice = new PhysicalDevice();
        swapchain = new Swapchain();
        descriptorSetLayout = new DescriptorSetLayout();
        graphicsPipeline = new GraphicsPipeline();
        renderPass = new RenderPass();
        frameBuffers = new FrameBuffers();
        descriptorPool = new DescriptorPool();
        graphicsCommandPool = new CommandPool();
        transferCommandPool = new CommandPool();
        commandBuffers = new CommandBuffers();
        sync = new Sync();
        camera = new Camera();
        renderUpdater = new RenderUpdater();
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
        vkDescriptorSetLayout = descriptorSetLayout.create(lDevice);
        vkGraphicsPipeline = graphicsPipeline.create(lDevice, swapchain, renderPass, vkDescriptorSetLayout);
        vkframebuffers = frameBuffers.create(lDevice, swapchain, renderPass);

        MemoryUtillities.init(vkInstance, pDevice, lDevice);

        vkDescriptorPool = descriptorPool.create(lDevice,swapchain);
        vkGraphicsCommandPool = graphicsCommandPool.create(physicalDevice, lDevice,0);
        vkTransferCommandPool = transferCommandPool.create(physicalDevice, lDevice,VK_COMMAND_POOL_CREATE_TRANSIENT_BIT);


        Mesh mesh = new Mesh(lDevice, vkTransferCommandPool, logicalDevice.getGraphicsQueue(), swapchain);
        mesh.create(vertices, indices, vkDescriptorSetLayout, vkDescriptorPool);

        Mesh mesh2 = new Mesh(lDevice, vkTransferCommandPool, logicalDevice.getGraphicsQueue(), swapchain);
        mesh2.create(vertices2, indices2, vkDescriptorSetLayout, vkDescriptorPool);

        Mesh mesh3 = new Mesh(lDevice, vkTransferCommandPool, logicalDevice.getGraphicsQueue(), swapchain);
        mesh3.create(vertices3, indices3, vkDescriptorSetLayout, vkDescriptorPool);

        meshes = new ArrayList<Mesh>();
        meshes.add(mesh);
        meshes.add(mesh2);
        meshes.add(mesh3);

        camera.create(lDevice, swapchain, vkDescriptorSetLayout, vkDescriptorPool);

        vkCommandBuffers = commandBuffers.create(lDevice,swapchain, vkGraphicsCommandPool);
        commandBuffers.record(swapchain,renderPass,graphicsPipeline,frameBuffers, meshes, camera);
        sync.create(lDevice,swapchain , renderUpdater.getMaxFrames());
        renderUpdater.create(logicalDevice,swapchain,commandBuffers,sync);
    }

    public static void resizeWindow(int nWidth, int nHeight){

        renderUpdater.destroy();
        graphicsCommandPool.destroy(lDevice);
        camera.destroy();
        descriptorPool.destroy();
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
        vkGraphicsPipeline = graphicsPipeline.create(lDevice, swapchain, renderPass,vkDescriptorSetLayout);
        vkframebuffers = frameBuffers.create(lDevice, swapchain, renderPass);
        vkDescriptorPool = descriptorPool.create(lDevice,swapchain);
        vkGraphicsCommandPool = graphicsCommandPool.create(physicalDevice, lDevice,0);


        camera.recreate(swapchain, vkDescriptorSetLayout, vkDescriptorPool);

        vkCommandBuffers = commandBuffers.create(lDevice,swapchain, vkGraphicsCommandPool);
        commandBuffers.record(swapchain,renderPass,graphicsPipeline,frameBuffers,meshes, camera);
        renderUpdater.create(logicalDevice,swapchain,commandBuffers,sync);
    }

    public static void cleanUp(){

        renderUpdater.destroy();

        camera.destroy();

        for(Mesh mesh : meshes){
            mesh.destroy();
        }

        sync.destroy(lDevice);

        graphicsCommandPool.destroy(lDevice);

        transferCommandPool.destroy(lDevice);

        descriptorPool.destroy();

        MemoryUtillities.destroy();

        frameBuffers.destroy(lDevice);

        graphicsPipeline.destroy(lDevice);

        descriptorSetLayout.destroy(lDevice);

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

        renderUpdater.update(meshes, camera);
    }

    public static Window getWindow() {
        return window;
    }

    public static Camera getCamera(){
        return camera;
    }

    public static void setCamera(float x, float y, float z, float xx, float yy, float zz){
        camera.setX(x);
        camera.setY(y);
        camera.setZ(z);
        camera.setXX(xx);
        camera.setYY(yy);
        camera.setZZ(zz);
        camera.update();
    }
}
