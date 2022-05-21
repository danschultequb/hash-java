package qub;

public interface HashFunctionTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(HashFunction.class, () ->
        {
            runner.test("createMD5()", (Test test) ->
            {
                final HashFunction md5 = HashFunction.createMD5().await();
                test.assertNotNull(md5);
                test.assertEqual("D41D8CD98F00B204E9800998ECF8427E", md5.takeDigest().toHexString());
                test.assertEqual("D41D8CD98F00B204E9800998ECF8427E", md5.takeDigest(new byte[0]).toHexString());
                test.assertEqual("8BB6C17838643F9691CC6A4DE6C51709", md5.takeDigest((byte)5).toHexString());
                test.assertEqual("5289DF737DF57326FCDD22597AFB1FAC", md5.takeDigest(new byte[] { 1, 2, 3 }).toHexString());
                test.assertEqual("5289DF737DF57326FCDD22597AFB1FAC", md5.takeDigest(new byte[] { 0, 1, 2, 3, 4 }, 1, 3).toHexString());
                test.assertEqual("0F343B0931126A20F133D67C2B018A3B", md5.takeDigest(new byte[1024]).toHexString());
                test.assertEqual("0CC175B9C0F1B6A831C399E269772661", md5.takeDigest(CharacterEncoding.US_ASCII.encodeCharacter('a').await()).toHexString());
                test.assertEqual("900150983CD24FB0D6963F7D28E17F72", md5.takeDigest(CharacterEncoding.US_ASCII.encodeCharacters("abc").await()).toHexString());
                test.assertEqual("F96B697D7CB7938D525A2F31AAF161D0", md5.takeDigest(CharacterEncoding.US_ASCII.encodeCharacters("message digest").await()).toHexString());
            });

            runner.test("createSHA1()", (Test test) ->
            {
                final HashFunction sha1 = HashFunction.createSHA1().await();
                test.assertNotNull(sha1);
                test.assertEqual("DA39A3EE5E6B4B0D3255BFEF95601890AFD80709", sha1.takeDigest().toHexString());
                test.assertEqual("DA39A3EE5E6B4B0D3255BFEF95601890AFD80709", sha1.takeDigest(new byte[0]).toHexString());
                test.assertEqual("8DC00598417D4EB788A77AC6CCEF3CB484905D8B", sha1.takeDigest((byte)5).toHexString());
                test.assertEqual("7037807198C22A7D2B0807371D763779A84FDFCF", sha1.takeDigest(new byte[] { 1, 2, 3 }).toHexString());
                test.assertEqual("7037807198C22A7D2B0807371D763779A84FDFCF", sha1.takeDigest(new byte[] { 0, 1, 2, 3, 4 }, 1, 3).toHexString());
                test.assertEqual("60CACBF3D72E1E7834203DA608037B1BF83B40E8", sha1.takeDigest(new byte[1024]).toHexString());
                test.assertEqual("86F7E437FAA5A7FCE15D1DDCB9EAEAEA377667B8", sha1.takeDigest(CharacterEncoding.US_ASCII.encodeCharacter('a').await()).toHexString());
                test.assertEqual("A9993E364706816ABA3E25717850C26C9CD0D89D", sha1.takeDigest(CharacterEncoding.US_ASCII.encodeCharacters("abc").await()).toHexString());
                test.assertEqual("C12252CEDA8BE8994D5FA0290A47231C1D16AAE3", sha1.takeDigest(CharacterEncoding.US_ASCII.encodeCharacters("message digest").await()).toHexString());
            });

            runner.test("createSHA256()", (Test test) ->
            {
                final HashFunction sha256 = HashFunction.createSHA256().await();
                test.assertNotNull(sha256);
                test.assertEqual("E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855", sha256.takeDigest().toHexString());
                test.assertEqual("E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855", sha256.takeDigest(new byte[0]).toHexString());
                test.assertEqual("E77B9A9AE9E30B0DBDB6F510A264EF9DE781501D7B6B92AE89EB059C5AB743DB", sha256.takeDigest((byte)5).toHexString());
                test.assertEqual("039058C6F2C0CB492C533B0A4D14EF77CC0F78ABCCCED5287D84A1A2011CFB81", sha256.takeDigest(new byte[] { 1, 2, 3 }).toHexString());
                test.assertEqual("039058C6F2C0CB492C533B0A4D14EF77CC0F78ABCCCED5287D84A1A2011CFB81", sha256.takeDigest(new byte[] { 0, 1, 2, 3, 4 }, 1, 3).toHexString());
                test.assertEqual("5F70BF18A086007016E948B04AED3B82103A36BEA41755B6CDDFAF10ACE3C6EF", sha256.takeDigest(new byte[1024]).toHexString());
                test.assertEqual("CA978112CA1BBDCAFAC231B39A23DC4DA786EFF8147C4E72B9807785AFEE48BB", sha256.takeDigest(CharacterEncoding.US_ASCII.encodeCharacter('a').await()).toHexString());
                test.assertEqual("BA7816BF8F01CFEA414140DE5DAE2223B00361A396177A9CB410FF61F20015AD", sha256.takeDigest(CharacterEncoding.US_ASCII.encodeCharacters("abc").await()).toHexString());
                test.assertEqual("F7846F55CF23E14EEBEAB5B4E1550CAD5B509E3348FBC4EFA3A1413D393CB650", sha256.takeDigest(CharacterEncoding.US_ASCII.encodeCharacters("message digest").await()).toHexString());
            });
        });
    }

    public static void test(TestRunner runner, Function0<? extends HashFunction> creator)
    {
        runner.testGroup(HashFunction.class, () ->
        {
            runner.testGroup("addBytes(byte[])", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    try (final HashFunction function = creator.run())
                    {
                        test.assertThrows(() -> function.addBytes(null),
                            new PreConditionFailure("values cannot be null."));
                    }
                });

                runner.test("when disposed", (Test test) ->
                {
                    try (final HashFunction function = creator.run())
                    {
                        function.dispose().await();

                        test.assertThrows(() -> function.addBytes(new byte[] { 1, 2, 3 }),
                            new PreConditionFailure("this.isDisposed() cannot be true."));
                    }
                });
            });

            runner.testGroup("addBytes(byte[],int,int)", () ->
            {
                final Action4<byte[],Integer,Integer,Throwable> addBytesErrorTest = (byte[] values, Integer startIndex, Integer length, Throwable expected) ->
                {
                    runner.test("with " + English.andList(Array.toString(values), startIndex, length), (Test test) ->
                    {
                        try (final HashFunction function = creator.run())
                        {
                            test.assertThrows(() -> function.addBytes(values, startIndex, length),
                                expected);
                        }
                    });
                };

                addBytesErrorTest.run(null, 0, 1, new PreConditionFailure("values cannot be null."));
                addBytesErrorTest.run(new byte[0], 1, 0, new PreConditionFailure("startIndex (1) must be equal to 0."));
                addBytesErrorTest.run(new byte[] { 1, 2, 3 }, -1, 0, new PreConditionFailure("startIndex (-1) must be between 0 and 2."));
                addBytesErrorTest.run(new byte[] { 1, 2, 3 }, 4, 0, new PreConditionFailure("startIndex (4) must be between 0 and 2."));
                addBytesErrorTest.run(new byte[] { 1, 2, 3 }, 1, -1, new PreConditionFailure("length (-1) must be between 0 and 2."));
                addBytesErrorTest.run(new byte[] { 1, 2, 3 }, 1, 3, new PreConditionFailure("length (3) must be between 0 and 2."));
            });
        });
    }
}
