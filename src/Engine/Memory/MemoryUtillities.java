package Engine.Memory;

import Engine.Geometry.Vertex;
import Engine.Renderer.PhysicalDevice.PhysicalDevice;
import Engine.Renderer.Utilities.ErrorUtilities;
import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryStack.stackGet;
import static org.lwjgl.vulkan.VK10.*;

public class MemoryUtillities {

        public static Buffer createBuffer(VkDevice lDevice, long size, int usage){

            VkBufferCreateInfo bufferCreateInfo = VkBufferCreateInfo.callocStack(stackGet())
                    .sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
                    .size(size)
                    .usage(usage)
                    .sharingMode(VK_SHARING_MODE_EXCLUSIVE);

            LongBuffer pBuffer = stackGet().mallocLong(1);
            int status = vkCreateBuffer(lDevice, bufferCreateInfo, null, pBuffer);
            if(status!=VK_SUCCESS){
                throw new RuntimeException("Failed to create buffer: " + ErrorUtilities.getError(status));
            }

            return new Buffer(lDevice, size, pBuffer.get(0));
        }

        public static void allocateMemory(Buffer buffer, int MemoryFlags){

            VkMemoryRequirements memoryRequirements = VkMemoryRequirements.callocStack(stackGet());

            vkGetBufferMemoryRequirements(buffer.getLDevice(),buffer.getPointer(),memoryRequirements);

            VkMemoryAllocateInfo allocateInfo = VkMemoryAllocateInfo.callocStack(stackGet())
                    .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                    .allocationSize(memoryRequirements.size())
                    .memoryTypeIndex(findMemoryType(buffer.getPDevice(), memoryRequirements.memoryTypeBits(),MemoryFlags));

            LongBuffer pMemory = stackGet().mallocLong(1);

            int status = vkAllocateMemory(buffer.getLDevice(),allocateInfo,null, pMemory);

            if(status!=VK_SUCCESS){
                throw new RuntimeException("Failed to allocate memory to buffer: " + buffer.getPointer() + " " + ErrorUtilities.getError(status));
            }

            long memory = pMemory.get(0);

            buffer.bind(memory);
        }

    private static int findMemoryType(VkPhysicalDevice pDevice, int typeFilter, int properties) {

        VkPhysicalDeviceMemoryProperties memProperties = VkPhysicalDeviceMemoryProperties.mallocStack();
        vkGetPhysicalDeviceMemoryProperties(pDevice, memProperties);

        for (int i = 0; i < memProperties.memoryTypeCount(); i++) {
            if ((typeFilter & (1 << i)) != 0 && (memProperties.memoryTypes(i).propertyFlags() & properties) == properties) {
                return i;
            }
        }
        throw new RuntimeException("Failed to find suitable memory type: " + typeFilter + " " + properties);
    }

    public static void memcpyVertex(ByteBuffer buffer, Vertex[] vertices) {
        for(Vertex vertex : vertices) {
            buffer.putFloat(vertex.getPos().x());
            buffer.putFloat(vertex.getPos().y());
            buffer.putFloat(vertex.getPos().z());

            buffer.putFloat(vertex.getColour().x());
            buffer.putFloat(vertex.getColour().y());
            buffer.putFloat(vertex.getColour().z());
        }
    }

}
