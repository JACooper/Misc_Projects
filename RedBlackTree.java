/*
    Jesse Cooper
    Red Black Tree application
    Made with jre7
    Requires jdk or Java IDE to compile and run
    (I used jdk1.7.0_13)
 */

class RedBlackTreeApp
{
    public static void main (String[] args)
    {
      RedBlackTree RBTree = new RedBlackTree();
        
        RBTree.insert(new Node(40));
        RBTree.insert(new Node(30));
        RBTree.insert(new Node(50));
        RBTree.insert(new Node(25));
        RBTree.insert(new Node(85));
        RBTree.insert(new Node(35));
        RBTree.insert(new Node(45));
        RBTree.insert(new Node(20));
        RBTree.insert(new Node(95));
        RBTree.insert(new Node(27));
        RBTree.insert(new Node(70));
        RBTree.insert(new Node(29));
        RBTree.insert(new Node(103));
        RBTree.insert(new Node(105));
        RBTree.insert(new Node(120));
        RBTree.insert(new Node(96));
        RBTree.insert(new Node(97));
        RBTree.insert(new Node(98));
        RBTree.insert(new Node(99));
        RBTree.insert(new Node(100));
        
        System.out.println("Completed insertions without error.");
        System.out.println("Tree has a black height (not including the root) of "
                            + RBTree.getBlackHeight(RBTree.getRoot().getRightChild())
                            + "\n");
        System.out.println("Root node: " + RBTree.getRoot().getNumber());
        System.out.println("Display dump (InOrder traversal): \n"
                            + RBTree.display(RBTree.getRoot()));
        System.out.println("\n" + RBTree.searchTree(100));
        
    }//End of main
}//End of RedBlackTreeApp


class RedBlackTree
{
    private Node root;
    private int blkHgt = 0;

    //Constructors
    public RedBlackTree(){}
    
    public RedBlackTree(Node r)
    {root = r;}
    
    //Methods
    public void setRoot (Node newRoot){root = newRoot;}
    
    public Node getRoot (){return root;}
    
    /// Main insertion method
    /// Takes in a node and adds it to the tree, performing any
    /// Necessary balance operations along the way
    public void insert(Node newNode)
    {
        if(root == null)            //If there's no root, make one
        {
            root = newNode;
            root.switchColor();     //Make root black
        }
        else
        {
            Node current = root;
            boolean left = false;   //For determining if the node took a left or right turn
            int tempDepth = 0;      //For determining node "depth" in the tree
            
            boolean inserted = false;
            boolean balanced = false;
            while (inserted == false)
            {
                Node parent = null;
                
                if (newNode.getNumber() < current.getNumber())      //Left turn
                {
                    parent = current;
                    current = current.getLeftChild();
                    left = true;
                    tempDepth++;
                }
                
                else if (newNode.getNumber() > current.getNumber()) //Right turn
                {
                    parent = current;
                    current = current.getRightChild();
                    left = false;
                    tempDepth++;
                }
                
                else if (newNode.getNumber() == current.getNumber())
                {
                    inserted = true;    //Don't bother inserting, tree doesn't
                                        //allow duplicates
                    balanced = true;    //Don't bother balancing, it was already
                                        //taken care of last insertion
                }
                
                if (current == null)
                {
                    if (left == true)   //Left turn
                    {
                        parent.setLeftChild(newNode);
                        newNode.setIsLeftChild();
                    }
                    else
                    {
                        parent.setRightChild(newNode);
                        newNode.setIsRightChild();
                    }
                    newNode.setParent(parent);
                    newNode.setDepth(tempDepth);
                    current = newNode;
                    inserted = true;
                }
            }//End of insertion loop
            
            while (balanced == false)
            {    
                if (current.getParent() != null
                    && current.getParent().isRed() == true
                    && current.isRed() == true) //Created red-on-red conflict -> rotate
                {
                    Node uncle = null;
                    boolean leftChild = false;
                    boolean rightChild = false;
                    boolean leftGrandchild = false;
                    boolean rightGrandchild = false;
                    
                    if (current.getIsLeftChild() == true)
                        leftGrandchild = true;                                      //Current is a left child
                    else
                        rightGrandchild = true;                                     //Current is a right child
                    
                    if (current.getParent().getIsLeftChild() == true)
                    {
                        leftChild = true;                                           //Parent is a left child
                        uncle = current.getParent().getParent().getRightChild();    //Uncle is a right child
                    }
                    else if (current.getParent().getIsRightChild() == true)
                    {
                        rightChild = true;                                          //Parent is a right child
                        uncle = current.getParent().getParent().getLeftChild();     //Uncle is a left child
                    }
                    
                    if (uncle == null || uncle.isRed() == false)                    //If uncle is black or null -> rotate
                    {
                        if (leftGrandchild == true && leftChild == true)            //Outer left rotation
                            this.rotateRight(current.getParent().getParent());
                        else if (rightGrandchild == true && rightChild == true)     //Outer right rotation
                            this.rotateLeft(current.getParent().getParent());
                        else if (leftGrandchild == true)                            //Current is inner left grandchild
                        {
                            this.rotateRight(current.getParent());                  //Rotate parent right
                            this.rotateLeft(current.getParent());                   //Rotate current left
                        }
                        else if (rightGrandchild == true)                           //Current is inner right grandchild
                        {
                            this.rotateLeft(current.getParent());                   //Rotate parent left
                            this.rotateRight(current.getParent());                  //Rotate current right
                        }
                    }
                    else if (uncle.isRed() == true)                                 //Red -> recolor
                    {
                        colorSwap(current.getParent().getParent());                 //Make grandparent red, parent and uncle black
                        current = current.getParent().getParent();                  //Current = grandparent to check for red-red violation above
                    }
                }//End of if
                else
                    balanced = true;    //No more conflicts, tree is balanced, end loop
            }//End of while
        }//End of else
    }//End of insert
    
