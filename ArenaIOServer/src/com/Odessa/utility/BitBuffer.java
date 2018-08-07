/*
 * WARP: WARP is a Replay Manager for Java(R)
 *
 * (C) Copyright 2010-2011 Michael Seifert <mseifert@erichseifert.de>
 *
 * This file is part of WARP.
 *
 * WARP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WARP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WARP.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.Odessa.utility;

import java.nio.ByteBuffer;
import java.nio.InvalidMarkException;

/**
 * Class that represents a wrapper for a <code>ByteBuffer</code> object
 * which allows bit-wise read operations.
 */
public class BitBuffer {
	public static enum BitOrder {
		MOST_TO_LEAST_SIGNIFICANT,
		LEAST_TO_MOST_SIGNIFICANT
	}

	private final ByteBuffer input;
	private byte cur;
	private int bitPos;
	private BitOrder order;

	private int mark;

	/**
	 * Creates a <code>BitBuffer</code> object with the specified data source.
	 * @param buffer Buffer to read from.
	 */
	public BitBuffer(ByteBuffer buffer) {
		this.input = buffer;
		cur = buffer.get(input.position());
		order = BitOrder.MOST_TO_LEAST_SIGNIFICANT;
		mark = -1;
	}

	/**
	 * Returns the next bit in the <code>BitBuffer</code>.
	 * This method always reads from the most to the least significant bit,
	 * regardless of the endianness of the underlying <code>ByteBuffer</code>.
	 * @return Bit value.
	 */
	public byte get() {
		int shift = 7-bitPos;
		int mask = 1 << shift;
		byte bit = (byte) ((cur & mask) >> shift);
		bitPos++;
		if (bitPos > 7) {
			input.position(input.position()+1);
			if (input.hasRemaining()) {
				cur = input.get(input.position());
			}
			bitPos = 0;
		}
		return bit;
	}

	public int getByte() {
		int tmp = (int) get(8);
		if (tmp<0)tmp=256+tmp;
		return tmp;
	}
	public byte[] getBytes(int count) {
		byte[] tmp = new byte[count];
		for (int i = 0; i < count; i++)
			tmp[i]=(byte) get(8);
		return tmp;
	}
	public byte[] getBits(int count) {
		byte[] tmp = new byte[count];
		for (int i = 0; i < count; i++)
			tmp[i] = get();
		return tmp;
	}

	public int getInt() {
		return (int) get(32);
	}

	/**
	 * Transfers bits from the buffer to the specified array.
	 * @param bits Bit values.
	 */
	public void get(byte[] bits) {
		for (int i = 0; i < bits.length; i++) {
			bits[i] = get();
		}
	}

	public long get(int bitCount) {
		if (bitCount > 64) {
			throw new IllegalArgumentException("Numbers of type long cannot have more than 64 bits.");
		}

		long value = 0;
		if (BitOrder.MOST_TO_LEAST_SIGNIFICANT == order) {
			for (int i = 0; i < bitCount; i++) {
				value = (value << 1) | get();
			}
		}
		else if (BitOrder.LEAST_TO_MOST_SIGNIFICANT == order) {
			for (int i = 0; i < bitCount; i++) {
				value = value | (get() << i);
			}
		}
		return value;
	}

	/**
	 * Marks the current position.
	 * This method also changes the mark of the underlying <code>ByteBuffer</code>.
	 * @return This buffer.
	 */
	public BitBuffer mark() {
		mark = position();
		input.mark();
		return this;
	}

	/**
	 * Returns the current position.
	 * @return Position.
	 */
	public int position() {
		return input.position()*8 + bitPos;
	}

	/**
	 * Returns the number of remaining bits that can be read until the
	 * limit is reached.
	 * @return Remaining bit count.
	 */
	public int remaining() {
		return limit() - position();
	}

	/**
	 * Returns the position in the buffer over which cannot be read beyond.
	 * @return Limit.
	 */
	public int limit() {
		return input.limit()*8;
	}

	/**
	 * Returns the amount of bits that is stored in this buffer.
	 * @return Stored bit count.
	 */
	public int capacity() {
		return input.capacity()*8;
	}

	/**
	 * Sets the buffer's position to the specified position.
	 * This method also changes the position of the underlying <code>ByteBuffer</code>.
	 * @param newPosition Bit at the position.
	 * @return This buffer.
	 */
	public BitBuffer position(int newPosition) {
		bitPos = newPosition % 8;
		int bytePos = (newPosition-bitPos) / 8;
		input.position(bytePos);
		cur = input.get(bytePos);
		return this;
	}

	/**
	 * Resets the position of this buffer to the last marked position.
	 * This method does not change or discard the mark.
	 * This method also changes the position of the underlying <code>ByteBuffer</code>.
	 * @return This buffer.
	 * @throws InvalidMarkException if the mark has not been set.
	 */
	public BitBuffer reset() {
		if (mark >= 0) {
			position(mark);
		}
		else {
			throw new InvalidMarkException();
		}
		return this;
	}

	/**
	 * Rewinds the buffer setting the position to zero and discarding the mark.
	 * This method also rewinds the underlying <code>ByteBuffer</code>.
	 * @return This buffer.
	 */
	public BitBuffer rewind() {
		input.rewind();
		position(0);
		mark = -1;
		return this;
	}

	public void order(BitOrder order) {
		if (order != null) {
			this.order = order;
		}
	}

	public BitOrder order() {
		return order;
	}
}