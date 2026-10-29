package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class ReflectionsGoldDarkJediPresenceComboTests {
    private VirtualTableScenario scenario() {
        var light=new HashMap<String,String>(); light.put("luke","1_19");
        var dark=new HashMap<String,String>(); dark.put("combo","15_232"); dark.put("vader","1_168"); dark.put("tarkin","1_179");
        var scn=new VirtualTableScenario(light,dark); scn.StartGame(); scn.MoveCardsToDSHand(scn.GetDSCard("combo")); return scn;
    }
    @Test public void DarkJediPresenceAndVadersEyePrintedMetadataMatchesRemaster() {
        var scn=scenario(); var card=scn.GetDSCard("combo").getBlueprint();
        assertEquals(4f,card.getDestiny(),scn.epsilon); assertEquals(CardSubtype.LOST,card.getCardSubtype()); assertEquals(Uniqueness.UNIQUE,card.getUniqueness());
        assertEquals(0,card.getCredits()); scn.BlueprintIconCheck(card,List.of(Icon.INTERRUPT,Icon.REFLECTIONS_GOLD)); scn.BlueprintKeywordCheck(card,List.of());
    }
    private void defend(boolean vader) {
        var scn=scenario(); var combo=scn.GetDSCard("combo"); var defender=scn.GetDSCard(vader?"vader":"tarkin"); var site=scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site,defender,scn.GetLSCard("luke")); scn.SkipToLSTurn(Phase.BATTLE); scn.EnsureLSForcePile(2);
        scn.LSInitiateBattle(site); scn.PassAllResponses(); if(scn.LSGetDecision()!=null) scn.LSPass();
        int before=scn.GetDSBattleDestinyCount(); int power=scn.GetDSTotalPower();
        scn.DSPlayCard(combo,vader?"Add 1 to power":"Add one battle destiny"); scn.PassAllResponses();
        assertEquals(before+1,scn.GetDSBattleDestinyCount()); assertEquals(power+(vader?1:0),scn.GetDSTotalPower());
        assertTrue(scn.GetDSLostPile().contains(combo));
    }
    @Test public void DarkJediPresenceAndVadersEyeAddsPowerAndDestinyForDefendingVaderAlone() { defend(true); }
    @Test public void DarkJediPresenceAndVadersEyeAddsOnlyDestinyForOtherDefendingImperialAlone() { defend(false); }
    @Test public void DarkJediPresenceAndVadersEyeDoublesOtherImperialPowerButHasNoDefendingBonusWhenAttacking() {
        var scn=scenario(); var combo=scn.GetDSCard("combo"); var vader=scn.GetDSCard("vader"); var trooper=scn.GetDSFiller(1); var site=scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site,vader,trooper,scn.GetLSCard("luke")); scn.SkipToDSTurn(Phase.BATTLE); scn.EnsureDSForcePile(3);
        scn.DSInitiateBattle(site); scn.PassAllResponses();
        assertFalse(scn.DSCardPlayAvailable(combo,"Add 1 to power")); assertTrue(scn.DSCardPlayAvailable(combo));
        int force=scn.GetDSForcePileCount(); int power=scn.GetPower(trooper); int vaderPower=scn.GetPower(vader);
        scn.DSPlayCard(combo); scn.DSChooseCard(vader); scn.PassAllResponses();
        assertEquals(power*2,scn.GetPower(trooper)); assertEquals(vaderPower,scn.GetPower(vader));
        assertEquals(force-1,scn.GetDSForcePileCount()); assertTrue(scn.GetDSLostPile().contains(combo));
    }
    @Test public void ComponentRestrictionSymbolsMatchPrintedTitles() {
        var card=new com.gempukku.swccgo.cards.set15.dark.Card15_232();
        assertEquals(java.util.List.of(new TitleComponent("Dark Jedi Presence",Uniqueness.UNRESTRICTED),new TitleComponent("Vader's Eye",Uniqueness.UNIQUE)),card.getTitleComponents());
    }
}
