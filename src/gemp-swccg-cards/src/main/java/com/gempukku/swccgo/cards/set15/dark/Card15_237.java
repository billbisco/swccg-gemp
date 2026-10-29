package com.gempukku.swccgo.cards.set15.dark;
import com.gempukku.swccgo.cards.set14.dark.Card14_103;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.HitResult;
import java.util.Collections;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.List;

public class Card15_237 extends Card14_103 {
    public Card15_237() {
        setLore(""); setGameText("If your battle droid just fired a blaster and hit a character, all opponent's weapons at same site are lost. OR During any deploy phase, target a character at same site as your battle droid. Target is landspeed = 0 for remainder of turn. OR If opponent just deployed a character to same site as your battle droid, opponent must use 1 Force or place that character in hand."); setAlternateDestiny(4);
        addComboCardTitles("Drop Your Weapons","Halt!");
        addIcon(Icon.THEED_PALACE,0);
        addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "Drop Your Weapons & Halt!"; }
    @Override public Float getDestiny() { return 4f; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game, EffectResult result, PhysicalCard self) {
        List<PlayInterruptAction> actions=new ArrayList<>();
        List<PlayInterruptAction> first=super.getGameTextOptionalAfterActions(playerId, game, result, self);
        if(first!=null) actions.addAll(first);
        List<PlayInterruptAction> second=dropWeapons(playerId, game, result, self);
        if(second!=null) actions.addAll(second);
        actions.forEach(action -> action.setPlayedAsSubtype(CardSubtype.USED));
        return actions;
    }
    private List<PlayInterruptAction> dropWeapons(String playerId, SwccgGame game, EffectResult result, PhysicalCard self) {
        if(TriggerConditions.justHitBy(game,result,Filters.character,Filters.blaster,Filters.and(Filters.your(self),Filters.battle_droid))) {
            PhysicalCard hit=((HitResult)result).getCardHit();
            Filter weapons=Filters.and(Filters.opponents(self),Filters.weapon,Filters.atSameSite(hit));
            if(GameConditions.canSpot(game,self,weapons)) {
                PlayInterruptAction action=new PlayInterruptAction(game,self,CardSubtype.USED);
                action.setText("Make opponent's weapons lost");
                action.allowResponses(new RespondablePlayCardEffect(action) {
                    @Override protected void performActionResults(Action targetingAction) {
                        action.appendEffect(new LoseCardsFromTableEffect(action,Filters.filterActive(game,self,weapons)));
                    }
                });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
