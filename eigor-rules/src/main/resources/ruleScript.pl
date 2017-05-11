#!/usr/bin/perl

#############################################
#                                           #
#   Cardinality Rules Prop File Generator   #
#   Infocert - EIGoR Project                #
#                                           #
#############################################

# This script generates a properties file from a Semantic Model CSV (File "EN16931-Matrix", sheet "Modello Semantico").
# It reads the CSV line by line and assemble properties in the format "BT-x = x..x", taking values from the first and third column
# of the CSV (Called "ID" and "Cardinality" respectively).

use strict;
use warnings FATAL => 'all';
use Text::CSV;

my $csv = Text::CSV->new({ sep_char => ',' });

printHelp(@ARGV);

my $input_file = $ARGV[0] or die "Need to get CSV file on the command line\n";
my $prop_file = $ARGV[1] or die "Need to get properties file on the command line\n";

open(INPUT, '<', $input_file) or die "Couldn't open $input_file, $!\n";
open(OUTPUT, '>', $prop_file) or die "Couldn't open $prop_file, $!\n";

print OUTPUT
    "# Cardinality validation rules for CEN-Core model
    # Syntax:
    #   ELEMENT_NAME = CARDINALITY\n";

my $count = 0;
while (my $line = <INPUT>) {
    if ($count eq 0) {
        $count++;
        next;
    }

    if ($csv->parse($line)) {
        my @fields = $csv->fields();
        if ($fields[0] eq "" || $fields[0] !~ m/B(T|G)-\d+/) {
            next;
        }
        print OUTPUT "$fields[0]=$fields[2]\n";
    }
}
close OUTPUT;
close INPUT;

sub printHelp {

    if (!@_)
    {
        my $help = <<"EOF";

usage: perl ruleScript.pl [-h | --help] [<source-file.csv> <target-file.properties>]

    Source file must be a valid Semantic Model CSV.
    Target file will be created if not existant.


EOF
        print "$help\n";
        exit;
    }
    my $first_arg = $_[0];
    if ($first_arg eq "-h" || $first_arg eq "--help")
    {
        # Print a help text

        my $help = <<"EOF";

usage: perl ruleScript.pl <source-file.csv> <target-file.properties>

    Source file must be a valid Semantic Model CSV.
    Target file will be created if not existant.
EOF
        print "$help\n";
        exit;
    }
}
