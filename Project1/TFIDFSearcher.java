//Name: Waris Vorathumdusadee , Apisarit Sungkornjittakupt, Sirichoke Yooyen
//Section: 3(All)
//ID: 6088128,6088181,6088232

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.lang.Math;

public class TFIDFSearcher extends Searcher
{	
//	HashMap of DocFrequency will contain the  word(key) 
//	and frequency that this word contain how many document(value)
	HashMap<String, Double> DocFrequency = new HashMap<>();
//	HashMap of IDFmapValue will contain the  word(key) 
//	and idf value of that  word .
	HashMap<String, Double> IDFmapValue = new HashMap<>();
//	HashMap of wordList will contain the  word(key) 
//	and termID value of that  word .
	HashMap<String,Integer> wordList = new HashMap<>();
//	HashMap of vectorMap will contain the Id of document
//	and array of TFIDF value of that document
	HashMap<Integer,HashMap> vectorMap = new HashMap<>();
	
	public TFIDFSearcher(String docFilename) {
		super(docFilename);
		/************* YOUR CODE HERE ******************/
		double tfidf;
		double termFreqency;
		int TermId = 0;
		double size = super.documents.size();

//		Find the tf or term ID of all word in all document
		
//		This loop will find the word all document
		for(int i=0;i<documents.size();i++)
		{	
			List<String> tokenInDoc = documents.get(i).getTokens();
			//Find the word in each document but must to tokenize first.
			for(int t=0;t<tokenInDoc.size();t++)
			{
				//if that word is not add it will add the new word because it is the new word. 
				if(wordList.containsKey(tokenInDoc.get(t))==false)
				{
					wordList.put(tokenInDoc.get(t), TermId);
					//put the word that find  into the map of wordlist by add term ID and word
					TermId++;
				}
			}
		}
		
//		Find DF that this term has in many document
//		This loop will find the DF which is count word that contains in how many documents.
		for(int Doc = 0;Doc<documents.size();Doc++)
		{
			Set<String> tokset = new HashSet<>(documents.get(Doc).getTokens());
			//Find the word in each document but must to tokenize first.
			for(String word: tokset)
			{	
//				if DocFrequency map that not contain this word it mean that it is the new word
				if(DocFrequency.containsKey(word)==false)
				{
					double df = 0;
					DocFrequency.put(word,df+1.0);
				}
				
//				if it find that word will update and the DF value by one
				else if(DocFrequency.containsKey(word)==true&&DocFrequency.get(word)!=0)
				{
					double newvalue = DocFrequency.get(word)+1.0;
					DocFrequency.replace(word, newvalue);
				}
					
			}
		}
		
//		Find IDF value of all term and add to the IDFmapValue
		for(String word: DocFrequency.keySet())
		{
			double valueOfIDF = Math.log10(1.0+ (size/DocFrequency.get(word)));
			IDFmapValue.put(word, valueOfIDF);
		}
//			This loop will run until all the document 
		for(int i=0;i<documents.size();i++)
		{
//			TFIDF is  the array that keep the value of tfidf of each document
			HashMap<String,Double> mapTfidf = new HashMap<String,Double>();
			List<String> tokenEachDoc = documents.get(i).getTokens();
			//set the word in each document but must to tokenize first.
			for(int z=0;z<tokenEachDoc.size();z++)
			{
//				termFreqency will find the frequency of each document that contains that word such as if contains word "Test Test" 
//				termFreqency will equal 2 
				termFreqency = Collections.frequency(tokenEachDoc, tokenEachDoc.get(z));
//				if termFreqency more than zero will find the new value by using termFreqency = 1 + Math.log10(termFreqency);
				if(termFreqency>0)
				{
					termFreqency = 1 + Math.log10(termFreqency);
				}
				if(mapTfidf.containsKey(tokenEachDoc.get(z))==false)
				{	
					tfidf = IDFmapValue.get(tokenEachDoc.get(z)) * termFreqency;
					mapTfidf.put(tokenEachDoc.get(z), tfidf);
				}
				
//				add the tfidf value to the that dictionary
			}
//			after finish each document will add the id of that document and the array of TFIDF
			vectorMap.put(documents.get(i).getId(), mapTfidf);
//			add the TFIDF value of all doc 
		}
	
		/***********************************************/
	}
	
