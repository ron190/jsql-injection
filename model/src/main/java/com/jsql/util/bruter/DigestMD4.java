package com.jsql.util.bruter;

// This file is currently unlocked (change this line if you lock the file)
//
// $Log: MD4.java,v $
// Revision 1.2  1998/01/05 03:41:19  iang
// Added references only.
//
// Revision 1.1.1.1  1997/11/03 22:36:56  hopwood
// + Imported to CVS (tagged as 'start').
//
// Revision 0.1.0.0  1997/07/14  R. Naffah
// + original version
//
// $Endlog$
/*
 * Copyright (c) 1997 Systemics Ltd
 * on behalf of the Cryptix Development Team.  All rights reserved.
 */

import java.security.MessageDigest;

/**
 * Implements the MD4 message digest algorithm in Java.
 * <p>
 * <b>References:</b>
 * <ol>
 *   <li> Ronald L. Rivest,
 *        "<a href="http://www.roxen.com/rfc/rfc1320.html">
 *        The MD4 Message-Digest Algorithm</a>",
 *        IETF RFC-1320 (informational).
 * </ol>
 *
 * <p><b>$Revision: 1.2 $</b>
 * @author  Raif S. Naffah
 */
public class DigestMD4 extends MessageDigest implements Cloneable {
    
    // MD4 specific object variables
    //...........................................................................

    /**
     * The size in bytes of the input block to the tranformation algorithm.
     */
    private static final int BLOCK_LENGTH = 64; //512 / 8

    /**
     * 4 32-bit words (interim result)
     */
    private int[] context = new int[4];

    /**
     * Number of bytes processed so far mod. 2 power of 64.
     */
    private long count;

    /**
     * 512 bits input buffer = 16 x 32-bit words holds until reaches 512 bits.
     */
    private byte[] buffer = new byte[BLOCK_LENGTH];

    /**
     * 512 bits work buffer = 16 x 32-bit words
     */
    private int[] X = new int[16];


    // Constructors
    //...........................................................................

    public DigestMD4() {
        
        super("MD4");
        this.engineReset();
    }

    /**
     *    This constructor is here to implement cloneability of this class.
     */
    private DigestMD4(DigestMD4 md) {
        
        this();
        this.context = md.context.clone();
        this.buffer = md.buffer.clone();
        this.count = md.count;
    }


    // Cloneable method implementation
    //...........................................................................

    /**
     * Returns a copy of this MD object.
     */
    @Override
    public Object clone() {
        
        return new DigestMD4(this);
    }


    // JCE methods
    //...........................................................................

    /**
     * Resets this object disregarding any temporary data present at the
     * time of the invocation of this call.
     */
    @Override
    public void engineReset() {
        
        // initial values of MD4 i.e. A, B, C, D
        // as per rfc-1320; they are low-order byte first
        this.context[0] = 0x67452301;
        this.context[1] = 0xEFCDAB89;
        this.context[2] = 0x98BADCFE;
        this.context[3] = 0x10325476;
        this.count = 0L;
        
        for (int i = 0; i < BLOCK_LENGTH; i++) {
            
            this.buffer[i] = 0;
        }
    }

    /**
     * Continues an MD4 message digest using the input byte.
     */
    @Override
    public void engineUpdate(byte b) {
        
        // compute number of bytes still unhashed; ie. present in buffer
        int i = (int)(this.count % BLOCK_LENGTH);
        this.count++;                                        // update number of bytes
        this.buffer[i] = b;
        
        if (i == BLOCK_LENGTH - 1) {
            
            this.transform(this.buffer, 0);
        }
    }

    /**
     * MD4 block update operation.
     * <p>
     * Continues an MD4 message digest operation, by filling the buffer,
     * transform(ing) data in 512-bit message block(s), updating the variables
     * context and count, and leaving (buffering) the remaining bytes in buffer
     * for the next update or finish.
     *
     * @param    input    input block
     * @param    offset    start of meaningful bytes in input
     * @param    len        count of bytes in input block to consider
     */
    @Override
    public void engineUpdate(byte[] input, int offset, int len) {
        
        // make sure we don't exceed input's allocated size/length
        if (offset < 0 || len < 0 || (long)offset + len > input.length) {
            
            throw new ArrayIndexOutOfBoundsException();
        }
        
        // compute number of bytes still unhashed; ie. present in buffer
        int bufferNdx = (int)(this.count % BLOCK_LENGTH);
        this.count += len;                                        // update number of bytes
        int partLen = BLOCK_LENGTH - bufferNdx;
        int i = 0;
        
        if (len >= partLen) {
            
            System.arraycopy(input, offset, this.buffer, bufferNdx, partLen);

            this.transform(this.buffer, 0);

            for (i = partLen; i + BLOCK_LENGTH - 1 < len; i+= BLOCK_LENGTH) {
                
                this.transform(input, offset + i);
            }
            
            bufferNdx = 0;
        }
        
        // buffer remaining input
        if (i < len) {
            
            System.arraycopy(input, offset + i, this.buffer, bufferNdx, len - i);
        }
    }

