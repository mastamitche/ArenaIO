package com.Game;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.ByteBuffer;

/*import net.bytebuddy.*;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice.Origin;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.jar.asm.commons.Method;
import net.bytebuddy.matcher.ElementMatchers;
*/

import com.Entity.VariableReplication;
import com.Odessa.utility.BitBuffer;
import com.Odessa.utility.BitOutputStream;

public class BitTester {
	public static void main(String args[]) {
		new BitTester();

	}

	int bitsNeeded = 32;

	public BitTester() {
		
		ByteArrayOutputStream buf2 = new ByteArrayOutputStream();
		BitOutputStream bis = new BitOutputStream(buf2);
		try {
			int written = writeCompactedInt(bis, 0, 3000, bitsNeeded, true);
			bis.flush();
			bis.close();
			System.out.println("Ended up writing " + written + " bits");

			// We now have our packet

			// System.out.println("\nAttempting read\n");
			BitBuffer buf = new BitBuffer(ByteBuffer.wrap(buf2.toByteArray()));
			// buf.order(BitBuffer.BitOrder.LEAST_TO_MOST_SIGNIFICANT);

			readCompactedInt(buf, bitsNeeded, true);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int writeCompactedInt(BitOutputStream buf, int oldValue, int value,
			int maxBitsNeeded, boolean canGoNegative) throws IOException {
		int diff = value - oldValue;
		System.out.println("diff: " + diff);
		int abs = Math.abs(diff);
		int numBits = (int) Math.floor(log2(abs));
		int checkBits = (int) Math.ceil(log2(maxBitsNeeded));

		int written = numBits;
		if (checkBits > 4)
			written += writeCompactedInt(buf, 0, numBits, checkBits, false);
		else {
			buf.write(checkBits, numBits);
			written += checkBits;
		}

		buf.write(numBits, (int) (abs - Math.pow(2, numBits)));

		if (maxBitsNeeded >= 1 && canGoNegative) {
			buf.write(1, diff < 0 ? 0 : 1);
			written++;
		}

		return written;
	}

	public int readCompactedInt(BitBuffer buf, int maxBitsNeeded,
			boolean canGoNegative) throws IOException {

		int checkBits = (int) Math.ceil(log2(maxBitsNeeded));

		int numberToRead = 0;
		if (checkBits > 4) {
			numberToRead = readCompactedInt(buf, checkBits, false);
		} else {
			numberToRead = (int) buf.get(checkBits);
		}

		int value = (int) (buf.get(numberToRead) + Math.pow(2, numberToRead));

		int sign = 1;
		if (maxBitsNeeded >= 1 && canGoNegative && buf.get() == 0)
			sign *= -1;

		System.out.println("Got: " + (value * sign));

		return (int) value * sign;
	}

	public static double log2(int n) {
		return (Math.log(n) / Math.log(2));
	}

}
