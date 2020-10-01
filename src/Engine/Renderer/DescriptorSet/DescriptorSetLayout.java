package Engine.Renderer.DescriptorSet;

import Engine.Renderer.Utilities.ErrorUtilities;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding;
import org.lwjgl.vulkan.VkDescriptorSetLayoutCreateInfo;
import org.lwjgl.vulkan.VkDevice;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;

public class DescriptorSetLayout {

    private long pLayout;

    public DescriptorSetLayout(){
        this.pLayout = 0l;
    }

    public long create(VkDevice lDevice){
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkDescriptorSetLayoutBinding.Buffer descriptorSetLayoutBinding = VkDescriptorSetLayoutBinding.callocStack(1, stack)
                    .binding(0)
                    .descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
                    .descriptorCount(1)
                    .stageFlags(VK_SHADER_STAGE_VERTEX_BIT);

            VkDescriptorSetLayoutCreateInfo descriptorSetLayout = VkDescriptorSetLayoutCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO)
                    .pBindings(descriptorSetLayoutBinding);

            LongBuffer pDescriptorSetLayout = stack.mallocLong(1);

            int status = vkCreateDescriptorSetLayout(lDevice, descriptorSetLayout, null, pDescriptorSetLayout);
            if(status != VK_SUCCESS){
                throw new RuntimeException("Failed to create Descriptor Set: " + ErrorUtilities.getError(status));
            }

            pLayout = pDescriptorSetLayout.get(0);
            return pLayout;
        }
    }

    public void destroy(VkDevice lDevice){
        vkDestroyDescriptorSetLayout(lDevice, pLayout, null);
    }
}
