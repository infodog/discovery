<solr persistent="true">
    <!-- by default, this is 50 @ WARN
   <logging enabled="true">
       <watcher size="100" threshold="INFO" />
   </logging>
    -->

    <!--
    adminPath: RequestHandler path to manage cores.
      If 'null' (or absent), cores will not be manageable via request handler
    defaultCoreName: (optional) core to use when no core name is specified in an access url

    All of the attributes in cores after defaultCoreName only apply when running in SolrCloud mode.
    You can read more about SolrCloud mode at http://wiki.apache.org/solr/SolrCloud
    -->
    <#noparse>
    <cores adminPath="/admin/cores" persistent="true" transientCacheSize="5000" defaultCoreName="collection1" host="${host:}" hostPort="${jetty.port:}" hostContext="${hostContext:}" zkClientTimeout="${zkClientTimeout:15000}">
    </#noparse>
    <#list domains as domain>
        <core name="${domain}" instanceDir="${domain}" loadOnStartup="false" transient="true"/>
    </#list>


    </cores>
</solr>