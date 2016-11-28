package org.apache.lucene.analysis.chinese.dictionary;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Manages analysis data configuration for SmartChineseAnalyzer
 * <p>
 * ChineseAnalyzer has a built-in dictionary and stopword list out-of-box.
 * </p>
  * 
 */
public class AnalyzerProfile {

  /**
   * Global indicating the configured analysis data directory
   */
  public static String ANALYSIS_DATA_DIR = "";

  static {
    init();
  }

  private static void init() {
    String dirName = "analysis-data";
    String propName = "analysis.properties";

    // Try the system property：-Danalysis.data.dir=/path/to/analysis-data
    ANALYSIS_DATA_DIR = System.getProperty("analysis.data.dir", "");
    if (ANALYSIS_DATA_DIR.length() != 0)
      return;

    File[] cadidateFiles = new File[] { new File("./" + dirName),
        new File("./lib/" + dirName), new File("./" + propName),
        new File("./lib/" + propName) };
    for (int i = 0; i < cadidateFiles.length; i++) {
      File file = cadidateFiles[i];
      if (file.exists()) {
        if (file.isDirectory()) {
          ANALYSIS_DATA_DIR = file.getAbsolutePath();
        } else if (file.isFile() && getAnalysisDataDir(file).length() != 0) {
          ANALYSIS_DATA_DIR = getAnalysisDataDir(file);
        }
        break;
      }
    }

    if (ANALYSIS_DATA_DIR.length() == 0) {
      // Dictionary directory cannot be found.
      System.err
          .println("WARNING: Can not find lexical dictionary directory!");
      System.err
          .println("WARNING: This will cause unpredictable exceptions in your application!");
      System.err
          .println("WARNING: Please refer to the manual to download the dictionaries.");
    }

  }

  private static String getAnalysisDataDir(File propFile) {
    Properties prop = new Properties();
    try {
      FileInputStream input = new FileInputStream(propFile);
      prop.load(input);
      String dir = prop.getProperty("analysis.data.dir", "");
      input.close();
      return dir;
    } catch (IOException e) {
    }
    return "";
  }

}
