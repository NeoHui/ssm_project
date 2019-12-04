package com.itheima.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageBean {
    //    1、当前页码
    private int pageNum;
    //    2、每页显示条数
    private int pageSize;
    //    3、每页的数据
    private List list;
    //    4、总条数
    private Long total;
    //    5、总页数
    private int pages;
    //   6、上一页
    private int prePage;
    //    7、下一页
    private int nextPage;
    //    8、起始页码
    private int navigateFirstPage;
    //    9、结束页码
    private int navigateLastPage;

    //    1、页面传入的  pageNum  pageSize
//    2、计算的  pages  prePage  nextPage  startRow  endRow
//    3、查询的   list  total
    //计算总页数
    public PageBean(Integer pageNum, Integer pageSize, List list, Long total) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.list = list;
        this.total = total;
        //计算总页数
        if (total % pageSize == 0) {
            pages = (int) (total / pageSize);
        } else {
            pages = ((int) (total / pageSize)) + 1;
        }
        //上一页
        if (pageNum <= 1) {
            prePage = 1;
        } else {
            prePage = pageNum - 1;
        }
        //下一页
        if (pageNum >= pages) {
            nextPage = pages;
        } else {
            nextPage = pageNum + 1;
        }

//        判断总页数pages是否大于5
//        如果不大于5 startRow=1   endRow=pages
//        startRow  endRow
        if (pages <= 5){
            navigateFirstPage = 1;
            navigateLastPage = pages;
        }else if (pages - pageNum <=2){
            navigateFirstPage = pages - 4;
            navigateLastPage = pages;
        }else {
            navigateFirstPage = pageNum - 2;
            navigateLastPage = pageNum + 2;
        }
    }
}
