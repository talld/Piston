package Engine.Renderer;

import Engine.Piston;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.*;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.system.MemoryStack.stackGet;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.util.shaderc.Shaderc.*;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compile_into_spv;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;

public class EngineUtilities {

    private  static PointerBuffer extensions = null;

    private static long createCompiler() {
        long options = shaderc_compile_options_initialize();
        shaderc_compile_options_set_generate_debug_info(options);
        long compiler = shaderc_compiler_initialize();
        if (compiler == NULL) {
            throw new IllegalStateException("failed to create compiler");
        }
        return compiler;
    }

    private static String getFileSource(String path) {
        try {
            InputStream in = new FileInputStream(path);
            int size = in.available();
            byte[] b = new byte[size];
            in.read(b, 0, size);
            return new String(b, StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static long createShaderModule(String path, String name, int stage, VkDevice device) {
        VkShaderModuleCreateInfo cInfo = VkShaderModuleCreateInfo.calloc();
        long c = createCompiler();
        cInfo.sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO);
        long result = shaderc_compile_into_spv(c, getFileSource(path), stage, name, "main", 0l);
        cInfo.pCode(shaderc_result_get_bytes(result));
        LongBuffer pModule = memAllocLong(1);
        int check = vkCreateShaderModule(device, cInfo, null, pModule);
        if (check != VK_SUCCESS) {
            throw new IllegalStateException("Failed to create shader:" + name);
        }
        long module = pModule.get();
        memFree(pModule);
        return module;
    }

    public static VkPhysicalDevice selectDevice() {
        try(MemoryStack stack = stackPush()) {
            IntBuffer deviceCount = stack.ints(0);
            vkEnumeratePhysicalDevices(VRenderer.getInstance(),deviceCount,null);
            PointerBuffer deivces = stack.mallocPointer(deviceCount.get(0));
            vkEnumeratePhysicalDevices(VRenderer.getInstance(),deviceCount,deivces);
            int highestScore = -1;
            VkPhysicalDevice highestDevice = null;
            for(int i = 0; i<deivces.capacity(); i++){
                VkPhysicalDevice device = new VkPhysicalDevice(deivces.get(i), VRenderer.getInstance());
                int score = rateDevice(device);
                if(highestScore<score){
                    highestScore = score;
                    highestDevice = device;
                }
            }
            if(highestScore == -1){
                throw new RuntimeException("Failed to find compatible device");
            }
            return highestDevice;
        }
    }

    private static int rateDevice(VkPhysicalDevice device){

        try(MemoryStack stack = stackPush()) {

            if (!checkDeviceCompatible(device)) {
                return -1;
            }

            VkPhysicalDeviceProperties properties = VkPhysicalDeviceProperties.callocStack(stack);
            vkGetPhysicalDeviceProperties(device, properties);

            return properties.limits().maxImageDimension2D() + properties.limits().maxMemoryAllocationCount();
        }
    }

    private static boolean checkDeviceCompatible(VkPhysicalDevice device){
            return (getQueueFamilies(device).validate());
    }

    public static QueueFamilyIndices getQueueFamilies(VkPhysicalDevice device){

        try(MemoryStack stack = stackPush()) {


            QueueFamilyIndices queueFamilyIndices = new QueueFamilyIndices();

            IntBuffer queueFamilyCount = stack.ints(0);
            vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, null);

            VkQueueFamilyProperties.Buffer queueFamilies = VkQueueFamilyProperties.callocStack(queueFamilyCount.get(0), stack);
            vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, queueFamilies);

            boolean graphicsQueueFound = false;
            boolean presentationQueueFound = false;
            boolean computeQueueFound = false;

            for (int i = 0; i < queueFamilies.capacity(); i++) {

                if ((queueFamilies.get(i).queueFlags() & VK_QUEUE_GRAPHICS_BIT)==VK_QUEUE_GRAPHICS_BIT && !graphicsQueueFound) {
                    queueFamilyIndices.graphicsFamilyIndex = i;
                    graphicsQueueFound = true;
                }
                IntBuffer pSupported = stack.ints(VK_FALSE);
                vkGetPhysicalDeviceSurfaceSupportKHR(device, i, Piston.getWindow().getSurface(), pSupported);

                if ((pSupported.get(0) == VK_SUCCESS) && !presentationQueueFound) {
                    queueFamilyIndices.presentationFamilyIndex = i;
                    presentationQueueFound = true;
                }

                if ((queueFamilies.get(i).queueFlags() & VK_QUEUE_COMPUTE_BIT)==VK_QUEUE_COMPUTE_BIT && !computeQueueFound) {
                    queueFamilyIndices.computeFamilyIndex = i;
                    computeQueueFound = true;
                }
                if (graphicsQueueFound && presentationQueueFound && computeQueueFound) {
                    break;
                }
            }

            return queueFamilyIndices;
        }
    }

    public static PointerBuffer getDeviceRequiredExtensions(){
        try(MemoryStack stack = stackPush()){
            if(extensions==null) {
               extensions = memAllocPointer(1);

                ByteBuffer VK_KHR_SWAPCHAIN_EXTENSION = memUTF8(VK_KHR_SWAPCHAIN_EXTENSION_NAME);
                extensions.put(VK_KHR_SWAPCHAIN_EXTENSION);

                extensions.flip();
            }
            return extensions;
        }
    }
}