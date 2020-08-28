package Engine.Renderer;

import Engine.Piston;
import org.lwjgl.vulkan.*;

import static org.lwjgl.vulkan.EXTDebugUtils.vkCreateDebugUtilsMessengerEXT;
import static org.lwjgl.vulkan.VK10.*;

public class VRenderer {

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
        instance = vInstance.create(VValidationLayers.getPointerBuffer());
        VValidationLayers.setupDebugMessenger();
        Piston.getWindow().createSurface();
        device = EngineUtilities.selectDevice();
        lDevice = vLogicalDevice.create(device);
    }

    public static void cleanUp(){

        vkDestroyDevice(lDevice,null);

        if(VValidationLayers.ENABLE_VALIDATION_LAYERS) {
            VValidationLayers.destroyDebugUtilsMessengerEXT(instance, null);
        }

        vkDestroyInstance(instance,null);
    }

    public static VkInstance getInstance() {
        return instance;
    }




}
