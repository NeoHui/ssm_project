package com.itheima.controller.cargo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.itheima.controller.BaseController;
import com.itheima.domain.cargo.Contract;
import com.itheima.domain.cargo.ContractExample;
import com.itheima.service.cargo.ContractService;
import com.itheima.utils.DownloadUtil;
import com.itheima.vo.ContractProductVo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sun.awt.SunHints;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("cargo/contract")
public class ContractController extends BaseController {
    @Reference
    private ContractService contractService;

    @RequestMapping(value = "/list", name = "进入购销合同页面")
    public String list(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        ContractExample example = new ContractExample();
        //创建criteria
        ContractExample.Criteria criteria = example.createCriteria();
        //指定条件
        criteria.andCompanyIdEqualTo(getCompanyId());
        /*
         *细颗粒度权限控制：1获取当前用户的级别
         *                2根据不同用户级别添加不同的查询条件
         */
        //设置排序
        Integer degree = getUser().getDegree();
        if (degree == 4) {//普通员工
            criteria.andCreateByEqualTo(getUser().getId());
        } else if (degree == 3) {//管理本部门，经理：部门id
            criteria.andCreateByEqualTo(getUser().getDeptId());
        } else if (degree == 2) {//管理下属部门和人员
            criteria.andCreateDeptLike(getUser().getDeptId() + "%");
        }
        //1 企业管理员
        example.setOrderByClause("create_time desc");
        PageInfo pageInfo = contractService.findAll(example, page, size);
        request.setAttribute("page", pageInfo);
        return "cargo/contract/contract-list";
    }

    @RequestMapping(value = "/toAdd", name = "进入保存合同页面")
    public String toAdd() {
        return "/cargo/contract/contract-add";
    }

    @RequestMapping(value = "/edit", name = "保存合同的方法")
    public String edit(Contract contract) {
        if (StringUtils.isEmpty(contract.getId())) {
            //id为空则新增方法，需要设置id
            contract.setId(UUID.randomUUID().toString());
            //设置状态 0：草稿  1：已经确认
            contract.setState(0);
            contract.setCompanyId(getCompanyId());
            contract.setCompanyName(getCompanyName());
            contract.setCreateTime(new Date());
            contract.setCreateBy(getUser().getId());//细颗粒度控制时需要此字段
            contract.setCreateDept(getUser().getDeptId());//细颗粒度
            contractService.save(contract);
        } else {
            //修改 方法
            contract.setUpdateBy(getUser().getId());
            contract.setCreateDept(getUser().getDeptId());
            contractService.update(contract);
        }
        return "redirect:/cargo/contract/list.do";
    }

    @RequestMapping(value = "/toUpdate", name = "进入修改合同页面")
    public String toUpdate(String id) {
        Contract contract = contractService.findById(id);
        request.setAttribute("contract", contract);
        return "/cargo/contract/contract-update";
    }

    @RequestMapping(value = "/submit", name = "提交合同的方法")
    public String submit(String id) {
        //将合同状态改为1
        Contract contract = new Contract();
        contract.setId(id);
        contract.setState(1);
        contractService.update(contract);
        return "redirect:/cargo/contract/list.do";
    }

    @RequestMapping(value = "/cancel", name = "取消合同方法")
    public String cannel(String id) {
        //将状态该为0
        Contract contract = new Contract();
        contract.setId(id);
        contract.setState(0);
        contractService.update(contract);
        return "redirect:/cargo/contract/list.do";
    }

    @RequestMapping(value = "/delete", name = "删除合同")
    public String delete(String id) {
        contractService.delete(id);
        return "redirect:/cargo/contract/list.do";
    }

    @RequestMapping(value = "/print", name = "进入（出货表）打印合同页面")
    public String print() {
        return "cargo/print/contract-print";
    }

    @Autowired
    private DownloadUtil downloadUtil;

