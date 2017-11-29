package it.infocert.eigor.model.core;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import it.infocert.eigor.model.core.model.AbstractBT;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BTBG;
import it.infocert.eigor.model.core.model.structure.BtBgName;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static java.lang.String.format;
import static java.lang.reflect.Modifier.isAbstract;

public class InvoiceUtils {

    private static Logger log = LoggerFactory.getLogger(InvoiceUtils.class);

    private final Reflections reflections;

    public InvoiceUtils(Reflections reflections) {
        this.reflections = reflections;
    }

    /**
     * Ensure that all the BGs specified in the {@code path} are present ( and created if absent)
     * in the provided invoice.
     *
     * @param path    A path like "/BG0025/BG0026".
     * @param invoice The invoice where the path should be guaranteed.
     */
    public BG0000Invoice ensurePathExists(String path, BG0000Invoice invoice) {
        log.info("Ensuring path '{}' exists.", path);

        //checkArgument(path!=null && path.startsWith("/"), "Illegal path '%s'.", path);

        List<String> namesOfBGs = Lists.newArrayList(path.substring(1).split("/"));

        BTBG current = invoice;

        try {
            for (String name : namesOfBGs) {
                List<BTBG> children = getChildrenAsList(current, name);

                if (children == null) {
                    throw new IllegalArgumentException(format("'%s' is wrong, '%s' doesn't have '%s' as child. Possible children of '%s' are: %s.",
                            path,
                            current.denomination(),
                            name,
                            current.denomination(),
                            children));
                }

                if (name.startsWith("BT")) {
                    log.debug("Found BT {} while ensuring path existance of [{}]. Reached end of chain.", name, path);
                    continue;
                }

                if (children.size() < 1) {
                    Class<? extends BTBG> childType = getBtBgByName(name);
                    if (childType != null) {
                        BTBG bg = childType.newInstance();
                        children.add(bg);
                    } else {
                        throw new IllegalArgumentException(format("Name %s didn't return a valid class.", name));
                    }
                } else if (children.size() > 1) {
                    throw new IllegalArgumentException(
                            format("'%s' is wrong, too many '%s' found in '%s'.",
                                    path, children.get(0).denomination(), current.denomination())
                    );
                }
                current = children.get(0);

            }
        } catch (IllegalAccessException | InstantiationException e) {
            log.error(e.getMessage(), e);
        }

        return invoice;
    }

