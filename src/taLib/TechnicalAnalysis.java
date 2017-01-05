package taLib;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

public class TechnicalAnalysis {
	
	public static Core c = new Core();
	
	public static void calculateSMA() {
		
	}

	public static void main(String [] args) {
		double [] closePrice = new double[100];
		double [] out = new double[100];
		MInteger begin = new MInteger();
		MInteger length = new MInteger();
		
		for (int i = 0; i < closePrice.length; i++) {
			closePrice[i] = (double) i;
		}
		
		Core c = new Core();
		//RetCode rc = c.sma(0, closePrice.length-1, closePrice, 30, begin, length, out);
		RetCode rc = c.movingAverage(0, closePrice.length-1, closePrice, 30, MAType.Kama, begin, length, out);
		if (rc == rc.Success) {
			System.out.println("RetCode Success!");
			System.out.println("Output Begin: " + begin.value);
			System.out.println("Output End: " + length.value);
			for (int i = begin.value; i < (length.value + begin.value) ; i++) {
				StringBuilder line = new StringBuilder();
				line.append("Period #");
				line.append(i);
				line.append(" close= ");
				line.append(closePrice[i]);
				line.append(" mov avg= ");
				line.append(out[i-begin.value]);
				System.out.println(line.toString());
			}
		}
		else {
			System.out.println("Error");
		}
	
	}
	
}
