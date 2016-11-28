package org.apache.lucene.analysis.chinese.synonym;

import org.apache.lucene.analysis.chinese.util.PiceneAssert;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MemSynonymEngine implements SynonymEngine {
	private static final String SYNONYM_FILE_NAME = "synonym.mem";
	// private Object MUTEX = new Object();
	private static MemSynonymEngine engine = null;
	private Map<String, Set<String>> synonym = new HashMap<String, Set<String>>();
	protected ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock();

	private MemSynonymEngine() {
		super();
	}

	public synchronized static SynonymEngine getInstance() {
		if (engine == null) {
			engine = new MemSynonymEngine();
			engine.init();
		}

		return engine;
	}
	
	public int getSize(){
		return this.synonym.size();
	}
	
	private void init() {
		this.rwlock.writeLock().lock();
		try {
			InputStream input = this.getClass().getClassLoader()
					.getResourceAsStream(this.SYNONYM_FILE_NAME);

			if (input == null) {
				File file = this.getFileFromDataHome();
				if (file != null) {
					input = new FileInputStream(file);
				}
			}

			PiceneAssert.notNull(input, "The " + this.SYNONYM_FILE_NAME
                    + " file does not exist!");

			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(input);
				this.synonym = (Map<String, Set<String>>) ois.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (ois != null) {
					ois.close();
				}

				if (input != null) {
					input.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.rwlock.writeLock().unlock();
		}
	}

	private File getFileFromDataHome() {
		String home = System.getProperty("user.dir") + File.separator;
		String filePath = home + "data" + File.separator + "mem"
				+ File.separator + this.SYNONYM_FILE_NAME;
		File file = new File(filePath);
		return file;
	}

	public boolean add(String word, String syn) {
		word = word.toLowerCase();
		syn = syn.toLowerCase();
		
		this.rwlock.writeLock().lock();

		try {
			Set<String> list = this.synonym.get(word);
			if (list != null) {
				list.add(syn);
			} else {
				list = new HashSet<String>();
				list.add(syn);
				this.synonym.put(word, list);
			}

			Set<String> synlist = this.synonym.get(syn);
			if (synlist != null) {
				synlist.add(word);
			} else {
				synlist = new HashSet<String>();
				synlist.add(word);
				this.synonym.put(syn, synlist);
			}
			return true;
		} finally {
			this.rwlock.writeLock().unlock();
		}

	}
	
	public boolean replaceBy(Map<String, Set<String>> syn) {
		this.rwlock.writeLock().lock();
		
		try{
			if(syn != null) {
				this.synonym = syn;
			}else {
				return false;
			}
			
			return true;
		}finally{
			this.rwlock.writeLock().unlock();
		}
	}
	
	public boolean flush() {

		this.rwlock.writeLock().lock();

		try {
			URL url = this.getClass().getClassLoader()
					.getResource(this.SYNONYM_FILE_NAME);
			File file = null;
			if (url != null) {
				file = new File(url.getFile());
			} else {
				file = this.getFileFromDataHome();
			}

			PiceneAssert.notNull(file, "The " + this.SYNONYM_FILE_NAME
					+ " file does not exist!");

			try {
				OutputStream os = new FileOutputStream(file);
				ObjectOutputStream output = new ObjectOutputStream(os);

				try {
					output.writeObject(this.synonym);
					output.flush();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (os != null) {
							os.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						if (output != null) {
							output.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return false;
		} finally {
			this.rwlock.writeLock().unlock();
		}

	}

	@Override
	public String[] getSynonyms(String s) throws IOException {
		this.rwlock.readLock().lock();
		try {
			String ss[] = new String[0];
			Set<String> syn = this.synonym.get(s);
			if (syn != null) {
				return syn.toArray(ss);
			} else {
				return null;
			}
		} finally{
			this.rwlock.readLock().unlock();
		}
	}
}
