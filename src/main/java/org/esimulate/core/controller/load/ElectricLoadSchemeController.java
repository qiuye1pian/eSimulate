package org.esimulate.core.controller.load;

import org.esimulate.core.pojo.ElectricLoadSchemeDto;
import org.esimulate.core.pojo.LoadPageQuery;
import org.esimulate.core.service.load.ElectricLoadSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/load/electric-load-schemes")
public class ElectricLoadSchemeController {

    @Autowired
    private ElectricLoadSchemeService electricLoadSchemeService;

    @PostMapping("/getListByPage")
    public Page<ElectricLoadSchemeDto> getListByPage(@RequestBody LoadPageQuery loadPageQuery) {
        return electricLoadSchemeService.getListByPage(loadPageQuery)
                .map(ElectricLoadSchemeDto::new);
    }
}