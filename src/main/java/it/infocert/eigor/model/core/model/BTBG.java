package it.infocert.eigor.model.core.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface BTBG {
    int order();

    void accept(Visitor v);

    default String denomination(){
        String simpleName = getClass().getSimpleName();
        Pattern pattern = Pattern.compile("(..\\d*)\\D");
        Matcher matcher = pattern.matcher(simpleName);
        boolean b = matcher.find();
        if(b){
            return matcher.group(1);
        }else{
            return simpleName;
        }
    }
}
