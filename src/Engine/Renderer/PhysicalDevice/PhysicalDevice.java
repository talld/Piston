package Engine.Renderer.PhysicalDevice;

import Engine.Renderer.Renderer;
import Engine.Renderer.Swapchain.SwapchainSupportDetails;
import Engine.Renderer.Utilities.RenderUtilities;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.system.Configuration.DEBUG;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.vulkan.KHRDisplay.vkGetDisplayModePropertiesKHR;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK10.VK_QUEUE_COMPUTE_BIT;

public class PhysicalDevice {


    private VkPhysicalDevice pDevice;
    private QueueFamilyIndices indices;

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
                int score = rateDevice(testedDevice, surface, stack);
                if(score>highestScore){
                    highestScore = score;
                    highestDevice = testedDevice;
                    this.indices = findQueueFamilies(highestDevice, surface);
                }
            }
            if(highestScore == -1){
                throw new RuntimeException("Failed to find compatible device");
            }

            if(DEBUG.get(true)) {
                VkPhysicalDeviceProperties properties = VkPhysicalDeviceProperties.callocStack(stack);
                vkGetPhysicalDeviceProperties(highestDevice, properties);
                System.out.println("Device " + properties.deviceNameString() + " selected");
            }

            pDevice = highestDevice;
            return pDevice;
        }
    }

    private int rateDevice(VkPhysicalDevice testedDevice, long surface, MemoryStack stack){

            if (!checkDeviceCompatible(testedDevice, surface,stack)) {
                return -1;
            }

            VkPhysicalDeviceProperties properties = VkPhysicalDeviceProperties.callocStack(stack);
            vkGetPhysicalDeviceProperties(testedDevice, properties);

            return properties.limits().maxImageDimension2D() + properties.limits().maxVertexInputBindings();
    }

    private boolean checkDeviceCompatible(VkPhysicalDevice testedDevice, long surface, MemoryStack stack){
        if(checkDeviceExtensionsSupported(testedDevice)) {

            //as swapchainSupportDetails is just some device enumerate and some queries its not too expensive to create a few here
            SwapchainSupportDetails tempSwapchainSupportDetails = new SwapchainSupportDetails(testedDevice, surface);
            tempSwapchainSupportDetails.selectFormat(stack);
            tempSwapchainSupportDetails.selectPresentMode(stack);

            //does the device have the queues we need and swapchain support
            return (findQueueFamilies(testedDevice, surface).validate() && tempSwapchainSupportDetails.isValid());

        }
        return false;
    }

    private boolean checkDeviceExtensionsSupported(VkPhysicalDevice testedDevice){
        try(MemoryStack stack = stackPush()) {
            IntBuffer pExtensionCount = stack.ints(-1);

            vkEnumerateDeviceExtensionProperties(testedDevice, (ByteBuffer) null,pExtensionCount,null );
            VkExtensionProperties.Buffer availableExtensions = VkExtensionProperties.callocStack(pExtensionCount.get(0),stack);
            vkEnumerateDeviceExtensionProperties(testedDevice, (ByteBuffer) null,pExtensionCount,availableExtensions);

            ArrayList<String> requiredExtensions = RenderUtilities.getExtensions();
            for(int i = 0; i<availableExtensions.capacity(); i++){
                requiredExtensions.remove(availableExtensions.get(i).extensionNameString());
                if(requiredExtensions.isEmpty()){
                    return true;
                }
            }
            for(int i = 0; i<requiredExtensions.size(); i++) {
              if(DEBUG.get(true)){
                  VkPhysicalDeviceProperties properties = VkPhysicalDeviceProperties.callocStack(stack);
                  vkGetPhysicalDeviceProperties(testedDevice,properties);
                  System.out.println("Device " + properties.deviceNameString() + properties.deviceID() + " Failed check");
                  System.out.println("Failed to find extension: " + requiredExtensions.get(i));
              }
            }
            return false;
        }
    }

    public QueueFamilyIndices findQueueFamilies(VkPhysicalDevice device, long surface){
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

                if ((queueFamilies.get(i).queueFlags() & VK_QUEUE_TRANSFER_BIT)==VK_QUEUE_TRANSFER_BIT){
                    queueFamilyIndices.addTransferQueue(i);
                }

                if ((queueFamilies.get(i).queueFlags() & VK_QUEUE_GRAPHICS_BIT)==VK_QUEUE_GRAPHICS_BIT && !graphicsQueueFound) {
                    queueFamilyIndices.setGraphicsFamilyIndex(i);
                    graphicsQueueFound = true;
                }
                IntBuffer pSupported = stack.ints(-1);
                vkGetPhysicalDeviceSurfaceSupportKHR(device, i, surface, pSupported);

                if ((pSupported.get(0) == VK_TRUE) && !presentationQueueFound) {
                    queueFamilyIndices.setPresentationFamilyIndex(i);
                    presentationQueueFound = true;
                }

                if ((queueFamilies.get(i).queueFlags() & VK_QUEUE_COMPUTE_BIT)==VK_QUEUE_COMPUTE_BIT && !computeQueueFound) {
                    queueFamilyIndices.setComputeFamilyIndex(i);
                    computeQueueFound = true;
                }
                if (graphicsQueueFound && presentationQueueFound && computeQueueFound) {
                    break;
                }
            }

            return queueFamilyIndices;
        }
    }

    public VkPhysicalDevice getPDevice() {
        return pDevice;
    }

    public QueueFamilyIndices getQueueFamilyIndices(){
        return indices;
    }
}
