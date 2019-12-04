package com.itheima.service.cargo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.dao.cargo.ContractDao;
import com.itheima.dao.cargo.ContractProductDao;
import com.itheima.dao.cargo.ExtCproductDao;
import com.itheima.dao.cargo.FactoryDao;
import com.itheima.domain.cargo.*;
import com.itheima.service.cargo.ContractProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContractProductServiceImpl implements ContractProductService {
    @Autowired
    private ContractProductDao contractProductDao;

    @Autowired
    private ContractDao contractDao;

    @Autowired
    private ExtCproductDao extCproductDao;

    @Autowired
    private FactoryDao factoryDao;

    @Override
    public void save(ContractProduct contractProduct) {
        //1.设置货物id Controller中已设置
        //2 计算货物总金额
        double money = 0;
        if (contractProduct.getCnumber() != null && contractProduct.getPrice() != null){
            money = contractProduct.getCnumber() * contractProduct.getPrice();
        }
        //3 设置货物总金额
        contractProduct.setAmount(money);
        //4 保存货物
        contractProductDao.insertSelective(contractProduct);
        //5 根据id查询购销合同
        Contract contract = contractDao.selectByPrimaryKey(contractProduct.getContractId());
        //6 设置合同总金额
        contract.setTotalAmount(contract.getTotalAmount() + money);
        //7 设置合同总数量
        contract.setProNum(contract.getProNum() + 1);
        //8 更新合同
        contractDao.updateByPrimaryKeySelective(contract);
    }

    @Override
    public void update(ContractProduct contractProduct) {
        //1 计算修改后的货物总金额
        double newMoney = 0;
        if (contractProduct.getCnumber() != null && contractProduct.getPrice() != null){
            newMoney = contractProduct.getCnumber() * contractProduct.getPrice();
        }
        //2 查询修改前的货物总金额
        ContractProduct ocp = contractProductDao.selectByPrimaryKey(contractProduct.getId());
        Double oldMoney = ocp.getAmount();
        //3 设置货物金总额
        contractProduct.setAmount(newMoney);
        //4 更新货物
        contractProductDao.updateByPrimaryKeySelective(contractProduct);
        //5 查询购销合同
        Contract contract = contractDao.selectByPrimaryKey(contractProduct.getContractId());
        //6 设置购销合同总金额
        contract.setTotalAmount(contract.getTotalAmount() - oldMoney + newMoney);
        //7 更新购销合同
        contractDao.updateByPrimaryKeySelective(contract);

    }

    @Override//删除货物
    public void delete(String id) {
        //1 查询货物对象
        ContractProduct contractProduct = contractProductDao.selectByPrimaryKey(id);
        //2 查询此货物下的附件
        ExtCproductExample example = new ExtCproductExample();
        ExtCproductExample.Criteria criteria = example.createCriteria();
        criteria.andContractIdEqualTo(id);
        List<ExtCproduct> exts = extCproductDao.selectByExample(example);
        //3 查询购销合同
        Contract contract = contractDao.selectByPrimaryKey(contractProduct.getContractId());
        //4 计算总金额(货物金额 + 附件金额)
        Double money = contractProduct.getAmount();
        for (ExtCproduct ext : exts) {
            money += ext.getAmount();
            //5 删除附件
            extCproductDao.deleteByPrimaryKey(ext.getId());
        }
        //6 删除货物
        contractProductDao.deleteByPrimaryKey(id);
        //7 设置合同的总金额
        contract.setTotalAmount(contract.getTotalAmount() - money);
        //8 设置附件和货物数量
        contract.setProNum(contract.getProNum() - 1);
        contract.setExtNum(contract.getExtNum() - exts.size());
        //9 更新购销合同
        contractDao.updateByPrimaryKeySelective(contract);
    }

    @Override
    public ContractProduct findById(String id) {
        return contractProductDao.selectByPrimaryKey(id);
    }

    @Override
    public PageInfo findAll(ContractProductExample example, int page, int size) {
        PageHelper.startPage(page, size);
        List<ContractProduct> list = contractProductDao.selectByExample(example);
        return new PageInfo(list);
    }

    @Override
    @Transactional
    public void saveList(List<ContractProduct> contractProducts) {
        for (ContractProduct contractProduct : contractProducts) {
            //根据名称查询厂家的对象
            FactoryExample factoryExample = new FactoryExample();
            FactoryExample.Criteria criteria = factoryExample.createCriteria();
            criteria.andFactoryNameEqualTo(contractProduct.getFactoryName()).andCtypeEqualTo("货物");
            List<Factory> factoryList = factoryDao.selectByExample(factoryExample);
            if (factoryList != null && factoryList.size() > 0){
                contractProduct.setFactoryId(factoryList.get(0).getId());
            }
            this.save(contractProduct);
        }
    }
}
