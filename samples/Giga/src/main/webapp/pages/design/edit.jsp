<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    #live{
        position:absolute;
        right:260px;
        top:165px;
    }
</style>

<h1>Edit Design</h1>

<p>You may have to click update to see your changes.</p>

<a href="javascript:" id="live" class="button orange">Live Preview</a>

<form action="${pageContext.request.contextPath}/designs/update/${design.id}" method="post">

    <input type="hidden" name="id" value="${design.id}"/>
    <input type="hidden" name="businessId" value="${design.businessId}"/>


    <label>Name<span class="information">&nbsp;&nbsp;Give your design a name!</span></label>
    <input type="text" name="name" value="${design.name}"/>

    <label>Html<span class="information">&nbsp;&nbsp;Html of the design, {{content}} tag required.
        Can include Javascript & Css. Actually we expect it to.</span></label>
    <textarea name="design" class="code" style="height:800px;">${design.design}</textarea>

    <div style="text-align: right;margin-top: 20px;">
        <input type="submit" value="Update Design!" class="button green"/>
    </div>

    <script>
        $(document).ready(function(){
            const uri = "${pageContext.request.contextPath}/${business.uri}";
            let $live = $('#live'),
                $preview = $('#live-preview'),
                $frame = $('#frame');

            $live.click(function(evt){
                evt.preventDefault();
                $preview.toggle();
                document.getElementById('frame').contentWindow.location.reload();
            })
        })
    </script>
</form>

<style>
    #live-preview{
        width:465px;
        height:500px;
        background:#fff;
        padding:0px;
        margin:0px;
        box-shadow: 0px 20px 30px rgba(0,0,0,0.29);
        border-radius: 20px;
        position: fixed;
        left:790px;
        top:50px;
        z-index: 5;
    }
    #live-preview iframe{
        width: 100%;
        height: 100%;
        padding:0px;margin:0px;
    }
</style>

<div id="live-preview" style="display:none">
    <h3 style="text-align: center; color:#3979E4;padding-top:20px !important;" >Live Preview</h3>
    <iframe src="${pageContext.request.contextPath}/${business.uri}" id="frame"></iframe>
</div>