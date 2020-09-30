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
                    .flags(VK_ATTACHMENT_DESCRIPTION_MAY_ALIAS_BIT)
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

            //create transfer windows between dependent points in the renderpass (in(VK_IMAGE_LAYOUT_UNDEFINED) - subpass1(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL) - out(VK_IMAGE_LAYOUT_PRESENT_SRC_KHR))

            VkSubpassDependency.Buffer subpassDependencys = VkSubpassDependency.callocStack(2, stack);

            VkSubpassDependency windowDependency = subpassDependencys.get(0)
                    .srcStageMask(VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT) // start of window (stage)
                    .srcAccessMask(VK_ACCESS_MEMORY_READ_BIT) // start of window (point in stage)
                    .srcSubpass(VK_SUBPASS_EXTERNAL) //in
                    .dstSubpass(0)//subpass1
                    .dstStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT) //end of window
                    .dstAccessMask(VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT) // end of window (point in stage)
                    .dependencyFlags(0);

            VkSubpassDependency pipelineDependency = subpassDependencys.get(1)
                    .srcStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT)
                    .srcAccessMask(VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT)
                    .srcSubpass(0) //subpass1
                    .dstSubpass(VK_SUBPASS_EXTERNAL) //out
                    .dstStageMask(VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT)
                    .dstAccessMask(VK_ACCESS_MEMORY_READ_BIT)
                    .dependencyFlags(0);


            VkRenderPassCreateInfo renderPassCreateInfo = VkRenderPassCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO)
                    .pAttachments(colourAttachmentDescriptions)
                    .pSubpasses(subpassDescriptions)
                    .pDependencies(subpassDependencys);

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
