package it.infocert.eigor.cli;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

/**
 * Created by danidemi on 19/04/17.
 */
public class HelpCommand implements CliCommand {

    @Override
    public void execute(){
        try {
            String help = IOUtils.toString(getClass().getResourceAsStream("/help.txt"));
            System.out.println(help);
        } catch (IOException e) {

        }
    }

}
