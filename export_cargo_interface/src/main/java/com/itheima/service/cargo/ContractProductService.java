package com.itheima.service.cargo;

import com.itheima.domain.cargo.ContractProduct;
import com.itheima.domain.cargo.ContractProductExample;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * 业务层接口
 */
public interface ContractProductService {

	/**
	 * 保存
	 */
	void save(ContractProduct contractProduct);

	/**
	 * 更新
	 */
	void update(ContractProduct contractProduct);

	/**
	 * 删除
	 */
	void delete(String id);

	/**
	 * 根据id查询
	 */
	ContractProduct findById(String id);

	/**
	 * 分页查询
	 */
	PageInfo findAll(ContractProductExample example, int page, int size);

	//批量保存
	void saveList(List<ContractProduct> contractProducts);
}
