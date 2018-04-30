package dao;

import java.util.List;
import java.util.Optional;

public interface ApiDAO<T> {


    public List<T> all();

    //JAVA 8 OPTIONAL EVITAR NULL POINTER EXCEPTION
    public Optional<T> byId(Long id);
}
