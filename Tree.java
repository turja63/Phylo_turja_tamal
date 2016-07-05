/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phylo;

import java.util.ArrayList;

/**
 *
 * @author TAMAL
 */
public class Tree {
    ArrayList <Node> T;
    Node root;
    
    Tree(Tree Tr){
        this.T=Tr.T;
    }
    Tree(ArrayList<Node> T){
        this.T=T;
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
    }
    public void ParsimonizeTree()
	{
		
		for(int i=0;i<8;i++)
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
		System.out.println("Score:"+root.finalScore);
		//System.out.println("after that");
	}
	public void PreOrder(Node root,int site)
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
	public int PostOrder(Node root,int site)
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
	public void printTree(Node root,int depth)
	{
		System.out.println(root.spc.seq);
		
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
	
}
