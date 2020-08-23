package example.sequence;

public class SequenceInfo {
	String key;
	long seqLength = 0;
	long lastNum = 0;
	long total = 0;
	
	public SequenceInfo(String key) {
		this.key = key;
	}
	
	public void add(long num) {
		if (num != lastNum + 1) {
			System.out.println("Missing sequence number. Found " + num + " last was " + lastNum);
		}
		lastNum = num;
		total += num;
		seqLength++;
	}
        public void add(String num) {
	
	   System.out.println(" Found " + num );
	
	}
	public long genNext() {
		long next = lastNum + 1;
		lastNum = next;
		seqLength++;
		total += next;
		return next;
	}
	
	public long getAverage() {
		return total / seqLength;
	}
	
	public String toString() {
		return "SequenceInfo: (key=" + key + ") (average = " + getAverage() + ") (seq length = " + seqLength + ")";
	}
};