package com.gempukku.swccgo.cards.set1102.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1102_021_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("rycar", "1102_21");
                    put("luke", "1_19");
                    put("affect", "1_043");
                }},
                new HashMap<>() {{
                    put("emperor", "9_109");
                    put("lightning", "9_136");
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
    public void RycarRyjerdStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("rycar").getBlueprint();

        assertEquals(Title.Rycar_Ryjerd, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
    }

    @Test
    public void RycarRyjerdTakesAnEffectThatDeploysOnCharactersFromReserveDeck() {
        var scn = GetScenario();
        var rycar = scn.GetLSCard("rycar");
        var affect = scn.GetLSCard("affect");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(rycar);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(affect);

        assertTrue(scn.LSCardActionAvailable(rycar, "Take card into hand from Reserve Deck"));
        scn.LSUseCardAction(rycar, "Take card into hand from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        if (scn.LSHasCardChoiceAvailable(affect)) {
            scn.LSChooseCard(affect);
            scn.PassAllResponses();
        }
        assertEquals(Zone.HAND, affect.getZone());
    }

    @Test
    public void RycarRyjerdPlacesAttachedEffectsInUsedPileWhenCharacterIsAboutToBeLost() {
        var scn = GetScenario();
        var rycar = scn.GetLSCard("rycar");
        var luke = scn.GetLSCard("luke");
        var affect = scn.GetLSCard("affect");
        var emperor = scn.GetDSCard("emperor");
        var lightning = scn.GetDSCard("lightning");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(rycar);
        scn.MoveCardsToLocation(site, luke, emperor);
        scn.AttachCardsTo(luke, affect);
        scn.MoveCardsToDSHand(lightning);
        scn.EnsureDSForcePile(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(7);

        assertTrue(scn.DSCardPlayAvailable(lightning, "Make target lost"));
        scn.DSPlayCard(lightning, "Make target lost");
        if (scn.DSDecisionAvailable("Choose Emperor")) {
            scn.DSChooseCard(emperor);
        }
        if (scn.DSHasCardChoiceAvailable(luke)) {
            scn.DSChooseCard(luke);
        }
        scn.PassResponses("ATTRIBUTE");
        scn.PassCardPlayResponses();
        scn.PassDestinyDrawResponses();
        scn.PassResponses("DESTINY");
        scn.PassResponses("ATTRIBUTE");
        scn.PassResponses("ABOUT_TO_BE_LOST");
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, affect);
    }
}
