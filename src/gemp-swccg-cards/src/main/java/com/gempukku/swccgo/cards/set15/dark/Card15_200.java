package com.gempukku.swccgo.cards.set15.dark;
import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.set6.dark.Card6_141;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.Collections;
import java.util.List;

public class Card15_200 extends AbstractNormalEffect {
    private static final CaptureActions CAPTURE = new CaptureActions();
    public Card15_200() {
        super(Side.DARK,2,PlayCardZoneOption.YOUR_SIDE_OF_TABLE,"Quick Reflexes & All Wrapped Up",Uniqueness.UNIQUE,ExpansionSet.REFLECTIONS_GOLD,Rarity.UNKNOWN);
        setLore("");
        setGameText("Deploy on table. We Have A Prisoner plays for free and is immune to Sense. Also, whenever opponent forfeits a character, your bounty hunter present may capture that character. Also, during your draw phase, you may use 2 Force to search through your Lost Pile and take any one Hidden Weapons into hand. (Immune to Alter while you occupy three Jabba's Palace sites.)");
        setTitleComponents(new TitleComponent("Quick Reflexes", Uniqueness.UNRESTRICTED),
                new TitleComponent("All Wrapped Up", Uniqueness.UNIQUE)); addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game,PhysicalCard self) {
        return List.of(new InterruptPlaysForFreeModifier(self,Filters.We_Have_A_Prisoner),
                new ImmuneToTitleModifier(self,Filters.We_Have_A_Prisoner,Title.Sense),
                new ImmuneToTitleModifier(self,new OccupiesCondition(self.getOwner(),3,Filters.Jabbas_Palace_site),Title.Alter));
    }
    @Override protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId,SwccgGame game,EffectResult result,PhysicalCard self,int textId) {
        return CAPTURE.actions(playerId,game,result,self,textId);
    }
    @Override protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId,SwccgGame game,PhysicalCard self,int textId) {
        GameTextActionId id=GameTextActionId.QUICK_REFLEXES__UPLOAD_HIDDEN_WEAPONS_OR_DOWNLOAD_BLASTER_FROM_LOST_PILE;
        if(GameConditions.isOnceDuringYourPhase(game,self,playerId,textId,id,Phase.DRAW)
                && GameConditions.canUseForce(game,playerId,2)
                && GameConditions.canTakeCardsIntoHandFromLostPile(game,playerId,self,id)) {
            TopLevelGameTextAction action=new TopLevelGameTextAction(self,textId,id);
            action.setText("Take Hidden Weapons into hand"); action.appendUsage(new OncePerPhaseEffect(action));
            action.appendCost(new UseForceEffect(action,playerId,2));
            action.appendEffect(new TakeCardIntoHandFromLostPileEffect(action,playerId,Filters.Hidden_Weapons,false));
            return Collections.singletonList(action);
        }
        return null;
    }
    private static final class CaptureActions extends Card6_141 {
        List<OptionalGameTextTriggerAction> actions(String playerId,SwccgGame game,EffectResult result,PhysicalCard self,int textId) {
            return super.getGameTextOptionalAfterTriggers(playerId,game,result,self,textId);
        }
    }
}
