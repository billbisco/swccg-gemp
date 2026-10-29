package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.cards.set5.light.Card5_069;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.List;

public class Card15_131 extends Card5_069 {
    public Card15_131() {
        setLore("");
        setAlternateDestiny(2);
        addIcon(Icon.CLOUD_CITY, 0);
        addIcons(Icon.REFLECTIONS_GOLD);
        addComboCardTitles("Smoke Screen", "It's A Trap!");
        setGameText("Use 3 force to cancel any battle just initiated at a site. (Immune to Sense.) OR During a battle at a site, if you are about to draw a card for battle destiny, you may instead use the ability number of one of your characters involved in the battle.");
    }
    @Override public String getTitle() { return "Smoke Screen & It's A Trap!"; }
    @Override public Float getDestiny() { return 2f; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new java.util.ArrayList<>();
        List<PlayInterruptAction> substitute = super.getGameTextOptionalAfterActions(playerId, game, effectResult, self);
        if (substitute != null) actions.addAll(substitute);
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.site)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Cancel battle");
            action.setImmuneTo(Title.Sense);
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelBattleEffect(action));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