    /// Swap method
    /// Takes in a node and makes both it and its sibling black
    /// If its parent is not the root, also make its parent red
    public void colorSwap(Node n)               //Make a node and its sibling black
    {
        Node lc = n.getLeftChild();
        Node rc = n.getRightChild();
        if (lc != null && lc.isRed() == true)   //Make left child black
            lc.switchColor();
        if (rc != null && rc.isRed() == true)   //Make right child black
            rc.switchColor();
        if (n != root)                          //If you aren't at the top of the tree, go black -> red
            n.switchColor();
    }
    
    /// Counter Clockwise rotation method
    /// Rakes in a node and performs a counter clockwise rotation on it
    public void rotateLeft(Node N)          //Counter Clockwise
    {
        Node temp1 = N.getRightChild();
        N.severRightChild();                //Cut connection
        if (N.getParent() != null)
        {
            Node temp2 = N.getParent();
            temp1.setParent(temp2);
            if (temp2.getLeftChild() == N)  //N was a left child
            {
                temp2.setLeftChild(temp1);  //temp1 takes N's place
                temp2.setIsLeftChild();
            }
            else                            //N was a right child
            {
                temp2.setRightChild(temp1); //temp1 takes N's place
                temp2.setIsRightChild();
            }
        }
        N.setParent(temp1);                 //Rotate node (child becomes parent)
        
        if (temp1.getLeftChild() != null)   //temp1 has a crossover node
        {
            Node temp3 = temp1.getLeftChild();
            N.setRightChild(temp3);
            temp3.setParent(N);
            temp3.setIsRightChild();
        }
        
        temp1.setLeftChild(N);              //Attach rotated node
                                            //(former parent becomes child)
        N.setIsLeftChild();
        if (temp1.isRed() == true)          //If the new parent (or "root") is red
            temp1.switchColor();            //Make "root" black
        if (N.isRed() == false)             //If the rotated node (the left child) is black
            N.switchColor();                //Make it red
    }
    
    /// Clockwise rotation method
    /// Takes in a node and performs a clockwise rotation on it
    public void rotateRight(Node N)
    {
        Node temp1 = N.getLeftChild();
        N.severLeftChild();                 //Cut connection
        if (N.getParent() != null)
        {
            Node temp2 = N.getParent();
            temp1.setParent(temp2);
            if (temp2.getLeftChild() == N)  //N was a left child
            {
                temp2.setLeftChild(temp1);  //temp1 takes N's place
                temp1.setIsLeftChild();
            }
            else                            //N was a right child
            {
                temp2.setRightChild(temp1); //temp1 takes N's place
                temp1.setIsRightChild();
            }
        }
        N.setParent(temp1);                 //Rotate node
        
        if (temp1.getRightChild() != null)  //temp1 has a crossover node
        {
            Node temp2 = temp1.getRightChild();
            N.setLeftChild(temp2);
            temp2.setParent(N);
            temp2.setIsLeftChild();
        }
        
        temp1.setRightChild(N);             //Attach rotated node
                                            //(former parent becomes child)
        N.setIsRightChild();
        if (temp1.isRed() == true)          //If the new parent (or "root") is red
            temp1.switchColor();            //Make "root" black
        if (N.isRed() == false)             //If the rotated node (the right child) is black
            N.switchColor();                //Make it red
    }
    
    /// Recursive computational method
    /// Counts the number of black nodes in a tree to determine its black height
    /// Returns the tree's black height as a number
    public int getBlackHeight(Node subRoot)
    {
        int height = 0;
        if (subRoot.getLeftChild() == null
            && subRoot.getRightChild() == null) //If it's a leaf (base case)
        {
            if (subRoot.isRed() == false)       //Leaf node is black
                height++;
        }
        else
        {
            if (subRoot.getLeftChild() != null)
                height = getBlackHeight(subRoot.getLeftChild());
            else if (subRoot.getRightChild() != null)
                height = getBlackHeight(subRoot.getRightChild());
            
            if (subRoot.isRed() == false)       //Calling node is black
                height++;
        }
        return height;
    }
    
