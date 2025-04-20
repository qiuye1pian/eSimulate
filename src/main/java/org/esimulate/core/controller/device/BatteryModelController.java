package org.esimulate.core.controller.device;

import lombok.extern.log4j.Log4j2;
import org.esimulate.core.model.device.BatteryModel;
import org.esimulate.core.pojo.model.BatteryModelDto;
import org.esimulate.core.pojo.model.BatteryPageQuery;
import org.esimulate.core.service.device.BatteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/model/battery")
@Log4j2
public class BatteryModelController {

    @Autowired
    private BatteryService batteryService;

    @PostMapping("/getListByPage")
    public Page<BatteryModelDto> findListByPage(@RequestBody BatteryPageQuery pageQuery) {
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

}
