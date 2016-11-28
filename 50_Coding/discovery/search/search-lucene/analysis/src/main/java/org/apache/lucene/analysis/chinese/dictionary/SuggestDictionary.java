package org.apache.lucene.analysis.chinese.dictionary;



public class SuggestDictionary extends Dictionary {
	private static SuggestDictionary instance;
	
	private SuggestDictionary(){
		super();
	}
	
	@Override
	protected String getDictionaryFileName() {
		return "suggestDict.mem";
	}
	
	public synchronized static SuggestDictionary getInstance() {
		if (instance == null) {
			instance = new SuggestDictionary();
			try {
				instance.load();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			} 
		}
		
		return instance;
	}
}