    /// Public search interface method
    /// Takes in a number representing the node to search for
    /// Returns a string describing the node and its children
    public String searchTree(int searchKey)
    {
        String returnString = "";
        if (root == null)       //Tree does not yet exist
            returnString = "The tree is empty, please insert nodes prior to search.";
        else
        {
            //Use the recursive search
            Node tempNode = this.search(root, new Node(searchKey));
            if (tempNode != null)
            {
                returnString += "The node with the value of "
                                + tempNode.getNumber() + " has a depth of "
                                + tempNode.getDepth()
                                + " from the root and is ";
                
                if (tempNode.isRed() == false)
                    returnString += "black.";
                else
                    returnString += "red.";
                
                //Could also test tempNode != root or tempNode.getDepth() != 0)
                if (tempNode.getParent() != null)
                {
                    if (tempNode.getIsLeftChild() == true)
                        returnString += " It is its parent's left child.";
                    else
                        returnString += " It is its parent's right child.";
                        
                    returnString += " Its parent has a value of "
                                    + tempNode.getParent().getNumber()
                                    + " and is ";
                                    
                    if (tempNode.getParent().isRed() == false)
                        returnString += "black.";
                    else
                        returnString += "red.";
                }
                
                if (tempNode.getRightChild() != null)
                {
                    returnString += " Its right child has a value of "
                                    + tempNode.getRightChild().getNumber()
                                    + " and is ";
                    
                    if (tempNode.getRightChild().isRed() == false)
                        returnString += "black.";
                    else
                        returnString += "red.";
                }
    
                if (tempNode.getLeftChild() != null)
                {
                    returnString += " Its left child has a value of "
                                    + tempNode.getLeftChild().getNumber()
                                    + " and is ";
                    
                    if (tempNode.getLeftChild().isRed() == false)
                        returnString += "black.";
                    else
                        returnString += "red.";
                }
            }
            else
                returnString += "The specified node could not be found.";
        }
        return returnString;
    }
    
    /// Recursive search method
    /// Takes in a root (or subroot) to begin searching from,
    /// and a node to search for
    /// Returns the node being searched for, or null if that
    /// node cannot be found
    private Node search(Node subRoot, Node searchNode)
    {
        Node returnNode = null;
        
        //If it's a leaf (base case)
        if (subRoot.getLeftChild() == null && subRoot.getRightChild() == null)
        {
            if (subRoot.equalData(searchNode) == true)
                returnNode = subRoot;
        }
        else
        {
            //It's not a leaf, but test anyway
            if (subRoot.equalData(searchNode) == true)
                returnNode = subRoot;
            else
            {
                if (subRoot.getLeftChild() != null)
                    returnNode = search(subRoot.getLeftChild(), searchNode);
                //If it wasn't found in the left branch
                if (returnNode == null)
                    if (subRoot.getRightChild() != null)
                        returnNode = search(subRoot.getRightChild(), searchNode);
            }
        }
        return returnNode;
    }
    
    /// Iterative search method
    /// Takes in a number representing the node to search for
    /// Returns the node being searched for, or null if that
    /// node cannot be found
    private Node search(int searchKey)
    {
        Node returnNode = null;
        if(root == null)
        {
            returnNode = null;      //Tree does not yet exist, return nothing
        }
        else
        {
            Node current = root;
            
            boolean found = false;
            while (found == false)
            {
                
                if (searchKey < current.getNumber())        //Left turn
                    current = current.getLeftChild();
                else if (searchKey > current.getNumber())   //Right turn
                    current = current.getRightChild();
                
                else if (searchKey == current.getNumber())
                {
                    found = true;
                    returnNode = current.clone();
                }
                
                if (current == null)            //If the last move exited the tree
                {
                    returnNode = null;          //Node does not exist
                }
            }
            
        }
        return returnNode;
    }
    
    /// Method stub for delete
    /// Takes in a searchkey and either deletes a node from
    /// the tree, or marks it as deleted without removing
    /// it from the underlying structure
    /// Currently unimplemented
    public void delete(int searchKey)
    {
        if (root == null){}                 //Tree does not yet exist, do nothing
        else
        {
            Node deletionNode = this.search(root, new Node(searchKey));
            if (deletionNode != null)       //Node exists and was found
            {
                
            }
        }
    }
    
