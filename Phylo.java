/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phylo;
import java.util.ArrayList;
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
    static int seqLen=8;
    static float height_mean_factor=(float) 1.5;
    static int height_var_factor=1;
    static int popSize=10;
    static int tournament_candidate=5;
    static int t=5; //number of iteration to hill climb
    static int time=10000; //total time
    static int crossOver_exploitation=15; //percentage value
    static Species s[];
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
       noOfSpecies=12;
        s= new Species[noOfSpecies];
        s[0]= new Species("cow","ATCGGTCT");
        s[1]= new Species("bat","AATCGACT");
        s[2]= new Species("man","CTAAGTGT");
        s[3]= new Species("spider","TAGAGTAT");
        s[4]=new Species("whale","CCTTAAGG");
        s[5]=new Species("tiger","GTAATGCC");
        s[6]=new Species("lion","TTAATGCC");
        s[7]=new Species("pokemon","GTGGAGCC");
        s[8]=new Species("pokemon1","GTGTTGCC");
        s[9]=new Species("pokemon2","CCGGAGCC");
        s[10]=new Species("pokemon3","GTGGTGCC");
        s[11]=new Species("pokemon4","AAAAAGCC");
        
        Population p= new Population(Phylo.popSize,s);
        p.RandomPopulation();		//initialize pop
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
