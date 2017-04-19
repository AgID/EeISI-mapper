package it.infocert.eigor.cli;

import java.nio.file.Path;

public class ReportFailuereCommand implements CliCommand {

    private final String msg;

    public ReportFailuereCommand(String format, Object... args) {
        msg = String.format(format, args);
    }

    @Override
    public void execute() {
        System.err.println(msg);
    }
}
