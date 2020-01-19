//Name(s): Waris Vorathumdusadee, Apisarit Sungkornjittakupt, Sirichoke Yooyen
//ID 6088128,6088181,6088232
//Section 3(all)
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * This class implements PageRank algorithm on simple graph structure.
 * Put your name(s), ID(s), and section here.
 *
 */
public class PageRanker {
	/**
	 * This class reads the direct graph stored in the file "inputLinkFilename" into memory.
	 * Each line in the input file should have the following format:
	 * <pid_1> <pid_2> <pid_3> .. <pid_n>
	 * 
	 * Where pid_1, pid_2, ..., pid_n are the page IDs of the page having links to page pid_1. 
	 * You can assume that a page ID is an integer.
	 */
	Map<Integer, Set<Integer>> LinkoutPage = null;
	private Set<String> info = new LinkedHashSet<String>();
	
	private Set<Integer> TotalPage = null;
	Map<Integer, Double> PageRank = null;
	private double NextPerplexity = -234442;
	private double d = 0.85;
	Set<Integer> NotLink = null;
	private double LastPerplexity = -2124;
	
	private int check = 1;
	int c =0;
	Map<Integer, Set<Integer>> LinkinPage = null;
	Map<Integer, Double> newPR = new HashMap<Integer,Double>();
//	this map will save the value of score and page ID 
	private Set<Double> perplexity = new LinkedHashSet<Double>();
//	this Set will save the value of perplexity
	public void loadData(String inputLinkFilename){
		try {
//			int c =0;
			
			File file = new File(inputLinkFilename);
			Scanner data = new Scanner(file);
//			this while loop will read until complete all of the line
			while(data.hasNextLine()){
				
				
//				String line = data.nextLine();
//				System.out.println("line = "+line);
//				String each[] = line.split(" ");
//				System.out.println("each = "+each[0]);
//				data.nextLine();
				
//				this info will add the value that read line by line
				info.add(data.nextLine());
				c+=1;
				
				

			}
//			System.out.println("c = "+c);
		}
//		catch for the error
		catch(Exception e){
		e.printStackTrace();
			
		}
		
	}
	
	
	
	
	/**
	 * This method will be called after the graph is loaded into the memory.
	 * This method initialize the parameters for the PageRank algorithm including
	 * setting an initial weight to each page.
	 */
	public void initialize()
	{
//		System.out.println("c = "+c);
		PageRank = new HashMap<Integer, Double>();
//		this PageRank is the page rank score of each page
		TotalPage = new LinkedHashSet<Integer>();
//		this TotalPage is the information of total page 
		LinkinPage = new HashMap<Integer, Set<Integer>>();
//		this LinkinPage is that store the linkinpage 
		LinkoutPage = new HashMap<Integer, Set<Integer>>();
//		this LinkoutPage is that store the LinkoutPage 
		NotLink = new LinkedHashSet<Integer>();
//		this NotLink is that store the page has not Link 
		int test = 0;
		for(String Totalinfo:info) 
		{
			test +=1;
//			System.out.println(Totalinfo);
			String[] spc = Totalinfo.split(" ");
//			System.out.println("spc = "+spc[0]);
			Set<Integer> Linkintemp = new LinkedHashSet<Integer>();
//			Linkintemp for store the value of link in page to prepare before add the value
//			this loop will run until all the value of each line
			for(int i=0;i<spc.length;i++) 
			{
				Set<Integer> tempLinkout = new LinkedHashSet<Integer>();
//				tempLinkout for store the value of link out page to prepare before add the value
//			
//				add the page id to this map
				TotalPage.add(Integer.parseInt(spc[i]));
//				if i more than or equal one will do in here
				
				if(i>=1)
				{
//					add the value of linkintemp to this set before add the value 
					Linkintemp.add(Integer.parseInt(spc[i]));
					if(LinkoutPage.containsKey(Integer.parseInt(spc[i]))) 
					{
//						LinkoutPage will get that page ID and add the page ID that link out
						LinkoutPage.get(Integer.parseInt(spc[i])).add(Integer.parseInt(spc[0]));
					}
					else 
					{
//						NotLink.add(Integer.parseInt(spc[i]));
						
//						Set<Integer> tempLinkout = new LinkedHashSet<Integer>();
						
//						it will add the page ID that page link out 
						tempLinkout.add(Integer.parseInt(spc[0]));
//						it will add the page ID that page link out 
						LinkoutPage.put(Integer.parseInt(spc[i]), tempLinkout);
					}
				}
			}
//			add the link in page 
		LinkinPage.put(Integer.parseInt(spc[0]), Linkintemp);
		}
		
//		this loop will assign the score of each page by using 1 over n
		for(Integer page: TotalPage) 
		{
			PageRank.put(page, 1.0/TotalPage.size());
//			if that page has link out will skip
			if(LinkoutPage.containsKey(page)==true) 
			{
				continue;
			}
			else 
			{
//				if that page has not link out will add the value into the map of notlink
				NotLink.add(page);
			}
		}
//		NotLink.addAll(TotalPage);
//		NotLink.removeAll((Collection<?>) LinkinPage);
//		System.out.println("PageRank = "+PageRank.size());
//		System.out.println("TotalPage = "+TotalPage.size());
//		System.out.println("LinkinPage = "+LinkinPage.size());
//		System.out.println("LinkoutPage = "+LinkoutPage.size());
//		System.out.println("NotLink = "+NotLink.size());
//		System.out.println("LinkoutPage = "+LinkoutPage.size());
	}
	/**
	 * Computes the perplexity of the current state of the graph. The definition
	 * of perplexity is given in the project specs.
	 */
	
	
	
