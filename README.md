# **Phenotypes and synonyms lexicon**  

**Introduction:**    
This phenotypes and symptoms lexicon project is for compiling the lexicon from Human Phenotypes Ontology (HPO), Phenome Wide Association Studies (PheWAS), and Online Mendelian Inheritance in Man (OMIM). Among the three resources, only HPO includes synonyms for phenotypes. However, several synonyms are still missing in HPO. We used our diseases lexicon to add missing synonyms to phenotypes and symptoms. Our diseases lexicon is compiled from UMLS Metathesaurus and SNOMED CT. Please see the github project https://github.com/stewart-lab/Diseases_lexicon for details.
  
We derived new phenotype terms by removing prefix and suffix related to medical terms. Our approach obtained erroneous terms that are not phenotypes (ex. dysplastic kidney to plastic kidney). We used KinderMiner 2.0, our recently developed text mining tool to remove the erroneous terms that are not present in PubMed abstracts. Please see the github project https://github.com/stewart-lab/KinderMiner_2 for details on KinderMiner 2.0.  

**Prerequisites:**  
Downloading files from HPO, PheWAS and OMIM is free. However, downloading and installing UMLS Metathesaurus and SNOMED CT require license (https://www.nlm.nih.gov/research/umls/knowledge_sources/metathesaurus/).   

---- RUN IN AN IDE ----    

The entire project should be pulled into Java IDE, such as eclipse.   

---- COMPILE AND RUN ON THE COMMAND LINE ----   

Processing includes Java programs, Python scripts, and manual curation. Please see https://github.com/stewart-lab/Phenotypes_and_symptoms_lexicon/tree/master/Documentation/Phenotypes_and_symptoms_lexicon.docx.   

Java version used for development: JavaSE-1.8   
Author: Kalpana Raja   

Affiliation: Morgridge Institute for Research, Madison, WI, USA   
