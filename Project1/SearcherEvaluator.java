//Name: Waris Vorathumdusadee , Apisarit Sungkornjittakupt, Sirichoke Yooyen
//Section: 3(All)
//ID: 6088128,6088181,6088232

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class SearcherEvaluator {
	private List<Document> queries = null;				//List of test queries. Each query can be treated as a Document object.
	private  Map<Integer, Set<Integer>> answers = null;	//Mapping between query ID and a set of relevant document IDs
	
	public List<Document> getQueries() {
		return queries;
	}

	public Map<Integer, Set<Integer>> getAnswers() {
		return answers;
	}

	/**
	 * Load queries into "queries"
	 * Load corresponding documents into "answers"
	 * Other initialization, depending on your design.
	 * @param corpus
	 */
	public SearcherEvaluator(String corpus)
	{
		String queryFilename = corpus+"/queries.txt";
		String answerFilename = corpus+"/relevance.txt";
		
		//load queries. Treat each query as a document. 
		this.queries = Searcher.parseDocumentFromFile(queryFilename);
		this.answers = new HashMap<Integer, Set<Integer>>();
		//load answers
		try {
			List<String> lines = FileUtils.readLines(new File(answerFilename), "UTF-8");
			for(String line: lines)
			{
				line = line.trim();
				if(line.isEmpty()) continue;
				String[] parts = line.split("\\t");
				Integer qid = Integer.parseInt(parts[0]);
				String[] docIDs = parts[1].trim().split("\\s+");
				Set<Integer> relDocIDs = new HashSet<Integer>();
				for(String docID: docIDs)
				{
					relDocIDs.add(Integer.parseInt(docID));
				}
				this.answers.put(qid, relDocIDs);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Returns an array of 3 numbers: precision, recall, F1, computed from the top *k* search results 
	 * returned from *searcher* for *query*
	 * @param query
	 * @param searcher
	 * @param k
	 * @return
	 */
	public double[] getQueryPRF(Document query, Searcher searcher, int k)
	{
		/*********************** YOUR CODE HERE *************************/
		double Array[] = new double[3];
		List<SearchResult> result = searcher.search(query.getRawText(), k);
//		List<SearchResult> resultID = result.getDocument().getId();
		Set<Integer> Query = answers.get(query.getId());
		double count =0;
//		This loop will run until the complete all of the query
		for(Integer i:Query)
		{		
	
//			This loop will find until the complete all of the query
			for(int x = 0;x<result.size();x++)
			{
				if(i==result.get(x).getDocument().getId())
				{
//					if the document ID of answer and document ID of searcher is the same will count++
//					it means that document is relevant
					count++;
					break;
				}
			}
		}
//		Find the recall by using the recall formula 
//		Query.size() must more than 0
		if(Query.size() > 0)
		{
			Array[1] = count/(double)Query.size();	
		}
//		Find the precision by using the precision formula 
//		k must more than 0
		if(k > 0)
		{
			Array[0] = count/(double)k;
		}
//		Find the F1 by using the F1 formula 
//		Recall and precision must more than 0
		if(Array[0] > 0 && Array[1] > 0)
		{
			double P = (Array[0]);
			double R = (Array[1]);
//			The F1 value come from the F1 formula
			Array[2] = (2*P*R)/(P+R);
		}	
		return Array;
		/****************************************************************/
	}
	
	/**
	 * Test all the queries in *queries*, from the top *k* search results returned by *searcher*
	 * and take the average of the precision, recall, and F1. 
	 * @param searcher
	 * @param k
	 * @return
	 */
	public double[] getAveragePRF(Searcher searcher, int k)
	{
		/*********************** YOUR CODE HERE *************************/
//		This Average array will receive the average of precision recall and F1.
		double Average[] = new double[3];
//		This PRF array will receive the value of precision recall and F1
		double PRF[];
//		This loop will until complete all of the query
//		for(int r=1;r<=50;r++)
//		{
//			
//		k=r;
		for(int i=0;i<queries.size();i++)
		{	
			PRF = getQueryPRF(queries.get(i),searcher,k);
//			set PRF from function getQueryPRF
			Average[0] = PRF[0]+ Average[0]; //find sum of precision of each query
			Average[1] = PRF[1]+ Average[1];//find sum of recall of each query
			Average[2] = PRF[2]+ Average[2];//find sum of F1 of each query
		}

		Average[0] = Average[0]/queries.size(); 
		//find Average of precision 
		Average[1] = Average[1]/queries.size();
		//find Average of recall 
		Average[2] = Average[2]/queries.size();
		//find Average of f1
//		System.out.println(r+","+Average[0]+","+Average[1]+","+Average[2]);
//		}
		return Average;
//		return the array of average
		/****************************************************************/
	}
}
