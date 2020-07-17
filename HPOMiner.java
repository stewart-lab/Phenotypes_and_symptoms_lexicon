/** 
  * Java program to process HPO file to get phenotypes
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

public class HPOMiner {
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();

		String line="", id="", phenotype="", synonym="", treeTerm="", umls="", mesh="", snomed="";
		int count=0;
		
		ArrayList<String> synonyms = new ArrayList<String>();
		ArrayList<String> tree = new ArrayList<String>();

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
				if(line.startsWith("id: ")) id = line.substring(line.indexOf(": ")+2).trim();
				
				//phenotype
				if(line.startsWith("name: ")) phenotype = line.substring(line.indexOf(": ")+2).trim();
				
				//synonyms
				if(line.startsWith("synonym: ")) {
					synonym = line.substring(line.indexOf(": \"")+3, line.lastIndexOf("\"")).trim();
					synonyms.add(synonym); 
				}
				
				//tree terms
				if(line.startsWith("is_a: ")) {
					treeTerm = line.substring(line.indexOf(": ")+2).trim();
					treeTerm = treeTerm.replaceAll(" ! ", "|");
					tree.add(treeTerm); 
				}
				
				//UMLS ref
				if(line.startsWith("xref: UMLS:")) umls = line.substring(line.lastIndexOf(":")+1);
				
				//MeSH ref
				if(line.startsWith("xref: MSH:")) mesh = line.substring(line.lastIndexOf(":")+1);
				
				//SNOMED CT ref
				if(line.startsWith("xref: SNOMEDCT_US:")) snomed = line.substring(line.lastIndexOf(":")+1);
				
				//write the record
				if(line.length()<=1) { //if(line.startsWith("\\[Term\\]")) {
					if(id.isEmpty()) continue;
					
					//collect synonyms
					String allSynonyms="";
					if(synonyms.size()>1) {
						for(String eachSynonym : synonyms) {
							if(allSynonyms.isEmpty()) allSynonyms = eachSynonym;
							else allSynonyms = allSynonyms + "###" + eachSynonym;
						}
					}
					else if(synonyms.size()==1) {
						allSynonyms = synonyms.get(0);
					}
					
					//collect tree terms
					String allTreeTerms="";
					if(tree.size()>1) {
						for(String eachTree : tree) {
							if(allTreeTerms.isEmpty()) allTreeTerms = eachTree;
							else allTreeTerms = allTreeTerms + "###" + eachTree;
						}
					}
					else if(tree.size()==1) {
						allTreeTerms = tree.get(0);
					}
					
					bw.append(id + "\t" + phenotype + "\t" + allSynonyms + "\t" + allTreeTerms + "\t" + umls + "\t" + mesh + "\t" + snomed);
					bw.append("\n");
					
					//clear
                    id="";
                    phenotype="";
                    synonyms.clear();
                    tree.clear();
                    umls="";
				}

				count++;
				if(count==100) break;
				//if(count%1000==0) System.out.println(count);
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
