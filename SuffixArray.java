import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

public class SuffixArray {

	private static final boolean DEBUG = false;

	/** contains suffix array indexes including sentinel */
	int[] suffixArray;

	private SuffixArray(String str) {
		int[] S = new int[str.length() + 1];
		int maxChar = 0;
		for (int i = 0; i < str.length(); ++i) {
			S[i] = str.charAt(i);
			maxChar = Math.max(maxChar, S[i]);
		}
		S[str.length()] = 0; // sentinel
		this.suffixArray = rec(S, maxChar);
	}

	private int[] rec(int[] S, int maxChar) {
		int N = S.length;
		BitSet isStype = new BitSet(N);
		isStype.set(N - 1);
		for (int i = N - 2; i >= 0; --i) {
			if (S[i] == S[i + 1]) {
				isStype.set(i, isStype.get(i + 1));
			} else {
				isStype.set(i, S[i] < S[i + 1]);
			}
		}
		ArrayList<Integer> lmsIndex = new ArrayList<>();
		for (int i = 0; i < N - 1; ++i) {
			if (!isStype.get(i) && isStype.get(i + 1)) lmsIndex.add(i + 1);
		}
		if (DEBUG) System.err.println("P:" + lmsIndex);
		int[] SA = new int[N];
		Arrays.fill(SA, -1);

		// induced sort for LMS strings
		int[] bucketTail = getBucketTail(S, maxChar);
		int[] bucketPos = bucketTail.clone();
		for (int i = 0; i < lmsIndex.size(); ++i) {
			int p = lmsIndex.get(i);
			int firstChar = S[p];
			bucketPos[firstChar]--;
			SA[bucketPos[firstChar]] = p;
		}
		inducedSort(S, isStype, SA, bucketTail.clone());

		// rename (reuse SA as buffer to sort P)
		int[] S1 = new int[lmsIndex.size()];
		int n1 = 1;
		for (int i = 1; i < N; ++i) {
			if (isLMS(isStype, SA[i])) {
				SA[n1++] = SA[i];
			}
		}
		assert (n1 == lmsIndex.size());
		Arrays.fill(SA, n1, N, -1);
		int name = 0;
		for (int i = 0; i < n1; ++i) {
			if (i != 0 && !isEqualLMS(S, isStype, SA[i - 1], SA[i])) ++name;
			SA[n1 + SA[i] / 2] = name;
		}
		for (int i = N - 1, j = n1; j > 0; --i) {
			if (SA[i] != -1) S1[--j] = SA[i];
		}
		if (DEBUG) System.err.println("S1:" + Arrays.toString(S1) + " name:" + name + " n1:" + n1);

		int[] SA1;
		if (name + 1 == n1) {
			// build SA directly
			SA1 = new int[n1];
			for (int i = 0; i < n1; ++i) {
				SA1[S1[i]] = i;
			}
		} else {
			// recursive call
			SA1 = rec(S1, name);
		}
		if (DEBUG) System.err.println("SA1:" + Arrays.toString(SA1));

		// induce whole SA from sorted LMS strings
		Arrays.fill(SA, -1);
		bucketPos = bucketTail.clone();
		for (int i = SA1.length - 1; i >= 0; --i) {
			int p = lmsIndex.get(SA1[i]);
			int firstChar = S[p];
			bucketPos[firstChar]--;
			SA[bucketPos[firstChar]] = p;
		}
		inducedSort(S, isStype, SA, bucketTail.clone());
		if (DEBUG) System.err.println("SA:" + Arrays.toString(SA));

		return SA;
	}

	private void inducedSort(int[] S, BitSet isStype, int[] SA, int[] bucketTail) {
		// step 2
		int[] bucketHead = new int[bucketTail.length];
		for (int i = 1; i < bucketHead.length; ++i) {
			bucketHead[i] = bucketTail[i - 1];
		}
		int N = S.length;
		for (int i = 0; i < N; ++i) {
			if (SA[i] <= 0) continue;
			if (!isStype.get(SA[i] - 1)) {
				int firstChar = S[SA[i] - 1];
				SA[bucketHead[firstChar]] = SA[i] - 1;
				bucketHead[firstChar]++;
			}
		}

		// step 3
		for (int i = N - 1; i >= 0; --i) {
			if (SA[i] <= 0) continue;
			if (isStype.get(SA[i] - 1)) {
				int firstChar = S[SA[i] - 1];
				bucketTail[firstChar]--;
				SA[bucketTail[firstChar]] = SA[i] - 1;
			}
		}
	}

	private boolean isEqualLMS(int[] S, BitSet isStype, int p1, int p2) {
		for (int i = 0;; ++i) {
			if (S[p1 + i] != S[p2 + i]) return false;
			if (isStype.get(p1 + i) != isStype.get(p2 + i)) return false;
			if (i > 0) {
				if (isLMS(isStype, p1 + i)) {
					return isLMS(isStype, p2 + i);
				}
				if (isLMS(isStype, p2 + i)) return false;
			}
		}
	}

	private static int[] getBucketTail(int[] S, int maxChar) {
		int[] bucketTail = new int[maxChar + 1];
		for (int i = 0; i < S.length; ++i) {
			bucketTail[S[i]]++;
		}
		for (int i = 1; i <= maxChar; ++i) {
			bucketTail[i] += bucketTail[i - 1];
		}
		return bucketTail;
	}

	private static boolean isLMS(BitSet isStype, int p) {
		if (p == 0) return false;
		return isStype.get(p) && !isStype.get(p - 1);
	}

	/**
	 *  create Suffix Array using SA-IS algorithm
	 *  @param str
	 *         target string
	 */
	public static SuffixArray build(String str) {
		return new SuffixArray(str);
	}

	public static int[] buildNaive(String str) {
		final int[] S = new int[str.length() + 1];
		for (int i = 0; i < str.length(); ++i) {
			S[i] = str.charAt(i);
		}
		S[str.length()] = 0; // sentinel
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
		SuffixArray sa = SuffixArray.build(str);
		int[] naive = SuffixArray.buildNaive(str);
		if (!Arrays.equals(sa.suffixArray, naive)) {
			System.out.println(Arrays.toString(sa.suffixArray));
			System.out.println(Arrays.toString(naive));
			System.out.println("fail");
		}
		for (int i = 0; i < sa.suffixArray.length; ++i) {
			System.out.printf("%2d %s\n", sa.suffixArray[i], str.substring(sa.suffixArray[i]));
		}
	}

}
