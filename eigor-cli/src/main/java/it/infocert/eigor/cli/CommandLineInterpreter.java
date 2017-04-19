package it.infocert.eigor.cli;

public interface CommandLineInterpreter {
    CliCommand parseCommandLine(String[] args);
}
