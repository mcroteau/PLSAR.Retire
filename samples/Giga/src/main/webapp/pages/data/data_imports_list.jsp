<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="io.Giga" %>

<c:if test="${not empty message}">
    <p class="notify">${message}</p>
</c:if>

<h1 class="left-float">Data Imports</h1>
<a href="${pageContext.request.contextPath}/import/media/${business.id}" class="button retro right-float" style="margin-top:20px;">New Item Image Import</a>
<br class="clear"/>

<p>What is an item image import? An item image import is an image import that can be easily converted
    into items.</p>

<c:if test="${dataImports.size() > 0}">
    <table>
        <tr>
            <th>Date</th>
            <th></th>
        </tr>
        <c:forEach var="dataImport" items="${dataImports}" varStatus="idx">
            <tr>
                <td>${dataImport.dateImport}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/imports/media/${business.id}/${dataImport.id}" class="button orange">Power Grid</a>
                </td>
            </tr>
        </c:forEach>
    </table>
</c:if>

<c:if test="${dataImports == null || dataImports.size() == 0}">
    <p class="notify">No item image imports added yet! <a href="${pageContext.request.contextPath}/import/media/${business.id}" class="href-dotted">New Import</a></p>
</c:if>
