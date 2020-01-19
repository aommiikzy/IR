//Waris Vorathumdusadee 6088128
//Apisarit Sungkornjittakupt 6088181
//Sirichoke Yooyen 6088232
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class BasicIndex implements BaseIndex {

	@Override
	public PostingList readPosting(FileChannel fc) {
		/*
		 * TODO: Your code here
		 *       Read and return the postings list from the given file.
		 */
		PostingList NewPostList = null;
		int Size = 2*2;
		ByteBuffer buffers;
		buffers = ByteBuffer.allocate(Size*Size/2);
		try {
			
			if(fc.read(buffers)!= -1){
				buffers.flip();
				List<Integer> NewPostLists = new ArrayList<Integer>();
				int TermOfID = buffers.getInt();
				int sizeOfIndex = buffers.getInt();
				
				buffers = ByteBuffer.allocate(Size*sizeOfIndex);
				fc.read(buffers);
				buffers.flip();
				int i=0;
				
				while(i<sizeOfIndex)
					{	
					NewPostLists.add(buffers.getInt());
					i++;
					}
			
				
				NewPostList = new PostingList(TermOfID,NewPostLists);
			}
		} 
		
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return NewPostList;
	}

	@Override
	public void writePosting(FileChannel fc, PostingList p) {
		/*
		 * TODO: Your code here
		 *       Write the given postings list to the given file.
		 */
		int Size = 4;
		List<Integer> Alllists;
		Alllists = p.getList();
		int BufferLength = (Size/2)+Alllists.size();
		int TermID,SizeOfList;
		TermID = p.getTermId();
		SizeOfList = Alllists.size();
		ByteBuffer buffers;
		buffers = ByteBuffer.allocate(Size*BufferLength);
		
		buffers.putInt(TermID);
		buffers.putInt(SizeOfList);
		
		for(int DocN : Alllists) 
		{
			
			buffers.putInt(DocN);
		}
		
		buffers.flip();
		try {
			fc.write(buffers);
			} 
		
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}