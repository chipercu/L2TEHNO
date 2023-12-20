package utils_soft.common;

import l2open.gameserver.model.L2Player;
import utils_soft.common.DatabaseResurce.exceptions.ResourceProvideException;

/**
 * Created by a.kiperku
 * Date: 01.12.2023
 */
@FunctionalInterface
public interface CommandsFunction {

    void execute(L2Player player, String[] args) throws ResourceProvideException;

}
