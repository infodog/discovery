package org.apache.lucene.analysis.chinese.synonym;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class FileSynonymEngine implements SynonymEngine {
	private Object MUTEX = new Object();
	private static FileSynonymEngine engine = null;
	private HashMap<String, String[]> map = new HashMap<String, String[]>();

	private FileSynonymEngine() {
		super();
	}

	public synchronized static SynonymEngine getInstance() {
		if (engine == null) {
			engine = new FileSynonymEngine();
			engine.init();
		}

		return engine;
	}

	private void init() {
		synchronized (MUTEX) {
			try {
				InputStream input = this.getClass().getClassLoader().getResourceAsStream(
						"synonyms.txt");
				InputStreamReader isr = new InputStreamReader(input, "UTF-8");
				BufferedReader br = new BufferedReader(isr);
				
				try {
					String word = null;
					while ((word = br.readLine()) != null) {
						try {
							String[] synonyms = word.split("\\&");
							
							for(String s : synonyms) {	
								s.trim();
								int j=0;
								String[] temp = new String[synonyms.length-1];
								for(String t : synonyms){
									t.trim();
									if(!t.equals(s)) {
										temp[j] = t;
										j++;
									}
								}						
								this.map.put(s, temp);						
							}
						}catch(Exception e) {
							e.printStackTrace();
						}
					}
				}finally {
					try {
						if(br != null) {
							br.close();
						}					
					}catch(Exception e) {
						e.printStackTrace();
					}
					
					try {
						if(isr != null) {
							isr.close();
						}					
					}catch(Exception e){
						e.printStackTrace();
					}
					
					try {
						if(input != null) {
							input.close();
						}					
					}catch (Exception e){
						e.printStackTrace();
					}				
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
	}

	@Override
	public String[] getSynonyms(String s) throws IOException {
		synchronized (MUTEX) {
			return map.get(s);
		}
	}

}