    /// Recursive display method
    /// Takes in a root (or subroot) node and displays
    /// it and all child nodes InOrder traversal
    public String display(Node subRoot)
    {
        String resultString = "";
        if (subRoot.getLeftChild() == null
            && subRoot.getRightChild() == null)     //If it's a leaf (base case)
        {
            //System.out.println("base case hit");
            resultString += subRoot.getNumber();
            if (subRoot.isRed() == true)
                resultString += "(R)(" + subRoot.getParent().getNumber() + ")   ";
            else
                resultString += "(B)(" + subRoot.getParent().getNumber() + ")   ";
        }
        else
        {
            if (subRoot.getLeftChild() != null)
            {
                resultString += display(subRoot.getLeftChild());
            }
            if (subRoot == root)
                resultString += "(root >>)" +  subRoot.getNumber() + "(<< root)   ";
            else
            {
                resultString += subRoot.getNumber();
                if (subRoot.isRed() == true)
                    resultString += "(R)(" + subRoot.getParent().getNumber() + ")   ";
                else
                    resultString += "(B)(" + subRoot.getParent().getNumber() + ")   ";
            }
            if (subRoot.getRightChild() != null)
            {
                resultString += display(subRoot.getRightChild());
            }
        }
        return resultString;
    }
    
}//End of RedBlackTree

class Node
{
    private int number;
    private int depth = 0;        //Zero-based, so root's depth is 0
    /*
     * Not happy with depth being in the Node class; technically it refers to 
     * information about a Node, but it's only useful in the context of a tree,
     * making Node less portable. . .
     */
    private boolean red = true;
    private boolean isLeftChild = false;
    private boolean isRightChild = false;
    private Node parent = null;
    private Node leftChild = null;
    private Node rightChild = null;
    
    public Node(int n){number = n;}
    
    private Node(Node copyNode)
    {
        this.setNumber(copyNode.getNumber());
        this.setDepth(copyNode.getDepth());
        this.setParent(copyNode.getParent());
        this.setLeftChild(copyNode.getLeftChild());
        this.setRightChild(copyNode.getRightChild());
        
        if (copyNode.getIsLeftChild() == true)
            this.setIsLeftChild();
        else
            this.setIsRightChild();
            
        if (copyNode.isRed() == false)
            this.setBlack();
    }
    
    //Methods
    public boolean isRed()
    {
        boolean isRedColor = true;    //Assume red
        
        if (red == false)
            isRedColor = false;
        
        return isRedColor;
    }
    
    public void setNumber(int n){number = n;}
    
    public int getNumber(){return number;}

    public void setDepth(int d){depth = d;}
    
    public int getDepth(){return depth;}
    
    public void setRed(){red = true;}
    
    public void setBlack(){red = false;}
    
    public void switchColor()
    {
        if (red == true)
            red = false;
        else
            red = true;
    }
    
    public void setIsLeftChild()
    {
        isLeftChild = true;
        isRightChild = false;
    }
    
    public boolean getIsLeftChild(){return isLeftChild;}
    
    public void setIsRightChild()
    {
        isRightChild = true;
        isLeftChild = false;
    }
    
    public boolean getIsRightChild(){return isRightChild;}
    
    public void setLeftChild(Node lc){leftChild = lc;}
    
    public void setRightChild(Node rc){rightChild = rc;}
    
    public Node getLeftChild(){return leftChild;}
    
    public Node getRightChild(){return rightChild;}
    
    public void setParent(Node p){parent = p;}
    
    public Node getParent(){return parent;}
    
    //Did these because, as I recall, you can't pass a null parameter into a function
    //Could also make overloaded setter methods with no parameters do deletion
    public void severRightChild()
    {
        rightChild = null;      //Sever the node's rightChild connection
    }
    
    public void severLeftChild()
    {
        leftChild = null;       //Sever the node's leftChild connection
    }
    
    public boolean equalData(Node n)
    {
        boolean equivalent = false;
        if (n != null)                              //If the other node exists
            if (this.getNumber() == n.getNumber())  //And their numbers are equivalent
                equivalent = true;                  //Then the two have equal data
        return equivalent;
    }
    
    public boolean equals(Object n)
    {
        boolean equivalent = false;
        if (n == null){}        //equivalent already equals false, so do nothing
        else if (this.getClass() != n.getClass()){}
        else
        {
            Node otherNode = (Node)n; //Typecast
            if (this.getNumber() == otherNode.getNumber()
                && this.isRed() == otherNode.isRed()
                && this.getDepth() == otherNode.getDepth()
                && this.getIsLeftChild() == otherNode.getIsLeftChild()
                && this.getIsRightChild() == otherNode.getIsRightChild()
                && this.getLeftChild() == otherNode.getLeftChild() 
                && this.getRightChild() == otherNode.getRightChild()
                && this.getParent() == otherNode.getParent())
                
                equivalent = true;
        }
        return equivalent;
    }
    
    public Node clone()
    {
        Node clonedNode = new Node(this);
        return clonedNode;
    }
    
}//End of Node