package com.vci.wb.controller;

import com.vci.wb.service.WeighbridgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Base64;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/weighbridge")
@CrossOrigin("*")
public class WeighbridgeController {

    private final WeighbridgeService weighbridgeService;

    @Autowired
    public WeighbridgeController(WeighbridgeService weighbridgeService) {
        this.weighbridgeService = weighbridgeService;
    }

    @PostMapping("/getWeight")
    public String getWeight(@RequestBody Map<String, Object> request) {
        String portName = (String) request.get("portName");
        int baudRate = (int) request.get("baudRate");
        int dataBits = (int) request.get("dataBits");
        int stopBits = (int) request.get("stopBits");
        int parity = (int) request.get("parity");

        return weighbridgeService.readWeight(portName, baudRate, dataBits, stopBits, parity);
    }

    @PostMapping("/capture")
    public Map<String, String> captureScreenshot(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        try {
            String base64Image = request.get("image").split(",")[1]; // Remove "data:image/png;base64,"
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            // Ensure the "screenshots" directory exists
            File screenshotDir = new File("screenshots");
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs(); // Create directory if it does not exist
            }

            // Generate the file path
            String filePath = "screenshots/capture_" + System.currentTimeMillis() + ".png";

            // Save the file
            try (OutputStream outputStream = new FileOutputStream(filePath)) {
                outputStream.write(imageBytes);
            }

            response.put("message", "Screenshot saved successfully");
            response.put("path", new File(filePath).getAbsolutePath()); // Return full file path
        } catch (Exception e) {
            response.put("error", "Error saving screenshot: " + e.getMessage());
        }
        return response;
    }

}
