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
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("id") Long id) {
        // 1️⃣ 检查文件是否为空
        if (file.isEmpty()) {
            return "上传失败：文件不能为空";
        }

        try {
            // 2️⃣ 解析文件内容
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

            // 3️⃣ 解析后的内容
            String fileContent = content.toString();
            log.debug("文件内容: \n{}", fileContent);

            //TODO: 把文件内容解析，更新到值

            // 4️⃣ 返回解析后的内容
            return "文件解析成功";

        } catch (IOException ioException) {
            log.error("解析文件内容失败", ioException);
            return "解析文件内容失败：" + ioException.getMessage();
        }
    }

}