package it.infocert.eigor.cli.commands;

import it.infocert.eigor.cli.CliCommand;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Command that prints out the help.
 */
public class HelpCommand implements CliCommand {

    @Override
    public int execute(PrintStream out, PrintStream err){
        try {
            String help = IOUtils.toString(getClass().getResourceAsStream("/help.txt"), StandardCharsets.UTF_8);
            System.out.println(help);
            return 0;
        } catch (IOException e) {
            return 1;
        }
    }

}
