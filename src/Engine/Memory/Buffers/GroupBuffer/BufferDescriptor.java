package Engine.Memory.Buffers.GroupBuffer;

public class BufferDescriptor {

    private long offset;
    private long size;

    public BufferDescriptor(long size){
        this.size = size;
        this.offset = 0l;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

}
