package com.gempukku.swccgo.cards.set1117.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1117_015_Tests {
    private static final String FIRE = "Fire Galen's Lightsaber";

    private VirtualTableScenario Started() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("luke", "1_19", "han", "1_11")),
                new HashMap<>(Map.of("saber", "1117_15", "galen", "601_81", "mynock", "4_110")),
                40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        var galen = scn.GetDSCard("galen");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), galen);
        scn.AttachCardsTo(galen, scn.GetDSCard("saber"));
        return scn;
    }

    @Test
    public void HittingACreatureDuringAnAttackDoesNotCrash() {
        var scn = Started();
        var site = scn.GetDSStartingLocation();
        var mynock = scn.GetDSCard("mynock");
        var saber = scn.GetDSCard("saber");
        scn.MoveCardsToLocation(site, mynock);
        scn.EnsureDSForcePile(1);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSUseCardAction(site, "attack");
        scn.PassAllResponses();
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(3), scn.GetDSDestiny(2));
        assertTrue(scn.DSCardActionAvailable(saber, FIRE));
        scn.DSUseCardAction(saber, FIRE);
        if (scn.DSHasCardChoiceAvailable(mynock)) scn.DSChooseCard(mynock);
        scn.PassAllResponses();
        assertTrue(mynock.isHit() || mynock.getZone() == Zone.LOST_PILE);
    }

    @Test
    public void HittingACharacterDuringABattleMakesItPowerMinusThree() {
        var scn = Started();
        var site = scn.GetDSStartingLocation();
        var luke = scn.GetLSCard("luke");
        var han = scn.GetLSCard("han");
        scn.MoveCardsToLocation(site, luke, han);
        scn.EnsureDSForcePile(3);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(3), scn.GetDSDestiny(2));
        scn.DSUseCardAction(scn.GetDSCard("saber"), FIRE);
        scn.DSChooseCard(luke);
        scn.PassAllResponses();
        assertTrue(luke.isHit());
        assertEquals(0, scn.GetPower(luke));
        assertEquals(3, scn.GetPower(han));
    }
}
