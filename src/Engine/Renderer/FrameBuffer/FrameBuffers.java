package Engine.Renderer.FrameBuffer;

import Engine.Renderer.RenderPass.RenderPass;
import Engine.Renderer.Swapchain.Swapchain;
import Engine.Renderer.Utilities.ErrorUtilities;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;
import java.util.ArrayList;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;


public class FrameBuffers {

    private ArrayList<Long> vkFrameBuffers;

    public FrameBuffers() {

    }

    public ArrayList<Long> create(VkDevice lDevice, Swapchain swapchain, RenderPass renderPass) {

        try (MemoryStack stack = stackPush()) {

            VkExtent2D swapchainExtent = swapchain.getSwapchainExtent();

            vkFrameBuffers = new ArrayList<Long>(swapchain.getSwapchainImagesViews().size());
            VkFramebufferCreateInfo framebufferCreateInfo = VkFramebufferCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
                    .renderPass(renderPass.getVkRenderPass())
                    .width(swapchainExtent.width())
                    .height(swapchainExtent.height())
                    .layers(1);

            LongBuffer pFrameBuffer = stack.mallocLong(1);
            LongBuffer pAttachment = stack.mallocLong(1);

            for (long imageView : swapchain.getSwapchainImagesViews()) {

                pAttachment.put(0, imageView);

                framebufferCreateInfo.pAttachments(pAttachment);

                int status = vkCreateFramebuffer(lDevice, framebufferCreateInfo, null, pFrameBuffer);
                if (status != VK_SUCCESS) {
                    throw new RuntimeException("Failed to create Frame buffer" + ErrorUtilities.getError(status));
                }

                vkFrameBuffers.add(pFrameBuffer.get(0));
            }
            return vkFrameBuffers;
        }
    }

    public ArrayList<Long> getVkFrameBuffers() {
        return vkFrameBuffers;
    }

    public void destroy(VkDevice lDevice){
        for(long frameBuffer : vkFrameBuffers){
            vkDestroyFramebuffer(lDevice, frameBuffer, null);
        }
    }
}
