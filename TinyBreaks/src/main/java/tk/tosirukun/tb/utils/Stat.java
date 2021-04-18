package tk.tosirukun.tb.utils;

public class Stat {
	private long block;
	private long point;
	private int before;
	public void setProperties(long block, long point) {
		this.block = block;
		this.point = point;
	}

	public long getBlock() {
		return block;
	}

	public void setBlock(long block) {
		this.block = block;
	}

	public long getPoint() {
		return point;
	}

	public void setPoint(long point) {
		this.point = point;
	}

	public int getBefore() {
		return before;
	}

	public void setBefore(int before) {
		this.before = before;
	}
}