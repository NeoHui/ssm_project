package com.itheima.service.cargo;

import com.itheima.domain.cargo.Contract;
import com.itheima.domain.cargo.ContractExample;
import com.github.pagehelper.PageInfo;
import com.itheima.vo.ContractProductVo;

import java.util.List;
import java.util.Map;


public interface ContractService {

	//根据id查询
    Contract findById(String id);

    //保存
    void save(Contract contract);

    //更新
    void update(Contract contract);

    //删除
    void delete(String id);

    //分页查询
	public PageInfo findAll(ContractExample example, int page, int size);

	//根据船期查询并封装vo对象
    List<ContractProductVo> findContractProductVoByShipTime(String inputDate, String companyId);

}
