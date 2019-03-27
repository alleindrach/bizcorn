package allein.bizcorn.service.facade;

import org.springframework.web.bind.annotation.*;

public interface ICacheService {

    public
    @PutMapping("/cache/{key}/{value}/{expire}")
    Boolean put(@PathVariable String key, @PathVariable  String value, @PathVariable Long expire);
    public
    @GetMapping("/cache/{key}")
    String get(@PathVariable String key);
    public
    @RequestMapping("/cache/exists/{key}")
    Boolean exists(@PathVariable final String key);
    public
    @DeleteMapping("/cache/{key}")
    Boolean del(@PathVariable String key);

}
