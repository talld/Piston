package Engine.Renderer.RenderUpdater;

import Engine.Renderer.Commands.CommandBuffers;
import Engine.Renderer.LogicalDevice.LogicalDevice;
import Engine.Renderer.Swapchain.Swapchain;
import Engine.Renderer.Sync.Sync;
import Engine.Renderer.Utilities.ErrorUtilities;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPresentInfoKHR;
import org.lwjgl.vulkan.VkSubmitInfo;

import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.system.MemoryStack.stackGet;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

public class RenderUpdater {


    private LogicalDevice logicalDevice;
    private VkDevice lDevice;

    private Swapchain swapchain;
    private long vkSwapchain;

    private CommandBuffers commandBuffers;
    private  ArrayList<VkCommandBuffer> vkCommandBuffers;

    private Sync sync;

    private int currentFrame = 0;
    private int MAX_FRAMES_IN_FLIGHT = 3;

    public RenderUpdater(){

    }

    public void create(LogicalDevice logicalDevice, Swapchain swapchain, CommandBuffers commandBuffers, Sync sync){
        this.logicalDevice = logicalDevice;
        lDevice = logicalDevice.get();

        this.swapchain = swapchain;
        vkSwapchain = swapchain.getVkSwapchain();

        this.commandBuffers = commandBuffers;
        vkCommandBuffers = commandBuffers.getCommandBuffers();

        this.sync = sync;
    }

    public void update(){
        try(MemoryStack stack = stackPush()){

            vkWaitForFences(lDevice, sync.getInFlightFence(currentFrame), true, Integer.MAX_VALUE);

            IntBuffer pImageIndex = stack.mallocInt(1);

            vkAcquireNextImageKHR(lDevice, vkSwapchain, Integer.MAX_VALUE, sync.getImageAvailableSemaphore(currentFrame), VK_NULL_HANDLE, pImageIndex);

            int imageIndex = pImageIndex.get(0);

            if(sync.getInFlightFence(imageIndex) != VK_NULL_HANDLE){
                vkWaitForFences(lDevice, sync.getInFlightFence(imageIndex), true, Integer.MAX_VALUE);
            }

            sync.setImagesInFlight(imageIndex,sync.getInFlightFence(currentFrame));

            VkSubmitInfo.Buffer submitInfo = VkSubmitInfo.callocStack(1, stackGet())
                    .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
                    .pWaitSemaphores(stack.longs(sync.getImageAvailableSemaphore(currentFrame)))
                    .waitSemaphoreCount(1)
                    .pSignalSemaphores(stack.longs(sync.getRenderFinishedSemaphore(currentFrame)))
                    .pWaitDstStageMask(stack.ints(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT))
                    .pCommandBuffers(stack.pointers(vkCommandBuffers.get(imageIndex)));

            vkResetFences(lDevice,sync.getInFlightFence(currentFrame));

            int status = vkQueueSubmit(logicalDevice.getGraphicsQueue(), submitInfo, sync.getInFlightFence(currentFrame));

            if (status != VK_SUCCESS) {
                throw new RuntimeException("Failed to submit graphics queue: " + ErrorUtilities.getError(status));
            }

            VkPresentInfoKHR presentInfo = VkPresentInfoKHR.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR)
                    .pWaitSemaphores(stack.longs(sync.getRenderFinishedSemaphore(currentFrame)))
                    .pSwapchains(stackGet().longs(vkSwapchain))
                    .swapchainCount(1)
                    .pImageIndices(stackGet().ints(imageIndex));

            status = vkQueuePresentKHR(logicalDevice.getPresentQueue(), presentInfo);

            if (status != VK_SUCCESS) {
                throw new RuntimeException("Failed to submit present queue: " + ErrorUtilities.getError(status));
            }
        }
        currentFrame = (currentFrame + 1) % MAX_FRAMES_IN_FLIGHT;
    }

    public void destroy(){
        vkDeviceWaitIdle(lDevice);
    }

    public int getMaxFrames() {
        return MAX_FRAMES_IN_FLIGHT;
    }
}
