import java.util.Arrays;
import java.util.BitSet;

public class SuffixArray {

	int[] SA;
	private BitSet T;

	private SuffixArray(int[] S) {
		this.SA = S;
	}
	
	public static SuffixArray build(String str) {
		int[] sa = buildNaive(str);
		return new SuffixArray(sa);
		
//		int[] S = new int[str.length() + 1];
//		for (int i = 0; i < str.length(); ++i) {
//			S[i] = str.charAt(i);
//		}
//		S[str.length()] = -1;
//		return new SuffixArray(S);
	}

	public static int[] buildNaive(String str) {
		final int[] S = new int[str.length() + 1];
		for (int i = 0; i < str.length(); ++i) {
			S[i] = str.charAt(i);
		}
		S[str.length()] = -1;
		Integer[] SA = new Integer[S.length];
		for (int i = 0; i < SA.length; ++i) {
			SA[i] = i;
		}
		Arrays.sort(SA, (Integer i, Integer j) -> {
			for (int pos = 0;; ++pos) {
				if (S[i + pos] != S[j + pos]) return Integer.compare(S[i + pos], S[j + pos]);
			}
		});

		int[] ret = new int[SA.length];
		for (int i = 0; i < ret.length; ++i) {
			ret[i] = SA[i];
		}
		return ret;
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("usage: java SuffixArray string");
			System.exit(1);
		}
		String str = args[0];
		int[] sa = SuffixArray.buildNaive(str);
		for (int i = 0; i < sa.length; ++i) {
			System.out.printf("%2d %s\n", sa[i], str.substring(sa[i]));
		}
	}

}
