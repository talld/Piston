package Engine.Renderer.Sync;

import Engine.Renderer.Utilities.ErrorUtilities;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class Semaphores {

    public long imageAvailableSemaphore;
    public long renderFinishedSemaphore;

    public Semaphores(){

    }

    public void create(VkDevice lDevice){

        try(MemoryStack stack = stackPush()){

            VkSemaphoreCreateInfo semaphoreCreateInfo = VkSemaphoreCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO);

            LongBuffer pimageAvailableSemaphore = stack.longs(VK_NULL_HANDLE);

            int status = vkCreateSemaphore(lDevice, semaphoreCreateInfo, null, pimageAvailableSemaphore);

            if(status != VK_SUCCESS){
                System.out.println("Failed to create Image Available semaphore: " + ErrorUtilities.getError(status));
            }

            imageAvailableSemaphore = pimageAvailableSemaphore.get(0);

            LongBuffer pRenderFinshedSemaphore = stack.longs(VK_NULL_HANDLE);

            status = vkCreateSemaphore(lDevice, semaphoreCreateInfo, null, pRenderFinshedSemaphore);

            if(status != VK_SUCCESS){
                System.out.println("Failed to create render finished semaphore: " + ErrorUtilities.getError(status));
            }

            renderFinishedSemaphore = pRenderFinshedSemaphore.get(0);

        }

    }

    public void destroy(VkDevice lDevice){
        vkDestroySemaphore(lDevice, imageAvailableSemaphore, null);
        vkDestroySemaphore(lDevice, renderFinishedSemaphore, null);
    }

}
