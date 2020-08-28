package Engine.Renderer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;


import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Set;


import static Engine.Renderer.EngineUtilities.getDeviceRequiredExtensions;
import static Engine.Renderer.EngineUtilities.getQueueFamilies;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;

public class VLogicalDevice {


    public VLogicalDevice(){

    }

    public VkDevice create(VkPhysicalDevice device){
        try(MemoryStack stack = stackPush()){

            QueueFamilyIndices queueFamilyIndices = getQueueFamilies(device);

            VkDeviceQueueCreateInfo.Buffer deviceQueueCreateInfo = VkDeviceQueueCreateInfo.callocStack(queueFamilyIndices.uniqueIndices().size(),stack);
            FloatBuffer priority = stack.floats(1);
            for(Object index : queueFamilyIndices.uniqueIndices()) {
                VkDeviceQueueCreateInfo createInfo = deviceQueueCreateInfo.get();
                createInfo.sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                        .pQueuePriorities(priority)
                        .queueFamilyIndex((Integer) index);
            }

            VkPhysicalDeviceFeatures physicalDeviceFeatures = VkPhysicalDeviceFeatures.callocStack(stack);




            VkDeviceCreateInfo deviceCreateInfo = VkDeviceCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
                    .pQueueCreateInfos(deviceQueueCreateInfo)
                    .pEnabledFeatures(physicalDeviceFeatures)
                    .ppEnabledExtensionNames(getDeviceRequiredExtensions());
            PointerBuffer pDevice = stack.pointers(VK_NULL_HANDLE);

            if(vkCreateDevice(device,deviceCreateInfo,null,pDevice)!=VK_SUCCESS){
                throw new RuntimeException("Failed to create logical device");
            }

            return new VkDevice(pDevice.get(),device,deviceCreateInfo);
        }
    }
}
