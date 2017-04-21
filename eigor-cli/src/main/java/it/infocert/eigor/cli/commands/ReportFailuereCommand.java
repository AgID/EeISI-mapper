package it.infocert.eigor.cli.commands;

import it.infocert.eigor.cli.CliCommand;

import java.io.PrintStream;

public class ReportFailuereCommand implements CliCommand {

    private final String msg;

    public ReportFailuereCommand(String format, Object... args) {
        msg = String.format(format, args);
    }

    @Override
    public int execute(PrintStream out, PrintStream err) {
        err.println(getErrorMessage());
        return 1;
    }

    public String getErrorMessage() {
        return msg;
    }
}
