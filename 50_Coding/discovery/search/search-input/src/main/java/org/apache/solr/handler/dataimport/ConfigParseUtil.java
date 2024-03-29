
package org.apache.solr.handler.dataimport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConfigParseUtil {
    public static String getStringAttribute(Element e, String name, String def) {
        String r = e.getAttribute(name);
        if (r == null || "".equals(r.trim()))
            r = def;
        return r;
    }

    public static HashMap<String, String> getAllAttributes(Element e) {
        HashMap<String, String> m = new HashMap<String, String>();
        NamedNodeMap nnm = e.getAttributes();
        for (int i = 0; i < nnm.getLength(); i++) {
            m.put(nnm.item(i).getNodeName(), nnm.item(i).getNodeValue());
        }
        return m;
    }

    public static String getText(Node elem, StringBuilder buffer) {
        if (elem.getNodeType() != Node.CDATA_SECTION_NODE) {
            NodeList childs = elem.getChildNodes();
            for (int i = 0; i < childs.getLength(); i++) {
                Node child = childs.item(i);
                short childType = child.getNodeType();
                if (childType != Node.COMMENT_NODE
                        && childType != Node.PROCESSING_INSTRUCTION_NODE) {
                    getText(child, buffer);
                }
            }
        } else {
            buffer.append(elem.getNodeValue());
        }

        return buffer.toString();
    }

    public static List<Element> getChildNodes(Element e, String byName) {
        List<Element> result = new ArrayList<Element>();
        NodeList l = e.getChildNodes();
        for (int i = 0; i < l.getLength(); i++) {
            if (e.equals(l.item(i).getParentNode())
                    && byName.equals(l.item(i).getNodeName()))
                result.add((Element) l.item(i));
        }
        return result;
    }
}
