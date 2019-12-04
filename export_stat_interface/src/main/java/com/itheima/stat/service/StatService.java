package com.itheima.stat.service;

import java.util.List;
import java.util.Map;

public interface StatService {
//厂家销量统计
    List<Map> factoryCharts(String companyId);
//产品销量统计
    List<Map> sellCharts(String companyId);
//24小时系统访问量
    List<Map> onlineCharts(String companyId);
}
