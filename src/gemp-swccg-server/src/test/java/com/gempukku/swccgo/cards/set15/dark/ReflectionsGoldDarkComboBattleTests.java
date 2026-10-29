package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.*;

public class ReflectionsGoldDarkComboBattleTests {
    private VirtualTableScenario battle(int id, String lightId, String darkId) {
        var light=new HashMap<String,String>(); light.put("character",lightId);
        var dark=new HashMap<String,String>(); dark.put("card","15_"+id); dark.put("character",darkId);
        var s=new VirtualTableScenario(light,dark); s.StartGame(); s.MoveCardsToDSHand(s.GetDSCard("card"));
        s.MoveCardsToLocation(s.GetDSStartingLocation(),s.GetLSCard("character"),s.GetDSCard("character"));
        s.SkipToDSTurn(Phase.BATTLE); s.EnsureDSForcePile(3);
        s.DSInitiateBattle(s.GetDSStartingLocation()); s.PassAllResponses(); return s;
    }
    @Test public void TrooperAssaultAndTacticalSupportBoostsTrooperPowerAndImmunityAsUsed() {
        var light=new HashMap<String,String>(); light.put("luke","1_19");
        var dark=new HashMap<String,String>(); dark.put("card","15_297"); dark.put("trooper","1_194");
        var s=new VirtualTableScenario(light,dark); var card=s.GetDSCard("card"); var trooper=s.GetDSCard("trooper");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveCardsToLocation(s.GetDSStartingLocation(),trooper,s.GetLSCard("luke"));
        s.SkipToDSTurn(Phase.BATTLE); s.EnsureDSForcePile(3); int power=s.GetPower(trooper);
        s.DSInitiateBattle(s.GetDSStartingLocation()); s.DSPlayCard(card,"Add power and immunity to troopers"); s.PassAllResponses();
        assertEquals(power+2,s.GetPower(trooper)); assertEquals(Float.MAX_VALUE,s.game().getModifiersQuerying().getImmunityToAttritionLessThan(s.gameState(),trooper),s.epsilon);
        assertTrue(s.GetDSUsedPile().contains(card));
    }
    @Test public void MaulStrikesAndNoMatchForASithAddsOneBattleDestinyAgainstOtherJedi() {
        var s=battle(264,"1_21","12_101"); var card=s.GetDSCard("card"); int before=s.GetDSBattleDestinyCount();
        s.DSPlayCard(card,"Add one battle destiny"); s.PassAllResponses();
        assertEquals(before+1,s.GetDSBattleDestinyCount()); assertTrue(s.GetDSLostPile().contains(card));
    }
    @Test public void MaulStrikesAndNoMatchForASithAddsTwoBattleDestinyAgainstQuiGon() {
        var s=battle(264,"11_10","12_101"); var card=s.GetDSCard("card"); int before=s.GetDSBattleDestinyCount();
        s.DSPlayCard(card,"Add two battle destiny"); s.PassAllResponses();
        assertEquals(before+2,s.GetDSBattleDestinyCount()); assertTrue(s.GetDSLostPile().contains(card));
    }
    @Test public void WattosChanceCubeAndYouSwindledMeEvenDestinyAddsTwoBattleDestinyAsUsed() {
        var s=battle(302,"1_21","11_65"); var card=s.GetDSCard("card"); int before=s.GetDSBattleDestinyCount();
        s.PrepareDSDestiny(2); s.DSPlayCard(card,"Draw destiny"); s.PassAllResponses();
        assertEquals(before+2,s.GetDSBattleDestinyCount()); assertTrue(s.GetDSUsedPile().contains(card));
    }
    @Test public void WattosChanceCubeAndYouSwindledMeOddDestinyResetsTotalBattleDestinyToZero() {
        var light=new HashMap<String,String>(); light.put("obi","1_21");
        var dark=new HashMap<String,String>(); dark.put("card","15_302"); dark.put("watto","11_65"); dark.put("vader","1_168");
        var s=new VirtualTableScenario(light,dark); var card=s.GetDSCard("card");
        s.StartGame(); s.MoveCardsToDSHand(card);
        s.MoveCardsToLocation(s.GetDSStartingLocation(),s.GetLSCard("obi"),s.GetDSCard("watto"),s.GetDSCard("vader"));
        s.SkipToDSTurn(Phase.BATTLE); s.EnsureDSForcePile(3); s.DSInitiateBattle(s.GetDSStartingLocation()); s.PassAllResponses();
        s.PrepareDSDestiny(1); s.DSPlayCard(card,"Draw destiny"); s.PassAllResponses();
        s.PrepareDSDestiny(6); s.PrepareLSDestiny(0); s.SkipToEndOfPowerSegment(true);
        assertEquals(0,s.GetDSTotalDestiny()); assertTrue(s.GetDSUsedPile().contains(card));
    }
    @Test public void ReleaseYourAngerAndUnexpectedInterruptionAddsDestinyOnlyToPowerAsLost() {
        var light=new HashMap<String,String>(); light.put("obi","1_21");
        var dark=new HashMap<String,String>(); dark.put("card","15_276"); dark.put("vader","1_168");
        var s=new VirtualTableScenario(light,dark); var card=s.GetDSCard("card"); var vader=s.GetDSCard("vader");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveCardsToLocation(s.GetDSStartingLocation(),vader,s.GetLSCard("obi"));
        s.SkipToLSTurn(Phase.BATTLE); s.EnsureLSForcePile(3); int power=s.GetPower(vader);
        s.LSInitiateBattle(s.GetDSStartingLocation()); s.LSPass();
        s.DSPlayCard(card,"Add one destiny to total power"); s.PassAllResponses();
        s.PrepareDSDestiny(3); s.SkipToPowerSegment();
        s.PassDestinyDrawResponses(); s.SkipBattleDestinyDraws(false);
        assertEquals(power+3,s.GetDSTotalPower()); assertEquals(0,s.GetDSTotalDestiny());
        assertTrue(s.GetDSLostPile().contains(card));
    }
    @Test public void SomethingHitUsAndWhyDidntYouTellMeReducesAttritionWhenLukeDefends() {
        var s=battle(282,"1_19","1_168"); var card=s.GetDSCard("card");
        s.PrepareDSDestiny(1); s.PrepareLSDestiny(4); s.SkipToEndOfPowerSegment(true);
        if(s.LSGetDecision()!=null) s.LSPass();
        int before=s.GetUnpaidDSAttrition(); s.PrepareDSDestiny(3);
        s.DSPlayCard(card,"Reduce opponent's attrition and total power"); s.PassAllResponses();
        assertEquals(Math.max(0,before-3),s.GetUnpaidDSAttrition()); assertTrue(s.GetDSLostPile().contains(card));
    }

