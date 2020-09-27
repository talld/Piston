package Engine.Mesh;

import Engine.Geometry.Vertex;
import Engine.Memory.Buffer;
import Engine.Memory.MemoryUtillities;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkQueue;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class Mesh {

    private VkDevice lDevice;


    private Vertex[] vertices;
    private short[] indices;


    private long verticesSize;
    private Buffer vertexBuffer;

    private int indicesSize;
    private Buffer indexBuffer;

    private VkQueue transferQueue;
    private long transferPool;


    public Mesh(VkDevice lDevice, long transferPool, VkQueue transferQueue){
        this.lDevice = lDevice;
        this.transferPool = transferPool;
        this.transferQueue = transferQueue;

    }

    public void create(Vertex[] vertices, short[] indices){
        this.vertices = vertices;
        this.indices = indices;
        this.verticesSize = vertices.length * (6*Float.BYTES);
        this.indicesSize = indices.length * Short.BYTES;
        this.create();
    }

    public void create(){
        try(MemoryStack stack = stackPush()) {

            Buffer stagingBuffer = MemoryUtillities.createBuffer(lDevice, (int) verticesSize,VK_BUFFER_USAGE_VERTEX_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_SRC_BIT);
            vertexBuffer = MemoryUtillities.createBuffer(lDevice, (int) verticesSize,VK_BUFFER_USAGE_VERTEX_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_DST_BIT);

            MemoryUtillities.allocateBuffer(stagingBuffer, VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
            MemoryUtillities.allocateBuffer(vertexBuffer, VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);

            stagingBuffer.bind();
            vertexBuffer.bind();

            PointerBuffer data = stack.mallocPointer(1);

            vkMapMemory(lDevice, stagingBuffer.getMemory(), 0, stagingBuffer.getSize(), 0, data);
            {
                MemoryUtillities.memCopy(data.getByteBuffer(0, (int) stagingBuffer.getSize()), vertices);
            }
            vkUnmapMemory(lDevice, stagingBuffer.getMemory());

            MemoryUtillities.copyBuffer(transferQueue, transferPool, stagingBuffer, vertexBuffer, verticesSize);

            stagingBuffer.destroy();

            stagingBuffer = MemoryUtillities.createBuffer(lDevice, (int) indicesSize,VK_BUFFER_USAGE_INDEX_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_SRC_BIT);
            MemoryUtillities.allocateBuffer(stagingBuffer, VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);

            indexBuffer = MemoryUtillities.createBuffer(lDevice, (int) indicesSize,VK_BUFFER_USAGE_INDEX_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_DST_BIT);
            MemoryUtillities.allocateBuffer(indexBuffer, VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);

            stagingBuffer.bind();
            indexBuffer.bind();


            data = stack.mallocPointer(1);

            vkMapMemory(lDevice, stagingBuffer.getMemory(), 0, stagingBuffer.getSize(), 0, data);
            {
                MemoryUtillities.memCopy(   data.getByteBuffer(0, (int) stagingBuffer.getSize()), indices);
            }
            vkUnmapMemory(lDevice, stagingBuffer.getMemory());


            MemoryUtillities.copyBuffer(transferQueue, transferPool, stagingBuffer, indexBuffer, indicesSize);


            stagingBuffer.destroy();
        }
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public Buffer getVertexBuffer() {
        return vertexBuffer;
    }

    public short[] getIndices(){
        return indices;
    }

    public Buffer getIndexBuffer(){
        return indexBuffer;
    }

    public void destroy(){
        vertexBuffer.destroy();
        indexBuffer.destroy();
    }
}