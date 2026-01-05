package RouteManagement;

import FoodParcelManagement.FoodParcelRequest;
import UserManagement.Address;

import java.util.List;

public interface IRouteMapDijkstra {
    int[][] AutoGenerateWeights(Address startNode, Address endNode, List<FoodParcelRequest> requests);

    List<Address> GenerateRouteMap(Address startNode, Address endNode, List<FoodParcelRequest> requests, int[][] weightMatrix);

    List<Address> GetAllNodes();
}
