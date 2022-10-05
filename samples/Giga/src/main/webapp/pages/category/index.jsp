<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="io.Giga" %>
<%@ page import="io.model.Item" %>
<%@ page import="java.util.List" %>

${siteService.getCategoryBit(Giga.HEAD, category, business, request)}

<style>

    .item-wrapper{
        margin:10px 0px;
        padding:0px 20px 20px;
        border-radius: 7px;
        height:300px;
        width:130px;
        float: left;
        position: relative;
        display:inline-block;
        text-decoration: none;
        /*transition: all 0.23s;*/
        /*box-shadow: 0px 50px 39px rgba(0,0,0,0.26);*/
    }

    .category-item {
    }
    .item-wrapper:hover {
        /*transform: translateY(-31px);*/
        /*box-shadow: 0px 50px 19px rgba(0,0,0,0.26);*/
    }

    .category-item img{
        border-radius: 7px;
    }
    .clear{clear:both}
</style>

<h1>${category.name}</h1>

${category.header}

<div id="category-items">

    <c:if test="${items.size() > 0}">
    <c:forEach items="${items}" var="item">
        <a href="${pageContext.request.contextPath}/${business.uri}/items/${category.id}/${item.id}" class="item-wrapper" id="item-${item.id}">
            <div class="category-item">
                <img src="${item.imageUri}" width="150"/>
                <div class="item-details-wrapper">
                    <p>${item.name}</p>
                    <h3 class="item-price">$${siteService.getPriceDos(item.price)}</h3>
                </div>
            </div>
        </a>
    </c:forEach>
<br class="clear"/>
</c:if>
<c:if test="${items.size() == 0}">
    <p>No items created yet for ${category.name}!<br/>Check back later!</p>
</c:if>

${siteService.getCategoryBit(Giga.BOTTOM, category, business, request)}

