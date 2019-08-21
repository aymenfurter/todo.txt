package ch.aymenfurter.todotxt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Default implementation for any action done upon detecting a change in todo file.
 */
public class DefaultModificationAction implements IActionOnModification {
    Logger _logger = LoggerFactory.getLogger(DefaultModificationAction.class);

    @Override
    public void performAction(File todo, File done) {
        _logger.debug("Change detected");


        BufferedReader reader = null;
        BufferedWriter writer = null;
        File tempFile = null;
        boolean changeApplied = false;

        try {
            tempFile = new File(todo.getAbsolutePath() + "-tmp");


            reader = new BufferedReader(new FileReader(todo));
            writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().endsWith(" " + Consts.DONE_DECLARE)) {
                    changeApplied = true;
                    done.createNewFile();
                    Files.write(done.toPath(), ((new SimpleDateFormat("dd-MM-yyyy")).format(new Date()) + ": " + line + System.getProperty("line.separator")).getBytes(), StandardOpenOption.APPEND);
                    continue;
                } else {
                    writer.write(line + System.getProperty("line.separator"));
                }
            }

        } catch (IOException e) {
            _logger.error("Error processing change in todotxt.", e);
        } finally {
            Util.close(writer);
            Util.close(reader);
            if (tempFile != null) {
                if (changeApplied) {
                    if (!tempFile.renameTo(todo)) {
                        try {
                            Files.move(tempFile.toPath(), todo.toPath());
                        } catch (IOException e) {
                            _logger.warn("Could not move file after processing.");
                        }
                    }
                }
            } else {
                throw new IllegalStateException("Could not access temp file.");
            }
        }
    }
}
