package it.infocert.eigor.model.core.model;

import java.util.ArrayList;
import java.util.Collection;

public class ParentAwareList<E extends AbstractBTBG> extends ArrayList<E> {

    private AbstractBTBG parent;

    public ParentAwareList(int initialCapacity, AbstractBTBG parent) {
        super(initialCapacity);
        this.parent = parent;
    }

    public ParentAwareList(AbstractBTBG parent) {
        super();
        this.parent = parent;
    }

    public ParentAwareList(Collection<? extends E> c, AbstractBTBG parent) {
        super(c);
        this.parent = parent;
    }
    
    public boolean add(E element) {
        element.setParent(parent);
        return super.add(element);
    }
    
    public void add(int index, E element) {
        element.setParent(parent);
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for (AbstractBTBG element: c) {
            element.setParent(parent);
        }
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        for (AbstractBTBG element: c) {
            element.setParent(parent);
        }
        return super.addAll(index, c);
    }

    @Override
    public E set(int index, E element) {
        element.setParent(parent);
        return super.set(index, element);
    }
}
