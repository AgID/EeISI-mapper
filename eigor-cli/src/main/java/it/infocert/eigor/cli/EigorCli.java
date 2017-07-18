package it.infocert.eigor.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EigorCli {

    private final static Logger log = LoggerFactory.getLogger(EigorCli.class);

    private final CommandLineInterpreter cli;

    public EigorCli(CommandLineInterpreter cli) {
        this.cli = cli;
    }

    public void run(String[] args) {
        CliCommand cliCommand = cli.parseCommandLine(args);
        cliCommand.execute(System.out, System.err);
    }
}
