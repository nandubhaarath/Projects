package RouteManagement;

import DatabaseManagement.DatabaseService;
import FoodParcelManagement.FoodParcelRequest;
import UserManagement.Address;

import java.util.ArrayList;
import java.util.List;

public class RouteMapVisitAllNodes {

    private List<Address> allNodes;

    public RouteMapVisitAllNodes() {
        this.allNodes = new ArrayList<>();
    }

    public List<Address> GenerateRouteMap(Address startNode, Address endNode, List<FoodParcelRequest> requests, List<int[]> weights) {
        allNodes.clear();
        allNodes.add(startNode);

        for (int i = 0; i < requests.size(); i++)
        {
            FoodParcelRequest request = requests.get(i);
            allNodes.add(request.getRecipient().getAddress());
        }

        allNodes.add(endNode);

        // Adjacency matrix
        int size = allNodes.size();
        int[][] weightMatrix = new int[size][size];
        populateWeightMatrix(weights, weightMatrix);

        List<Address> route = new ArrayList<>();
        boolean[] visited = new boolean[size];

        int currentIndex = allNodes.indexOf(startNode);
        route.add(startNode);
        visited[currentIndex] = true;

        while (route.size() < allNodes.size() - 1) { // Exclude the end node from this loop
            int nextNodeIndex = -1;
            int minDistance = Integer.MAX_VALUE;

            for (int i = 0; i < size; i++) {
                if (!visited[i] && weightMatrix[currentIndex][i] < minDistance && allNodes.get(i) != endNode) {
                    minDistance = weightMatrix[currentIndex][i];
                    nextNodeIndex = i;
                }
            }

            if (nextNodeIndex == -1) {
                break;
            }

            route.add(allNodes.get(nextNodeIndex));
            visited[nextNodeIndex] = true;
            currentIndex = nextNodeIndex;
        }

        // Add the end node as the last node
        route.add(endNode);

        return route;
    }

    private void populateWeightMatrix(List<int[]> weights, int[][] weightMatrix) {
        for (int i = 0; i < weights.size(); i++)
        {
            int[] weight = weights.get(i);
            int fromIndex = findNodeIndexById(weight[0]);
            int toIndex = findNodeIndexById(weight[1]);
            int value = weight[2];

            if (fromIndex != -1 && toIndex != -1)
            {
                weightMatrix[fromIndex][toIndex] = value;
                weightMatrix[toIndex][fromIndex] = value; // Assuming undirected graph
            }
        }
    }

    private int findNodeIndexById(int addressId) {
        for (int i = 0; i < allNodes.size(); i++) {
            if (allNodes.get(i).getAddressId() == addressId) {
                return i;
            }
        }
        return -1;
    }

    public List<Address> GetAllNodes() {
        return new ArrayList<>(allNodes);
    }
}
