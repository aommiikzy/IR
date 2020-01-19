//Waris Vorathumdusadee 6088128
//Apisarit Sungkornjittakupt 6088181
//Sirichoke Yooyen 6088232

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Query {

	// Term id -> position in index file
	private  Map<Integer, Long> posDict = new TreeMap<Integer, Long>();
	// Term id -> document frequency
	private  Map<Integer, Integer> freqDict = new TreeMap<Integer, Integer>();
	// Doc id -> doc name dictionary
	private  Map<Integer, String> docDict = new TreeMap<Integer, String>();
	// Term -> term id dictionary
	private  Map<String, Integer> termDict = new TreeMap<String, Integer>();
	// Index
	private  BaseIndex index = null;
	private  int wordcount = 0;

	//indicate whether the query service is running or not
	private boolean running = false;
	private RandomAccessFile indexFile = null;
	
	/* 
	 * Read a posting list with a given termID from the file 
	 * You should seek to the file position of this specific
	 * posting list and read it back.
	 * */
	private  PostingList readPosting(FileChannel fc, int termId)
			throws IOException {
		/*
		 * TODO: Your code here
		 */
		Long term = posDict.get(termId).longValue();
		FileChannel value = fc.position(term);
		PostingList result = index.readPosting(value);
		if(result==null) {
//			System.out.println("check Position");
			return null;
		}
		else
		{
			return result;
		}
	}
	
	
	public void runQueryService(String indexMode, String indexDirname) throws IOException
	{
		//Get the index reader
		try {
			Class<?> indexClass = Class.forName(indexMode+"Index");
			index = (BaseIndex) indexClass.newInstance();
		} catch (Exception e) {
			System.err
					.println("Index method must be \"Basic\", \"VB\", or \"Gamma\"");
			throw new RuntimeException(e);
		}
		
		//Get Index file
		File inputdir = new File(indexDirname);
		if (!inputdir.exists() || !inputdir.isDirectory()) {
			System.err.println("Invalid index directory: " + indexDirname);
			return;
		}
		
		/* Index file */
		indexFile = new RandomAccessFile(new File(indexDirname,
				"corpus.index"), "r");

		String line = null;
		/* Term dictionary */
		BufferedReader termReader = new BufferedReader(new FileReader(new File(
				indexDirname, "term.dict")));
		while ((line = termReader.readLine()) != null) {
			String[] tokens = line.split("\t");
			termDict.put(tokens[0], Integer.parseInt(tokens[1]));
		}
		termReader.close();

		/* Doc dictionary */
		BufferedReader docReader = new BufferedReader(new FileReader(new File(
				indexDirname, "doc.dict")));
		while ((line = docReader.readLine()) != null) {
			String[] tokens = line.split("\t");
			docDict.put(Integer.parseInt(tokens[1]), tokens[0]);
		}
		docReader.close();

		/* Posting dictionary */
		BufferedReader postReader = new BufferedReader(new FileReader(new File(
				indexDirname, "posting.dict")));
		while ((line = postReader.readLine()) != null) {
			String[] tokens = line.split("\t");
			posDict.put(Integer.parseInt(tokens[0]), Long.parseLong(tokens[1]));
			freqDict.put(Integer.parseInt(tokens[0]),
					Integer.parseInt(tokens[2]));
		}
		postReader.close();
		
		this.running = true;
	}
    
	public List<Integer> retrieve(String query) throws IOException
	{	if(!running) 
		{
			System.err.println("Error: Query service must be initiated");
		}
		
		/*
		 * TODO: Your code here
		 *       Perform query processing with the inverted index.
		 *       return the list of IDs of the documents that match the query
		 *      
		 */
	int TermID;
	List<List<Integer>> ResultFinal = new ArrayList<>();
	String splitQuery[] = query.split(" ");
//	spilt the query  to the new list split by space 
	for (String ShortQuery : splitQuery )
	{
		if (termDict.get(ShortQuery) != null)
		{	
			wordcount++;
//			wordcount will count the amount of output
			TermID = termDict.get(ShortQuery);
//			set add term ID of that file 
			PostingList text = readPosting(indexFile.getChannel(),TermID);
			ResultFinal.add(text.getList());
		}
	}
//	if wordcount equal zero it means that it find only one output 
//	so don't do anything. It will return the empty arraylist
	if(wordcount == 0)
	{
		return new ArrayList<Integer>();
	}
	else if (wordcount >= 1)
	{
			while(ResultFinal.size() > 1) 
		{
			List<Integer> position1 = ResultFinal.remove(0);
			List<Integer> position2 = ResultFinal.remove(0);
//			use List to keep the result valude
			List<Integer> MergeResult = new ArrayList<>();
//	keep the length
			int LenghtP1 = position1.size();
			int LenghtP2 = position2.size();
			
			int indexP1 = 0;
			int indexP2 = 0;
			int start = 0;
//			this loop will find the add the answer to output file
			while (LenghtP1 > start && LenghtP2 > start) 
			{
				int A = position1.get(indexP1) ;
				int B = position2.get(indexP2) ;
//				if A and B is the same value it will add only one value
//				After that move position to the next one 
//				and decrease the length of both because it will exceed index
				if (A == B) 
				{
					MergeResult.add(A);
					indexP1++;
					indexP2++;
//					increase the index to the next one
					LenghtP1--;
//					decrease the length
					LenghtP2--;
				}
				else if (A > B)
				{
					indexP2++;
//					increase the index to the next one
//					decrease the length
					LenghtP2--;
				}
				else if (A < B) {
					indexP1++;
//					increase the index to the next one
//					decrease the length
					LenghtP1--;
				}
				
			}
//			if it add the result that finish 
//			after that it will return the list of answer
			if (wordcount >= 1) 
			{
				ResultFinal.add(MergeResult);
			}
			
		}
//			return ResultFinal.remove(0);
	}
	if (ResultFinal.size() > 0) {
		return ResultFinal.remove(0);
	}
	
	else {
		return new ArrayList<Integer>();
	}

//		return ResultFinal.remove(0);
	
//	return null;
	
	
	
}
	
	
    String outputQueryResult(List<Integer> res) {
        /*
         * TODO: 
         * 
         * Take the list of documents ID and prepare the search results, sorted by lexicon order. 
         * 
         * E.g.
         * 	0/fine.txt
		 *	0/hello.txt
		 *	1/bye.txt
		 *	2/fine.txt
		 *	2/hello.txt
		 *
		 * If there no matched document, output:
		 * 
		 * no results found
		 * 
         * */
    	 	StringBuilder wordwrite = new StringBuilder();
//    	 	System.out.p
    	 	if(res == null||res.size() <=0 )
    	 	{
    	 		String re = "no results found";
    	 		return re;
    	 	}
    	 		
    	     if(res!=null) 
    	     {
    	    	 	StringBuilder string = new StringBuilder();
    	    	 	Set<String> Allresult = new TreeSet<String>();
    	    	
    	    	 	for(Integer i: res)
    	    	 	{
    	    	 		Allresult.add(docDict.get(i));
    	    		}
    	    	 	for(String str: Allresult)
    	    	 	{
    	    	 		string.append(str+"\n");
//    	    	 		System.out.println(str);
    	    	 		
    	    	 	}
    	    	  return string.toString();
    	     }
    	 	
         
         return wordwrite.toString();
    	

    }
	
	public static void main(String[] args) throws IOException {

		/* Parse command line */
		
		if (args.length != 2) {
			System.err.println("Usage: java Query [Basic|VB|Gamma] index_dir");
			return;
		}

		/* Get index */
		String className = null;
		try {
			className = args[0];
		} catch (Exception e) {
			System.err
					.println("Index method must be \"Basic\", \"VB\", or \"Gamma\"");
			throw new RuntimeException(e);
		}

		/* Get index directory */
		String input = args[1];
		
		Query queryService = new Query();
		queryService.runQueryService(className, input);
		
		/* Processing queries */
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		/* For each query */
		String line = null;
		while ((line = br.readLine()) != null) {
			List<Integer> hitDocs = queryService.retrieve(line);
			queryService.outputQueryResult(hitDocs);
		}
		
		br.close();
	}
	
	protected void finalize()
	{
		try {
			if(indexFile != null)indexFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

