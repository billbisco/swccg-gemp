package com.gempukku.swccgo.cards.set1101.light;

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

public class Card_1101_006_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("saitorr", "1101_6");
                    put("luke", "1_19");
                    put("saber", "9_90");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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
    public void SaitorrKalFasStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("saitorr").getBlueprint();

        assertEquals(Title.Saitorr_Kal_Fas, card.getTitle());
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
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
    }

    @Test
    public void SaitorrKalFasDownloadsMatchingWeaponOnJustDeployedUniqueCharacter() {
        var scn = GetScenario();
        var saitorr = scn.GetLSCard("saitorr");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(saitorr);
        scn.MoveCardsToLSHand(luke);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(8);
        scn.MoveCardsToTopOfLSReserveDeck(saber);
        assertTrue(scn.LSDeployAvailable(luke));
        scn.LSDeployCard(luke);
        scn.LSChooseCard(site);
        assertTrue(scn.DSDecisionAvailable("Use 3 Force - Optional responses"));
        scn.DSPass();
        assertTrue(scn.LSDecisionAvailable("Use 3 Force - Optional responses"));
        scn.LSPass();
        assertTrue(scn.DSDecisionAvailable("PLAY - Optional responses"));
        scn.DSPass();
        assertTrue(scn.LSDecisionAvailable("just deployed - Optional responses"));
        assertTrue(scn.LSCardActionAvailable(saitorr, "Deploy matching weapon"));
        scn.LSUseCardAction(saitorr, "Deploy matching weapon");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.LSChooseCard(saber);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(luke, saber));
    }
}
