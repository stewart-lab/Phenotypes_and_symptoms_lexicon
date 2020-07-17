/** 
  * Java program to retrieve synonyms from diseases lexicon
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
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class UMLSSynonymsRetriever {
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();

		String line="";
		int count=0;

		String arg1 = args[0]; //input_file -- disease lexicon
		String arg2 = args[1]; //input_file -- phenotypes, synonyms and derived phenotypes filtered with KM
		String arg3 = args[2]; //output_file
		
		ArrayList<String> diseaseAndCUI = new ArrayList<String>();
		Set<String> disease = new LinkedHashSet<String>();
		Set<String> output = new LinkedHashSet<String>();
		
		try {
			FileInputStream fis0 = new FileInputStream(arg1);
			InputStreamReader isr0 = new InputStreamReader(fis0,"UTF-8");
		    BufferedReader br0 = new BufferedReader(isr0);
		    while((line = br0.readLine()) != null) {
		    	diseaseAndCUI.add(line);
		    }
		    
		    FileInputStream fis = new FileInputStream(arg2);
			InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
		    BufferedReader br = new BufferedReader(isr);
		         
		    FileOutputStream fos = new FileOutputStream(arg3);
		    OutputStreamWriter osr = new OutputStreamWriter(fos, "UTF-8");
		    BufferedWriter bw = new BufferedWriter(osr);
		    
			while((line = br.readLine()) != null) {
				if(line.startsWith("target\t")) {
					String[] arrLine = line.split("\t");
					output.add(arrLine[0] + "\t" + arrLine[5] + "\t" + arrLine[6]);
				    continue;
				}
				
				String[] arrLine = line.split("\t");
				if(arrLine[6].startsWith("C")) {
					output.add(arrLine[0] + "\t" + arrLine[5] + "\t" + arrLine[6]);
					disease.add(arrLine[0]);
					String cui = arrLine[6];
					for(String eachDiseaseAndCUI : diseaseAndCUI) {
						if(eachDiseaseAndCUI.endsWith("\t"+cui)) {
							String[] arr = eachDiseaseAndCUI.split("\t");
							if(disease.contains(arr[0])) continue;
						
							output.add(arr[0] + "\t-\t" + cui);
						}
					}
				}
				else {
					output.add(arrLine[0] + "\t" + arrLine[5] + "\tNO_ID");
				}
				
				count++;
				if(count==10) break;
				//if(count%1000==0) System.out.println(count);
			}
			
			//write to file
			for(String eachOutput : output) {
				bw.append(eachOutput);
				bw.append("\n");
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
