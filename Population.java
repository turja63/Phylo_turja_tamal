/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phylo;

import java.util.ArrayList;
import java.util.Random;


/**
 *
 * @author TAMAL
 */
public class Population {
    int popSize;
    ArrayList<Tree> pop;
    ArrayList<Node> leaves;
    Population(int n, ArrayList<Tree> p, Species s[]){
        popSize=n;
        pop=p;
        leaves= new ArrayList<Node>();
        for(int i=0;i<s.length;i++)
            leaves.add(new Node(s[i],i));
    }
    Population(int n, Species s[]){
        popSize=n;
        pop= new ArrayList<Tree>();
        leaves= new ArrayList<Node>();
        for(int i=0;i<s.length;i++)
            leaves.add(new Node(s[i],i));
    }
    
    void RandomPopulation(){
        for(int i=0;i<popSize;i++)
        {
        	Tree t=GenRandomIndividual();
        	t.ParsimonizeTree();
            pop.add(t);
            
            //ParsimonyScore.ParsimonizeTree(pop.get(i));
        }
    }
    
    Tree GenRandomIndividual(){
        Tree t;
        Random rn= new Random();
        
        ArrayList<Node> ind=new ArrayList<Node>();
        ArrayList<Node> spcs=new ArrayList<Node>();
        for(int i=0;i<leaves.size();i++)
        {
        	Node _t=new Node(leaves.get(i).spc,leaves.get(i).label);
        	ind.add(_t);
        	spcs.add(_t);
        }
        
        int count=0;
        while(spcs.size()>1){
            Node newInt= new Node(null,Phylo.noOfSpecies+count);
            count++;
            for(int i=0;i<2;i++){
                int index= rn.nextInt(spcs.size());
                Node removed=spcs.remove(index);
                if(removed.spc!=null)
                {    ind.get(removed.label).parent=newInt;
                    newInt.child[i]=ind.get(removed.label); 
                }else{
                    removed.parent=newInt;
                    newInt.child[i]=removed;
                    ind.add(removed);  
                }
            }
            spcs.add(newInt);
        //    System.out.println(count+ " " + spcs.size());
        }
        Node r=spcs.remove(0);
        ind.add(r);
        t= new Tree(ind);
        t.root=r;
        return t;
    }
    
    void print(){
       
        for(int i=0; i< popSize;i++){
            pop.get(i).print();
            pop.get(i).printTree(pop.get(i).root,0);
            System.out.println();
        }
    }
    Tree tournamentSelection(int numberOfCandidate)
    {
    	Random rnd=new Random();
    	Tree t=pop.get(rnd.nextInt(popSize));
    	
    	for(int i=0;i<numberOfCandidate;i++){
    		Tree temp=pop.get(rnd.nextInt(popSize));
    		if(temp.root.score<t.root.score)
    			t=temp;
    	}
    	return t;
    }
    Tree SelectForDeath()
    {
    	Random rnd=new Random();
    	return pop.remove(rnd.nextInt(popSize));
    }
    private void getLeaves(Node root,ArrayList<Node> L)
    {
    	if(root.child[0]==null && root.child[1]==null){
    		L.add(root);
    		return;
    	}
    	getLeaves(root.child[0],L);
    	getLeaves(root.child[1],L);
    }
    Tree CrossOver(Tree pr1,Tree pr2)
    {
    	Tree P1=pr1.getCopy();//child1
    	Tree P2=pr2.getCopy();//child2
    	
    	Node iNode=P1.selectRandomInternalNode(); //select random internalNode
    	System.out.println("Selected : "+iNode.label);
    	ArrayList<Node> L=new ArrayList<Node>();
    	getLeaves(iNode,L);                       //find corresponding leaves
    	
    	P1.printTree();
    	System.out.println();
    	P2.printTree();
   
    	//delete those leaves from P2
    	ArrayList<Node> spareInternalNodes=new ArrayList<Node>();
    	for(int i=0;i<L.size();i++)
    	{
    		System.out.println("Deleting : "+L.get(i).label);
    		Node leaf_1=L.get(i);
    		Node leaf_2=P2.T.get(leaf_1.label);
    		Node parent=leaf_2.parent;
    		Node sibling = null;
    		Node grandParent=parent.parent;
    		
    		for(int j=0;j<2;j++)
	    		if(parent.child[j]==leaf_2)
	    		{
	    			parent.child[j]=null;
	    			sibling=parent.child[1-j];
	    			break;
	    		}
    		
    		for(int j=0;j<2;j++)
    			if(grandParent!=null && grandParent.child[j]==parent)
    			{
    				grandParent.child[j]=sibling;
    				break;
    			}
    			else if(grandParent==null)
    			{
    				P2.root=sibling;
    			}
    		
    		leaf_2.parent=null;
    		sibling.parent=grandParent;
    		spareInternalNodes.add(parent);
    	}
    	P2.printTree();
    	//leaves deleted
    	do{
    		iNode=P2.selectRandomInternalNode();
    	}while(iNode.child[0]==null || iNode.child[1]==null);
    	Random rnd=new Random();
    	int count=0;
    	while(L.size()>1)
    	{
	    	int l1=rnd.nextInt(L.size());
	    	Node removed_1=L.remove(l1);
	    	int l2=rnd.nextInt(L.size());
	    	Node removed_2=L.remove(l2);
	    	
	    	Node p=spareInternalNodes.get(count++);
	    	removed_1.parent=p;
	    	removed_2.parent=p;
	    	p.child[0]=removed_1;
	    	p.child[1]=removed_2;
	    	
	    	L.add(p);
    	}
    	
    	
    	System.out.println("Selected : "+iNode.label);
    	Node r=spareInternalNodes.get(count);
    	for(int i=0;i<2;i++)
	    	if(iNode.parent.child[i]==iNode)
	    	{
	    		iNode.parent.child[i]=r;
	    		r.parent=iNode.parent;
	    		r.child[i]=iNode;
	    		iNode.parent=r;
	    		r.child[1-i]=L.get(0);
	    		L.get(0).parent=r;
	    		break;
	    	}
    	
    	System.out.println("\nActual Tree\n");
    	P2.printTree();
    	
    	return P2;
    }
    
    
}
