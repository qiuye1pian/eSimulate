package org.esimulate.core.controller.device;

import lombok.extern.log4j.Log4j2;
import org.esimulate.core.model.device.PumpedStorageModel;
import org.esimulate.core.pojo.model.PumpedStorageModelDto;
import org.esimulate.core.pojo.model.PumpedStoragePageQuery;
import org.esimulate.core.service.device.PumpedStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/model/pumped-storage")
@Log4j2
public class PumpedStorageModelController {

    @Autowired
    private PumpedStorageService gridService;

    @PostMapping("/getListByPage")
    public Page<PumpedStorageModel> findListByPage(@RequestBody PumpedStoragePageQuery pageQuery) {
        return gridService.findListByPage(pageQuery);
    }

    @PostMapping("/add")
    public PumpedStorageModel addPumpedStorageModel(@RequestBody PumpedStorageModelDto gasBoilerModelDto) {
        if (gasBoilerModelDto.getId() != null) {
            return gridService.updatePumpedStorageModel(gasBoilerModelDto);
        }
        return gridService.addPumpedStorageModel(gasBoilerModelDto);
    }

    @PostMapping("/delete")
    public String deletePumpedStorageModel(@RequestBody PumpedStorageModelDto gasBoilerModelDto) {
        gridService.deleteById(gasBoilerModelDto.getId());
        return "抽水蓄能模型删除成功";
    }

}
