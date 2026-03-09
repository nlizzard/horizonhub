package com.nlizzard.horizonhub.entity.query.basequery;

//基础查询类
public class BaseQuery {
    //分页信息
    private SimplePage simplePage;
    //页码
    private Integer pageNo;
    //每页大小
    private Integer pageSize;
    //排序变量
    private String orderBy;

    public SimplePage getSimplePage() {
        return simplePage;
    }

    public void setSimplePage(SimplePage simplePage) {
        this.simplePage = simplePage;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPagoNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
