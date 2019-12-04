package com.itheima.service.cargo;



import com.itheima.domain.cargo.ExtCproduct;
import com.itheima.domain.cargo.ExtCproductExample;
import com.github.pagehelper.PageInfo;

import java.util.Map;

/**

 */
public interface ExtCproductService {
	/**
	 * 保存
	 */
	void save(ExtCproduct extCproduct);

	/**
	 * 更新
	 */
	void update(ExtCproduct extCproduct);

	/**
	 * 删除
	 */
	void delete(String id);

	/**
	 * 根据id查询
	 */
	ExtCproduct findById(String id);

	/**
	 * 分页查询
	 */
	PageInfo findAll(ExtCproductExample example, int page, int size);
}
