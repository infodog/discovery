<?xml version="1.0" encoding="UTF-8" ?>

<!--
 This is the Solr schema file. This file should be named "schema.xml" and
 should be in the conf directory under the solr home
 (i.e. ./solr/conf/schema.xml by default)
 or located where the classloader for the Solr webapp can find it.

 This example schema is the recommended starting point for users.
 It should be kept correct and concise, usable out-of-the-box.

 For more information, on how to customize this file, please see
 http://wiki.apache.org/solr/SchemaXml

 PERFORMANCE NOTE: this schema includes many optional features and should not
 be used for benchmarking.  To improve performance one could
  - set stored="false" for all fields possible (esp large fields) when you
    only need to search on the field but don't need to return the original
    value.
  - set indexed="false" if you don't need to search on the field, but only
    return the field as a result of searching on other indexed fields.
  - remove all unneeded copyField statements
  - for best index size and searching performance, set "index" to false
    for all general text fields, use copyField to copy them to the
    catchall "text" field, and use that for searching.
  - For maximum indexing performance, use the StreamingUpdateSolrServer
    java client.
  - Remember to run the JVM in server mode, and use a higher logging level
    that avoids logging every request
-->

<schema name="${name}" version="1.5">
<!-- attribute "name" is the name of this schema and is only used for display purposes.
  version="x.y" is Solr's version number for the schema syntax and
  semantics.  It should not normally be changed by applications.

  1.0: multiValued attribute did not exist, all fields are multiValued
       by nature
  1.1: multiValued attribute introduced, false by default
  1.2: omitTermFreqAndPositions attribute introduced, true by default
       except for text fields.
  1.3: removed optional field compress feature
  1.4: autoGeneratePhraseQueries attribute introduced to drive QueryParser
       behavior when a single string produces multiple tokens.  Defaults
       to off for version >= 1.4
  1.5: omitNorms defaults to true for primitive field types
       (int, float, boolean, string...)
-->

