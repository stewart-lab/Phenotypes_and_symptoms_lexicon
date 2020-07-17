/** 
  * Java program to generate a list of phenotypes from HPO
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
import java.util.LinkedHashSet;
import java.util.Set;

public class PhenotypeLister {
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();

		String line="";
		int count=0;

		String arg1 = args[0]; //input_file -- hpo_phenotypes.txt
		String arg2 = args[1]; //output_file -- phenotypes_and_synonyms.txt
		
		Set<String> output = new LinkedHashSet<String>();
		
		try {
			FileInputStream fis = new FileInputStream(arg1);
			InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
		    BufferedReader br = new BufferedReader(isr);
		         
		    FileOutputStream fos = new FileOutputStream(arg2);
		    OutputStreamWriter osr = new OutputStreamWriter(fos, "UTF-8");
		    BufferedWriter bw = new BufferedWriter(osr);
		    
		    bw.append("Phenotype/Synonym\tID");
		    bw.append("\n");
			while((line = br.readLine()) != null) {
				if(line.startsWith("ID")) continue;
				
				String[] arrLine = line.split("\t");
				
				//phenotype
				output.add(arrLine[1] + "\t" + arrLine[0]);
				
				//process synonyms
				if(arrLine[2].isEmpty()) continue;
				
				if(arrLine[2].contains("###")) {
					String[] arr = arrLine[2].split("###");
					
					for(String eachArr : arr) {
						output.add(eachArr + "\t" + arrLine[0]);
					}
				}
				else {
					output.add(arrLine[2] + "\t" + arrLine[0]);
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
