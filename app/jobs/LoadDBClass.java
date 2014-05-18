package jobs;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import play.jobs.* ; 
@OnApplicationStart
public class LoadDBClass extends Job{
	private static Map<String,java.util.List<String> > similarWords;
	public void doJob(){
		similarWords = new HashMap<String, java.util.List<String> >() ; 
		BufferedReader inpFile  = null ;
		String line = null ; 
		try {
			inpFile = new BufferedReader(new FileReader(new File("cpruned.tsv"))) ;
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			inpFile.readLine() ;
			while ((line = inpFile.readLine()) != null){
				String[] brokenLine = line.split("\t") ; 
				String simWord = brokenLine[1];
				//System.out.println(brokenLine.length);
				if(similarWords.containsKey(brokenLine[0]) == false){
					java.util.List<String> simsim = new ArrayList<String>() ; 
					simsim.add(simWord) ;
					similarWords.put(brokenLine[0], simsim) ; 
				}
				else{
					java.util.List<String> simsim = new ArrayList<String>(similarWords.get(brokenLine[0])) ;
					similarWords.remove(brokenLine[0]) ;
					simsim.add(simWord) ; 
					similarWords.put(brokenLine[0], simsim) ;
				}
				 
			}
			System.out.println(similarWords.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace()  ;  
		}
	}
	
	public java.util.List<String> getSimWords(String word){
		return similarWords.get(word);
	}
	
}
