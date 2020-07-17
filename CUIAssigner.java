/** 
  * Java program to assign CUI from Diseases lexicon
  * Disease lexicon is compiled from UMLS Metathesaurus and SNOMED-CT
  *
  *
  * author: Kalpana Raja, Morgridge Institute for Research, WI, USA
  */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class CUIAssigner {
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();

		String line="";
		int count=0;

		String arg1 = args[0]; //input_file -- disease lexicon
		String arg2 = args[1]; //input_file -- phenotypes, synonyms and derived phenotypes filtered with KM
		String arg3 = args[2]; //output_file
		
		HashMap<String, String> diseaseAndCUI = new LinkedHashMap<String, String>();
		
		try {
			FileInputStream fis0 = new FileInputStream(arg1);
			InputStreamReader isr0 = new InputStreamReader(fis0,"UTF-8");
		    BufferedReader br0 = new BufferedReader(isr0);
		    while((line = br0.readLine()) != null) {
		    	String[] arrLine = line.split("\t");
		    	diseaseAndCUI.put(arrLine[0].toLowerCase(), arrLine[1]);
		    }
		    
		    FileInputStream fis = new FileInputStream(arg2);
			InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
		    BufferedReader br = new BufferedReader(isr);
		         
		    FileOutputStream fos = new FileOutputStream(arg3);
		    OutputStreamWriter osr = new OutputStreamWriter(fos, "UTF-8");
		    BufferedWriter bw = new BufferedWriter(osr);
		    
			while((line = br.readLine()) != null) {
				if(line.startsWith("target\t")) {
					bw.append(line + "\tCUI");
				    bw.append("\n");
				    continue;
				}
				
				String[] arrLine = line.split("\t");
				String cui = diseaseAndCUI.get(arrLine[0].toLowerCase());
				bw.append(line + "\t" + cui);
				bw.append("\n");

				count++;
				if(count==10) break;
				//if(count%1000==0) System.out.println(count);
			}
			br0.close();
			br.close();
			bw.close();
		} catch(IOException e) {
			System.err.println(e);
		}

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println("Execution time in milliseconds: " + elapsedTime);
	}

}
