package giga.ingest;

import giga.Giga;
import giga.model.*;
import giga.repo.*;
import giga.service.SeaService;
import dev.blueocean.RouteAttributes;
import dev.blueocean.model.FileComponent;
import dev.blueocean.model.NetworkRequest;
import dev.blueocean.model.RequestComponent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ItemGroupIngest {

    final int GROUP_NAME   = 0;

    final int MODEL_NUMBER = 0;
    final int QUANTITY     = 1;
    final int WEIGHT       = 2;

    final int PRICE_HEADER = 1;

    final int FIRST_ROW  = 1;
    final int SECOND_ROW = 2;
    final int THIRD_ROW  = 3;

    IngestStats ingestStats;

    Integer priceIdx = null;
    Integer weightIdx = null;
    Integer quantityIdx = null;

    Long designId;
    Long businessId;
    ModelRepo modelRepo;
    GroupRepo groupRepo;
    PriceRepo priceRepo;
    OptionRepo optionRepo;
    IngestRepo ingestRepo;
    List<GroupModel> groupModels;

    NetworkRequest req;

    public ItemGroupIngest(Builder builder){
        this.req = builder.req;
        this.designId = builder.designId;
        this.ingestRepo = builder.ingestRepo;
        this.businessId = builder.businessId;
        this.modelRepo = builder.modelRepo;
        this.groupRepo = builder.groupRepo;
        this.priceRepo = builder.priceRepo;
        this.optionRepo = builder.optionRepo;
        this.groupModels = new ArrayList<>();
        this.ingestStats = new IngestStats();
    }

    public ItemGroupIngest ingest() throws IOException {
        RequestComponent requestComponent = req.getRequestComponent("media");
        List<FileComponent> fileComponents = requestComponent.getFileComponents();

        RouteAttributes routeAttributes = req.getRouteAttributes();
        String key = (String) routeAttributes.get("cloud.key");
        String secret = (String) routeAttributes.get("cloud.secret");
        SeaService seaService = new SeaService();

        Integer processed = 0;
        Integer unprocessed = 0;

        for(int q = 0; q < fileComponents.size(); q++){

            Ingest ingest = new Ingest();
            ingest.setBusinessId(businessId);
            ingest.setDateIngest(Giga.getDate());
            ingestRepo.save(ingest);

            Ingest savedIngest = ingestRepo.getSaved();

            FileComponent part = fileComponents.get(q);
            InputStream in = new ByteArrayInputStream(part.getFileBytes());
            Scanner scanner = new Scanner(in);

            ItemGroup savedGroup = null;

            Integer idx = 0;
            while (scanner.hasNext()) {
                idx++;
                String line = scanner.nextLine();
                String[] values = line.split(",");

                if (idx.equals(this.SECOND_ROW)) setIndexes(values);

                if (idx.equals(this.FIRST_ROW)) {
                    String name = values[this.GROUP_NAME];
                    ItemGroup storedGroup = groupRepo.get(name);
                    if (name.equals("") ||
                            storedGroup != null) {
                        break;
                    }
                    savedGroup = createGroup(savedIngest.getId(), designId, businessId, values);
                }

                if (savedGroup == null) {
                    break;
                }

                if (idx.equals(this.THIRD_ROW)) {
                    for (int z = 0; z < values.length; z++) {
                        if (z > this.weightIdx && z < this.priceIdx) {
                            String title = values[z];
                            if (title.equals("")) {
                                continue;
                            }
                            GroupOption groupOption = new GroupOption();
                            groupOption.setIngestId(savedIngest.getId());
                            groupOption.setBusinessId(businessId);
                            groupOption.setGroupId(savedGroup.getId());
                            groupOption.setTitle(title);
                            optionRepo.saveOption(groupOption);
                        }
                        if (z >= this.priceIdx) {
                            String description = values[z];
                            PricingOption pricingOption = new PricingOption();
                            pricingOption.setIngestId(savedIngest.getId());
                            pricingOption.setDescription(description);
                            pricingOption.setBusinessId(businessId);
                            pricingOption.setGroupId(savedGroup.getId());
                            priceRepo.saveOption(pricingOption);
                        }
                    }
                }

                if (idx.compareTo(this.THIRD_ROW) > 0) {

                    if (values.length < priceIdx) {
                        unprocessed++;
                        ingestStats.setUnprocessed(unprocessed);
                        continue;
                    }
                    String modelNumber = values[this.MODEL_NUMBER];
                    String weight = values[this.WEIGHT];
                    String quantity = values[this.QUANTITY];

                    if (modelNumber.equals("")) continue;
                    if (quantity.equals("")) continue;
                    if (weight.equals("")) continue;

                    modelRepo.delete(modelNumber);

                    GroupModel groupModel = new GroupModel();
                    groupModel.setModelNumber(modelNumber);
                    groupModel.setIngestId(savedIngest.getId());
                    groupModel.setGroupId(savedGroup.getId());
                    groupModel.setBusinessId(businessId);
                    groupModel.setWeight(new BigDecimal(weight));
                    groupModel.setQuantity(new BigDecimal(quantity));
                    modelRepo.save(groupModel);

                    GroupModel savedModel = modelRepo.get(modelNumber);

                    for (int z = 0; z < values.length; z++) {

                        if (z > weightIdx && z < priceIdx) {
                            String value = values[z];
                            if (value.equals("")) {
                                modelRepo.delete(savedModel.getId());
                                unprocessed++;
                                ingestStats.setUnprocessed(unprocessed);
                                continue;
                            }
                            GroupOptionValue groupValue = new GroupOptionValue();
                            groupValue.setIngestId(savedIngest.getId());
                            groupValue.setBusinessId(businessId);
                            groupValue.setModelId(savedModel.getId());
                            groupValue.setGroupId(savedGroup.getId());
                            groupValue.setOptionValue(value);
                            optionRepo.saveValue(groupValue);
                        }
                        if (z >= priceIdx) {
                            String price = values[z];
                            if (price.equals("")) {
                                optionRepo.deleteValuesModel(savedModel.getId());
                                modelRepo.delete(savedModel.getId());
                                unprocessed++;
                                ingestStats.setUnprocessed(unprocessed);
                                continue;
                            }
                            try {
                                PricingValue pricingValue = new PricingValue();
                                pricingValue.setIngestId(savedIngest.getId());
                                pricingValue.setGroupId(savedGroup.getId());
                                pricingValue.setBusinessId(businessId);
                                pricingValue.setModelId(savedModel.getId());
                                pricingValue.setPrice(new BigDecimal(price));
                                priceRepo.saveValue(pricingValue);
                            } catch (Exception ex) {
                                optionRepo.deleteValuesModel(savedModel.getId());
                                modelRepo.delete(savedModel.getId());
                                unprocessed++;
                                ingestStats.setUnprocessed(unprocessed);

                            }

                        }

                    }


                    processed++;
                    ingestStats.setCount(idx);
                    ingestStats.setProcessed(processed);
                    ingestStats.setUnprocessed(unprocessed);
                }
            }
        }

        return this;
    }


    public IngestStats getStats(){
        return this.ingestStats;
    }

    protected ItemGroup createGroup(Long ingestId, Long designId, Long businessId, String[] values){
        String name = values[0];
        if(name.equals("")){
            return null;
        }

        String priceHeader = "";
        if(values.length >= 2) {
            priceHeader = values[this.PRICE_HEADER];
        }

        String quantityHeader = "";
        if(values.length >= 3) {
            quantityHeader = values[2];
        }

        ItemGroup itemGroup = new ItemGroup();
        itemGroup.setIngestId(ingestId);
        itemGroup.setBusinessId(businessId);
        itemGroup.setDesignId(designId);
        itemGroup.setName(name);
        itemGroup.setPricingHeader(priceHeader);
        itemGroup.setqHeader(quantityHeader);
        groupRepo.save(itemGroup);
        ItemGroup savedItemGroup = groupRepo.getSaved();
        return savedItemGroup;
    }


    protected void setIndexes(String[] values){
        for(int idx = 0; idx < values.length; idx++){
            if(this.priceIdx == null &&
                    values[idx].equals("::prices::")){
                this.priceIdx = idx;
            }
            if(values[idx].equals("::weight::")){
                this.weightIdx = idx;
            }
            if(values[idx].equals("::quantity::")){
                this.quantityIdx = idx;
            }
        }
    }

    public static class Builder{
        Long designId;
        Long businessId;
        IngestRepo ingestRepo;
        ModelRepo modelRepo;
        GroupRepo groupRepo;
        PriceRepo priceRepo;
        OptionRepo optionRepo;
        NetworkRequest req;

        public Builder withDesignId(Long designId){
            this.designId = designId;
            return this;
        }
        public Builder withBusinessId(Long businessId){
            this.businessId = businessId;
            return this;
        }
        public Builder withModelRepo(ModelRepo modelRepo){
            this.modelRepo = modelRepo;
            return this;
        }
        public Builder withGroupRepo(GroupRepo groupRepo){
            this.groupRepo = groupRepo;
            return this;
        }
        public Builder withOptionRepo(OptionRepo optionRepo){
            this.optionRepo = optionRepo;
            return this;
        }
        public Builder withPriceRepo(PriceRepo priceRepo){
            this.priceRepo = priceRepo;
            return this;
        }
        public Builder withIngestRepo(IngestRepo ingestRepo){
            this.ingestRepo = ingestRepo;
            return this;
        }
        public Builder withRequest(NetworkRequest req){
            this.req = req;
            return this;
        }
        public ItemGroupIngest build(){
            return new ItemGroupIngest(this);
        }
    }
}
