package net.xinshi.discovery.search.client.query;


/**
  A Term represents a word from text.  This is the unit of search.  It is
  composed of two elements, the text of the word, as a string, and the name of
  the field that the text occurred in.

  Note that terms may represent more than words from text fields, but also
  things like dates, email addresses, urls, etc.  */

public final class Term implements Comparable<Term> {
  String field;
  String text;


  /** Constructs a Term with the given field and text.
   * <p>Note that a null field or null text value results in undefined
   * behavior for most Lucene APIs that accept a Term parameter. */
  public Term(String fld, String text) {
    this.field = fld;
    this.text = text;
  }

  /** Returns the field of this term.   The field indicates
    the part of a document which this term came from. */
  public final String field() { return field; }

  /** Returns the text of this term.  In the case of words, this is simply the
    text of the word.  In the case of dates and other types, this is an
    encoding of the object as a string.  */
  public final String text() { return text; }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Term other = (Term) obj;
    if (field == null) {
      if (other.field != null)
        return false;
    } else if (!field.equals(other.field))
      return false;
    if (text == null) {
      if (other.text != null)
        return false;
    } else if (!text.equals(other.text()))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((field == null) ? 0 : field.hashCode());
    result = prime * result + ((text == null) ? 0 : text.hashCode());
    return result;
  }

  /** Compares two terms, returning a negative integer if this
    term belongs before the argument, zero if this term is equal to the
    argument, and a positive integer if this term belongs after the argument.

    The ordering of terms is first by field, then by text.*/
  public final int compareTo(Term other) {
    if (field.equals(other.field)) {
      return text.compareTo(other.text());
    } else {
      return field.compareTo(other.field);
    }
  }

  /** 
   * Resets the field and text of a Term. 
   * <p>WARNING: the provided BytesRef is not copied, but used directly.
   * Therefore the bytes should not be modified after construction, for
   * example, you should clone a copy rather than pass reused bytes from
   * a TermsEnum.
   */
  final void set(String fld, String text) {
    field = fld;
    this.text = text;
  }

  @Override
  public final String toString() { return field + ":" + text; }

}
