


<style>
    .grid input[type="text"]{padding:10px 0px !important}
    .option-wrapper{width:40%;float:left;margin:0px;}
    .pricing-wrapper{width:60%; float:right}
    #outer-wrapper{width:980px;}
    #content-wrapper{width: 100%;}
    #menu-wrapper{display:none;}
    .model-wrapper{padding:20px 0px; margin:20px 0px; border-top:solid 1px #e4e8e9; border-bottom: solid 1px #e4e8e9;}
    input[type="file"]{width:182px;}
</style>

<div class="align-center">
    <img src="${itemGroup.imageUri}" width="250" style="border-radius: 16px;margin: auto;"/>
</div>

<h1>Add Items/Models to Group</h1>

<p>Add Model to ${itemGroup.name}, the options below will build a
    form for you next model for this item.</p>

<div class="left-float">
    <label>Number <br/>of Options</label>
    <select id="options-select">
        <option value="1">1</option>
        <option value="2">2</option>
        <option value="3">3</option>
        <option value="4">4</option>
        <option value="5">5</option>
        <option value="6">6</option>
        <option value="7">7</option>
        <option value="8">8</option>
        <option value="9">9</option>
        <option value="10">10</option>
        <option value="11">11</option>
        <option value="12">12</option>
        <option value="13">13</option>
        <option value="14">14</option>
        <option value="15">15</option>
        <option value="16">16</option>
        <option value="17">17</option>
        <option value="18">18</option>
        <option value="19">19</option>
        <option value="20">20</option>
        <option value="21">21</option>
    </select>
</div>

<div class="left-float" style="margin-left: 40px;">
    <label>Number <br/>of Prices</label>
    <select id="prices-select" >
        <option value="1">1</option>
        <option value="2">2</option>
        <option value="3">3</option>
        <option value="4">4</option>
        <option value="5">5</option>
        <option value="5">6</option>
    </select>
</div>


<div class="left-float" style="margin: 60px 0px 0px 40px;">
    <input type="submit" id="build" class="button retro" value="Build Form!"/>
</div>

<br class="clear"/>

<ocean:if spec="${optionsCount != null &&
                        pricesCount != null}">

    <form action="/${business.id}/models/save" method="post">

        <input type="hidden" name="optionsCount" value="${optionsCount}"/>

        <div class="model-wrapper">

            <div class="left-float" style="width:45%;">
                <h3>Model Number #</h3>
                <input type="text" placeholder="Model No."/>
            </div>

            <div class="right-float"style="width:45%;">
                <h3>Weight</h3>
                <input type="text" placeholder="Weight" style="width:45%;"/>
            </div>

            <br class="clear"/>

            <div class="option-wrapper">
                <h3>Model Options</h3>
                <table class="grid">
                    <tr>
                        <th>Label</th>
                        <th>Value</th>
                    </tr>
                    <ocean:each var="idx" begin="1" end="${optionsCount}" step="1">
                        <tr>
                            <td>
                                <select name="modelOption">
                                    <option>Option ...</option>
                                    <ocean:each items="${groupOptions}" var="groupOption">
                                        <option id="${groupOption.id}">${groupOption.title}</option>
                                    </ocean:each>
                                </select>
                            </td>
                            <td>
                                <input type="text" placeholder="Value" name="groupValue"/><br/>
                                <input type="file" name="media">
                            </td>
                        </tr>
                    </ocean:each>
                </table>
            </div>


            <div class="pricing-wrapper">
                <h3>Pricing Details</h3>
                <table class="grid">
                    <tr>
                        <th>Pricing Header</th>
                        <td colspan="${pricesCount}"><input type="text" placeholder="Price/Carton" name="pricingHeader"></td>
                    </tr>
                    <tr>
                        <th>Label</th>
                        <ocean:each var="idx" begin="1" end="${pricesCount}" step="1">
                            <td><input type="text" placeholder="eg. 1" name="pricingLabel"/></td>
                        </ocean:each>
                    </tr>
                    <tr>
                        <th>$ Price</th>
                        <ocean:each var="idx" begin="1" end="${pricesCount}" step="1">
                            <td><input type="text" placeholder="$ Price" name="pricing"/></td>
                        </ocean:each>
                    </tr>
                    <tr>
                        <th>Resellers Price</th>
                        <ocean:each var="idx" begin="1" end="${pricesCount}" step="1">
                            <td><input type="text" placeholder="Resellers $ Price" name="resellersPrice"/></td>
                        </ocean:each>
                    </tr>
                    <tr>
                        <th>Quantity</th>
                        <ocean:each var="idx" begin="1" end="${pricesCount}" step="1">
                            <td><input type="text" placeholder="Quantity" name="quantity"/></td>
                        </ocean:each>
                    </tr>
                </table>

                <div class="button-wrapper">
                    <input type="submit" class="button blue" value="Save">
                    <input type="submit" class="button green" value="Add Model Number">
                    <p class="align-right tiny">Add model saves and adds it to this item itemGroup</p>
                </div>
            </div>

            <br class="clear"/>

        </div>
    </form>
</ocean:if>



<script>
    $(document).ready(function(){
        $('#build').click(function(evt){

            const optionsCount = $("#options-select").val();
            const pricesCount = $("#prices-select").val();

            let uri = cleanupUri(window.location.href);

            function cleanupUri(uri){
                let bits = uri.split("?")
                return bits[0];
            }

            const z = uri + "?pricesCount=" + pricesCount + "&optionsCount=" + optionsCount;
            console.log(z)
            window.location.href = z;
        });
    })
</script>










