package com.itheima.service.cargo;

import com.itheima.domain.cargo.Export;
import com.itheima.domain.cargo.ExportExample;
import com.github.pagehelper.PageInfo;
import com.itheima.vo.ExportResult;


public interface ExportService {

    Export findById(String id);

    void save(Export export);

    void update(Export export);

    void delete(String id);

	PageInfo findAll(ExportExample example, int page, int size);

	//电子报运
    void updateE(ExportResult exportResult);
}
