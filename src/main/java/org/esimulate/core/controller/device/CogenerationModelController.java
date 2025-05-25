package org.esimulate.core.controller.device;

import lombok.extern.log4j.Log4j2;
import org.esimulate.core.model.device.CogenerationModel;
import org.esimulate.core.pojo.model.CogenerationModelDto;
import org.esimulate.core.pojo.model.CogenerationPageQuery;
import org.esimulate.core.service.device.CogenerationModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/model/cogeneration-model")
@Log4j2
public class CogenerationModelController {

    @Autowired
    private CogenerationModelService cogenerationModelService;


    @PostMapping("/getListByPage")
    public Page<CogenerationModel> findListByPage(@RequestBody CogenerationPageQuery pageQuery) {
        return cogenerationModelService.findListByPage(pageQuery);
    }

    @PostMapping("/add")
    public CogenerationModel addCogenerationModel(@RequestBody CogenerationModelDto thermalPowerModelDto) {
        if (thermalPowerModelDto.getId() != null) {
            return cogenerationModelService.updateCogenerationModel(thermalPowerModelDto);
        }
        return cogenerationModelService.addCogenerationModel(thermalPowerModelDto);
    }

    @PostMapping("/delete")
    public String deleteCogenerationModel(@RequestBody CogenerationModelDto thermalPowerModelDto) {
        cogenerationModelService.deleteById(thermalPowerModelDto.getId());
        return "热电联产模型删除成功";
    }
}