    @RequestMapping(value = "/printExcel", name = "进入导出出货表页面")
    public void printExcel(String inputDate) throws Exception {
        //根据船期查询数据
        List<ContractProductVo> contractProductVoList = contractService.findContractProductVoByShipTime(inputDate, getCompanyId());
        //导出excel
        // 创建空的excel
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("POI测试");
        //设置sheet的列宽
        sheet.setColumnWidth(0, 4200);//第一列 无内容
        sheet.setColumnWidth(1, 256 * 26);
        sheet.setColumnWidth(2, 16 * 256);
        sheet.setColumnWidth(3, 26 * 256);
        sheet.setColumnWidth(4, 16 * 256);
        sheet.setColumnWidth(5, 16 * 256);
        sheet.setColumnWidth(6, 16 * 256);
        sheet.setColumnWidth(7, 16 * 256);
        sheet.setColumnWidth(8, 16 * 256);
        Row bigTitleRow = sheet.createRow(0);//标题行
        //创建单元格
        for (int i = 1; i <= 8; i++) {
            bigTitleRow.createCell(i);
        }
        //设置行高
        bigTitleRow.setHeightInPoints(36);
        //合并单元格
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 8));
        //向合并后的单元格写入一句话：2012年8月份出货表
        Cell cell = bigTitleRow.getCell(1);
        cell.setCellValue(inputDate.replace("-0", "年").replace("-", "年") + "月份出货表");
        cell.setCellStyle(bigTitle(workbook));
        //创建小标题行
        Row titleRow = sheet.createRow(1);
        String[] titles = {"客户", "合同号", "货号", "数量", "工厂", "工厂交期", "船期", "贸易条款"};
        for (int i = 1; i <= 8; i++) {
            cell = titleRow.createCell(i);
            cell.setCellStyle(title(workbook));
            cell.setCellValue(titles[i - 1]);
        }
        //内容   contractProductVoList
        int rowIndex = 2;
        Row row = null;
        for (ContractProductVo contractProductVo : contractProductVoList) {
            row = sheet.createRow(rowIndex);
            cell = row.createCell(1);
            cell.setCellValue(contractProductVo.getCustomName());
            cell.setCellStyle(text(workbook));

            cell = row.createCell(2);
            cell.setCellValue(contractProductVo.getContractNo());
            cell.setCellStyle(text(workbook));

            cell = row.createCell(3);
            cell.setCellValue(contractProductVo.getProductNo());
            cell.setCellStyle(text(workbook));

            cell = row.createCell(4);
            cell.setCellValue(contractProductVo.getCnumber());
            cell.setCellStyle(text(workbook));

            cell = row.createCell(5);
            cell.setCellValue(contractProductVo.getFactoryName());
            cell.setCellStyle(text(workbook));
//            工厂交期	船期	贸易条款

            cell = row.createCell(6);
            cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(contractProductVo.getDeliveryPeriod()));
            cell.setCellStyle(text(workbook));

            cell = row.createCell(7);
            cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(contractProductVo.getShipTime()));
            cell.setCellStyle(text(workbook));

            cell = row.createCell(8);
            cell.setCellValue(contractProductVo.getTradeTerms());
            cell.setCellStyle(text(workbook));
            rowIndex++;
        }
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String nowDay = dateFormat.format(date);
        //        把workbook文件导出
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);//把workBook内容写到缓存流中
        downloadUtil.download(byteArrayOutputStream, response, "出货表-"+nowDay+".xlsx");
    }

    @RequestMapping(value = "/printExcelWithTemplate", name = "模板导出出货表")
    public void printExcelWithTemplate(String inputDate) throws Exception {
        List<ContractProductVo> contractProductVoList = contractService.findContractProductVoByShipTime(inputDate, getCompanyId());
        //获取项目的根路径-->模板文件路径
        String filePath = session.getServletContext().getRealPath("/make/xlsprint/tOUTPRODUCT.xlsx");
        FileInputStream fileInputStream = new FileInputStream(filePath);//读取模板的文件流
        Workbook workbook = new XSSFWorkbook(fileInputStream);//获取工作表
        Sheet sheet = workbook.getSheetAt(0);//获取sheet
        Cell cell = sheet.getRow(0).getCell(1);
        cell.setCellValue(inputDate.replace("-0", "年").replace("-", "年") + "月份出货表");//bigTitleRow文字
        CellStyle[] cellStyles = new CellStyle[8];//获取模板中的8个样式
        Row row = null;
        row = sheet.getRow(2);
        for (int i = 1; i <= 8; i++) {//获取模板中第三行的八个样式
            cellStyles[i - 1] = row.getCell(i).getCellStyle();//每个cell的样式
        }
        int rowIndex = 2;
        for (ContractProductVo contractProductVo : contractProductVoList) {
            row = sheet.createRow(rowIndex);
            cell = row.createCell(1);
            cell.setCellValue(contractProductVo.getCnumber());
            cell.setCellStyle(cellStyles[0]);
            cell = row.createCell(2);
            cell.setCellValue(contractProductVo.getContractNo());
            cell.setCellStyle(cellStyles[1]);
            cell = row.createCell(3);
            cell.setCellValue(contractProductVo.getProductNo());
            cell.setCellStyle(cellStyles[2]);

            cell = row.createCell(4);
            cell.setCellValue(contractProductVo.getCnumber());
            cell.setCellStyle(cellStyles[3]);

            cell = row.createCell(5);
            cell.setCellValue(contractProductVo.getFactoryName());
            cell.setCellStyle(cellStyles[4]);
//            工厂交期	船期	贸易条款

            cell = row.createCell(6);
            cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(contractProductVo.getDeliveryPeriod()));
            cell.setCellStyle(cellStyles[5]);

            cell = row.createCell(7);
            cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(contractProductVo.getShipTime()));
            cell.setCellStyle(cellStyles[6]);

            cell = row.createCell(8);
            cell.setCellValue(contractProductVo.getTradeTerms());
            cell.setCellStyle(cellStyles[7]);
            rowIndex++;
        }
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String nowDay = dateFormat.format(date);
        //将workBook导出
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);//workBook中内容写到缓存流中
        downloadUtil.download(byteArrayOutputStream,response,"出货表-"+nowDay+".xlsx");

    }


    //大标题样式
    public CellStyle bigTitle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("宋体");
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    //小标题的样式
    public CellStyle title(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);                //横向居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);        //纵向居中
        style.setBorderTop(BorderStyle.THIN);                        //上细线
        style.setBorderBottom(BorderStyle.THIN);                    //下细线
        style.setBorderLeft(BorderStyle.THIN);                        //左细线
        style.setBorderRight(BorderStyle.THIN);                        //右细线
        return style;
    }

    //文字样式
    public CellStyle text(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 10);

        style.setFont(font);

        style.setAlignment(HorizontalAlignment.LEFT);                //横向居左
        style.setVerticalAlignment(VerticalAlignment.CENTER);        //纵向居中
        style.setBorderTop(BorderStyle.THIN);                        //上细线
        style.setBorderBottom(BorderStyle.THIN);                    //下细线
        style.setBorderLeft(BorderStyle.THIN);                        //左细线
        style.setBorderRight(BorderStyle.THIN);                        //右细线
        return style;
    }

}

