package RouteManagement;

import UserManagement.Address;
import DatabaseManagement.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class Dijkstra {
    private ArrayList<String> nodeNames;
    private int[][] adjacencyMatrix;

    // Path and cost arrays
    private int[] costArray;
    private String[] pathArray;
    private boolean[] visited;

    public Dijkstra(ArrayList<String> nodeNames) {
        this.nodeNames = nodeNames;
        adjacencyMatrix = new int[nodeNames.size()][nodeNames.size()];
        costArray = new int[nodeNames.size()];
        pathArray = new String[nodeNames.size()];
        visited = new boolean[nodeNames.size()];
    }

    public void SetWeightBetweenNodes(String startNode, String destinationNode, int weight) {
        int startNodeIndex = nodeNames.indexOf(startNode);
        int destinationNodeIndex = nodeNames.indexOf(destinationNode);
        adjacencyMatrix[startNodeIndex][destinationNodeIndex] = weight;
        adjacencyMatrix[destinationNodeIndex][startNodeIndex] = weight;
    }

    public void LoadWeightsFromDatabase(DatabaseService databaseService) {
        List<int[]> weightsFromDb = databaseService.GetWeights();
        List<Address> allAddresses = databaseService.GetAllAddresses();

        for (int[] weight : weightsFromDb) {
            int fromId = weight[0];
            int toId = weight[1];
            int value = weight[2];

            Address fromAddress = findAddressById(allAddresses, fromId);
            Address toAddress = findAddressById(allAddresses, toId);

            if (fromAddress != null && toAddress != null) {
                this.SetWeightBetweenNodes(fromAddress.getFullAddress(), toAddress.getFullAddress(), value);
            }
        }
    }

    private Address findAddressById(List<Address> addresses, int id) {
        for (Address address : addresses) {
            if (address.getAddressId() == id) {
                return address;
            }
        }
        return null;
    }

    public String GetShortestPath(String fromNode, String toNode) {
        PriorityQueue priorityQueue = new PriorityQueue();

        for (int i = 0; i < nodeNames.size(); i++) {
            costArray[i] = Integer.MAX_VALUE; // Initialize cost array to an infinite value
            pathArray[i] = null;
            visited[i] = false;
        }

        // Begin with distance at startNode as 0
        int fromIndex = nodeNames.indexOf(fromNode);
        costArray[fromIndex] = 0;

        // Add startNode to priority queue
        priorityQueue.Add(fromNode, fromNode, 0);

        GeneratePathCostArrayData(priorityQueue);

        // Return shortest path as a comma-separated string
        return getPath(fromNode, toNode);
    }

    private void GeneratePathCostArrayData(PriorityQueue priorityQueue) {
        while (!priorityQueue.isEmpty()) {
            Node currentNode = priorityQueue.DeQueue();
            int currentIndex = nodeNames.indexOf(currentNode.getEndNode());

            // Go through unvisited nodes
            if (!visited[currentIndex]) {
                visited[currentIndex] = true;

                // Update costs and paths for each neighbor
                for (int i = 0; i < nodeNames.size(); i++) {
                    if (adjacencyMatrix[currentIndex][i] > 0) {
                        int newDistance = costArray[currentIndex] + adjacencyMatrix[currentIndex][i];
                        if (newDistance < costArray[i]) {
                            costArray[i] = newDistance;
                            pathArray[i] = currentNode.getEndNode();
                            priorityQueue.Add(currentNode.getEndNode(), nodeNames.get(i), newDistance);
                        }
                    }
                }
            }
        }
    }

    private String getPath(String fromNode, String toNode) {
        boolean hasPathBuilt = false;
        StringBuilder pathBuilder = new StringBuilder();
        String vertexNode = toNode;

        while (!hasPathBuilt) {
            for (int i = 0; i < nodeNames.size(); i++) {
                if (nodeNames.get(i).equals(vertexNode)) {
                    pathBuilder.insert(0, vertexNode);

                    if (vertexNode.equals(fromNode)) {
                        hasPathBuilt = true;
                    } else {
                        pathBuilder.insert(0, " --> ");
                        vertexNode = pathArray[i];
                    }
                    break;
                }
            }
        }

        return pathBuilder.toString();
    }

    public int GetShortestPathLength(String fromNode, String toNode) {
        GetShortestPath(fromNode, toNode); // To ensure path is calculated
        return costArray[nodeNames.indexOf(toNode)];
    }
}
