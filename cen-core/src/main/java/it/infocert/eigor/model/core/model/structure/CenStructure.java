package it.infocert.eigor.model.core.model.structure;

import it.infocert.eigor.model.core.model.CenStructureSource;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Gives access to the CEN structure tree.
 * @see BtBgNode
 */
public class CenStructure extends CenStructureSource {

    CenStructure() {
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

    private BtBgNode parentOf(BtBgNode btBgNode) {
        Item item = findItemByBgBtName(btBgNode.getName()).orElseThrow(() -> new IllegalArgumentException("Unable to find an element with name '" + btBgNode + "'."));

        String parent = item.getParent();
        if(parent.isEmpty()){
            // it's the invoice, no parent at all!
            return null;
        }

        BtBgName nameOfParent = BtBgName.parse(parent.substring(0, 6));

        Item item1 = findItemByBgBtName(nameOfParent).orElse(null);

        if(item1 == null) return null;

        return findByName(item1.getBtBg() + item1.getNumber());

    }

    private Set<BtBgNode> childrenOf(BtBgNode btBgNode) {
        String key = btBgNode.getBtOrBg() + String.format("%04d", btBgNode.getNumber());
        return Arrays.stream(items)
                .filter( i -> i.getParent().startsWith(key) )
                .map( i -> new BtBgNode(i,this) )
                .collect(toSet());
    }

    private Item findItemByName(String btBgNumberName) {
        BtBgName name = BtBgName.parse(btBgNumberName);
        return findItemByBgBtName(name).orElseThrow(() -> new IllegalArgumentException("Unable to find an element with name '" + btBgNumberName + "'."));
    }

    private Optional<Item> findItemByBgBtName(BtBgName name) {
        return Arrays.stream(items).filter(i -> i.getBtBg().equalsIgnoreCase(name.bgOrBt()) && i.getNumber().equals( String.valueOf(name.number()) ) ).findFirst();
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
    }

}
