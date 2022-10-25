package io.informant.assets;

import io.informant.Main;
import io.informant.model.Paper;
import io.informant.repo.PaperRepo;
import io.informant.repo.UserRepo;
import io.kakai.Kakai;
import io.kakai.annotate.StartupEvent;
import io.kakai.events.KakaiStartup;
import io.kakai.security.SecurityManager;

import java.nio.file.Path;
import java.nio.file.Paths;

@StartupEvent
public class AmadeusStartup implements KakaiStartup {
    @Override//todo:∆ from setup complete to initialized
    public void setupComplete(Kakai kakai) {
        SecurityAccess securityAccess = (SecurityAccess) kakai.getElement("securityaccess");
        SecurityManager.configure(securityAccess);

        PaperRepo paperRepo = (PaperRepo) kakai.getElement("paperrepo");
        UserRepo userRepo = (UserRepo) kakai.getElement("userrepo");

        Path audiPath = Paths.get("web-ux", "resources", "media", "audi.jpg");
        String audiUri = audiPath.toAbsolutePath().toString();
        StringBuilder audi = new StringBuilder();
        audi.append(Main.getBasePrefix("audi.jpg"));
        audi.append(Main.getEncoded(audiUri));

        Path royksoppPath = Paths.get("web-ux", "resources", "media", "royksopp.jpg");
        String royksoppUri = royksoppPath.toAbsolutePath().toString();
        StringBuilder royksopp = new StringBuilder();
        royksopp.append(Main.getBasePrefix("royksopp.jpg"));
        royksopp.append(Main.getEncoded(royksoppUri));

        Path saabPath = Paths.get("web-ux", "resources", "media", "saab.jpg");
        String saabUri = saabPath.toAbsolutePath().toString();
        StringBuilder saab = new StringBuilder();
        saab.append(Main.getBasePrefix("saab.jpg"));
        saab.append(Main.getEncoded(saabUri));

        StringBuilder persistence = new StringBuilder();
        persistence.append(saab);
        persistence.append(Main.DELIMITER);
        persistence.append(royksopp);

        Paper paper = new Paper();
        paper.setContent("They trooped down the hillside silently, they'd abjured unnecessary speech, along with sex and liquor. Some stopped to pour out food they wouldn't need, covering the path with flour.");
        paper.setUserId(1L);
        paper.setSixtyFour(audi.toString());
        paper.setTimeCreated(Main.getDate(0));
        paperRepo.save(paper);

        String permissionUne = "sheets:maintenance:1";
        userRepo.savePermission(1L, permissionUne);

        Paper paperDeux = new Paper();
        paperDeux.setContent("Abjure unnecessary speech with sex and liquor, stalled don't");
        paperDeux.setUserId(2L);
        paperDeux.setSixtyFour(persistence.toString());
        paperDeux.setTimeCreated(Main.getDate(2));
        paperRepo.save(paperDeux);

        String permissionDeux = "sheets:maintenance:2";
        userRepo.savePermission(2L, permissionDeux);

        Paper paperTrois = new Paper();
        paperTrois.setContent("Confirmed.");
        paperTrois.setOriginId(2L);
        paperTrois.setOriginContent("Abjure unnecessary speech, along with sex and liquor.");
        paperTrois.setUserId(1L);
        paperTrois.setTimeCreated(Main.getDate(3));
        paperRepo.save(paperTrois);

        String permissionTrois = "sheets:maintenance:3";
        userRepo.savePermission(1L, permissionTrois);
    }
}
