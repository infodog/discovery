package net.xinshi.discovery.search.client.query;

import org.apache.commons.lang.StringUtils;

public class PiceneTextQuery extends Query {

	private static final long serialVersionUID = 1L;
	private String field;
	private String keyword;

	public PiceneTextQuery(String field, String keyword) {
		super();
		this.field = field;
		this.keyword = keyword;
	}

	@Override
	public String toString(String field) {
		StringBuilder buffer = new StringBuilder();
        //_query_:"{!dismax qf=myfield}how now brown cow"
       /* buffer.append("_query_:");
        buffer.append("\"");
        buffer.append("{!picene df=");
		buffer.append(this.field);
        buffer.append("}");
		buffer.append(keyword);
        buffer.append("\"");*/
		//buffer.append(getBoost());

		String[] words = StringUtils.split(this.keyword," ");
		int n = 0;
//		buffer.append(this.field + ":");
		for(String word : words){
			if (StringUtils.isNotBlank(word)) {
				if(n==0) {
					buffer.append(this.field + ":\"" + word + "\"");
				}
				else{
					buffer.append(" AND "+ this.field + ":\""+ word + "\"");
				}
				n++;
			}
		}
		//buffer.append(this.field + ":\""+this.keyword + "\"");
		return buffer.toString();
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
}
