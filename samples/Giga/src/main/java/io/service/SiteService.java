package io.service;

import io.model.*;
import io.repo.CategoryRepo;
import io.repo.DesignRepo;
import jakarta.servlet.http.HttpServletRequest;
import okhttp3.internal.connection.RouteDatabase;
import qio.annotate.Inject;
import qio.annotate.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.List;

@Service
public class SiteService {

    @Inject
    AuthService authService;

    @Inject
    DesignRepo designRepo;

    @Inject
    CategoryRepo categoryRepo;

    public String getBit(Integer slice, String design, Design blueprint, Category category, Business business, HttpServletRequest req){
        if(design.equals("\\{\\{content\\}\\}")) design = " " + design + " ";
        String[] bits = design.split("\\{\\{site.content\\}\\}");
        String bit = bits[slice];
        return setAttributes(bit, blueprint, category, business, req);
    }

    public String getQueryBit(Integer slice, Business business, HttpServletRequest req){
        Design design = designRepo.getBase(business.getId());
        String blueprint = design.getDesign();
        return getBit(slice, blueprint, design, null, business, req);
    }

    public String getBaseBit(Integer slice, Design design, Business business, HttpServletRequest req){
        String blueprint = design.getDesign();
        return getBit(slice, blueprint, design, null, business, req);
    }

    public String getPageBit(Integer slice, Page page, Business business, HttpServletRequest req){
        Design blueprint = designRepo.get(page.getDesignId());
        String design = blueprint.getDesign();
        return getBit(slice, design, blueprint, null, business, req);
    }

    public String getItemBit(Integer slice, Item item, Category category, Business business, HttpServletRequest req){
        Design blueprint = designRepo.get(item.getDesignId());
        String design = blueprint.getDesign();
        return getBit(slice, design, blueprint, null, business, req);
    }

    public String getCategoryBit(Integer slice, Category category, Business business, HttpServletRequest req){
        Design blueprint = designRepo.get(category.getDesignId());
        String design = blueprint.getDesign();
        return getBit(slice, design, blueprint, category, business, req);
    }

    public String setAttributes(String bit, Design blueprint, Category category, Business business, HttpServletRequest req){
        bit = bit.replace("{{categories}}", getMenu(category, business, req));
        bit = bit.replace("{{categories.vertical}}", getMenuVertical(category, business, req));
        bit = bit.replace("{{kart}}", getKart(business, req));
        bit = bit.replace("{{css}}", blueprint.getCss());
        bit = bit.replace("{{js}}", blueprint.getJavascript());
        bit = bit.replace("{{greeting}}", getGreeting());
        bit = bit.replace("{{business.name}}", business.getName());
        bit = bit.replace("{{business.phone}}", business.getPhone());
        bit = bit.replace("{{business.uri}}", business.getUri());
        bit = bit.replace("{{search.box}}", getSearch(business, req));
        bit = bit.replace("{{signin.signout.href}}", getSignin(business, req));
        bit = bit.replace("{{signup.href}}", getSignup(business, req));
        bit = bit.replace("{{activity.href}}", getActivity(business, req));
        return bit;
    }

    private String getKart(Business business, HttpServletRequest req) {
        String href = req.getContextPath() + "/" + business.getUri() + "/cart";
        return "<a href=\"" + href + "\">Kart</a>";
    }

    private String getSignin(Business business, HttpServletRequest req){
        String href = "";
        if(authService.isAuthenticated()){
            href = req.getContextPath() + "/" + business.getUri() + "/signout";
            return "<a href=\"" + href + "\">Signout</a>";
        }
        href = req.getContextPath() + "/" + business.getUri() + "/signin";
        return "<a href=\"" + href + "\">Signin</a>";
    }

    private String getSignup(Business business, HttpServletRequest req){
        if(!authService.isAuthenticated()){
            String href = req.getContextPath() + "/" + business.getUri() + "/signup";
            return "<a href=\"" + href + "\">Register</a>";
        }
        return "";
    }

    private String getActivity(Business business, HttpServletRequest req){
        String href = req.getContextPath() + "/" + business.getUri() + "/activity";
        return "<a href=\"" + href + "\">My Activity</a>";
    }

