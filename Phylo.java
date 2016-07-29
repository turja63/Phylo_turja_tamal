/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phylo;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.Math;
/**
 *
 * @author TAMAL
 */
public class Phylo {

    /**
     * @param args the command line arguments
     */
    
    static int noOfSpecies;
    static int seqLen;
    static float height_mean_factor=(float) 1.5;
    static int height_var_factor=2;
    static int popSize=50;
    static int tournament_candidate=5;
    static int t=1; //number of iteration to hill climb
    static int time=10000; //total time
    static int crossOver_exploitation=25; //percentage value
    static Species s[];
    static ArrayList<Species> input=new ArrayList<Species>();
    Phylo(int n, Species s[]){
        noOfSpecies=n;
        Phylo.s=s;
    }
    
    ArrayList<Node> genLeafNodes(Species s[]){
        ArrayList<Node> leaves= new ArrayList<Node>();
        for(int i=0;i<s.length;i++)
            leaves.add(new Node(s[i],i));
        return leaves;
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
    	int count=0;
    	try {
			Scanner scn=new Scanner(new FileReader("Sample_data_2.txt"));
			while(scn.hasNext())
			{
				String line=scn.nextLine();
				Scanner scn2=new Scanner(line);
				line.trim();
				while(scn2.hasNext())
				{
					int flag2=0;
					String spc=scn2.next();
					String seq=scn2.next();
					if(count==0)
						seqLen=seq.length();
					else if(seq.length()>seqLen)
						seqLen=seq.length();
					input.add(new Species(spc,seq));
					char [] seq2=seq.toCharArray();
					for(int i=0;i<seq.length();i++)
						if(seq2[i]=='W' ||seq2[i]=='K' ||seq2[i]=='R' ||seq2[i]=='M' || seq2[i]=='A' || seq2[i]=='G' || seq2[i]=='T' || seq2[i]=='C' || seq2[i]=='-')
						{
							
						}
						else {
						flag2=1;
							System.out.print(seq2[i]);
						}
					if(flag2==1)System.out.println("Got culprit");
					count++;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println("I am here ");
       noOfSpecies=count;
        s= new Species[count];
        for(int i=0;i<count;i++)
        {
        	if(input.get(i).seq.length!=seqLen){
        		char[] temp=new char[seqLen];
        		for(int j=0;j<input.get(i).seq.length;j++)
        			temp[j]=input.get(i).seq[j];
        		for(int j=input.get(i).seq.length;j<seqLen;j++)
        			temp[j]='-';
        		input.get(i).seq=temp;
        	}
        	s[i]=input.get(i);
        	System.out.println(s[i].seq);
        }
        System.out.println("Species: "+s.length);
       /* s[0]= new Species("cow","ATCGGTCT");
        s[1]= new Species("bat","AAT--ACT");
        s[2]= new Species("man","CTA-G-GT");
        s[3]= new Species("spider","T-GA-TAT");
        s[4]=new Species("whale","CCTTAAGG");
        s[5]=new Species("tiger","GTAATGCC");
        s[6]=new Species("lion","TTAATGCC");
        s[7]=new Species("pokemon","GTGGAGCC");
        s[8]=new Species("pokemon1","GTGTTGCC");
        s[9]=new Species("pokemon2","CCGGAGCC");
        s[10]=new Species("pokemon3","GTGGTGCC");
        s[11]=new Species("pokemon4","AAAAAGCC");*/
        
        Population p= new Population(Phylo.popSize,s);
        System.out.println("I made it againg");
        p.RandomPopulation();		//initialize pop
        System.out.println("I made it againg ......");
        Tree Best=null;
        
        for(int i=0;i<popSize;i++){
        	Tree T=p.pop.get(i);
        	T.ParsimonizeTree(); //assess fitness
        	T.HillClimb(t); //HillClimbing
        	
        	if(Best==null || T.Score<Best.Score)
        		Best=T;
        }
        
        do
        {
        	//select parent
        	Tree P1=p.tournamentSelection(Phylo.tournament_candidate);
        	Tree P2=p.tournamentSelection(Phylo.tournament_candidate);
        	
        	while(P1==P2)
        		P2=p.tournamentSelection(Phylo.tournament_candidate);
        	
        	//cross-over
        	Tree C1=p.CrossOver(P1, P2);
        	Tree C2=p.CrossOver(P2, P1);
        	
        	C1.HillClimb(Phylo.tournament_candidate);
        	C2.HillClimb(Phylo.tournament_candidate);
      
        	C1.ParsimonizeTree();
        	C2.ParsimonizeTree();
        	
        	if(C1.Score < Best.Score)
        		Best=C1;
        	if(C2.Score < Best.Score)
        		Best=C2;
        	
        	p.SelectForDeath();
        	p.SelectForDeath();
        	
        	p.pop.add(C1);
        	p.pop.add(C2);
        	
        }while(time-->0);
        
        
        Best.printTree();
        System.out.println("\nScore: "+Best.Score+"\nOther Scores:");
        for(int i=0;i<p.pop.size();i++)
        {
        	System.out.print(p.pop.get(i).Score+" ");
        }
        
    }
}
