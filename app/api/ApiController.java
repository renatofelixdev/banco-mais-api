package api;

import play.mvc.Result;

public interface ApiController {

    public Result all();
    public Result byId(Long id);
    public Result save();
    public Result update(Long id);
    public Result delete(Long id);
    public Result alterStatus(Long id);
}
