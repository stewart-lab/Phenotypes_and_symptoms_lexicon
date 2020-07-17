/** 
  * Java program to remove prefix / suffix to get new phenotypes
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

public class PrefixSuffixRemover {
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();

		String line="";
		int count=0;

		String arg1 = args[0]; //input_file -- prefix and suffix
		String arg2 = args[1]; //input_file -- stop-words
		String arg3 = args[2]; //input_file -- phenotypes lexicon
		String arg4 = args[3]; //output_file
		
		ArrayList<String> prefixSuffix = new ArrayList<String>();
		ArrayList<String> stopwords = new ArrayList<String>();
		Set<String> output = new LinkedHashSet<String>();
	
		try {
			FileInputStream fis0 = new FileInputStream(arg1);
			InputStreamReader isr0 = new InputStreamReader(fis0,"UTF-8");
		    BufferedReader br0 = new BufferedReader(isr0);
		    while((line = br0.readLine()) != null) {
		    	String[] arrLine = line.split("\t");
		    	if(arrLine[0].contains(", ")) {
		    		String[] arr = arrLine[0].split(", ");
		    		for(String each : arr) {
		    			prefixSuffix.add(each);
		    		}
		    	}
		    	else prefixSuffix.add(arrLine[0]);
		    }
		    
		    FileInputStream fis1 = new FileInputStream(arg2);
			InputStreamReader isr1 = new InputStreamReader(fis1,"UTF-8");
		    BufferedReader br1 = new BufferedReader(isr1);
		    while((line = br1.readLine()) != null) {
		    	stopwords.add(line);
		    }
		    
		    FileInputStream fis = new FileInputStream(arg3);
			InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
		    BufferedReader br = new BufferedReader(isr);
		         
		    FileOutputStream fos = new FileOutputStream(arg4);
		    OutputStreamWriter osr = new OutputStreamWriter(fos, "UTF-8");
		    BufferedWriter bw = new BufferedWriter(osr);
		       
			while((line = br.readLine()) != null) {
				//header
				if(line.startsWith("phenotype/synonym\t")) {
					output.add(line);
					output.add("\n");
					continue;
				}
				
				//write the original record
				output.add(line);
				output.add("\n");
				
				//remove applicable prefix and suffix
				String[] arrLine = line.split("\t");
				String originalPhenotype = arrLine[0];
				String customizedID = arrLine[1];
				
				if(originalPhenotype.contains(" ")) { //multi-words phenotype
					String[] originalPhenotypeTerms = originalPhenotype.split(" ");
						
					for(String eachFix : prefixSuffix) {
						if(eachFix.charAt(0) == '-' && eachFix.charAt(eachFix.length()-1) == '-') continue;
							
						ArrayList<String> phenotypes = new ArrayList<String>();
						ArrayList<String> terms = new ArrayList<String>();
							
						//prefix or suffix removal
						for(String eachTerm : originalPhenotypeTerms) {
							if(stopwords.contains(eachTerm)) {
								terms.add(eachTerm);
								continue;
							}
								
							if(eachFix.charAt(0) != '-' && eachFix.charAt(eachFix.length()-1) == '-') {
								phenotypes = prefixRemover(eachFix, eachTerm);
							}
							else if(eachFix.charAt(0) == '-' && eachFix.charAt(eachFix.length()-1) != '-') {
								phenotypes = suffixRemover(eachFix, eachTerm);
							}
								
							if(phenotypes.size()==1) terms.add(phenotypes.get(0));
							else if(phenotypes.size()>1) {
								String p="";
								for(String eachPhenotype : phenotypes) {
									if(p.isEmpty()) p = eachPhenotype;
									else p = p + "|" + eachPhenotype;
								}
									
								terms.add(p);
							}
						}
							
						//combine terms to phenotype name
						StringBuilder sb = new StringBuilder(terms.size());
						StringBuilder sb2 = new StringBuilder(terms.size());
						for(String t : terms) {
							if(!t.contains("|")) { 
								sb.append(t + " ");
								sb2.append(t + " ");
							}
							else if(t.contains("|")) {
								String[] arrT = t.split("\\|");
								sb.append(arrT[0] + " ");
								sb2.append(arrT[1] + " ");
							}
						}
						String sbTerm = sb.toString().trim();
						String sbTerm2 = sb2.toString().trim();
						
						//write to file
						if(!sbTerm.isEmpty() && !sbTerm.equals(originalPhenotype)) {
							if(!stopwords.contains(sbTerm)) output.add(sbTerm + "\t" + customizedID + "_D\t-\t-\t-");
						}
						if(!sbTerm2.isEmpty() && !sbTerm2.equals(originalPhenotype)) {
							if(!stopwords.contains(sbTerm2)) output.add(sbTerm2 + "\t" + customizedID + "_D\t-\t-\t-");
						}
					}
				}
				else { //one-word phenotype
					for(String eachFix : prefixSuffix) {
						ArrayList<String> phenotypes = new ArrayList<String>();
						//prefix removal
						if(eachFix.charAt(0) != '-' && eachFix.charAt(eachFix.length()-1) == '-') phenotypes = prefixRemover(eachFix, originalPhenotype);
						else if(eachFix.charAt(0) == '-' && eachFix.charAt(eachFix.length()-1) != '-') phenotypes = suffixRemover(eachFix, originalPhenotype); //suffix removal
								
						for(String eachPhenotype : phenotypes) {
							if(!eachPhenotype.isEmpty() && !eachPhenotype.equals(originalPhenotype)) {
								output.add(eachPhenotype + "\t" + customizedID + "_D\t-\t-\t-");
							}
						}
					}
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
			br1.close();
			br.close();
			bw.close();
		} catch(IOException e) {
			System.err.println(e);
		}

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println("Execution time in milliseconds: " + elapsedTime);
	}
	
	public static ArrayList<String> prefixRemover(String eachFix, String originalPhenotype) {
		String phenotype="";
		ArrayList<String> phenotypes = new ArrayList<String>();
		
		eachFix = eachFix.replaceAll("-", "");
		if(eachFix.contains("(")) {
			String s = eachFix;
			
			//whole word match
			eachFix = eachFix.replaceAll("\\(", "");
			eachFix = eachFix.replaceAll("\\)", "");
			if(originalPhenotype.startsWith(eachFix)) {
				phenotype = originalPhenotype.substring(eachFix.length()-1); 
				if(!phenotype.isEmpty()) phenotypes.add(phenotype);
			}
			else if(!originalPhenotype.equals(eachFix)) phenotypes.add(originalPhenotype);
			
			//substring match
			eachFix = s.substring(0, s.indexOf("("));
			if(originalPhenotype.startsWith(eachFix)) {
				phenotype = originalPhenotype.substring(eachFix.length()); 
				if(!phenotype.isEmpty()) phenotypes.add(phenotype);
			}
			else if(!originalPhenotype.equals(eachFix) && !phenotypes.contains(originalPhenotype)) phenotypes.add(originalPhenotype);
			
		}
		else {
			if(originalPhenotype.startsWith(eachFix)) {
				phenotype = originalPhenotype.substring(eachFix.length()); 
				if(!phenotype.isEmpty()) phenotypes.add(phenotype);
			}
			else if(!originalPhenotype.equals(eachFix)) phenotypes.add(originalPhenotype);
		}
		
		return phenotypes;
	}
	
	public static ArrayList<String> suffixRemover(String eachFix, String originalPhenotype) {
		String phenotype="";
		ArrayList<String> phenotypes = new ArrayList<String>();
		
		eachFix = eachFix.replaceAll("-", "");
		if(eachFix.contains("(")) {
			String s = eachFix;
			
			//whole word match
			eachFix = eachFix.replaceAll("\\(", "");
			eachFix = eachFix.replaceAll("\\)", "");
			if(originalPhenotype.endsWith(eachFix)) {
				phenotype = originalPhenotype.substring(0, originalPhenotype.lastIndexOf(eachFix));
				if(!phenotype.isEmpty()) phenotypes.add(phenotype);
			}
			else if(!originalPhenotype.equals(eachFix)) phenotypes.add(originalPhenotype);
			
			//substring match
			eachFix = s.substring(0, s.indexOf("(")-1);
			if(originalPhenotype.endsWith(eachFix)) {
				phenotype = originalPhenotype.substring(0, originalPhenotype.lastIndexOf(eachFix));
				if(!phenotype.isEmpty()) phenotypes.add(phenotype);
			}
			else if(!originalPhenotype.equals(eachFix) && !phenotypes.contains(originalPhenotype)) phenotypes.add(originalPhenotype);
		}
		else {
			if(originalPhenotype.endsWith(eachFix)) {
				phenotype = originalPhenotype.substring(0, originalPhenotype.lastIndexOf(eachFix));
				if(!phenotype.isEmpty()) phenotypes.add(phenotype);
			}
			else if(!originalPhenotype.equals(eachFix)) phenotypes.add(originalPhenotype);
		}
		
		return phenotypes;
	}

}

/*if(originalPhenotype.contains(" ")) { //multi-words phenotype
					String[] originalPhenotypeTerms = originalPhenotype.split(" ");
					
					for(String eachFix : prefixSuffix) {
						if(eachFix.charAt(0) == '-' && eachFix.charAt(eachFix.length()-1) == '-') continue;
						
						ArrayList<String> phenotypes = new ArrayList<String>();
						ArrayList<String> terms = new ArrayList<String>();
						
						//prefix or suffix removal
						for(String eachTerm : originalPhenotypeTerms) {
							if(stopwords.contains(eachTerm)) {
								terms.add(eachTerm);
								continue;
							}
							
							if(eachFix.charAt(0) != '-' && eachFix.charAt(eachFix.length()-1) == '-') {
								phenotypes = prefixRemover(eachFix, eachTerm);
							}
							else if(eachFix.charAt(0) == '-' && eachFix.charAt(eachFix.length()-1) != '-') {
								phenotypes = suffixRemover(eachFix, eachTerm);
							}
							
							if(phenotypes.size()==1) terms.add(phenotypes.get(0));
							else if(phenotypes.size()>1) {
								String p="";
								for(String eachPhenotype : phenotypes) {
									if(p.isEmpty()) p = eachPhenotype;
									else p = p + "|" + eachPhenotype;
								}
								
								terms.add(p);
							}
						}
						
						//combine terms to phenotype name
						StringBuilder sb = new StringBuilder(terms.size());
						for(String t : terms) {
							sb.append(t + " ");
						}
						String sbTerm = sb.toString().trim();
						
						//write to file
						if(sbTerm.contains("|")) {
							String[] arrSBTerm = sbTerm.split("\\|");
							String s1 = arrSBTerm[0] + arrSBTerm[1].substring(arrSBTerm[1].indexOf(" "));
							String s2 = arrSBTerm[0].substring(arrSBTerm[0].lastIndexOf(" ")) + arrSBTerm[1];
							bw.append(s1 + "\t" + customizedID + "\t-\t-\t-");
							bw.append("\n");
							bw.append(s2 + "\t" + customizedID + "\t-\t-\t-");
							bw.append("\n");
						}
						else {
							bw.append(sbTerm + "\t" + customizedID + "\t-\t-\t-");
							bw.append("\n");
						}
					}
				}
				else { //one-word phenotype
					for(String eachFix : prefixSuffix) {
						ArrayList<String> phenotypes = new ArrayList<String>();
						//prefix removal
						if(eachFix.charAt(0) != '-' && eachFix.charAt(eachFix.length()-1) == '-') phenotypes = prefixRemover(eachFix, originalPhenotype);
						else if(eachFix.charAt(0) == '-' && eachFix.charAt(eachFix.length()-1) != '-') phenotypes = suffixRemover(eachFix, originalPhenotype); //suffix removal
							
						for(String eachPhenotype : phenotypes) {
							bw.append(eachPhenotype + "\t" + customizedID + "\t-\t-\t-");
							bw.append("\n");
						}
					}
				}*/
