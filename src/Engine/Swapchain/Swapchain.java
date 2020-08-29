package Engine.Swapchain;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR;

public class Swapchain {

    private long swapchain;

    public Swapchain(){

    }

    public long create(VkPhysicalDevice device, long surface) {
        try (MemoryStack stack = stackPush()) {
            SwapchainSupportDetails supportDetails = new SwapchainSupportDetails(device,surface);
            
        }
        return 0l;
    }


    public void destroy(){

    }
}
