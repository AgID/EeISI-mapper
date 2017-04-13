package it.infocert.eigor.model.core;

import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BTBG;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

public class InvoiceUtils {

    private static Logger log = LoggerFactory.getLogger(InvoiceUtils.class);

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
                List<BTBG> children = getChildrenAsList(current, name);

                if (children == null) {
                    throw new IllegalArgumentException(format("'%s' is wrong, '%s' doesn't have '%s' as child.", path, current.denomination(), name));
                }

                if (children.size() < 1) {
                    Class<? extends BTBG> childType = getBtBgByName(name);

                    BTBG bg = childType.newInstance();
                    children.add(bg);
                } else if (children.size() > 1) {
                    throw new IllegalArgumentException(
                            format("'%s' is wrong, too many '%s' children found.",
                            path, current.denomination())
                    );
                }
                current = children.get(0);

            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }

        return invoice;
    }

    public List<BTBG> getChildrenAsList(BTBG parent, String childName) throws IllegalAccessException, InvocationTargetException {
        Method getterMethod = Arrays.stream(parent.getClass()
                .getMethods())
                .filter(method ->
                        method.getName()
                                .startsWith("get" + childName))
                .findFirst()
                .orElse(null);
        if (getterMethod == null) {
            return null;
        }

        return (List<BTBG>) getterMethod.invoke(parent);
    }

    public Class<? extends BTBG> getBtBgByName(String name) {
        return reflections.getSubTypesOf(BTBG.class)
                                .stream()
                                .filter(c ->
                                        c.getSimpleName()
                                                .startsWith(name)
                                )
                                .findFirst()
                                .orElse(null);
    }

    public BTBG getFirstChild(String path, BG0000Invoice invoice) {

        List<String> namesOfBGs = new ArrayList<>(Arrays.asList(path.split("/")));
        namesOfBGs.remove(0);

        BTBG current = invoice;

        try {
            for (String name : namesOfBGs) {
                List<BTBG> children = getChildrenAsList(current, name);

                if (children == null) {
                    throw new IllegalArgumentException(format("'%s' is wrong, '%s' doesn't have '%s' as child.", path, current.denomination(), name));
                }

               if (children.isEmpty()) {
                   return null;
               }
                current = children.get(0);

            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }

        return current;
    }

    public void addChild(BTBG bg, BTBG bt) throws IllegalAccessException, InvocationTargetException {
        List<BTBG> childrenAsList = getChildrenAsList(bg, bt.denomination());
        childrenAsList.add(bt);
    }

    //TODO Try to simplify duplicate code between this and getFirstChild()
    public boolean hasChild(String invoicePath, BG0000Invoice invoice) {
        List<String> namesOfBGs = new ArrayList<>(Arrays.asList(invoicePath.split("/")));
        namesOfBGs.remove(0);

        BTBG current = invoice;

        try {
            for (String name : namesOfBGs) {
                List<BTBG> children = getChildrenAsList(current, name);
                if (children != null && children.size() != 0) {
                    current = children.get(0);
                }
                if (current.denomination().equals(namesOfBGs.get(namesOfBGs.size() - 1 ))) {
                    return true;
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }
}
