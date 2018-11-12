package it.infocert.eigor.model.core.model.structure;

/**
 * An object representing the name of a BT or BG node in the CEN structure.
 * <p>
 * To get an instance please use {@link BtBgName#parse(String)}.
 * </p>
 */
public class BtBgName {

    private final String bgOrBt;
    private final int number;

    /**
     * @param btBgName A BT/BG designator as "BT0001", "BG0", "bt-12" and similars.
     * @throws IllegalArgumentException If the provided string is not a valid BT/BG designator.
     */
    public static BtBgName parse(final String btBgName) {

        if (btBgName == null || btBgName.trim().length() < 3) throw buildException(btBgName);

        String s = btBgName.trim().toUpperCase();

        String bgOrBt = s.substring(0, 2);
        if (!"BT".equals(bgOrBt) && !"BG".equals(bgOrBt)) throw buildException(btBgName);

        String n = s.substring(2);

        String nn = "";
        boolean numFound = false;
        boolean isADigit;
        for (int i = 0; i < n.length(); i++) {

            char var = n.charAt(i);

            isADigit = var >= '0' && var <= '9';
            numFound = numFound || isADigit;

            if (!numFound && var != '-') throw buildException(btBgName);

            if (numFound && !isADigit) throw buildException(btBgName);
            if (isADigit) nn += var;
        }


        return new BtBgName(bgOrBt, Integer.parseInt(nn));

    }

    private static IllegalArgumentException buildException(String btBgName) {
        return new IllegalArgumentException(btBgName != null ? "Cannot parse '" + btBgName + "'." : "Cannot parse null values.");
    }

    private BtBgName(String bgOrBt, int number) {
        this.bgOrBt = bgOrBt;
        this.number = number;
    }

    public static String formatStandardCen(String name) {
        name = name.replace(" ", "");
        String identifier = name.substring(0, 2).toUpperCase();
        String body = name.substring(2);
        String number;
        if (body.startsWith("-")) {
            body = body.substring(1);
        }

        if (body.matches("^\\d+$")) {
            // remove leading zeroes
            number = parseNumber(body);
        } else if (body.matches("^\\d+-\\d*$")) {
            String[] slices = body.split("-");
            number = String.format("%s-%s", parseNumber(formatNum(slices[0])), parseNumber(slices[1]));
        } else {
            throw new IllegalArgumentException(String.format("Cannot format %s, should starts with either \"BT\" or \"BG\" followed by numbers. " +
                    "Example: \"BT0001\", \"BG0\", \"bt-12\" and similars.", name));
        }
        return String.format("%s-%s", identifier, number);

    }

    private static String parseNumber(String number) {
        return Long.valueOf(number).toString();
    }

    /**
     * @param name
     * @return
     */
    public static String formatPadded(String name) {
        name = name.replace(" ", "");
        String identifier = name.substring(0, 2).toUpperCase();
        String body = name.substring(2);
        String number;

        if (body.startsWith("-")) {
            body = body.substring(1);
        }
        if (body.matches("^\\d+$")) {
            number = formatNum(body);
        } else if (body.matches("^\\d+-\\d*$")) {
            String[] slices = body.split("-");
            number = String.format("%s-%s", formatNum(slices[0]), slices[1]);
        } else {
            throw new IllegalArgumentException(String.format("Cannot format %s, should starts with either \"BT\" or \"BG\" followed by numbers. " +
                    "Example: \"BT0001\", \"BG0\", \"bt-12\" and similars.", name));
        }
        return identifier + number;
    }


    private static String formatNum(String s) {
        return String.format("%04d", Integer.parseInt(s));
    }

    /**
     * Returns {@code "BT"} or {@code "BG"} uppercase.
     * @return {@code "BT"} or {@code "BG"} uppercase.
     */
    public String bgOrBt() {
        return bgOrBt;
    }


    /**
     * Returns the number associated to a BT or BG. For instance, for {@code BG0003} returns {@code 3}.
     */
    public int number() {
        return number;
    }

    @Override
    public String toString() {
        return bgOrBt + "-" + number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        BtBgName btBgName = (BtBgName) o;

        return number == btBgName.number && bgOrBt.equals(btBgName.bgOrBt);
    }

    @Override
    public int hashCode() {
        int result = bgOrBt.hashCode();
        result = 31 * result + number;
        return result;
    }
}
