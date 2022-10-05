import chico.Chico;
import chico.ChicoFilter;
import chico.support.DbSecurityAccess;
import io.Giga;
import io.model.*;
import io.repo.*;
import io.service.AuthService;
import io.service.BusinessService;
import io.service.ItemService;
import io.support.DbAccess;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import qio.Qio;
import qio.model.web.ResponseData;
import qio.support.DbMediator;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MaintenanceTest {

    Qio Qio;
    Item savedItem;
    Business savedIbm;
    Design ibmDesign;
    AuthService authService;
    ChicoFilter filter;
    Category savedCategory;


    @Test
    @Order(1)
    public void accessDenied(){
        setData();
        authService.signin("croteau.mike+ioc@gmail.com", "password");

        ItemService itemService = (ItemService) Qio.getElement("itemservice");
        ResponseData data = new ResponseData();
        itemService.edit(savedItem.getId(), savedIbm.getId(), data);

        Assertions.assertEquals("Unauthorized to edit this item.", data.get("message"));
        filter.destroy();
        authService.signout();
    }

//    @Test
//    @Order(2)
//    public void granted(){
//        setData();
//
//        authService.signin("croteau.mike+ibm@gmail.com", "password");
//
//        ItemService itemService = (ItemService) Qio.getElement("itemservice");
//        ResponseData data = new ResponseData();
//        itemService.edit(savedItem.getId(), savedIbm.getId(), data);
//
//        Assertions.assertEquals("/pages/item/edit.jsp", data.get("page"));
//
//        filter.destroy();
//        authService.signout();
//    }
//
//    @Test
//    @Order(3)
//    public void update() throws IOException, ServletException {
//
//        List<String> parameters = new ArrayList<>();
//        parameters.add("id=" + savedItem.getId());
//        parameters.add("name=Ikes");
//        parameters.add("imageUri=/on.png");
//        parameters.add("price=49.01");
//        parameters.add("quantity=14");
//        parameters.add("weight=31");
//        parameters.add("active=true");
//        parameters.add("categories=[" + savedCategory.getId() + "]");
//        parameters.add("businessId=" + savedIbm.getId());
//        parameters.add("designId=" + ibmDesign.getId());
//
//        HttpServletRequest req = new MockHttpRequest(parameters);
//        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
//        FilterConfig config = Mockito.mock(FilterConfig.class);
//        FilterChain filterChain = Mockito.mock(FilterChain.class);
//
//        filter = new ChicoFilter();
//        try {
//            filter.init(config);
//            filter.doFilter(req, resp, filterChain);
//        }catch(Exception ex){}
//
//        authService.signout();
//        authService.signin("croteau.mike+ibm@gmail.com", "password");
//
//        ItemService itemService = (ItemService) Qio.getElement("itemservice");
//        ResponseData data = new ResponseData();
//        itemService.update(savedItem.getId(), savedIbm.getId(), false, data, req);
//
//        ItemRepo itemRepo = (ItemRepo) Qio.getElement("itemRepo");
//        Item updatedItem = itemRepo.get(savedItem.getId());
//
//        Assertions.assertEquals(new BigDecimal(49.01).setScale(2, RoundingMode.HALF_UP), updatedItem.getPrice());
//
//        filter.destroy();
//        authService.signout();
//    }


    @BeforeAll
    public void setup(){
        List<String> properties = Arrays.asList(new String[]{ "qio.props" });
        try {
            this.Qio = new Qio.Injector()
                    .setBasic(false)
                    .setCreateDb(true)
                    .setDropDb(true)
                    .withContext(new MockServletContext())
                    .withPropertyFiles(properties)
                    .withWebResources(new ArrayList<>())
                    .inject();
        }catch(Exception ex){
            ex.printStackTrace();
        }

        authService = (AuthService) Qio.getElement("authservice");
        DbSecurityAccess access = (DbAccess) Qio.getElement("dbaccess");
        System.out.println("access " + access);
        Chico.configure(access);
        ChicoFilter chicoFilter = new ChicoFilter();

        UserRepo userRepo = (UserRepo) Qio.getElement("userrepo");
        RoleRepo roleRepo = (RoleRepo) Qio.getElement("rolerepo");

        Role superRole = roleRepo.find(Giga.SUPER_ROLE);
        Role businessRole = roleRepo.find(Giga.BUSINESS_ROLE);

        if(superRole == null){
            superRole = new Role();
            superRole.setName(Giga.SUPER_ROLE);
            roleRepo.save(superRole);
        }

        if(businessRole == null){
            businessRole = new Role();
            businessRole.setName(Giga.BUSINESS_ROLE);
            roleRepo.save(businessRole);
        }

        Role savedBusinessRole = roleRepo.get(Giga.BUSINESS_ROLE);

        User iocUser = new User();
        iocUser.setUsername("croteau.mike+ioc@gmail.com");
        iocUser.setPassword(Chico.dirty("password"));
        userRepo.save(iocUser);
        User savedIocUser = userRepo.getSaved();

        User ibmUser = new User();
        ibmUser.setName("Ibm User");
        ibmUser.setPhone("9079878652");
        ibmUser.setUsername("croteau.mike+ibm@gmail.com");
        ibmUser.setPassword(Chico.dirty("password"));
        ibmUser.setDateJoined(Giga.getDate());
        userRepo.save(ibmUser);
        User savedIbmUser = userRepo.getSaved();

        userRepo.saveUserRole(savedIbmUser.getId(), savedBusinessRole.getId());
        String permission = Giga.USER_MAINTENANCE + savedIbmUser.getId();
        userRepo.savePermission(savedIbmUser.getId(), permission);

        BusinessRepo businessRepo = (BusinessRepo) Qio.getElement("businessrepo");
        Business ibm = new Business();
        ibm.setName("IBM");
        ibm.setUserId(savedIbmUser.getId());
        businessRepo.save(ibm);

        this.savedIbm = businessRepo.getSaved();

        String ibmPermission = Giga.BUSINESS_MAINTENANCE + savedIbm.getId();
        userRepo.savePermission(savedIbmUser.getId(), ibmPermission);

        BusinessService businessService = (BusinessService) Qio.getElement("businessService");

        try{
            businessService.configure(savedIbm, true);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        ItemRepo itemRepo = (ItemRepo) Qio.getElement("itemrepo");
        CategoryRepo categoryRepo = (CategoryRepo) Qio.getElement("categoryrepo");
        DesignRepo designRepo = (DesignRepo) Qio.getElement("designrepo");

        this.ibmDesign = designRepo.getSaved();

        Category shopCategory = new Category();
        shopCategory.setTopLevel(true);
        shopCategory.setBusinessId(savedIbm.getId());
        shopCategory.setName("Shop");
        shopCategory.setUri("shop");
        shopCategory.setDesignId(ibmDesign.getId());
        categoryRepo.save(shopCategory);
        this.savedCategory = categoryRepo.getSaved();

        String categoryPermission = Giga.CATEGORY_MAINTENANCE + savedCategory.getId();
        userRepo.savePermission(savedIbmUser.getId(), categoryPermission);

        Item item = new Item();
        item.setDesignId(ibmDesign.getId());
        item.setBusinessId(savedIbm.getId());
        item.setName("Giga Item " + Giga.getString(4));
        item.setImageUri(Giga.OCEAN_ENDPOINT + Giga.ITEM_IMAGE);
        item.setPrice(new BigDecimal(45));
        item.setAffiliatePrice(new BigDecimal(45));
        item.setQuantity(new BigDecimal(Giga.getNumber(30)));
        item.setWeight(new BigDecimal(Giga.getNumber(48)));
        item.setCost(new BigDecimal(50));
        itemRepo.save(item);

        this.savedItem = itemRepo.getSaved();

        String itemPermission = Giga.ITEM_MAINTENANCE + savedItem.getId();
        userRepo.savePermission(savedIbmUser.getId(), itemPermission);
    }

    @AfterAll
    public void tearDown(){
        DbMediator mediator = (DbMediator) Qio.getElement("dbmediator");
        mediator.dropDb();
    }


    private void setData(){
        HttpServletRequest req = new MockHttpRequest(new ArrayList<>());
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        FilterConfig config = Mockito.mock(FilterConfig.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        filter = new ChicoFilter();
        try {
            filter.init(config);
            filter.doFilter(req, resp, filterChain);
        }catch(Exception ex){}
    }
}
