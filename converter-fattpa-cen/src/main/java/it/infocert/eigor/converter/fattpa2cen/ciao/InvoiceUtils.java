package it.infocert.eigor.converter.fattpa2cen.ciao;

import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BTBG;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

public class InvoiceUtils {

    private final Reflections reflections;

    public InvoiceUtils(Reflections reflections) {
        this.reflections = reflections;
    }


    public BG0000Invoice ensurePathExists(String path, BG0000Invoice invoice) {

        List<String> namesOfBGs = new ArrayList<>(Arrays.asList(path.split("/")));
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
                if (getterMethod == null) {
                    throw new IllegalArgumentException(format("'%s' is wrong, '%s' doesn't have '%s' as child.", path, current.denomination(), name));
                }
                List<BTBG> children = (List<BTBG>) getterMethod.invoke(current);


                if (children.size() < 1) {
                    Class<? extends BTBG> childType = reflections.getSubTypesOf(BTBG.class)
                            .stream()
                            .filter(c ->
                                    c.getSimpleName()
                                            .startsWith(name)
                            )
                            .findFirst()
                            .orElse(null);

                    children.add(childType.newInstance());
                } else if (children.size() > 1) {
                    throw new IllegalArgumentException(
                            format("'%s' is wrong, too many '%s' childs found.",
                            path, current.denomination())
                    );
                }
                current = children.get(0);

            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return invoice;
    }
}
