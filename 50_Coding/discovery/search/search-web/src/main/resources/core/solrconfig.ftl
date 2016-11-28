<?xml version="1.0" encoding="UTF-8" ?>


<config>

<luceneMatchVersion>4.4</luceneMatchVersion>

<lib dir="../../../dist/" regex="apache-solr-cell-\d.*\.jar" />
<lib dir="../../../contrib/extraction/lib" regex=".*\.jar" />

<lib dir="../../../dist/" regex="apache-solr-clustering-\d.*\.jar" />
<lib dir="../../../contrib/clustering/lib/" regex=".*\.jar" />

<lib dir="../../../dist/" regex="apache-solr-langid-\d.*\.jar" />
<lib dir="../../../contrib/langid/lib/" regex=".*\.jar" />

<lib dir="../../../dist/" regex="apache-solr-velocity-\d.*\.jar" />
<lib dir="../../../contrib/velocity/lib" regex=".*\.jar" />

<lib dir="/total/crap/dir/ignored" />

<queryParser name="picene" class="org.apache.solr.search.PiceneQParserPlugin" />

<#noparse>
<dataDir>${solr.data.dir:}</dataDir>

</#noparse>


<#noparse>
<directoryFactory name="DirectoryFactory"
                  class="${solr.directoryFactory:solr.NRTCachingDirectoryFactory}"/>
</#noparse>


<indexConfig>
    <useCompoundFile>true</useCompoundFile>
    <mergeFactor>5</mergeFactor>
</indexConfig>


<jmx />

<updateHandler class="solr.DirectUpdateHandler2">
    <autoCommit>
        <maxTime>600000</maxTime>
        <openSearcher>true</openSearcher>
    </autoCommit>

    <autoSoftCommit>
        <maxTime>10000</maxTime>
    </autoSoftCommit>

    <updateLog>
        <#noparse>
        <str name="dir">${solr.data.dir:}</str>
        </#noparse>
    </updateLog>


</updateHandler>

<query>

    <maxBooleanClauses>10240</maxBooleanClauses>


    <filterCache class="solr.FastLRUCache"
                 size="5120"
                 initialSize="512"
                 autowarmCount="0"/>


    <queryResultCache class="solr.LRUCache"
                      size="5120"
                      initialSize="512"
                      autowarmCount="0"/>


    <documentCache class="solr.LRUCache"
                   size="5120"
                   initialSize="512"
                   autowarmCount="0"/>



    <enableLazyFieldLoading>true</enableLazyFieldLoading>

    <queryResultWindowSize>20</queryResultWindowSize>

    <queryResultMaxDocsCached>200</queryResultMaxDocsCached>

    <listener event="newSearcher" class="solr.QuerySenderListener">
        <arr name="queries">
        </arr>
    </listener>
    <listener event="firstSearcher" class="solr.QuerySenderListener">
        <arr name="queries">
            <lst>
                <str name="q">static firstSearcher warming in solrconfig.xml</str>
            </lst>
        </arr>
    </listener>


    <useColdSearcher>false</useColdSearcher>


    <maxWarmingSearchers>10</maxWarmingSearchers>

</query>


<requestDispatcher handleSelect="false" >

    <requestParsers enableRemoteStreaming="true"
                    multipartUploadLimitInKB="2048000" />

    <httpCaching never304="true" />

</requestDispatcher>

<requestHandler name="/select" class="solr.SearchHandler">
    <lst name="defaults">
        <str name="echoParams">explicit</str>
        <int name="rows">10</int>
        <str name="df">text</str>
    </lst>

    <arr name="components">
        <str>query</str>
        <str>facet</str>
        <str>mlt</str>
        <str>highlight</str>
        <str>discoveryStats</str>
        <str>debug</str>
        <str>track</str>
        <str>insight</str>
    </arr>
</requestHandler>

<requestHandler name="/query" class="solr.SearchHandler">
    <lst name="defaults">
        <str name="echoParams">explicit</str>
        <str name="wt">json</str>
        <str name="indent">true</str>
        <str name="df">text</str>
    </lst>
</requestHandler>

<requestHandler name="/get" class="solr.RealTimeGetHandler">
    <lst name="defaults">
        <str name="omitHeader">true</str>
        <str name="wt">json</str>
        <str name="indent">true</str>
    </lst>
</requestHandler>


<requestHandler name="/update" class="solr.UpdateRequestHandler">
</requestHandler>

<requestHandler name="/update/json" class="solr.JsonUpdateRequestHandler">
    <lst name="defaults">
        <str name="stream.contentType">application/json</str>
    </lst>
