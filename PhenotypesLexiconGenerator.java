/** 
  * Java program to compile phenotypes from HPO, PheWAS and OMIM
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
import java.util.HashMap;
import java.util.LinkedHashMap;

public class PhenotypesLexiconGenerator {

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();

		String line="", phenotypeID="";
		long customizedDiseaseID=00000000;

		String arg1 = args[0]; //input_file -- HPO phenotypes
		String arg2 = args[1]; //input_file -- PheWAS phenotypes
		String arg3 = args[2]; //input_file -- OMIM phenotypes
		String arg4 = args[3]; //output_file
		
		ArrayList<String> hpoDisease = new ArrayList<String>();
		ArrayList<String> phewasDisease = new ArrayList<String>();
		ArrayList<String> omimDisease = new ArrayList<String>();
		
		HashMap<String, String> hpoDiseaseAndID = new LinkedHashMap<String, String>();
		HashMap<String, String> phewasDiseaseAndID = new LinkedHashMap<String, String>();
		HashMap<String, String> omimDiseaseAndID = new LinkedHashMap<String, String>();
		
		ArrayList<String> phewasIDs = new ArrayList<String>();
		ArrayList<String> omimIDs = new ArrayList<String>();
		
		try {
			//HPO phenotypes
			FileInputStream fis = new FileInputStream(arg1);
			InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
		    BufferedReader br = new BufferedReader(isr);
		    while((line = br.readLine()) != null) {
		    	if(line.startsWith("phenotype/synonym")) continue;
		    	
				String[] arrLine = line.split("\t");
				String d = arrLine[0].toLowerCase();
				if(d.equals("all")) continue;
				if(d.equals("phenotype/synonym")) continue;
				
				hpoDisease.add(d);
				hpoDiseaseAndID.put(d, arrLine[1]);
			}
		    
		    //PheWAS phenotypes
			FileInputStream fis0 = new FileInputStream(arg2);
			InputStreamReader isr0 = new InputStreamReader(fis0,"UTF-8");
		    BufferedReader br0 = new BufferedReader(isr0);
		    while((line = br0.readLine()) != null) {
		    	if(line.startsWith("Phecode")) continue;
		    	
				String[] arrLine = line.split("\t");
				String d = arrLine[1].toLowerCase();
				
				phewasDisease.add(d);
				phewasDiseaseAndID.put(d, arrLine[0]);
			}
		    
		    //OMIM phenotypes
			FileInputStream fis1 = new FileInputStream(arg3);
			InputStreamReader isr1 = new InputStreamReader(fis1,"UTF-8");
		    BufferedReader br1 = new BufferedReader(isr1);
		    while((line = br1.readLine()) != null) {
		    	if(line.startsWith("Phenotypic Series Title")) continue;
		    	
				String[] arrLine = line.split("\t");
				String d = arrLine[0].toLowerCase();
				
				omimDisease.add(d);
				omimDiseaseAndID.put(d, arrLine[1]);
			}
		    
		    //combined output to file
		    FileOutputStream fos = new FileOutputStream(arg4);
		    OutputStreamWriter osr = new OutputStreamWriter(fos, "UTF-8");
		    BufferedWriter bw = new BufferedWriter(osr);
		    
		    //HPO -> PheWAS -> OMIM
		    String prehpoID="";
		    bw.append("phenotype/synonym\tcustomized_id\tHPO_ID\tPheCode\tOMIM_ID");
		    bw.append("\n");
		    for(String eachDisease : hpoDisease) {
		    	//get HPO ID
		    	String hpoID = hpoDiseaseAndID.get(eachDisease);
		    	
		    	//get PheWAS ID, if present
		    	String phewasID = phewasDiseaseAndID.get(eachDisease);
		    	if(phewasID == null) phewasID=""; 
		    	else phewasIDs.add(phewasID);
		    	
		    	//get OMIM ID, if present
		    	String omimID = omimDiseaseAndID.get(eachDisease);
		    	if(omimID == null) omimID=""; 
		    	else omimIDs.add(omimID);
		    	
		    	//combine IDs
		    	String allIDs = "";
		    	if(!phewasID.isEmpty()) {
		    		allIDs = hpoID + "\t" + phewasID;
		    		if(!omimID.isEmpty()) allIDs = allIDs + "\t" + omimID;
		    		else allIDs = allIDs + "\t-"; //added now
		    	}
		    	else {
		    		if(!omimID.isEmpty()) allIDs = hpoID + "\t-\t" + omimID;
		    		else allIDs = hpoID + "\t-\t-"; //added now
		    	}
		    	
		    	if(allIDs.isEmpty()) allIDs = hpoID + "\t-\t-";
		    	
		    	//automatically generate customized disease ID
		    	if(prehpoID.isEmpty() || !hpoID.equals(prehpoID)) {
		    		customizedDiseaseID = customizedDiseaseID + 1;
		    		phenotypeID = "Phe".concat(String.format("%08d", customizedDiseaseID));
		    	}
		    	
		    	//write to file
		    	if(!eachDisease.isEmpty()) {
		    		bw.append(eachDisease + "\t" + phenotypeID + "\t" + allIDs);
		    		bw.append("\n");
		    	}
		    	
		    	if(prehpoID.isEmpty()) prehpoID = hpoID;
		    	else if(!hpoID.equals(prehpoID)) prehpoID = hpoID;
		    }
		    
		    //PheWAS -> OMIM
		    for(String eachDisease : phewasDisease) {
		    	//get PheWAS ID
		    	String phewasID = phewasDiseaseAndID.get(eachDisease);
		    	if(phewasIDs.contains(phewasID)) continue;
		    	
		    	//get OMIM ID, if present
		    	String omimID = omimDiseaseAndID.get(eachDisease);
		    	if(omimID == null) omimID=""; 
		    	
		    	//combine IDs
		    	String allIDs = "";
		    	if(!omimID.isEmpty()) {
		    		if(!omimIDs.contains(omimID)) {
		    			allIDs = "-\t" + phewasID + "\t" + omimID;
		    			omimIDs.add(omimID);
		    		}
		    		//else allIDs = phewasID; -- This shows why PheWAS code could not map to HPO code
		    	}
		    	else allIDs = "-\t" + phewasID + "\t-";
		    	
		    	//automatically generate customized disease ID
		    	customizedDiseaseID = customizedDiseaseID + 1;
		    	phenotypeID = "Phe".concat(String.format("%08d", customizedDiseaseID));
		    	
		    	//write to file
		    	if(!eachDisease.isEmpty()) {
		    		bw.append(eachDisease + "\t" + phenotypeID + "\t" + allIDs);
		    		bw.append("\n");
		    	}
		    }
		    
		    //OMIM
		    for(String eachDisease : omimDisease) {
		    	//get OMIM ID
		    	String omimID = omimDiseaseAndID.get(eachDisease);
		    	if(omimIDs.contains(omimID)) continue;
		    	
		    	//automatically generate customized disease ID
		    	customizedDiseaseID = customizedDiseaseID + 1;
		    	phenotypeID = "Phe".concat(String.format("%08d", customizedDiseaseID));
	    		
	    		//combine IDs
	    		String allIDs = "";
	    		allIDs = "-\t-\t" + omimID;
	    		
		    	//write to file
	    		if(!eachDisease.isEmpty()) {
	    			bw.append(eachDisease + "\t" + phenotypeID + "\t" + allIDs);
	    			bw.append("\n");
	    		}
		    }
		       
			br.close();
			br0.close();
			br1.close();
			bw.close();
		} catch(IOException e) {
			System.err.println(e);
		}

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println("Execution time in milliseconds: " + elapsedTime);
	}
	
}
