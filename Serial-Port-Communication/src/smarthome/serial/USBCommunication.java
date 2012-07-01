package smarthome.serial;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.util.Enumeration;

import smarthome.sql.SQL;

//Imports from RXTX Java Library
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class USBCommunication {

	SerialPort serialPort;

	private static final String LINUX_PORT = "/dev/ttyUSB0"; // The port we are
																// going to use
																// for linux.
	// Buffered input stream <- port
	private InputStream input;
	// Output stream -> port
	private OutputStream output;
	// BAUD Rate
	private static final int BAUD = 9600;
	private static final int TIME_OUT = 5000; // Connection time out boundary

	private SQL db = new SQL("", "", "", "", ""); // Creds to be added

	// SQL Tables (Hardcoded)
	private static final String COMMANDS_TABLE = "commands";
	private static final String COMMANDS_LOG_TABLE = "command_log";
	private static final String EVENTS_LOG_TABLE = "event_log";

	// ... More to be added

	/* Initialize Stuff */
	public void init() {

		// Initializing time (in nano-seconds) in order to check the speed of
		// our init() method
		long start = System.nanoTime();
		long elapsedTime;

		// Connect to our database
		db.dbConnect();

		CommPortIdentifier PORT_ID = null;

		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		// Lookup port
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = ((CommPortIdentifier) portEnum
					.nextElement());
			if (currPortId.getName().equals(LINUX_PORT)) {
				PORT_ID = currPortId;
			}
		}

		if (PORT_ID == null) {
			System.out.println("Couldn't find device on COM port.");
		} else {
			System.out.println("Device found successfuly on COM port");
		}

		try {
			// Opening a serial port
			serialPort = (SerialPort) PORT_ID.open(this.getClass().getName(),
					TIME_OUT);

			// Initialize port
			serialPort.setSerialPortParams(BAUD, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			// Unleash the KRAKEN ... and open the streams
			input = serialPort.getInputStream();
			output = serialPort.getOutputStream();

			// Gimme some listeners
			serialPort.addEventListener((SerialPortEventListener) this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception we) {
			System.err.println(we.toString());
		}
		elapsedTime = System.nanoTime() - start;
		System.out.println("Started.");
		System.out.println("Time elapsed: " + elapsedTime);

	}

	// Parse data
	public synchronized void serialEvent(SerialPortEvent e) {
		if (e.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				// To be implemented with database..
				FileWriter fw = new FileWriter("output.txt", true);
				BufferedWriter bw = new BufferedWriter(fw);
				int b1 = input.read();
				int b2 = input.read();
				int low = b1 & 0xff; // Defining byte0 range [0-255]
				int high = b2 & 0xff; // Defining byte1 range [0-255]
				bw.write("" + high + low);
				bw.newLine();
				bw.flush();
				bw.close();
				SQL.Query("INSERT INTO " + EVENTS_LOG_TABLE + " VALUES('" + b1
						+ "', '" + b2 + "', DATETIME();");

			} catch (Exception ex) {
				System.err.println(ex.toString());
			}
		}
	}

	// Sends data to Serial Port and logs the action
	public void sendData(byte b1, byte b2) {
		try {
			ResultSet s;
			// Log the action
			SQL.Query("INSERT INTO " + COMMANDS_LOG_TABLE + " VALUES('" + b1
					+ "', '" + b2 + "', DATETIME();");
			// Check for command existence
			s = SQL.Select("SELECT byte1, byte2 FROM " + COMMANDS_TABLE
					+ " WHERE byte1 =  " + b1 + " AND byte2 = " + b2 + ";");
			if (s != null) {
				output.write(b1);
				output.write(b2);
				output.flush();
			}
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	// Avoiding port lock on Linux
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

}