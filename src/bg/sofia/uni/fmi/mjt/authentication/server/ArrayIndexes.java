package bg.sofia.uni.fmi.mjt.authentication.server;

public enum ArrayIndexes {
	INDEX_0(0), INDEX_1(1), INDEX_2(2), INDEX_3(3), INDEX_4(4), INDEX_5(5), INDEX_6(6), INDEX_7(7);

	private int value;

	ArrayIndexes(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
