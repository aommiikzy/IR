//Waris Vorathumdusadee 6088128
//Apisarit Sungkornjittakupt 6088181
//Sirichoke Yooyen 6088232
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Index {

	// Term id -> (position in index file, doc frequency) dictionary
	private static Map<Integer, Pair<Long, Integer>> postingDict 
		= new TreeMap<Integer, Pair<Long, Integer>>();
	// Doc name -> doc id dictionary
	private static Map<String, Integer> docDict
		= new TreeMap<String, Integer>();
	// Term -> term id dictionary
	private static Map<String, Integer> termDict
		= new TreeMap<String, Integer>();
	// Block queue
	private static LinkedList<File> blockQueue
		= new LinkedList<File>();

	// Total file counter
	private static int totalFileCount = 0;
	// Document counter
	private static int docIdCounter = 0;
	// Term counter
	private static int wordIdCounter = 0;
	// Index
	private static BaseIndex index = null;
	public static int position = 1;
	private static long indexs =0;
	public static int bytes = 8;
	
	/* 
	 * Write a posting list to the given file 
	 * You should record the file position of this posting list
	 * so that you can read it back during retrieval
	 * 
	 * */
	private static void writePosting(FileChannel fc, PostingList posting)
			throws IOException {
		/*
		 * TODO: Your code here
		 *	 
		 */
		//Fuse done

		int termid = posting.getTermId();
		index.writePosting(fc,posting);
		Pair<Long,Integer> postList =new Pair<Long,Integer>(indexs,posting.getList().size());
		postingDict.put(termid, postList);
	
		indexs = indexs + bytes+((bytes/2)*posting.getList().size());
		
//		System.out.println(fc);
//		System.out.println("Test");
	}
	

	 /**
     * Pop next element if there is one, otherwise return null
     * @param iter an iterator that contains integers
     * @return next element or null
     */
    private static Integer popNextOrNull(Iterator<Integer> iter) {
        if (iter.hasNext()) {
            return iter.next();
        } else {
            return null;
        }
    }
    
	
	/**
	 * Main method to start the indexing process.
	 * @param method		:Indexing method. "Basic" by default, but extra credit will be given for those
	 * 			who can implement variable byte (VB) or Gamma index compression algorithm
	 * @param dataDirname	:relative path to the dataset root directory. E.g. "./datasets/small"
	 * @param outputDirname	:relative path to the output directory to store index. You must not assume
	 * 			that this directory exist. If it does, you must clear out the content before indexing.
	 */
    static void DeleteAllFile(File file) {
        File[] OnlyContents = file.listFiles();
        
        if (OnlyContents != null) 
        {
            for (File indexFile : OnlyContents)
            {
                if (Files.isSymbolicLink(indexFile.toPath())==false) 
                {
                	DeleteAllFile(indexFile);
                	//recursive
                }
            }
        }
        file.delete();
    }
	public static int runIndexer(String method, String dataDirname, String outputDirname) throws IOException 
	{
		
		
		/* Get index */
		String className = method + "Index";
		try {
			Class<?> indexClass = Class.forName(className);
			index = (BaseIndex) indexClass.newInstance();
		} catch (Exception e) {
			System.err
					.println("Index method must be \"Basic\", \"VB\", or \"Gamma\"");
			throw new RuntimeException(e);
		}
		
		/* Get root directory */
		
		File rootdir = new File(dataDirname);
//		System.out.println("rootdir =  "+rootdir);
		if (!rootdir.exists() || !rootdir.isDirectory()) {
			System.err.println("Invalid data directory: " + dataDirname);
			return -1;
		}
		
		   
		/* Get output directory*/
		File outdir = new File(outputDirname);
		if (outdir.exists() && !outdir.isDirectory()) {
			System.err.println("Invalid output directory: " + outputDirname);
			return -1;
		}
		
//		System.out.println("outdir = "+outdir);
//		System.out.println(outputDirname);
		
		/*	TODO: delete all the files/sub folder under outdir
		 * 
		 */
		DeleteAllFile(outdir);
		
		
		
		
		
		if (!outdir.exists()) {
			if (!outdir.mkdirs()) {
				System.err.println("Create output directory failure");
				return -1;
			}
		}
		
		
		
		
		/* BSBI indexing algorithm */
		File[] dirlist = rootdir.listFiles();

		
		/* For each block */
		for (File block : dirlist) {
			File blockFile = new File(outputDirname, block.getName());
			
			System.out.println("Running in block "+block.getName());
			blockQueue.add(blockFile);

			File blockDir = new File(dataDirname, block.getName());
			File[] filelist = blockDir.listFiles();
			/* For each file */	
			int position = 1;
			int round = 0;
			Map<Integer,PostingList> p = new TreeMap<Integer,PostingList>();
			for (File file : filelist) {
				
				++totalFileCount;
				String fileName = block.getName() + "/" + file.getName();
				
				 // use pre-increment to ensure docID > 0
                int docId = ++docIdCounter;
                docDict.put(fileName, docId);
				
				
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line;
				int checkcontainkey;
				while ((line = reader.readLine()) != null) {
					String[] tokens = line.trim().split("\\s+");
					for (String token : tokens) 
					{
						/*
						 * TODO: Your code here
						 *       For each term, build up a list of
						 *       documents in which the term occurs
						 */
						///fuse fix this 247 -272 done 
						
						if(termDict.containsKey(token)==false) {
							checkcontainkey = 0;
							if(checkcontainkey == 0) {
								wordIdCounter++;
							}
							
							
							termDict.put(token, wordIdCounter);
							
							
						}
						
						if(p.containsKey(termDict.get(token))==false) {
							checkcontainkey = 0;
							if(checkcontainkey==0) {
								PostingList nword = new PostingList(termDict.get(token)); 
								//new word add to posting
								nword.getList().add(docId);
								p.put(termDict.get(token), nword);
							}
							
						}
						else if(p.containsKey(termDict.get(token))==true){
							checkcontainkey = 1;{
								if(checkcontainkey==1) {
									if(p.get(termDict.get(token)).getList().contains(docId)==false) {
										p.get(termDict.get(token)).getList().add(docId);
									}
								}
							}
							
						}
						
					}
				}
				reader.close();
			}
			

			/* Sort and output */
			if (!blockFile.createNewFile()) {
				System.err.println("Create new block failure.");
				return -1;
			}
//			System.out.println(blockFile);
			RandomAccessFile bfc = new RandomAccessFile(blockFile, "rw");
			
			/*
			 * TODO: Your code here
			 *       Write all posting lists for all terms to file (bfc) 
			 */
	        round = 0;
			
			FileChannel fileCh = bfc.getChannel();

			
			int size = p.size();
//			System.out.println("size="+size);
			for(Integer i: p.keySet()){
				//System.out.println("check");
				
				round++;
				writePosting(fileCh, p.get(i));
			}
			bfc.close();
		
		}
	
		/* Required: output total number of files. */

		/* Merge blocks */

		while (true) {
			if (blockQueue.size() <= 1)
				break;

			File b1 = blockQueue.removeFirst();
			File b2 = blockQueue.removeFirst();
			
			File combfile = new File(outputDirname, b1.getName() + "+" + b2.getName());
			if (!combfile.createNewFile()) {
				System.err.println("Create new block failure.");
				return -1;
			}

			RandomAccessFile bf1 = new RandomAccessFile(b1, "r");
			RandomAccessFile bf2 = new RandomAccessFile(b2, "r");
			RandomAccessFile mf = new RandomAccessFile(combfile, "rw");
			/*
			 * TODO: Your code here
			 *       Combine blocks bf1 and bf2 into our combined file, mf
			 *       You will want to consider in what order to merge
			 *       the two blocks (based on term ID, perhaps?).
			 *       
			 */
//			Fuse done
			//create TreeMap to keep the data
			Map<Integer, PostingList> mp = new TreeMap<Integer,PostingList>();
			
			long pts = 0;
			PostingList p = index.readPosting(bf1.getChannel().position(pts));
			
			//if p is not null or empty will do in here 
			while(p!=null){
				
				mp.put(p.getTermId(),p);
				pts = pts + ((bytes/2)*p.getList().size())+bytes;
				p = index.readPosting(bf1.getChannel().position(pts));
			}
			pts = 0;
			
			p = index.readPosting(bf2.getChannel().position(0));
			//if p is not null or empty will do in here 
			while(p!=null) {
				if(!mp.containsKey(p.getTermId())) {	
					mp.put(p.getTermId(), p);
				}
				else if(mp.containsKey(p.getTermId())) {
					for(int i:p.getList()) {
						if(!mp.get(p.getTermId()).getList().contains(i)) {
							mp.get(p.getTermId()).getList().add(i);
						}
					}
				}
				else {
					mp.put(p.getTermId(), p);
				}
				
				pts = pts + bytes+((bytes/2)*p.getList().size());
				p = index.readPosting(bf2.getChannel().position(pts));
				
			}
			
			if(indexs != 0) {
				//if inxdex is not equal 0 set it = 0
				indexs = 0;
			}
			
			for(Integer i:mp.keySet()) {
				//use Collections to sort
				Collections.sort(mp.get(i).getList());
				//writePosting
				writePosting(mf.getChannel(),mp.get(i));
			}
			
			bf1.close();
			bf2.close();
			mf.close();
			b1.delete();
			b2.delete();
			blockQueue.add(combfile);
		}

		/* Dump constructed index back into file system */
		File indexFile = blockQueue.removeFirst();
		indexFile.renameTo(new File(outputDirname, "corpus.index"));

		BufferedWriter termWriter = new BufferedWriter(new FileWriter(new File(
				outputDirname, "term.dict")));
		for (String term : termDict.keySet()) {
			termWriter.write(term + "\t" + termDict.get(term) + "\n");
		}
		termWriter.close();

		BufferedWriter docWriter = new BufferedWriter(new FileWriter(new File(
				outputDirname, "doc.dict")));
		for (String doc : docDict.keySet()) {
			docWriter.write(doc + "\t" + docDict.get(doc) + "\n");
		}
		docWriter.close();

		BufferedWriter postWriter = new BufferedWriter(new FileWriter(new File(
				outputDirname, "posting.dict")));
//		postWriter.write("test");
		for (Integer termId : postingDict.keySet()) {
			postWriter.write(termId + "\t" + postingDict.get(termId).getFirst()
					+ "\t" + postingDict.get(termId).getSecond() + "\n");
		}
		postWriter.close();
		
		return totalFileCount;
	}

	public static void main(String[] args) throws IOException {
		/* Parse command line */
//		args = new String[3];
//		args[0] = "Basic";
//		args[1] = "./datasets/small/";
//		args[2] = "./index/small/";
//		
//		"Basic", "./datasets/large", "./index/large")
//
//		args[0] = "Basic";
//		args[1] = "./datasets/large";
//		args[2] = "./index/large";
//		System.out.println("Test = "+args[0]);
//		for(int i =0;i<args.length;i++)
//		{
//			System.out.println(args[i]);
//		}
//		System.out.println(args.length);
		if (args.length != 3) {
			System.err
					.println("Usage: java Index [Basic|VB|Gamma] data_dir output_dir");
			return;
		}

		/* Get index */
		String className = "";
		try {
			className = args[0];
		} catch (Exception e) {
			System.err
					.println("Index method must be \"Basic\", \"VB\", or \"Gamma\"");
			throw new RuntimeException(e);
		}

		/* Get root directory */
		String root = args[1];
		

		/* Get output directory */
		String output = args[2];
		runIndexer(className, root, output);
	}

}
