package io.informant;

import dev.blueocean.Dao;
import dev.blueocean.PersistenceConfig;
import dev.blueocean.annotations.ServerStartup;
import dev.blueocean.drivers.Drivers;
import dev.blueocean.implement.ServerListener;
import io.informant.model.Paper;
import io.informant.repo.PaperRepo;
import io.informant.repo.UserRepo;

import java.nio.file.Path;
import java.nio.file.Paths;

@ServerStartup
public class InformantStartup implements ServerListener {
    @Override//todo:∆ from setup complete to initialized
    public void startup() {

        PersistenceConfig persistenceConfig = new PersistenceConfig();
        persistenceConfig.setDriver(Drivers.H2);
        persistenceConfig.setUrl("jdbc:h2:~/devDb");
        persistenceConfig.setUser("sa");
        persistenceConfig.setPassword("");

        Dao dao = new Dao(persistenceConfig);

        PaperRepo paperRepo = new PaperRepo(dao);
        UserRepo userRepo = new UserRepo(dao);

        Path audiPath = Paths.get("src", "main", "webapp", "resources", "media", "audi.jpg");
        String audiUri = audiPath.toAbsolutePath().toString();
        StringBuilder audi = new StringBuilder();
        audi.append(Informant.getBasePrefix("audi.jpg"));
        audi.append(Informant.getEncoded(audiUri));

        Path royksoppPath = Paths.get("src", "main", "webapp", "resources", "media", "royksopp.jpg");
        String royksoppUri = royksoppPath.toAbsolutePath().toString();
        StringBuilder royksopp = new StringBuilder();
        royksopp.append(Informant.getBasePrefix("royksopp.jpg"));
        royksopp.append(Informant.getEncoded(royksoppUri));

        Path saabPath = Paths.get("src", "main", "webapp", "resources", "media", "saab.jpg");
        String saabUri = saabPath.toAbsolutePath().toString();
        StringBuilder saab = new StringBuilder();
        saab.append(Informant.getBasePrefix("saab.jpg"));
        saab.append(Informant.getEncoded(saabUri));

        StringBuilder persistence = new StringBuilder();
        persistence.append(saab);
        persistence.append(Informant.DELIMITER);
        persistence.append(royksopp);

        Paper paper = new Paper();
        paper.setContent("They trooped down the hillside silently, they'd abjured unnecessary speech, along with sex and liquor. Some stopped to pour out food they wouldn't need, covering the path with flour.");
        paper.setUserId(1L);
        paper.setSixtyFour(audi.toString());
        paper.setTimeCreated(Informant.getDate(0));
        paperRepo.save(paper);

        String permissionUne = "sheets:maintenance:1";
        userRepo.savePermission(1L, permissionUne);

        Paper paperDeux = new Paper();
        paperDeux.setContent("Abjure unnecessary speech with sex and liquor, stalled don't");
        paperDeux.setUserId(2L);
        paperDeux.setSixtyFour(persistence.toString());
        paperDeux.setTimeCreated(Informant.getDate(2));
        paperRepo.save(paperDeux);

        String permissionDeux = "sheets:maintenance:2";
        userRepo.savePermission(2L, permissionDeux);

        Paper paperTrois = new Paper();
        paperTrois.setContent("Confirmed.");
        paperTrois.setOriginId(2L);
        paperTrois.setOriginContent("Abjure unnecessary speech, along with sex and liquor.");
        paperTrois.setUserId(1L);
        paperTrois.setTimeCreated(Informant.getDate(3));
        paperRepo.save(paperTrois);

        String permissionTrois = "sheets:maintenance:3";
        userRepo.savePermission(1L, permissionTrois);
    }
}
