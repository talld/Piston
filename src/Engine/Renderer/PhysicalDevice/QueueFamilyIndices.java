package Engine.Renderer.PhysicalDevice;

import java.util.HashSet;
import java.util.Set;

public class QueueFamilyIndices {

    public int graphicsFamilyIndex;
    public int presentationFamilyIndex;
    public int computeFamilyIndex;

    public QueueFamilyIndices() {
        this.graphicsFamilyIndex = -1;
        this.presentationFamilyIndex = -1;
        this.computeFamilyIndex = -1;
    }

    public boolean validate(){
        return (graphicsFamilyIndex != -1 && presentationFamilyIndex != -1 && computeFamilyIndex != -1);
    }

    public Set uniqueIndices(){
        Set unique = new HashSet<Integer>();
        unique.add(graphicsFamilyIndex);
        unique.add(presentationFamilyIndex);
        unique.add(computeFamilyIndex);

        return unique;
    }
}
