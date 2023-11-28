package l2open.common.MultisellEditor;

import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.base.MultiSellEntry;
import l2open.gameserver.model.base.MultiSellIngredient;

import java.io.*;

import static l2open.gameserver.model.L2Multisell.*;

/**
 * Created by a.kiperku
 * Date: 28.11.2023
 */

public class Editor {

    public void createCopy() throws IOException, ClassNotFoundException {
        final MultiSellListContainer list = getInstance().getList(1);
        final MultiSellListContainer copy = copy(list);
        L2Multisell.getInstance().addMultiSellListContainer(list.getListId()  + 1_000_000_000, copy);
    }

    public void deleteItem(int id){
        final MultiSellListContainer list = getInstance().getList(id);
        list.getEntries().remove(id);
    }

    public void addItem(int id){
        final MultiSellListContainer list = getInstance().getList(id);

        final MultiSellEntry multiSellEntry = list.getEntries().get(1);

        multiSellEntry.addProduct(new MultiSellIngredient());
        multiSellEntry.addIngredient(new MultiSellIngredient());


    }

    public static MultiSellListContainer copy(MultiSellListContainer object) throws IOException, ClassNotFoundException {
        // Сериализуем объект
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);

        // Десериализуем объект
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        return (MultiSellListContainer) objectInputStream.readObject();
    }


}
