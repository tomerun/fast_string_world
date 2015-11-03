import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

public class Entropy {

	private static final char SENTINEL = '$';

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("usage: java Entropy string [k]");
			System.exit(1);
		}
		String str = args[0];
		int k = -1;
		if (args.length > 1) {
			try {
				k = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				System.out.println("k must be an integer.");
				System.exit(1);
			}
		}
		if (k != -1) {
			double e = k == 0 ? H_0(str) : H_k(str, k);
			System.out.println(k + "-th entropy:" + e);
		} else {
			System.out.println(0 + "-th entropy:" + H_0(str));
			for (k = 1; k < Math.min(100, str.length()); ++k) {
				System.out.println(k + "-th entropy:" + H_k(str, k));
			}
		}
	}

	private static double H_0(String str) {
		HashMap<Character, Integer> hist = new HashMap<>();
		for (int i = 0; i < str.length(); ++i) {
			if (hist.containsKey(str.charAt(i))) {
				hist.put(str.charAt(i), hist.get(str.charAt(i)) + 1);
			} else {
				hist.put(str.charAt(i), 1);
			}
		}
		double ret = 0;
		for (Entry<Character, Integer> e : hist.entrySet()) {
			double ratio = 1.0 * e.getValue() / str.length();
			ret += -1.0 * ratio * Math.log(ratio) / Math.log(2);
		}
		return ret;
	}

	private static double H_k(String str, int k) {
		int N = str.length();
		char[] sentinel = new char[k];
		Arrays.fill(sentinel, SENTINEL);
		str += String.valueOf(sentinel);
		HashMap<String, String> preStr = new HashMap<>();
		for (int i = 0; i < N; ++i) {
			String substr = str.substring(i + 1, i + 1 + k);
			char before = str.charAt(i);
			if (preStr.containsKey(substr)) {
				preStr.put(substr, preStr.get(substr) + before);
			} else {
				preStr.put(substr, "" + before);
			}
		}

		double ret = 0;
		for (String pre : preStr.values()) {
			ret += 1.0 * pre.length() / N * H_0(pre);
		}
		return ret;
	}
}
