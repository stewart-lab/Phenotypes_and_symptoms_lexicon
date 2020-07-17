/** 
  * Java program to process PheWAS
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

public class PheWasCodesAndICD9CodesGrouper {
	
	public static void main(String[] args) {
		String line="", phewasCode="", phenotype="", icd9code="", icd9disease="";
		int count=0;
		
		ArrayList<String> codes = new ArrayList<String>();
		ArrayList<String> phewasCodes = new ArrayList<String>();
		
		String arg1 = args[0]; //input_file
		String arg2 = args[1]; //output_file
		
		try {
			FileInputStream fis = new FileInputStream(arg1);
			InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
		    BufferedReader br = new BufferedReader(isr);
		         
		    FileOutputStream fos = new FileOutputStream(arg2);
		    OutputStreamWriter osr = new OutputStreamWriter(fos, "UTF-8");
		    BufferedWriter bw = new BufferedWriter(osr);
		    
		    bw.append("Phecode\tPhenotype\tICD9_codes\tICD9_diseases");
            bw.append("\n");
			while((line = br.readLine()) != null) {
				line = line.replaceAll("\"", "");
				if(line.startsWith("ICD9")) continue;
                String[] arrLine = line.split("\t");
                phewasCode = arrLine[2].trim();
                phenotype = arrLine[3].trim();
                icd9code = arrLine[0].trim();
                icd9disease = arrLine[1].trim();

                codes.add(phewasCode + "\t" +  phenotype + "\t" + icd9code + "\t" + icd9disease);
                if(!phewasCodes.contains(phewasCode)) phewasCodes.add(phewasCode);

				count++;
				if(count==10) break;
			}

			for(String eachPhewasCode : phewasCodes) {
				String eachPhenotype="";

                for(String eachCode : codes) {
                        if(eachCode.startsWith(eachPhewasCode+"\t")) {
                                String[] arrEachCode = eachCode.split("\t");

                                //phenotype
                                if(eachPhenotype.isEmpty()) eachPhenotype = arrEachCode[1];                    

                                //group ICD 9 codes
                                if(icd9code.isEmpty()) icd9code = arrEachCode[2];
                                else icd9code = icd9code + "|" + arrEachCode[2];

                                //group ICD 9 diseases
                                if(icd9disease.isEmpty()) icd9disease = arrEachCode[3];
                                else icd9disease = icd9disease + "|" + arrEachCode[3];
                        }
                }

                bw.append(eachPhewasCode + "\t" + eachPhenotype + "\t" + icd9code + "\t" + icd9disease);
                bw.append("\n");
                icd9code = "";
                icd9disease = "";

				count++;
				if(count==2) break;
			}
			
			br.close();
			bw.close();
		} catch(IOException e) {
			System.err.println(e);
		}
	}
	
}
