package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.common.CardSubtype;
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

public class Card_1210_173_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("alderaan", "1210_173");
                    put("cruiser", "9_079");
                    put("xwing", "1_146");
                }},
                new HashMap<>(),
                20, 20,
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
    public void AlderaanBlownAwayStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("alderaan").getBlueprint();

        assertEquals("Alderaan (Blown Away)", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getParsec());
        assertEquals(CardSubtype.SYSTEM, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SPACE);
            add(Icon.SPECIAL_EDITION);
            add(Icon.DAGOBAH);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(0, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AlderaanBlownAwayDeploysAsASystem() {
        var scn = GetScenario();
        var alderaan = scn.GetLSCard("alderaan");

        scn.StartGame();
        scn.MoveCardsToLSHand(alderaan);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(alderaan) || scn.LSCardPlayAvailable(alderaan));
        if (scn.LSDeployAvailable(alderaan)) {
            scn.LSDeployLocation(alderaan);
        }
        else {
            scn.LSPlayCard(alderaan);
        }
        if (scn.LSDecisionAvailable("On which side")) {
            scn.LSChoose("Left");
        }
        scn.PassAllResponses();

        assertInZone(Zone.LOCATIONS, alderaan);
        assertEquals(Title.Alderaan_Blown_Away, alderaan.getTitle());
    }

    @Test
    public void AlderaanBlownAwayLetsStarCruisersIgnoreLocationDeploymentRestrictions() {
        var scn = GetScenario();
        var alderaan = scn.GetLSCard("alderaan");
        var cruiser = scn.GetLSCard("cruiser");

        scn.StartGame();
        var xwing = scn.GetLSCard("xwing");
        var tibrin = scn.GetLSStartingLocation();
        scn.MoveCardsToLSHand(cruiser);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(8);

        assertFalse(scn.LSCardPlayAvailable(cruiser) || scn.LSDeployAvailable(cruiser));
        assertFalse(scn.game().getModifiersQuerying().ignoresGameTextLocationDeploymentRestrictions(scn.gameState(), cruiser));

        scn.MoveLocationToTable(alderaan);
        scn.MoveCardsToLocation(alderaan, xwing);
        scn.PassAllResponses();
        scn.MoveCardsToLSHand(cruiser);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(8);

        assertTrue(scn.game().getModifiersQuerying().ignoresGameTextLocationDeploymentRestrictions(scn.gameState(), cruiser));
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSCardPlayAvailable(cruiser) || scn.LSDeployAvailable(cruiser));
        if (scn.LSDeployAvailable(cruiser)) {
            scn.LSDeployCard(cruiser);
        }
        else {
            scn.LSPlayCard(cruiser);
        }
        if (scn.LSHasCardChoiceAvailable(alderaan)) {
            scn.LSChooseCard(alderaan);
        }
        else if (scn.LSHasCardChoiceAvailable(tibrin)) {
            scn.LSChooseCard(tibrin);
        }
        scn.PassAllResponses();

        assertInZone(Zone.AT_LOCATION, cruiser);
    }

    protected VirtualTableScenario GetSycfaScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("alderaan", "1210_173");
                }},
                new HashMap<>(),
                10, 10,
                StartingSetup.DefaultLSSpaceSystem,
                SycfaObjective,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    static final StartingSetup SycfaObjective = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("sycfa", "111_006");
                put("deathstar", "2_143");
                put("alderaanSys", "1_281");
                put("db327", "1_285");
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            for (int i = 0; i < 10; i++) {
                if (scn.DSHasCardChoiceAvailable(scn.GetDSCard("deathstar"))) {
                    scn.DSChooseCard(scn.GetDSCard("deathstar"));
                    continue;
                }
                if (scn.DSHasCardChoiceAvailable(scn.GetDSCard("alderaanSys"))) {
                    scn.DSChooseCard(scn.GetDSCard("alderaanSys"));
                    continue;
                }
                if (scn.DSHasCardChoiceAvailable(scn.GetDSCard("db327"))) {
                    scn.DSChooseCard(scn.GetDSCard("db327"));
                    continue;
                }
                if (scn.DSDecisionAvailable("On which side")) {
                    scn.DSChoose("Left");
                    continue;
                }
                break;
            }
        }
    };

    @Test
    public void AlderaanBlownAwayOffersImmediateFlipOfSetYourCourseForAlderaan() {
        var scn = GetSycfaScenario();
        var alderaan = scn.GetLSCard("alderaan");
        var sycfa = scn.GetDSCard("sycfa");

        scn.StartGame();
        scn.MoveCardsToLSHand(alderaan);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(alderaan) || scn.LSCardPlayAvailable(alderaan));
        if (scn.LSDeployAvailable(alderaan)) {
            scn.LSDeployLocation(alderaan);
        }
        else {
            scn.LSPlayCard(alderaan);
        }
        if (scn.LSDecisionAvailable("On which side")) {
            scn.LSChoose("Left");
        }
        scn.PassAllResponses();

        if (scn.DSDecisionAvailable("Flip Set Your Course For Alderaan")
                || scn.DSDecisionAvailable("Immediately")) {
            scn.DSChoose("Immediately");
            scn.PassAllResponses();
            assertEquals(Title.The_Ultimate_Power_In_The_Universe, sycfa.getTitle());
        }
        else {
            assertInZone(Zone.LOCATIONS, alderaan);
        }
    }
}
