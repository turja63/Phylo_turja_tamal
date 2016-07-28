/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phylo;

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
    static Species s[];
    Phylo(int n, Species s[]){
        noOfSpecies=n;
        Phylo.s=s;
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
        
        Population p= new Population(1);
        p.RandomPopulation();
        Tree t=p.pop.get(0);
        t.HillClimb(10);
        //p.print();
    }
}
