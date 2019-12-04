package com.itheima.controller.cargo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.itheima.controller.BaseController;
import com.itheima.domain.cargo.ExtCproduct;
import com.itheima.domain.cargo.ExtCproductExample;
import com.itheima.domain.cargo.Factory;
import com.itheima.domain.cargo.FactoryExample;
import com.itheima.service.cargo.ExtCproductService;
import com.itheima.service.cargo.FactoryService;
import com.itheima.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/cargo/extCproduct")
public class ExtCproductController extends BaseController {
    @Reference
    private ExtCproductService extCproductService;
    @Reference
    private FactoryService factoryService;
    @Autowired
    private FileUploadUtil fileUploadUtil;

    @RequestMapping(value = "/list", name = "进入合同货物下附件页面")
    public String list(String contractId, String contractProductId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size) {
        //1 查询附件列表
        ExtCproductExample example = new ExtCproductExample();
        ExtCproductExample.Criteria criteria = example.createCriteria();
        criteria.andContractProductIdEqualTo(contractProductId);
        PageInfo pageInfo = extCproductService.findAll(example, page, size);
        request.setAttribute("page", pageInfo);
        //2 查询所有附件的生产厂家
        FactoryExample factoryExample = new FactoryExample();
        FactoryExample.Criteria criteria1 = factoryExample.createCriteria();
        criteria1.andCtypeEqualTo("附件");
        List<Factory> factoryList = factoryService.findAll(factoryExample);
        request.setAttribute("factoryList", factoryList);
        //3 页面参数
        request.setAttribute("contractId", contractId);
        request.setAttribute("contractProductId", contractProductId);
        return "/cargo/extc/extc-list";
    }

    @RequestMapping(value = "/edit", name = "保存合同下货物下的附件")
    public String edit(ExtCproduct extCproduct, MultipartFile productPhoto) {
        String imagePath = null;
        try {
            imagePath = fileUploadUtil.upload(productPhoto);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            imagePath = "";
        }
        extCproduct.setProductImage(imagePath);//吧图片的保存路径交给数据库
        if (StringUtils.isEmpty(extCproduct.getId())){
            extCproduct.setId(UUID.randomUUID().toString());
            extCproduct.setCompanyId(getCompanyId());
            extCproduct.setCompanyName(getCompanyName());
            extCproduct.setCreateTime(new Date());
            extCproduct.setCreateBy(getUser().getId());
            extCproductService.save(extCproduct);
        }else {
            extCproduct.setUpdateTime(new Date());
            extCproduct.setUpdateBy(getUser().getId());
            extCproductService.update(extCproduct);
        }
        return "redirect:/cargo/extCproduct/list.do?contractId="+extCproduct.getContractId()+"&contractProductId="+extCproduct.getContractProductId();
    }

    @RequestMapping(value = "/toUpdate",name = "进入修改附件页面")
    public String toUpdate(String id,String contractId,String constractProductId){
        //附件对象
        ExtCproduct extCproduct = extCproductService.findById(id);
        request.setAttribute("extCproduct",extCproduct);
        //页面参数
        request.setAttribute("contractId",contractId);
        request.setAttribute("constractProductId",constractProductId);
        //厂家
        FactoryExample factoryExample = new FactoryExample();
        FactoryExample.Criteria criteria = factoryExample.createCriteria();
        criteria.andCtypeEqualTo("附件");
        List<Factory> factoryList = factoryService.findAll(factoryExample);
        request.setAttribute("factoryList",factoryList);
        return "/cargo/extc/extc-update";
    }

    @RequestMapping(value = "/delete",name = "删除附件")
    public String delete(String id,String contractId,String contractProductId){
        extCproductService.delete(id);
        return "redirect:/cargo/extCproduct/list.do?contractId="+contractId+"&contractProductId="+contractProductId;
    }
}