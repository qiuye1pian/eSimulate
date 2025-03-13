package org.esimulate.core.controller.load;

import org.esimulate.core.service.load.ElectricLoadSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/load/electric-load-schemes")
public class ElectricLoadSchemeController {

    @Autowired
    private ElectricLoadSchemeService electricLoadSchemeService;


}