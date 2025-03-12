package org.esimulate.core.controller.device;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.esimulate.core.model.device.WindPowerModel;
import org.esimulate.core.pojo.WindPowerModelDto;
import org.esimulate.core.pojo.WindPowerPageQuery;
import org.esimulate.core.service.device.WindPowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/model/wind-power")
@Log4j2
public class WindPowerModelController {

    @Autowired
    private WindPowerService windPowerService;


    @PostMapping("/getListByPage")
    public Page<WindPowerModel> findListByPage(@RequestBody WindPowerPageQuery pageQuery) {
        return windPowerService.findListByPage(pageQuery);
    }


    /**
     * 新增风力发电模型
     *
     * @param windPowerModel 传入 JSON 数据
     * @return 返回新增的模型
     */
    @PostMapping("/add")
    public WindPowerModel addWindPowerModel(@RequestBody WindPowerModelDto windPowerModel) {
        log.info("新增风力发电模型：{}", JSONObject.toJSONString(windPowerModel));
        return windPowerService.addWindPowerModel(windPowerModel);
    }

    /**
     * 删除风力发电模型
     *
     * @param requestBody 前端传递的 JSON，包含 `id`
     * @return 操作结果
     */
    @PostMapping("/delete/{id}")
    public String deleteWindPowerModel(@RequestBody Map<String, Long> requestBody) {
        windPowerService.deleteById(requestBody.get("id"));
        return "风力发电模型删除成功";
    }


}
