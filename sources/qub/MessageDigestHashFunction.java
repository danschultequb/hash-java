package qub;

public class MessageDigestHashFunction implements HashFunction
{
    private final java.security.MessageDigest messageDigest;
    private boolean disposed;

    private MessageDigestHashFunction(String algorithm)
    {
        PreCondition.assertNotNullAndNotEmpty(algorithm, "algorithm");

        try
        {
            this.messageDigest = java.security.MessageDigest.getInstance(algorithm);
        }
        catch (java.security.NoSuchAlgorithmException e)
        {
            throw Exceptions.asRuntime(e);
        }
    }

    public static Result<MessageDigestHashFunction> create(String algorithm)
    {
        PreCondition.assertNotNullAndNotEmpty(algorithm, "algorithm");

        return Result.create(() ->
        {
            return new MessageDigestHashFunction(algorithm);
        });
    }

    /**
     * Get the name of the algorithm that this {@link MessageDigestHashFunction} is using.
     */
    public String getAlgorithm()
    {
        return this.messageDigest.getAlgorithm();
    }

    @Override
    public void addByte(byte value)
    {
        PreCondition.assertNotDisposed(this, "this");

        this.messageDigest.update(value);
    }

    @Override
    public void addBytes(byte[] values, int startIndex, int length)
    {
        PreCondition.assertNotNull(values, "values");
        PreCondition.assertStartIndex(startIndex, values.length);
        PreCondition.assertLength(length, startIndex, values.length);
        PreCondition.assertNotDisposed(this, "this");

        this.messageDigest.update(values, startIndex, length);
    }

    @Override
    public BitArray takeDigest()
    {
        PreCondition.assertNotDisposed(this, "this");

        final byte[] digest = this.messageDigest.digest();
        final BitArray result = BitArray.createFromBytes(digest);

        PostCondition.assertNotNullAndNotEmpty(result, "result");

        return result;
    }

    @Override
    public void reset()
    {
        this.messageDigest.reset();
    }

    @Override
    public boolean isDisposed()
    {
        return this.disposed;
    }

    @Override
    public Result<Boolean> dispose()
    {
        return Result.create(() ->
        {
            boolean result = !this.disposed;
            if (result)
            {
                this.disposed = true;
                this.reset();
            }
            return result;
        });
    }
}
