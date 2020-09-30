package Engine.Renderer.PhysicalDevice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class QueueFamilyIndices {

    private int graphicsFamilyIndex;
    private int presentationFamilyIndex;
    private int computeFamilyIndex;

    private ArrayList<Integer> transferQueues;

    public QueueFamilyIndices() {
        this.graphicsFamilyIndex = -1;
        this.presentationFamilyIndex = -1;
        this.computeFamilyIndex = -1;
        this.transferQueues = new ArrayList<Integer>();
    }

    public boolean validate(){
        return (graphicsFamilyIndex != -1 && presentationFamilyIndex != -1 && computeFamilyIndex != -1);
    }

    public Set<Integer> uniqueIndices(){
        Set unique = new HashSet<Integer>();
        unique.add(graphicsFamilyIndex);
        unique.add(presentationFamilyIndex);
        unique.add(computeFamilyIndex);

        return unique;
    }

    public Set<Integer> uniqueGraphicsIndices(){
        Set unique = new HashSet<Integer>();
        unique.add(graphicsFamilyIndex);
        unique.add(presentationFamilyIndex);

        return unique;
    }

    public void setGraphicsFamilyIndex(int graphicsFamilyIndex) {
        this.graphicsFamilyIndex = graphicsFamilyIndex;
    }

    public void setPresentationFamilyIndex(int presentationFamilyIndex) {
        this.presentationFamilyIndex = presentationFamilyIndex;
    }

    public void setComputeFamilyIndex(int computeFamilyIndex) {
        this.computeFamilyIndex = computeFamilyIndex;
    }

    public int getGraphicsFamilyIndex() {
        return graphicsFamilyIndex;
    }

    public int getPresentationFamilyIndex() {
        return presentationFamilyIndex;
    }

    public int getComputeFamilyIndex() {
        return computeFamilyIndex;
    }

    public void addTransferQueue(int q){
        transferQueues.add(q);
    }

    public ArrayList<Integer> getTransferQueues() {
        return transferQueues;
    }
}
