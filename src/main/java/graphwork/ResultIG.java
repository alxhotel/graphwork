package graphwork;

public class ResultIG extends Result {
	
	public Float percentage;
	public Integer tries;
	
	public ResultIG() {
		
	}
	
	public ResultIG(Float weight, Long time, Float percentage, Integer tries) {
		super(weight, time);
		this.percentage = percentage;
		this.tries = tries;
	}
	
}
