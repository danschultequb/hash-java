package qub;

public interface HashFunctionByteWriteStreamTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(HashFunctionByteWriteStream.class, () ->
        {
            runner.testGroup("create(ByteWriteStream,HashFunction)", () ->
            {
                runner.test("with null innerStream", (Test test) ->
                {
                    try (final HashFunction md5 = HashFunction.createMD5().await())
                    {
                        test.assertThrows(() -> HashFunctionByteWriteStream.create(null, md5),
                            new PreConditionFailure("innerStream cannot be null."));
                    }
                });

                runner.test("with null hashFunction", (Test test) ->
                {
                    final InMemoryByteStream innerStream = InMemoryByteStream.create().endOfStream();
                    test.assertThrows(() -> HashFunctionByteWriteStream.create(innerStream, (HashFunction)null),
                        new PreConditionFailure("hashFunction cannot be null."));
                });

                runner.test("with non-null arguments", (Test test) ->
                {
                    final InMemoryByteStream innerStream = InMemoryByteStream.create().endOfStream();
                    try (final HashFunction md5 = HashFunction.createMD5().await())
                    {
                        final HashFunctionByteWriteStream stream = HashFunctionByteWriteStream.create(innerStream, md5);
                        test.assertNotNull(stream);
                        test.assertFalse(stream.isDisposed());
                        test.assertEqual("D41D8CD98F00B204E9800998ECF8427E", stream.takeDigest().toHexString());
                    }
                });
            });

            runner.testGroup("create(ByteWriteStream,Function0<Result<? extends HashFunction>>)", () ->
            {
                runner.test("with null innerStream", (Test test) ->
                {
                    test.assertThrows(() -> HashFunctionByteWriteStream.create(null, HashFunction::createMD5),
                        new PreConditionFailure("innerStream cannot be null."));
                });

                runner.test("with null hashFunctionCreator", (Test test) ->
                {
                    final InMemoryByteStream innerStream = InMemoryByteStream.create().endOfStream();
                    test.assertThrows(() -> HashFunctionByteWriteStream.create(innerStream, (Function0<Result<? extends HashFunction>>)null),
                        new PreConditionFailure("hashFunctionCreator cannot be null."));
                });

                runner.test("with non-null arguments", (Test test) ->
                {
                    final InMemoryByteStream innerStream = InMemoryByteStream.create().endOfStream();
                    try (final HashFunctionByteWriteStream stream = HashFunctionByteWriteStream.create(innerStream, HashFunction::createMD5).await())
                    {
                        test.assertNotNull(stream);
                        test.assertFalse(stream.isDisposed());
                        test.assertEqual("D41D8CD98F00B204E9800998ECF8427E", stream.takeDigest().toHexString());
                    }
                });

                runner.test("with hasFunctionCreator that returns null", (Test test) ->
                {
                    final InMemoryByteStream innerStream = InMemoryByteStream.create().endOfStream();
                    test.assertThrows(() -> HashFunctionByteWriteStream.create(innerStream, () -> { return Result.success(null); }).await(),
                        new PreConditionFailure("hashFunction cannot be null."));
                });

                runner.test("with hasFunctionCreator that throws an exception", (Test test) ->
                {
                    final InMemoryByteStream innerStream = InMemoryByteStream.create().endOfStream();
                    test.assertThrows(() -> HashFunctionByteWriteStream.create(innerStream, () -> { return Result.error(new NotFoundException("blah")); }).await(),
                        new NotFoundException("blah"));
                });
            });

            runner.testGroup("createMD5(ByteWriteStream)", () ->
            {
                runner.test("with null innerStream", (Test test) ->
                {
                    test.assertThrows(() -> HashFunctionByteWriteStream.createMD5(null),
                        new PreConditionFailure("innerStream cannot be null."));
                });

                runner.test("with non-null innerStream", (Test test) ->
                {
                    final InMemoryByteStream innerStream = InMemoryByteStream.create().endOfStream();
                    try (final HashFunctionByteWriteStream stream = HashFunctionByteWriteStream.createMD5(innerStream).await())
                    {
                        test.assertNotNull(stream);
                        test.assertFalse(stream.isDisposed());
                        test.assertEqual("D41D8CD98F00B204E9800998ECF8427E", stream.takeDigest().toHexString());
                    }
                });
            });

            runner.testGroup("createSHA1(ByteWriteStream)", () ->
            {
                runner.test("with null innerStream", (Test test) ->
                {
                    test.assertThrows(() -> HashFunctionByteWriteStream.createSHA1(null),
                        new PreConditionFailure("innerStream cannot be null."));
                });

                runner.test("with non-null innerStream", (Test test) ->
                {
                    final InMemoryByteStream innerStream = InMemoryByteStream.create().endOfStream();
                    try (final HashFunctionByteWriteStream stream = HashFunctionByteWriteStream.createSHA1(innerStream).await())
                    {
                        test.assertNotNull(stream);
                        test.assertFalse(stream.isDisposed());
                        test.assertEqual("DA39A3EE5E6B4B0D3255BFEF95601890AFD80709", stream.takeDigest().toHexString());
                    }
                });
            });

            runner.testGroup("createSHA256(ByteWriteStream)", () ->
            {
                runner.test("with null innerStream", (Test test) ->
                {
                    test.assertThrows(() -> HashFunctionByteWriteStream.createSHA256(null),
                        new PreConditionFailure("innerStream cannot be null."));
                });

                runner.test("with non-null innerStream", (Test test) ->
                {
                    final InMemoryByteStream innerStream = InMemoryByteStream.create().endOfStream();
                    try (final HashFunctionByteWriteStream stream = HashFunctionByteWriteStream.createSHA256(innerStream).await())
                    {
                        test.assertNotNull(stream);
                        test.assertFalse(stream.isDisposed());
                        test.assertEqual("E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855", stream.takeDigest().toHexString());
                    }
                });
            });

            runner.testGroup("write(byte)", () ->
            {
                final Action2<byte[],String> writeByteTest = (byte[] bytes, String expected) ->
                {
                    runner.test("with " + Array.toString(bytes), (Test test) ->
                    {
                        final InMemoryByteStream innerStream = InMemoryByteStream.create();
                        try (final HashFunctionByteWriteStream stream = HashFunctionByteWriteStream.createMD5(innerStream).await())
                        {
                            for (int i = 0; i < bytes.length; i++)
                            {
                                test.assertEqual(1, stream.write(bytes[i]).await());
                            }

                            test.assertEqual(bytes, innerStream.getBytes());
                            test.assertEqual(expected, stream.takeDigest().toHexString());
                        }
                    });
                };

                writeByteTest.run(new byte[0], "D41D8CD98F00B204E9800998ECF8427E");
                writeByteTest.run(new byte[] { 5 }, "8BB6C17838643F9691CC6A4DE6C51709");
                writeByteTest.run(new byte[] { 1, 2, 3 }, "5289DF737DF57326FCDD22597AFB1FAC");
                writeByteTest.run(new byte[1024], "0F343B0931126A20F133D67C2B018A3B");
            });

            runner.testGroup("write(byte[],int,int)", () ->
            {
                final Action2<byte[],String> writeBytesTest = (byte[] bytes, String expected) ->
                {
                    runner.test("with " + Array.toString(bytes), (Test test) ->
                    {
                        final InMemoryByteStream innerStream = InMemoryByteStream.create();
                        try (final HashFunctionByteWriteStream stream = HashFunctionByteWriteStream.createMD5(innerStream).await())
                        {
                            int startIndex = 0;
                            int length = Math.minimum(10, bytes.length - startIndex);
                            while (startIndex < bytes.length)
                            {
                                final int bytesWritten = stream.write(bytes, startIndex, length).await();
                                test.assertEqual(length, bytesWritten);

                                startIndex += bytesWritten;
                                length = Math.minimum(10, bytes.length - startIndex);
                            }

                            test.assertEqual(bytes, innerStream.getBytes());
                            test.assertEqual(expected, stream.takeDigest().toHexString());
                        }
                    });
                };

                writeBytesTest.run(new byte[0], "D41D8CD98F00B204E9800998ECF8427E");
                writeBytesTest.run(new byte[] { 5 }, "8BB6C17838643F9691CC6A4DE6C51709");
                writeBytesTest.run(new byte[] { 1, 2, 3 }, "5289DF737DF57326FCDD22597AFB1FAC");
                writeBytesTest.run(new byte[1024], "0F343B0931126A20F133D67C2B018A3B");
            });

            runner.test("dispose()", (Test test) ->
            {
                final InMemoryByteStream innerStream = InMemoryByteStream.create().endOfStream();
                try (final HashFunction md5 = HashFunction.createMD5().await())
                {
                    try (final HashFunctionByteWriteStream stream = HashFunctionByteWriteStream.create(innerStream, md5))
                    {
                        test.assertTrue(stream.dispose().await());
                        test.assertTrue(stream.isDisposed());
                        test.assertTrue(md5.isDisposed());
                        test.assertTrue(innerStream.isDisposed());

                        test.assertFalse(stream.dispose().await());
                        test.assertTrue(stream.isDisposed());
                        test.assertTrue(md5.isDisposed());
                        test.assertTrue(innerStream.isDisposed());
                    }
                }
            });
        });
    }
}
