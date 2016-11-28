package org.apache.lucene.analysis.chinese.dictionary;



public class SearchDictionary extends Dictionary {
	private static SearchDictionary instance;
	
	private SearchDictionary(){
		super();
	}
	
	@Override
	protected String getDictionaryFileName() {
		return "coredict.mem";
	}
	
	public synchronized static SearchDictionary getInstance() {
		if (instance == null) {
			instance = new SearchDictionary();
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
