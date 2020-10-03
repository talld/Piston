package Engine.Renderer.DescriptorSet;

import Engine.Renderer.Swapchain.Swapchain;
import Engine.Renderer.Utilities.ErrorUtilities;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkDescriptorPoolCreateInfo;
import org.lwjgl.vulkan.VkDescriptorPoolSize;
import org.lwjgl.vulkan.VkDevice;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;

public class DescriptorPool {

    private long descriptorPool;
    private VkDevice lDevice;

    public DescriptorPool(){

    }

    public long create(VkDevice lDevice, Swapchain swapchain){

        this.lDevice = lDevice;

        try(MemoryStack stack = MemoryStack.stackPush()) {

            //one descriptor pool for each swapchain image
            VkDescriptorPoolSize.Buffer descriptorPoolSize = VkDescriptorPoolSize.callocStack(1, stack)
                    .type(VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
                    .descriptorCount(swapchain.getSwapchainImagesViews().size()+6);

            VkDescriptorPoolCreateInfo descriptorPoolCreateInfo = VkDescriptorPoolCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO)
                    .pPoolSizes(descriptorPoolSize)
                    .maxSets(swapchain.getSwapchainImagesViews().size()+6)
                    .flags(0);

            LongBuffer pDescriptorPool = stack.longs(VK_NULL_HANDLE);

            int status = vkCreateDescriptorPool(lDevice, descriptorPoolCreateInfo, null, pDescriptorPool);

            if(status != VK_SUCCESS){
                throw new RuntimeException("Failed to create Descriptor Pool: " + ErrorUtilities.getError(status));
            }

            descriptorPool = pDescriptorPool.get(0);

            return descriptorPool;
        }
    }

    public long getDescriptorPool() {
        return descriptorPool;
    }

    public void destroy(){
        vkDestroyDescriptorPool(lDevice, descriptorPool, null);
    }
}
