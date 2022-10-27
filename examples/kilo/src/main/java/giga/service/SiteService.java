package giga.service;

import giga.model.*;
import giga.repo.CategoryRepo;
import giga.repo.DesignRepo;
import giga.repo.UserRepo;
import dev.blueocean.model.NetworkRequest;
import dev.blueocean.security.SecurityManager;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class SiteService {

    UserRepo userRepo;

    DesignRepo designRepo;

    CategoryRepo categoryRepo;

    SecurityManager security;

    public SiteService(UserRepo userRepo, DesignRepo designRepo, CategoryRepo categoryRepo, SecurityManager security){
        this.userRepo = userRepo;
        this.designRepo = designRepo;
        this.categoryRepo = categoryRepo;
        this.security = security;
    }

    public String getBit(Integer slice, String design, Design blueprint, Category category, Business business, NetworkRequest req){
        if(design.equals("\\{\\{content\\}\\}")) design = " " + design + " ";
        String[] bits = design.split("\\{\\{site.content\\}\\}");
        String bit = bits[slice];
        return setAttributes(bit, blueprint, category, business, req);
    }

    public String getQueryBit(Integer slice, Business business, NetworkRequest req){
        Design design = designRepo.getBase(business.getId());
        String blueprint = design.getDesign();
        return getBit(slice, blueprint, design, null, business, req);
    }

    public String getBaseBit(Integer slice, Design design, Business business, NetworkRequest req){
        String blueprint = design.getDesign();
        return getBit(slice, blueprint, design, null, business, req);
    }

    public String getPageBit(Integer slice, Page page, Business business, NetworkRequest req){
        Design blueprint = designRepo.get(page.getDesignId());
        String design = blueprint.getDesign();
        return getBit(slice, design, blueprint, null, business, req);
    }

    public String getItemBit(Integer slice, Item item, Category category, Business business, NetworkRequest req){
        Design blueprint = designRepo.get(item.getDesignId());
        String design = blueprint.getDesign();
        return getBit(slice, design, blueprint, null, business, req);
    }

    public String getCategoryBit(Integer slice, Category category, Business business, NetworkRequest req){
        Design blueprint = designRepo.get(category.getDesignId());
        String design = blueprint.getDesign();
        return getBit(slice, design, blueprint, category, business, req);
    }

    public String setAttributes(String bit, Design blueprint, Category category, Business business, NetworkRequest req){
        bit = bit.replace("{{categories}}", getMenu(category, business, req));
        bit = bit.replace("{{categories.vertical}}", getMenuVertical(category, business, req));
        bit = bit.replace("{{kart}}", getKart(business, req));
        bit = bit.replace("{{css}}", blueprint.getCss());
        bit = bit.replace("{{js}}", blueprint.getJavascript());
        bit = bit.replace("{{greeting}}", getGreeting(req));
        bit = bit.replace("{{business.name}}", business.getName());
        bit = bit.replace("{{business.phone}}", business.getPhone());
        bit = bit.replace("{{business.uri}}", business.getUri());
        bit = bit.replace("{{search.box}}", getSearch(business, req));
        bit = bit.replace("{{signin.signout.href}}", getSignin(business, req));
        bit = bit.replace("{{signup.href}}", getSignup(business, req));
        bit = bit.replace("{{activity.href}}", getActivity(business, req));
        return bit;
    }

    private String getKart(Business business, NetworkRequest req) {
        String href = "/" + business.getUri() + "/cart";
        return "<a href=\"" + href + "\">Kart</a>";
    }

    private String getSignin(Business business, NetworkRequest req){
        String href = "";
        if(security.isAuthenticated(req)){
            href = "/" + business.getUri() + "/signout";
            return "<a href=\"" + href + "\">Signout</a>";
        }
        href = "/" + business.getUri() + "/signin";
        return "<a href=\"" + href + "\">Signin</a>";
    }

    private String getSignup(Business business, NetworkRequest req){
        if(!security.isAuthenticated(req)){
            String href = "/" + business.getUri() + "/signup";
            return "<a href=\"" + href + "\">Register</a>";
        }
        return "";
    }

    private String getActivity(Business business, NetworkRequest req){
        String href = "/" + business.getUri() + "/activity";
        return "<a href=\"" + href + "\">My Activity</a>";
    }

    public String getSearch(Business business, NetworkRequest request){
        StringBuilder sb = new StringBuilder();
        String action = "/query/" + business.getId();
        String search = "type=\"text\" name=\"q\" placeholder=\"Search...\"";
        sb.append("<form action=\"" + action + "\">");
        sb.append("<input " + search + "/>");
        sb.append("</form>");
        return sb.toString();
    }

    public String getGreeting(NetworkRequest req){
        if(security.isAuthenticated(req)){
            String credential = security.getUser(req);
            User authUser = userRepo.getPhone(credential);
            if(authUser == null){
                authUser = userRepo.getEmail(credential);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Welcome back ");
            if(authUser.getName() != null){
                sb.append(authUser.getName() + "!");
            }else{
                sb.append(authUser.getEmail() + "!");
            }
            return sb.toString();
        }
        return "";
    }


    public String getMenuVertical(Category activeCategory, Business business, NetworkRequest req){
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

    public String getMenu(Category activeCategory, Business business, NetworkRequest req){
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

    public String getUri(Category category, Business business, NetworkRequest req){
        return "/" + business.getUri() + "/" + category.getUri() + "/items";
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
