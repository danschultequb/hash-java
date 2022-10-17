package qub;

public interface JavaMessageDigestHashFunctionTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(JavaMessageDigestHashFunction.class, () ->
        {
            runner.testGroup("create(String)", () ->
            {
                final Action2<String,Throwable> createErrorTest = (String algorithm, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(algorithm), (Test test) ->
                    {
                        test.assertThrows(() -> JavaMessageDigestHashFunction.create(algorithm).await(),
                            expected);
                    });
                };

                createErrorTest.run(null, new PreConditionFailure("algorithm cannot be null."));
                createErrorTest.run("", new PreConditionFailure("algorithm cannot be empty."));
                createErrorTest.run("spam", new java.security.NoSuchAlgorithmException("spam MessageDigest not available"));
                createErrorTest.run("md-5", new java.security.NoSuchAlgorithmException("md-5 MessageDigest not available"));

                final Action1<String> createTest = (String algorithm) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(algorithm), (Test test) ->
                    {
                        final JavaMessageDigestHashFunction hashFunction = JavaMessageDigestHashFunction.create(algorithm).await();
                        test.assertNotNull(hashFunction);
                        test.assertEqual(algorithm, hashFunction.getAlgorithm());
                    });
                };

                createTest.run("MD5");
                createTest.run("SHA-1");
                createTest.run("SHA-256");

                createTest.run("md5");
                createTest.run("sha1");
                createTest.run("SHA1");
                createTest.run("Sha1");
                createTest.run("sha256");
                createTest.run("SHA256");
                createTest.run("sHa256");
            });

            HashFunctionTests.test(runner, () -> JavaMessageDigestHashFunction.create("MD5").await());
        });
    }
}
