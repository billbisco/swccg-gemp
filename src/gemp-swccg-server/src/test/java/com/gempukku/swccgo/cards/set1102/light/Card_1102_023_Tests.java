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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1102_023_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("mods", "1102_23");
                    put("falcon", "1_143");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("isd", "1_302");
                    put("turbolaser", "1_323");
                }},
                10, 10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void SpecialModificationsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("mods").getBlueprint();

        assertEquals("Special Modifications", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
        }});
        assertFalse(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SpecialModificationsDeploysOnTable() {
        var scn = GetScenario();
        var mods = scn.GetLSCard("mods");

        scn.StartGame();
        scn.MoveCardsToLSHand(mods);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardPlayAvailable(mods) || scn.LSDeployAvailable(mods));
        scn.LSPlayCard(mods);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, mods);
    }

    @Test
    public void SpecialModificationsPlacesInLostPileToMakeStarshipImmuneToAttrition() {
        var scn = GetScenario();
        var mods = scn.GetLSCard("mods");
        var falcon = scn.GetLSCard("falcon");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(mods);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), falcon);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.LSCardActionAvailable(mods, "Make a starship immune to attrition"));
        scn.LSUseCardAction(mods, "Make a starship immune to attrition");
        scn.LSChooseCard(falcon);
        scn.PassAllResponses();

        assertInZone(Zone.LOST_PILE, mods);
        assertTrue(scn.game().getModifiersQuerying().hasAnyImmunityToAttrition(scn.gameState(), falcon));
    }

    @Test
    public void SpecialModificationsKeepsAStarshipInPlayWhenAboutToBeLostDuringWeaponsSegment() {
        var scn = GetScenario();
        var mods = scn.GetLSCard("mods");
        var falcon = scn.GetLSCard("falcon");
        var luke = scn.GetLSCard("luke");
        var isd = scn.GetDSCard("isd");
        var turbolaser = scn.GetDSCard("turbolaser");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(mods);
        scn.MoveCardsToLocation(system, falcon, isd);
        scn.BoardAsPilot(falcon, luke);
        scn.AttachCardsTo(isd, turbolaser);
        scn.EnsureDSForcePile(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(system);
        scn.PrepareDSDestiny(7);
        scn.PrepareDSDestiny(6);

        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(turbolaser, "Fire"));
        scn.DSUseCardAction(turbolaser, "Fire");
        scn.DSChooseCard(falcon);
        scn.PassCardAndForceUseResponses();
        scn.PassWeaponFireWithDestinyDraw(2);
        assertTrue(falcon.isHit());
        scn.PassWeaponsSegmentActions();
        scn.PassResponses("ABOUT_TO_BE_LOST");
        scn.PassAllResponses();
        assertTrue(falcon.getZone().isInPlay());
    }
}
