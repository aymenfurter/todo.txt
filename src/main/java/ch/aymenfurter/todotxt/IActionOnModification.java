package ch.aymenfurter.todotxt;

import java.io.File;

public interface IActionOnModification {
    public void performAction(File todo, File done) ;
}
