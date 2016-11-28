package net.xinshi.discovery.search.mgt.response;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.XML;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.search.ReturnFields;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 9/11/13
 * Time: 10:56 AM
 */
public class InsightXMLWriter extends InsightTextResponseWriter {
    public static float CURRENT_VERSION=2.2f;

    private static final char[] XML_START1="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".toCharArray();

    private static final char[] XML_STYLESHEET="<?xml-stylesheet type=\"text/xsl\" href=\"".toCharArray();
    private static final char[] XML_STYLESHEET_END="\"?>\n".toCharArray();

    private static final char[] XML_START2_NOSCHEMA=("<response>\n").toCharArray();

    final int version;

    public InsightXMLWriter(Writer writer,IndexSchema schema, SolrParams params, NamedList values, ReturnFields returnFields, int offset, int limit) {
        super(writer, schema, params, values, returnFields,offset,limit);

        String version = params.get(CommonParams.VERSION);
        float ver = version==null? CURRENT_VERSION : Float.parseFloat(version);
        this.version = (int)(ver*1000);
        if( this.version < 2200 ) {
            throw new SolrException( SolrException.ErrorCode.BAD_REQUEST,
                    "XMLWriter does not support version: "+version );
        }
    }



    public void writeResponse() throws IOException {
        writer.write(XML_START1);

        String stylesheet = this.params.get("stylesheet");
        if (stylesheet != null && stylesheet.length() > 0) {
            writer.write(XML_STYLESHEET);
            XML.escapeAttributeValue(stylesheet, writer);
            writer.write(XML_STYLESHEET_END);
        }

        writer.write(XML_START2_NOSCHEMA);

        // dump response values
        NamedList<?> lst = this.values;
        Boolean omitHeader = this.params.getBool(CommonParams.OMIT_HEADER);
        if(omitHeader != null && omitHeader) lst.remove("responseHeader");
        int sz = lst.size();
        int start=0;

        for (int i=start; i<sz; i++) {
            writeVal(lst.getName(i),lst.getVal(i));
        }

        writer.write("\n</response>\n");
    }





    /** Writes the XML attribute name/val. A null val means that the attribute is missing. */
    private void writeAttr(String name, String val) throws IOException {
        writeAttr(name, val, true);
    }

    public void writeAttr(String name, String val, boolean escape) throws IOException{
        if (val != null) {
            writer.write(' ');
            writer.write(name);
            writer.write("=\"");
            if(escape){
                XML.escapeAttributeValue(val, writer);
            } else {
                writer.write(val);
            }
            writer.write('"');
        }
    }

    void startTag(String tag, String name, boolean closeTag) throws IOException {
        if (doIndent) indent();

        writer.write('<');
        writer.write(tag);
        if (name!=null) {
            writeAttr("name", name);
            if (closeTag) {
                writer.write("/>");
            } else {
                writer.write(">");
            }
        } else {
            if (closeTag) {
                writer.write("/>");
            } else {
                writer.write('>');
            }
        }
    }


    @Override
    public void writeStartDocumentList(String name,
                                       long start, int size, long numFound, Float maxScore) throws IOException
    {
        if (doIndent) indent();

        writer.write("<result");
        writeAttr("name",name);
        writeAttr("numFound",Long.toString(numFound));
        writeAttr("start",Long.toString(start));
        if(maxScore!=null) {
            writeAttr("maxScore",Float.toString(maxScore));
        }
        writer.write(">");

        incLevel();
    }


    /**
     * The SolrDocument should already have multivalued fields implemented as
     * Collections -- this will not rewrite to &lt;arr&gt;
     */
    @Override
    public void writeSolrDocument(String name, SolrDocument doc, ReturnFields returnFields, int idx ) throws IOException {
        startTag("doc", name, false);
        incLevel();

        for (String fname : doc.getFieldNames()) {
            if (!returnFields.wantsField(fname)) {
                continue;
            }

            Object val = doc.getFieldValue(fname);
            if( "_explain_".equals( fname ) ) {
                System.out.println( val );
            }
            writeVal(fname, val);
        }

        decLevel();
        writer.write("</doc>");
    }

