public class BWT {

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("usage: java BWT string");
			System.exit(1);
		}
		String str = args[0];
		SuffixArray sa = SuffixArray.build(str);
		char[] transformed = new char[str.length()];
		int pos = 0;
		for (int i = 0; i < sa.suffixArray.length; ++i) {
			if (sa.suffixArray[i] == 0) continue;
			transformed[pos++] = str.charAt(sa.suffixArray[i] - 1);
		}
		System.out.println(String.valueOf(transformed));
	}
}
