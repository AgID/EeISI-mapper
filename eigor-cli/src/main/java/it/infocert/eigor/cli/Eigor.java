package it.infocert.eigor.cli;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class Eigor {

    public static void main(String[] args) {
        new Eigor().run(args);
    }

    void run(String[] args) {

        OptionParser parser = new OptionParser();
        parser.accepts( "input" ).withRequiredArg();
        parser.accepts( "output" ).withRequiredArg();
        parser.accepts( "source" ).withRequiredArg();
        parser.accepts( "target" ).withRequiredArg();
        OptionSet options = parser.parse( args );
        String inputInvoicePath = (String)options.valueOf("input");
        String outputFolderPath = (String)options.valueOf("output");

        Path inputInvoice = FileSystems.getDefault().getPath(inputInvoicePath);
        if(Files.notExists(inputInvoice)){
            System.err.println(String.format("Input invoice '%s' does not exist.", inputInvoice));
        }

        Path outputFolder = FileSystems.getDefault().getPath(outputFolderPath);
        if(Files.notExists(outputFolder)){
            System.err.println(String.format("Output folder '%s' does not exist.", outputFolder));
        }

    }

}
