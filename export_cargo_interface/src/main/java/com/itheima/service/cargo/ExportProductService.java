package com.itheima.service.cargo;


import com.itheima.domain.cargo.ExportProduct;
import com.itheima.domain.cargo.ExportProductExample;
import com.github.pagehelper.PageInfo;

import java.util.List;


public interface ExportProductService {

	ExportProduct findById(String id);

	void save(ExportProduct exportProduct);

	void update(ExportProduct exportProduct);

	void delete(String id);

	PageInfo findAll(ExportProductExample exportProductExample, int page, int size);

	List<ExportProduct> findByExportId(String id);
}
