package Engine.Memory.Buffers.GroupBuffer;

import Engine.Memory.Buffers.SingleBuffer.Buffer;
import Engine.Memory.MemoryUtillities;
import Engine.Renderer.Commands.CommandPool;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkQueue;

import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.vulkan.VK10.*;

public class GroupBuffer {

    private VkDevice lDevice;
    private VkPhysicalDevice pDevice;


    private int memoryProperties;
    private long buffer;
    private long stagingBuffer;

    private long priorSize;
    private long size;
    private long currentOffset;
    private long stagingMemory;
    private long memory;

    private HashMap<Integer,HashMap<String,BufferDescriptor>> bufferDescriptors; //using an integer as an ID so buffer descriptors can easily be found again


    public GroupBuffer(VkPhysicalDevice pDevice, VkDevice lDevice, int memoryProperties){
        this.pDevice = pDevice;
        this.lDevice = lDevice;

        this.memoryProperties = memoryProperties;

        this.bufferDescriptors = new HashMap<Integer, HashMap<String, BufferDescriptor>>();
        this.priorSize = 0l;
        this.size = 0l;
        this.currentOffset = 0l;
        this.memory = 0l;
    }

    public void addBufferDescriptor(Integer ID,String name,BufferDescriptor bufferDescriptor){
        if(bufferDescriptors.get(ID) == null) {

            HashMap<String, BufferDescriptor> descriptor = new HashMap<String, BufferDescriptor>();
            descriptor.put(name, bufferDescriptor);
            bufferDescriptors.put(ID, descriptor);
            size+=bufferDescriptor.getSize();
            bufferDescriptor.setOffset(currentOffset);
            currentOffset+= size-priorSize;
            priorSize = size;
        }else{
            HashMap<String, BufferDescriptor> descriptor = bufferDescriptors.get(ID);
            descriptor.put(name, bufferDescriptor);
            bufferDescriptors.put(ID,descriptor);
            size+=bufferDescriptor.getSize();
            bufferDescriptor.setOffset(currentOffset);
            currentOffset+=size-priorSize;
            priorSize = size;
        }
    }

    public BufferDescriptor getBufferDescriptor(Integer ID, String name){
        return bufferDescriptors.get(ID).get(name);
    }

    public void set(int usage, int dstMemoryProperties){
        this.stagingBuffer = MemoryUtillities.createGroupBuffer(lDevice, size, usage | VK_BUFFER_USAGE_TRANSFER_SRC_BIT);
        this.buffer = MemoryUtillities.createGroupBuffer(lDevice, size, usage | VK_BUFFER_USAGE_TRANSFER_DST_BIT);
        this.stagingMemory = MemoryUtillities.allocateBuffer(lDevice, stagingBuffer, memoryProperties);
        this.memory = MemoryUtillities.allocateBuffer(lDevice, buffer, dstMemoryProperties);
        vkBindBufferMemory(lDevice,stagingBuffer,stagingMemory,0);
        vkBindBufferMemory(lDevice,buffer,memory,0);
    }

    public void copy(long transferPool, VkQueue transferQueue) {
        MemoryUtillities.copyBuffer(lDevice, transferQueue, transferPool, stagingBuffer, buffer, size);
    }

    public long getStagingMemory() {
        return stagingMemory;
    }

    public long getMemory() {
        return memory;
    }

    public long getSize() {
        return size;
    }

    public long getStagingBuffer() {
        return stagingBuffer;
    }

    public long getBuffer() {
        return buffer;
    }

    public void destroy(){
        vkFreeMemory(lDevice,memory,null);
        vkFreeMemory(lDevice,stagingMemory,null);
    }

}
