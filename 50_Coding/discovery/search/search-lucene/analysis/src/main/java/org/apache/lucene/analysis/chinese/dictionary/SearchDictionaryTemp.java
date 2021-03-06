package org.apache.lucene.analysis.chinese.dictionary;



public class SearchDictionaryTemp extends Dictionary {
	private static SearchDictionaryTemp instance;
	
	private SearchDictionaryTemp(){
		super();
	}
	
	@Override
	protected String getDictionaryFileName() {
		return null;
	}
	
	public synchronized static SearchDictionaryTemp getInstance() {
		if (instance == null) {
			instance = new SearchDictionaryTemp();
			try {
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			} 
		}
		
		return instance;
	}
	
	public void init() {
		this.rwlock.writeLock().lock();
		try {
			wordIndexTable = new short[PRIME_INDEX_LENGTH];
			charIndexTable = new char[PRIME_INDEX_LENGTH];
			for (int i = 0; i < PRIME_INDEX_LENGTH; i++) {
				charIndexTable[i] = 0;
				wordIndexTable[i] = -1;
			}
			wordItem_charArrayTable = new char[GB2312_CHAR_NUM][][];
			wordItem_frequencyTable = new int[GB2312_CHAR_NUM][];
		} finally {
			this.rwlock.writeLock().unlock();
		}	
	}

	@Override
	public void flush() {
		
	}

	@Override
	public void load() throws Exception {
		
	}
	
	
}
