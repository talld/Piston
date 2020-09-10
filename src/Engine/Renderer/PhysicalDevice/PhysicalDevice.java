package Engine.Renderer.PhysicalDevice;

import Engine.Renderer.Renderer;
import Engine.Renderer.Swapchain.SwapchainSupportDetails;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK10.VK_QUEUE_COMPUTE_BIT;

public class PhysicalDevice {

    private PointerBuffer extensionsPointer = null;
    private ArrayList<String> extensions = null;
    private VkPhysicalDevice device;

    public PhysicalDevice(){
    }

    public VkPhysicalDevice selectDevice(long surface) {
        try(MemoryStack stack = stackPush()) {
            IntBuffer deviceCount = stack.ints(0);
            vkEnumeratePhysicalDevices(Renderer.getVkInstance(),deviceCount,null);
            PointerBuffer devices = stack.mallocPointer(deviceCount.get(0));
            vkEnumeratePhysicalDevices(Renderer.getVkInstance(),deviceCount,devices);
            int highestScore = -1;
            VkPhysicalDevice highestDevice = null;
            for(int i = 0; i<devices.capacity(); i++){
                VkPhysicalDevice testedDevice = new VkPhysicalDevice(devices.get(i), Renderer.getVkInstance());
                int score = rateDevice(testedDevice, surface);
                if(highestScore<score){
                    highestScore = score;
                    highestDevice = testedDevice;
                }
            }
            if(highestScore == -1){
                throw new RuntimeException("Failed to find compatible device");
            }
            device = highestDevice;
            return device;
        }
    }

    private int rateDevice(VkPhysicalDevice testedDevice, long surface){

        try(MemoryStack stack = stackPush()) {

            if (!checkDeviceCompatible(testedDevice, surface)) {
                return -1;
            }

            VkPhysicalDeviceProperties properties = VkPhysicalDeviceProperties.callocStack(stack);
            vkGetPhysicalDeviceProperties(testedDevice, properties);

            return properties.limits().maxImageDimension2D() + properties.limits().maxMemoryAllocationCount();
        }
    }

    private boolean checkDeviceCompatible(VkPhysicalDevice testedDevice, long surface){
        if(checkDeviceExtensionsSupported(testedDevice)) {

            //as swapchainSupportDetails is just some device enumerate and some queries its not too expensive to create a few here
            SwapchainSupportDetails tempSwapchainSupportDetails = new SwapchainSupportDetails(testedDevice, surface);

            //does the device have the queues we need and swapchain support
            return (getQueueFamilies(testedDevice, surface).validate()) && tempSwapchainSupportDetails.isValid();

        }
        return false;
    }

    private boolean checkDeviceExtensionsSupported(VkPhysicalDevice testedDevice){
        try(MemoryStack stack = stackPush()) {
            IntBuffer pExtensionCount = stack.ints(-1);

            vkEnumerateDeviceExtensionProperties(testedDevice, (ByteBuffer) null,pExtensionCount,null );
            VkExtensionProperties.Buffer availableExtensions = VkExtensionProperties.callocStack(pExtensionCount.get(0),stack);
            vkEnumerateDeviceExtensionProperties(testedDevice, (ByteBuffer) null,pExtensionCount,availableExtensions);

            ArrayList<String> requiredExtensions = getExtensions();
            for(int i = 0; i<availableExtensions.capacity(); i++){
                requiredExtensions.remove(availableExtensions.extensionNameString());
                if(requiredExtensions.isEmpty()){
                    return true;
                }
            }
            return false;
        }
    }

    public QueueFamilyIndices getQueueFamilies(VkPhysicalDevice device, long surface){

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
                vkGetPhysicalDeviceSurfaceSupportKHR(device, i, surface, pSupported);

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

    public ArrayList<String> getExtensions(){
        if(extensions==null){
            extensions = new ArrayList<String>();
            extensions.add(VK_KHR_SWAPCHAIN_EXTENSION_NAME);
        }
        return extensions;
    }

    public PointerBuffer getDeviceRequiredExtensionsPointer(){
        try(MemoryStack stack = stackPush()){
            if(extensionsPointer==null) {
                extensionsPointer = memAllocPointer(1);

                for(String extensionName : getExtensions()) {
                    ByteBuffer extension = memUTF8(extensionName);
                    extensionsPointer.put(extension);
                }
                extensionsPointer.flip();
            }
            return extensionsPointer;
        }
    }

    public VkPhysicalDevice getDevice() {
        return device;
    }
}
