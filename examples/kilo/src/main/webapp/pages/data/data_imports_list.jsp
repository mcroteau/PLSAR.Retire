
<%@ page import="giga.Giga" %>

<ocean:if spec="${message != ''}">
    <p class="notify">${message}</p>
</ocean:if>

<h1>Data Imports</h1>

<a href="/import/media/${business.id}" class="button remove" style="margin-top:20px;">New Item Image Import</a>&nbsp;&nbsp;
<a href="/imports/item_groups/new/${business.id}" class="button orange" style="margin-top:20px;">New Item Group Import</a>
<br class="clear"/>

<p>Item import allows gives you the ability to easily import items using images.</p>
<p>Item groups are what they sound like, groups of items. Meant for wholesale and distributors.</p>
