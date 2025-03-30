package org.esimulate.core.controller.device;

import lombok.extern.log4j.Log4j2;
import org.esimulate.core.model.device.GasBoilerModel;
import org.esimulate.core.pojo.model.GasBoilerModelDto;
import org.esimulate.core.pojo.model.GasBoilerPageQuery;
import org.esimulate.core.service.device.GasBoilerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/model/gas-boiler")
@Log4j2
public class GasBoilerModelController {

    @Autowired
    private GasBoilerService gasBoilerService;

    @PostMapping("/getListByPage")
    public Page<GasBoilerModel> findListByPage(@RequestBody GasBoilerPageQuery pageQuery) {
        return gasBoilerService.findListByPage(pageQuery);
    }

    @PostMapping("/add")
    public GasBoilerModel addGasBoilerModel(@RequestBody GasBoilerModelDto gasBoilerModelDto) {
        if (gasBoilerModelDto.getId() != null) {
            return gasBoilerService.updateGasBoilerModel(gasBoilerModelDto);
        }
        return gasBoilerService.addGasBoilerModel(gasBoilerModelDto);
    }

}
