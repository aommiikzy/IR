//Name: Waris Vorathumdusadee , Apisarit Sungkornjittakupt, Sirichoke Yooyen
//Section: 3(All)
//ID: 6088128,6088181,6088232

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class JaccardSearcher extends Searcher{

	public JaccardSearcher(String docFilename) {
		super(docFilename);
		/************* YOUR CODE HERE ******************/
	
		/***********************************************/
	}

	@Override
	public List<SearchResult> search(String queryString, int k) {
		/************* YOUR CODE HERE ******************/
		/***********************************************/
//		Why we use Set because it will not add the word that has already in the set.
		Set<String> query = new HashSet<String>(Searcher.tokenize(queryString));
//		List of result will receive the document and the score of each document.
		List<SearchResult> result = new LinkedList<>();
		double score;
// 		use count to count the word in document that match the query or not
		double count=0;
		for(int i=0;i<documents.size();i++)
		{
//			use Set<String> union because it will not add the word that has already in the set.
			Set<String> union = new HashSet<String>(query);
//			use addAll to add the value
			union.addAll(documents.get(i).getTokens());
//			this loop will run until complete all the query
			for(String q:query)
			{
//				if this document contain the query will count the number that match.
				if((documents.get(i).getTokens().contains(q)))
				{
					count++;
				}
//				for(int ListofEachDoc = 0;ListofEachDoc<documents.get(i).getTokens().size();ListofEachDoc++)
//				{
//					
//					if(q.equals(documents.get(i).getTokens().get(ListofEachDoc)))
//					{
//						count++;
//						break;
//					}
//				}
			}

//			it will find the score by use jaccard similarity formula
			score = (double) (count/(double)union.size());
//			add the document and score into the list of result 
			result.add(new SearchResult(documents.get(i),score));
			count=0;

		}
//		Collections.sort(result) will rank the score of each document 
		Collections.sort(result);
//		and return the top 10 document
		return result.subList(0, k);
		/***********************************************/
	
	}

}


