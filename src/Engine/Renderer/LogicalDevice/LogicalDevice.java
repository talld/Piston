package Engine.Renderer.LogicalDevice;

import Engine.Renderer.PhysicalDevice.PhysicalDevice;
import Engine.Renderer.PhysicalDevice.QueueFamilyIndices;
import Engine.Renderer.ValidationLayers.ValidationLayers;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Pointer;
import org.lwjgl.vulkan.*;


import java.nio.FloatBuffer;
import java.util.ArrayList;


import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class LogicalDevice {

    private VkDevice lDevice;

    public VkQueue graphicsQueue;

    public VkQueue presentQueue;

    public LogicalDevice(){

    }

    public VkDevice create(PhysicalDevice device, long surface){
        try(MemoryStack stack = stackPush()){

            QueueFamilyIndices queueFamilyIndices = device.findQueueFamilies(device.getDevice(), surface);

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
                    .ppEnabledExtensionNames(device.getDeviceRequiredExtensionsPointer())
                    .ppEnabledLayerNames(ValidationLayers.getPointerBuffer());
            PointerBuffer pDevice = stack.pointers(VK_NULL_HANDLE);

            if(vkCreateDevice(device.getDevice(),deviceCreateInfo,null,pDevice)!=VK_SUCCESS){
                throw new RuntimeException("Failed to create logical device");
            }

            lDevice = new VkDevice(pDevice.get(0),device.getDevice(),deviceCreateInfo);


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
