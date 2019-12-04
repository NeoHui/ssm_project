package com.itheima.service.cargo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.dao.cargo.ContractDao;
import com.itheima.dao.cargo.ContractProductDao;
import com.itheima.dao.cargo.ExtCproductDao;
import com.itheima.domain.cargo.Contract;
import com.itheima.domain.cargo.ContractExample;
import com.itheima.service.cargo.ContractService;
import com.itheima.vo.ContractProductVo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class ContractServiceImpl implements ContractService{
    @Autowired
    private ContractDao contractDao;
    @Autowired
    private ExtCproductDao extCproductDao;
    @Autowired
    private ContractProductDao contractProductDao;
    @Override
    public Contract findById(String id) {
        return contractDao.selectByPrimaryKey(id);
    }

    @Override
    public void save(Contract contract) {
        contractDao.insertSelective(contract);
    }

    @Override
    public void update(Contract contract) {
        contractDao.updateByPrimaryKeySelective(contract);
    }

    @Override
    public void delete(String id) {
        //删除附件
        extCproductDao.deleteByContractId(id);
        //删除货物
        contractProductDao.deleteByContractId(id);
        //删除合同
        contractDao.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo findAll(ContractExample example, int page, int size) {
        PageHelper.startPage(page,size);
        List<Contract> list = contractDao.selectByExample(example);
        return new PageInfo(list,5);
    }

    //根据船期查询封装vo
    @Override
    public List<ContractProductVo> findContractProductVoByShipTime(String shipTime, String companyId) {
        return contractDao.findContractProductVoByShipTime(shipTime,companyId);

    }
}
