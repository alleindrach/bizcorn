package allein.bizcorn.common.aspect;

import org.springframework.cache.annotation.Cacheable;

@Cacheable(value="method", keyGenerator = "MethodKeyGeneratorCache30S")
public @interface CacheMethod30S {
}
