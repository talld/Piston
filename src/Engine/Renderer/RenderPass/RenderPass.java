package Engine.Renderer.RenderPass;

import Engine.Renderer.Swapchain.Swapchain;
import Engine.Renderer.Utilities.ErrorUtilities;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

public class RenderPass {

    private long renderPass;

    public RenderPass(){

    }

    public long create(VkDevice lDevice, Swapchain swapchain) {
        try (MemoryStack stack = stackPush()) {

            VkAttachmentDescription.Buffer colourAttachmentDescriptions = VkAttachmentDescription.callocStack(1,stack)
                    .format(swapchain.getImageFormat())
                    .samples(VK_SAMPLE_COUNT_1_BIT)
                    .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
                    .storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                    .stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                    .stencilLoadOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                    .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                    .finalLayout(VK_IMAGE_LAYOUT_PRESENT_SRC_KHR);

            VkAttachmentReference.Buffer colourAttachmentReference = VkAttachmentReference.callocStack(1,stack)
                    .attachment(0)
                    .layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);

            VkSubpassDescription.Buffer subpassDescriptions = VkSubpassDescription.callocStack(1,stack)
                    .pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS)
                    .pColorAttachments(colourAttachmentReference)
                    .colorAttachmentCount(1);

            VkSubpassDependency.Buffer subpassDependency = VkSubpassDependency.callocStack(1, stack)
                .srcSubpass(VK_SUBPASS_EXTERNAL)
                .dstSubpass(0)
                .srcStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT)
                .srcAccessMask(0)
                .dstStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT)
                .dstAccessMask(VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT);

            VkRenderPassCreateInfo renderPassCreateInfo = VkRenderPassCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO)
                    .pAttachments(colourAttachmentDescriptions)
                    .pSubpasses(subpassDescriptions)
                    .pDependencies(subpassDependency);

            LongBuffer pRenderPass = stack.mallocLong(1);

            int status = vkCreateRenderPass(lDevice, renderPassCreateInfo, null, pRenderPass);

            if(status != VK_SUCCESS){
                throw new RuntimeException("Failed to create Render     pass: " + ErrorUtilities.getError(status));
            }

            renderPass = pRenderPass.get();

            return renderPass;
        }
    }

    public long getVkRenderPass(){
        return renderPass;
    }

    public void destroy(VkDevice lDevice){
        vkDestroyRenderPass(lDevice,renderPass,null);
    }

}
