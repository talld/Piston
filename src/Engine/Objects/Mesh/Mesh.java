package Engine.Objects.Mesh;

import Engine.Geometry.Vertex;
import Engine.Memory.Buffer.Buffer;
import Engine.Memory.MemoryUtillities;
import Engine.Objects.Camera.Camera;
import Engine.Renderer.Swapchain.Swapchain;
import Engine.Renderer.Utilities.ErrorUtilities;
import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.util.vma.Vma.*;
import static org.lwjgl.vulkan.VK10.*;

public class Mesh {

    private VkDevice lDevice;


    private Vertex[] vertices;
    private short[] indices;

    private long verticesSize;
    private long indicesSize;
    private int uboSize;

    private Buffer stagingBuffer;
    private Buffer vertexBuffer;
    private Buffer indexBuffer;

    private VkQueue transferQueue;
    private long transferPool;


    public Mesh(VkDevice lDevice, long transferPool, VkQueue transferQueue, Swapchain swapchain){
        this.lDevice = lDevice;
        this.transferPool = transferPool;
        this.transferQueue = transferQueue;
    }

    public void create(Vertex[] vertices, short[] indices, long descriptorSetLayout, long descriptorPool){
        this.vertices = vertices;
        this.indices = indices;
        this.verticesSize = Vertex.getSize()*vertices.length;
        this.indicesSize = Short.BYTES * indices.length;

        try(MemoryStack stack = stackPush()) {

            //staging buffer to copy from
            stagingBuffer = MemoryUtillities.createBuffer(verticesSize, VK_BUFFER_USAGE_VERTEX_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_SRC_BIT, VMA_MEMORY_USAGE_CPU_ONLY);

            PointerBuffer data = stack.mallocPointer(1);

            vmaMapMemory(stagingBuffer.getAllocator(), stagingBuffer.getAllocation(), data);
            {
                MemoryUtillities.memCopy(data.getByteBuffer(0, (int) verticesSize), vertices);
            }
            vmaUnmapMemory(stagingBuffer.getAllocator(), stagingBuffer.getAllocation());

            //vertex buffer to copy to
            vertexBuffer = MemoryUtillities.createBuffer(verticesSize, VK_BUFFER_USAGE_VERTEX_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_DST_BIT, VMA_MEMORY_USAGE_GPU_ONLY);
            MemoryUtillities.copyBuffer(transferQueue, transferPool, stagingBuffer, vertexBuffer, verticesSize);
            stagingBuffer.destroy();

            //staging buffer to copy from
            stagingBuffer = MemoryUtillities.createBuffer(indicesSize, VK_BUFFER_USAGE_INDEX_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_SRC_BIT, VMA_MEMORY_USAGE_CPU_ONLY);

            data = stack.mallocPointer(1);

            vmaMapMemory(stagingBuffer.getAllocator(), stagingBuffer.getAllocation(), data);
            {
                MemoryUtillities.memCopy(data.getByteBuffer(0, (int) indicesSize), indices);
            }
            vmaUnmapMemory(stagingBuffer.getAllocator(), stagingBuffer.getAllocation());

            //vertex buffer to copy to
            indexBuffer = MemoryUtillities.createBuffer(verticesSize, VK_BUFFER_USAGE_INDEX_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_DST_BIT, VMA_MEMORY_USAGE_GPU_ONLY);
            MemoryUtillities.copyBuffer(transferQueue, transferPool, stagingBuffer, indexBuffer, indicesSize);
            stagingBuffer.destroy();
       }
    }


    public Vertex[] getVertices() {
        return vertices;
    }

    public short[] getIndices(){
        return indices;
    }

    public Buffer getVertexBuffer() {
        return vertexBuffer;
    }

    public Buffer getIndexBuffer() {
        return indexBuffer;
    }

    public void destroy(){

        vertexBuffer.destroy();
        indexBuffer.destroy();
    }
}