    public String getSearch(Business business, HttpServletRequest request){
        StringBuilder sb = new StringBuilder();
        String action = request.getContextPath() + "/query/" + business.getId();
        String search = "type=\"text\" name=\"q\" placeholder=\"Search...\"";
        sb.append("<form action=\"" + action + "\">");
        sb.append("<input " + search + "/>");
        sb.append("</form>");
        return sb.toString();
    }

    public String getGreeting(){
        if(authService.isAuthenticated()){
            User authUser = authService.getUser();
            StringBuilder sb = new StringBuilder();
            sb.append("Welcome back ");
            if(authUser.getName() != null){
                sb.append(authUser.getName() + "!");
            }else{
                sb.append(authUser.getUsername() + "!");
            }
            return sb.toString();
        }
        return "";
    }


    public String getMenuVertical(Category activeCategory, Business business, HttpServletRequest req){
        List<Category> categories = null;
        if(activeCategory != null) {
            categories = categoryRepo.getList(activeCategory.getId(), business.getId());
        }

        if(categories == null ||
                categories.size() == 0){
            categories = categoryRepo.getList(business.getId(), true);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<ul id=\"menu\" class=\"menu\">");
        for(Category category : categories){
            String uri = getUri(category, business, req);
            sb.append("<li><a href=\"" + uri + "\" id=cateogory-" + category.getId() +  "\">" + category.getName() + "</a></li>");
        }
        sb.append("</ul>");
        return sb.toString();
    }

    public String getMenu(Category activeCategory, Business business, HttpServletRequest req){
        List<Category> categories = null;
        if(activeCategory != null) {
            categories = categoryRepo.getList(activeCategory.getId(), business.getId());
        }

        if(categories == null ||
                categories.size() == 0){
            categories = categoryRepo.getList(business.getId(), true);
        }

        StringBuilder sb = new StringBuilder();
        for(Category category : categories){
            String uri = getUri(category, business, req);
            sb.append("<a href=\"" + uri + "\" id=\"category-" + category.getId() +  "\">" + category.getName() + "</a>");
        }
        return sb.toString();
    }

    public String getUri(Category category, Business business, HttpServletRequest req){
        return req.getContextPath() + "/" + business.getUri() + "/" + category.getUri() + "/items";
    }

    public String getPrice(BigDecimal pre){
        if(pre != null){
            pre = pre.setScale(3, RoundingMode.HALF_DOWN);
            String post = pre.toPlainString();
            String[] bits = post.split("\\.");
            String decimal = bits[1];
            BigDecimal fat = new BigDecimal(bits[0]);

            DecimalFormat dc = new DecimalFormat("###,###");
            String value = dc.format(fat);
            return value + ".<span class=\"decimal\">" + decimal + "</span>";
        }
        return "0.00";
    }

    public String getPriceDos(BigDecimal pre){
        if(pre != null){
            pre = pre.setScale(2, RoundingMode.HALF_DOWN);
            String post = pre.toPlainString();
            String[] bits = post.split("\\.");
            String decimal = bits[1];
            BigDecimal fat = new BigDecimal(bits[0]);

            DecimalFormat dc = new DecimalFormat("###,###");
            String value = dc.format(fat);
            return value + ".<span class=\"decimal\">" + decimal + "</span>";
        }
        return "0.00";
    }


    public String getPriceTres(BigDecimal pre){
        if(pre != null){
            pre = pre.setScale(2, RoundingMode.HALF_DOWN);
            String post = pre.toPlainString();
            String[] bits = post.split("\\.");
            String decimal = bits[1];
            BigDecimal fat = new BigDecimal(bits[0]);

            DecimalFormat dc = new DecimalFormat("###,###");
            String value = dc.format(fat);
            return value + "." + decimal;
        }
        return "0.00";
    }


    public String getPercent(BigDecimal rawDigits){
        BigDecimal percent = rawDigits.multiply(new BigDecimal(100), new MathContext(3, RoundingMode.HALF_DOWN));
        DecimalFormat formatter = new DecimalFormat("###,###.###");
        return formatter.format(percent);
    }

}
