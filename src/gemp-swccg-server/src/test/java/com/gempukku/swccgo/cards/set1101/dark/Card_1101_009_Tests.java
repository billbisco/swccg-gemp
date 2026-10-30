package com.gempukku.swccgo.cards.set1101.dark;

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
import static org.junit.Assert.assertTrue;

public class Card_1101_009_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("rack", "1101_9");
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

    @Test
    public void BlasterRackStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("rack").getBlueprint();

        assertEquals(Title.Blaster_Rack, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
    }

    @Test
    public void BlasterRackDownloadsMatchingWeaponOnJustDeployedUniqueCharacter() {
        var scn = GetScenario();
        var rack = scn.GetDSCard("rack");
        var vader = scn.GetDSCard("vader");
        var saber = scn.GetDSCard("saber");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(rack);
        scn.MoveCardsToDSHand(vader);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(8);
        scn.MoveCardsToTopOfDSReserveDeck(saber);
        assertTrue(scn.DSDeployAvailable(vader));
        scn.DSDeployCard(vader);
        scn.DSChooseCard(site);
        assertTrue(scn.LSDecisionAvailable("Use 6 Force - Optional responses"));
        scn.LSPass();
        assertTrue(scn.DSDecisionAvailable("Use 6 Force - Optional responses"));
        scn.DSPass();
        assertTrue(scn.LSDecisionAvailable("PLAY - Optional responses"));
        scn.LSPass();
        assertTrue(scn.DSDecisionAvailable("just deployed - Optional responses"));
        assertTrue(scn.DSCardActionAvailable(rack, "Deploy matching weapon"));
        scn.DSUseCardAction(rack, "Deploy matching weapon");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(saber);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(vader, saber));
    }
}
