package org.esimulate.core.controller.device;

import org.esimulate.core.model.device.SolarPowerModel;
import org.esimulate.core.pojo.model.SolarPowerModelDto;
import org.esimulate.core.pojo.model.SolarPowerPageQuery;
import org.esimulate.core.service.device.SolarPowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/model/solar-power")
public class SolarPowerModelController {

    @Autowired
    private SolarPowerService solarPowerService;

    @PostMapping("/getListByPage")
    public Page<SolarPowerModel> findListByPage(@RequestBody SolarPowerPageQuery pageQuery) {
        return solarPowerService.findListByPage(pageQuery);
    }


    /**
     * 新增光伏发电模型
     *
     * @param solarPowerModelDto 传入 JSON 数据
     * @return 返回新增的模型
     */
    @PostMapping("/add")
    public SolarPowerModel addSolarPowerModel(@RequestBody SolarPowerModelDto solarPowerModelDto) {
        if (solarPowerModelDto.getId() != null) {
            return solarPowerService.updateSolarPowerModel(solarPowerModelDto);
        }
        return solarPowerService.addSolarPowerModel(solarPowerModelDto);
    }

    /**
     * 删除光伏发电模型
     *
     * @param requestBody 前端传递的 JSON，包含 `id`
     * @return 操作结果
     */
    @PostMapping("/delete")
    public String deleteWindPowerModel(@RequestBody Map<String, Long> requestBody) {
        solarPowerService.deleteById(requestBody.get("id"));
        return "光伏发电模型删除成功";
    }
}
