package com.itheima.dao.cargo;

import com.itheima.domain.cargo.ContractProduct;
import com.itheima.domain.cargo.ContractProductExample;
import com.itheima.vo.ContractProductVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContractProductDao {

	//删除
    int deleteByPrimaryKey(String id);

	//保存
    int insertSelective(ContractProduct record);

	//条件查询
    List<ContractProduct> selectByExample(ContractProductExample example);

	//id查询
    ContractProduct selectByPrimaryKey(String id);

	//更新
    int updateByPrimaryKeySelective(ContractProduct record);

    //根据合同id删除货物
    void deleteByContractId(String id);

}