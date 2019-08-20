package ch.aymenfurter.todotxt;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Entry Point for Todo txt app.
 */
public class TodoTxtApp {
    private static Logger LOGGER = LoggerFactory.getLogger(TodoTxtApp.class);

    public static void main(String[] args) {
        Options options = new Options();

        Option input = new Option("t", "todofile", true, "todo txt file to monitor");
        input.setRequired(false);
        options.addOption(input);

        Option output = new Option("o", "output", true, "done txt file to output");
        output.setRequired(false);
        options.addOption(output);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(0);
        }

        String todoFile = cmd.getOptionValue("todofile");
        String doneFile = cmd.getOptionValue("output");

        if (todoFile == null || todoFile.isEmpty()) {
            todoFile = System.getProperty("user.home") + "/todo/todo.txt";
        }

        if (doneFile == null || doneFile.isEmpty()) {
            doneFile = System.getProperty("user.home") + "/todo/done.txt";
        }

        LOGGER.info("Starting todo.txt application ...");

        FileChangeObserver observer = new FileChangeObserver(new File(todoFile), new File(doneFile), new DefaultModificationAction());
        observer.startObserve();
    }
}