package qub;

/**
 * A {@link ByteWriteStream} that will compute the hash value of the bytes that are written.
 */
public class HashFunctionByteWriteStream implements ByteWriteStream
{
    private final ByteWriteStream innerStream;
    private final HashFunction hashFunction;
    private boolean isDisposed;

    private HashFunctionByteWriteStream(ByteWriteStream innerStream, HashFunction hashFunction)
    {
        PreCondition.assertNotNull(innerStream, "innerStream");
        PreCondition.assertNotNull(hashFunction, "hashFunction");

        this.innerStream = innerStream;
        this.hashFunction = hashFunction;
    }

    public static HashFunctionByteWriteStream create(ByteWriteStream innerStream, HashFunction hashFunction)
    {
        return new HashFunctionByteWriteStream(innerStream, hashFunction);
    }

    public static Result<HashFunctionByteWriteStream> create(ByteWriteStream innerStream, Function0<Result<? extends HashFunction>> hashFunctionCreator)
    {
        PreCondition.assertNotNull(innerStream, "innerStream");
        PreCondition.assertNotNull(hashFunctionCreator, "hashFunctionCreator");

        return Result.create(() ->
        {
            return HashFunctionByteWriteStream.create(innerStream, hashFunctionCreator.run().await());
        });
    }

    public static Result<HashFunctionByteWriteStream> createMD5(ByteWriteStream innerStream)
    {
        return HashFunctionByteWriteStream.create(innerStream, HashFunction::createMD5);
    }

    public static Result<HashFunctionByteWriteStream> createSHA1(ByteWriteStream innerStream)
    {
        return HashFunctionByteWriteStream.create(innerStream, HashFunction::createSHA1);
    }

    public static Result<HashFunctionByteWriteStream> createSHA256(ByteWriteStream innerStream)
    {
        return HashFunctionByteWriteStream.create(innerStream, HashFunction::createSHA256);
    }

    @Override
    public Result<Integer> write(byte toWrite)
    {
        return Result.create(() ->
        {
            final int result = this.innerStream.write(toWrite).await();
            this.hashFunction.addByte(toWrite);
            return result;
        });
    }

    @Override
    public Result<Integer> write(byte[] toWrite, int startIndex, int length)
    {
        PreCondition.assertNotNull(toWrite, "toWrite");
        PreCondition.assertStartIndex(startIndex, toWrite.length);
        PreCondition.assertLength(length, startIndex, toWrite.length);

        return Result.create(() ->
        {
            final int result = this.innerStream.write(toWrite, startIndex, length).await();
            this.hashFunction.addBytes(toWrite, startIndex, result);
            return result;
        });
    }

    /**
     * Take the current hash value/message digest that has been computed from the bytes that have
     * been written. This will reset the {@link HashFunction} to its initial state.
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
