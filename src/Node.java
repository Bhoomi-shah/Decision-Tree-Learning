import java.util.*;

class Node {        
    
    Node parent;
    Node left;
    Node right;
    int value;
    int leftValue;
    int rightValue;
    ArrayList<Integer> leftDatapoints;
    ArrayList<Integer> rightDatapoints;
    ArrayList<Boolean> visited;
    DecisionTree dt;
    int number; 
    boolean isVisited;
	
    public Node()
    {
        dt= new DecisionTree();
        parent=null;
        left=null;
        right=null;
        leftValue=2;
        rightValue=2;
        visited=new ArrayList<Boolean>();
        leftDatapoints=new ArrayList<Integer>();
        rightDatapoints=new ArrayList<Integer>();  
        isVisited=false;
    }    
    
}