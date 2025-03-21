package org.esimulate.core.controller.load;

import lombok.extern.log4j.Log4j2;
import org.esimulate.core.model.load.electric.ElectricLoadScheme;
import org.esimulate.core.pojo.ElectricLoadSchemeDto;
import org.esimulate.core.pojo.LoadPageQuery;
import org.esimulate.core.service.load.ElectricLoadSchemeService;
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

@Log4j2
@RestController
@RequestMapping("/load/electric-load-schemes")
public class ElectricLoadSchemeController {

    @Autowired
    private ElectricLoadSchemeService electricLoadSchemeService;

    @PostMapping("/getListByPage")
    public Page<ElectricLoadSchemeDto> getListByPage(@RequestBody LoadPageQuery loadPageQuery) {
        return electricLoadSchemeService.getListByPage(loadPageQuery)
                .map(ElectricLoadSchemeDto::new);
    }

    @PostMapping("/add")
        public ElectricLoadScheme addElectricLoadScheme(@RequestBody ElectricLoadSchemeDto electricLoadSchemeDto) {
        return electricLoadSchemeService.addElectricLoadScheme(electricLoadSchemeDto);
    }
    
    @PostMapping("/upload")
    public ElectricLoadScheme uploadFile(@RequestParam("id") Long id, @RequestParam("file") MultipartFile file) {
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
            return electricLoadSchemeService.updateElectricLoadScheme(id, lineList);

        } catch (IOException ioException) {
            log.error("解析文件内容失败", ioException);
            throw new RuntimeException("解析文件内容失败", ioException);
        }
    }

}