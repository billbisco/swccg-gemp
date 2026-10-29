package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.set4.light.Card4_049;
import com.gempukku.swccgo.cards.set5.light.Card5_064;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import java.util.ArrayList;
import java.util.List;

public class Card15_075 extends Card4_049 {
    public Card15_075() {
        setLore(""); addIcon(Icon.DAGOBAH, 0); addIcons(Icon.REFLECTIONS_GOLD);
        addComboCardTitles("Effective Repairs", "Punch It!");
        setGameText("USED: Cancel Limited Resources. LOST: Use 3 Force to search your Lost Pile and take one Effect, Utinni Effect, Mobile Effect, or Immediate Effect into hand. OR If Han or your Lando is piloting a starfighter which is defending a battle at a system, add one battle destiny (add two if starfighter is Falcon). Also, starfighter is immune to attrition for remainder of turn.");
    }
    @Override public String getTitle() { return "Effective Repairs & Punch It!"; }
    @Override public Float getDestiny() { return 3f; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    private static final PunchItActions PUNCH_IT = new PunchItActions();
    private static final class PunchItActions extends Card5_064 {
        List<PlayInterruptAction> actions(String playerId, SwccgGame game, PhysicalCard self) {
            return super.getGameTextTopLevelActions(playerId, game, self);
        }
    }
    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>();
        if (GameConditions.canTargetToCancel(game, self, Filters.Limited_Resources)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Limited_Resources, Title.Limited_Resources);
            actions.add(action);
        }
        GameTextActionId id = GameTextActionId.RG_EFFECTIVE_REPAIRS_PUNCH_IT__UPLOAD_EFFECT_FROM_LOST_PILE;
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3)
                && GameConditions.canTakeCardsIntoHandFromLostPile(game, playerId, self, id)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self, id, CardSubtype.LOST);
            action.setText("Take an Effect into hand from Lost Pile");
            action.appendCost(new UseForceEffect(action, playerId, 3));
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new TakeCardIntoHandFromLostPileEffect(action, playerId,
                            Filters.or(Filters.Effect, Filters.Utinni_Effect, Filters.Mobile_Effect, Filters.Immediate_Effect), false));
                }
            });
            actions.add(action);
        }
        List<PlayInterruptAction> punch = PUNCH_IT.actions(playerId, game, self);
        if (punch != null) {
            punch.forEach(action -> action.setPlayedAsSubtype(CardSubtype.LOST));
            actions.addAll(punch);
        }
        return actions;
    }
}
