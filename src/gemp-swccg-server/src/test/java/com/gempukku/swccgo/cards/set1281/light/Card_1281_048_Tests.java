package com.gempukku.swccgo.cards.set1281.light;

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
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_048_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("gun", "1281_48");
                    put("jawa", "1_12");
                    put("jawa2", "1_12");
                    put("trooper", "1_28");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
                    put("stormtrooper2", "1_194");
                    put("tempest", "8_171");
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
    public void JawaIonGunVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("gun").getBlueprint();

        assertEquals(Title.Jawa_Ion_Gun, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.JAWA_WEAPON);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void JawaIonGunVUsesOneForceToDeployOnYourJawaOnly() {
        var scn = GetScenario();
        var gun = scn.GetLSCard("gun");
        var jawa = scn.GetLSCard("jawa");
        var trooper = scn.GetLSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, jawa, trooper);
        scn.MoveCardsToLSHand(gun);
        scn.LSActivateForceCheat(3);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSDeployAvailable(gun));
        scn.LSDeployCard(gun);
        assertTrue(scn.LSHasCardChoiceAvailable(jawa));
        assertFalse(scn.LSHasCardChoiceAvailable(trooper));
        scn.LSChooseCard(jawa);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(jawa, gun));
        assertEquals(force - 1, scn.GetLSForcePileCount());
    }

    @Test
    public void JawaIonGunVHitsVehicleAndZeroesPowerIfDestinyPlusJawasPresentExceedsDefenseValue() {
        var scn = GetScenario();
        var gun = scn.GetLSCard("gun");
        var jawa = scn.GetLSCard("jawa");
        var jawa2 = scn.GetLSCard("jawa2");
        var tempest = scn.GetDSCard("tempest");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, jawa, jawa2, tempest, stormtrooper);
        scn.AttachCardsTo(jawa, gun);
        scn.LSActivateForceCheat(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(3 /* 3 + 2 Jawas = 5 > 4 */);

        assertEquals(4, scn.game().getModifiersQuerying().getDefenseValue(scn.gameState(), tempest), scn.epsilon);
        scn.LSUseCardAction(gun, "Fire");
        scn.LSChooseCard(tempest);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();

        assertTrue(tempest.isHit());
        assertEquals(0, scn.GetPower(tempest));
    }

    @Test
    public void JawaIonGunVMissesVehicleIfDestinyPlusJawasPresentIsNotGreaterThanDefenseValue() {
        var scn = GetScenario();
        var gun = scn.GetLSCard("gun");
        var jawa = scn.GetLSCard("jawa");
        var tempest = scn.GetDSCard("tempest");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, jawa, tempest, stormtrooper);
        scn.AttachCardsTo(jawa, gun);
        scn.LSActivateForceCheat(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(3 /* 3 + 1 Jawa = 4, not > 4 */);

        scn.LSUseCardAction(gun, "Fire");
        scn.LSChooseCard(tempest);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();

        assertFalse(tempest.isHit());
    }

    @Test
    public void JawaIonGunVExcludesCharacterFromBattleIfDestinyEqualsDefenseValue() {
        var scn = GetScenario();
        var gun = scn.GetLSCard("gun");
        var jawa = scn.GetLSCard("jawa");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var stormtrooper2 = scn.GetDSCard("stormtrooper2");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, jawa, stormtrooper, stormtrooper2);
        scn.AttachCardsTo(jawa, gun);
        scn.LSActivateForceCheat(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        int defense = (int) scn.game().getModifiersQuerying().getDefenseValue(scn.gameState(), stormtrooper);
        scn.PrepareLSDestiny(defense);

        scn.LSUseCardAction(gun, "Fire");
        scn.LSChooseCard(stormtrooper);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();

        assertTrue(scn.game().getModifiersQuerying().isExcludedFromBattle(scn.gameState(), stormtrooper));
        assertFalse(stormtrooper.isHit());
    }
}
