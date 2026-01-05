package RouteManagement;

import FoodParcelManagement.FoodParcelRequest;
import UserManagement.Address;

import java.util.*;

public class RouteMapDijkstra implements IRouteMapDijkstra {
    private final List<Address> allNodes;

    public RouteMapDijkstra() {
        this.allNodes = new ArrayList<>();
    }

    @Override
    public int[][] AutoGenerateWeights(Address startNode, Address endNode, List<FoodParcelRequest> requests) {
        allNodes.clear();
        allNodes.add(startNode);

        for (int i = 0; i < requests.size(); i++)
        {
            FoodParcelRequest request = requests.get(i);
            allNodes.add(request.getRecipient().getAddress());
        }

        allNodes.add(endNode);

        int size = allNodes.size();
        int[][] weightMatrix = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i != j) {
                    weightMatrix[i][j] = (int) (Math.random() * 20) + 1; // auto-generate weights
                } else {
                    weightMatrix[i][j] = 0;
                }
            }
        }

        return weightMatrix;
    }

    @Override
    public List<Address> GenerateRouteMap(Address startNode, Address endNode, List<FoodParcelRequest> requests, int[][] weightMatrix) {
        List<Address> route = new ArrayList<>();
        PriorityQueue pq = new PriorityQueue();
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previousNodes = new HashMap<>();
        List<String> visited = new ArrayList<>();

        // Initialize distances
        for (int i = 0; i < allNodes.size(); i++)
        {
            Address node = allNodes.get(i);
            distances.put(node.getPostalCode(), Integer.MAX_VALUE);
        }
        distances.put(startNode.getPostalCode(), 0);

        pq.Add(null, startNode.getPostalCode(), 0);

        while (!pq.isEmpty()) {
            Node current = pq.DeQueue();
            String currentPostCode = current.getEndNode();

            if (!visited.contains(currentPostCode)) {
                visited.add(currentPostCode);
                int currentIndex = getNodeIndex(currentPostCode);

                for (int i = 0; i < weightMatrix[currentIndex].length; i++) {
                    String neighborPostCode = allNodes.get(i).getPostalCode();
                    if (!visited.contains(neighborPostCode)) {
                        int newDist = distances.get(currentPostCode) + weightMatrix[currentIndex][i];
                        if (newDist < distances.get(neighborPostCode)) {
                            distances.put(neighborPostCode, newDist);
                            previousNodes.put(neighborPostCode, currentPostCode);
                            pq.Add(currentPostCode, neighborPostCode, newDist);
                        }
                    }
                }
            }
        }

        // Build route in reverse order, starting from the end node
        String step = endNode.getPostalCode();
        while (step != null) {
            Address node = getNodeByPostCode(step);
            if (!route.contains(node)) {
                route.add(0, node); // Build route backwards
            }
            step = previousNodes.get(step);
        }

        // Ensure start node is first and end node is last
        if (!route.contains(startNode)) {
            route.add(0, startNode);
        }
        if (!route.get(route.size() - 1).equals(endNode)) {
            route.add(endNode);
        }

        return route;
    }

    @Override
    public List<Address> GetAllNodes() {
        return new ArrayList<>(allNodes);
    }

    private int getNodeIndex(String postCode) {
        for (int i = 0; i < allNodes.size(); i++) {
            if (allNodes.get(i).getPostalCode().equals(postCode)) {
                return i;
            }
        }
        throw new IllegalStateException("Node not found for postcode: " + postCode);
    }

    private Address getNodeByPostCode(String postCode) {
        for (int i = 0; i < allNodes.size(); i++)
        {
            Address node = allNodes.get(i);
            if (node.getPostalCode().equals(postCode))
            {
                return node;
            }
        }
        throw new IllegalStateException("Address not found for postcode: " + postCode);
    }
}
