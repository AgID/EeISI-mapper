package it.infocert.eigor.cli;

import it.infocert.eigor.api.utils.EigorVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EigorCli {

    private final static Logger log = LoggerFactory.getLogger(EigorCli.class);

    private final CommandLineInterpreter cli;

    public EigorCli(CommandLineInterpreter cli) {
        this.cli = cli;
    }

    public void run(String[] args) {
        String eigorVersion = EigorVersion.getAsString();
        log.info(eigorVersion);
        System.out.println(eigorVersion);
        CliCommand cliCommand = cli.parseCommandLine(args);
        cliCommand.execute(System.out, System.err);
    }
}
