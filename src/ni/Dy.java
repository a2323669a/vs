package ni;

import java.util.ArrayList;
import java.util.List;

public class Dy {
	public static List<Interval> INTERVALS = new ArrayList<Interval>();
	static {
		INTERVALS.add(new Interval(1, 5, 5));
		INTERVALS.add(new Interval(2, 3, 3));
		INTERVALS.add(new Interval(4, 8, 6));
		INTERVALS.add(new Interval(6, 12, 10));
		INTERVALS.add(new Interval(7, 17, 12));
		INTERVALS.add(new Interval(9, 10, 1));
		INTERVALS.add(new Interval(11, 15, 7));
		INTERVALS.add(new Interval(13, 14, 0));
		INTERVALS.add(new Interval(16, 18, 4));
	}
	private static int imax = 0;
	private static List<Interval> maxList = new ArrayList<Interval>();

	public static void main(String[] args) {
		List<Interval> ins = new ArrayList<Interval>();
		dy(ins, 0);
		System.out.println("max weight : "+imax);
		for(Interval i : maxList){
			System.out.println("start : "+i.start + " , "+"end : "+i.end+ " , "+"weight : "+i.weight);
		}

	}

	public static void dy(List<Interval> list, int i) {
		if (i < INTERVALS.size()) {
			dy(list, i + 1);

			if (!isCover(INTERVALS.get(i), list)) {
				list.add(INTERVALS.get(i));
				dy(list, i + 1);
				list.remove(INTERVALS.get(i));
			}
		} else {
			if (imax < max(list)) {
				maxList.clear();
				imax = max(list);
				maxList.addAll(list);
			}
		}
	}

	public static int max(List<Interval> in) {
		int result = 0;
		for (Interval i : in) {
			result += i.weight;
		}
		return result;
	}

	public static boolean isCover(Interval inte, List<Interval> in) {
		for (Interval i : in) {
			if (i.start < inte.end && i.end > inte.start) {
				return true;
			}
			if (i.end > inte.start && i.end < inte.end) {
				return true;
			}
		}

		return false;
	}

}

class Interval {
	public int start;
	public int end;
	public int weight;

	public Interval(int start, int end, int weight) {
		this.start = start;
		this.end = end;
		this.weight = weight;
	}

	@Override
	public boolean equals(Object o) {
		Interval i = (Interval) o;
		if (i.start == start && i.end == end && i.weight == weight) {
			return true;
		}
		return false;
	}
}