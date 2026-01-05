package RouteManagement;

import java.util.ArrayList;

public class PriorityQueue {
    private ArrayList<Node> nodes;

    public PriorityQueue() {
        nodes = new ArrayList<>();
    }

    public void Add(String startNode, String endNode, int weight) {
        Node node = new Node(startNode, endNode, weight);
        boolean isInserted = false;

        for (int i = 0; i < nodes.size(); i++) {
            if (weight < nodes.get(i).getWeight()) {
                nodes.add(i, node);
                isInserted = true;
                break;
            }
        }
        if (!isInserted) {
            nodes.add(node);
        }
    }

    public Node DeQueue() {
        return nodes.remove(0);
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }
}
