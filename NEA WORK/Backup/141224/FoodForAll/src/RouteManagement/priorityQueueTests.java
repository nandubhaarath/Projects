package RouteManagement;

import org.junit.Assert;
import org.junit.Test;

public class priorityQueueTests
{

    @Test
    public void Test_DeQueue_Returns_Correct_Element_When_Two_Records_Are_Added()
    {
        PriorityQueue priorityQueue= new PriorityQueue();

        priorityQueue.Add("A" ,"C",2);
        priorityQueue.Add("A","G",5);

        Node expectedNode= new Node("A","C",2);
        Node actualNode= priorityQueue.DeQueue();

        Assert.assertEquals("Dequeue returns correct start node ordered by weight",expectedNode.getStartNode(),actualNode.getStartNode());
        Assert.assertEquals("Dequeue returns correct end node ordered by weight",expectedNode.getEndNode(),actualNode.getEndNode());
        Assert.assertEquals("Dequeue returns correct weight",expectedNode.getWeight(),actualNode.getWeight());

    }

    @Test
    public void Test_DeQueue_Returns_Correct_Element_When_Four_Records_Are_Added()
    {
        PriorityQueue priorityQueue= new PriorityQueue();

        priorityQueue.Add("B" ,"A",4);
        priorityQueue.Add("B","E",4);
        priorityQueue.Add("B","C",3);
        priorityQueue.Add("B","D",7);

        Node expectedNode= new Node("B","C",3);
        Node actualNode= priorityQueue.DeQueue();


        Assert.assertEquals("Dequeue returns correct start node ordered by weight",expectedNode.getStartNode(),actualNode.getStartNode());
        Assert.assertEquals("Dequeue returns correct end node ordered by weight",expectedNode.getEndNode(),actualNode.getEndNode());
        Assert.assertEquals("Dequeue returns correct weight",expectedNode.getWeight(),actualNode.getWeight());

    }

    @Test
    public void Test_DeQueue_Returns_Correct_Element_Between_A_And_G()
    {
        PriorityQueue priorityQueue= new PriorityQueue();

        priorityQueue.Add("A","B",4);
        priorityQueue.Add("B","E",4);
        priorityQueue.Add("E","G",7);
        priorityQueue.Add("G","F",5);
        priorityQueue.Add("F","D",2);
        priorityQueue.Add("D","C",3);
        priorityQueue.Add("C","A",2);
        priorityQueue.Add("B","C",3);
        priorityQueue.Add("B","D",7);
        priorityQueue.Add("E","D",1);
        priorityQueue.Add("E","F",5);


        Node expectedNode= new Node("E","D",1);
        Node actualNode= priorityQueue.DeQueue();


        Assert.assertEquals("Dequeue returns correct start node ordered by weight",expectedNode.getStartNode(),actualNode.getStartNode());
        Assert.assertEquals("Dequeue returns correct end node ordered by weight",expectedNode.getEndNode(),actualNode.getEndNode());
        Assert.assertEquals("Dequeue returns correct weight",expectedNode.getWeight(),actualNode.getWeight());


    }

    @Test
    public void Test_DeQueue_Returns_Correct_Element_When_Five_Records_Are_Added()
    {
        PriorityQueue priorityQueue= new PriorityQueue();

        priorityQueue.Add("D" ,"C",3);
        priorityQueue.Add("D","F",2);
        priorityQueue.Add("D","E",1);
        priorityQueue.Add("D","B",7);

        Node expectedNode= new Node("D","E",1);
        Node actualNode= priorityQueue.DeQueue();


        Assert.assertEquals("Dequeue returns correct start node ordered by weight",expectedNode.getStartNode(),actualNode.getStartNode());
        Assert.assertEquals("Dequeue returns correct end node ordered by weight",expectedNode.getEndNode(),actualNode.getEndNode());
        Assert.assertEquals("Dequeue returns correct weight",expectedNode.getWeight(),actualNode.getWeight());

    }





}

