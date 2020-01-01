## 使用Java多线程完成的爬虫项目

## 主要演化阶段
1. 根据基本的算法思路完成爬虫功能（全部写在了main方法中）
2. 第一次重构：将main方法中那些大段的代码抽取成方法（注意方法名要有清晰易懂，能表达代码功能）
3. 引入数据库（H2，无需安装，引入pom即可，将内存操作全部替换成JDBC操作，实现数据持久化
4. 第二次重构：将数据相关操作抽取到另一个类中，并写成接口，方便数据库模块的替换
5. 使用Mybatis替换JDBC简化数据库操作（主函数无需变动，只需要更换数据库依赖即可，体现了上一步接口的作用）
6. 将H2数据库替换为MySQL数据库（如果Sql写的比较通用，这里只需要替换几个配置即可）
7. 使用多线程进行爬取

## 进阶 - MySQL
1. 将MySQL数据量提升到百万级别（通过一个简单的程序将数据库中现有的几千条数据进行扩展）
2. 使用索引，体会索引的作用，理解MySQL索引的最左匹配原则

## 进阶 - Elasticsearch
1. 使用Elasticsearch创建亿级数据量，并体会Elasticsearch相比关系型数据库在大文本查询上的优势
ps: 由于亿级数据太多，所以这里自己只是重点练习了一下使用Docker安装Elasticsearch，
以及跟着文档简单的写了几个demo，对于Elasticsearch有了一个基本的了解  
参考链接：[Elasticsearch Java API](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.5/java-rest-high.html)  
注意：由于我们使用的是高版本的Client，所以需要参考 `Java High Level REST Client`