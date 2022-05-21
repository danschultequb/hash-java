package qub;

public interface HashFunctionByteReadStreamTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(HashFunctionByteReadStream.class, () ->
        {
            runner.testGroup("create(ByteReadStream,HashFunction)", () ->
            {
                runner.test("with null innerStream", (Test test) ->
                {
                    try (final HashFunction md5 = HashFunction.createMD5().await())
                    {
                        test.assertThrows(() -> HashFunctionByteReadStream.create(null, md5),
                            new PreConditionFailure("innerStream cannot be null."));
                    }
                });

                runner.test("with null hashFunction", (Test test) ->
                {
                    final InMemoryByteStream innerStream = InMemoryByteStream.create().endOfStream();
                    test.assertThrows(() -> HashFunctionByteReadStream.create(innerStream, (HashFunction)null),
                        new PreConditionFailure("hashFunction cannot be null."));
                });

                runner.test("with non-null arguments", (Test test) ->
                {
                    final InMemoryByteStream innerStream = InMemoryByteStream.create().endOfStream();
                    try (final HashFunction md5 = HashFunction.createMD5().await())
                    {
                        final HashFunctionByteReadStream stream = HashFunctionByteReadStream.create(innerStream, md5);
                        test.assertNotNull(stream);
                        test.assertFalse(stream.isDisposed());
                        test.assertEqual("D41D8CD98F00B204E9800998ECF8427E", stream.takeDigest().toHexString());
                    }
                });
            });

            runner.testGroup("create(ByteReadStream,Function0<Result<? extends HashFunction>>)", () ->
            {
                runner.test("with null innerStream", (Test test) ->
                {
                    test.assertThrows(() -> HashFunctionByteReadStream.create(null, HashFunction::createMD5),
                        new PreConditionFailure("innerStream cannot be null."));
                });

                runner.test("with null hashFunctionCreator", (Test test) ->
                {
                    final InMemoryByteStream innerStream = InMemoryByteStream.create().endOfStream();
                    test.assertThrows(() -> HashFunctionByteReadStream.create(innerStream, (Function0<Result<? extends HashFunction>>)null),
                        new PreConditionFailure("hashFunctionCreator cannot be null."));
                });

                runner.test("with non-null arguments", (Test test) ->
                {
                    final InMemoryByteStream innerStream = InMemoryByteStream.create().endOfStream();
                    try (final HashFunctionByteReadStream stream = HashFunctionByteReadStream.create(innerStream, HashFunction::createMD5).await())
                    {
                        test.assertNotNull(stream);
                        test.assertFalse(stream.isDisposed());
                        test.assertEqual("D41D8CD98F00B204E9800998ECF8427E", stream.takeDigest().toHexString());
                    }
                });

                runner.test("with hasFunctionCreator that returns null", (Test test) ->
                {
                    final InMemoryByteStream innerStream = InMemoryByteStream.create().endOfStream();
                    test.assertThrows(() -> HashFunctionByteReadStream.create(innerStream, () -> { return Result.success(null); }).await(),
                        new PreConditionFailure("hashFunction cannot be null."));
                });

                runner.test("with hasFunctionCreator that throws an exception", (Test test) ->
                {
                    final InMemoryByteStream innerStream = InMemoryByteStream.create().endOfStream();
                    test.assertThrows(() -> HashFunctionByteReadStream.create(innerStream, () -> { return Result.error(new NotFoundException("blah")); }).await(),
                        new NotFoundException("blah"));
                });
            });

            runner.testGroup("createMD5(ByteReadStream)", () ->
            {
                runner.test("with null innerStream", (Test test) ->
                {
                    test.assertThrows(() -> HashFunctionByteReadStream.createMD5(null),
                        new PreConditionFailure("innerStream cannot be null."));
                });

                runner.test("with non-null innerStream", (Test test) ->
                {
                    final InMemoryByteStream innerStream = InMemoryByteStream.create().endOfStream();
                    try (final HashFunctionByteReadStream stream = HashFunctionByteReadStream.createMD5(innerStream).await())
                    {
                        test.assertNotNull(stream);
                        test.assertFalse(stream.isDisposed());
                        test.assertEqual("D41D8CD98F00B204E9800998ECF8427E", stream.takeDigest().toHexString());
                    }
                });
            });

            runner.testGroup("createSHA1(ByteReadStream)", () ->
            {
                runner.test("with null innerStream", (Test test) ->
                {
                    test.assertThrows(() -> HashFunctionByteReadStream.createSHA1(null),
                        new PreConditionFailure("innerStream cannot be null."));
                });

                runner.test("with non-null innerStream", (Test test) ->
                {
                    final InMemoryByteStream innerStream = InMemoryByteStream.create().endOfStream();
                    try (final HashFunctionByteReadStream stream = HashFunctionByteReadStream.createSHA1(innerStream).await())
                    {
                        test.assertNotNull(stream);
                        test.assertFalse(stream.isDisposed());
                        test.assertEqual("DA39A3EE5E6B4B0D3255BFEF95601890AFD80709", stream.takeDigest().toHexString());
                    }
                });
            });

            runner.testGroup("createSHA256(ByteReadStream)", () ->
            {
                runner.test("with null innerStream", (Test test) ->
                {
                    test.assertThrows(() -> HashFunctionByteReadStream.createSHA256(null),
                        new PreConditionFailure("innerStream cannot be null."));
                });

                runner.test("with non-null innerStream", (Test test) ->
                {
                    final InMemoryByteStream innerStream = InMemoryByteStream.create().endOfStream();
                    try (final HashFunctionByteReadStream stream = HashFunctionByteReadStream.createSHA256(innerStream).await())
                    {
                        test.assertNotNull(stream);
                        test.assertFalse(stream.isDisposed());
                        test.assertEqual("E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855", stream.takeDigest().toHexString());
                    }
                });
            });

            runner.testGroup("readByte()", () ->
            {
                final Action2<byte[],String> readByteTest = (byte[] bytes, String expected) ->
                {
                    runner.test("with " + Array.toString(bytes), (Test test) ->
                    {
                        final InMemoryByteStream innerStream = InMemoryByteStream.create(bytes).endOfStream();
                        try (final HashFunctionByteReadStream stream = HashFunctionByteReadStream.createMD5(innerStream).await())
                        {
                            for (int i = 0; i < bytes.length; i++)
                            {
                                test.assertEqual(bytes[i], stream.readByte().await());
                            }

                            for (int i = 0; i < 2; i++)
                            {
                                test.assertThrows(() -> stream.readByte().await(),
                                    new EndOfStreamException());
                            }

                            test.assertEqual(expected, stream.takeDigest().toHexString());
                        }
                    });
                };

                readByteTest.run(new byte[0], "D41D8CD98F00B204E9800998ECF8427E");
                readByteTest.run(new byte[] { 5 }, "8BB6C17838643F9691CC6A4DE6C51709");
                readByteTest.run(new byte[] { 1, 2, 3 }, "5289DF737DF57326FCDD22597AFB1FAC");
                readByteTest.run(new byte[1024], "0F343B0931126A20F133D67C2B018A3B");
            });

            runner.testGroup("readBytes(byte[],int,int)", () ->
            {
                final Action2<byte[],String> readBytesTest = (byte[] bytes, String expected) ->
                {
                    runner.test("with " + Array.toString(bytes), (Test test) ->
                    {
                        final InMemoryByteStream innerStream = InMemoryByteStream.create(bytes).endOfStream();
                        try (final HashFunctionByteReadStream stream = HashFunctionByteReadStream.createMD5(innerStream).await())
                        {
                            final byte[] buffer = new byte[100];
                            Integer bytesRead = 0;
                            while (bytesRead != null)
                            {
                                bytesRead = stream.readBytes(buffer, 5, 73).catchError(EndOfStreamException.class).await();
                                if (bytesRead != null)
                                {
                                    test.assertTrue(1 <= bytesRead && bytesRead <= 73);
                                }
                            }

                            for (int i = 0; i < 2; i++)
                            {
                                test.assertThrows(() -> stream.readBytes(buffer, 5, 73).await(),
                                    new EndOfStreamException());
                            }

                            test.assertEqual(expected, stream.takeDigest().toHexString());
                        }
                    });
                };

                readBytesTest.run(new byte[0], "D41D8CD98F00B204E9800998ECF8427E");
                readBytesTest.run(new byte[] { 5 }, "8BB6C17838643F9691CC6A4DE6C51709");
                readBytesTest.run(new byte[] { 1, 2, 3 }, "5289DF737DF57326FCDD22597AFB1FAC");
                readBytesTest.run(new byte[1024], "0F343B0931126A20F133D67C2B018A3B");
            });

            runner.test("dispose()", (Test test) ->
            {
                final InMemoryByteStream innerStream = InMemoryByteStream.create().endOfStream();
                try (final HashFunction md5 = HashFunction.createMD5().await())
                {
                    try (final HashFunctionByteReadStream stream = HashFunctionByteReadStream.create(innerStream, md5))
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
