package Engine.Renderer;

import Engine.Renderer.Objects.QueueFamilyIndices;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.lwjgl.vulkan.VK10.*;


import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;


import static Engine.Renderer.RenderUtil.getQueueFamilies;
import static java.util.stream.Collectors.toSet;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;

public class VLogicalDevice {

    private static final Set<String> DEVICE_EXTENSIONS = new HashSet<>();
        

    public VLogicalDevice(){
        DEVICE_EXTENSIONS.add(VK_KHR_SWAPCHAIN_EXTENSION_NAME);

    }

    public long create(VkPhysicalDevice device){
        try(MemoryStack stack = stackPush()){

            QueueFamilyIndices queueFamilyIndices = getQueueFamilies(device);



            VkDeviceQueueCreateInfo.Buffer deviceQueueCreateInfo = VkDeviceQueueCreateInfo.callocStack(3,stack);
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
                    .ppEnabledExtensionNames(null);


            PointerBuffer pDevice = stack.pointers(0);
            if(vkCreateDevice(device,deviceCreateInfo,null,pDevice)!=VK_SUCCESS){
                throw new RuntimeException("Failed to create logical device");
            }

            return pDevice.get();
        }
    }
}
