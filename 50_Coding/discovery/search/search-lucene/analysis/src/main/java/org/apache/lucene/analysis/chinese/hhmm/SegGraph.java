package org.apache.lucene.analysis.chinese.hhmm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Graph representing possible tokens at each start offset in the sentence.
 * <p>
 * For each start offset, a list of possible tokens is stored.
 */
class SegGraph {

  /**
   * Map of start offsets to ArrayList of tokens at that position
   */
  private Map<Integer,ArrayList<SegToken>> tokenListTable = new HashMap<Integer,ArrayList<SegToken>>();

  private int maxStart = -1;

  /**
   * Returns true if a mapping for the specified start offset exists
   * 
   * @param s startOffset
   * @return true if there are tokens for the startOffset
   */
  public boolean isStartExist(int s) {
    return tokenListTable.get(s) != null;
  }

  /**
   * Get the list of tokens at the specified start offset
   * 
   * @param s startOffset
   * @return List of tokens at the specified start offset.
   */
  public List<SegToken> getStartList(int s) {
    return tokenListTable.get(s);
  }

  /**
   * Get the highest start offset in the map
   * 
   * @return maximum start offset, or -1 if the map is empty.
   */
  public int getMaxStart() {
    return maxStart;
  }

  /**
   * Set the {@link SegToken#index} for each token, based upon its order by startOffset. 
   * @return a {@link java.util.List} of these ordered tokens.
   */
  public List<SegToken> makeIndex() {
    List<SegToken> result = new ArrayList<SegToken>();
    int s = -1, count = 0, size = tokenListTable.size();
    List<SegToken> tokenList;
    short index = 0;
    while (count < size) {
      if (isStartExist(s)) {
        tokenList = tokenListTable.get(s);
        for (SegToken st : tokenList) {
          st.index = index;
          result.add(st);
          index++;
        }
        count++;
      }
      s++;
    }
    return result;
  }

  /**
   * Add a {@link SegToken} to the mapping, creating a new mapping at the token's startOffset if one does not exist. 
   * @param token {@link SegToken}
   */
  public void addToken(SegToken token) {
    int s = token.startOffset;
    if (!isStartExist(s)) {
      ArrayList<SegToken> newlist = new ArrayList<SegToken>();
      newlist.add(token);
      tokenListTable.put(s, newlist);
    } else {
      List<SegToken> tokenList = tokenListTable.get(s);
      tokenList.add(token);
    }
    if (s > maxStart)
      maxStart = s;
  }

  /**
   * Return a {@link java.util.List} of all tokens in the map, ordered by startOffset.
   * 
   * @return {@link java.util.List} of all tokens in the map.
   */
  public List<SegToken> toTokenList() {
    List<SegToken> result = new ArrayList<SegToken>();
    int s = -1, count = 0, size = tokenListTable.size();
    List<SegToken> tokenList;

    while (count < size) {
      if (isStartExist(s)) {
        tokenList = tokenListTable.get(s);
        for (SegToken st : tokenList) {
          result.add(st);
        }
        count++;
      }
      s++;
    }
    return result;
  }

  @Override
  public String toString() {
    List<SegToken> tokenList = this.toTokenList();
    StringBuilder sb = new StringBuilder();
    for (SegToken t : tokenList) {
      sb.append(t + "\n");
    }
    return sb.toString();
  }
}