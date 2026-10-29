package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1220_108_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("remote", "1220_108");
                    put("golan", "3_075");
                    put("eg4", "4_013");
                    put("cantina", "1_128");
                }},
                new HashMap<>() {{
                    put("cannon", "1220_83");
                    put("atat", "104_5");
                    put("veers", "3_87");
                    put("docking", "1_291");
                    put("ice", "3_148");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void ArtilleryRemoteStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("remote").getBlueprint();

        assertEquals("Artillery Remote", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEVICE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_CHARACTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEVICE);
            add(Icon.HOTH);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ArtilleryRemoteMakesArtilleryAtSameSiteHeroicWithArmorFour() {
        var scn = GetScenario();
        var remote = scn.GetLSCard("remote");
        var golan = scn.GetLSCard("golan");
        var warrior = scn.GetLSFiller(1);
        var docking = scn.GetDSCard("docking");

        scn.StartGame();
        scn.MoveLocationToTable(docking);
        scn.MoveCardsToLocation(docking, warrior);
        scn.AttachCardsTo(docking, golan);
        scn.AttachCardsTo(warrior, remote);
        assertTrue(scn.HasKeyword(golan, Keyword.HEROIC));
        assertEquals(4, scn.GetArmor(golan));
    }

    @Test
    public void ArtilleryRemoteDoesNotMakeArtilleryHeroicOnAnotherPlanet() {
        var scn = GetScenario();
        var remote = scn.GetLSCard("remote");
        var golan = scn.GetLSCard("golan");
        var warrior = scn.GetLSFiller(1);
        var docking = scn.GetDSCard("docking");
        var ice = scn.GetDSCard("ice");

        scn.StartGame();
        scn.MoveLocationToTable(docking);
        scn.MoveLocationToTable(ice);
        scn.MoveCardsToLocation(docking, warrior);
        scn.AttachCardsTo(warrior, remote);
        scn.AttachCardsTo(ice, golan);
        assertFalse(scn.HasKeyword(golan, Keyword.HEROIC));
    }

    @Test
    public void ArtilleryRemoteLetsATATCannonTargetArtilleryLikeAVehicle() {
        var scn = GetScenario();
        var remote = scn.GetLSCard("remote");
        var golan = scn.GetLSCard("golan");
        var warrior = scn.GetLSFiller(1);
        var cannon = scn.GetDSCard("cannon");
        var atat = scn.GetDSCard("atat");
        var veers = scn.GetDSCard("veers");
        var docking = scn.GetDSCard("docking");

        scn.StartGame();
        scn.MoveLocationToTable(docking);
        scn.MoveCardsToLocation(docking, warrior, atat);
        scn.BoardAsPilot(atat, veers);
        scn.AttachCardsTo(warrior, remote);
        scn.AttachCardsTo(docking, golan);
        scn.AttachCardsTo(atat, cannon);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(docking);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(cannon, "Fire"));
        scn.DSUseCardAction(cannon, "Fire");
        assertTrue(scn.DSHasCardChoiceAvailable(golan));
    }

    @Test
    public void ArtilleryRemoteWithoutDeviceATATCannonCannotTargetArtillery() {
        var scn = GetScenario();
        var golan = scn.GetLSCard("golan");
        var warrior = scn.GetLSFiller(1);
        var cannon = scn.GetDSCard("cannon");
        var atat = scn.GetDSCard("atat");
        var veers = scn.GetDSCard("veers");
        var docking = scn.GetDSCard("docking");

        scn.StartGame();
        scn.MoveLocationToTable(docking);
        scn.MoveCardsToLocation(docking, warrior, atat);
        scn.BoardAsPilot(atat, veers);
        scn.AttachCardsTo(docking, golan);
        scn.AttachCardsTo(atat, cannon);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(docking);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(cannon, "Fire"));
        scn.DSUseCardAction(cannon, "Fire");
        assertFalse(scn.DSHasCardChoiceAvailable(golan));
        assertTrue(scn.DSHasCardChoiceAvailable(warrior));
    }

    @Test
    public void ArtilleryRemoteMayPlaceInUsedPileToAddTwoToArtilleryWeaponDestiny() {
        var scn = GetScenario();
        var remote = scn.GetLSCard("remote");
        var golan = scn.GetLSCard("golan");
        var eg4 = scn.GetLSCard("eg4");
        var warrior = scn.GetLSFiller(1);
        var docking = scn.GetDSCard("docking");
        var stormtrooper = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(docking);
        scn.MoveCardsToLocation(docking, warrior, eg4, stormtrooper);
        scn.AttachCardsTo(warrior, remote);
        scn.AttachCardsTo(docking, golan);
        scn.EnsureLSForcePile(4);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(docking);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(golan, "Fire"));
        scn.PrepareLSDestiny(1);
        scn.LSUseCardAction(golan, "Fire");
        if (scn.LSHasCardChoiceAvailable(warrior)) {
            scn.LSChooseCard(warrior);
        }
        if (scn.LSHasCardChoiceAvailable(stormtrooper)) {
            scn.LSChooseCard(stormtrooper);
        }
        scn.PassForceUseResponses();
        boolean placed = false;
        for (int i = 0; i < 12; i++) {
            try {
                var actions = scn.GetLSAvailableActions();
                if (actions != null && actions.stream().anyMatch(a -> a != null && a.contains("Place in Used Pile"))) {
                    scn.LSUseCardAction(remote, "Place in Used Pile to add 2 to destiny");
                    placed = true;
                    break;
                }
            } catch (RuntimeException ignored) {
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
            } else if (scn.DSGetDecision() != null) {
                scn.DSPass();
            } else {
                break;
            }
        }
        assertTrue(placed);
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, remote);
    }
}
