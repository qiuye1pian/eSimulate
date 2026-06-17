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
    ```

5. **生成模拟数据**：在 dev 包下运行相应生成器，例如 SunlightCsvGenerator，生成光照或温度数据 CSV 文件。

## Docker 运行

当前项目源码按 Java 8 编译，Docker 镜像也使用 Java 8。镜像构建阶段执行
`mvn clean package -DskipTests`，因为当前仓库存在既有业务测试断言失败。

### 使用 Docker Compose 启动后端和 MySQL

从仓库根目录运行：

```bash
docker compose up --build -d
docker compose ps
```

后端访问地址为 `http://localhost:8080/api`，健康检查地址为
`http://localhost:8080/api/actuator/health`。项目 MySQL 映射到宿主机
`3307`，避免与已有的 `3306` MySQL 冲突。

MySQL 首次创建数据卷时会导入 `脚本/db/dump-eSimulate-202506091753.sql`。
导入后，后端会使用 Hibernate `update` 模式将这个项目专属数据库对齐到当前实体结构。
停止服务不会删除数据：

```bash
docker compose down
```

仅在确认不再需要项目数据库数据时，才删除数据卷：

```bash
docker compose down -v
```

### 单独构建和运行后端镜像

```bash
docker build -t esimulate-backend ./code/backend/eSimulate

docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL='jdbc:mysql://host.docker.internal:3306/eSimulate_system?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8&allowPublicKeyRetrieval=true' \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=root \
  esimulate-backend
```

Docker 镜像构建阶段跳过测试，以便打包与测试验证可以独立执行。提交代码前仍应单独运行：

```bash
mvn test
```

容器连接宿主机上的 MySQL 时，应使用 `host.docker.internal`，不能使用
`127.0.0.1`。可复制仓库根目录的 `.env.example` 为 `.env`，覆盖 Compose
的默认密码和端口。

## 阿里云 ACR 自动构建

ACR 构建规则建议配置如下：

```text
代码源：https://github.com/qiuye1pian/eSimulate
构建分支：main 或 master，按 GitHub 实际默认分支选择
构建上下文：code/backend/eSimulate
Dockerfile 路径：Dockerfile
镜像仓库：crpi-jofg3qfjap2gegjm.cn-heyuan.personal.cr.aliyuncs.com/e-simulate/backend-and-database
镜像标签：latest；如 ACR 支持，再增加 commit SHA 标签
```

不要把 Dockerfile 放到仓库根目录并直接 `COPY target/*.jar`。ACR 自动构建只会执行
Dockerfile，不会预先在宿主机执行 Maven 打包；本项目的 Dockerfile 已在镜像内完成 Maven 构建。

## ECS Docker Compose 部署

仓库提供了 ECS 示例编排文件：

```text
deploy/aliyun/docker-compose.yml
```

该文件使用 ACR 后端镜像和 MySQL `8.0.41` 容器，数据库通过 named volume 持久化。

在 ECS 上安装 Docker 后：

```bash
docker login crpi-jofg3qfjap2gegjm.cn-heyuan.personal.cr.aliyuncs.com
mkdir -p /opt/esimulate
cd /opt/esimulate
```

把 `deploy/aliyun/docker-compose.yml` 放到 `/opt/esimulate/docker-compose.yml`，然后按需创建 `.env`：

```bash
cat > .env <<'EOF'
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=eSimulate
MYSQL_USER=simulator
MYSQL_PASSWORD=simulatorPassword
BACKEND_HOST_PORT=8080
MYSQL_HOST_PORT=3306
SPRING_JPA_HIBERNATE_DDL_AUTO=update
EOF
```

启动服务：

```bash
docker compose pull
docker compose up -d
docker compose ps
curl http://localhost:8080/api/actuator/health
```

如果使用 ECS Docker MySQL，需要首次导入数据库 dump。示例：

```bash
docker compose exec -T mysql mysql -uroot -p"$MYSQL_ROOT_PASSWORD" "$MYSQL_DATABASE" < dump-eSimulate-202506091753.sql
```

导入前请先把 SQL 文件上传到 ECS 当前目录，或改用绝对路径重定向。

环境变量清单：

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
SPRING_JPA_HIBERNATE_DDL_AUTO
MYSQL_ROOT_PASSWORD
MYSQL_DATABASE
MYSQL_USER
MYSQL_PASSWORD
BACKEND_HOST_PORT
MYSQL_HOST_PORT
```

数据库部署建议：

- 推荐方案：阿里云 RDS MySQL。优点是自动备份、恢复和运维更省心；成本较高。使用 RDS 时，ECS Compose 可以只部署后端，并把 `SPRING_DATASOURCE_URL` 指向 RDS 地址。
- 低成本方案：ECS Docker MySQL。当前 `deploy/aliyun/docker-compose.yml` 已包含 MySQL `8.0.41`、自动重启和 named volume。建议定期执行 `mysqldump` 备份，升级 MySQL 前先备份数据卷和 SQL dump。

## 贡献

欢迎通过 Issue 提出建议，或提交 Pull Request 改进代码、算法或文档。提交前请确保通过现有测试并遵循项目代码风格。
