package utils_soft.common.DatabaseResurce;

import java.util.List;

public interface Resource<T extends DataBaseTable>{

     T find(Filter filter);
     List<T> findAll();
     List<T> findList(Filter filter);

}