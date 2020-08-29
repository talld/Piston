package Engine.Renderer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;


import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Set;


import static Engine.Renderer.EngineUtilities.getDeviceRequiredExtensionsPointer;
import static Engine.Renderer.EngineUtilities.getQueueFamilies;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;

public class VLogicalDevice {

    private VkDevice lDevice;

    public VLogicalDevice(){

    }

    public VkDevice create(VkPhysicalDevice device, long surface){
        try(MemoryStack stack = stackPush()){

            QueueFamilyIndices queueFamilyIndices = getQueueFamilies(device, surface);

            Object[] uniqueIndices = queueFamilyIndices.uniqueIndices().toArray();

            VkDeviceQueueCreateInfo.Buffer deviceQueueCreateInfo = VkDeviceQueueCreateInfo.callocStack(uniqueIndices.length,stack);
            FloatBuffer priority = stack.floats(1);

            for(int i = 0; i < uniqueIndices.length; i++) {
                VkDeviceQueueCreateInfo createInfo = deviceQueueCreateInfo.get(i);
                createInfo.sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                        .pQueuePriorities(priority)
                        .queueFamilyIndex((Integer) uniqueIndices[i]);
            }

            VkPhysicalDeviceFeatures physicalDeviceFeatures = VkPhysicalDeviceFeatures.callocStack(stack);


            VkDeviceCreateInfo deviceCreateInfo = VkDeviceCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
                    .pQueueCreateInfos(deviceQueueCreateInfo)
                    .pEnabledFeatures(physicalDeviceFeatures)
                    .ppEnabledExtensionNames(getDeviceRequiredExtensionsPointer());
            PointerBuffer pDevice = stack.pointers(VK_NULL_HANDLE);

            if(vkCreateDevice(device,deviceCreateInfo,null,pDevice)!=VK_SUCCESS){
                throw new RuntimeException("Failed to create logical device");
            }

            lDevice = new VkDevice(pDevice.get(),device,deviceCreateInfo);

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
