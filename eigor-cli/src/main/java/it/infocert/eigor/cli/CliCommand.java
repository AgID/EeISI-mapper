package it.infocert.eigor.cli;

import java.io.PrintStream;

public interface CliCommand {

    /**
     * Execute the command.
     * @param out The system output.
     * @param err The system err.
     * @return The exit code.
     */
    int execute(PrintStream out, PrintStream err);


}
