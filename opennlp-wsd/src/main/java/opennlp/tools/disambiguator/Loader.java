package opennlp.tools.disambiguator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.dictionary.Dictionary;
import net.sf.extjwnl.dictionary.MorphologicalProcessor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.lemmatizer.SimpleLemmatizer;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

public class Loader {

	private static String modelsDir = "src\\test\\resources\\opennlp\\tools\\disambiguator\\";
	
	private static SentenceDetectorME sdetector;
	private static Tokenizer tokenizer;
	private static POSTaggerME tagger;
	private static NameFinderME nameFinder;
	private static SimpleLemmatizer lemmatizer;

	private static Dictionary dictionary;
	private static MorphologicalProcessor morph;
	private static boolean IsInitialized = false;  

	// local caches for faster lookup
	private static HashMap<String,Object> stemCache;
	private static HashMap<String,Object> stopCache;
	private static HashMap<String,Object> relvCache;
	
	

	// Constructor
	public Loader(){
		super();
		load();
	}

	public static HashMap<String,Object> getRelvCache(){
		if (relvCache==null || relvCache.keySet().isEmpty()){
			relvCache = new HashMap<String, Object>();
			for (String t : Constants.relevantPOS){
				relvCache.put(t, null);
			}
		}
		return relvCache;
	}
	
	public static HashMap<String,Object> getStopCache(){
		if (stopCache==null || stopCache.keySet().isEmpty()){
			stopCache = new HashMap<String, Object>();
			for (String s : Constants.stopWords){
				stopCache.put(s, null);
			}
		}
		return stopCache;
	}
	
	public static HashMap<String,Object> getStemCache(){
		if (stemCache==null || stemCache.keySet().isEmpty()){
			stemCache = new HashMap<String,Object>();
			for (Object pos : POS.getAllPOS()){
				stemCache.put(((POS)pos).getKey(),new HashMap());
			}
		}
		return stemCache;
	}
	
	public static MorphologicalProcessor getMorph(){
		if (morph==null){
			morph 		= dictionary.getMorphologicalProcessor();
		}
		return morph;
	}

	public static Dictionary getDictionary(){
		if (dictionary==null){
			try {
				dictionary 	= Dictionary.getDefaultResourceInstance();
			} catch (JWNLException e) {
				e.printStackTrace();
			}
		}
		return dictionary;
	}

	public static SimpleLemmatizer getLemmatizer(){
		if (lemmatizer==null){
			try {
				lemmatizer = new SimpleLemmatizer (new FileInputStream(modelsDir + "en-lemmatizer.dict"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return lemmatizer;
	}

	public static NameFinderME getNameFinder(){
		if (nameFinder==null){
			TokenNameFinderModel nameFinderModel;
			try {
				nameFinderModel = new TokenNameFinderModel(new FileInputStream(modelsDir + "en-ner-person.bin"));
				nameFinder = new NameFinderME(nameFinderModel);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return nameFinder;
	}

	public static POSTaggerME getTagger(){
		if (tagger==null){
			POSModel posTaggerModel = new POSModelLoader().load(new File(modelsDir + "en-pos-maxent.bin"));
			tagger = new POSTaggerME(posTaggerModel);
		}
		return tagger;
	}

	public static SentenceDetectorME getSDetector(){
		if (sdetector==null){
			try {
				SentenceModel enSentModel = new SentenceModel(new FileInputStream(modelsDir + "en-sent.bin"));
				sdetector = new SentenceDetectorME(enSentModel);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sdetector;
	}

	public static Tokenizer getTokenizer(){
		if (tokenizer == null){
			try {
				TokenizerModel  tokenizerModel = new TokenizerModel(new FileInputStream(modelsDir + "en-token.bin"));
				tokenizer = new TokenizerME(tokenizerModel);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return tokenizer;
	}

	public static boolean isInitialized(){
		return (dictionary !=null
				&& morph		!=null 
				&& stemCache	!=null 
				&& stopCache	!=null
				&& relvCache 	!=null);
	}
	
	public void load(){
		try {
			SentenceModel enSentModel = new SentenceModel(new FileInputStream(modelsDir + "en-sent.bin"));
			sdetector = new SentenceDetectorME(enSentModel);

			TokenizerModel TokenizerModel = new TokenizerModel(new FileInputStream(modelsDir + "en-token.bin"));
			tokenizer = new TokenizerME(TokenizerModel);


			POSModel posTaggerModel = new POSModelLoader().load(new File(modelsDir + "en-pos-maxent.bin"));
			tagger = new POSTaggerME(posTaggerModel);

			TokenNameFinderModel nameFinderModel = new TokenNameFinderModel(new FileInputStream(modelsDir + "en-ner-person.bin"));
			nameFinder = new NameFinderME(nameFinderModel);

			lemmatizer = new SimpleLemmatizer (new FileInputStream(modelsDir + "en-lemmatizer.dict"));

			dictionary 	= Dictionary.getDefaultResourceInstance();
			morph 		= dictionary.getMorphologicalProcessor();

			// loading lookup caches 
			stemCache = new HashMap();
			for (Object pos : POS.getAllPOS()){
				stemCache.put(((POS)pos).getKey(),new HashMap());
			}

			stopCache = new HashMap<String, Object>();
			for (String s : Constants.stopWords){
				stopCache.put(s, null);
			}
			relvCache = new HashMap<String, Object>();
			for (String t : Constants.relevantPOS){
				relvCache.put(t, null);
			}


			if (isInitialized()){
				Constants.print("loading was succesfull");
			}else{
				Constants.print("loading was unsuccesfull");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JWNLException e) {
			e.printStackTrace();
		}
	}

	public static void unload ()
	{ 
		dictionary.close();
	}



}
