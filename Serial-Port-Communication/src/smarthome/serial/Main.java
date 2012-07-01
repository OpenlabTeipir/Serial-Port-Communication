package smarthome.serial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

	private static USBCommunication com = new USBCommunication();

	public static void main(String[] args) {
		//Initialize bytes to 00h -> Reset 
		byte b1 = 0, b2 = 0;
		String s;
		char c = 'O', temp[];
		boolean check;
		com.init();
		System.out.println("For closing the application give X");
		while (c != 'X') {
			try {
				check = true;
				InputStreamReader isr = new InputStreamReader(System.in);
				BufferedReader br = new BufferedReader(isr);
				System.out.println("Please enter 1st byte to send (LOW)");
				s = br.readLine();
				if (s.equals('X')) {
					com.close();
					continue;
				}
				if (s.length() != 1) {
					System.out
							.println("Wrong parameters, please give only one byte!");
					check = false;
				} else {
					temp = s.toCharArray();
					b1 = (byte) temp[0];
				}
				if (check == true) {
					System.out.println("Please enter 2nd byt to send (HIGH)");
					s = br.readLine();
					if (s.length() != 1) {
						System.out
								.println("Wrong parameters, please give only one byte!");
						check = false;
					} else {
						temp = s.toCharArray();
						b2 = (byte) temp[0];
					}
				}
				if (check == true) {
					com.sendData(b1, b2);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}