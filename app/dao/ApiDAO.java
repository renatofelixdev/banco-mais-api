package dao;

import java.util.List;

public interface ApiDAO {

    public List<?> all();
    public Object byId(Long id);
}
