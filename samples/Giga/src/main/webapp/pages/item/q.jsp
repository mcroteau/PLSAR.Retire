<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="io.Giga" %>
<%@ page import="io.model.Item" %>
<%@ page import="java.util.List" %>

${siteService.getQueryBit(Giga.HEAD, business, request)}

<style>

    .category-item-wrapper{
        margin:10px 0px;
        padding:0px 20px 20px;
        border-radius: 7px;
        height:300px;
        width:160px;
        float: left;
        position: relative;
        display:inline-block;
        text-decoration: none;
        /*transition: all 0.23s;*/
        /*box-shadow: 0px 50px 39px rgba(0,0,0,0.26);*/
    }

    .category-item {
    }
    .category-item-wrapper:hover {
        /*transform: translateY(-31px);*/
        /*box-shadow: 0px 50px 19px rgba(0,0,0,0.26);*/
    }

    .category-item img{
        border-radius: 7px;
    }
    .clear{clear:both}
</style>

<h1>Search Results : ${q}</h1>

<div id="category-items">

    <c:if test="${items.size() > 0}">
    <c:forEach items="${items}" var="item">
    <a href="${pageContext.request.contextPath}/${business.uri}/items/${item.id}" class="category-item-wrapper">
        <div class="category-item">
            <img src="${item.imageUri}" width="150"/>
            <div class="item-details-wrapper">
                <h3>${item.name}</h3>
                <span class="item-price">$${siteService.getPrice(item.price)}</span>
            </div>
        </div>
    </a>
    </c:forEach>
    <br class="clear"/>
    </c:if>
    <c:if test="${items.size() == 0}">
        <p>No items created yet for ${category.name}!<br/>Check back later!</p>
    </c:if>

${siteService.getQueryBit(Giga.BOTTOM, business, request)}

