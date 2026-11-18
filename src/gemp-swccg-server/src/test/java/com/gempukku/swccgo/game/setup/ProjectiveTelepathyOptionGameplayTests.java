package com.gempukku.swccgo.game.setup;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class ProjectiveTelepathyOptionGameplayTests {
    @Parameterized.Parameters(name = "format={0}, choice={1}, imported={2}")
    public static List<Object[]> values() {
        List<Object[]> rows = new ArrayList<>();
        for (String format : List.of("open", "premiere_ds2"))
            for (String choice : List.of("printed", "current"))
                for (String imported : List.of("5_149", "5_149o"))
                    rows.add(new Object[]{format, choice, imported});
        return rows;
    }

    private final String format;
    private final String choice;
    private final String imported;

    public ProjectiveTelepathyOptionGameplayTests(String format, String choice, String imported) {
        this.format = format;
        this.choice = choice;
        this.imported = imported;
    }

    private VirtualTableScenario battle(boolean own) {
        var formats = VirtualTableScenario._formatLibrary;
        var setup = GameSetup.of(formats.getFormat(format), Map.of(GameOptions.PROJECTIVE_TELEPATHY, choice), GameOption.Scope.CASUAL);
        var effective = formats.effectiveFormat(setup);
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("rebel", "1_28")),
                new HashMap<>(Map.of("telepathy", imported, "alien", "1_171")), effective);
        assertEquals(choice.equals("printed") ? "5_149o" : "5_149", scn.GetDSCard("telepathy").getBlueprintId(true));
        scn.StartGame();
        var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("rebel"), scn.GetDSCard("alien"));
        scn.MoveCardsToDSHand(scn.GetDSCard("telepathy"));
        if (own) scn.SkipToDSTurn(Phase.BATTLE);
        else scn.SkipToLSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(own ? 1 : 0);
        scn.EnsureLSForcePile(own ? 0 : 1);
        scn.DSUseForceCheat(scn.GetDSForcePileCount() - (own ? 1 : 0));
        scn.LSUseForceCheat(scn.GetLSForcePileCount() - (own ? 0 : 1));
        if (own) scn.DSInitiateBattle(site);
        else scn.LSInitiateBattle(site);
        return scn;
    }

    @Test
    public void ProjectiveTelepathyChosenPrintedTextCancelsOwnBattleButCurrentDoesNotOfferIt() {
        //test1: the table choice overrides the imported printing at the actual battle response.
        var scn = battle(true);
        if (scn.LSGetDecision() != null) scn.LSPass();
        var card = scn.GetDSCard("telepathy");
        assertTrue(scn.IsActiveBattle());
        assertEquals(choice.equals("printed"), scn.DSCardPlayAvailable(card)); //test1
        if (choice.equals("printed")) {
            scn.DSPlayCard(card);
            scn.PassAllResponses();
            assertFalse(scn.IsActiveBattle()); //test1
            assertTrue(scn.GetDSUsedPile().contains(card)); //test1
        }
    }

    @Test
    public void ProjectiveTelepathyEitherChosenTextCancelsOpponentBattleWithoutPayment() {
        //test1: both texts retain opponent cancellation when two Force cannot be paid.
        var scn = battle(false);
        if (scn.DSGetDecision() == null && scn.LSGetDecision() != null) scn.LSPass();
        var card = scn.GetDSCard("telepathy");
        assertTrue(scn.DSCardPlayAvailable(card)); //test1
        scn.DSPlayCard(card);
        scn.PassAllResponses();
        assertFalse(scn.IsActiveBattle()); //test1
        assertTrue(scn.GetDSUsedPile().contains(card)); //test1
        assertEquals(0, scn.GetLSForcePileCount()); //test1
    }
}
