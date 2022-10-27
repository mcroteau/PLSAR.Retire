

<h1>New Item Group Import</h1>

<p>Here is a template for an Item Group to be used for an import.</p>

<form action="/imports/item_groups/${business.id}" id="import-form"  enctype="multipart/form-data" method="post">

    <label>Item Image Files</label>
    <input type="file" name="media" multiple/>
</form>
<div style="text-align: right;margin-top: 20px;">
    <input type="button" value="Import Item Groups" class="button green" id="import-submit"/>
</div>

<script>
    $(document).ready(function(){
        const $form = $('#import-form');
        let $submit = $('#import-submit');

        $submit.click(function(){
            this.disabled = true;
            this.value = "Importing Data...";
            $form.submit();
        })
    });
</script>
