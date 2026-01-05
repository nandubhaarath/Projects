package RouteManagement;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class DijkstraTests
{

    //Dijkstra has been built based on the logic explained in this video https://www.youtube.com/watch?v=CerlT7tTZfY
    //---------------------------------------------------------------------------

    @Test
    public void Test_1_Correct_shortest_path_is_returned_between_A_and_C()
    {

        ArrayList<String> nodeNames = new ArrayList<>();
        nodeNames.add("A");
        nodeNames.add("B");
        nodeNames.add("C");
        nodeNames.add("D");
        nodeNames.add("F");
        nodeNames.add("G");
        nodeNames.add("H");
        nodeNames.add("J");


        Dijkstra dijkstra = new Dijkstra(nodeNames);

        dijkstra.SetWeightBetweenNodes("A", "C", 2);
        dijkstra.SetWeightBetweenNodes("A", "B", 4);
        dijkstra.SetWeightBetweenNodes("A", "G", 7);
        dijkstra.SetWeightBetweenNodes("C", "F", 8);
        dijkstra.SetWeightBetweenNodes("C", "G", 3);
        dijkstra.SetWeightBetweenNodes("B", "D", 2);
        dijkstra.SetWeightBetweenNodes("D", "G", 5);
        dijkstra.SetWeightBetweenNodes("D", "H", 6);
        dijkstra.SetWeightBetweenNodes("H", "J", 2);
        dijkstra.SetWeightBetweenNodes("J", "G", 4);
        dijkstra.SetWeightBetweenNodes("J", "F", 3);

        Assert.assertEquals("Shortest path between A and C", "A --> C", dijkstra.GetShortestPath("A", "C"));

    }

    @Test
    public void Test_2_Correct_shortest_path_length_is_returned_between_A_and_C()
    {

        ArrayList<String> nodeNames = new ArrayList<>();
        nodeNames.add("A");
        nodeNames.add("B");
        nodeNames.add("C");
        nodeNames.add("D");
        nodeNames.add("F");
        nodeNames.add("G");
        nodeNames.add("H");
        nodeNames.add("J");


        Dijkstra dijkstra = new Dijkstra(nodeNames);

        dijkstra.SetWeightBetweenNodes("A", "C", 2);
        dijkstra.SetWeightBetweenNodes("A", "B", 4);
        dijkstra.SetWeightBetweenNodes("A", "G", 7);
        dijkstra.SetWeightBetweenNodes("C", "F", 8);
        dijkstra.SetWeightBetweenNodes("C", "G", 3);
        dijkstra.SetWeightBetweenNodes("B", "D", 2);
        dijkstra.SetWeightBetweenNodes("D", "G", 5);
        dijkstra.SetWeightBetweenNodes("D", "H", 6);
        dijkstra.SetWeightBetweenNodes("H", "J", 2);
        dijkstra.SetWeightBetweenNodes("J", "G", 4);
        dijkstra.SetWeightBetweenNodes("J", "F", 3);

        Assert.assertEquals("Shortest path between A and C", 2, dijkstra.GetShortestPathLength("A", "C"));

    }


    @Test
    public void Test_3_Correct_shortest_path_is_returned_between_A_and_J()
    {

        ArrayList<String> nodeNames = new ArrayList<>();
        nodeNames.add("A");
        nodeNames.add("B");
        nodeNames.add("C");
        nodeNames.add("D");
        nodeNames.add("F");
        nodeNames.add("G");
        nodeNames.add("H");
        nodeNames.add("J");


        Dijkstra dijkstra = new Dijkstra(nodeNames);

        dijkstra.SetWeightBetweenNodes("A", "C", 2);
        dijkstra.SetWeightBetweenNodes("A", "B", 4);
        dijkstra.SetWeightBetweenNodes("A", "G", 7);
        dijkstra.SetWeightBetweenNodes("C", "F", 8);
        dijkstra.SetWeightBetweenNodes("C", "G", 3);
        dijkstra.SetWeightBetweenNodes("B", "D", 2);
        dijkstra.SetWeightBetweenNodes("D", "G", 5);
        dijkstra.SetWeightBetweenNodes("D", "H", 6);
        dijkstra.SetWeightBetweenNodes("H", "J", 2);
        dijkstra.SetWeightBetweenNodes("J", "G", 4);
        dijkstra.SetWeightBetweenNodes("J", "F", 3);

        Assert.assertEquals("Shortest path between A and J", "A --> C --> G --> J", dijkstra.GetShortestPath("A", "J"));

    }

    @Test
    public void Test_4_Correct_shortest_path_length_is_returned_between_A_and_J()
    {

        ArrayList<String> nodeNames = new ArrayList<>();
        nodeNames.add("A");
        nodeNames.add("B");
        nodeNames.add("C");
        nodeNames.add("D");
        nodeNames.add("F");
        nodeNames.add("G");
        nodeNames.add("H");
        nodeNames.add("J");


        Dijkstra dijkstra = new Dijkstra(nodeNames);

        dijkstra.SetWeightBetweenNodes("A", "C", 2);
        dijkstra.SetWeightBetweenNodes("A", "B", 4);
        dijkstra.SetWeightBetweenNodes("A", "G", 7);
        dijkstra.SetWeightBetweenNodes("C", "F", 8);
        dijkstra.SetWeightBetweenNodes("C", "G", 3);
        dijkstra.SetWeightBetweenNodes("B", "D", 2);
        dijkstra.SetWeightBetweenNodes("D", "G", 5);
        dijkstra.SetWeightBetweenNodes("D", "H", 6);
        dijkstra.SetWeightBetweenNodes("H", "J", 2);
        dijkstra.SetWeightBetweenNodes("J", "G", 4);
        dijkstra.SetWeightBetweenNodes("J", "F", 3);

        Assert.assertEquals("Shortest path between A and J", 9, dijkstra.GetShortestPathLength("A", "J"));

    }


    @Test
    public void Test_5_Correct_shortest_path_is_returned_between_A_and_H()
    {

        ArrayList<String> nodeNames = new ArrayList<>();
        nodeNames.add("A");
        nodeNames.add("B");
        nodeNames.add("C");
        nodeNames.add("D");
        nodeNames.add("F");
        nodeNames.add("G");
        nodeNames.add("H");
        nodeNames.add("J");


        Dijkstra dijkstra = new Dijkstra(nodeNames);

        dijkstra.SetWeightBetweenNodes("A", "C", 2);
        dijkstra.SetWeightBetweenNodes("A", "B", 4);
        dijkstra.SetWeightBetweenNodes("A", "G", 7);
        dijkstra.SetWeightBetweenNodes("C", "F", 8);
        dijkstra.SetWeightBetweenNodes("C", "G", 3);
        dijkstra.SetWeightBetweenNodes("B", "D", 2);
        dijkstra.SetWeightBetweenNodes("D", "G", 5);
        dijkstra.SetWeightBetweenNodes("D", "H", 6);
        dijkstra.SetWeightBetweenNodes("H", "J", 2);
        dijkstra.SetWeightBetweenNodes("J", "G", 4);
        dijkstra.SetWeightBetweenNodes("J", "F", 3);

        Assert.assertEquals("Shortest path between A and H", "A --> C --> G --> J --> H", dijkstra.GetShortestPath("A", "H"));

    }


    @Test
    public void Test_6_Correct_shortest_path_length_is_returned_between_A_and_H()
    {

        ArrayList<String> nodeNames = new ArrayList<>();
        nodeNames.add("A");
        nodeNames.add("B");
        nodeNames.add("C");
        nodeNames.add("D");
        nodeNames.add("F");
        nodeNames.add("G");
        nodeNames.add("H");
        nodeNames.add("J");


        Dijkstra dijkstra = new Dijkstra(nodeNames);

        dijkstra.SetWeightBetweenNodes("A", "C", 2);
        dijkstra.SetWeightBetweenNodes("A", "B", 4);
        dijkstra.SetWeightBetweenNodes("A", "G", 7);
        dijkstra.SetWeightBetweenNodes("C", "F", 8);
        dijkstra.SetWeightBetweenNodes("C", "G", 3);
        dijkstra.SetWeightBetweenNodes("B", "D", 2);
        dijkstra.SetWeightBetweenNodes("D", "G", 5);
        dijkstra.SetWeightBetweenNodes("D", "H", 6);
        dijkstra.SetWeightBetweenNodes("H", "J", 2);
        dijkstra.SetWeightBetweenNodes("J", "G", 4);
        dijkstra.SetWeightBetweenNodes("J", "F", 3);

        Assert.assertEquals("Shortest path between A and H", 11, dijkstra.GetShortestPathLength("A", "H"));

    }

    //------------------------------------------------------------------------------------------------------------

    @Test
    public void Test_7_Correct_shortest_path_is_returned_between_A_and_G()
    {

        ArrayList<String> nodeNames = new ArrayList<>();
        nodeNames.add("A");
        nodeNames.add("B");
        nodeNames.add("C");
        nodeNames.add("D");
        nodeNames.add("E");
        nodeNames.add("F");
        nodeNames.add("G");


        Dijkstra dijkstra = new Dijkstra(nodeNames);

        dijkstra.SetWeightBetweenNodes("A", "B", 4);
        dijkstra.SetWeightBetweenNodes("B", "E", 4);
        dijkstra.SetWeightBetweenNodes("E", "G", 7);
        dijkstra.SetWeightBetweenNodes("G", "F", 5);
        dijkstra.SetWeightBetweenNodes("F", "D", 2);
        dijkstra.SetWeightBetweenNodes("D", "C", 3);
        dijkstra.SetWeightBetweenNodes("C", "A", 2);
        dijkstra.SetWeightBetweenNodes("B", "C", 3);
        dijkstra.SetWeightBetweenNodes("B", "D", 7);
        dijkstra.SetWeightBetweenNodes("D", "E", 1);
        dijkstra.SetWeightBetweenNodes("E", "F", 5);


        Assert.assertEquals("Shortest path between A and G", "A --> C --> D --> F --> G", dijkstra.GetShortestPath("A", "G"));

    }


    @Test
    public void Test_8_Correct_shortest_path_is_returned_between_BeckyJane_and_JohnSmith()
    {

        ArrayList<String> nodeNames = new ArrayList<>();
        nodeNames.add("John Doe");
        nodeNames.add("Becky Jane");
        nodeNames.add("Mary Jane");
        nodeNames.add("Mary Jones");
        nodeNames.add("John Smith");

        Dijkstra dijkstra = new Dijkstra(nodeNames);

        dijkstra.SetWeightBetweenNodes("Becky Jane", "John Doe", 1);
        dijkstra.SetWeightBetweenNodes("John Doe", "Mary Jane", 4);
        dijkstra.SetWeightBetweenNodes("Mary Jane", "Becky Jane", 7);

        dijkstra.SetWeightBetweenNodes("Mary Jones", "John Smith", 7);
        dijkstra.SetWeightBetweenNodes("John Smith", "Mary Jane", 3);
        dijkstra.SetWeightBetweenNodes("Mary Jane", "Mary Jones", 2);

        dijkstra.SetWeightBetweenNodes("Becky Jane", "Mary Jones", 6);
        dijkstra.SetWeightBetweenNodes("John Doe", "Mary Jones", 3);

        Assert.assertEquals("Shortest path between Becky Jane and John Smith", "Becky Jane --> John Doe --> Mary Jane --> John Smith", dijkstra.GetShortestPath("Becky Jane", "John Smith"));

    }

    @Test
    public void Test_9_Correct_shortest_path_length_is_returned_between_BeckyJane_and_JohnSmith()
    {

        ArrayList<String> nodeNames = new ArrayList<>();
        nodeNames.add("John Doe");
        nodeNames.add("Becky Jane");
        nodeNames.add("Mary Jane");
        nodeNames.add("Mary Jones");
        nodeNames.add("John Smith");

        Dijkstra dijkstra = new Dijkstra(nodeNames);

        dijkstra.SetWeightBetweenNodes("Becky Jane", "John Doe", 1);
        dijkstra.SetWeightBetweenNodes("John Doe", "Mary Jane", 4);
        dijkstra.SetWeightBetweenNodes("Mary Jane", "Becky Jane", 7);

        dijkstra.SetWeightBetweenNodes("Mary Jones", "John Smith", 7);
        dijkstra.SetWeightBetweenNodes("John Smith", "Mary Jane", 3);
        dijkstra.SetWeightBetweenNodes("Mary Jane", "Mary Jones", 2);

        dijkstra.SetWeightBetweenNodes("Becky Jane", "Mary Jones", 6);
        dijkstra.SetWeightBetweenNodes("John Doe", "Mary Jones", 3);

        Assert.assertEquals("Shortest path between Becky Jane and John Smith", 8, dijkstra.GetShortestPathLength("Becky Jane", "John Smith"));
    }

    @Test
    public void Test_10_Correct_shortest_path_is_returned_between_BeckyJane_and_MaryJones()
    {

        ArrayList<String> nodeNames = new ArrayList<>();
        nodeNames.add("John Doe");
        nodeNames.add("Becky Jane");
        nodeNames.add("Mary Jane");
        nodeNames.add("Mary Jones");
        nodeNames.add("John Smith");

        Dijkstra dijkstra = new Dijkstra(nodeNames);

        dijkstra.SetWeightBetweenNodes("Becky Jane", "John Doe", 1);
        dijkstra.SetWeightBetweenNodes("John Doe", "Mary Jane", 4);
        dijkstra.SetWeightBetweenNodes("Mary Jane", "Becky Jane", 7);

        dijkstra.SetWeightBetweenNodes("Mary Jones", "John Smith", 7);
        dijkstra.SetWeightBetweenNodes("John Smith", "Mary Jane", 3);
        dijkstra.SetWeightBetweenNodes("Mary Jane", "Mary Jones", 2);

        dijkstra.SetWeightBetweenNodes("Becky Jane", "Mary Jones", 6);
        dijkstra.SetWeightBetweenNodes("John Doe", "Mary Jones", 3);

        Assert.assertEquals("Shortest path between Becky Jane and Mary Jones", "Becky Jane --> John Doe --> Mary Jones", dijkstra.GetShortestPath("Becky Jane", "Mary Jones"));

    }

    @Test
    public void Test_11_Correct_shortest_path_length_is_returned_between_BeckyJane_and_MaryJones()
    {

        ArrayList<String> nodeNames = new ArrayList<>();
        nodeNames.add("John Doe");
        nodeNames.add("Becky Jane");
        nodeNames.add("Mary Jane");
        nodeNames.add("Mary Jones");
        nodeNames.add("John Smith");

        Dijkstra dijkstra = new Dijkstra(nodeNames);

        dijkstra.SetWeightBetweenNodes("Becky Jane", "John Doe", 1);
        dijkstra.SetWeightBetweenNodes("John Doe", "Mary Jane", 4);
        dijkstra.SetWeightBetweenNodes("Mary Jane", "Becky Jane", 7);

        dijkstra.SetWeightBetweenNodes("Mary Jones", "John Smith", 7);
        dijkstra.SetWeightBetweenNodes("John Smith", "Mary Jane", 3);
        dijkstra.SetWeightBetweenNodes("Mary Jane", "Mary Jones", 2);

        dijkstra.SetWeightBetweenNodes("Becky Jane", "Mary Jones", 6);
        dijkstra.SetWeightBetweenNodes("John Doe", "Mary Jones", 3);

        Assert.assertEquals("Shortest path between Becky Jane and Mary Jones", 4, dijkstra.GetShortestPathLength("Becky Jane", "Mary Jones"));
    }


    @Test
    public void Test_12_Correct_shortest_path_is_returned_between_BeckyJane_and_MaryJane()
    {

        ArrayList<String> nodeNames = new ArrayList<>();
        nodeNames.add("John Doe");
        nodeNames.add("Becky Jane");
        nodeNames.add("Mary Jane");
        nodeNames.add("Mary Jones");
        nodeNames.add("John Smith");

        Dijkstra dijkstra = new Dijkstra(nodeNames);

        dijkstra.SetWeightBetweenNodes("Becky Jane", "John Doe", 1);
        dijkstra.SetWeightBetweenNodes("John Doe", "Mary Jane", 4);
        dijkstra.SetWeightBetweenNodes("Mary Jane", "Becky Jane", 7);

        dijkstra.SetWeightBetweenNodes("Mary Jones", "John Smith", 7);
        dijkstra.SetWeightBetweenNodes("John Smith", "Mary Jane", 3);
        dijkstra.SetWeightBetweenNodes("Mary Jane", "Mary Jones", 2);

        dijkstra.SetWeightBetweenNodes("Becky Jane", "Mary Jones", 6);
        dijkstra.SetWeightBetweenNodes("John Doe", "Mary Jones", 3);

        Assert.assertEquals("Shortest path between Becky Jane and Mary Jane", "Becky Jane --> John Doe --> Mary Jane", dijkstra.GetShortestPath("Becky Jane", "Mary Jane"));

    }
    @Test
    public void Test_13_Correct_shortest_path_length_is_returned_between_BeckyJane_and_MaryJane()
    {

        ArrayList<String> nodeNames = new ArrayList<>();
        nodeNames.add("John Doe");
        nodeNames.add("Becky Jane");
        nodeNames.add("Mary Jane");
        nodeNames.add("Mary Jones");
        nodeNames.add("John Smith");

        Dijkstra dijkstra = new Dijkstra(nodeNames);

        dijkstra.SetWeightBetweenNodes("Becky Jane", "John Doe", 1);
        dijkstra.SetWeightBetweenNodes("John Doe", "Mary Jane", 4);
        dijkstra.SetWeightBetweenNodes("Mary Jane", "Becky Jane", 7);

        dijkstra.SetWeightBetweenNodes("Mary Jones", "John Smith", 7);
        dijkstra.SetWeightBetweenNodes("John Smith", "Mary Jane", 3);
        dijkstra.SetWeightBetweenNodes("Mary Jane", "Mary Jones", 2);

        dijkstra.SetWeightBetweenNodes("Becky Jane", "Mary Jones", 6);
        dijkstra.SetWeightBetweenNodes("John Doe", "Mary Jones", 3);

        Assert.assertEquals("Shortest path between Becky Jane and Mary Jane", 5, dijkstra.GetShortestPathLength("Becky Jane", "Mary Jane"));
    }

    @Test
    public void Test_14_Correct_shortest_path_is_returned_between_BeckyJane_and_JohnDoe()
    {

        ArrayList<String> nodeNames = new ArrayList<>();
        nodeNames.add("John Doe");
        nodeNames.add("Becky Jane");
        nodeNames.add("Mary Jane");
        nodeNames.add("Mary Jones");
        nodeNames.add("John Smith");

        Dijkstra dijkstra = new Dijkstra(nodeNames);

        dijkstra.SetWeightBetweenNodes("Becky Jane", "John Doe", 1);
        dijkstra.SetWeightBetweenNodes("John Doe", "Mary Jane", 4);
        dijkstra.SetWeightBetweenNodes("Mary Jane", "Becky Jane", 7);

        dijkstra.SetWeightBetweenNodes("Mary Jones", "John Smith", 7);
        dijkstra.SetWeightBetweenNodes("John Smith", "Mary Jane", 3);
        dijkstra.SetWeightBetweenNodes("Mary Jane", "Mary Jones", 2);

        dijkstra.SetWeightBetweenNodes("Becky Jane", "Mary Jones", 6);
        dijkstra.SetWeightBetweenNodes("John Doe", "Mary Jones", 3);

        Assert.assertEquals("Shortest path between Becky Jane and John Doe", "Becky Jane --> John Doe", dijkstra.GetShortestPath("Becky Jane", "John Doe"));

    }

    @Test
    public void Test_15_Correct_shortest_path_length_is_returned_between_BeckyJane_and_JohnDoe()
    {

        ArrayList<String> nodeNames = new ArrayList<>();
        nodeNames.add("John Doe");
        nodeNames.add("Becky Jane");
        nodeNames.add("Mary Jane");
        nodeNames.add("Mary Jones");
        nodeNames.add("John Smith");

        Dijkstra dijkstra = new Dijkstra(nodeNames);

        dijkstra.SetWeightBetweenNodes("Becky Jane", "John Doe", 1);
        dijkstra.SetWeightBetweenNodes("John Doe", "Mary Jane", 4);
        dijkstra.SetWeightBetweenNodes("Mary Jane", "Becky Jane", 7);

        dijkstra.SetWeightBetweenNodes("Mary Jones", "John Smith", 7);
        dijkstra.SetWeightBetweenNodes("John Smith", "Mary Jane", 3);
        dijkstra.SetWeightBetweenNodes("Mary Jane", "Mary Jones", 2);

        dijkstra.SetWeightBetweenNodes("Becky Jane", "Mary Jones", 6);
        dijkstra.SetWeightBetweenNodes("John Doe", "Mary Jones", 3);

        Assert.assertEquals("Shortest path between Becky Jane and John Doe", 1, dijkstra.GetShortestPathLength("Becky Jane", "John Doe"));
    }


    @Test
    public void Test_16_Correct_shortest_path_is_returned_between_JohnDoe_and_JohnSmith()
    {

        ArrayList<String> nodeNames = new ArrayList<>();
        nodeNames.add("John Doe");
        nodeNames.add("Becky Jane");
        nodeNames.add("Mary Jane");
        nodeNames.add("Mary Jones");
        nodeNames.add("John Smith");

        Dijkstra dijkstra = new Dijkstra(nodeNames);

        dijkstra.SetWeightBetweenNodes("Becky Jane", "John Doe", 1);
        dijkstra.SetWeightBetweenNodes("John Doe", "Mary Jane", 4);
        dijkstra.SetWeightBetweenNodes("Mary Jane", "Becky Jane", 7);

        dijkstra.SetWeightBetweenNodes("Mary Jones", "John Smith", 7);
        dijkstra.SetWeightBetweenNodes("John Smith", "Mary Jane", 3);
        dijkstra.SetWeightBetweenNodes("Mary Jane", "Mary Jones", 2);

        dijkstra.SetWeightBetweenNodes("Becky Jane", "Mary Jones", 6);
        dijkstra.SetWeightBetweenNodes("John Doe", "Mary Jones", 3);

        Assert.assertEquals("Shortest path between John Doe and John Smith", "John Doe --> Mary Jane --> John Smith", dijkstra.GetShortestPath("John Doe", "John Smith"));

    }




    @Test
    public void Test_17_Correct_shortest_path_length_is_returned_between_JohnDoe_and_JohnSmith()
    {

        ArrayList<String> nodeNames = new ArrayList<>();
        nodeNames.add("John Doe");
        nodeNames.add("Becky Jane");
        nodeNames.add("Mary Jane");
        nodeNames.add("Mary Jones");
        nodeNames.add("John Smith");

        Dijkstra dijkstra = new Dijkstra(nodeNames);

        dijkstra.SetWeightBetweenNodes("Becky Jane", "John Doe", 1);
        dijkstra.SetWeightBetweenNodes("John Doe", "Mary Jane", 4);
        dijkstra.SetWeightBetweenNodes("Mary Jane", "Becky Jane", 7);

        dijkstra.SetWeightBetweenNodes("Mary Jones", "John Smith", 7);
        dijkstra.SetWeightBetweenNodes("John Smith", "Mary Jane", 3);
        dijkstra.SetWeightBetweenNodes("Mary Jane", "Mary Jones", 2);

        dijkstra.SetWeightBetweenNodes("Becky Jane", "Mary Jones", 6);
        dijkstra.SetWeightBetweenNodes("John Doe", "Mary Jones", 3);

        Assert.assertEquals("Shortest path between John Doe and John Smith", 7, dijkstra.GetShortestPathLength("John Doe", "John Smith"));
    }




}