<fields>
    <!-- Valid attributes for fields:
      name: mandatory - the name for the field
      type: mandatory - the name of a field type from the
        <types> fieldType section
      indexed: true if this field should be indexed (searchable or sortable)
      stored: true if this field should be retrievable
      multiValued: true if this field may contain multiple values per document
      omitNorms: (expert) set to true to omit the norms associated with
        this field (this disables length normalization and index-time
        boosting for the field, and saves some memory).  Only full-text
        fields or fields that need an index-time boost need norms.
        Norms are omitted for primitive (non-analyzed) types by default.
      termVectors: [false] set to true to store the term vector for a
        given field.
        When using MoreLikeThis, fields used for similarity should be
        stored for best performance.
      termPositions: Store position information with the term vector.
        This will increase storage costs.
      termOffsets: Store offset information with the term vector. This
        will increase storage costs.
      required: The field is required.  It will throw an error if the
        value does not exist
      default: a value that should be used if no value is specified
        when adding a document.
    -->

    <!-- field names should consist of alphanumeric or underscore characters only and
       not start with a digit.  This is not currently strictly enforced,
       but other field names will not have first class support from all components
       and back compatibility is not guaranteed.  Names with both leading and
       trailing underscores (e.g. _version_) are reserved.
    -->

    <#list fields as field>
    <field name="${field.fieldName}" type="${field.type}" indexed="${field.search?string("true", "false")}" stored="${field.result?string("true", "false")}" />
    </#list>

    <field name="id" type="term" indexed="true" stored="true"/>
    <field name="all.in.one.id" type="term" indexed="true" stored="true"/>


    <field name="text" type="text" indexed="true" stored="false"/>

    <field name="spellcheck"  type="textSpell"  indexed="true"/>
    <field name="keyword_freq"  type="wordFrequency"  indexed="true" stored="false" omitNorms="true" omitPositions="true"/>

    <field name="metric_order-id" type="term" indexed="true" stored="false"/>
    <field name="metric_user-id" type="term" indexed="true" stored="false"/>

    <field name="_version_" type="long" indexed="true" stored="true"/>
    <field name="loadTime" type="date"   indexed="true"  stored="true"/>
    <field name="unloadTime" type="date"   indexed="true"  stored="true"/>
    <dynamicField name="*_term"  type="term"  indexed="true"  stored="false"/>
    <dynamicField name="*_text"  type="text"  indexed="true"  stored="false"/>
    <dynamicField name="tokenized_*"  type="text"  indexed="true"  stored="false"/>
    <dynamicField name="*_store"  type="term"  indexed="false"  stored="true"/>
    <dynamicField name="*_multiValued"  type="term"  indexed="true"  stored="false" multiValued="true"/>

    <dynamicField name="*_highlight"  type="text"  indexed="false"  stored="true"/>

    <dynamicField name="*_path"  type="descendent_path"  indexed="true"  stored="false" multiValued="true"/>

    <dynamicField name="*_facetColumn"  type="facetColumn"  indexed="true"  stored="false" multiValued="true"/>
    <dynamicField name="*_facetColumn0"  type="term"  indexed="true"  stored="false" multiValued="true"/>
    <dynamicField name="*_facetColumn1"  type="term"  indexed="true"  stored="false" multiValued="true"/>
    <dynamicField name="*_facetColumn2"  type="term"  indexed="true"  stored="false" multiValued="true"/>
    <dynamicField name="*_facetColumn3"  type="term"  indexed="true"  stored="false" multiValued="true"/>
    <dynamicField name="*_facetColumn4"  type="term"  indexed="true"  stored="false" multiValued="true"/>
    <dynamicField name="*_facetColumn5"  type="term"  indexed="true"  stored="false" multiValued="true"/>
    <dynamicField name="*_facetColumn6"  type="term"  indexed="true"  stored="false" multiValued="true"/>
    <dynamicField name="*_facetColumn7"  type="term"  indexed="true"  stored="false" multiValued="true"/>
    <dynamicField name="*_facetColumn8"  type="term"  indexed="true"  stored="false" multiValued="true"/>
    <dynamicField name="*_facetColumn9"  type="term"  indexed="true"  stored="false" multiValued="true"/>


    <dynamicField name="*_hierarchy"  type="facetColumn"  indexed="true"  stored="false" multiValued="false"/>
    <dynamicField name="*_hierarchy0"  type="term"  indexed="true"  stored="false" multiValued="false"/>
    <dynamicField name="*_hierarchy1"  type="term"  indexed="true"  stored="false" multiValued="false"/>
    <dynamicField name="*_hierarchy2"  type="term"  indexed="true"  stored="false" multiValued="false"/>
    <dynamicField name="*_hierarchy3"  type="term"  indexed="true"  stored="false" multiValued="false"/>
    <dynamicField name="*_hierarchy4"  type="term"  indexed="true"  stored="false" multiValued="false"/>
    <dynamicField name="*_hierarchy5"  type="term"  indexed="true"  stored="false" multiValued="false"/>
    <dynamicField name="*_hierarchy6"  type="term"  indexed="true"  stored="false" multiValued="false"/>
    <dynamicField name="*_hierarchy7"  type="term"  indexed="true"  stored="false" multiValued="false"/>
    <dynamicField name="*_hierarchy8"  type="term"  indexed="true"  stored="false" multiValued="false"/>
    <dynamicField name="*_hierarchy9"  type="term"  indexed="true"  stored="false" multiValued="false"/>


    <dynamicField name="*_i"  type="int"    indexed="true"  stored="true"/>
    <dynamicField name="*_s"  type="string"  indexed="true"  stored="true"/>

    <dynamicField name="metric_*"  type="double"   indexed="true"  stored="true"/>
    <dynamicField name="*-time"  type="date"   indexed="true"  stored="true"/>
    <dynamicField name="*_l"  type="long"   indexed="true"  stored="true"/>
    <dynamicField name="*_f"  type="float"  indexed="true"  stored="true"/>
    <dynamicField name="*_d"  type="double" indexed="true"  stored="true"/>
    <dynamicField name="*_tf"  type="tfloat" indexed="true"  stored="true"/>
    <dynamicField name="*_tl"  type="tlong" indexed="true"  stored="true"/>

<#--the rest-->
    <dynamicField name="*" type="term" indexed="true" stored="false"/>
</fields>


<!-- Field to use to determine and enforce document uniqueness.
   Unless this field is marked with required="false", it will be a required field
-->
<uniqueKey>all.in.one.id</uniqueKey>

<types>
<!-- field type definitions. The "name" attribute is
   just a label to be used by field definitions.  The "class"
   attribute and any other attributes determine the real
   behavior of the fieldType.
     Class names starting with "solr" refer to java classes in a
   standard package such as org.apache.solr.analysis
