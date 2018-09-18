1. 已完成部分
   - 创建和删除namespace，用于未来可能需要的用户隔离
   - 创建和删除持久存储卷(storage)，基于glusterfs分布式存储系统
   - 创建和删除对内/对外应用(application)，封装kubernetes的pod、deployment、service等概念
2. 已支持特性
   - 手动设置应用对外服务端口，支持tcp/udp协议
   - 单应用可以包含多个容器(container)，互相访问使用localhost即可
   - 对内应用可以建立集群，kubernetes自动实现负载均衡(基于tcp)
   - 自动为对外应用分配外部ip地址(external ip)，需要外部路由设置配合
   - 挂载持久存储卷到应用，允许单storage多应用挂载
   - 注入环境变量到容器
3. 未支持特性
   - 注入配置文件到容器
   - web终端
   - 用户隔离，这个不一定非要做，看需求，需要分布式网络的支持，部分网络模式不支持用户隔离
4. 一些特殊说明
   1. 开发环境中kubernetes集群推荐使用一主二从或一主三从，可以比较明显的看到一些效果，集群默认不会在master节点上部署服务，所以要想单master节点进行开发和测试，需要进行特殊的设置，具体请Google
   2. 开发环境中kubernetes集群每个节点使用2C2G的配置就足够了，资源紧张的话1C1G也不是不可以
   3. 部署好kubernetes集群后推荐先部署kubernetes dashboard，可以监控到整个集群的状态
   4. 外部ip地址段配置在kubernetes.external-ip，是一个node名称到CIDR地址段的map，分配时自动从地址段中查找一个可用ip进行分配，注意要使外部ip地址生效需要手动配置路由，对于使用虚拟机进行开发的环境，如果虚拟机网络为桥接，则需要在路由器上设置静态路由，如果虚拟机网络为nat，则需要在本机上设置静态路由
   5. 使用持久存储卷storage的前提是为kubernetes集群配置好glusterfs分布式存储系统，如果使用其他后端存储，需要修改代码
5. kubernetes相关概念说明
   1. 以deployment为核心，每个deployment包含一个或多个相同配置的pod，每个deployment可以设置一个或多个docker镜像，这些镜像会反映到deployment下的每个pod上，生成对应的container
   2. 一个pod中的多个container共享文件系统，可以通过localhost互相无限制地访问网络
   3. deployment通过service暴露为服务，可选内部服务和外部服务
   4. 项目中将上述概念进行了封装，创建一个应用的过程包括了创建deployment和service的过程