    /**
     * Completes the hash computation by performing final operations such
     * as padding. At the return of this engineDigest, the MD engine is
     * reset.
     *
     * @return the array of bytes for the resulting hash value.
     */
    @Override
    public byte[] engineDigest() {
        
        // pad output to 56 mod 64; as RFC1320 puts it: congruent to 448 mod 512
        int bufferNdx = (int)(this.count % BLOCK_LENGTH);
        int padLen = bufferNdx < 56 ? 56 - bufferNdx : 120 - bufferNdx;

        // padding is alwas binary 1 followed by binary 0s
        byte[] tail = new byte[padLen + 8];
        tail[0] = (byte)0x80;

        // append length before final transform:
        // save number of bits, casting the long to an array of 8 bytes
        // save low-order byte first.
        for (int i = 0; i < 8; i++) {
            
            tail[padLen + i] = (byte)((this.count * 8) >>> (8 * i));
        }
        
        this.engineUpdate(tail, 0, tail.length);

        byte[] result = new byte[16];
        
        // cast this MD4's context (array of 4 ints) into an array of 16 bytes.
        for (int i = 0; i < 4; i++) {
            
            for (int j = 0; j < 4; j++) {
                
                result[i * 4 + j] = (byte)(this.context[i] >>> (8 * j));
            }
        }
    
        // reset the engine
        this.engineReset();
        
        return result;
    }


    // own methods
    //...........................................................................

    /**
     *    MD4 basic transformation.
     *    <p>
     *    Transforms context based on 512 bits from input block starting
     *    from the offset'th byte.
     *
     *    @param    block    input sub-array.
     *    @param    offset    starting position of sub-array.
     */
    private void transform(byte[] block, int offset) {

        // encodes 64 bytes from input block into an array of 16 32-bit
        // entities. Use A as a temp var.
        for (int i = 0; i < 16; i++) {
            this.X[i] =
                (block[offset++] & 0xFF)
                | (block[offset++] & 0xFF) <<  8
                | (block[offset++] & 0xFF) << 16
                | (block[offset++] & 0xFF) << 24;
        }

        int A = this.context[0];
        int B = this.context[1];
        int C = this.context[2];
        int D = this.context[3];

        A = this.FF(A, B, C, D, this.X[ 0],  3);
        D = this.FF(D, A, B, C, this.X[ 1],  7);
        C = this.FF(C, D, A, B, this.X[ 2], 11);
        B = this.FF(B, C, D, A, this.X[ 3], 19);
        A = this.FF(A, B, C, D, this.X[ 4],  3);
        D = this.FF(D, A, B, C, this.X[ 5],  7);
        C = this.FF(C, D, A, B, this.X[ 6], 11);
        B = this.FF(B, C, D, A, this.X[ 7], 19);
        A = this.FF(A, B, C, D, this.X[ 8],  3);
        D = this.FF(D, A, B, C, this.X[ 9],  7);
        C = this.FF(C, D, A, B, this.X[10], 11);
        B = this.FF(B, C, D, A, this.X[11], 19);
        A = this.FF(A, B, C, D, this.X[12],  3);
        D = this.FF(D, A, B, C, this.X[13],  7);
        C = this.FF(C, D, A, B, this.X[14], 11);
        B = this.FF(B, C, D, A, this.X[15], 19);

        A = this.GG(A, B, C, D, this.X[ 0],  3);
        D = this.GG(D, A, B, C, this.X[ 4],  5);
        C = this.GG(C, D, A, B, this.X[ 8],  9);
        B = this.GG(B, C, D, A, this.X[12], 13);
        A = this.GG(A, B, C, D, this.X[ 1],  3);
        D = this.GG(D, A, B, C, this.X[ 5],  5);
        C = this.GG(C, D, A, B, this.X[ 9],  9);
        B = this.GG(B, C, D, A, this.X[13], 13);
        A = this.GG(A, B, C, D, this.X[ 2],  3);
        D = this.GG(D, A, B, C, this.X[ 6],  5);
        C = this.GG(C, D, A, B, this.X[10],  9);
        B = this.GG(B, C, D, A, this.X[14], 13);
        A = this.GG(A, B, C, D, this.X[ 3],  3);
        D = this.GG(D, A, B, C, this.X[ 7],  5);
        C = this.GG(C, D, A, B, this.X[11],  9);
        B = this.GG(B, C, D, A, this.X[15], 13);

        A = this.HH(A, B, C, D, this.X[ 0],  3);
        D = this.HH(D, A, B, C, this.X[ 8],  9);
        C = this.HH(C, D, A, B, this.X[ 4], 11);
        B = this.HH(B, C, D, A, this.X[12], 15);
        A = this.HH(A, B, C, D, this.X[ 2],  3);
        D = this.HH(D, A, B, C, this.X[10],  9);
        C = this.HH(C, D, A, B, this.X[ 6], 11);
        B = this.HH(B, C, D, A, this.X[14], 15);
        A = this.HH(A, B, C, D, this.X[ 1],  3);
        D = this.HH(D, A, B, C, this.X[ 9],  9);
        C = this.HH(C, D, A, B, this.X[ 5], 11);
        B = this.HH(B, C, D, A, this.X[13], 15);
        A = this.HH(A, B, C, D, this.X[ 3],  3);
        D = this.HH(D, A, B, C, this.X[11],  9);
        C = this.HH(C, D, A, B, this.X[ 7], 11);
        B = this.HH(B, C, D, A, this.X[15], 15);

        this.context[0] += A;
        this.context[1] += B;
        this.context[2] += C;
        this.context[3] += D;
    }

    // The basic MD4 atomic functions.

    private int FF(int a, int b, int c, int d, int x, int s) {
        
        int t = a + ((b & c) | (~b & d)) + x;
        return t << s | t >>> (32 - s);
    }
    
    private int GG(int a, int b, int c, int d, int x, int s) {
        
        int t = a + ((b & (c | d)) | (c & d)) + x + 0x5A827999;
        return t << s | t >>> (32 - s);
    }
    
    private int HH(int a, int b, int c, int d, int x, int s) {
        
        int t = a + (b ^ c ^ d) + x + 0x6ED9EBA1;
        return t << s | t >>> (32 - s);
    }
}