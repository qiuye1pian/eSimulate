package org.esimulate.core.controller.device;

import lombok.extern.log4j.Log4j2;
import org.esimulate.core.model.device.BatteryModel;
import org.esimulate.core.model.device.HydroPowerPlantModel;
import org.esimulate.core.pojo.model.BatteryModelDto;
import org.esimulate.core.pojo.model.BatteryPageQuery;
import org.esimulate.core.pojo.model.HydroPowerPlantModelDto;
import org.esimulate.core.service.device.BatteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/model/battery")
@Log4j2
public class BatteryModelController {

    @Autowired
    private BatteryService batteryService;

    @PostMapping("/getListByPage")
    public Page<BatteryModel> findListByPage(@RequestBody BatteryPageQuery pageQuery) {
        return batteryService.findListByPage(pageQuery).map(BatteryModelDto::new);
    }

    /**
     * 新增电池模型
     *
     * @param batteryModelDto 传入 JSON 数据
     * @return 返回新增的模型
     */
    @PostMapping("/add")
    public BatteryModel addBatteryModel(@RequestBody BatteryModelDto batteryModelDto) {
        if (batteryModelDto.getId() != null) {
            return batteryService.updateBatteryModel(batteryModelDto);
        }
        return batteryService.addBatteryModel(batteryModelDto);
    }


    /**
     * 删除电池模型
     *
     * @param batteryModelDto 前端传递的 JSON，包含 `id`
     * @return 操作结果
     */
    @PostMapping("/delete")
    public String deleteBatteryModel(@RequestBody BatteryModelDto batteryModelDto) {
        batteryService.deleteById(batteryModelDto.getId());
        return "电池模型删除成功";
    }

    @PostMapping("/calculate_eta")
    public BigDecimal calculate_eta(@RequestBody HydroPowerPlantModelDto hydroPowerPlantModelDto) {
        hydroPowerPlantModelDto.setZ1(BigDecimal.ONE);
        hydroPowerPlantModelDto.setZ2(BigDecimal.ONE);
        hydroPowerPlantModelDto.setV1(BigDecimal.ONE);
        hydroPowerPlantModelDto.setV2(BigDecimal.ONE);
        hydroPowerPlantModelDto.setP1(BigDecimal.ONE);
        hydroPowerPlantModelDto.setP2(BigDecimal.ONE);
        hydroPowerPlantModelDto.setPg(BigDecimal.ONE);
        hydroPowerPlantModelDto.setG(BigDecimal.ONE);
        return new HydroPowerPlantModel(hydroPowerPlantModelDto).getEta();
    }

    @PostMapping("/calculate_head")
    public BigDecimal calculate_head(@RequestBody HydroPowerPlantModelDto hydroPowerPlantModelDto) {
        hydroPowerPlantModelDto.setEta1(BigDecimal.ONE);
        hydroPowerPlantModelDto.setEta2(BigDecimal.ONE);
        hydroPowerPlantModelDto.setEta3(BigDecimal.ONE);
        return new HydroPowerPlantModel(hydroPowerPlantModelDto).getHead();
    }

}
