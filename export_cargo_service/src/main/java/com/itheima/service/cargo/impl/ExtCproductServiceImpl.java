package com.itheima.service.cargo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.dao.cargo.ContractDao;
import com.itheima.dao.cargo.ExtCproductDao;
import com.itheima.domain.cargo.Contract;
import com.itheima.domain.cargo.ExtCproduct;
import com.itheima.domain.cargo.ExtCproductExample;
import com.itheima.service.cargo.ExtCproductService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class ExtCproductServiceImpl implements ExtCproductService {
    @Autowired
    private ExtCproductDao extCproductDao;
    @Autowired
    private ContractDao contractDao;
    @Override
    public void save(ExtCproduct extCproduct) {
        //1 计算附件的总金额
        double money = 0;
        if (extCproduct.getCnumber() != null && extCproduct.getPrice() != null) {
            money = extCproduct.getCnumber() * extCproduct.getPrice();
        }
        //2 设置附件总金额
        extCproduct.setAmount(money);
        //3 保存附件
        extCproductDao.insertSelective(extCproduct);
        //4 根据id查询购销合同
        Contract contract = contractDao.selectByPrimaryKey(extCproduct.getContractId());
        //5 设置购销合同的总金额
        contract.setTotalAmount(contract.getTotalAmount() + money);
        //6 设置附件数量
        contract.setExtNum(contract.getExtNum() + 1);
        //7 更新合同
        contractDao.updateByPrimaryKeySelective(contract);
    }

    @Override
    public void update(ExtCproduct extCproduct) {
        //计算修改后的总金额
        double newMoney = 0;
        if (extCproduct.getCnumber() != null && extCproduct.getPrice() != null){
            newMoney = extCproduct.getCnumber() * extCproduct.getPrice();
        }
        //获取修改前的总金额
        ExtCproduct extCproduct1 = extCproductDao.selectByPrimaryKey(extCproduct.getId());
        double oldMoney = extCproduct1.getAmount();
        //设置附件总金额
        extCproduct.setAmount(newMoney);
        //更新附件
        extCproductDao.updateByPrimaryKeySelective(extCproduct);
        //查询购销合同
        Contract contract = contractDao.selectByPrimaryKey(extCproduct.getContractId());
        //设置合同总金额
        contract.setTotalAmount(contract.getTotalAmount() - oldMoney + newMoney);
        //更新购销合同
        contractDao.updateByPrimaryKeySelective(contract);
    }

    @Override
    public void delete(String id) {
        //根据id查询附件
        ExtCproduct extCproduct = extCproductDao.selectByPrimaryKey(id);
        //获取删除附件的总金额
        Double amount = extCproduct.getAmount();
        //查询购销合同
        Contract contract = contractDao.selectByPrimaryKey(extCproduct.getContractId());
        //设置购销合同总金额
        contract.setTotalAmount(contract.getTotalAmount() - amount);
        //设置合同的附件数
        contract.setExtNum(contract.getExtNum() - 1);
        //更新合同
        contractDao.updateByPrimaryKeySelective(contract);
        //删除附件
        extCproductDao.deleteByPrimaryKey(id);
    }

    @Override
    public ExtCproduct findById(String id) {
        return extCproductDao.selectByPrimaryKey(id);
    }

    @Override
    public PageInfo findAll(ExtCproductExample example, int page, int size) {
        PageHelper.startPage(page, size);
        List<ExtCproduct> list = extCproductDao.selectByExample(example);
        return new PageInfo(list);
    }
}
