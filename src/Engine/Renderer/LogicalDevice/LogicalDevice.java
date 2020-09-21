package Engine.Renderer.LogicalDevice;

import Engine.Renderer.PhysicalDevice.PhysicalDevice;
import Engine.Renderer.PhysicalDevice.QueueFamilyIndices;
import Engine.Renderer.Utilities.ErrorUtilities;
import Engine.Renderer.Utilities.RenderUtilities;
import Engine.Renderer.ValidationLayers.ValidationLayers;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;

public class LogicalDevice {

    private VkDevice lDevice;

    public VkQueue graphicsQueue;

    public VkQueue presentQueue;

    public LogicalDevice(){

    }

    public VkDevice create(PhysicalDevice device, long surface){
        try(MemoryStack stack = stackPush()){

            QueueFamilyIndices queueFamilyIndices = device.getQueueFamilyIndices();

            Object[] uniqueIndices = queueFamilyIndices.uniqueIndices().toArray();

            VkDeviceQueueCreateInfo.Buffer deviceQueueCreateInfos = VkDeviceQueueCreateInfo.callocStack(uniqueIndices.length,stack);
            FloatBuffer priority = stack.floats(1);

            for(int i = 0; i < uniqueIndices.length; i++) {
                VkDeviceQueueCreateInfo createInfo = deviceQueueCreateInfos.get(i);
                createInfo.sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                        .pQueuePriorities(priority)
                        .queueFamilyIndex((int) uniqueIndices[i]);
            }

            VkPhysicalDeviceFeatures physicalDeviceFeatures = VkPhysicalDeviceFeatures.callocStack(stack);

            VkDeviceCreateInfo deviceCreateInfo = VkDeviceCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
                    .pQueueCreateInfos(deviceQueueCreateInfos)
                    .pEnabledFeatures(physicalDeviceFeatures)
                    .ppEnabledExtensionNames(RenderUtilities.getDeviceRequiredExtensionsPointer())
                    .ppEnabledLayerNames(RenderUtilities.asPointerBuffer(ValidationLayers.getValidationLayers()));

            PointerBuffer pDevice = stack.pointers(VK_NULL_HANDLE);

            int status = vkCreateDevice(device.getPDevice(),deviceCreateInfo,null,pDevice);

            if(status!=VK_SUCCESS){
                throw new RuntimeException("Failed to create logical device: " + ErrorUtilities.getError(status));
            }

            lDevice = new VkDevice(pDevice.get(0),device.getPDevice(),deviceCreateInfo);

            PointerBuffer pQueue = stack.pointers(VK_NULL_HANDLE);

            vkGetDeviceQueue(lDevice,device.getQueueFamilyIndices().graphicsFamilyIndex, 0, pQueue);
            graphicsQueue = new VkQueue(pQueue.get(0), lDevice);



            vkGetDeviceQueue(lDevice, device.getQueueFamilyIndices().presentationFamilyIndex, 0, pQueue);
            presentQueue = new VkQueue(pQueue.get(0), lDevice);

            return lDevice;
        }
    }

    public VkDevice get(){
        return lDevice;
    }

    public void destroy(){
        vkDestroyDevice(lDevice,null);
    }
}