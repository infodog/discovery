package net.xinshi.discovery.search.client.services;

public class SumArg {
	public static final String SUM = "sum";
	public static final String COUNT = "count";
	public static final String DISTINCT_COUNT = "dcount";
	
	private String fieldName;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;
	
	//The minimum value of the value set
	private long min = Long.MIN_VALUE;
	
	//The maximum value of the value set
	private long max = Long.MAX_VALUE;
	
	public long getMin() {
		return min;
	}

	public void setMin(long min) {
		this.min = min;
	}

	public long getMax() {
		return max;
	}

	public void setMax(long max) {
		this.max = max;
	}
	
	
	public SumArg(String fieldName, String type) {
		super();
		this.fieldName = fieldName;
		this.type = type;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
}
