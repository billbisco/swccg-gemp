package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_021_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("sandtrooper", "1210_21");
                    put("dewback", "7_312");
                    put("desert", "6_169");
                    put("vader", "1_168");
                    put("saber", "1_324");
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

    protected VirtualTableScenario GetOffTatooineScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("sandtrooper", "1210_21");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DSStartingLocation("1_284"),
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void SandtrooperStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("sandtrooper").getBlueprint();

        assertEquals("Sandtrooper", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SANDTROOPER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.WARRIOR);
            add(Icon.SPECIAL_EDITION);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SandtrooperDeploysToTatooineMarketplace() {
        var scn = GetScenario();
        var sandtrooper = scn.GetDSCard("sandtrooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(sandtrooper);
        scn.EnsureDSForcePile(3);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(sandtrooper));
        scn.DSPlayCard(sandtrooper);
        scn.DSChooseCard(site);
        scn.PassAllResponses();
        assertEquals(site, sandtrooper.getAtLocation());
    }

    @Test
    public void SandtrooperMayNotDeployToDeathStar() {
        var scn = GetOffTatooineScenario();
        var sandtrooper = scn.GetDSCard("sandtrooper");

        scn.StartGame();
        scn.MoveCardsToDSHand(sandtrooper);
        scn.EnsureDSForcePile(3);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardPlayAvailable(sandtrooper));
    }

    @Test
    public void SandtrooperIsImmuneToSandwhirl() {
        var scn = GetScenario();
        var sandtrooper = scn.GetDSCard("sandtrooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, sandtrooper);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), sandtrooper, Title.Sandwhirl));
    }

    @Test
    public void SandtrooperResetsYourDesertLocationsToDestinyFour() {
        var scn = GetScenario();
        var sandtrooper = scn.GetDSCard("sandtrooper");
        var desert = scn.GetDSCard("desert");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(desert);
        scn.MoveCardsToLocation(site, sandtrooper);
        assertEquals(4, scn.GetDestiny(desert));
    }

    @Test
    public void SandtrooperRidingDewbackIsPowerPlusThreeAndMayNotBeTargetedByLightsabers() {
        var scn = GetScenario();
        var sandtrooper = scn.GetDSCard("sandtrooper");
        var dewback = scn.GetDSCard("dewback");
        var vader = scn.GetDSCard("vader");
        var saber = scn.GetDSCard("saber");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, dewback, vader);
        scn.AttachCardsTo(vader, saber);
        assertEquals(2, scn.GetPower(sandtrooper));
        assertTrue(scn.game().getModifiersQuerying().canBeTargetedBy(scn.gameState(), sandtrooper, saber));
        scn.BoardAsPassenger(dewback, sandtrooper);
        assertEquals(dewback, sandtrooper.getAttachedTo());
        assertEquals(5, scn.GetPower(sandtrooper));
        assertFalse(scn.game().getModifiersQuerying().canBeTargetedBy(scn.gameState(), sandtrooper, saber));
    }
}
