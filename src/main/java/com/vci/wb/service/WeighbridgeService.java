package com.vci.wb.service;

import org.springframework.stereotype.Service;
import purejavacomm.*;

import java.io.InputStream;
import java.util.Enumeration;

@Service
public class WeighbridgeService {

    public String readWeight(String portName, int baudRate, int dataBits, int stopBits, int parity) {
        CommPortIdentifier portIdentifier;
        SerialPort serialPort = null;

        try {
            // Check if the port exists
            Enumeration<?> portList = CommPortIdentifier.getPortIdentifiers();
            boolean portFound = false;
            while (portList.hasMoreElements()) {
                CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
                if (portId.getName().equals(portName)) {
                    portIdentifier = portId;
                    portFound = true;
                    break;
                }
            }

            if (!portFound) {
                return "Error: Port " + portName + " not found!";
            }

            // Open the port
            portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            serialPort = (SerialPort) portIdentifier.open("WeighbridgeReader", 2000);
            serialPort.setSerialPortParams(baudRate, dataBits, stopBits, parity);

            // Read data from the port
            InputStream inputStream = serialPort.getInputStream();
            byte[] buffer = new byte[1024];
            int len = inputStream.read(buffer);

            // Convert to string, trim spaces, and remove leading zeros
            String weight = new String(buffer, 0, len).trim().replaceFirst("^0+(?!$)", "");

            return weight.isEmpty() ? "No weight data available." : weight;

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        } finally {
            if (serialPort != null) {
                serialPort.close(); // Close the port to prevent conflicts
            }
        }
    }
}
