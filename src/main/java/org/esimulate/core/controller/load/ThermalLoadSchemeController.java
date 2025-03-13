package org.esimulate.core.controller.load;

import org.esimulate.core.model.load.heat.ThermalLoadScheme;
import org.esimulate.core.pojo.LoadPageQuery;
import org.esimulate.core.pojo.ThermalLoadSchemeDto;
import org.esimulate.core.service.load.ThermalLoadSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/load/thermal-load-schemes")
public class ThermalLoadSchemeController {

    @Autowired
    private ThermalLoadSchemeService thermalLoadSchemeService;

    @PostMapping("/getListByPage")
    public Page<ThermalLoadSchemeDto> getListByPage(@RequestBody LoadPageQuery loadPageQuery) {
        return thermalLoadSchemeService.getListByPage(loadPageQuery)
                .map(ThermalLoadSchemeDto::new);
    }



}