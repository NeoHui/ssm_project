package com.itheima.service.cargo.impl;

import com.itheima.dao.cargo.*;
import com.itheima.domain.cargo.*;
import com.itheima.service.cargo.ExportService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.vo.ExportProductResult;
import com.itheima.vo.ExportResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class ExportServiceImpl implements ExportService {

    @Autowired
    private ExportDao exportDao;
    @Autowired
    private ContractDao contractDao;
    @Autowired
    private ContractProductDao contractProductDao;
    @Autowired
    private ExportProductDao exportProductDao;
    @Autowired
    private ExtEproductDao extEproductDao;

    @Override
    public Export findById(String id) {
        return exportDao.selectByPrimaryKey(id);
    }

    @Override
    public void save(Export export) {
        //货物和附件数据的转移
        String contractIds = export.getContractIds();
        //查询合同下货物数据
        ContractProductExample contractProductExample = new ContractProductExample();
        //得到每个合同id
        List<String> contractIdList = Arrays.asList(contractIds.split(","));
        contractProductExample.createCriteria().andContractIdIn(contractIdList);
        List<ContractProduct> contractProductList = contractProductDao.selectByExample(contractProductExample);
        //根据合同下的货物数据向报运单下的货物表中插入数据
        for (ContractProduct contractProduct : contractProductList) {//合同下的货物数据
            ExportProduct exportProduct = new ExportProduct();//报运单下的货物数据
            BeanUtils.copyProperties(contractProduct, exportProduct);//spring框架的工具包，从一个对象属性值给另一个对象属性赋值
            exportProduct.setExportId(export.getId());//关联报运单id
            exportProductDao.insertSelective(exportProduct);//报运单添加货物
            List<ExtCproduct> extCproducts = contractProduct.getExtCproducts();//货物附件
            for (ExtCproduct extCproduct : extCproducts) {
                ExtEproduct extEproduct = new ExtEproduct();//报运单附件
                BeanUtils.copyProperties(extCproduct, extEproduct);//从货物附件向报运单附件赋值
                extEproduct.setExportId(export.getId());//关联报运单id
                extEproduct.setExportProductId(exportProduct.getId());//关联报运单货物id
                extEproductDao.insertSelective(extEproduct);//报运单添加附件
            }
        }
        //处理保存报运单之前还需要 货物总数、附件总数、客户名称使用空格连接
        ContractExample contractExample = new ContractExample();
        contractExample.createCriteria().andIdIn(contractIdList);
        List<Contract> contracts = contractDao.selectByExample(contractExample);
        Integer totalProNum = 0;//总货物数量
        Integer totalExtNum = 0;//总附件数量
        StringBuffer sb = new StringBuffer("");//用来接收多个合同的客户名称
        for (Contract contract : contracts) {
            totalProNum += contract.getProNum();
            totalExtNum += contract.getExtNum();
            sb.append(contract.getCustomName()).append("");
            //报运后的合同不再在合同管理列表显示
            contract.setState(2);
            contractDao.updateByPrimaryKeySelective(contract);
        }
        //  货物总数、附件总数、客户名称使用空格连接
        export.setProNum(totalProNum);
        export.setExtNum(totalExtNum);
        export.setCustomerContract(sb.toString());
        exportDao.insertSelective(export);//保存报运单
    }

    @Override
    public void update(Export export) {
        //需要修改两个表的数据
        List<ExportProduct> exportProducts = export.getExportProducts();
        if (exportProducts != null) {
            //报运单货物修改
            for (ExportProduct exportProduct : exportProducts) {
                exportProductDao.updateByPrimaryKeySelective(exportProduct);
            }
        }
        //报运单数据修改
        exportDao.updateByPrimaryKeySelective(export);
    }

    @Override
    public void delete(String id) {
        exportDao.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo findAll(ExportExample example, int page, int size) {
        PageHelper.startPage(page, size);
        List<Export> list = exportDao.selectByExample(example);
        return new PageInfo(list);
    }

    @Override//电子报运
    public void updateE(ExportResult exportResult) {
        String exportId = exportResult.getExportId();
        //把海关返回的数据更新到我们的表中  报运单表  报运单货物表
        Export export = new Export();
        export.setId(exportId);
        export.setState(exportResult.getState());
        export.setRemark(exportResult.getRemark());
        exportDao.updateByPrimaryKeySelective(export);
        //报运单货物
        Set<ExportProductResult> products = exportResult.getProducts();
        ExportProduct exportProduct = null;
        for (ExportProductResult product : products) {
            exportProduct = new ExportProduct();
            exportProduct.setId(product.getExportProductId());
            exportProduct.setTax(product.getTax());//设置税
            exportProductDao.updateByPrimaryKeySelective(exportProduct);
        }
    }
}
