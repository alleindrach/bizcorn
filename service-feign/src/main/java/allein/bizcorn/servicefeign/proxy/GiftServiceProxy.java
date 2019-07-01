/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.servicefeign.proxy;

import allein.bizcorn.service.facade.gate.IFileServiceGate;
import allein.bizcorn.service.facade.gate.IGiftServiceGate;
import allein.bizcorn.servicefeign.config.FeignMultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;

//动态注入proxy
@FeignClient(value = "service",
        configuration = FeignMultipartSupportConfig.class,
        fallback = GiftServiceHystric.class
)
public interface GiftServiceProxy extends IGiftServiceGate {
}
