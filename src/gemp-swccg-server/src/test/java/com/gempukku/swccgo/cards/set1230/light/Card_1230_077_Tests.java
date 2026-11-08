package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Card_1230_077_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("orri", "1230_77");
                    put("original", "2230_77");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("blaster", "1_317"); // Imperial Blaster
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
    public void StatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("orri").getBlueprint();
        assertEquals("Orrimaarko", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(5, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(4, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SCOUT);
            add(Keyword.LEADER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.WARRIOR);
            add(Icon.ENDOR);
            add(Icon.LEGACY_BLOCK_3);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void Gameplay() {
        var scn = GetScenario();
        var card = scn.GetLSCard("orri");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, card);
        assertTrue(card.getZone() == Zone.AT_LOCATION || card.getZone() == Zone.ATTACHED);
    }
    @Test
    public void OrrimaarkoHasTheErrataText() {
        var scn = GetScenario();
        var card = scn.GetLSCard("orri").getBlueprint();
        assertEquals("Once per game, may take a rifle and/or an Interrupt with 'scout' in game text into hand from Reserve Deck; reshuffle. While alone at an exterior site, opponent's weapon destiny draws here are -2. Immune to attrition < 4.",
                card.getGameText());
        assertNull(card.getLegacyVersion());
    }

    /** Orrimaarko alone at the (exterior) Marketplace with an opponent's Imperial Blaster there; returns the -2 count. */
    protected static long weaponDestinyMinusTwoHere(VirtualTableScenario scn, PhysicalCardImpl orri, boolean undercover) {
        var site = scn.GetDSStartingLocation();
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");
        scn.StartGame();
        assertTrue(Filters.exterior_site.accepts(scn.game(), site));
        scn.MoveCardsToLocation(site, orri, trooper);
        scn.AttachCardsTo(trooper, blaster);
        if (undercover) {
            scn.ApplyAdHocModifier(new KeywordModifier(orri, orri, Keyword.SPY));
            scn.MakeCardGoUndercover(orri);
            assertTrue(orri.isUndercover());
        }
        return scn.game().getModifiersQuerying().getModifiers(scn.gameState(), ModifierType.EACH_WEAPON_DESTINY)
                .stream().filter(m -> m.getSource(scn.gameState()) == orri).count();
    }

    @Test
    public void OrrimaarkoAloneAtExteriorSiteMakesOpponentsWeaponDestinyMinusTwo() {
        var scn = GetScenario();
        assertEquals(1, weaponDestinyMinusTwoHere(scn, scn.GetLSCard("orri"), false));
    }

    @Test
    public void OrrimaarkoWeaponDestinyClauseAppliesEvenWhileUndercover() {
        // The errata removed "Unless Undercover,"
        var scn = GetScenario();
        assertEquals(1, weaponDestinyMinusTwoHere(scn, scn.GetLSCard("orri"), true));
    }

    @Test
    public void OrrimaarkoAndHisOriginalVersionAreTheSameUniqueCard() {
        var scn = GetScenario();
        var orri = scn.GetLSCard("orri");
        var original = scn.GetLSCard("original");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), original);
        scn.MoveCardsToLSHand(orri);
        scn.EnsureLSForcePile(8);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSDeployAvailable(orri));
    }
}
