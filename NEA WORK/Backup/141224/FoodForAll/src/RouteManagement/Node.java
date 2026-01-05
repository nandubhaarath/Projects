package RouteManagement;

public class Node {
    private String startNode;
    private String endNode;
    private int weight;

    public Node(String startNode, String endNode, int weight) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.weight = weight;
    }

    public String getStartNode() {
        return startNode;
    }

    public String getEndNode() {
        return endNode;
    }

    public int getWeight() {
        return weight;
    }
}