</requestHandler>
<requestHandler name="/update/csv" class="solr.CSVRequestHandler">
    <lst name="defaults">
        <str name="stream.contentType">application/csv</str>
    </lst>
</requestHandler>

<requestHandler name="/update/extract"
                startup="lazy"
                class="solr.extraction.ExtractingRequestHandler" >
    <lst name="defaults">
        <str name="lowernames">true</str>
        <str name="uprefix">ignored_</str>

        <str name="captureAttr">true</str>
        <str name="fmap.a">links</str>
        <str name="fmap.div">ignored_</str>
    </lst>
</requestHandler>


<requestHandler name="/dataimport" class="org.apache.solr.handler.dataimport.DataImportHandler">
    <lst name="defaults">
        <str name="config">data-config.xml</str>
    </lst>
</requestHandler>


<requestHandler name="/analysis/field"
                startup="lazy"
                class="solr.FieldAnalysisRequestHandler" />


<requestHandler name="/analysis/document"
                class="solr.DocumentAnalysisRequestHandler"
                startup="lazy" />

<requestHandler name="/admin/"
                class="solr.admin.AdminHandlers"/>


<requestHandler name="/admin/ping" class="solr.PingRequestHandler">
    <lst name="invariants">
        <str name="q">solrpingquery</str>
    </lst>
    <lst name="defaults">
        <str name="echoParams">all</str>
    </lst>
</requestHandler>

<requestHandler name="/debug/dump" class="solr.DumpRequestHandler" >
    <lst name="defaults">
        <str name="echoParams">explicit</str>
        <str name="echoHandler">true</str>
    </lst>
</requestHandler>

<requestHandler name="/replication" class="solr.ReplicationHandler" >
</requestHandler>

<searchComponent name="discoveryStats" class="solr.DiscoveryStatsComponent">
</searchComponent>

<searchComponent name="insight" class="solr.InsightComponent">
</searchComponent>

<searchComponent name="track" class="solr.TrackComponent">
</searchComponent>

<searchComponent name="spellcheck" class="solr.SpellCheckComponent">

    <str name="queryAnalyzerFieldType">textSpell</str>

    <lst name="spellchecker">
        <str name="name">sc_keyword</str>
        <str name="classname">solr.IndexBasedSpellChecker</str>
        <str name="field">spellcheck</str>
        <str name="spellcheckIndexDir">./spellchecker</str>
        <str name="accuracy">0.5</str>
        <str name="chinese">on</str>
        <str name="buildOnOptimize">true</str>
    </lst>

    <lst name="spellchecker">
        <str name="name">sc_keyword_freq</str>
        <str name="classname">solr.IndexBasedSpellChecker</str>
        <str name="field">keyword_freq</str>
        <str name="spellcheckIndexDir">./spellchecker_freq</str>
        <str name="accuracy">0.5</str>
        <str name="chinese">on</str>
        <str name="buildOnOptimize">true</str>
    </lst>

</searchComponent>

<requestHandler name="/spell" class="solr.SearchHandler" startup="lazy">
    <lst name="defaults">
        <str name="df">spellcheck</str>
        <str name="spellcheck.dictionary">sc_keyword</str>
        <str name="spellcheck.dictionary">sc_keyword_freq</str>
        <str name="spellcheck">on</str>
        <str name="spellcheck.extendedResults">true</str>
        <str name="spellcheck.count">10</str>
        <str name="spellcheck.alternativeTermCount">5</str>
        <str name="spellcheck.maxResultsForSuggest">5</str>
        <str name="spellcheck.collate">true</str>
        <str name="spellcheck.collateExtendedResults">true</str>
        <str name="spellcheck.maxCollationTries">10</str>
        <str name="spellcheck.maxCollations">5</str>
    </lst>
    <arr name="last-components">
        <str>spellcheck</str>
    </arr>
</requestHandler>

<searchComponent  name="suggest" class="solr.SpellCheckComponent">
    <str name="queryAnalyzerFieldType">textSpell</str>

    <lst name="spellchecker">
        <str name="name">suggest</str>
        <str name="classname">org.apache.solr.spelling.suggest.Suggester</str>
        <str name="lookupImpl">org.apache.solr.spelling.suggest.tst.TSTLookup</str>
        <str name="field">spellcheck</str>
        <#--<str name="sourceLocation">./spellings.txt</str>-->
        <float name="threshold">0.005</float>
        <str name="buildOnOptimize">true</str>
        <str name="storeDir">./suggester</str>
    </lst>

    <lst name="spellchecker">
        <str name="name">suggest_freq</str>
        <str name="classname">org.apache.solr.spelling.suggest.Suggester</str>
        <str name="lookupImpl">org.apache.solr.spelling.suggest.tst.TSTLookup</str>
        <str name="field">keyword_freq</str>
        <float name="threshold">0.005</float>
        <str name="buildOnOptimize">true</str>
        <str name="storeDir">./suggester_freq</str>
    </lst>

