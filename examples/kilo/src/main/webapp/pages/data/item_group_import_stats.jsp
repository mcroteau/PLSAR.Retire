

<h1>Import Stats</h1>

<style>
    .value{font-size:72px;display: block;}
    .value.large{font-size:102px;}
</style>

<h3 class="center-align left-float" style="width:40%;">
    <span class="value large" id="processed"></span>
    Processed
</h3>
<h3 class="center-align right-float" style="width:40%;">
    <span class="value large" id="unprocessed"></span>
    Unprocessed
</h3>

<script>
    $(document).ready(function(){
        let $processed = $('#processed'),
            $unprocessed = $('#unprocessed');

        setInterval(function(){
            $.ajax({
                url : "/imports/item_groups/data/${business.id}",
                success : function(data){
                    $processed.val(data.processed)
                    $unprocessed.val(data.processed)
                }
            })
        },3000)
    });
</script>