-->

    <fieldType name="term" class="solr.StrField" sortMissingLast="true" />

    <fieldType name="facetColumn" class="solr.FacetColumnField" sortMissingLast="true" />


    <!-- The StrField type is not analyzed, but indexed/stored verbatim. -->
    <fieldType name="string" class="solr.StrField" sortMissingLast="true" />

    <!-- boolean type: "true" or "false" -->
    <fieldType name="boolean" class="solr.BoolField" sortMissingLast="true"/>

    <!-- sortMissingLast and sortMissingFirst attributes are optional attributes are
         currently supported on types that are sorted internally as strings
         and on numeric types.
         This includes "string","boolean", and, as of 3.5 (and 4.x),
         int, float, long, date, double, including the "Trie" variants.
       - If sortMissingLast="true", then a sort on this field will cause documents
         without the field to come after documents with the field,
         regardless of the requested sort order (asc or desc).
       - If sortMissingFirst="true", then a sort on this field will cause documents
         without the field to come before documents with the field,
         regardless of the requested sort order.
       - If sortMissingLast="false" and sortMissingFirst="false" (the default),
         then default lucene sorting will be used which places docs without the
         field first in an ascending sort and last in a descending sort.
    -->

    <!--
      Default numeric field types. For faster range queries, consider the tint/tfloat/tlong/tdouble types.
    -->
    <fieldType name="int" class="solr.TrieIntField" precisionStep="0" positionIncrementGap="0"/>
    <fieldType name="float" class="solr.TrieFloatField" precisionStep="0" positionIncrementGap="0"/>
    <fieldType name="long" class="solr.TrieLongField" precisionStep="0" positionIncrementGap="0"/>
    <fieldType name="double" class="solr.TrieDoubleField" precisionStep="0" positionIncrementGap="0"/>

    <!--
     Numeric field types that index each value at various levels of precision
     to accelerate range queries when the number of values between the range
     endpoints is large. See the javadoc for NumericRangeQuery for internal
     implementation details.

     Smaller precisionStep values (specified in bits) will lead to more tokens
     indexed per value, slightly larger index size, and faster range queries.
     A precisionStep of 0 disables indexing at different precision levels.
    -->
    <fieldType name="tint" class="solr.TrieIntField" precisionStep="8" positionIncrementGap="0"/>
    <fieldType name="tfloat" class="solr.TrieFloatField" precisionStep="8" positionIncrementGap="0"/>
    <fieldType name="tlong" class="solr.TrieLongField" precisionStep="8" positionIncrementGap="0"/>
    <fieldType name="tdouble" class="solr.TrieDoubleField" precisionStep="8" positionIncrementGap="0"/>

    <!-- The format for this date field is of the form 1995-12-31T23:59:59Z, and
       is a more restricted form of the canonical representation of dateTime
       http://www.w3.org/TR/xmlschema-2/#dateTime
       The trailing "Z" designates UTC time and is mandatory.
       Optional fractional seconds are allowed: 1995-12-31T23:59:59.999Z
       All other components are mandatory.

       Expressions can also be used to denote calculations that should be
       performed relative to "NOW" to determine the value, ie...

             NOW/HOUR
                ... Round to the start of the current hour
             NOW-1DAY
                ... Exactly 1 day prior to now
             NOW/DAY+6MONTHS+3DAYS
                ... 6 months and 3 days in the future from the start of
                    the current day

       Consult the DateField javadocs for more information.

       Note: For faster range queries, consider the tdate type
    -->
    <fieldType name="date" class="solr.TrieDateField" precisionStep="0" positionIncrementGap="0"/>

    <!-- A Trie based date field for faster date range queries and date faceting. -->
    <fieldType name="tdate" class="solr.TrieDateField" precisionStep="6" positionIncrementGap="0"/>

    <!-- A text field that only splits on whitespace for exact matching of words -->
    <fieldType name="text_ws" class="solr.TextField" positionIncrementGap="100">
        <analyzer>
            <tokenizer class="solr.WhitespaceTokenizerFactory"/>
        </analyzer>
    </fieldType>

    <fieldType name="text" class="solr.TextField" positionIncrementGap="100">
       <analyzer>
             <tokenizer class="solr.StandardTokenizerFactory"/>
             <filter class="solr.StandardFilterFactory"/>
             <filter class="solr.LowerCaseFilterFactory"/>
             <filter class="solr.StopFilterFactory"/>
      </analyzer>
     <analyzer type="query">
         <tokenizer class="solr.StandardTokenizerFactory"/>
         <filter class="solr.LowerCaseFilterFactory"/>
     </analyzer>

    </fieldType>

    <fieldType name="textSpell" class="solr.TextField" positionIncrementGap="100">
        <analyzer type="index">
            <tokenizer class="solr.ChineseTokenizerFactory"/>
            <filter class="solr.LowerCaseFilterFactory"/>
        </analyzer>
        <analyzer type="query">
            <tokenizer class="solr.KeywordTokenizerFactory" />
        </analyzer>
    </fieldType>

    <fieldType name="wordFrequency" class="solr.TextField" positionIncrementGap="100">
        <analyzer type="index">
            <tokenizer class="solr.WordFrequencyTokenizerFactory"/>
            <filter class="solr.LowerCaseFilterFactory"/>
        </analyzer>
        <analyzer type="query">
            <tokenizer class="solr.KeywordTokenizerFactory" />
        </analyzer>
    </fieldType>

    <!--
      Example of using PathHierarchyTokenizerFactory at index time, so
      queries for paths match documents at that path, or in descendent paths
    -->
    <fieldType name="descendent_path" class="solr.TextField">
        <analyzer type="index">
            <tokenizer class="solr.PathHierarchyTokenizerFactory" delimiter="/" />
        </analyzer>
        <analyzer type="query">
            <tokenizer class="solr.KeywordTokenizerFactory" />
        </analyzer>
    </fieldType>
    <!--
      Example of using PathHierarchyTokenizerFactory at query time, so
      queries for paths match documents at that path, or in ancestor paths
    -->
    <fieldType name="ancestor_path" class="solr.TextField">
        <analyzer type="index">
            <tokenizer class="solr.KeywordTokenizerFactory" />
        </analyzer>
        <analyzer type="query">
            <tokenizer class="solr.PathHierarchyTokenizerFactory" delimiter="/" />
        </analyzer>
    </fieldType>

</types>

</schema>