    /**
     * Returns the children of a parent given their name
     *
     * @param parent    the parent BG
     * @param childName the name of the children to look for
     * @return a {@link List} of children matching the given name
     */
    public List<BTBG> getChildrenAsList(BTBG parent, final String childName) {
        List<Method> methods = Arrays.asList(parent.getClass().getMethods());
        Collection<Method> filter = Collections2.filter(methods, new Predicate<Method>() {
            @Override
            public boolean apply(Method method) {
                return method.getName().startsWith("get" + BtBgName.format(childName)) &&
                        method.getParameterTypes().length == 0;
            }
        });

        if (filter == null || filter.isEmpty()) return null;

        Method getterMethod = filter.iterator().next();
        try {
            return (List<BTBG>) getterMethod.invoke(parent);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause == null || !(cause instanceof RuntimeException))
                throw new RuntimeException("An exception occurred while invoking " + getterMethod.toString() + " on " + parent, e);
            throw (RuntimeException) cause;
        }
    }

    /**
     * Return the class implementing a particular CEN element given the name
     *
     * @param name a {@link String} containing the name of the element
     * @return the {@link Class} corresponding to the wanted element
     */
    public Class<? extends BTBG> getBtBgByName(final String name) {

        Set<Class<? extends BTBG>> subTypesOf = reflections.getSubTypesOf(BTBG.class);

        Collection<Class<? extends BTBG>> filtered = Collections2.filter(subTypesOf, new Predicate<Class<? extends BTBG>>() {
            @Override
            public boolean apply(Class<? extends BTBG> c) {
                return c.getSimpleName().startsWith(BtBgName.format(name));
            }
        });

        if (filtered == null || filtered.isEmpty()) return null;
        else return filtered.iterator().next();

    }

    /**
     * Return the class implementing a particular CEN element given the name
     *
     * @param name a {@link BtBgName} representing the name of the element
     * @return the {@link Class} corresponding to the wanted element
     */
    public Class<? extends BTBG> getBtBgByName(final BtBgName name) {

        Set<Class<? extends BTBG>> subTypesOf = reflections.getSubTypesOf(BTBG.class);

        Collection<Class<? extends BTBG>> filter = Collections2.filter(subTypesOf, new Predicate<Class<? extends BTBG>>() {
            @Override
            public boolean apply(Class<? extends BTBG> c) {

                if (isAbstract(c.getModifiers())) return false;

                String substring = c.getSimpleName().substring(0, 6);
                BtBgName parse = BtBgName.parse(substring);
                return parse.equals(name);

            }
        });

        if (filter == null || filter.isEmpty()) return null;

        return filter.iterator().next();

    }

    /**
     * Get the first child of an invoice at the given path
     *
     * @param path the path of the child to return
     * @param invoice the invoice to traverse
     * @return the first child found
     */
    public BTBG getFirstChild(String path, BG0000Invoice invoice) {

        if (path.length() < 1) {
            throw new IllegalArgumentException("Wrong path: [" + path + "] is not a valid cen path");
        }

        List<String> namesOfBGs = Lists.newArrayList((path.substring(1)).split("/"));

        BTBG current = invoice;

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

        return current;
    }

    /**
     * Tries to add the given child to the given parent if it is possible.
     *
     * @return {@literal true} if the child has been added, {@literal false} otherwise.
     * @throws IllegalAccessException    If something goes wrong.
     * @throws InvocationTargetException If something goes wrong.
     */
    public boolean addChild(BTBG parentBg, BTBG childBt) throws IllegalAccessException, InvocationTargetException {
        List<BTBG> childrenAsList = getChildrenAsList(parentBg, childBt.denomination());
        if (childrenAsList != null) {
            childrenAsList.add(childBt);
            return true;
        }
        return false;
    }

    //TODO Try to simplify duplicate code between this and getFirstChild()
    public boolean hasChild(BG0000Invoice invoice, String path) {
        List<String> namesOfBGs = Lists.newArrayList((path.substring(1)).split("/"));

        BTBG current = invoice;

        for (String name : namesOfBGs) {
            List<BTBG> children = getChildrenAsList(current, name);
            if (children != null && children.size() != 0) {
                current = children.get(0);
            }
            if (current.denomination().equals(BtBgName.format(namesOfBGs.get(namesOfBGs.size() - 1)))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns all the existent BTs given a CEN path
     *
     * @param parent the parent BG into which to search
     * @param cenPath the path to the BT
     * @param bts    the list of found bts to populate
     * @return the populated bts list
     */
    public List<AbstractBT> getBtRecursively(BTBG parent, String cenPath, final List<AbstractBT> bts) {
        if (cenPath.startsWith("/")) {
            cenPath = cenPath.substring(1);
        }
        ArrayList<String> steps = Lists.newArrayList(cenPath.split("/"));

        return getBtRecursively(parent, steps, bts);
    }

    /**
     * Returns all the existent BTs given a chain of bg/bt
     *
     * @param parent the parent BG into which to search
     * @param steps  the list of subsequents steps of the chain
     * @param bts    the list of found bts to populate
     * @return the populated bts list
     */
    public List<AbstractBT> getBtRecursively(BTBG parent, final ArrayList<String> steps, final List<AbstractBT> bts) {
        List<BTBG> childrenAsList = getChildrenAsList(parent, steps.remove(0));
        for (BTBG btbg : childrenAsList) {
            if (btbg.getClass().getSimpleName().startsWith("BG")) {
                getBtRecursively(btbg, (ArrayList<String>) steps.clone(), bts);
            } else {
                bts.add((AbstractBT) btbg);
            }
        }

        return bts;
    }

}