</searchComponent>

<requestHandler  name="/suggest" class="org.apache.solr.handler.component.SearchHandler">
    <lst name="defaults">
        <str name="spellcheck">true</str>
        <str name="spellcheck.dictionary">suggest</str>
        <str name="spellcheck.dictionary">suggest_freq</str>
        <str name="spellcheck.onlyMorePopular">true</str>
        <str name="spellcheck.count">5</str>
        <str name="spellcheck.collate">true</str>
    </lst>
    <arr name="components">
        <str>suggest</str>
    </arr>
</requestHandler>


<searchComponent name="terms" class="solr.TermsComponent"/>

<requestHandler name="/terms" class="solr.SearchHandler" startup="lazy">
    <lst name="defaults">
        <bool name="terms">true</bool>
    </lst>
    <arr name="components">
        <str>terms</str>
    </arr>
</requestHandler>

<searchComponent class="solr.HighlightComponent" name="highlight">
    <highlighting>
        <fragmenter name="gap"
                    default="true"
                    class="solr.highlight.GapFragmenter">
            <lst name="defaults">
                <int name="hl.fragsize">100</int>
            </lst>
        </fragmenter>

        <fragmenter name="regex"
                    class="solr.highlight.RegexFragmenter">
            <lst name="defaults">
                <int name="hl.fragsize">70</int>
                <float name="hl.regex.slop">0.5</float>
                <str name="hl.regex.pattern">[-\w ,/\n\&quot;&apos;]{20,200}</str>
            </lst>
        </fragmenter>

        <formatter name="html"
                   default="true"
                   class="solr.highlight.HtmlFormatter">
            <lst name="defaults">
                <str name="hl.simple.pre"><![CDATA[<em>]]></str>
                <str name="hl.simple.post"><![CDATA[</em>]]></str>
            </lst>
        </formatter>

        <encoder name="html"
                 class="solr.highlight.HtmlEncoder" />

        <fragListBuilder name="simple"
                         class="solr.highlight.SimpleFragListBuilder"/>

        <fragListBuilder name="single"
                         class="solr.highlight.SingleFragListBuilder"/>

        <fragListBuilder name="weighted"
                         default="true"
                         class="solr.highlight.WeightedFragListBuilder"/>

        <fragmentsBuilder name="default"
                          default="true"
                          class="solr.highlight.ScoreOrderFragmentsBuilder">
        </fragmentsBuilder>

        <fragmentsBuilder name="colored"
                          class="solr.highlight.ScoreOrderFragmentsBuilder">
            <lst name="defaults">
                <str name="hl.tag.pre"><![CDATA[
                    <b style="background:yellow">,<b style="background:lawgreen">,
                        <b style="background:aquamarine">,<b style="background:magenta">,
                            <b style="background:palegreen">,<b style="background:coral">,
                                <b style="background:wheat">,<b style="background:khaki">,
                                    <b style="background:lime">,<b style="background:deepskyblue">]]></str>
                <str name="hl.tag.post"><![CDATA[</b>]]></str>
            </lst>
        </fragmentsBuilder>

        <boundaryScanner name="default"
                         default="true"
                         class="solr.highlight.SimpleBoundaryScanner">
            <lst name="defaults">
                <str name="hl.bs.maxScan">10</str>
                <str name="hl.bs.chars">.,!? &#9;&#10;&#13;</str>
            </lst>
        </boundaryScanner>

        <boundaryScanner name="breakIterator"
                         class="solr.highlight.BreakIteratorBoundaryScanner">
            <lst name="defaults">
                <str name="hl.bs.type">WORD</str>
                <str name="hl.bs.language">en</str>
                <str name="hl.bs.country">US</str>
            </lst>
        </boundaryScanner>
    </highlighting>
</searchComponent>


<queryResponseWriter name="json" class="solr.JSONResponseWriter">
    <str name="content-type">application/json; charset=UTF-8</str>
</queryResponseWriter>

<queryResponseWriter name="json_cols" class="solr.JSONColsResponseWriter">
    <str name="content-type">application/json; charset=UTF-8</str>
</queryResponseWriter>

<queryResponseWriter name="velocity" class="solr.VelocityResponseWriter" startup="lazy"/>

<queryResponseWriter name="xslt" class="solr.XSLTResponseWriter">
    <int name="xsltCacheLifetimeSeconds">5</int>
</queryResponseWriter>

<admin>
    <defaultQuery>*:*</defaultQuery>
</admin>

</config>