    @Override
    public void writeEndDocumentList() throws IOException
    {
        decLevel();
        if (doIndent) indent();
        writer.write("</result>");
    }



    //
    // Generic compound types
    //

    @Override
    public void writeNamedList(String name, NamedList val) throws IOException {
        int sz = val.size();
        startTag("lst", name, sz<=0);

        incLevel();

        for (int i=0; i<sz; i++) {
            writeVal(val.getName(i),val.getVal(i));
        }

        decLevel();

        if (sz > 0) {
            if (doIndent) indent();
            writer.write("</lst>");
        }
    }

    @Override
    public void writeMap(String name, Map map, boolean excludeOuter, boolean isFirstVal) throws IOException {
        int sz = map.size();

        if (!excludeOuter) {
            startTag("lst", name, sz<=0);
            incLevel();
        }

        for (Map.Entry entry : (Set<Map.Entry>)map.entrySet()) {
            Object k = entry.getKey();
            Object v = entry.getValue();
            // if (sz<indentThreshold) indent();
            writeVal( null == k ? null : k.toString(), v);
        }

        if (!excludeOuter) {
            decLevel();
            if (sz > 0) {
                if (doIndent) indent();
                writer.write("</lst>");
            }
        }
    }

    @Override
    public void writeArray(String name, Object[] val) throws IOException {
        writeArray(name, Arrays.asList(val).iterator());
    }

    @Override
    public void writeArray(String name, Iterator iter) throws IOException {

        if ("rows".equals(name)) {
            int off = offset;
            int lim = limit;
            if (iter.hasNext()) {
                startTag("arr", name, false);
                incLevel();
                while (iter.hasNext()) {
                    if (--off >= 0) {
                        iter.next();
                        continue;
                    }
                    if (--lim < 0) break;
                    writeVal(null, iter.next());
                }
                decLevel();
                if (doIndent) indent();
                writer.write("</arr>");
            } else {
                startTag("arr", name, true);
            }
        } else {
            if (iter.hasNext()) {
                startTag("arr", name, false);
                incLevel();
                while (iter.hasNext()) {
                    writeVal(null, iter.next());
                }
                decLevel();
                if (doIndent) indent();
                writer.write("</arr>");
            } else {
                startTag("arr", name, true);
            }
        }
    }

    //
    // Primitive types
    //

    @Override
    public void writeNull(String name) throws IOException {
        writePrim("null",name,"",false);
    }

    @Override
    public void writeStr(String name, String val, boolean escape) throws IOException {
        writePrim("str",name,val,escape);
    }

    @Override
    public void writeInt(String name, String val) throws IOException {
        writePrim("int",name,val,false);
    }

    @Override
    public void writeLong(String name, String val) throws IOException {
        writePrim("long",name,val,false);
    }

    @Override
    public void writeBool(String name, String val) throws IOException {
        writePrim("bool",name,val,false);
    }

    @Override
    public void writeFloat(String name, String val) throws IOException {
        writePrim("float",name,val,false);
    }

    @Override
    public void writeFloat(String name, float val) throws IOException {
        writeFloat(name,Float.toString(val));
    }

    @Override
    public void writeDouble(String name, String val) throws IOException {
        writePrim("double",name,val,false);
    }

    @Override
    public void writeDouble(String name, double val) throws IOException {
        writeDouble(name,Double.toString(val));
    }

    @Override
    public void writeDate(String name, String val) throws IOException {
        writePrim("date",name,val,false);
    }

    //
    // OPT - specific writeInt, writeFloat, methods might be faster since
    // there would be less write calls (write("<int name=\"" + name + ... + </int>)
    //
    private void writePrim(String tag, String name, String val, boolean escape) throws IOException {
        int contentLen = val==null ? 0 : val.length();

        startTag(tag, name, contentLen==0);
        if (contentLen==0) return;

        if (escape) {
            XML.escapeCharData(val,writer);
        } else {
            writer.write(val,0,contentLen);
        }

        writer.write('<');
        writer.write('/');
        writer.write(tag);
        writer.write('>');
    }

}
