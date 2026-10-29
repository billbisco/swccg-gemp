package com.gempukku.swccgo.cards.set15.dark;
import com.gempukku.swccgo.cards.set1.dark.Card1_243;
import com.gempukku.swccgo.cards.set2.dark.Card2_137;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.MayNotMoveUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.List;

public class Card15_238 extends Card1_243 {
    private static final OotaActions OOTA=new OotaActions();
    public Card15_238() {
        setLore(""); setGameText("If Nabrun Leids just completed a transport, use 2 Force. Nabrun is lost and all Rebels transported are captured. (Immune to Sense.) OR At any time (except during battle), target any or all of your characters at one site to 'transport' (relocate) to any one other site. Draw destiny. Use that much Force to 'transport' or place Interrupt in Lost Pile. OR Prevent a smuggler from moving this turn. OR Cancel a Kessel Run."); setCardSubtype(CardSubtype.LOST);
        addComboCardTitles("Elis Helrot",Title.Oota_Goota_Solo); addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "Elis Helrot & Oo-ta Goo-ta Solo?"; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId,SwccgGame game,Effect effect,PhysicalCard self) {
        return OOTA.before(playerId,game,effect,self);
    }
    @Override protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId,SwccgGame game,EffectResult result,PhysicalCard self) {
        if(TriggerConditions.transportCompletedBy(game,result,Filters.Nabrun_Leids)) return OOTA.after(playerId,game,result,self);
        return null;
    }
    @Override protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId,SwccgGame game,PhysicalCard self) {
        List<PlayInterruptAction> actions=new ArrayList<>();
        List<PlayInterruptAction> transport=super.getGameTextTopLevelActions(playerId,game,self);
        if(transport!=null) actions.addAll(transport);
        List<PlayInterruptAction> cancel=OOTA.top(playerId,game,self);
        if(cancel!=null) actions.addAll(cancel);
        if(GameConditions.canTarget(game,self,Filters.smuggler)) {
            PlayInterruptAction action=new PlayInterruptAction(game,self);
            action.setText("Prevent a smuggler from moving");
            action.appendTargeting(new TargetCardOnTableEffect(action,playerId,"Choose smuggler",Filters.smuggler) {
                @Override protected void cardTargeted(int targetGroupId,PhysicalCard selected) {
                    action.addAnimationGroup(selected);
                    action.allowResponses(new RespondablePlayCardEffect(action) {
                        @Override protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new MayNotMoveUntilEndOfTurnEffect(action,action.getPrimaryTargetCard(targetGroupId)));
                        }
                    });
                }
            }); actions.add(action);
        }
        return actions;
    }
    private static final class OotaActions extends Card2_137 {
        List<PlayInterruptAction> top(String playerId,SwccgGame game,PhysicalCard self) { return super.getGameTextTopLevelActions(playerId,game,self); }
        List<PlayInterruptAction> before(String playerId,SwccgGame game,Effect effect,PhysicalCard self) { return super.getGameTextOptionalBeforeActions(playerId,game,effect,self); }
        List<PlayInterruptAction> after(String playerId,SwccgGame game,EffectResult result,PhysicalCard self) { return super.getGameTextOptionalAfterActions(playerId,game,result,self); }
    }
}
