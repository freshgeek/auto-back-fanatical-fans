#  托管到云服务器一直运行：


## 1 修改配置文件


- **首先修改pom配置文件（添加maven打jar包插件），添加如下代码**：

```xml
<build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <filters>
                                <filter>
                                    <artifact>*:*</artifact>
                                    <!-- 这里必须要填下面这段，否则报错 -->
                                    <excludes>
                                        <exclude>META-INF/*.SF</exclude>
                                        <exclude>META-INF/*.DSA</exclude>
                                         <exclude>META-INF/*.RSA</exclude>
                                    </excludes>
                                </filter>
                            </filters>
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <!-- 下面这里要填对入口类，否则会报错 -->
                                    <mainClass>top.chen.fansback.common.spider.csdn.BackFansSpider</mainClass>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```
## 2 本地运行

- **然后运行maven命令clean --> package，会在target目录下生成一个jar包文件**
  **先在本地运行试试能不能成功**

![image-20220724141031125](image/cloud_tech_jar.png)

![图像 1](image/cloud_tech_run.png)

**可以看到已经运行成功，接下来就是上云操作了**

## 3 上云

- 登录云服务器，打开面板（运维基础，这里就不多做解释了）

- 上传文件

  ![d](image/cloud_tech_upload.png)

- **在文件列表开启终端，输入命令 nohup java -jar auto-back-fanatical-fans-1.0-SNAPSHOT.jarr**

- **默认不显示 输出日志，需要加个命令**

![image](image/cloud_tech_nohup.png)

大功告成

![图像 008](image/cloud_tech_show_log.png)

****
