package com.licensechain.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Random;
import java.util.regex.Pattern;

public class Utils {
    
    // Validation methods
    public static boolean validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
    
    public static boolean validateLicenseKey(String licenseKey) {
        if (licenseKey == null || licenseKey.length() != 32) {
            return false;
        }
        return licenseKey.matches("^[A-Z0-9]+$");
    }
    
    public static boolean validateUuid(String uuid) {
        if (uuid == null) {
            return false;
        }
        String uuidRegex = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";
        Pattern pattern = Pattern.compile(uuidRegex, Pattern.CASE_INSENSITIVE);
        return pattern.matcher(uuid).matches();
    }
    
    public static boolean validateAmount(Double amount) {
        return amount != null && amount > 0 && !amount.isInfinite() && !amount.isNaN();
    }
    
    public static boolean validateCurrency(String currency) {
        if (currency == null) {
            return false;
        }
        String[] validCurrencies = {"USD", "EUR", "GBP", "CAD", "AUD", "JPY", "CHF", "CNY"};
        for (String validCurrency : validCurrencies) {
            if (validCurrency.equalsIgnoreCase(currency)) {
                return true;
            }
        }
        return false;
    }
    
    // String utilities
    public static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;");
    }
    
    public static String generateLicenseKey() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }
        return result.toString();
    }
    
    public static String generateUuid() {
        return java.util.UUID.randomUUID().toString();
    }
    
    public static String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
    
    public static String toSnakeCase(String text) {
        if (text == null) {
            return null;
        }
        return text.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
    
    public static String toPascalCase(String text) {
        if (text == null) {
            return null;
        }
        String[] words = text.split("_");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(capitalizeFirst(word));
            }
        }
        return result.toString();
    }
    
    public static String truncateString(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
    
    public static String slugify(String text) {
        if (text == null) {
            return null;
        }
        return text.toLowerCase()
                   .replaceAll("[^a-z0-9\\s-]", "")
                   .replaceAll("\\s+", "-")
                   .replaceAll("-+", "-")
                   .replaceAll("^-|-$", "");
    }
    
    // Date utilities
    public static String formatTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return dateTime.format(formatter) + "Z";
    }
    
    public static LocalDateTime parseTimestamp(String timestamp) {
        if (timestamp == null || timestamp.trim().isEmpty()) {
            return null;
        }
        try {
            // Remove 'Z' suffix if present
            if (timestamp.endsWith("Z")) {
                timestamp = timestamp.substring(0, timestamp.length() - 1);
            }
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            return LocalDateTime.parse(timestamp, formatter);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static String getCurrentTimestamp() {
        return formatTimestamp(LocalDateTime.now());
    }
    
    public static String getCurrentDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
    
    // Crypto utilities
    public static String createWebhookSignature(String payload, String secret) {
        try {
            String data = payload + secret;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error creating webhook signature", e);
        }
    }
    
    public static boolean verifyWebhookSignature(String payload, String signature, String secret) {
        if (payload == null || signature == null || secret == null) return false;
        String expected = createWebhookSignature(payload, secret);
        String received = (signature != null && signature.startsWith("sha256=")) ? signature.substring(7) : signature;
        try {
            byte[] a = hexStringToBytes(expected);
            byte[] b = hexStringToBytes(received);
            if (a.length != b.length) return false;
            return java.security.MessageDigest.isEqual(a, b);
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] hexStringToBytes(String hex) {
        int len = hex.length();
        if (len % 2 != 0) throw new IllegalArgumentException("Invalid hex length");
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
    
    public static String sha256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error creating SHA-256 hash", e);
        }
    }
    
    public static String sha1(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(data.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error creating SHA-1 hash", e);
        }
    }
    
    public static String md5(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(data.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error creating MD5 hash", e);
        }
    }
    
    // Formatting utilities
    public static String formatBytes(long bytes) {
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        double size = bytes;
        int unitIndex = 0;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.1f %s", size, units[unitIndex]);
    }
    
    public static String formatDuration(long seconds) {
        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            long remainingSeconds = seconds % 60;
            return minutes + "m " + remainingSeconds + "s";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            return hours + "h " + minutes + "m";
        } else {
            long days = seconds / 86400;
            long hours = (seconds % 86400) / 3600;
            return days + "d " + hours + "h";
        }
    }
    
    public static String formatPrice(Double price, String currency) {
        return String.format("%.4f %s", price, currency);
    }
    
    // Validation helpers
    public static void validateNotEmpty(String value, String fieldName) throws IllegalArgumentException {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
    }
    
    public static void validatePositive(Double value, String fieldName) throws IllegalArgumentException {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive");
        }
    }
    
    public static void validateRange(Double value, Double min, Double max, String fieldName) throws IllegalArgumentException {
        if (value == null || value < min || value > max) {
            throw new IllegalArgumentException(fieldName + " must be between " + min + " and " + max);
        }
    }
    
    // Base64 utilities
    public static String base64Encode(String data) {
        if (data == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(data.getBytes());
    }
    
    public static String base64Decode(String data) {
        if (data == null) {
            return null;
        }
        try {
            return new String(Base64.getDecoder().decode(data));
        } catch (Exception e) {
            return null;
        }
    }
    
    // URL utilities
    public static String urlEncode(String data) {
        if (data == null) {
            return null;
        }
        try {
            return java.net.URLEncoder.encode(data, "UTF-8");
        } catch (Exception e) {
            return data;
        }
    }
    
    public static String urlDecode(String data) {
        if (data == null) {
            return null;
        }
        try {
            return java.net.URLDecoder.decode(data, "UTF-8");
        } catch (Exception e) {
            return data;
        }
    }
    
    // Array utilities
    public static <T> T[][] chunkArray(T[] array, int chunkSize) {
        int chunks = (int) Math.ceil((double) array.length / chunkSize);
        @SuppressWarnings("unchecked")
        T[][] result = (T[][]) new Object[chunks][];
        
        for (int i = 0; i < chunks; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, array.length);
            result[i] = java.util.Arrays.copyOfRange(array, start, end);
        }
        
        return result;
    }
}
