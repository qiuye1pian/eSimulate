package org.esimulate.core.controller.device;

import lombok.extern.log4j.Log4j2;
import org.esimulate.core.model.device.GridModel;
import org.esimulate.core.pojo.model.GridModelDto;
import org.esimulate.core.pojo.model.GridPageQuery;
import org.esimulate.core.service.device.GridService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/model/grid")
@Log4j2
public class GridModelController {

    @Autowired
    private GridService gasBoilerService;

    @PostMapping("/getListByPage")
    public Page<GridModel> findListByPage(@RequestBody GridPageQuery pageQuery) {
        return gasBoilerService.findListByPage(pageQuery);
    }

    @PostMapping("/add")
    public GridModel addGridModel(@RequestBody GridModelDto gasBoilerModelDto) {
        if (gasBoilerModelDto.getId() != null) {
            return gasBoilerService.updateGridModel(gasBoilerModelDto);
        }
        return gasBoilerService.addGridModel(gasBoilerModelDto);
    }
    
    @PostMapping("/delete")
    public String deleteGridModel(@RequestBody GridModelDto gasBoilerModelDto) {
        gasBoilerService.deleteById(gasBoilerModelDto.getId());
        return "燃气锅炉模型删除成功";
    }

}
