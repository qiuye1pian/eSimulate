package org.esimulate.core.pojo.simulate;

import com.alibaba.fastjson2.JSONObject;
import org.esimulate.core.pojo.simulate.enums.EnvironmentTypeEnum;
import org.esimulate.core.pojo.simulate.enums.LoadTypeEnum;
import org.esimulate.core.pojo.simulate.enums.ModelTypeEnum;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SimulateConfigDtoTest {

    private static @NotNull SimulateConfigDto getSimulateConfigDto() {
        SimulateConfigDto simulateConfigDto = new SimulateConfigDto();

        List<LoadDto> loadDtoList = Arrays.asList(
                new LoadDto(LoadTypeEnum.ElectricLoad, 0L),
                new LoadDto(LoadTypeEnum.ThermalLoad, 1L));
        simulateConfigDto.setLoadDtoList(loadDtoList);

        List<ModelDto> modelDtoList = Arrays.asList(
                new ModelDto(ModelTypeEnum.WindPower, 2L, 1),
                new ModelDto(ModelTypeEnum.SolarPower, 3L, 2),
                new ModelDto(ModelTypeEnum.HydroPower, 4L, 1)
        );
        simulateConfigDto.setModelDtoList(modelDtoList);

        List<EnvironmentDto> environmentDtoList = Arrays.asList(
                new EnvironmentDto(EnvironmentTypeEnum.WindSpeed, 5L),
                new EnvironmentDto(EnvironmentTypeEnum.WaterSpeed, 6L),
                new EnvironmentDto(EnvironmentTypeEnum.Sunlight, 7L)
        );
        simulateConfigDto.setEnvironmentDtoList(environmentDtoList);
        return simulateConfigDto;
    }

    @Test
    public void test_simulate_config_dto_to_json() {
        SimulateConfigDto simulateConfigDto = getSimulateConfigDto();

        String expectedJson = "{\n" +
                "  \"environmentDtoList\": [\n" +
                "    {\"environmentTypeEnum\": \"WindSpeed\", \"id\": 5},\n" +
                "    {\"environmentTypeEnum\": \"WaterSpeed\", \"id\": 6},\n" +
                "    {\"environmentTypeEnum\": \"Sunlight\", \"id\": 7}\n" +
                "  ],\n" +
                "  \"loadDtoList\": [\n" +
                "    {\"id\": 0, \"loadTypeEnum\": \"ElectricLoad\"},\n" +
                "    {\"id\": 1, \"loadTypeEnum\": \"ThermalLoad\"}\n" +
                "  ],\n" +
                "  \"modelDtoList\": [\n" +
                "    {\"id\": 2, \"modelTypeEnum\": \"WindPower\", \"quantity\": 1},\n" +
                "    {\"id\": 3, \"modelTypeEnum\": \"SolarPower\", \"quantity\": 2},\n" +
                "    {\"id\": 4, \"modelTypeEnum\": \"HydroPower\", \"quantity\": 1}\n" +
                "  ]\n" +
                "}";

        assertEquals(JSONObject.parse(expectedJson), JSONObject.parse(JSONObject.toJSONString(simulateConfigDto)));
    }

    @Test
    public void test_反序列化() {
        String json = "{\n" +
                "    \"environmentDtoList\": [\n" +
                "        {\n" +
                "            \"environmentTypeEnum\": \"Sunlight\",\n" +
                "            \"id\": 1\n" +
                "        }\n" +
                "    ],\n" +
                "    \"loadDtoList\": [\n" +
                "        {\n" +
                "            \"id\": 13,\n" +
                "            \"loadTypeEnum\": \"ThermalLoad\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"modelDtoList\": [\n" +
                "        {\n" +
                "            \"id\": 2,\n" +
                "            \"modelTypeEnum\": \"ThermalPower\",\n" +
                "            \"quantity\": 1\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 2,\n" +
                "            \"modelTypeEnum\": \"GasBoiler\",\n" +
                "            \"quantity\": 1\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        SimulateConfigDto simulateConfigDto = JSONObject.parseObject(json, SimulateConfigDto.class);
        assertNotNull(simulateConfigDto.getEnvironmentDtoList());
        assertNotNull(simulateConfigDto.getLoadDtoList());
        assertNotNull(simulateConfigDto.getModelDtoList());
        assertEquals(1, simulateConfigDto.getEnvironmentDtoList().size());
        assertEquals(1, simulateConfigDto.getLoadDtoList().size());
        assertEquals(2, simulateConfigDto.getModelDtoList().size());
    }
}