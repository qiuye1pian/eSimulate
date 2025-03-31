package org.esimulate.core.controller.device;

import lombok.extern.log4j.Log4j2;
import org.esimulate.core.model.device.HydroPowerPlantModel;
import org.esimulate.core.pojo.model.HydroPowerPlantModelDto;
import org.esimulate.core.pojo.model.HydroPowerPlantPageQuery;
import org.esimulate.core.service.device.HydroPowerPlantModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/model/hydro_power_plant")
@Log4j2
public class HydroPowerPlantController {

    @Autowired
    private HydroPowerPlantModelService hydroPowerPlantService;

    @PostMapping("/getListByPage")
    public Page<HydroPowerPlantModel> findListByPage(@RequestBody HydroPowerPlantPageQuery pageQuery) {
        return hydroPowerPlantService.findListByPage(pageQuery);
    }

    @PostMapping("/add")
    public HydroPowerPlantModel addHydroPowerPlantModel(@RequestBody HydroPowerPlantModelDto hydroPowerPlantModelDto) {
        if (hydroPowerPlantModelDto.getId() != null) {
            return hydroPowerPlantService.updateHydroPowerPlantModel(hydroPowerPlantModelDto);
        }
        return hydroPowerPlantService.addHydroPowerPlantModel(hydroPowerPlantModelDto);
    }

    @PostMapping("/delete")
    public String deleteHydroPowerPlantModel(@RequestBody HydroPowerPlantModelDto hydroPowerPlantModelDto) {
        hydroPowerPlantService.deleteById(hydroPowerPlantModelDto.getId());
        return "风力发电模型删除成功";
    }

    @PostMapping("/calculate_eta")
    public BigDecimal calculate_eta(@RequestBody HydroPowerPlantModelDto hydroPowerPlantModelDto) {
        return new HydroPowerPlantModel(hydroPowerPlantModelDto).getEta();
    }

    @PostMapping("/calculate_head")
    public BigDecimal calculate_head(@RequestBody HydroPowerPlantModelDto hydroPowerPlantModelDto) {
        return new HydroPowerPlantModel(hydroPowerPlantModelDto).getHead();
    }

}
