package RouteManagement;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class DijkstraFoodForAllDataTests
{

    // Helper method to initialize the graph and set weights between nodes
    private Dijkstra initializeGraph() {
        ArrayList<String> nodeNames = new ArrayList<>();
        nodeNames.add("Food Bank Central");
        nodeNames.add("Brasted Close");
        nodeNames.add("Steetley Court");
        nodeNames.add("Beresford Road");
        nodeNames.add("High Street");
        nodeNames.add("Castle Row");
        nodeNames.add("Green Avenue");
        nodeNames.add("Rosedrop");

        Dijkstra dijkstra = new Dijkstra(nodeNames);
        setupWeights(dijkstra);
        return dijkstra;
    }

    private void setupWeights(Dijkstra dijkstra) {
        // Food Bank Central connections (higher weights to Volunteer homes to allow recipient routes)
        dijkstra.SetWeightBetweenNodes("Food Bank Central", "Brasted Close", 9);
        dijkstra.SetWeightBetweenNodes("Food Bank Central", "Steetley Court", 4);
        dijkstra.SetWeightBetweenNodes("Food Bank Central", "Beresford Road", 5);
        dijkstra.SetWeightBetweenNodes("Food Bank Central", "High Street", 6);
        dijkstra.SetWeightBetweenNodes("Food Bank Central", "Castle Row", 7);
        dijkstra.SetWeightBetweenNodes("Food Bank Central", "Green Avenue", 11); // Slightly increased
        dijkstra.SetWeightBetweenNodes("Food Bank Central", "Rosedrop", 15); // Slightly increased

        // Brasted Close connections
        dijkstra.SetWeightBetweenNodes("Brasted Close", "Steetley Court", 2);
        dijkstra.SetWeightBetweenNodes("Brasted Close", "Beresford Road", 4);
        dijkstra.SetWeightBetweenNodes("Brasted Close", "High Street", 8);
        dijkstra.SetWeightBetweenNodes("Brasted Close", "Castle Row", 2);
        dijkstra.SetWeightBetweenNodes("Brasted Close", "Green Avenue", 4);
        dijkstra.SetWeightBetweenNodes("Brasted Close", "Rosedrop", 9);

        // Steetley Court connections
        dijkstra.SetWeightBetweenNodes("Steetley Court", "Beresford Road",4);
        dijkstra.SetWeightBetweenNodes("Steetley Court", "High Street", 5);
        dijkstra.SetWeightBetweenNodes("Steetley Court", "Castle Row", 3);
        dijkstra.SetWeightBetweenNodes("Steetley Court", "Green Avenue", 7);
        dijkstra.SetWeightBetweenNodes("Steetley Court", "Rosedrop", 8);

        // Beresford Road connections
        dijkstra.SetWeightBetweenNodes("Beresford Road", "High Street", 3);
        dijkstra.SetWeightBetweenNodes("Beresford Road", "Castle Row", 4);
        dijkstra.SetWeightBetweenNodes("Beresford Road", "Green Avenue", 11);
        dijkstra.SetWeightBetweenNodes("Beresford Road", "Rosedrop", 10); // Favorable for Volunteer B

        // High Street connections
        dijkstra.SetWeightBetweenNodes("High Street", "Castle Row", 2);
        dijkstra.SetWeightBetweenNodes("High Street", "Green Avenue", 6);
        dijkstra.SetWeightBetweenNodes("High Street", "Rosedrop", 10);

        // Castle Row connections
        dijkstra.SetWeightBetweenNodes("Castle Row", "Green Avenue", 5);
        dijkstra.SetWeightBetweenNodes("Castle Row", "Rosedrop", 4);

        // Volunteer node connections
        dijkstra.SetWeightBetweenNodes("Green Avenue", "Rosedrop", 4);
    }

    @Test
    public void testShortestPath_FoodBankToBrastedClose() {
        Dijkstra dijkstra = initializeGraph();
        Assert.assertEquals("Food Bank Central --> Steetley Court --> Brasted Close",
                dijkstra.GetShortestPath("Food Bank Central", "Brasted Close"));
        Assert.assertEquals(6, dijkstra.GetShortestPathLength("Food Bank Central", "Brasted Close"));
    }

    @Test
    public void testShortestPath_BrastedCloseToCastleRow() {
        Dijkstra dijkstra = initializeGraph();
        Assert.assertEquals("Brasted Close --> Castle Row",
                dijkstra.GetShortestPath("Brasted Close", "Castle Row"));
        Assert.assertEquals(2, dijkstra.GetShortestPathLength("Brasted Close", "Castle Row"));
    }

    @Test
    public void testShortestPath_SteetleyCourtToHighStreet() {
        Dijkstra dijkstra = initializeGraph();
        Assert.assertEquals("Steetley Court --> High Street",
                dijkstra.GetShortestPath("Steetley Court", "High Street"));
        Assert.assertEquals(5, dijkstra.GetShortestPathLength("Steetley Court", "High Street"));
    }

    @Test
    public void testShortestPath_FoodBankToVolunteerA() {
        Dijkstra dijkstra = initializeGraph();
        Assert.assertEquals("Food Bank Central --> Steetley Court --> Brasted Close --> Green Avenue",
                dijkstra.GetShortestPath("Food Bank Central", "Green Avenue"));
        Assert.assertEquals(10, dijkstra.GetShortestPathLength("Food Bank Central", "Green Avenue"));
    }

    @Test
    public void testShortestPath_FoodBankToVolunteerB() {
        Dijkstra dijkstra = initializeGraph();
        Assert.assertEquals("Food Bank Central --> Castle Row --> Rosedrop",
                dijkstra.GetShortestPath("Food Bank Central", "Rosedrop"));
        Assert.assertEquals(11, dijkstra.GetShortestPathLength("Food Bank Central", "Rosedrop"));
    }

    @Test
    public void testShortestPath_GreenAvenueToRosedrop() {
        Dijkstra dijkstra = initializeGraph();
        Assert.assertEquals("Green Avenue --> Rosedrop",
                dijkstra.GetShortestPath("Green Avenue", "Rosedrop"));
        Assert.assertEquals(4, dijkstra.GetShortestPathLength("Green Avenue", "Rosedrop"));
    }
}
