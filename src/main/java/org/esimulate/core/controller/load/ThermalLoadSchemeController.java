package org.esimulate.core.controller.load;

import lombok.extern.log4j.Log4j2;
import org.esimulate.core.model.load.heat.ThermalLoadScheme;
import org.esimulate.core.pojo.LoadPageQuery;
import org.esimulate.core.pojo.ThermalLoadSchemeDto;
import org.esimulate.core.service.load.ThermalLoadSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@Log4j2
@RequestMapping("/load/thermal-load-schemes")
public class ThermalLoadSchemeController {

    @Autowired
    private ThermalLoadSchemeService thermalLoadSchemeService;

    @PostMapping("/getListByPage")
    public Page<ThermalLoadSchemeDto> getListByPage(@RequestBody LoadPageQuery loadPageQuery) {
        return thermalLoadSchemeService.getListByPage(loadPageQuery)
                .map(ThermalLoadSchemeDto::new);
    }

    @PostMapping("/add")
    public ThermalLoadScheme addThermalLoadScheme(@RequestBody ThermalLoadSchemeDto thermalLoadSchemeDto) {
        return thermalLoadSchemeService.addThermalLoadScheme(thermalLoadSchemeDto);
    }

    @PostMapping("/upload")
    public ThermalLoadScheme uploadFile(@RequestParam("id") Long id, @RequestParam("file") MultipartFile file) {
        // 1️⃣ 检查文件是否为空
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传失败：文件不能为空");
        }

        try {
            // 2️⃣ 解析文件内容
            List<String> lineList = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lineList.add(line);
                }
            }

            // 3️⃣ 解析后的内容
            log.debug("文件内容共有{}行", lineList.size());

            // 4️⃣ 返回更新后的对象
            return thermalLoadSchemeService.updateThermalLoadScheme(id, lineList);

        } catch (IOException ioException) {
            log.error("解析文件内容失败", ioException);
            throw new RuntimeException("解析文件内容失败", ioException);
        }
    }


}