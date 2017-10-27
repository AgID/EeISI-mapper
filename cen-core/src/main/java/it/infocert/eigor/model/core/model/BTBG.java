package it.infocert.eigor.model.core.model;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BTBG implements Serializable{

    public abstract BTBG getParent();

    public abstract int order();

    public abstract void accept(Visitor v);

    public String denomination(){
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
