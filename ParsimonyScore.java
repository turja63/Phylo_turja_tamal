package phylo;

public class ParsimonyScore {

	
	public static void ParsimonizeTree(Tree T)
	{
		
		for(int i=0;i<8;i++)
		{
			//for each site
			PostOrder(T.root,i);
			PreOrder(T.root,i);
			
			//Finding the sequence of internal node
			for(int j=0;j<T.T.size();j++)
			{
				if(T.T.get(j).spc==null)T.T.get(j).spc=new Species();
				if(T.T.get(j).child[0]!=null || T.T.get(j).child[1]!=null)
				{
					switch(T.T.get(j).helper)
					{
					case 1:
						T.T.get(j).spc.seq[i]='A';
						break;
					case 2:
						T.T.get(j).spc.seq[i]='T';
						break;
					case 4:
						T.T.get(j).spc.seq[i]='G';
						break;
					case 8:
						T.T.get(j).spc.seq[i]='C';
						break;
					}
					T.T.get(j).finalScore+=T.T.get(j).score;
				}
				
				T.T.get(j).helper=0;
			}

			
		}
		System.out.println("Score:"+T.root.finalScore);
		//System.out.println("after that");
	}
	public static void PreOrder(Node root,int site)
	{
		int count=1;
		if(root.parent!=null)
		{
			root.height=root.parent.height+1;
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
	public static int PostOrder(Node root,int site)
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
		}
		return root.helper;
	}
}
