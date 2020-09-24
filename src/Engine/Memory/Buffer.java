package Engine.Memory;

import Engine.Renderer.PhysicalDevice.PhysicalDevice;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDevice;

import static org.lwjgl.vulkan.VK10.*;

public class Buffer {

    private long buffer;
    private VkDevice lDevice;
    private VkPhysicalDevice pDevice;

    private long size;
    private long memory;

    public Buffer(VkDevice lDevice, long size, long buffer){
        this.buffer = buffer;
        this.lDevice = lDevice;
        this.pDevice = lDevice.getPhysicalDevice();
        this.size = size;
    }

    public void bind(long memory){
        this.memory = memory;
        vkBindBufferMemory(lDevice,buffer,memory,0);
    }

    public long getPointer(){
        return buffer;
    }

    public VkDevice getLDevice() {
        return lDevice;
    }

    public VkPhysicalDevice getPDevice(){
        return pDevice;
    }

    public long getSize() {
        return size;
    }

    public long getMemory() {
        return memory;
    }

    public void destroy() throws Throwable {
        vkDestroyBuffer(lDevice, buffer, null);
        vkFreeMemory(lDevice, memory, null);
        this.finalize();
    }

}
