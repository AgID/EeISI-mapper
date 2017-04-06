package it.infocert.eigor.converter.fattpa2cen.ciao;

import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BTBG;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dodo {

    private final Reflections reflections;

    public Dodo(Reflections reflections) {
        this.reflections = reflections;
    }


    public BG0000Invoice stuff(String s, BG0000Invoice invoice) {

        List<String> namesOfBGs = new ArrayList<>(Arrays.asList(s.split("/")));
        namesOfBGs.remove(0);

        BTBG current = invoice;

        try {
            for (String name : namesOfBGs) {
                Method getterMethod = Arrays.stream(current.getClass()
                        .getMethods())
                        .filter(method ->
                                method.getName()
                                        .startsWith("get" + name))
                        .findFirst()
                        .orElse(null);

                List<BTBG> children = (List<BTBG>) getterMethod.invoke(current);

                if (children.size() != 1) {
                    Class<? extends BTBG> childType = reflections.getSubTypesOf(BTBG.class)
                            .stream()
                            .filter(c ->
                                    c.getSimpleName()
                                            .startsWith(name)
                            )
                            .findFirst()
                            .orElse(null);

                    children.add(childType.newInstance());
                }
                current = children.get(0);

            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return invoice;
    }
}