    @Test public void MaulStrikesAndNoMatchForASithMatchingDestinyResetsJediPowerAsLost() {
        var light=new HashMap<String,String>(); light.put("obi","1_21");
        var dark=new HashMap<String,String>(); dark.put("card","15_264"); dark.put("maul","12_101"); dark.put("saber","11_99");
        var s=new VirtualTableScenario(light,dark); var card=s.GetDSCard("card"); var obi=s.GetLSCard("obi");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveCardsToLocation(s.GetDSStartingLocation(),obi,s.GetDSCard("maul"));
        s.AttachCardsTo(s.GetDSCard("maul"),s.GetDSCard("saber"));
        s.SkipToDSTurn(Phase.BATTLE); s.EnsureDSForcePile(5); s.DSInitiateBattle(s.GetDSStartingLocation()); s.PassAllResponses();
        s.MoveCardsToTopOfDSReserveDeck(s.GetDSDestiny(2),s.GetDSDestiny(3));
        s.MoveCardsToTopOfLSReserveDeck(s.GetLSDestiny(1),s.GetLSDestiny(3)); int force=s.GetDSForcePileCount();
        s.DSPlayCard(card,"Make both players draw two destiny"); s.DSChooseCard(obi); s.PassAllResponses();
        assertEquals(0,s.GetPower(obi)); assertEquals(force-2,s.GetDSForcePileCount());
        s.PassAllResponses(); s.PassAllResponses();
        assertTrue(s.GetDSLostPile().contains(card));
    }
    @Test public void MaulStrikesAndNoMatchForASithDuelsJediAndLosesLowerDestinyCharacter() {
        var light=new HashMap<String,String>(); light.put("obi","1_21");
        var dark=new HashMap<String,String>(); dark.put("card","15_264"); dark.put("maul","12_101");
        var s=new VirtualTableScenario(light,dark); var card=s.GetDSCard("card"); var maul=s.GetDSCard("maul"); var obi=s.GetLSCard("obi");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveCardsToLocation(s.GetDSStartingLocation(),maul,obi); s.SkipToDSTurn(Phase.CONTROL);
        s.MoveCardsToTopOfDSReserveDeck(s.GetDSDestiny(3),s.GetDSDestiny(4)); s.MoveCardsToTopOfLSReserveDeck(s.GetLSDestiny(0),s.GetLSDestiny(1));
        s.DSPlayCard(card,"Initiate duel between Maul and a Jedi"); s.DSChooseCard(obi); s.PassAllResponses(); s.PassAllResponses(); s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(obi)); assertFalse(s.GetDSLostPile().contains(maul)); assertTrue(s.GetDSLostPile().contains(card));
    }
}
