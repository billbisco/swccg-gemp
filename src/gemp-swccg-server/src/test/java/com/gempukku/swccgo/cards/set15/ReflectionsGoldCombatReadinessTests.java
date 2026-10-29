package com.gempukku.swccgo.cards.set15;

import com.gempukku.swccgo.common.*;

import com.gempukku.swccgo.framework.*;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class ReflectionsGoldCombatReadinessTests {
    private void activate(int amount) {
        var light=new HashMap<String,String>();
        light.put("luke","1_19");
        var dark=new HashMap<String,String>();
        dark.put("combo","15_228");
        dark.put("trooper","1_194");

        var s=new VirtualTableScenario(light,dark,40,40,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,StartingSetup.NoLSStartingInterrupts,new StartingSetup() { public HashMap<String,String> Cards() { return new HashMap<>();
         } public void Setup(VirtualTableScenario s) { s.DSPass();
         } },StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);
        s.StartGame();
        s.MoveCardsToDSHand(s.GetDSCard("combo"));
        s.MoveCardsToLocation(s.GetDSStartingLocation(),s.GetLSCard("luke"),s.GetDSCard("trooper"));
        s.SkipToDSTurn(Phase.BATTLE);
        s.PrepareDSDestiny(3);
        s.DSInitiateBattle(s.GetDSStartingLocation());

        for(int i=0;
        i<12&&(s.DSGetDecision()==null||!s.DSCardPlayAvailable(s.GetDSCard("combo")));
        i++) {if(s.LSGetDecision()!=null&&s.LSDecisionAvailable("Optional"))s.LSPass();
        else if(s.DSGetDecision()!=null&&s.DSDecisionAvailable("Optional"))s.DSPass();
        else break;
        }
        int force=s.GetDSForcePileCount();
        s.DSPlayCard(s.GetDSCard("combo"));
        s.PassAllResponses();
        s.DSDecided(Integer.toString(amount));
        s.PassAllResponses();
        assertEquals(force+amount,s.GetDSForcePileCount());
        assertTrue(s.GetDSUsedPile().contains(s.GetDSCard("combo")));

    }
    @Test public void CombatReadinessMayActivateZeroAfterDestinyThree() {activate(0);
        }
    @Test public void CombatReadinessMayActivateLessThanDestiny() {activate(2);
        }
    @Test public void CombatReadinessMayActivateFullDestiny() {activate(3);
        }
    @Test public void StartingDeploysRelatedBattlegroundAndTwoFreeImmuneEffectsThenIsLost() {
        var light=new HashMap<String,String>();
        var dark=new HashMap<String,String>();
        dark.put("combo","15_228");
        dark.put("site","1_291");
        dark.put("iao","8_126");
        dark.put("points","9_129");

        StartingSetup starting=new StartingSetup() {
            public HashMap<String,String> Cards() {return new HashMap<>();
        }
            public void Setup(VirtualTableScenario s) {
                s.DSChooseCard(s.GetDSCard("combo"));
        s.PassAllResponses();

        s.PassAllResponses();
        s.DSChooseOption("Left");s.PassAllResponses();assertTrue("effects decision="+s.DSGetDecision().getClass()+" "+s.DSGetDecision().getText(),s.DSHasCardChoiceAvailable(s.GetDSCard("iao"))); s.DSChooseCard(s.GetDSCard("iao")); s.PassAllResponses(); s.DSChooseCard(s.GetDSCard("points"));
        s.PassAllResponses();

            }
        };

        var s=new VirtualTableScenario(light,dark,40,40,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,StartingSetup.NoLSStartingInterrupts,starting,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);
        s.StartGame();

        assertTrue(s.GetDSCard("site").getZone().isInPlay());
        assertTrue(s.GetDSCard("iao").getZone().isInPlay());
        assertTrue(s.GetDSCard("points").getZone().isInPlay());
        assertTrue(s.GetDSLostPile().contains(s.GetDSCard("combo")));

    }}
