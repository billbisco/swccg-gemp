package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1250_028_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{ put("luke", "1_19"); put("rifle", "1_153"); put("red1", "1_144"); put("torpedoes", "1_158"); }},
                new HashMap<>() {{ put("tactics", "1250_28"); put("sil", "14_88"); put("jango", "1250_8"); put("trooper", "1_194");
                    put("avenger", "1263_19"); put("system", "1_282"); }},
                20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void TradeFederationTacticsVStatsAndIdentityMatchPrintedCard() {
        var scn = scenario();
        var bp = scn.GetDSCard("tactics").getBlueprint();
        assertEquals("Trade Federation Tactics", bp.getTitle());
        assertTrue(bp.hasVirtualSuffix());
        assertTrue(bp.isLegacy());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(6, bp.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, bp.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.INTERRUPT); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.INTERRUPT); add(Icon.EPISODE_I); add(Icon.THEED_PALACE); add(Icon.LEGACY_BLOCK_5); }});
    }

    /** Luke fires a Blaster Rifle at Sil Unch during the Light Side's battle. */
    private VirtualTableScenario lukeFiresAtSil(String otherDarkCharacter) {
        var scn = scenario();
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSCard("sil"), scn.GetDSCard(otherDarkCharacter));
        scn.AttachCardsTo(scn.GetLSCard("luke"), scn.GetLSCard("rifle"));
        scn.MoveCardsToDSHand(scn.GetDSCard("tactics"));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSActivateForceCheat(4);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        scn.PrepareLSDestiny(6);
        scn.LSUseCardAction(scn.GetLSCard("rifle"), "Fire");
        scn.LSChooseCard(scn.GetDSCard("sil"));
        scn.PassForceUseResponses();
        return scn;
    }

    @Test
    public void TradeFederationTacticsVRetargetsWeaponFromEpisodeICharacterToOtherEpisodeICharacterPresent() {
        var scn = lukeFiresAtSil("jango");
        var tactics = scn.GetDSCard("tactics");
        assertTrue(scn.DSCardPlayAvailable(tactics));
        scn.DSPlayCard(tactics);
        if (scn.DSHasCardChoiceAvailable(scn.GetDSCard("sil"))) scn.DSChooseCard(scn.GetDSCard("sil"));
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetDSCard("jango")));
        scn.DSChooseCard(scn.GetDSCard("jango"));
        scn.PassAllResponses();
        scn.PassWeaponFireWithDestinyDraw();
        assertTrue(scn.GetDSCard("jango").isHit());
        assertFalse(scn.GetDSCard("sil").isHit());
        assertInZone(Zone.USED_PILE, tactics);
    }

    @Test
    public void TradeFederationTacticsVMayNotRetargetToNonEpisodeICharacter() {
        var scn = lukeFiresAtSil("trooper");
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("tactics")));
    }

    @Test
    public void TradeFederationTacticsVRetargetsStarshipHitToCharacterAboardAndProtectsStarship() {
        var scn = scenario();
        var system = scn.GetDSCard("system");
        scn.StartGame();
        scn.MoveLocationToTable(system);
        var avenger = scn.GetDSCard("avenger");
        var red1 = scn.GetLSCard("red1");
        scn.MoveCardsToLocation(system, avenger, red1);
        scn.BoardAsPilot(avenger, scn.GetDSCard("jango"));
        scn.BoardAsPilot(red1, scn.GetLSCard("luke"));
        scn.AttachCardsTo(red1, scn.GetLSCard("torpedoes"));
        scn.MoveCardsToDSHand(scn.GetDSCard("tactics"));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSActivateForceCheat(4);
        scn.LSInitiateBattle(system);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        scn.PrepareLSDestiny(7);
        scn.LSUseCardAction(scn.GetLSCard("torpedoes"), "Fire");
        if (scn.LSHasCardChoiceAvailable(avenger)) scn.LSChooseCard(avenger);
        scn.PassForceUseResponses();
        scn.PassResponses("Fire ");
        scn.PassDestinyDrawResponses();
        var tactics = scn.GetDSCard("tactics");
        for (int i = 0; i < 6 && !(scn.DSAnyDecisionsAvailable() && scn.DSCardPlayAvailable(tactics)); i++) {
            if (scn.LSAnyDecisionsAvailable()) scn.LSPass(); else if (scn.DSAnyDecisionsAvailable()) scn.DSPass();
        }
        assertTrue(scn.DSCardPlayAvailable(tactics));
        scn.DSPlayCard(tactics);
        if (scn.DSHasCardChoiceAvailable(scn.GetDSCard("jango"))) scn.DSChooseCard(scn.GetDSCard("jango"));
        scn.PassAllResponses();
        assertFalse(avenger.isHit());
        assertTrue(scn.GetDSCard("jango").isHit());
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(),
                com.gempukku.swccgo.logic.modifiers.ModifierType.MAY_NOT_BE_TARGETED_BY, avenger).isEmpty());
    }

    @Test
    public void TradeFederationTacticsVDeploysSilUnchFromReserveDeck() {
        var scn = scenario();
        var tactics = scn.GetDSCard("tactics");
        var sil = scn.GetDSCard("sil");
        scn.StartGame();
        scn.MoveCardsToDSHand(tactics);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSActivateForceCheat(4);
        scn.MoveCardsToTopOfDSReserveDeck(sil);
        assertTrue(scn.DSCardPlayAvailable(tactics));
        scn.DSPlayCard(tactics);
        scn.PassAllResponses();
        if (scn.DSHasCardChoiceAvailable(sil)) scn.DSChooseCard(sil);
        if (scn.DSDecisionAvailable("Choose where to deploy") || scn.DSDecisionAvailable("Choose location where to deploy")) scn.DSChooseCard(scn.GetDSStartingLocation());
        scn.PassAllResponses();
        assertEquals(Zone.AT_LOCATION, sil.getZone());
    }
}
