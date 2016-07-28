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
public class Tree {
    ArrayList <Node> T;
    Node root;
    int Score;
    Tree(Tree Tr){
        this.T=Tr.T;
    }
    Tree(ArrayList<Node> T){
        this.T=T;
        if(T.size()>0)root=T.get(T.size()-1);
    }
    
    void print(Node c){
        if(c.child[0]==null && c.child[1]==null){
            System.out.print(c.label);
            return;
        }
        System.out.print("(");
        print(c.child[0]);
        System.out.print(",");
        print(c.child[1]);
        System.out.print(")");
    }
    void print(){
        for(int i=0;i<T.size();i++){
            T.get(i).print();
        }
         System.out.println("");
    }
   public void SmallTweak()
    {
    	//first we flip a coin if it is head then we do swap else we rotate
    	Random rnd=new Random();
    	int coin=rnd.nextInt(2);
    	Node rp;
    	//coin=0;
    	int trial=7;
    	if(coin==1)
    		SwapRandomLeaves();
    	else{
    		do{
    			//System.out.println("Rotating");
    			int index=rnd.nextInt(T.size()/2-1)+(T.size()+1)/2;
        		rp=T.get(index);
        		if(--trial==0){
        			SwapRandomLeaves();
        			return;
        		}
    		}
    		while(rp.child[1]==null || rp.child[1].child[0]==null);
    		//System.out.println("Rotating "+rp.label);
    		//printTree(T.get(T.size()-1),0);
    		
    		Left_Rotate(rp);
    		//printTree(T.get(T.size()-1),0);
    		
    	}
    }
    private void SwapRandomLeaves()
    {
    	//printTree(root,0);
    	int leaf_count=(T.size()+1)/2;
    	Random r=new Random();
		//choosing two random indices of leaves
		int l1=r.nextInt(leaf_count);
		int l2=r.nextInt(leaf_count);
		
		while(T.get(l1).parent==T.get(l2).parent)
		{
			l2=r.nextInt(leaf_count);
		}
		//leaves found and now we have to swap them
    	Node leaf1=T.get(l1);
    	Node leaf2=T.get(l2);
    	Node p1=leaf1.parent;
    	Node p2=leaf2.parent;
    	
    	//System.out.println("Swapping "+leaf1.label+" and "+leaf2.label);
    	leaf1.parent=p2;
    	leaf2.parent=p1;
    	if(p1.child[0]==leaf1)
    		p1.child[0]=leaf2;
    	else p1.child[1]=leaf2;
    	
    	if(p2.child[0]==leaf2)
    		p2.child[0]=leaf1;
    	else p2.child[1]=leaf1;
    	//System.out.println();
    	//printTree(root,0);
    		
    }
    private void Left_Rotate(Node rp)
    {
    	Node temp=rp.child[1];
    	rp.child[1]=temp.child[0];
    	rp.child[1].parent=rp;
    	temp.child[0]=rp;
    	temp.parent=rp.parent;
    	rp.parent=temp;
    	if(temp.parent==null)
    	{
    		int a=T.indexOf(temp);
    		int b=T.indexOf(rp);
    		T.set(a, rp);
    		T.set(b, temp);
    	}
    	else {
    		if(temp.parent.child[0]==rp)
    			temp.parent.child[0]=temp;
    		else 
    			temp.parent.child[1]=temp;
		}
    	int ind1=T.indexOf(temp);
    	int ind2=T.indexOf(rp);
    	T.set(ind1, rp);
    	T.set(ind2, temp);
    
    }
    public void HillClimb(int iteration)
    {
    	
    	Tree current=this;
    	Tree copyTree=current.getCopy();
    	current.ParsimonizeTree();
    	int count=0;
    	while(iteration-->0)
    	{
    		copyTree.SmallTweak();
    		copyTree.ParsimonizeTree();
    		
    		if(current.Score>copyTree.Score)
    			current=copyTree.getCopy();
    	}
    	this.T=current.T;
    }
    public int ParsimonizeTree()
	{
		
		for(int i=0;i<Phylo.seqLen;i++)
		{
			//for each site
			PostOrder(root,i);
			PreOrder(root,i);
			
			//Finding the sequence of internal node
			for(int j=0;j<T.size();j++)
			{
				if(T.get(j).spc==null)T.get(j).spc=new Species();
				if(T.get(j).child[0]!=null || T.get(j).child[1]!=null)
				{
					switch(T.get(j).helper)
					{
					case 1:
						T.get(j).spc.seq[i]='A';
						break;
					case 2:
						T.get(j).spc.seq[i]='T';
						break;
					case 4:
						T.get(j).spc.seq[i]='G';
						break;
					case 8:
						T.get(j).spc.seq[i]='C';
						break;
					}
					T.get(j).finalScore+=T.get(j).score;
				}
				
				T.get(j).helper=0;
			}

			
		}
		Score=root.finalScore;
		return root.finalScore;
	}
	private void PreOrder(Node root,int site)
	{
		int count=1;
		if(root.parent!=null)
		{
			if((root.parent.helper & root.helper) !=0)
			{
				root.helper &=root.parent.helper;
			}
		}
		while(root.helper%2==0)
		{
			count=count*2;
			root.helper/=2;
		}
		root.helper=count;
		
		if(root.child[0]!=null)PreOrder(root.child[0],site);
		if(root.child[1]!=null)PreOrder(root.child[1],site);
	}
	private int PostOrder(Node root,int site)
	{
		if(root.child[0]==null && root.child[1]==null)
		{
			char c=root.spc.seq[site];
			
			switch(c)
			{
			case 'A':
				root.helper=1;
				return 1;
			case 'T':
				root.helper=2;
				return 2;
			case 'G':
				root.helper=4;
				return 4;
			case 'C':
				root.helper=8;
				return 8;
			}
		}
		else
		{
			int left_helper=PostOrder(root.child[0],site);
			int right_helper=PostOrder(root.child[1],site);
			root.score=root.child[0].score+root.child[1].score;
			
			if((left_helper & right_helper)!=0)
				root.helper=left_helper & right_helper;
			else {
				root.helper=left_helper | right_helper;
				root.score++;
			}
			if(root.child[0].height>root.child[1].height)
				root.height=root.child[0].height+1;
			else
				root.height=root.child[0].height+1;
		}
		return root.helper;
	}
	public void printTree()
	{
		printTree(root,0);
	}
	public void printTree(Node root,int depth)
	{
		System.out.println(root.label);
		
		if(root.child[0]!=null){
			for(int i=0;i<depth*5;i++)System.out.print(" ");
			System.out.print("----");
			printTree(root.child[0],depth+1);
		}
		if(root.child[1]!=null){
			for(int i=0;i<depth*5;i++){
				System.out.print(" ");
			}
			System.out.print("----");
			printTree(root.child[1],depth+1);
		
		}
	}
	public Tree getCopy()
	{
		Tree B=new Tree(new ArrayList<Node>());
		int i;
		for(i=0;i<T.size();i++)
		{
			Node t=new Node(T.get(i).spc,T.get(i).label);
			B.T.add(t);
		}
		
		for(i=(T.size()+1)/2;i<T.size();i++)
		{
			if(T.get(i).child[0]!=null)
				B.T.get(i).child[0]=B.T.get(T.indexOf(T.get(i).child[0]));
			if(T.get(i).child[1]!=null)
				B.T.get(i).child[1]=B.T.get(T.indexOf(T.get(i).child[1]));
		}
		for(i=0;i<T.size()-1;i++)
				B.T.get(i).parent=B.T.get(T.indexOf(T.get(i).parent));
		
		B.root=B.T.get(B.T.size()-1);
		return B;
	}
	public Node selectRandomInternalNode()
	{
		/*This function selects an internal node with Normal pdf 
		with a previously defined mean and variance*/
		Node r=null;
		int trial=5;
		while(r==null)
		{
			Random rnd=new Random();
			int height=(int) (rnd.nextGaussian()*Phylo.height_var_factor+root.height/Phylo.height_mean_factor);
			int count=0;
			for(int i=0;i<T.size()-1;i++)
				if(T.get(i).height==height && T.get(i)!=root && T.get(i).child[0]!=null && T.get(i).child[1]!=null)
					count++;
			
			if(count>0)
			{
				int ind=rnd.nextInt(count)+1;
				
				for(int i=(T.size()+1)/2;i<T.size();i++){
					
					if(T.get(i).height==height && T.get(i)!=root && T.get(i).child[0]!=null && T.get(i).child[1]!=null)
						ind--;
					
					if(ind==0)
					{
						r=T.get(i);
						break;
					}
				}
			}
			if(--trial==0){
				r=T.get(rnd.nextInt((T.size()-1)/2)+(T.size()+1)/2-1);
				break;
			}
		}
		return r;
	}
}
