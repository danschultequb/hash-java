package qub;

/**
 * A state-ful function that can map data of any size to a fixed size "hash" (also known as a
 * message digest).
 */
public interface HashFunction extends Disposable
{
    /**
     * Create a new instance of an MD5 hash function.
     */
    public static Result<? extends HashFunction> createMD5()
    {
        return MessageDigestHashFunction.create("MD5");
    }

    /**
     * Create a new instance of an SHA-1 hash function.
     */
    public static Result<? extends HashFunction> createSHA1()
    {
        return MessageDigestHashFunction.create("SHA1");
    }

    /**
     * Create a new instance of an SHA-256 hash function.
     */
    public static Result<? extends HashFunction> createSHA256()
    {
        return MessageDigestHashFunction.create("SHA256");
    }

    /**
     * Add a single {@link byte} to the {@link HashFunction}.
     * @param value The {@link byte} to add.
     */
    public void addByte(byte value);

    /**
     * Add the provided bytes to the {@link HashFunction}.
     * @param values The bytes to add.
     */
    public default void addBytes(byte[] values)
    {
        PreCondition.assertNotNull(values, "values");

        this.addBytes(values, 0, values.length);
    }

    /**
     * Add the provided bytes to the {@link HashFunction}.
     * @param values The bytes to add.
     * @param startIndex The index into the values that the bytes should be added from.
     * @param length The number of byte values that should be added.
     */
    public void addBytes(byte[] values, int startIndex, int length);

    /**
     * Complete the {@link HashFunction} and return the resulting digest/hash. This will reset the
     * {@link HashFunction} so that no trace of the operation remains.
     * @return The resulting digest/hash.
     */
    public BitArray takeDigest();

    /**
     * Add the provided {@link byte} to this {@link HashFunction}, complete this
     * {@link HashFunction}, and then return the resulting digest/hash. This will reset the
     * {@link HashFunction} so that no trace of the operation remains.
     * @param value The {@link byte} to add.
     * @return The resulting digest/hash.
     */
    public default BitArray takeDigest(byte value)
    {
        this.addByte(value);
        return this.takeDigest();
    }

    /**
     * Add the provided bytes to this {@link HashFunction}, complete this
     * {@link HashFunction}, and then return the resulting digest/hash. This will reset the
     * {@link HashFunction} so that no trace of the operation remains.
     * @param values The {@link byte}s to add.
     * @return The resulting digest/hash.
     */
    public default BitArray takeDigest(byte[] values)
    {
        this.addBytes(values);
        return this.takeDigest();
    }

    /**
     * Add the provided bytes to this {@link HashFunction}, complete this
     * {@link HashFunction}, and then return the resulting digest/hash. This will reset the
     * {@link HashFunction} so that no trace of the operation remains.
     * @param values The {@link byte}s to add.
     * @param startIndex The index into the values that the bytes should be added from.
     * @param length The number of byte values that should be added.
     * @return The resulting digest/hash.
     */
    public default BitArray takeDigest(byte[] values, int startIndex, int length)
    {
        this.addBytes(values, startIndex, length);
        return this.takeDigest();
    }

    /**
     * Reset this {@link HashFunction} so that no trace of the operation remains.
     */
    public void reset();
}
