package qub;

/**
 * A {@link ByteReadStream} that will compute the hash value of the bytes that are read.
 */
public class HashFunctionByteReadStream implements ByteReadStream
{
    private final ByteReadStream innerStream;
    private final HashFunction hashFunction;
    private boolean isDisposed;

    private HashFunctionByteReadStream(ByteReadStream innerStream, HashFunction hashFunction)
    {
        PreCondition.assertNotNull(innerStream, "innerStream");
        PreCondition.assertNotNull(hashFunction, "hashFunction");

        this.innerStream = innerStream;
        this.hashFunction = hashFunction;
    }

    public static HashFunctionByteReadStream create(ByteReadStream innerStream, HashFunction hashFunction)
    {
        return new HashFunctionByteReadStream(innerStream, hashFunction);
    }

    public static Result<HashFunctionByteReadStream> create(ByteReadStream innerStream, Function0<Result<? extends HashFunction>> hashFunctionCreator)
    {
        PreCondition.assertNotNull(innerStream, "innerStream");
        PreCondition.assertNotNull(hashFunctionCreator, "hashFunctionCreator");

        return Result.create(() ->
        {
            return HashFunctionByteReadStream.create(innerStream, hashFunctionCreator.run().await());
        });
    }

    public static Result<HashFunctionByteReadStream> createMD5(ByteReadStream innerStream)
    {
        return HashFunctionByteReadStream.create(innerStream, HashFunction::createMD5);
    }

    public static Result<HashFunctionByteReadStream> createSHA1(ByteReadStream innerStream)
    {
        return HashFunctionByteReadStream.create(innerStream, HashFunction::createSHA1);
    }

    public static Result<HashFunctionByteReadStream> createSHA256(ByteReadStream innerStream)
    {
        return HashFunctionByteReadStream.create(innerStream, HashFunction::createSHA256);
    }

    @Override
    public Result<Byte> readByte()
    {
        return Result.create(() ->
        {
            final byte value = this.innerStream.readByte().await();
            this.hashFunction.addByte(value);
            return value;
        });
    }

    @Override
    public Result<Integer> readBytes(byte[] outputBytes, int startIndex, int length)
    {
        PreCondition.assertNotNull(outputBytes, "outputBytes");
        PreCondition.assertStartIndex(startIndex, outputBytes.length);
        PreCondition.assertLength(length, startIndex, outputBytes.length);

        return Result.create(() ->
        {
            final Integer bytesRead = this.innerStream.readBytes(outputBytes, startIndex, length).await();
            this.hashFunction.addBytes(outputBytes, startIndex, bytesRead);
            return bytesRead;
        });
    }

    /**
     * Take the current hash value/message digest that has been computed from the bytes that have
     * been read. This will reset the {@link HashFunction} to its initial state.
     */
    public BitArray takeDigest()
    {
        return this.hashFunction.takeDigest();
    }

    @Override
    public boolean isDisposed()
    {
        return this.isDisposed;
    }

    @Override
    public Result<Boolean> dispose()
    {
        return Result.create(() ->
        {
            final boolean result = !this.isDisposed;
            if (result)
            {
                this.isDisposed = true;

                this.hashFunction.dispose().await();
                this.innerStream.dispose().await();
            }
            return result;
        });
    }
}
