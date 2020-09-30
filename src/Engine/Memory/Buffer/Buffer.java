package Engine.Memory.Buffer;

import Engine.Memory.MemoryUtillities;

import static org.lwjgl.util.vma.Vma.vmaDestroyBuffer;

public class Buffer {

    private long allocator;
    private long buffer;
    private long allocation;
    private long size;

    public Buffer(long allocator, long buffer, long allocation, long size){
        this.buffer = buffer;
        this.allocation = allocation;
        this.size = size;
        this.allocator = allocator;
    }

    public long getAllocator() {
        return allocator;
    }

    public long getBuffer() {
        return buffer;
    }

    public long getAllocation() {
        return allocation;
    }

    public long getSize() {
        return size;
    }

    public void destroy(){
        vmaDestroyBuffer(allocator,buffer,allocation);
    }
}
