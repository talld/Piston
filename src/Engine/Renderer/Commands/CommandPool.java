package Engine.Renderer.Commands;

import Engine.Renderer.PhysicalDevice.PhysicalDevice;
import Engine.Renderer.PhysicalDevice.QueueFamilyIndices;
import Engine.Renderer.Utilities.ErrorUtilities;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;
import org.lwjgl.vulkan.VkDevice;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class CommandPool {

    private long commandPool;

    public CommandPool(){

    }

    public long create(PhysicalDevice pDevice, VkDevice lDevice, int flags){
        try(MemoryStack stack = stackPush()){

            QueueFamilyIndices queueFamilyIndices = pDevice.getQueueFamilyIndices();

            VkCommandPoolCreateInfo commandPoolCreateInfo = VkCommandPoolCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
                    .queueFamilyIndex(queueFamilyIndices.getGraphicsFamilyIndex())
                    .flags(flags);

            LongBuffer pCommandPool = stack.longs(VK_NULL_HANDLE);

            int status = vkCreateCommandPool(lDevice, commandPoolCreateInfo, null, pCommandPool);

            if(status != VK_SUCCESS){
                throw new RuntimeException("Failed to create CommandPool: " + ErrorUtilities.getError(status));
            }

            commandPool = pCommandPool.get(0);

        }
        return commandPool;
    }

    public long getCommandPool() {
        return commandPool;
    }

    public void destroy(VkDevice lDevice){
        vkDestroyCommandPool(lDevice, commandPool, null);
    }

}
