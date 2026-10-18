package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.CardSubtype;
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


public class Card_1291_067_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("rescue", "1291_67");
                    put("amidala", "12_22");
                    put("anakin", "200_2");
                    put("obi", "11_6");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("petranaki", "227_10");
                    put("vader", "1_168");
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
    public void RescueOnGeonosisStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("rescue").getBlueprint();
        assertEquals("Rescue On Geonosis", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.MISSION); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.CLONE_ARMY);
            add(Icon.MISSION);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RescueOnGeonosisDeploysOnBegunTheCloneWarHas() {
        var scn = GetScenario();
        var rescue = scn.GetLSCard("rescue");
        scn.StartGame();
        var host = MissionTestSupport.putOnTable(scn, 9700, scn.LS, MissionTestSupport.begunTheCloneWarHas());
        scn.MoveCardsToHand(rescue);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(rescue));
        scn.LSDeployCard(rescue);
        scn.LSChooseCard(host);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(host, rescue));
    }

    @Test
    public void RescueOnGeonosisTakesAmidalaFromReserveDeck() {
        var scn = GetScenario();
        var rescue = scn.GetLSCard("rescue");
        var amidala = scn.GetLSCard("amidala");
        scn.StartGame();
        var host = MissionTestSupport.putOnTable(scn, 9700, scn.LS, MissionTestSupport.begunTheCloneWarHas());
        scn.AttachCardsTo(host, rescue);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfOwnReserveDeck(amidala);
        assertTrue(scn.LSActionAvailable("Take Amidala into hand from Reserve Deck"));
        scn.LSUseCardAction(rescue, "Take Amidala into hand from Reserve Deck");
        scn.LSChooseCard(amidala);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, amidala);
    }

    @Test
    public void RescueOnGeonosisMakesPetranakiAPrisonAndLosesAmidalaIfNotImprisoned() {
        var scn = GetScenario();
        var rescue = scn.GetLSCard("rescue");
        var amidala = scn.GetLSCard("amidala");
        var petranaki = scn.GetDSCard("petranaki");
        scn.StartGame();
        var host = MissionTestSupport.putOnTable(scn, 9700, scn.LS, MissionTestSupport.begunTheCloneWarHas());
        scn.AttachCardsTo(host, rescue);
        scn.MoveLocationToTable(petranaki);
        assertTrue(scn.HasKeyword(petranaki, Keyword.PRISON));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), amidala);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.PassResponses("required");
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, amidala);
    }

    @Test
    public void RescueOnGeonosisDeploysAmidalaImprisonedFromHand() {
        var scn = GetScenario();
        var rescue = scn.GetLSCard("rescue");
        var amidala = scn.GetLSCard("amidala");
        var petranaki = scn.GetDSCard("petranaki");
        scn.StartGame();
        var host = MissionTestSupport.putOnTable(scn, 9700, scn.LS, MissionTestSupport.begunTheCloneWarHas());
        scn.AttachCardsTo(host, rescue);
        scn.MoveLocationToTable(petranaki);
        scn.MoveCardsToHand(amidala);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSActionAvailable("Deploy Amidala imprisoned from hand"));
        scn.LSUseCardAction(rescue, "Deploy Amidala imprisoned from hand");
        scn.PassAllResponses();
        assertTrue(amidala.isCaptive());
        assertTrue(scn.HasKeyword(petranaki, Keyword.PRISON));
    }

    @Test
    public void RescueOnGeonosisCompletesOnForceDrainRelease() {
        var scn = GetScenario();
        var rescue = scn.GetLSCard("rescue");
        var amidala = scn.GetLSCard("amidala");
        var petranaki = scn.GetDSCard("petranaki");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        var host = MissionTestSupport.putOnTable(scn, 9700, scn.LS, MissionTestSupport.begunTheCloneWarHas());
        scn.AttachCardsTo(host, rescue);
        scn.MoveLocationToTable(petranaki);
        scn.gameState().imprisonCharacter(scn.game(), amidala, petranaki);
        scn.MoveCardsToLocation(petranaki, luke);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(petranaki));
        scn.LSForceDrainAt(petranaki);
        scn.PassDSResponses("optional");
        assertTrue(scn.LSActionAvailable("Release a captive"));
        scn.LSChooseAction("Release a captive");
        scn.PassAllResponses();
        assertFalse(amidala.isCaptive());
        assertTrue(rescue.getWhileInPlayData() != null);
    }

    @Test
    public void RescueOnGeonosisTurnTheTideIfOpponentControlsPetranaki() {
        var scn = GetScenario();
        var rescue = scn.GetLSCard("rescue");
        var petranaki = scn.GetDSCard("petranaki");
        var vader = scn.GetDSCard("vader");
        scn.StartGame();
        var host = MissionTestSupport.putOnTable(scn, 9700, scn.LS, MissionTestSupport.begunTheCloneWarHas());
        scn.AttachCardsTo(host, rescue);
        scn.MoveLocationToTable(petranaki);
        scn.MoveCardsToLocation(petranaki, vader);
        scn.EnterControlOrKeepRequired();
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, rescue);
    }
}
