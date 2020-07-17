/** 
  * Java program to process HPO file to get the annotated resources
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

public class HPOMappingResources {
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();

		String line="", ref="";
		int count=0;
		
		ArrayList<String> mappingRef = new ArrayList<String>();

		String arg1 = args[0]; //input_file --hp.obo
		String arg2 = args[1]; //output_file
		
		try {
			FileInputStream fis = new FileInputStream(arg1);
			InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
		    BufferedReader br = new BufferedReader(isr);
		         
		    FileOutputStream fos = new FileOutputStream(arg2);
		    OutputStreamWriter osr = new OutputStreamWriter(fos, "UTF-8");
		    BufferedWriter bw = new BufferedWriter(osr);
		       
			while((line = br.readLine()) != null) {
				//id
				if(line.startsWith("xref: "))  {
					ref = line.substring(0, line.lastIndexOf(":"));
					if(!mappingRef.contains(ref)) mappingRef.add(ref);
				}
				
				count++;
				if(count==100) break;
				//if(count%1000==0) System.out.println(count);
			}
			
			for(String each : mappingRef) {
				bw.append(each);
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
