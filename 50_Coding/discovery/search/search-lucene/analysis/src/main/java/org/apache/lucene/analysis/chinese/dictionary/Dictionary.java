package org.apache.lucene.analysis.chinese.dictionary;

import org.apache.lucene.analysis.chinese.util.PiceneAssert;
import org.apache.lucene.analysis.chinese.util.Utility;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Word Dictionary 
 */
public abstract class Dictionary extends AbstractDictionary {
	protected ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock(); 
	protected abstract String getDictionaryFileName();

	/**
	 * 用户搜索记录词库
	 */
	//private static WordDictionary suggestInstance;

	/**
	 * Large prime number for hash function
	 */
	public static final int PRIME_INDEX_LENGTH = 12071;

	/**
	 * wordIndexTable guarantees to hash all Chinese characters in Unicode into
	 * PRIME_INDEX_LENGTH array. There will be conflict, but in reality this
	 * program only handles the 6768 characters found in GB2312 plus some ASCII
	 * characters. Therefore in order to guarantee better precision, it is
	 * necessary to retain the original symbol in the charIndexTable.
	 */
	protected short[] wordIndexTable;

	protected char[] charIndexTable;

	/**
	 * To avoid taking too much space, the data structure needed to store the
	 * lexicon requires two multidimensional arrays to store word and frequency.
	 * Each word is placed in a char[]. Each char represents a Chinese char or
	 * other symbol. Each frequency is put into an int. These two arrays
	 * correspond to each other one-to-one. Therefore, one can use
	 * wordItem_charArrayTable[i][j] to look up word from lexicon, and
	 * wordItem_frequencyTable[i][j] to look up the corresponding frequency.
	 */
	protected char[][][] wordItem_charArrayTable;

	protected int[][] wordItem_frequencyTable;

	private void init() {
		if(wordIndexTable == null) {
			wordIndexTable = new short[PRIME_INDEX_LENGTH];
			charIndexTable = new char[PRIME_INDEX_LENGTH];
			for (int i = 0; i < PRIME_INDEX_LENGTH; i++) {
				charIndexTable[i] = 0;
				wordIndexTable[i] = -1;
			}
			wordItem_charArrayTable = new char[GB2312_CHAR_NUM][][];
			wordItem_frequencyTable = new int[GB2312_CHAR_NUM][];
		}		
	}

	public void replaceBy(Dictionary dictionary) {
		this.rwlock.writeLock().lock();
		try {
			if(dictionary != null) {
				if(dictionary.charIndexTable != null && dictionary.wordIndexTable != null &&
						dictionary.wordItem_charArrayTable!= null && dictionary.wordItem_frequencyTable != null) {
					this.charIndexTable = dictionary.charIndexTable;
					this.wordIndexTable = dictionary .wordIndexTable;
					this.wordItem_charArrayTable = dictionary.wordItem_charArrayTable;
					this.wordItem_frequencyTable = dictionary.wordItem_frequencyTable;
				}
			}
		} finally {
			this.rwlock.writeLock().unlock();
		}
	}
	
	public void flush() {
		this.rwlock.writeLock().lock();
		try {
			URL url =  this.getClass().getClassLoader().getResource(this.getDictionaryFileName());
			File file = null;
			if(url != null) {
				file = new File(url.getFile());
			} else {
				file = this.getFileFromDataHome();
			}
			
			PiceneAssert.notNull(file, "The " + this.getDictionaryFileName() + " file does not exist!");
			
			this.saveToObj(file);
		}catch(Exception e) {
			e.printStackTrace();
		} finally {
			this.rwlock.writeLock().unlock();
		}
	}
	
