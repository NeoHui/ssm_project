package com.itheima.controller.cargo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.itheima.controller.BaseController;
import com.itheima.domain.cargo.ContractProduct;
import com.itheima.domain.cargo.ContractProductExample;
import com.itheima.domain.cargo.Factory;
import com.itheima.domain.cargo.FactoryExample;
import com.itheima.service.cargo.ContractProductService;
import com.itheima.service.cargo.FactoryService;
import com.itheima.utils.FileUploadUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/cargo/contractProduct")
public class ContractProductController extends BaseController {
    @Reference
    private ContractProductService contractProductService;

    @Reference
    private FactoryService factoryService;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @RequestMapping(value = "/list", name = "展示合同下货物列表数据")
    public String list(String contractId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size) {
        //-------------------构造货物列表-------------
        //1 创建example
        ContractProductExample example = new ContractProductExample();
        //2 创建criteria
        ContractProductExample.Criteria criteria = example.createCriteria();
        //3 添加查询条件
        criteria.andContractIdEqualTo(contractId);
        PageInfo pageInfo = contractProductService.findAll(example, page, size);
        request.setAttribute("page", pageInfo);

        //------------------------查询所有生产厂家--------------------
        FactoryExample factoryExample = new FactoryExample();
        FactoryExample.Criteria criteria1 = factoryExample.createCriteria();
        criteria1.andCtypeEqualTo("货物");
        List<Factory> factoryList = factoryService.findAll(factoryExample);
        request.setAttribute("factoryList", factoryList);
        //-------------------将购销合同id存入request----------------
        request.setAttribute("contractId", contractId);
        return "/cargo/product/product-list";
    }


    @RequestMapping(value = "/edit", name = "保存合同下方货物的方法")
    public String edit(ContractProduct contractProduct, MultipartFile productPhoto) throws IOException {
        //        文件的保存 七牛云
        try {
            String upload = fileUploadUtil.upload(productPhoto);
            contractProduct.setProductImage(upload); //把七牛云返回的url设置到表中
        } catch (Exception e) {
            e.printStackTrace();
        }

//        还是根据是否有id判断保存还是修改
        if (StringUtils.isEmpty(contractProduct.getId())) { //新增
            contractProduct.setId(UUID.randomUUID().toString());
            contractProduct.setCreateTime(new Date()); //创建时间
            contractProductService.save(contractProduct);
        } else {
            contractProduct.setUpdateTime(new Date());
            contractProductService.update(contractProduct);
        }
        return "redirect:/cargo/contractProduct/list.do?contractId=" + contractProduct.getContractId();
    }

    @RequestMapping(value = "/toUpdate", name = "进入修改合同的货物页面")
    public String toUpdate(String id) {
        ContractProduct contractProduct = contractProductService.findById(id);
        request.setAttribute("contractProduct", contractProduct);
        //查询所有厂家
        FactoryExample factoryExample = new FactoryExample();
        FactoryExample.Criteria criteria = factoryExample.createCriteria();
        criteria.andCtypeEqualTo("货物");
        List<Factory> factoryList = factoryService.findAll(factoryExample);
        request.setAttribute("factoryList", factoryList);
        return "/cargo/product/product-update";
    }

    @RequestMapping(value = "/delete", name = "删除货物方法")
    public String delete(String id, String contractId) {
        contractProductService.delete(id);
        return "redirect:/cargo/contractProduct/list.do?contractId=" + contractId;
    }

    @RequestMapping(value = "/toImport", name = "进入导入货物页面")
    public String toImport(String contractId) {
        request.setAttribute("contractId", contractId);
        return "cargo/product/product-import";
    }

    @RequestMapping(value = "/import", name = "批量导入合同货物的方法")
    public String importXls(String contractId, MultipartFile file) throws Exception {
        //1 创建一个新的工作簿
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        //2 获取一个工作表
        XSSFSheet sheet = workbook.getSheetAt(0);
        int lastRowIndex = sheet.getLastRowNum();//读取sheet的最后一行索引值
        //生产厂家 货号 数量	包装单位(PCS/SETS)  装率	箱数	单价	货物描述	要求
        ArrayList<ContractProduct> list = new ArrayList<>();
        for (int i = 1; i <= lastRowIndex; i++) {
            XSSFRow row = sheet.getRow(i);//获取行
            ContractProduct product = new ContractProduct();//货物
            String factoryName = row.getCell(1).getStringCellValue();//生产厂家
            product.setFactoryName(factoryName);
            String productNo = row.getCell(2).getStringCellValue();//货号
            product.setProductNo(productNo);
            Integer cnumber = ((Double) row.getCell(3).getNumericCellValue()).intValue();//数量
            product.setCnumber(cnumber);
            String packingUnit = row.getCell(4).getStringCellValue();//包装单位
            product.setPackingUnit(packingUnit);
            String loadingRate = row.getCell(5).getNumericCellValue() + "";//装率
            product.setLoadingRate(loadingRate);
            Integer boxNum = ((Double) row.getCell(6).getNumericCellValue()).intValue();//数量
            product.setBoxNum(boxNum);
            Double price = row.getCell(7).getNumericCellValue(); //单价
            product.setPrice(price);
            String productDesc = row.getCell(8).getStringCellValue(); // 货物描述
            product.setProductDesc(productDesc);
            String productRequest = row.getCell(9).getStringCellValue(); // 要求
            product.setProductRequest(productRequest);
            product.setContractId(contractId);//设置合同id
            //新增时需要赋企业id
            product.setCompanyId(getCompanyId());
            product.setCompanyName(getCompanyName());
            product.setId(UUID.randomUUID().toString());//设置随机id
            product.setCreateDept(getUser().getDeptId());
            product.setCreateBy(getUser().getId());
            product.setCreateTime(new Date());
            list.add(product);
        }
        contractProductService.saveList(list);//一次性保存
        return "redirect:/cargo/contractProduct/list.do?contractId=" + contractId;  //重定向到列表数据
    }
}
