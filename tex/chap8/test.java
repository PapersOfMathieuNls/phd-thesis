public class {% SUT %}_{% BUG %} extends TestCase {

	private static final String failure = {% CRASH_STACK %};
	private static final int threshold = {% THRESHOLD %};
	private static int differences = Integer.MAX_VALUE;
	private static final StringTokenizer tokenizerFailure =
		new StringTokenizer(failure, "\n");

	@Test
	public test{% SUT %}() {
		try {
			{% STEPS %}
		} catch (Exception e) {
			 // Count the differences
		}
		assertTrue(differences <= tokenizeOriginalFailure
			.countTokens() / 100 * (threshold-100));
	}
}
