package tk.tosirukun.tb.utils;

public class Stat {
	private int level;
	private int moon;

	private long block;

	private long max_block;

	private long point;

	private String rank;

	public void setProperties(int level, int moon, long block, long max_block, String rank) {
		this.level = level;
		this.moon = moon;
		this.block = block;
		this.max_block = max_block;
		this.rank = rank;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getMoon() {
		return moon;
	}

	public void setMoon(int moon) {
		this.moon = moon;
	}

	public long getBlock() {
		return block;
	}

	public void setBlock(long block) {
		this.block = block;
	}

	public long getMax_block() {
		return max_block;
	}

	public void setMax_block(long max_block) {
		this.max_block = max_block;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public long getPoint() {
		return point;
	}

	public void setPoint(long point) {
		this.point = point;
	}

}
