package utils_soft.common.DatabaseResurce.exceptions;

/**
 * Created by a.kiperku
 * Date: 19.12.2023
 */

public class ResourceProvideException extends Exception{

    public ResourceProvideException(String message) {
        super(message);
    }


    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
