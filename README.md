#**Phenotypes and synonyms lexicon** 

**Resources:** 
•	Human Phenotype Ontology (HPO)  
•	Phenotype-Wide Association Studies (PheWAS)  
•	Online Mendelian Inheritance in Man (OMIM) database  
  
We used our diseases lexicon to add missing synonyms and to identify the duplicates across the resources. Please see diseases lexicon project for details. We compiled the diseases lexicon from two resources.   
•	Unified Medical Language System (UMLS) Metathesaurus   
•	Systematized Nomenclature of Medicine -- Clinical Terms (SNOMED - CT)  
   
   
**Human Phenotype Ontology (HPO):**   
URL: http://human-phenotype-ontology.github.io/  
Downloaded file: hp.obo  
  
We developed a Java program to extract the phenotypes from hp.obo.  
$ javac HPOMiner.java  
$ java HPOMiner hp.obo HPO_OUTPUT  
  
We developed a Java program to extract the resources information for every phenotype.  
$ javac HPOMappingResources.java  
$ java HPOMappingResources hp.obo HPO_RESOURCES_OUTPUT  
  
We developed a Java program to get list of phenotypes and synonyms.   
$ javac PhenotypeLister.java  
$ java PhenotypeLister HPO_OUTPUT HPO_PHENOTYPES_AND_SYNONYMS  
   
   
**Phenome-wide association studies (PheWAS) for ICD-9 codes:**   
URL: https://medschool.vanderbilt.edu/cpm/center-precision-medicine-blog/icd-9-phewas-code-map-version-12  
Version: PheWAS Code version 1.2, published in 2015.     
Downloaded file: phecode_icd9_rolled.csv  
   
To download the file, click on “Export All” button to download the entire table. The file is saved as .csv.  
   
To convert the file to ‘Tab delimited Text (.txt)’ use some online tools. We used onlinecsvtools (https://onlinecsvtools.com/convert-csv-to-tsv).  
   
To group ICD 9 codes based on Phe codes:  
$ javac PheWASCodesAndICD9CodesGrouper.java  
$ java PheWASCodesAndICD9CodesGrouper phecode_icd9_rolled.txt PHEWAS_OUTPUT  
   
   
**Online Mendelian Inheritance in Man (OMIM) database:**     
URL: https://www.omim.org/phenotypicSeriesTitle/all  
Downloaded file: Phenotypic-Series-Titles-all.txt   
  
  
Phenotypes Lexicon from HPO, PheWAS and OMIM:   
$ javac PhenotypesLexiconGenerator.java  
$ java PhenotypesLexiconGenerator ~/HPO_PHENOTYPES_AND_SYNONYMS ~/PHEWAS_OUTPUT ~/Phenotypic-Series-Titles-all.txt PHENOTYPES_LEXICON_FROM_HPO_PHEWAS_OMIM  
   
   
**Resources for prefixes and suffixes for medical terms:**   
Reference: https://en.wikipedia.org/wiki/List_of_medical_roots,_suffixes_and_prefixes   
File: prefix_suffix_for_medicalTerms.txt   
   
To remove prefixes and suffixes from phenotype names:   
We developed a Java program to remove prefix and suffix from phenotype names. Each phenotype name is matched for all 500 prefixes and suffixes. Each processed name is saved to file with customized ID suffixed with "<underscore>D". Since, these names are derived from the original phenotype name, they are not assigned with any resource ID. The customized ID helps to identify the original phenotype name and "<underscore>D" indicated that it is a derived phenotype.   
    
Removal of prefix and suffix can get entirely different phenotypes.   
$ javac PrefixSuffixRemover.java  
$ java PrefixSuffixRemover prefix_suffix_for_medicalTerms.txt STANDARD_LIST_OF_STOPWORDS PHENOTYPES_LEXICON_FROM_HPO_PHEWAS_OMIM PHENOTYPES_LEXICON_FROM_HPO_PHEWAS_OMIM_PREFIX_SUFFIX_REMOVAL_DFLAG_OUTPUT  
  
  
**KinderMiner 2.0 to identify phenotypes mentioned in PubMed abstracts:**     
All the terms derived by removing prefixes and suffixes are not phenotypes. We used KinderMiner 2.0 our recently developed text mining tool to identify phenotypes mentioned at least in one PubMed abstracts. The process removed many false positives. Let us say PHENOTYPES_LEXICON_KINDERMINER_FILTERED_OUTPUT is the output file.  
See KinderMiner project for details. 
  
  
**Diseases lexicon for assigning unique concept identifier (CUI):**  
$ javac CUIAssigner.java  
$ java CUIAssigner DISEASES_LEXICON PHENOTYPES_LEXICON_KINDERMINER_FILTERED_OUTPUT PHENOTYPES_LEXICON_KINDERMINER_FILTERED_OUTPUT_CUI_ASSIGNED  
   
   
**Diseases lexicon for retrieving the missing synonyms:**   
It is possible that phenotypes, synonyms and derived phenotypes may have more synonyms in disease lexicon compiled from UMLS Metathesaurus and SNOMED CT. We retrieved such synonyms based on CUI assigned in the previous step. Please note that the synonyms in the original list are from Human Phenotype Ontology (HPO).   
   
$ javac UMLSSynonymsRetriever.java  
$ java UMLSSynonymsRetriever PHENOTYPES_LEXICON_KINDERMINER_FILTERED_OUTPUT_CUI_ASSIGNED PHENOTYPES_LEXICON_KINDERMINER_FILTERED_OUTPUT_CUI_ASSIGNED_SYNONYMS_ADDED  
  
  
**PSEUDO CUI:**  
We manually annotated pseudo CUI for the derived phenotypes without CUI. We verified that these derived terms are actually phenotypes.     
    
Java version used for development: JavaSE-1.8  
     
Author: Kalpana Raja  
    
Affiliation: Morgridge Institute for Research, Madison, WI, USA.     
