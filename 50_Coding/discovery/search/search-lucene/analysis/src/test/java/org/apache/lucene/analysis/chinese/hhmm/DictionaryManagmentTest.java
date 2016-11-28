package org.apache.lucene.analysis.chinese.hhmm;

import java.io.EOFException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.junit.Before;
import org.junit.Test;

public class DictionaryManagmentTest {
	private short[] wordIndexTable;

	private char[] charIndexTable;

	private char[][][] wordItem_charArrayTable;

	private int[][] wordItem_frequencyTable;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void dictioanry() {
		InputStream serialObjectInputStream = this.getClass().getClassLoader()
				.getResourceAsStream("coredict.mem");

		ObjectInputStream input = null;

		String fileName = "/Users/benzhao/work/temp/wordDictionary.txt";
		FileWriter fw = null;

		
		try {
			input = new ObjectInputStream(serialObjectInputStream);
			wordIndexTable = (short[]) input.readObject();
			charIndexTable = (char[]) input.readObject();
			wordItem_charArrayTable = (char[][][]) input.readObject();
			wordItem_frequencyTable = (int[][]) input.readObject();
			// log.info("load core dict from serialization.");
			
			fw = new FileWriter(fileName);
			
			int n = 0;
			for (int i = 0; i < wordIndexTable.length; i++) {
				if (wordIndexTable[i] != -1) {
					
					//System.out.println(wordIndexTable[i] + " : " + charIndexTable[wordIndexTable[i]]);
					char[][] items = wordItem_charArrayTable[wordIndexTable[i]];
					for (int j = 0; j < items.length; j++) {
						char[] item = items[j];
						if (item == null) {
							continue;
						}
						StringBuilder sb = new StringBuilder();
						sb.append(charIndexTable[wordIndexTable[i]]);
						n++;
						//System.out.print(charIndexTable[wordIndexTable[i]]);
						for (int k = 0; k < item.length; k++) {
							//System.out.print(item[k]);
							sb.append(item[k]);
						}
						System.out.println(sb.toString() +  " --- " + wordItem_frequencyTable[wordIndexTable[i]][j]);
						fw.write(sb.toString() + "|" + wordItem_frequencyTable[wordIndexTable[i]][j] + "\n");
						
					}
				}
				
			}
			System.out.println("total" + n);			
		} catch (Exception e) {
			if (e instanceof EOFException) {
				System.out.println(e.getMessage());
			} else {
				e.printStackTrace();
			}
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}
