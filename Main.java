package gr.teipir.openlab.smarthome;

import java.io.BufferedReader;
import java.io.IOException;

public class Main {

	private static USBCommunication com = new USBCommunication();
	
	public static void main(String[] args) {
		byte b1, b2;
		String s;
		char c = "O";
		boolean check;
		com.init();
		System.out.println("For closing the application give X");
		while(c != "X") {
			try {
				check = true;
				InputStreamReader isr = new InputStreamReader(System.in);
				BufferedReader br = new BufferedReader(isr);
				System.out.println("Please enter 1st byte to send (LOW)");
				s = br.readLine();
				if(s.equals("X")){
					com.close();
					continue;
				}
				if(s.length() != 1) {
					System.out.println("Wrong parameters, please give only one byte!");
					check = false;					
				} else {
					b1 = (byte) s;
				}
				if(check == true) {
					System.out.println("Please enter 2nd byt to send (HIGH)");
					s.readLine();
					if(s.length() != 1) {
						System.out.println("Wrong parameters, please give only one byte!");
						check = false;
					} else {
						b2 = (byte) s;
					}
				}
				if(check == true) {
					com.sendData(b1,b2);
				}			
				
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
		
	}

}
