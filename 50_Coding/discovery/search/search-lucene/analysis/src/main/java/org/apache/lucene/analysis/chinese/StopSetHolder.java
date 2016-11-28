package org.apache.lucene.analysis.chinese;

import org.apache.lucene.analysis.chinese.util.PiceneAssert;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.Version;

import java.io.*;
import java.util.Collections;
import java.util.Set;

public class StopSetHolder {
	private static final String DEFAULT_STOPWORD_FILE = "stopwords.txt";
	private static final String STOPWORD_FILE_COMMENT = "//";
	private static StopSetHolder stopSetHolder;
	private Set<?> DEFAULT_STOP_SET;
	
	private StopSetHolder(){
		super();
		this.init();
	}	
	
	public Set<?> getDEFAULT_STOP_SET() {
		return DEFAULT_STOP_SET;
	}

	public synchronized static StopSetHolder getInstance() {
		if(stopSetHolder == null) {
			stopSetHolder = new StopSetHolder();
		}
		
		return stopSetHolder;
	}
	
	private void init() {
		try {
			DEFAULT_STOP_SET = loadDefaultStopWordSet();
		} catch (IOException ex) {
			// default set should always be present as it is part of the
			// distribution (JAR)
			throw new RuntimeException(
					"Unable to load default stopword set");
		}
	}


	private Set<?> loadDefaultStopWordSet() throws IOException {
		InputStream stream = this.getClass().getClassLoader()
				.getResourceAsStream(DEFAULT_STOPWORD_FILE);
		
		if (stream == null) {
			File file = this.getFileFromDataHome();
			if (file != null) {
				stream = new FileInputStream(file);
			}
		}
		
		PiceneAssert.notNull(stream, "The " + this.DEFAULT_STOPWORD_FILE + " file does not exist!");
		
		
		InputStreamReader reader = new InputStreamReader(stream,
		"UTF-8");
		try {			
			// make sure it is unmodifiable as we expose it in the outer
			// class
			
			Set<?> words  = Collections.unmodifiableSet(WordlistLoader.getWordSet(
                    reader, STOPWORD_FILE_COMMENT, Version.LUCENE_CURRENT));
			
			return words;
		} finally {
			try {
				if(reader != null){
					reader.close();
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			try {
				if(stream != null) {
					stream.close();
				}				
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private File getFileFromDataHome() {
		String home = System.getProperty("user.dir") + File.separator;
		String filePath = home + "data" + File.separator + "mem" + File.separator + this.DEFAULT_STOPWORD_FILE;
		File file = new File(filePath);
		return file;
	}
}
