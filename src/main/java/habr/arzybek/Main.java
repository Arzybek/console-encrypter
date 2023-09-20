package habr.arzybek;

import org.apache.commons.cli.*;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Main {
    public static void main(String[] args) {

        Options options = new Options();
        Option mode = new Option("m", "mode", true, "Options are: enc/dec. Encryption/decryption mode switch");
        mode.setRequired(true);
        options.addOption(mode);
        Option generate = new Option("g", "generate", false, "Generate new AES key");
        generate.setRequired(false);
        options.addOption(generate);
        Option inpArg = new Option("i", "input", true, "Input file path");
        inpArg.setRequired(true);
        options.addOption(inpArg);
        Option outArg = new Option("o", "output", true, "Output file path");
        outArg.setRequired(true);
        options.addOption(outArg);
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("console-encrypter", options);
            System.exit(1);
        }

        try {
            File keyFile = new File(Consts.KEY_FILENAME);
            SecretKey secretKey;
            if (!keyFile.exists() || keyFile.isDirectory() || cmd.hasOption('g')) {
                secretKey = KeyGenerator.getInstance("AES").generateKey();
                byte[] keyRaw = secretKey.getEncoded();
                String keyString = Base64.getEncoder().encodeToString(keyRaw);
                keyFile.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(Consts.KEY_FILENAME);
                outputStream.write(keyString.getBytes());
                outputStream.close();
            } else {
                byte[] keyRawBase = Files.readAllBytes(keyFile.toPath());
                byte[] keyRaw = Base64.getDecoder().decode(keyRawBase);
                secretKey = new SecretKeySpec(keyRaw, 0, keyRaw.length, "AES");
            }

            Cipher cipher = Cipher.getInstance("AES");
            File fileIn = new File(cmd.getOptionValue('i'));
            File fileOut = new File(cmd.getOptionValue('o'));
            if (!fileOut.exists()) {
                fileOut.createNewFile();
            }

            if (cmd.getOptionValue('m').equals("enc")) {
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                byte[] buffer = new byte[(int) fileIn.length()];
                try (FileInputStream is = new FileInputStream(fileIn)) {
                    is.read(buffer);
                }
                byte[] encryptedMessage = cipher.doFinal(buffer);
                try (FileOutputStream outputStream = new FileOutputStream(fileOut)) {
                    outputStream.write(encryptedMessage);
                }
                System.out.println(String.format(Consts.FILE_ENCRYPTED, fileIn.toPath(), fileOut.toPath()));
            } else if (cmd.getOptionValue('m').equals("dec")) {
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
                byte[] buffer = new byte[(int) fileIn.length()];
                try (FileInputStream is = new FileInputStream(fileIn)) {
                    is.read(buffer);
                }
                byte[] decryptedMessage = cipher.doFinal(buffer);
                try (FileOutputStream outputStream = new FileOutputStream(fileOut)) {
                    outputStream.write(decryptedMessage);
                }
                System.out.println(String.format(Consts.FILE_DECRYPTED, fileIn.toPath(), fileOut.toPath()));
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IOException | IllegalBlockSizeException | BadPaddingException e) {
            System.out.println(Consts.ERROR);
            System.out.println(e.getMessage());
        }
    }
}