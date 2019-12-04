package com.itheima.controller.cargo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.itheima.controller.BaseController;
import com.itheima.domain.cargo.*;
import com.itheima.service.cargo.ContractService;
import com.itheima.service.cargo.ExportProductService;
import com.itheima.service.cargo.ExportService;
import com.itheima.utils.BeanMapUtils;
import com.itheima.utils.DownloadUtil;
import com.itheima.utils.MailUtil;
import com.itheima.vo.ExportProductVo;
import com.itheima.vo.ExportResult;
import com.itheima.vo.ExportVo;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/cargo/export")
public class ExportController extends BaseController {
    @Reference
    private ContractService contractService;

    @RequestMapping(value = "/contractList", name = "进入合同管理列表")
    public String contractList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pagesize) {
        ContractExample contractExample = new ContractExample();
        ContractExample.Criteria criteria = contractExample.createCriteria();
        criteria.andCompanyIdEqualTo(getCompanyId()).andStateEqualTo(1);
        contractExample.setOrderByClause("create_time desc");
        PageInfo pageInfo = contractService.findAll(contractExample, page, pagesize);
        request.setAttribute("page", pageInfo);
        return "/cargo/export/export-contractList";
    }

    @Reference
    private ExportService exportService;

    @RequestMapping(value = "/list", name = "进入 出口报运 列表页面")
    public String list(@RequestParam(name = "page", defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int size) {
        ExportExample exportExample = new ExportExample();
        exportExample.createCriteria().andCompanyIdEqualTo(getCompanyId());
        exportExample.setOrderByClause("create_time desc");
        PageInfo pageInfo = exportService.findAll(exportExample, pageNum, size);
        request.setAttribute("page", pageInfo);
        return "cargo/export/export-list";
    }

    @RequestMapping(value = "/toExport", name = "进入填写报运单页面")
    public String toExport(String id) {
        /*
         * 在springmvc中，如果传递多个同名的参数，默认会将多个参数凭借为字符串
         *对多个购销合同进行报运
         * */
        request.setAttribute("id", id);
        return "cargo/export/export-toExport";
    }

    @RequestMapping(value = "/edit", name = "保存报运单方法")
    public String edit(Export export) {
        if (StringUtils.isEmpty(export.getId())) {
            export.setId(UUID.randomUUID().toString());
            export.setCompanyId(getCompanyId());
            export.setCompanyName(getCompanyName());
            export.setCreateDept(getUser().getDeptId());
            export.setCreateBy(getUser().getId());
            export.setCreateTime(new Date());
            export.setState(0);
            exportService.save(export);
        } else {
            export.setUpdateBy(getUser().getId());
            export.setUpdateTime(new Date());
            exportService.update(export);
        }
        return "redirect:/cargo/export/list.do";
    }

    @Reference
    private ExportProductService exportProductService;

    @RequestMapping(value = "/toUpdate", name = "进入修改报运单页面")
    public String toUpdate(String id) {//根据id查找进入哪个报运单
        //数据回显
        Export export = exportService.findById(id);
        request.setAttribute("export", export);
        //报运单下货物数据显示
        List<ExportProduct> exportProductList = exportProductService.findByExportId(id);
        request.setAttribute("eps", exportProductList);
        return "cargo/export/export-update";
    }

    @RequestMapping(value = "/toView", name = "/查看报运单")
    public String toView(String id) {
        //数据回显
        Export export = exportService.findById(id);
        request.setAttribute("export", export);

        return "/cargo/export/export-view";
    }

    @RequestMapping(value = "/exportE", name = "海关电子报运方法")
    public String exportE(String id) {//报运单id
        //webService远程调用
        ExportVo exportVo = new ExportVo();
        Export export = exportService.findById(id);
        BeanUtils.copyProperties(export, exportVo);
        //ExportVo需要手动设置exportId来自于Export的id,List<ExportProductVo>
        exportVo.setExportId(id);
        //接收需要放入exportVo货物的集合
        ArrayList<ExportProductVo> exportProductVoList = new ArrayList<>();
        //查询次报运单下的货物，把他们都转成exportProductVo
        List<ExportProduct> exportProductList = exportProductService.findByExportId(id);
        ExportProductVo exportProductVo = null;
        for (ExportProduct exportProduct : exportProductList) {
            exportProductVo = new ExportProductVo();
            BeanUtils.copyProperties(exportProduct, exportProductVo);
            exportProductVo.setEid(exportVo.getId());//手动设置Vo的Eid
            exportProductVo.setExportProductId(exportProduct.getId());//手动设置Vo的ExportProductId
            exportProductVoList.add(exportProductVo);
        }
        //向海关传送数据
        exportVo.setProducts(exportProductVoList);
        WebClient.create("http://localhost:9090/ws/export/ep").post(exportVo);
        //向海关获取结果
        ExportResult exportResult = WebClient.create("http://localhost:9090/ws/export/ep/" + id).get(ExportResult.class);
        exportService.updateE(exportResult);//将海关返回的数据更新到我们的表中
        return "redirect:/cargo/export/list.do";
    }

    @Autowired
    private DownloadUtil downloadUtil;

    @RequestMapping(value = "/exportPdf", name = "下载报运单pdf")
    public void exportPdf(String id) throws Exception {
        //报运单
        Export export = exportService.findById(id);
        //读取模板的路径
        String realPath = session.getServletContext().getRealPath("/template/export.jasper");
        //文件输入流
        InputStream inputStream = new FileInputStream(realPath);
        //准备模板中需要的参数 parameters中放报运单的数据  将bean对象转成map
        Map<String, Object> parameters = BeanMapUtils.beanToMap(export);
        //报运单的货物
        List<ExportProduct> exportProducts = exportProductService.findByExportId(id);
        //准备一个集合的数据源
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(exportProducts);
        //填充模板数据
        JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parameters, dataSource);
        byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //将pdf字节数组写入到缓存流中
        byteArrayOutputStream.write(bytes);
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String time = dateFormat.format(date);
        downloadUtil.download(byteArrayOutputStream, response, "报运单" + time + ".pdf");
    }
}
