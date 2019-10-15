package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.service.MemberService;
import com.itheima.service.ReportService;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/report")
public class ReportController {
    @Reference
    private ReportService reportService;
    @Reference
    private MemberService memberService;
    @GetMapping("/getMemberReport")
    public Result getMemberReport(){
        Map<String,List<Object>> mapList=memberService.getMemberReport();
        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS,mapList);
    }
    @GetMapping("/getPackageReport")
    public Result getPackageReport(){
        //查询套餐占比数据 Map<String,Object> value:数量,name:套餐名称,每一套餐就是一个map;
      List<Map<String,Object>> packageCount = reportService.getPackageReport();//会员的数量
        //组装套餐占比数据
      List<String> packageNames=new ArrayList<>();
        if (null !=packageCount) {
            for (Map<String, Object> map : packageCount) {
                //一个套餐数据
               packageNames.add((String)map.get("name"));
            }
        }
        Map<String,Object> relus=new HashMap<String, Object>(2);
        relus.put("packageNames",packageNames);
        relus.put("packageCount",packageCount);
        return new Result(true,MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS,relus);
    }

    /**
     * 查询运营数据
     * @return
     */
    @GetMapping("/getBusinessReportData")
    public Result getBusinessReportData(){
       Map<String,Object> reportData= reportService.getBusinessReportData();
       return new Result(true,MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS,reportData);
    }

    /**
     * 导处数据文件
     * @return
     */
    @GetMapping("exportBusinessReport")
    public Result exportBusinessReport(HttpServletRequest req,HttpServletResponse rep){
    //调用业务服务查询运营数据
        Map<String, Object> map = reportService.getBusinessReportData();
        //写到excel中
        //1.获取模板 getRealPath:找到webapp
        String template = req.getSession().getServletContext().getRealPath("template") + File.separator + "report_template.xlsx";
        //2.根据创建模板创建工作簿
        try ( XSSFWorkbook wb = new XSSFWorkbook(template);){
            //3.获取工作表
           Sheet sht = wb.getSheetAt(0);
            //4.获取行,单元格,在对应的位置写入内容
            sht.getRow(2).getCell(5).setCellValue(((String) map.get("reportDate")));
            //会员数据
            sht.getRow(4).getCell(5).setCellValue(((Integer) map.get("todayNewMember")));
            sht.getRow(4).getCell(7).setCellValue(((Integer) map.get("totalMember")));
            sht.getRow(5).getCell(5).setCellValue(((Integer) map.get("thisWeekNewMember")));
            sht.getRow(5).getCell(7).setCellValue(((Integer) map.get("thisMonthNewMember")));
            //预约到诊数据统计
            sht.getRow(7).getCell(5).setCellValue(((Integer) map.get("todayOrderNumber")));
            sht.getRow(7).getCell(7).setCellValue(((Integer) map.get("todayVisitsNumber")));
            sht.getRow(8).getCell(5).setCellValue(((Integer) map.get("thisWeekOrderNumber")));
            sht.getRow(8).getCell(7).setCellValue(((Integer) map.get("thisWeekVisitsNumber")));
            sht.getRow(9).getCell(5).setCellValue(((Integer) map.get("thisMonthOrderNumber")));
            sht.getRow(9).getCell(7).setCellValue(((Integer) map.get("thisMonthVisitsNumber")));
            //热门套餐
            int rowCnt= 12;
            List<Map<String, Object>> hotPackage = (List<Map<String, Object>>) map.get("hotPackage");
            if (null !=hotPackage) {
                for (Map<String, Object> pkgMap : hotPackage) {
                    sht.getRow(rowCnt).getCell(4).setCellValue(((String) pkgMap.get("name")));
                    sht.getRow(rowCnt).getCell(5).setCellValue(((Long) pkgMap.get("count")));
                    sht.getRow(rowCnt).getCell(6).setCellValue(((BigDecimal) pkgMap.get("proportion")).toString());
                    sht.getRow(rowCnt).getCell(7).setCellValue(((String) pkgMap.get("remark")));
                    rowCnt++;
                }
            }
            //5.告诉浏览器接收的是文件 Content-Type
            rep.setContentType("application/vnd.ms-excel");
            // 下载文件
            String filename = "运营数据.xlsx";
            // 解决乱码
            filename = new String(filename.getBytes(),"ISO-8859-1");
            //.调用response的输出流实现 下载
            ServletOutputStream out = rep.getOutputStream();
            rep.setHeader("Content-Disposition","attachment;filename=" + filename);
            wb.write(out);
            out.flush();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
      return new Result(false,MessageConstant.GET_BUSINESS_REPORT_FAIL);
    }
    @GetMapping("/getByDateMemberReport")
    public Result getByDateMemberReport(String value1){
        String str=value1;
        String[] split = str.split(",");
        String startTime = split[0];
        String finishTime= split[1];
        Map<String,List<Object>> mapList = reportService.getByDateMemberReport(startTime,finishTime);
        return new Result(true,MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS,mapList);
    }
}
