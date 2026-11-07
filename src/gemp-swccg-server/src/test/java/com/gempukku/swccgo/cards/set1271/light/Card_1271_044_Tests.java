package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.cards.set1271.LegacyShuffleObserver;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.*;
import org.junit.Test;
import java.util.*;
import java.util.function.Consumer;
import static org.junit.Assert.*;

public class Card_1271_044_Tests {
    private boolean selectable(VirtualTableScenario s, String name) {
        String[] ids=s.LSGetADParam("blueprintId"), allowed=s.LSGetADParam("selectable");
        assertNotNull(ids);assertNotNull(allowed);
        for(int i=0;i<ids.length;i++) if(ids[i].equals(s.GetLSCard(name).getBlueprintId(true)) && Boolean.parseBoolean(allowed[i]))return true;
        return false;
    }
    private VirtualTableScenario scenario(boolean starting, List<String> choices, Consumer<VirtualTableScenario> inspect) {
        var light=new HashMap<String,String>();
        light.put("frigate","1271_44");light.put("redemption","5_87");light.put("masanya","9_78");
        light.put("homeOne","9_74");light.put("oldCapital","1_140");light.put("bwing","9_66");light.put("luke","1_19");
        light.put("setup","1271_46");light.put("do","4_21");light.put("staging","9_40");light.put("insurrection","8_40");
        light.put("conditional","7_75");light.put("paid","9_34");light.put("shield","13_4");
        StartingSetup interrupt=new StartingSetup() {
            public HashMap<String,String> Cards(){return new HashMap<>();}
            public void Setup(VirtualTableScenario s){
                if(!starting){s.LSPass();return;}
                s.LSChooseCard(s.GetLSCard("frigate"));s.PassAllResponses();
                if(inspect!=null)inspect.accept(s);
                for(String name:choices){
                    s.LSChooseCard(s.GetLSCard(name));s.PassAllResponses();
                    if(name.equals("setup") && s.GetLSCard(name).getAttachedTo()==null){s.LSChooseCard(s.GetLSStartingLocation());s.PassAllResponses();}
                }
                if(choices.size()<3){s.LSDecided("");s.PassAllResponses();}
            }
        };
        return new VirtualTableScenario(light,new HashMap<>(),40,40,StartingSetup.LSStartingLocation("1_135"),StartingSetup.DefaultDSGroundLocation,
            interrupt,StartingSetup.NoDSStartingInterrupts,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);
    }
    private VirtualTableScenario normal(){return scenario(false,List.of(),null);}
    private VirtualTableScenario used(){
        var s=normal();s.StartGame();s.SkipToLSTurn(Phase.CONTROL);s.MoveCardsToLSHand(s.GetLSCard("frigate"));s.SkipToPhase(Phase.DEPLOY);
        return s;
    }
    @Test public void HeadingForTheMedicalFrigatePrintedStatsIconsAndKeywordsAreCorrect(){
        var s=normal();var b=s.GetLSCard("frigate").getBlueprint();
        assertEquals("Heading For The Medical Frigate",b.getTitle());assertTrue(b.hasVirtualSuffix());assertEquals(Side.LIGHT,b.getSide());
        assertEquals(3,b.getDestiny(),s.epsilon);assertEquals(Uniqueness.UNIQUE,b.getUniqueness());assertEquals(CardSubtype.USED_OR_STARTING,b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1,b.getExpansionSet());assertEquals(Rarity.V,b.getRarity());
        s.BlueprintCardTypeCheck(b,List.of(CardType.INTERRUPT));s.BlueprintKeywordCheck(b,List.of());
        s.BlueprintIconCheck(b,List.of(Icon.INTERRUPT,Icon.DEATH_STAR_II,Icon.LEGACY_BLOCK_7));
    }
    private void take(String name){
        var s=used();var observer=LegacyShuffleObserver.observe(s);var c=s.GetLSCard("frigate");int force=s.GetLSForcePileCount();
        s.LSPlayCard(c);s.PassAllResponses();assertTrue(selectable(s,name));s.LSChooseCard(s.GetLSCard(name));s.PassAllResponses();
        assertEquals(Zone.HAND,s.GetLSCard(name).getZone());assertTrue(s.GetLSUsedPile().contains(c));
        assertEquals(force,s.GetLSForcePileCount());assertTrue(observer.sawOwnedShuffle(s.LS,Zone.RESERVE_DECK));
    }
    @Test public void HeadingForTheMedicalFrigateUsedTakesNonDeathStarIIRedemptionAndReshuffles(){take("redemption");}
    @Test public void HeadingForTheMedicalFrigateUsedTakesAQualifyingDeathStarIICapitalAndReshuffles(){take("masanya");}
    @Test public void HeadingForTheMedicalFrigateUsedExcludesStarCruisersOldCapitalsAndStarfighters(){
        var s=used();s.LSPlayCard(s.GetLSCard("frigate"));s.PassAllResponses();
        assertTrue(selectable(s,"redemption"));assertTrue(selectable(s,"masanya"));
        for(String name:List.of("homeOne","oldCapital","bwing","luke"))assertFalse(name,selectable(s,name));
        s.LSChooseCard(s.GetLSCard("masanya"));s.PassAllResponses();assertEquals(Zone.HAND,s.GetLSCard("masanya").getZone());
    }
    @Test public void HeadingForTheMedicalFrigateUsedStillGoesToUsedWhenNoQualifyingCardIsFound(){
        var s=used();s.MoveCardsToLSHand(s.GetLSCard("redemption"),s.GetLSCard("masanya"));var observer=LegacyShuffleObserver.observe(s);
        s.LSPlayCard(s.GetLSCard("frigate"));s.PassAllResponses();s.LSDecided("");s.PassAllResponses();
        assertTrue("Used Interrupt must finish resolving",s.GetLSUsedPile().contains(s.GetLSCard("frigate")));
        assertTrue("Reserve Deck must reshuffle after unsuccessful search",observer.sawOwnedShuffle(s.LS,Zone.RESERVE_DECK));
    }
    @Test public void HeadingForTheMedicalFrigateStartingMayDeployZeroCardsAndGoesToLost(){
        var s=scenario(true,List.of(),null);s.StartGame();assertTrue(s.GetLSLostPile().contains(s.GetLSCard("frigate")));
        for(String name:List.of("setup","do","staging","insurrection"))assertFalse(s.GetLSCard(name).getZone().isInPlay());
    }
    @Test public void HeadingForTheMedicalFrigateStartingDeploysThreeQualifyingEffectsAndNoFourth(){
        var s=scenario(true,List.of("do","staging","insurrection"),null);s.StartGame();
        for(String name:List.of("do","staging","insurrection"))assertEquals(Zone.SIDE_OF_TABLE,s.GetLSCard(name).getZone());
        assertFalse(s.GetLSCard("setup").getZone().isInPlay());assertTrue(s.GetLSLostPile().contains(s.GetLSCard("frigate")));
    }
    @Test public void HeadingForTheMedicalFrigateStartingAcceptsASetupMissionAsANonEffect(){
        var s=scenario(true,List.of("setup","do"),null);s.StartGame();
        assertEquals(s.GetLSStartingLocation(),s.GetLSCard("setup").getAttachedTo());assertEquals(Zone.SIDE_OF_TABLE,s.GetLSCard("do").getZone());
        assertTrue(s.GetLSLostPile().contains(s.GetLSCard("frigate")));
    }
    @Test public void HeadingForTheMedicalFrigateStartingExcludesConditionalImmunityPaidEffectsAndNonEffectsWithoutSetup(){
        var s=scenario(true,List.of(),x->{
            assertTrue(selectable(x,"do"));assertTrue(selectable(x,"setup"));
            for(String name:List.of("conditional","paid","luke","shield"))assertFalse(name,selectable(x,name));
        });s.StartGame();assertTrue(s.GetLSLostPile().contains(s.GetLSCard("frigate")));
    }
}