	@Override
	public List<SearchResult> search(String queryString, int k) {
		/************* YOUR CODE HERE ******************/
		List<String> QueryList = super.tokenize(queryString);
//		List<String> QueryList get queryString that already tokenize
		List<SearchResult> result = new LinkedList<>();
		double tf = 0;
		double QueryFrequency;
//		double ValueTFIDFOfQuery[];
		int docid;
		HashMap<String, Double> ValueTFIDFOfQuery = new HashMap<>();
//		ValueTFIDFOfQuery = new double[wordList.size()];
//		this loop will do QueryList.size() time to find the tfidf of query
		for(int y=0;y<QueryList.size();y++)
		{
//			if that query word contain in the word list will do in here
			if(wordList.get(QueryList.get(y)) != null)
			{
//				find the QueryFrequency of each
				QueryFrequency = Collections.frequency(QueryList, QueryList.get(y));
				tf = 1 + Math.log10(QueryFrequency);
				double score = tf*IDFmapValue.get(QueryList.get(y));
				ValueTFIDFOfQuery.put(QueryList.get(y), score);
//				IDFmapValue it save the value of idf of all term 
			}
		}
		
//		This loop will find the cosine similarity of each document compare with the query 
		Set<String> querynew =  new HashSet<String>(super.tokenize(queryString));
//		System.out.println("size of querynew = " +querynew.size());
//		System.out.println("size of ValueTFIDFOfQuery = " +ValueTFIDFOfQuery.size());
		for(int z=0;z<documents.size();z++)
//		for(int z=0;z<1;z++)
		{
			double NormOfDoc;
			double NormOfQuery;
			double NormOfDocAndQuery;
			
			NormOfDoc = 0;
			NormOfQuery = 0;
			NormOfDocAndQuery = 0;
//			set the docid equal that the ID of each documents
			docid = documents.get(z).getId();

		
//			set the ValueTFIDFOfDoc equal the array of tfidf value of each doc
			
//			This loop will find the Normvalue to use the cosine similarity function
			Map ValueTFIDFOfDoc;//this map will receive the TFIDF of each document
			Set<String> query = new HashSet<String>(documents.get(z).getTokens()); //I  use set because I don't want to get the duplicate query
			
			ValueTFIDFOfDoc = vectorMap.get(docid);
//			this loop will find the Norm of document get by query
			for(String x: query)
			{
				NormOfDoc += Math.pow((double) ValueTFIDFOfDoc.get(x), 2);
			}
//			This loop will find the Norm of query that get by query
//			This loop will find the Norm of query and document that get by query
			for(String x:querynew)
			{		
//				if Value of TFIDF Of that Query is not match with the query will do not thing
				if(ValueTFIDFOfQuery.get(x)!=null)
					{
//					Find the NormOfQuery 
						NormOfQuery += Math.pow(ValueTFIDFOfQuery.get(x), 2);
						if(ValueTFIDFOfDoc.containsKey(x))
						{
//							Find the NormOfDocAndQuery 
							NormOfDocAndQuery += (double)ValueTFIDFOfDoc.get(x)*(double)ValueTFIDFOfQuery.get(x);
						}
					}
					
				
			}
			
//			Math.sqrt(NormOfDoc) it will find the square root of summation of NormOfDoc
			NormOfDoc = Math.sqrt(NormOfDoc);
//			Math.sqrt(NormOfQuery) it will find the square root of summation of NormOfQuery
			NormOfQuery = Math.sqrt(NormOfQuery);
			double score = 0.0;
//			it will find the score by use cosine similarity formula
			score = NormOfDocAndQuery/(NormOfDoc*NormOfQuery);
//			add the key which is the data of that document and the value which the score that get from cosine similarity formula
			result.add(new SearchResult(documents.get(z),score));
		}
		
//		Collections.sort(result) will rank the score of each document 
		Collections.sort(result);
//		and return the top 10 document
		return result.subList(0, k);	
		/***********************************************/
	}
}
