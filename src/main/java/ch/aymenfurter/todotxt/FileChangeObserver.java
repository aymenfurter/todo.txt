package ch.aymenfurter.todotxt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class FileChangeObserver {
    Logger _logger = LoggerFactory.getLogger(FileChangeObserver.class);

    private File _todoFile;
    private File _doneFile;
    private IActionOnModification _action;
    private boolean _createContainingDirectory;

    public FileChangeObserver (File todoFile, File doneFile, IActionOnModification todoAction) {
        this(todoFile, doneFile, todoAction, true);
    }

    public FileChangeObserver (File todoFile, File doneFile, IActionOnModification action, boolean createContainingDirectory) {
        _todoFile = todoFile;
        _doneFile = doneFile;
        _action = action;
        _createContainingDirectory = createContainingDirectory;
    }

    public void startObserve() {
        _logger.debug("Start observing target directory for changes");

        final Path basePath = FileSystems.getDefault().getPath(_todoFile.getAbsolutePath().substring(0, _todoFile.getAbsolutePath().lastIndexOf('/')));
        File directory = new File(basePath.toString());

        if (!directory.exists()) {
            directory.mkdir();
        }

        try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
            final WatchKey watchKey = basePath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            while (true) {
                _logger.debug("Checking for change in directory " + _todoFile.getAbsolutePath() + " ...");
                final WatchKey wk = watchService.take();
                for (WatchEvent<?> event : wk.pollEvents()) {
                    final Path changed = (Path) event.context();
                    _logger.debug("Detected change in containing directory.");
                    if (changed.endsWith(_todoFile.getAbsolutePath().substring(_todoFile.getAbsolutePath().lastIndexOf("/") + 1))) {
                        _logger.debug("Calling Perform Action");
                        _action.performAction(_todoFile, _doneFile);
                    }
                }

                boolean valid = wk.reset();
                if (!valid) {
                    _logger.info("Unregistered");
                }
            }
        } catch (IOException | InterruptedException e) {
            _logger.error("Error during processing change.", e);
        }
    }
}
