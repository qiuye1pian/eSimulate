package org.esimulate.core.service.device;

import lombok.extern.log4j.Log4j2;
import org.esimulate.core.model.device.SolarPowerModel;
import org.esimulate.core.model.environment.sunlight.SunlightIrradianceValue;
import org.esimulate.core.model.environment.temperature.TemperatureValue;
import org.esimulate.core.pojo.chart.bar3d.SolarPower3DChartDto;
import org.esimulate.core.pojo.model.SolarPowerModelDto;
import org.esimulate.core.pojo.model.SolarPowerPageQuery;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;
import org.esimulate.core.repository.SolarPowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class SolarPowerService {

    @Autowired
    SolarPowerRepository solarPowerRepository;


    @Transactional(readOnly = true)
    public Page<SolarPowerModel> findListByPage(SolarPowerPageQuery pageQuery) {
        if (pageQuery.getModelName() == null || pageQuery.getModelName().trim().isEmpty()) {
            // ✅ 当 `modelName` 为空时，查询所有数据，但分页
            return solarPowerRepository.findAll(pageQuery.toPageable());
        }

        // ✅ 当 `modelName` 有值时，执行 `LIKE` 查询
        return solarPowerRepository.findByModelNameContaining(pageQuery.getModelName(), pageQuery.toPageable());
    }

    @Transactional
    public SolarPowerModel addSolarPowerModel(SolarPowerModelDto solarPowerModelDto) {
        Optional<SolarPowerModel> existingModel = solarPowerRepository.findByModelName(solarPowerModelDto.getModelName());
        if (existingModel.isPresent()) {
            throw new IllegalArgumentException("模型名称已存在: " + solarPowerModelDto.getModelName());
        }
        SolarPowerModel solarPowerModel = new SolarPowerModel(solarPowerModelDto);

        return solarPowerRepository.save(solarPowerModel);
    }

    /**
     * 根据 ID 删除模型
     *
     * @param id 模型 ID
     */
    @Transactional
    public void deleteById(Long id) {
        Optional<SolarPowerModel> existingModel = solarPowerRepository.findById(id);
        if (!existingModel.isPresent()) {
            throw new IllegalArgumentException("模型不存在，ID: " + id);
        }
        solarPowerRepository.deleteById(id);
    }

    @Transactional
    public SolarPowerModel updateSolarPowerModel(SolarPowerModelDto solarPowerModelDto) {
        Optional<SolarPowerModel> solarPowerModelOptional = solarPowerRepository.findById(solarPowerModelDto.getId());
        if (!solarPowerModelOptional.isPresent()) {
            throw new IllegalArgumentException("模型不存在，ID: " + solarPowerModelDto.getId());
        }
        SolarPowerModel solarPowerModel = solarPowerModelOptional.get();
        solarPowerModel.setP_pvN(solarPowerModelDto.getPpvN());
        solarPowerModel.setT_e(solarPowerModelDto.getTe());
        solarPowerModel.setT_ref(solarPowerModelDto.getTref());
        solarPowerModel.setG_ref(solarPowerModelDto.getGref());
        solarPowerModel.setCarbonEmissionFactor(solarPowerModelDto.getCarbonEmissionFactor());
        solarPowerModel.setCost(solarPowerModelDto.getCost());
        solarPowerModel.setPurchaseCost(solarPowerModelDto.getPurchaseCost());
        solarPowerModel.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return solarPowerRepository.save(solarPowerModel);
    }

    public static List<SolarPower3DChartDto> getSolarPowerOutPutList(SolarPowerModelDto solarPowerModelDto) {
        SolarPowerModel solarPowerModel = new SolarPowerModel(solarPowerModelDto);

        // 温度范围
        // 生成一个从 -50 到 50的列表，间隔为1
        List<TemperatureValue> temperatureList = new ArrayList<>();
        for (int t = -50; t <= 50; t++) {
            temperatureList.add(new TemperatureValue(BigDecimal.valueOf(t)));
        }
        // 生成 0 到 3000 的整数列表
        List<SunlightIrradianceValue> sunlightIrradianceValueList = new ArrayList<>();
        for (int g = 0; g <= 3000; g++) {
            sunlightIrradianceValueList.add(new SunlightIrradianceValue(BigDecimal.valueOf(g)));
        }

        List<SolarPower3DChartDto> solarPower3DChartDtoList = new ArrayList<>();

        for (TemperatureValue temperatureValue : temperatureList) {
            for (SunlightIrradianceValue sunlightIrradianceValue : sunlightIrradianceValueList) {
                Energy produce = solarPowerModel.produce(Arrays.asList(temperatureValue, sunlightIrradianceValue));
                solarPower3DChartDtoList.add(new SolarPower3DChartDto(temperatureValue.getValue(),sunlightIrradianceValue.getValue(),produce.getValue()));
            }
        }

        return solarPower3DChartDtoList;
    }
}