	/**
	 * Attempt to load dictionary from provided directory, first trying
	 * coredict.mem, failing back on coredict.dct
	 * 
	 * @param dctFileRoot
	 *            path to dictionary directory
	 */
	@Deprecated
	public void load(String dctFileRoot) {
		String dctFilePath = dctFileRoot + "/coredict.dct";
		File serialObj = new File(dctFileRoot + "/coredict.mem");

		if (serialObj.exists() && loadFromObj(serialObj)) {

		} else {
			try {
				wordIndexTable = new short[PRIME_INDEX_LENGTH];
				charIndexTable = new char[PRIME_INDEX_LENGTH];
				for (int i = 0; i < PRIME_INDEX_LENGTH; i++) {
					charIndexTable[i] = 0;
					wordIndexTable[i] = -1;
				}
				wordItem_charArrayTable = new char[GB2312_CHAR_NUM][][];
				wordItem_frequencyTable = new int[GB2312_CHAR_NUM][];
				// int total =
				loadMainDataFromFile(dctFilePath);
				expandDelimiterData();
				mergeSameWords();
				sortEachItems();
				// log.info("load dictionary: " + dctFilePath + " total:" +
				// total);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}

			saveToObj(serialObj);
		}
	}

	private File getFileFromDataHome() {
		String home = System.getProperty("user.dir") + File.separator;
		String filePath = home + "data" + File.separator + "mem" + File.separator + this.getDictionaryFileName();
		File file = new File(filePath);
		return file;
	}
	
	/**
	 * Load coredict.mem internally from the jar file.
	 * 
	 * @throws ClassNotFoundException
	 * @throws java.io.IOException
	 */
	public void load() throws Exception {
		InputStream input = this.getClass().getClassLoader().getResourceAsStream(this.getDictionaryFileName());
	
		if(input == null) {
			File file = this.getFileFromDataHome();
			if (file != null) {
				input = new FileInputStream(file);
			}
		}
		
		PiceneAssert.notNull(input, "The " + this.getDictionaryFileName() + " file does not exist!");
		
		try {
			loadFromObjectInputStream(input);
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally {
			if(input != null) {
				input.close();
			}
		}		
		
		this.init();
	}
	
	@Deprecated
	private void loadCustomDictionary() {
		try {
			InputStream input = this.getClass().getClassLoader().getResourceAsStream(
					"customDict.txt");
			InputStreamReader isr = new InputStreamReader(input, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			
			try {
				String word = null;
				while ((word = br.readLine()) != null) {
					try {
						String[] temp = word.split("\\|");

						String term = temp[0].trim().toLowerCase();
						int frequency = Integer.parseInt(temp[1].trim());					
						
						this.putIntoDictionary(term,frequency);
						
					}catch(Exception e) {
						System.out.println(word);
						e.printStackTrace();
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
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
				}catch(Exception e) {
					e.printStackTrace();
				}
				
				try {
					if(input != null) {
						input.close();
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean putIntoDictionary(String term, int frequency) {
		this.rwlock.writeLock().lock();
		try {
			try {
				char[] charArray = term.toCharArray();
				char c = charArray[0];
				
				int index =  this.getPrefixMatch(charArray);
				//如果词库中已经存在，就不需要再添加
				if(index != -1) {
					return false;
				}	
				
				int tableIndex = this.wordIndexTable[this.getAvaliableTableIndex(c)];					
				
				if(tableIndex != -1) {
					//添加新词到词库
					char[][] item = this.wordItem_charArrayTable[tableIndex]; 
					int[] freq = this.wordItem_frequencyTable[tableIndex];
					

					int l = item.length;
					char[][] newItem = new char[l + 1][];
					
					//插入的位置
					int pos = -1;
					pos = this.getPosition(item,charArray);						
					System.arraycopy(item, 0, newItem, 0, pos);
					System.arraycopy(item, pos, newItem, pos+1, item.length - pos);
					
					char[] ca = new char[charArray.length-1];						
					System.arraycopy(charArray, 1, ca, 0, charArray.length-1); 
					
					newItem[pos] = ca;
					this.wordItem_charArrayTable[tableIndex] = newItem;
					
					int fl = freq.length;
					int[] newFreq = new int[fl +1];
					newFreq[pos] = -1;						
					
					System.arraycopy(freq, 0, newFreq, 0, pos);
					System.arraycopy(freq, pos, newFreq, pos+1, freq.length - pos);
					newFreq[pos] = frequency;
					this.wordItem_frequencyTable[tableIndex] = newFreq;						
				} else {
					tableIndex = this.getGB2312Id(c);
					wordItem_charArrayTable[tableIndex] = new char[1][];
					wordItem_frequencyTable[tableIndex] = new int[1];
					
					char[] ca = new char[charArray.length-1];						
					System.arraycopy(charArray, 1, ca, 0, charArray.length-1); 
					wordItem_charArrayTable[tableIndex][0] = ca;
					wordItem_frequencyTable[tableIndex][0] = frequency;
					
					setTableIndex(c, tableIndex);
				}
				
				return true;
			}catch(Exception e) {
				e.printStackTrace();
				return false;
			}
		} finally{
			this.rwlock.writeLock().unlock();
		}
		
		
	}

	private int getPosition(char[][] items, char[] charArray) {
		int start = 0, end = items.length - 1;
		int mid = (start + end) / 2, cmpResult;

		// Binary search for the index of idArray
		while (start <= end) {
			cmpResult = Utility.compareArray(items[mid], 0, charArray, 1);

			if (cmpResult == 0)
				return mid;// find it
			else if (cmpResult < 0)
				start = mid + 1;
			else if (cmpResult > 0)
				end = mid - 1;

			mid = (start + end) / 2;
		}
		
		if(mid>= 0 && mid <= items.length) {
			cmpResult = Utility.compareArray(items[mid], 0, charArray, 1);
			
			int pos = mid - cmpResult;
			
			if(pos < 0) {
				pos = 0;
			}
			
			return pos;			
		}
		
		return -1;
	}

	private boolean loadFromObj(File serialObj) {
		try {
			InputStream input = new FileInputStream(serialObj);
			try {
				loadFromObjectInputStream(input);
			}catch(Exception e) {
				e.printStackTrace();
			} finally {
				if(input != null) {
					input.close();
				} 
			}			
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void loadFromObjectInputStream(InputStream serialObjectInputStream)
			throws Exception {
		ObjectInputStream input = null;
		
		try {
			input = new ObjectInputStream(serialObjectInputStream);
			wordIndexTable = (short[]) input.readObject();
			charIndexTable = (char[]) input.readObject();
			wordItem_charArrayTable = (char[][][]) input.readObject();
			wordItem_frequencyTable = (int[][]) input.readObject();

		} catch (Exception e) {
			if(e instanceof EOFException) {
				System.out.println(e.getMessage());
			} else {
				e.printStackTrace();
				throw e;
			}
		} finally{
			if(input != null) {
				input.close();
			}
		}
	}

	private void saveToObj(File serialObj) {
		try {			
			OutputStream os = new FileOutputStream(serialObj);
			ObjectOutputStream output = new ObjectOutputStream(os);
			
			try {
				output.writeObject(wordIndexTable);
				output.writeObject(charIndexTable);
				output.writeObject(wordItem_charArrayTable);
				output.writeObject(wordItem_frequencyTable);
			}catch (Exception e) {
				e.printStackTrace();
			}finally {				
				try {
					if(os != null) {
						os.close();
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
				
				try {
					if(output != null) {
						output.close();
					}
				}catch(Exception e) {
					e.printStackTrace();
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load the datafile into this WordDictionary
	 * 
	 * @param dctFilePath
	 *            path to word dictionary (coredict.dct)
	 * @return number of words read
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.IOException
	 * @throws java.io.UnsupportedEncodingException
	 */
	@Deprecated
	private int loadMainDataFromFile(String dctFilePath)
			throws FileNotFoundException, IOException,
			UnsupportedEncodingException {
		int i, cnt, length, total = 0;
		// The file only counted 6763 Chinese characters plus 5 reserved slots
		// 3756~3760.
		// The 3756th is used (as a header) to store information.
		int[] buffer = new int[3];
		byte[] intBuffer = new byte[4];
		String tmpword;
		RandomAccessFile dctFile = new RandomAccessFile(dctFilePath, "r");
		
		try {
			// GB2312 characters 0 - 6768
			for (i = GB2312_FIRST_CHAR; i < GB2312_FIRST_CHAR + CHAR_NUM_IN_FILE; i++) {
				// if (i == 5231)
				// System.out.println(i);

				dctFile.read(intBuffer);
				// the dictionary was developed for C, and byte order must be
				// converted to work with Java
				cnt = ByteBuffer.wrap(intBuffer).order(ByteOrder.LITTLE_ENDIAN)
						.getInt();
				if (cnt <= 0) {
					wordItem_charArrayTable[i] = null;
					wordItem_frequencyTable[i] = null;
					continue;
				}
				wordItem_charArrayTable[i] = new char[cnt][];
				wordItem_frequencyTable[i] = new int[cnt];
				total += cnt;
				int j = 0;
				while (j < cnt) {
					// wordItemTable[i][j] = new WordItem();
					dctFile.read(intBuffer);
					buffer[0] = ByteBuffer.wrap(intBuffer).order(
							ByteOrder.LITTLE_ENDIAN).getInt();// frequency
					dctFile.read(intBuffer);
					buffer[1] = ByteBuffer.wrap(intBuffer).order(
							ByteOrder.LITTLE_ENDIAN).getInt();// length
					dctFile.read(intBuffer);
					buffer[2] = ByteBuffer.wrap(intBuffer).order(
							ByteOrder.LITTLE_ENDIAN).getInt();// handle

					// wordItemTable[i][j].frequency = buffer[0];
					wordItem_frequencyTable[i][j] = buffer[0];

					length = buffer[1];
					if (length > 0) {
						byte[] lchBuffer = new byte[length];
						dctFile.read(lchBuffer);
						tmpword = new String(lchBuffer, "GB2312");
						// indexTable[i].wordItems[j].word = tmpword;
						// wordItemTable[i][j].charArray = tmpword.toCharArray();
						wordItem_charArrayTable[i][j] = tmpword.toCharArray();
					} else {
						// wordItemTable[i][j].charArray = null;
						wordItem_charArrayTable[i][j] = null;
					}
					// System.out.println(indexTable[i].wordItems[j]);
					j++;
				}

				String str = getCCByGB2312Id(i);
				setTableIndex(str.charAt(0), i);
			}
		}finally {
			if(dctFile != null) {
				dctFile.close();
			}			
		}		
		return total;
	}

	/**
	 * The original lexicon puts all information with punctuation into a chart
	 * (from 1 to 3755). Here it then gets expanded, separately being placed
	 * into the chart that has the corresponding symbol.
	 */
	@Deprecated
	private void expandDelimiterData() {
		int i;
		int cnt;
		// Punctuation then treating index 3755 as 1,
		// distribute the original punctuation corresponding dictionary into
		int delimiterIndex = 3755 + GB2312_FIRST_CHAR;
		i = 0;
		while (i < wordItem_charArrayTable[delimiterIndex].length) {
			char c = wordItem_charArrayTable[delimiterIndex][i][0];
			int j = getGB2312Id(c);// the id value of the punctuation
			if (wordItem_charArrayTable[j] == null) {

				int k = i;
				// Starting from i, count the number of the following worditem
				// symbol from j
				while (k < wordItem_charArrayTable[delimiterIndex].length
						&& wordItem_charArrayTable[delimiterIndex][k][0] == c) {
					k++;
				}
				// c is the punctuation character, j is the id value of c
				// k-1 represents the index of the last punctuation character
				cnt = k - i;
				if (cnt != 0) {
					wordItem_charArrayTable[j] = new char[cnt][];
					wordItem_frequencyTable[j] = new int[cnt];
				}

				// Assign value for each wordItem.
				for (k = 0; k < cnt; k++, i++) {
					// wordItemTable[j][k] = new WordItem();
					wordItem_frequencyTable[j][k] = wordItem_frequencyTable[delimiterIndex][i];
					wordItem_charArrayTable[j][k] = new char[wordItem_charArrayTable[delimiterIndex][i].length - 1];
					System.arraycopy(
							wordItem_charArrayTable[delimiterIndex][i], 1,
							wordItem_charArrayTable[j][k], 0,
							wordItem_charArrayTable[j][k].length);
				}
				setTableIndex(c, j);
			}
		}
		// Delete the original corresponding symbol array.
		wordItem_charArrayTable[delimiterIndex] = null;
		wordItem_frequencyTable[delimiterIndex] = null;
	}

	/*
	 * since we aren't doing POS-tagging, merge the frequencies for entries of
	 * the same word (with different POS)
	 */
	@Deprecated
	private void mergeSameWords() {
		int i;
		for (i = 0; i < GB2312_FIRST_CHAR + CHAR_NUM_IN_FILE; i++) {
			if (wordItem_charArrayTable[i] == null)
				continue;
			int len = 1;
			for (int j = 1; j < wordItem_charArrayTable[i].length; j++) {
				if (Utility.compareArray(wordItem_charArrayTable[i][j], 0,
						wordItem_charArrayTable[i][j - 1], 0) != 0)
					len++;

			}
			if (len < wordItem_charArrayTable[i].length) {
				char[][] tempArray = new char[len][];
				int[] tempFreq = new int[len];
				int k = 0;
				tempArray[0] = wordItem_charArrayTable[i][0];
				tempFreq[0] = wordItem_frequencyTable[i][0];
				for (int j = 1; j < wordItem_charArrayTable[i].length; j++) {
					if (Utility.compareArray(wordItem_charArrayTable[i][j], 0,
							tempArray[k], 0) != 0) {
						k++;
						// temp[k] = wordItemTable[i][j];
						tempArray[k] = wordItem_charArrayTable[i][j];
						tempFreq[k] = wordItem_frequencyTable[i][j];
					} else {
						// temp[k].frequency += wordItemTable[i][j].frequency;
						tempFreq[k] += wordItem_frequencyTable[i][j];
					}
				}
				// wordItemTable[i] = temp;
				wordItem_charArrayTable[i] = tempArray;
				wordItem_frequencyTable[i] = tempFreq;
			}
		}
	}
	
	@Deprecated
	private void sortEachItems() {
		char[] tmpArray;
		int tmpFreq;
		for (int i = 0; i < wordItem_charArrayTable.length; i++) {
			if (wordItem_charArrayTable[i] != null
					&& wordItem_charArrayTable[i].length > 1) {
				for (int j = 0; j < wordItem_charArrayTable[i].length - 1; j++) {
					for (int j2 = j + 1; j2 < wordItem_charArrayTable[i].length; j2++) {
						if (Utility.compareArray(wordItem_charArrayTable[i][j],
								0, wordItem_charArrayTable[i][j2], 0) > 0) {
							tmpArray = wordItem_charArrayTable[i][j];
							tmpFreq = wordItem_frequencyTable[i][j];
							wordItem_charArrayTable[i][j] = wordItem_charArrayTable[i][j2];
							wordItem_frequencyTable[i][j] = wordItem_frequencyTable[i][j2];
							wordItem_charArrayTable[i][j2] = tmpArray;
							wordItem_frequencyTable[i][j2] = tmpFreq;
						}
					}
				}
			}
		}
	}

	/*
	 * Calculate character c's position in hash table, then initialize the value
	 * of that position in the address table.
	 */
	private boolean setTableIndex(char c, int j) {
		int index = getAvaliableTableIndex(c);
		if (index != -1) {
			charIndexTable[index] = c;
			wordIndexTable[index] = (short) j;
			return true;
		} else
			return false;
	}

	private short getAvaliableTableIndex(char c) {
		int hash1 = (int) (hash1(c) % PRIME_INDEX_LENGTH);
		int hash2 = hash2(c) % PRIME_INDEX_LENGTH;
		if (hash1 < 0)
			hash1 = PRIME_INDEX_LENGTH + hash1;
		if (hash2 < 0)
			hash2 = PRIME_INDEX_LENGTH + hash2;
		int index = hash1;
		int i = 1;
		while (charIndexTable[index] != 0 && charIndexTable[index] != c
				&& i < PRIME_INDEX_LENGTH) {
			index = (hash1 + i * hash2) % PRIME_INDEX_LENGTH;
			i++;
		}
		// System.out.println(i - 1);

		if (i < PRIME_INDEX_LENGTH
				&& (charIndexTable[index] == 0 || charIndexTable[index] == c)) {
			return (short) index;
		} else
			return -1;
	}

	private short getWordItemTableIndex(char c) {
		int hash1 = (int) (hash1(c) % PRIME_INDEX_LENGTH);
		int hash2 = hash2(c) % PRIME_INDEX_LENGTH;
		if (hash1 < 0)
			hash1 = PRIME_INDEX_LENGTH + hash1;
		if (hash2 < 0)
			hash2 = PRIME_INDEX_LENGTH + hash2;
		int index = hash1;
		int i = 1;
		while (charIndexTable[index] != 0 && charIndexTable[index] != c
				&& i < PRIME_INDEX_LENGTH) {
			index = (hash1 + i * hash2) % PRIME_INDEX_LENGTH;
			i++;
		}

		if (i < PRIME_INDEX_LENGTH && charIndexTable[index] == c) {
			return (short) index;
		} else
			return -1;
	}

	/**
	 * Look up the text string corresponding with the word char array, and
	 * return the position of the word list.
	 * 
	 * @param knownHashIndex
	 *            already figure out position of the first word symbol
	 *            charArray[0] in hash table. If not calculated yet, can be
	 *            replaced with function int findInTable(char[] charArray).
	 * @param charArray
	 *            look up the char array corresponding with the word.
	 * @return word location in word array. If not found, then return -1.
	 */
	private int findInTable(short knownHashIndex, char[] charArray) {
		if (charArray == null || charArray.length == 0)
			return -1;

		char[][] items = wordItem_charArrayTable[wordIndexTable[knownHashIndex]];
		int start = 0, end = items.length - 1;
		int mid = (start + end) / 2, cmpResult;

		// Binary search for the index of idArray
		while (start <= end) {
			cmpResult = Utility.compareArray(items[mid], 0, charArray, 1);

			if (cmpResult == 0)
				return mid;// find it
			else if (cmpResult < 0)
				start = mid + 1;
			else if (cmpResult > 0)
				end = mid - 1;

			mid = (start + end) / 2;
		}
		return -1;
	}

	/**
	 * Find the first word in the dictionary that starts with the supplied
	 * prefix
	 * 
	 * @see #getPrefixMatch(char[], int)
	 * @param charArray
	 *            input prefix
	 * @return index of word, or -1 if not found
	 */
	public int getPrefixMatch(char[] charArray) {
		return getPrefixMatch(charArray, 0);
	}

	/**
	 * Find the nth word in the dictionary that starts with the supplied prefix
	 * 
	 * @see #getPrefixMatch(char[])
	 * @param charArray
	 *            input prefix
	 * @param knownStart
	 *            relative position in the dictionary to start
	 * @return index of word, or -1 if not found
	 */
	public int getPrefixMatch(char[] charArray, int knownStart) {
		this.rwlock.readLock().lock();
		try {
			short index = getWordItemTableIndex(charArray[0]);
			if (index == -1)
				return -1;
			char[][] items = wordItem_charArrayTable[wordIndexTable[index]];
			int start = knownStart, end = items.length - 1;

			int mid = (start + end) / 2, cmpResult;

			// Binary search for the index of idArray
			while (start <= end) {
				cmpResult = Utility.compareArrayByPrefix(charArray, 1, items[mid],
						0);
				if (cmpResult == 0) {
					// Get the first item which match the current word
					while (mid >= 0
							&& Utility.compareArrayByPrefix(charArray, 1,
									items[mid], 0) == 0)
						mid--;
					mid++;
					return mid;// Find the first word that uses charArray as prefix.
				} else if (cmpResult < 0)
					end = mid - 1;
				else
					start = mid + 1;
				mid = (start + end) / 2;
			}
			return -1;
		} finally{
			this.rwlock.readLock().unlock();
		}
	}

	/**
	 * Get the frequency of a word from the dictionary
	 * 
	 * @param charArray
	 *            input word
	 * @return word frequency, or zero if the word is not found
	 */
	public int getFrequency(char[] charArray) {
		this.rwlock.readLock().lock();
		try {
			short hashIndex = getWordItemTableIndex(charArray[0]);
			if (hashIndex == -1)
				return 0;
			int itemIndex = findInTable(hashIndex, charArray);
			if (itemIndex != -1)
				return wordItem_frequencyTable[wordIndexTable[hashIndex]][itemIndex];
			return 0;
		} finally{
			this.rwlock.readLock().unlock();
		}
	}

	/**
	 * Return true if the dictionary entry at itemIndex for table charArray[0]
	 * is charArray
	 * 
	 * @param charArray
	 *            input word
	 * @param itemIndex
	 *            item index for table charArray[0]
	 * @return true if the entry exists
	 */
	public boolean isEqual(char[] charArray, int itemIndex) {
		short hashIndex = getWordItemTableIndex(charArray[0]);
		return Utility.compareArray(charArray, 1,
				wordItem_charArrayTable[wordIndexTable[hashIndex]][itemIndex],
				0) == 0;
	}
}
