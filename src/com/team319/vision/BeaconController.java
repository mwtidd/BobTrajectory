package com.team319.vision;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team319.auto.AutoConfig;
import com.team319.auto.AutoConfigException;
import com.team319.auto.AutoManager;
import com.team319.auto.IAutoConfigChangeListener;

/**
 * @author mwtidd
 */
public class BeaconController implements SerialPortEventListener, IAutoConfigChangeListener {
    SerialPort serialPort = null;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    private static final String PORT_NAMES[] = {
//        "/dev/tty.usbmodem", // Mac OS X
//        "/dev/usbdev", // Linux
//        "/dev/tty", // Linux
//        "/dev/serial", // Linux
    		"COM3", // Windows
    		//"COM16"
    };

    private String appName;
    private BufferedReader input;
    private OutputStream output;

    private static final int TIME_OUT = 1000; // Port open timeout
    private static final int DATA_RATE = 9600; // Arduino serial port

    boolean initialized = false;

    public boolean initialize() {
    	initialized = false;

    	new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
		            CommPortIdentifier portId = null;
		            Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		            // Enumerate system ports and try connecting to Arduino over each
		            //
		            System.out.println( "Trying:");
		            while (portId == null && portEnum.hasMoreElements()) {
		                // Iterate through your host computer's serial port IDs
		                //
		                CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
		                System.out.println( "   port" + currPortId.getName() );
		                for (String portName : PORT_NAMES) {
		                    if ( currPortId.getName().equals(portName)
		                      || currPortId.getName().startsWith(portName)) {

		                        // Try to connect to the Arduino on this port
		                        //
		                        // Open serial port
		                        serialPort = (SerialPort)currPortId.open(appName, TIME_OUT);
		                        portId = currPortId;
		                        logger.info( "Connected on port" + currPortId.getName());
		                        break;
		                    }
		                }
		            }

		            if (portId == null || serialPort == null) {
		            	logger.error("Couldn't connect to arduino");
		            }

		            // set port parameters
		            serialPort.setSerialPortParams(DATA_RATE,
		                            SerialPort.DATABITS_8,
		                            SerialPort.STOPBITS_1,
		                            SerialPort.PARITY_NONE);

		            BeaconController.getInstance().startListening();
		        }
		        catch ( TooManyListenersException e ) {
		            logger.error("Too many listeners");
		        } catch (UnsupportedCommOperationException e) {
		        	logger.error("Unsupported Comm");
				} catch (PortInUseException e) {
					logger.error("Port in Use");
				} catch(RuntimeException e){
					logger.error("Runtime Exception");
					try {
						Thread.sleep(1000);
						//BeaconController.getInstance().initialize();
					} catch (InterruptedException e1) {
						logger.error("Unable to sleep.");
					}
				}
			}
		}).start();


        return false;
    }

    public void startListening() throws TooManyListenersException{
    	 // add event listeners
        serialPort.addEventListener(this);
        serialPort.notifyOnDataAvailable(true);

        // Give the Arduino some time
        try { Thread.sleep(2000); } catch (InterruptedException ie) {}

        initialized = true;

        this.sendData("r");

        this.onChange(AutoManager.getInstance().getConfig());
    }

    private void sendData(String data) {
        try {
            System.out.println("Sending data: '" + data +"'");

            // open the streams and send the "y" character
            output = serialPort.getOutputStream();
            output.write( data.getBytes() );
        }catch (IOException e) {
			logger.error("unable to send data");
			this.initialize();
		}
    }

    //
    // This should be called when you stop using the port
    //
    public synchronized void close() {
        if ( serialPort != null ) {
            serialPort.removeEventListener();
            serialPort.close();
        }

        AutoManager.getInstance().unregisterListener(this);
    }

    //
    // Handle serial port event
    //
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        //System.out.println("Event received: " + oEvent.toString());
        try {
            switch (oEvent.getEventType() ) {
                case SerialPortEvent.DATA_AVAILABLE:
                    if ( input == null ) {
                        input = new BufferedReader(
                            new InputStreamReader(
                                    serialPort.getInputStream()));
                    }
                    String inputLine = input.readLine();
                    System.out.println(inputLine);
                    break;
                default:
                    break;
            }
        }
        catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    private BeaconController() {
        appName = getClass().getName();
        AutoManager.getInstance().registerListener(this);
    }

    private static BeaconController instance = null;

    public static BeaconController getInstance(){
    	if(instance == null){
    		instance = new BeaconController();
    	}
    	return instance;
    }

    @Override
    public void onChange(AutoConfig config) {
    	if(initialized){
    		if(config.getSelectedAlliance().equalsIgnoreCase("red")){
        		sendData("r");
        	}else{
        		sendData("b");
        	}
    	}
    }

    @Override
    public void onConfigException(AutoConfigException exception) {
    	// TODO Auto-generated method stub

    }
}



