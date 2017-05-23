package it.infocert.eigor.model.core.model.structure;

import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import it.infocert.eigor.model.core.model.CenStructureSource;

import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toSet;

/**
 * Gives access to the CEN structure tree.
 * @see BtBgNode
 */
public class CenStructure extends CenStructureSource {

    public CenStructure() {
        super();
    }

    /**
     * @param name "BT-002", "BG01", "bt 001".
     * @throws IllegalArgumentException When the node cannot be found in the CEN structure.
     */
    public BtBgNode findByName(String name) {
        Item item = findItemByName(name);
        return new BtBgNode(
                item.getBtBg(),
                Integer.parseInt( item.getNumber() ), this);
    }

    /**
     * @param name "BT-002", "BG01", "bt 001".
     * @throws IllegalArgumentException When the node cannot be found in the CEN structure.
     */
    public BtBgNode findByName(BtBgName name) {
        Item item = findItemByBgBtName(name);
        Preconditions.checkArgument(item!=null, "Unable to find an element with name '" + name + "'.");
        return new BtBgNode(
                item.getBtBg(),
                Integer.parseInt( item.getNumber() ), this);
    }

    private Item findItemByBgBtName(final BtBgName name) {

        Collection<Item> filter = Collections2.filter(Arrays.asList(items), new com.google.common.base.Predicate<Item>() {

            @Override
            public boolean apply(Item i) {
                return i.getBtBg().equalsIgnoreCase(name.bgOrBt()) && i.getNumber().equals(String.valueOf(name.number()));
            }

        });
        if(filter!=null && !filter.isEmpty()){
            return filter.iterator().next();
        }else{
            return null;
        }

    }

    private Item findItemByName(String btBgNumberName) {
        BtBgName name = BtBgName.parse(btBgNumberName);
        Item item = findItemByBgBtName(name);
        Preconditions.checkArgument(item!=null, "Unable to find an element with name '%s'.", btBgNumberName);
        return item;
    }

    private BtBgNode parentOf(BtBgNode btBgNode) {
        Item item = findItemByBgBtName(btBgNode.getName());

        Preconditions.checkArgument(item!=null, "Unable to find an element with name '%s'.", btBgNode);

        String parent = item.getParent();
        if(parent.isEmpty()){
            // it's the invoice, no parent at all!
            return null;
        }

        BtBgName nameOfParent = BtBgName.parse(parent.substring(0, 6));


        Item item1 = findItemByBgBtName(nameOfParent);
        if(item1 == null) return null;

        return findByName(item1.getBtBg() + item1.getNumber());

    }

    private Set<BtBgNode> childrenOf(BtBgNode btBgNode) {
        final String key = btBgNode.getBtOrBg() + String.format("%04d", btBgNode.getNumber());

        Collection<Item> filter = Collections2.filter(Arrays.asList(items), new com.google.common.base.Predicate<Item>() {

            @Override
            public boolean apply(Item item) {
                return item.getParent().startsWith(key);
            }
        });

        return new LinkedHashSet<>( Collections2.transform(filter, new com.google.common.base.Function<Item, BtBgNode>(){
            @Override
            public BtBgNode apply(Item item) {
                return new BtBgNode(item,CenStructure.this);
            }
        }));

    }


    /**
     * A node in the tree-like CEN structure.
     */
    public static final class BtBgNode {

        private final BtBgName btBgName;
        private final CenStructure manager;

        private BtBgNode(String btOrBg, int theNumber, CenStructure manager) {
            this.manager = manager;
            this.btBgName = BtBgName.parse(btOrBg+theNumber);
        }

        private BtBgNode(Item i, CenStructure manager) {
            this.manager = manager;
            this.btBgName = BtBgName.parse(i.getBtBg()+i.getNumber());
        }

        public String getBtOrBg() {
            return btBgName.bgOrBt();
        }

        public int getNumber() {
            return btBgName.number();
        }

        public BtBgName getName() {
            return btBgName;
        }

        public BtBgNode getParent() {
            return manager.parentOf(this);
        }

        @Override public String toString() {
            return btBgName.toString();
        }

        public Set<BtBgNode> getChildren() {
            return manager.childrenOf( this );
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            BtBgNode btBgNode = (BtBgNode) o;

            return btBgName.equals(btBgNode.btBgName);
        }

        @Override public int hashCode() {
            return btBgName.hashCode();
        }

        public boolean isBg() {
            return "BG".equalsIgnoreCase(btBgName.bgOrBt());
        }

        public boolean isBt() {
            return !isBg();
        }

        public String path() {
            String path = "";
            BtBgNode btBgNode = this;
            do {
                if(btBgNode.getParent()!=null) {
                    path = btBgNode.toString() + (path.isEmpty() ? "" : "/") + path;
                }
                btBgNode = btBgNode.getParent();
            }while(btBgNode!=null);
            return "/" + path;
        }
    }

}
