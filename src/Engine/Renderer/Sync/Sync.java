package Engine.Renderer.Sync;

import Engine.Renderer.Swapchain.Swapchain;
import Engine.Renderer.Utilities.ErrorUtilities;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkFenceCreateInfo;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;

import java.nio.LongBuffer;
import java.util.ArrayList;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class Sync {

    ArrayList<Long> imageAvailableSemaphores;
    ArrayList<Long> renderFinishedSemaphores;
    ArrayList<Long> inFlightFences;
    ArrayList<Long> imagesInFlight;

    public Sync(){

    }

    public void create(VkDevice lDevice, Swapchain swapchain, int maxFrames){

        imageAvailableSemaphores = new ArrayList<Long>(maxFrames);
        renderFinishedSemaphores = new ArrayList<Long>(maxFrames);
        inFlightFences = new ArrayList<Long>(maxFrames);
        imagesInFlight = new ArrayList<Long>(swapchain.getSwapchainImagesViews().size());

        for(int i = 0; i<swapchain.getSwapchainImagesViews().size(); i++){
            imagesInFlight.add(0l);
        }


        try(MemoryStack stack = stackPush()) {
            VkSemaphoreCreateInfo semaphoreCreateInfo = VkSemaphoreCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO);

            VkFenceCreateInfo fenceCreateInfo = VkFenceCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_FENCE_CREATE_INFO)
                    .flags(VK_FENCE_CREATE_SIGNALED_BIT);

            for(int i = 0; i<maxFrames; i++){

                LongBuffer syncPointer = stack.longs(VK_NULL_HANDLE);

                int status = vkCreateSemaphore(lDevice, semaphoreCreateInfo, null, syncPointer);
                if(status != VK_SUCCESS){
                    throw new RuntimeException("Failed to create imageAvailableSemaphores " + i + ": "  + ErrorUtilities.getError(status));
                }

                imageAvailableSemaphores.add(syncPointer.get(0));

                status = vkCreateSemaphore(lDevice, semaphoreCreateInfo, null, syncPointer);
                if(status != VK_SUCCESS){
                    throw new RuntimeException("Failed to create renderFinishedSemaphores"  + i + ": " + ErrorUtilities.getError(status));
                }
                renderFinishedSemaphores.add((syncPointer.get(0)));

                status = vkCreateFence(lDevice,fenceCreateInfo,null,syncPointer);
                if(status != VK_SUCCESS){
                    throw new RuntimeException("Failed to create inFlightFence"  + i + ": " + ErrorUtilities.getError(status));
                }
                inFlightFences.add((syncPointer.get(0)));
            }
        }
    }

    public long getImageAvailableSemaphore(int i) {
        return imageAvailableSemaphores.get(i);
    }

    public long getRenderFinishedSemaphore(int i) {
        return renderFinishedSemaphores.get(i);
    }

    public long getInFlightFence(int i) {
        return inFlightFences.get(i);
    }

    public long getImagesInFlight(int i){
        return imagesInFlight.get(i);
    }

    public void setImagesInFlight(int i, long fence){

            imagesInFlight.set(i, fence);

    }

    public void destroy(VkDevice lDevice){

        for(long fence: inFlightFences){
            vkDestroyFence(lDevice,fence,null);
        }

        for(long semaphore : imageAvailableSemaphores){
            vkDestroySemaphore(lDevice,semaphore,null);
        }

        for(long semaphore : renderFinishedSemaphores){
            vkDestroySemaphore(lDevice,semaphore,null);
        }

    }
}
