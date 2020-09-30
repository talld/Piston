package Engine.Mesh;

import Engine.Geometry.Vertex;
import Engine.Memory.Buffer.Buffer;
import Engine.Memory.MemoryUtillities;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.util.vma.*;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.util.vma.Vma.*;
import static org.lwjgl.vulkan.VK10.*;

public class Mesh {

    private VkDevice lDevice;


    private Vertex[] vertices;
    private short[] indices;

    private long verticesSize;
    private long indicesSize;

    private Buffer stagingBuffer;
    private Buffer vertexBuffer;
    private Buffer indexBuffer;

    private VkQueue transferQueue;
    private long transferPool;

    private Integer ID;

    public Mesh(VkDevice lDevice, long transferPool, VkQueue transferQueue){
        this.lDevice = lDevice;
        this.transferPool = transferPool;
        this.transferQueue = transferQueue;
    }

    public void create(Vertex[] vertices, short[] indices){
        this.vertices = vertices;
        this.indices = indices;

        this.verticesSize = Vertex.getSize()*vertices.length;
        this.indicesSize = Short.BYTES * indices.length;

        try(MemoryStack stack = stackPush()) {

            //staging buffer to copy from
            stagingBuffer = MemoryUtillities.createBuffer(verticesSize, VK_BUFFER_USAGE_VERTEX_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_SRC_BIT, VMA_MEMORY_USAGE_CPU_ONLY);

            PointerBuffer data = stack.mallocPointer(1);

            vmaMapMemory(stagingBuffer.getAllocator(),stagingBuffer.getAllocation(),data);
            {
                MemoryUtillities.memCopy(data.getByteBuffer(0, (int) verticesSize), vertices);
            }
            vmaUnmapMemory(stagingBuffer.getAllocator(),stagingBuffer.getAllocation());

            //vertex buffer to copy to
            vertexBuffer  = MemoryUtillities.createBuffer(verticesSize, VK_BUFFER_USAGE_VERTEX_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_DST_BIT, VMA_MEMORY_USAGE_GPU_ONLY);
            MemoryUtillities.copyBuffer(transferQueue, transferPool, stagingBuffer, vertexBuffer, verticesSize);
            stagingBuffer.destroy();

            //staging buffer to copy from
            stagingBuffer = MemoryUtillities.createBuffer(indicesSize, VK_BUFFER_USAGE_INDEX_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_SRC_BIT, VMA_MEMORY_USAGE_CPU_ONLY);

            data = stack.mallocPointer(1);

            vmaMapMemory(stagingBuffer.getAllocator(),stagingBuffer.getAllocation(),data);
            {
                MemoryUtillities.memCopy(data.getByteBuffer(0, (int) indicesSize), indices);
            }
            vmaUnmapMemory(stagingBuffer.getAllocator(),stagingBuffer.getAllocation());

            //vertex buffer to copy to
            indexBuffer  = MemoryUtillities.createBuffer(verticesSize, VK_BUFFER_USAGE_INDEX_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_DST_BIT, VMA_MEMORY_USAGE_GPU_ONLY);
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