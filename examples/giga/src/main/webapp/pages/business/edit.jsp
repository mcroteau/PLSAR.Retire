<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>Edit Business</h1>
<form action="${pageContext.request.contextPath}/businesses/update/${editBusiness.id}" method="post">

    <input type="hidden" name="id" value="${editBusiness.id}"/>

    <input type="hidden" name="userId" value="${authUser.id}"/>

    <label>Name<span class="information">&nbsp;&nbsp;Give your business a name!</span></label>
    <input type="text" name="name" value="${editBusiness.name}"/>

    <label>URI<span class="information">&nbsp;&nbsp;Give your business a uri slug, must not contain any special characters.</span></label>
    <input type="text" name="uri" value="${editBusiness.uri}"/>

    <input type="hidden" name="phone" value="${editBusiness.phone}"/>
    <input type="hidden" name="street" value="${editBusiness.street}"/>
    <input type="hidden" name="streetDos" value="${editBusiness.streetDos}"/>
    <input type="hidden" name="city" value="${editBusiness.city}"/>
    <input type="hidden" name="state" value="${editBusiness.state}"/>
    <input type="hidden" name="zip" value="${editBusiness.zip}"/>
    <input type="hidden" name="country" value="${editBusiness.country}"/>

    <div style="text-align: right;margin-top: 20px;">
        <input type="submit" value="Update Business!" class="button green"/>
    </div>
</form>
