package org.esimulate.core.controller.device;

import lombok.extern.log4j.Log4j2;
import org.esimulate.core.model.device.WindPowerModel;
import org.esimulate.core.pojo.WindPowerChartDto;
import org.esimulate.core.pojo.WindPowerModelDto;
import org.esimulate.core.pojo.WindPowerPageQuery;
import org.esimulate.core.service.device.WindPowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        return windPowerService.addWindPowerModel(windPowerModel);
    }

    /**
     * 删除风力发电模型
     *
     * @param requestBody 前端传递的 JSON，包含 `id`
     * @return 操作结果
     */
    @PostMapping("/delete")
    public String deleteWindPowerModel(@RequestBody Map<String, Long> requestBody) {
        windPowerService.deleteById(requestBody.get("id"));
        return "风力发电模型删除成功";
    }

    @PostMapping("/show-graph")
    public WindPowerChartDto showGraph(@RequestBody WindPowerModelDto windPowerModelDto) {

        // 计算风速最大值：向上取整 `vOut * 1.1`
        int maxWindSpeed = windPowerModelDto.getV_out()
                .multiply(new BigDecimal("1.1"))
                .setScale(0, RoundingMode.CEILING)
                .intValue();

        // 生成 0 到 maxWindSpeed 的整数列表
        List<BigDecimal> windSpeedList = new ArrayList<>();
        for (int i = 0; i <= maxWindSpeed; i++) {
            windSpeedList.add(new BigDecimal(i));
        }

        List<BigDecimal> outPutList = WindPowerService.getWindPowerOutPutList(windPowerModelDto, windSpeedList);

        return new WindPowerChartDto(windSpeedList, outPutList);
    }

}
