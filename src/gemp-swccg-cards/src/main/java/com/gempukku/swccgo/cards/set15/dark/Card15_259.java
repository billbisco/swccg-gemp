package com.gempukku.swccgo.cards.set15.dark;
import com.gempukku.swccgo.cards.set7.dark.Card7_255;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import java.util.ArrayList;
import java.util.List;

public class Card15_259 extends Card7_255 {
    public Card15_259() {
        setLore(""); setGameText("If your bounty hunter just fired a character weapon during a battle, add one battle destiny. OR During your deploy phase, deploy on your bounty hunter one character weapon from Reserve Deck; reshuffle. OR Target an opponent's spy at same site as your spy or bounty hunter, draw destiny. Target is lost if destiny > 2. If target is Undercover, it is lost if destiny > 0."); setAlternateDestiny(4);
        setTitleComponents(new TitleComponent("Jabba's Through With You",Uniqueness.UNIQUE),new TitleComponent("Nevar Yalnal",Uniqueness.UNRESTRICTED)); addIcon(Icon.SPECIAL_EDITION,0); addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "Jabba's Through With You & Nevar Yalnal"; }
    @Override public Float getDestiny() { return 4f; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId,SwccgGame game,PhysicalCard self) {
        List<PlayInterruptAction> actions=new ArrayList<>();
        List<PlayInterruptAction> upload=super.getGameTextTopLevelActions(playerId,game,self);
        if(upload!=null) actions.addAll(upload);
        Filter support=Filters.and(Filters.your(self),Filters.or(Filters.spy,Filters.bounty_hunter));
        Filter target=Filters.and(Filters.opponents(self),Filters.spy,Filters.at(Filters.sameSiteAs(self,SpotOverride.INCLUDE_UNDERCOVER,support)));
        if(GameConditions.canTarget(game,self,SpotOverride.INCLUDE_UNDERCOVER,TargetingReason.TO_BE_LOST,target)) {
            PlayInterruptAction action=new PlayInterruptAction(game,self);
            action.setText("Target opponent's spy");
            action.appendTargeting(new TargetCardOnTableEffect(action,playerId,"Choose opponent's spy",SpotOverride.INCLUDE_UNDERCOVER,TargetingReason.TO_BE_LOST,target) {
                @Override protected void cardTargeted(int targetGroupId,PhysicalCard selected) {
                    action.addAnimationGroup(selected);
                    action.allowResponses(new RespondablePlayCardEffect(action) {
                        @Override protected void performActionResults(Action targetingAction) {
                            PhysicalCard finalTarget=action.getPrimaryTargetCard(targetGroupId);
                            action.appendEffect(new DrawDestinyEffect(action,playerId) {
                                @Override protected void destinyDraws(SwccgGame game,List<PhysicalCard> draws,List<Float> values,Float total) {
                                    int threshold=Filters.undercover_spy.accepts(game,finalTarget)?0:2;
                                    if(total!=null && total>threshold) action.appendEffect(new LoseCardFromTableEffect(action,finalTarget));
                                }
                            });
                        }
                    });
                }
            });
            actions.add(action);
        }
        return actions;
    }
}
