# eSimulate 后端服务

eSimulate 是一个用于多能源系统仿真与装机容量优化的科研平台。该仓库包含后端部分，使用 Spring Boot 构建，提供统一的 REST API。

## 项目简介
该应用用于模拟电力、热力等多种能源载体的供需情况，并通过粒子群优化（PSO）算法计算各类设备的最优装机容量组合，为能源规划提供决策参考。

## 核心特性
- **多能源数据生成器**：在 `dev` 包中包含光照、温度等时序数据生成器，可用于算法测试和仿真实验。
- **统一接口响应**：`common` 包定义了 ApiResponse、PaginationResponse 等通用响应对象，并提供全局异常和响应封装机制。
- **配置灵活**：`config` 包包括异步线程池、跨域、Fastjson、Spring Security 等配置；主要运行参数在 `application.yml` 中配置。
- **工具类**：`util` 包提供日期时间格式化、CSV 行解析、JWT/RSA 加解密等实用工具函数。
- **核心业务逻辑**：`core` 包按分层组织 controller、service、repository、model/pojo、pso 等，包含主要算法与业务逻辑。
- **框架层**：`frame` 包实现通用功能和抽象组件，如基础模型、仓储接口等。
- **日志与监控**：使用 Log4j2 作为日志框架，Actuator 提供健康检查与监控端点。

## 目录结构
    backend/
        └──eSimulate/
            ├── pom.xml
            └── src/
                ├── main/
                │   ├── java/org/esimulate/
                │   │   ├── ServerApplication.java        # 应用入口
                │   │   ├── common/                       # 通用响应和处理
                │   │   ├── config/                       # 配置类
                │   │   ├── core/                         # 核心业务逻辑
                │   │   ├── dev/                          # 数据生成器
                │   │   ├── frame/                        # 框架层代码
                │   │   └── util/                         # 工具类
                │   └── resources/
                │       ├── application.yml               # 运行配置
                │       └── log4j2.xml                    # 日志配置
                └── test/
                    └── java/                             # 单元测试

## 快速启动
1. **环境准备**：安装 JDK 8（或更高版本）、Maven 3.x 和 MySQL；在 MySQL 中创建数据库，例如 `eSimulate`。
2. **克隆项目并构建**：
   ```bash
   git clone https://github.com/qiuye1pian/eSimulate.git
   cd eSimulate/backend
   mvn clean install
3. **配置数据库**：修改 src/main/resources/application.yml 中的数据库 URL、用户名和密码。
4. **运行服务**：
    ```bash
    mvn spring-boot:run

5. **生成模拟数据**：在 dev 包下运行相应生成器，例如 SunlightCsvGenerator，生成光照或温度数据 CSV 文件。

贡献

欢迎通过 Issue 提出建议，或提交 Pull Request 改进代码、算法或文档。提交前请确保通过现有测试并遵循项目代码风格。