package Engine.Renderer.Commands;

import Engine.Renderer.FrameBuffer.FrameBuffers;
import Engine.Renderer.GraphicsPipeline.GraphicsPipeline;
import Engine.Renderer.RenderPass.RenderPass;
import Engine.Renderer.Swapchain.Swapchain;
import Engine.Renderer.Utilities.ErrorUtilities;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.util.ArrayList;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class CommandBuffers {

    private ArrayList<VkCommandBuffer> commandBuffers;
    int swapchainImageCount;

    public CommandBuffers(){

    }

    public ArrayList<VkCommandBuffer> create(VkDevice lDevice, Swapchain swapchain, long vkCommandPool){

        swapchainImageCount = swapchain.getSwapchainImagesViews().size();

        commandBuffers = new ArrayList<VkCommandBuffer>(swapchainImageCount);

        try(MemoryStack stack = stackPush()){

            VkCommandBufferAllocateInfo commandBufferAllocateInfo = VkCommandBufferAllocateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                    .commandPool(vkCommandPool)
                    .level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                    .commandBufferCount(swapchainImageCount);

            PointerBuffer pCommandBuffers = stack.mallocPointer(swapchainImageCount);

            int status = vkAllocateCommandBuffers(lDevice, commandBufferAllocateInfo,pCommandBuffers);

            if(status != VK_SUCCESS){
                throw new RuntimeException("Failed to create command buffer: " + ErrorUtilities.getError(status));
            }

            for(int i = 0; i < swapchainImageCount; i++){
                commandBuffers.add(new VkCommandBuffer(pCommandBuffers.get(i),lDevice));
            }
        }
        return commandBuffers;
    }

    public void record(Swapchain swapchain, RenderPass renderPass, GraphicsPipeline graphicsPipeline, FrameBuffers frameBuffers){

        try(MemoryStack stack = stackPush()){

            VkCommandBufferBeginInfo commandBufferBeginInfo = VkCommandBufferBeginInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);

            VkRect2D renderArea = VkRect2D.callocStack(stack);

            renderArea.offset(VkOffset2D.callocStack(stack).set(0, 0));
            renderArea.extent(swapchain.getSwapchainExtent());

            VkClearValue.Buffer clearValues = VkClearValue.callocStack(1, stack);
            clearValues.color().float32(stack.floats(0.0f, 0.0f, 0.0f, 1.0f));

            VkRenderPassBeginInfo renderPassBeginInfo = VkRenderPassBeginInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO)
                    .renderPass(renderPass.getVkRenderPass())
                    .renderArea(renderArea)
                    .pClearValues(clearValues);

            for(int i = 0; i < swapchainImageCount; i++){

                VkCommandBuffer commandBuffer = commandBuffers.get(i);

                int status = vkBeginCommandBuffer(commandBuffer, commandBufferBeginInfo);

                if(status != VK_SUCCESS){
                    throw new RuntimeException("Failed to begin command buffer recording");
                }

                renderPassBeginInfo.framebuffer(frameBuffers.getVkFrameBuffers().get(i));

                vkCmdBeginRenderPass(commandBuffer, renderPassBeginInfo, VK_SUBPASS_CONTENTS_INLINE);
                {
                    vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, graphicsPipeline.getVkGraphicsPipeline());

                    vkCmdDraw(commandBuffer, 3, 1, 0, 0);
                }
                vkCmdEndRenderPass(commandBuffer);

               status = vkEndCommandBuffer(commandBuffer);

                if(status != VK_SUCCESS) {
                    throw new RuntimeException("Failed to record command buffer");
                }

            }
        }

    }

    public ArrayList<VkCommandBuffer> getCommandBuffers() {
        return commandBuffers;
    }
}
