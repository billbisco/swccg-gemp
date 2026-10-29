package com.gempukku.swccgo.cards.set15.dark;
import com.gempukku.swccgo.cards.set6.dark.Card6_150;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.NoBattleDamageModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Card15_215 extends Card6_150 {
    public Card15_215() {
        setLore(""); setGameText("Deploy on table. If you 'probe' opponent's \"Hidden Base\" system, opponent loses X Force, where X = 15 - number of systems on table. You take no battle damage where you have a 'probe' card. Also, while you have fewer than 13 cards in your hand, your non-unique cards in hand (except effects of any kind and any interrupts) are immune to Grimtaash. (Immune to Alter while you occupy 2 battlegrounds)."); setAlternateDestiny(4);
        addComboCardTitles(Title.Well_Guarded,Title.Security_Precautions);
        addIcon(Icon.JABBAS_PALACE,0); addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "Well Guarded & Security Precautions"; }
    @Override public Float getDestiny() { return 4f; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game,PhysicalCard self) {
        List<Modifier> modifiers=new ArrayList<>(super.getGameTextWhileActiveInPlayModifiers(game,self));
        modifiers.add(new NoBattleDamageModifier(self,Filters.hasStacked(Filters.probeCard),self.getOwner()));
        return modifiers;
    }
    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game,EffectResult result,PhysicalCard self,int textId) {
        if (TriggerConditions.hiddenBaseProbed(game,result)) {
            int loss=Math.max(0,15-Filters.countTopLocationsOnTable(game,Filters.system));
            if (loss>0) {
                RequiredGameTextTriggerAction action=new RequiredGameTextTriggerAction(self,textId);
                action.setText("Make opponent lose "+loss+" Force");
                action.appendEffect(new LoseForceEffect(action,game.getOpponent(self.getOwner()),loss));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
