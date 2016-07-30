/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phylo;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
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
    static float height_mean_factor=(float) 2;
    static int height_var_factor=1;
    static int popSize=30;
    static int tournament_candidate=20;
    static int t=100; //number of iteration to hill climb
    static int time=200; //total time
    static int crossOver_exploitation=10; //percentage value
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
        
        long startTime=System.currentTimeMillis();
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
        
        long endTime=System.currentTimeMillis();
        Best.printTree();
        
        //write to a excel file
        String fileName="Result.xlsx";
        FileInputStream fis;
        FileOutputStream fos;
        XSSFWorkbook wb=null;
        try {
          fis= new FileInputStream(fileName);
          wb = new XSSFWorkbook(fis);
          fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        String sheetName="result";
        XSSFSheet sheet =  wb.getSheet(sheetName);
        int row_count= sheet.getLastRowNum();
        System.out.println("r:"+row_count);
        Row r=sheet.createRow(row_count+1);
        r.createCell(0).setCellValue(Best.Score);
        r.createCell(1).setCellValue((-startTime+endTime)/1000);
        try {
            fos= new FileOutputStream(fileName);
            wb.write(fos);
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("\nScore: "+Best.Score+"\nOther Scores:");
        System.out.println("Run Time: "+(-startTime+endTime)/1000+" s");
        
    }
}