	public double getPerplexity()
	{
		double pow = 0.0;
		double perplexity = 0.0;
		for(Integer tp: PageRank.keySet()) 
		{
			double score = PageRank.get(tp);
			//set score equal the score of each page rank
			pow += score* log2(score);
//			sum the pow like a summation
			 
		}
		
		perplexity = Math.pow(2, -pow);
//		return the perplexity 
		return perplexity;
	}
	public double log2(double score) {
		return Math.log(score)/Math.log(2);
	}
	

	
	/**
	 * Returns true if the perplexity converges (hence, terminate the PageRank algorithm).
	 * Returns false otherwise (and PageRank algorithm continue to update the page scores). 
	 */
	public boolean isConverge(){
		
		int next = (int)NextPerplexity % 10;
//		find the decimal point number of next perplexity
		int last = (int)LastPerplexity % 10;
//		find the decimal point number of last perplexity
//		find the different between next and last value if it very close it will approximate 0 
//		int comp = Integer.compare(next,last); 
//		int comp = next-last; 
		if(next==last) 
		{
			check -=-1;
//			if check equal 4 it will return true it mean that it is very very close enough
			if(check == 4) 
			{
				return true;
			}	
//			else it mean it not close enough yet
			else {
				return false;
			}
	
		}
//		else this for that value it not close enough 
		else 
		{
//			set the value of LastPerplexity to the new value and set check equal to 1
			LastPerplexity = NextPerplexity;
			check=1;
		}
		return false;
		
	}
	
	
	
	
	/**
	 * The main method of PageRank algorithm. 
	 * Can assume that initialize() has been called before this method is invoked.
	 * While the algorithm is being run, this method should keep track of the perplexity
	 * after each iteration. 
	 * 
	 * Once the algorithm terminates, the method generates two output files.
	 * [1]	"perplexityOutFilename" lists the perplexity after each iteration on each line. 
	 * 		The output should look something like:
	 *  	
	 *  	183811
	 *  	79669.9
	 *  	86267.7
	 *  	72260.4
	 *  	75132.4
	 *  
	 *  Where, for example,the 183811 is the perplexity after the first iteration.
	 *
	 * [2] "prOutFilename" prints out the score for each page after the algorithm terminate.
	 * 		The output should look something like:
	 * 		
	 * 		1	0.1235
	 * 		2	0.3542
	 * 		3 	0.236
	 * 		
	 * Where, for example, 0.1235 is the PageRank score of page 1.
	 * 
	 */
	public void runPageRank(String perplexityOutFilename, String prOutFilename){
		
		//this while loop will run until it is converge or the score of each is very close
		while(isConverge()==false) 
		{
			Map<Integer, Double> newPR = new HashMap<Integer,Double>();
			double SinkRank = 0;
//			this loop will find the value of score of the sink out page
			for(Integer x: NotLink) 
			{
				SinkRank+=PageRank.get(x);
			}
//			this loop will find the new score of all page
			for(Integer tp : TotalPage ) 
			{
//			find the new score of that page	
				double NewScore = (1-d)*1/TotalPage.size();
//				after that plus by this formula
				NewScore += d*SinkRank*1/TotalPage.size();
//				This condition that for check this page is has link in page or not
				if(LinkinPage.containsKey(tp)) 
				{	
//					if has it will run until complete all of node that link in
					for(Integer lp: LinkinPage.get(tp)) 
					{
//						find the new score by using this formula
						NewScore = NewScore + d*PageRank.get(lp)/LinkoutPage.get(lp).size();
					}
				
				}
//				put the new score of that page into this map
			newPR.put(tp, NewScore);
			}
//			set the original page rank equal the new score. 
			PageRank = newPR;
			NextPerplexity = getPerplexity();
//			set the NextPerplexity
			perplexity.add(NextPerplexity);
//			and add this value for write a file
		}
		
		 
		try{    
	           FileWriter fileW1=new FileWriter(prOutFilename); 
	           //write the file pr_scores.txt.
	           FileWriter fileW2 = new FileWriter(perplexityOutFilename);
	           //write the file perplexity.txt.
//	           this loop will write the score of page 
	           for(Integer tp: PageRank.keySet()) 
	           {	
//	        	   by using append and use this format
	        	   String format = tp + " " +PageRank.get(tp)+"\n";
	        	   	fileW1.append(format);
	           }
	           
//	           this loop will write the perplexity of each round 
	           for(Double per:perplexity) 
	           {	
//	        	   by using append and use this format
	        	   String format = per+"\n";
	        	   fileW2.append(format);
	           }
	           
	           fileW1.close();
//	           close two file that already written
	           fileW2.close();
	          }
		catch(Exception e)
//		here will prevent that cannot write the file 
			{
				e.printStackTrace();
	         }    
	   }
	
	
	/**
	 * Return the top K page IDs, whose scores are highest.
	 */
	public Integer[] getRankedPages(int K)
	{
		
		Stream<Entry<Integer, Double>> Stem = PageRank.entrySet().stream();
		Map<Integer, Double> PageRankSorted = Stem.sorted(Map.Entry.comparingByValue(Collections.reverseOrder())).
				limit(K).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,(e1, e2) -> e1,LinkedHashMap::new));
//		this PageRankSorted is the map that store the ID of that page and the score of that page
		int pisition =0; //set the position equal 0
		Integer[] PageTop = new Integer[PageRankSorted.size()];
//		create the array of PageTop to store the ranking of page
		for(Integer page: PageRankSorted.keySet()) 
		{
				int index = pisition++;
				//set index or position that add the which index to add
				PageTop[index] = page; 
				//set the position and set the value
		}	
		return PageTop; // it will return the rank of all page first page mean that has the highest score
//		if k == 100 it will rank 100 documents
	}
	
	public static void main(String args[])
	{
	long startTime = System.currentTimeMillis();
	PageRanker pageRanker =  new PageRanker();
	pageRanker.loadData("citeseer.dat");
//	pageRanker.loadData("test.dat");
	pageRanker.initialize();
	pageRanker.runPageRank("perplexity.out", "pr_scores.out");
	Integer[] rankedPages = pageRanker.getRankedPages(100);
double estimatedTime = (double)(System.currentTimeMillis() - startTime)/1000.0;
	
	System.out.println("Top 100 Pages are:\n"+Arrays.toString(rankedPages));
	System.out.println("Proccessing timeaom: "+estimatedTime+" seconds");
	}
}
